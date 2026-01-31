import React, { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { io } from "socket.io-client";
import Editor from "@monaco-editor/react";
import { Emulator } from "../../components/Emulator/Emulator";
import { BASE_URL } from "../../constants";
import "./PlaygroundPage.css";

const SAVE_INTERVAL_MS = 2000;
const RUN_INTERVAL_MS = 1200;

// debounce helper
const debounce = (fn, delay = 250) => {
  let t;
  return (...args) => {
    clearTimeout(t);
    t = setTimeout(() => fn(...args), delay);
  };
};

export const PlaygroundPage = () => {
  const { projectID } = useParams();

  const [html, setHtml] = useState("loading...");
  const [css, setCss] = useState("loading...");
  const [js, setJs] = useState("loading...");
  const [delayedCodes, setDelayedCodes] = useState({ html, css, js });
  const [socket, setSocket] = useState(null);

  const isRemoteUpdate = useRef(false);
  const hasLoadedFromServer = useRef(false);
  const lastSaved = useRef({ html: "", css: "", js: "" });
  const sendUpdate = useRef(null);

  // connect socket
  useEffect(() => {
    const s = io(BASE_URL, {
      transports: ["websocket"],
      query: { room: projectID },
    });
    setSocket(s);
    return () => s.disconnect();
  }, [projectID]);

  // debounced live typing sender
  useEffect(() => {
    if (!socket) return;

    sendUpdate.current = debounce((type, value) => {
      if (isRemoteUpdate.current) return;

      socket.emit("project_write", {
        room: projectID,
        type,
        data: value,
      });
    }, 200);
  }, [socket, projectID]);

  // initial load
  useEffect(() => {
    if (socket) socket.emit("project_get", { room: projectID });
  }, [socket, projectID]);

  // iframe refresh
  useEffect(() => {
    const interval = setInterval(() => {
      setDelayedCodes({ html, css, js });
    }, RUN_INTERVAL_MS);
    return () => clearInterval(interval);
  }, [html, css, js]);

  // autosave (only when content changed)
  useEffect(() => {
    if (!socket || !hasLoadedFromServer.current) return;

    const interval = setInterval(() => {
      if (isRemoteUpdate.current) return;

      if (
          lastSaved.current.html === html &&
          lastSaved.current.css === css &&
          lastSaved.current.js === js
      ) {
        return; // nothing changed
      }

      lastSaved.current = { html, css, js };

      socket.emit("project_save", {
        room: projectID,
        html,
        css,
        js,
      });
    }, SAVE_INTERVAL_MS);

    return () => clearInterval(interval);
  }, [socket, html, css, js, projectID]);

  // socket listeners
  useEffect(() => {
    if (!socket) return;

    const project_read = ({ data, type }) => {
      isRemoteUpdate.current = true;

      if (type === "HTML") setHtml(data);
      if (type === "CSS") setCss(data);
      if (type === "JS") setJs(data);

      setTimeout(() => (isRemoteUpdate.current = false), 50);
    };

    const project_retrieved = (data) => {
      isRemoteUpdate.current = true;

      setHtml(data.html);
      setCss(data.css);
      setJs(data.js);

      lastSaved.current = {
        html: data.html,
        css: data.css,
        js: data.js,
      };

      hasLoadedFromServer.current = true;

      setTimeout(() => (isRemoteUpdate.current = false), 50);
    };

    socket.on("project_read", project_read);
    socket.on("project_retrieved", project_retrieved);

    return () => {
      socket.off("project_read", project_read);
      socket.off("project_retrieved", project_retrieved);
    };
  }, [socket]);

  // local typing
  const onLocalChange = (type, value, setter) => {
    if (value === undefined) return;

    isRemoteUpdate.current = false;
    setter(value);
    sendUpdate.current(type, value);
  };

  return (
      <div className="playground">
        <div className="editor-row">
          <Editor
              height="30vh"
              language="html"
              value={html}
              onChange={(v) => onLocalChange("HTML", v, setHtml)}
              theme="vs-dark"
          />

          <Editor
              height="30vh"
              language="css"
              value={css}
              onChange={(v) => onLocalChange("CSS", v, setCss)}
              theme="vs-dark"
          />

          <Editor
              height="30vh"
              language="javascript"
              value={js}
              onChange={(v) => onLocalChange("JS", v, setJs)}
              theme="vs-dark"
          />
        </div>

        <Emulator values={delayedCodes} />
      </div>
  );
};
