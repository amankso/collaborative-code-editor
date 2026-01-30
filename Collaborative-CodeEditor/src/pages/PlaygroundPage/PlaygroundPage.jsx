import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { io } from "socket.io-client";
import Editor from "@monaco-editor/react";
import { Emulator } from "../../components/Emulator/Emulator";
import { BASE_URL } from "../../constants";
import "./PlaygroundPage.css";

const SAVE_INTERVAL_MS = 2000;
const RUN_INTERVAL_MS = 1200;

export const PlaygroundPage = () => {
  const { projectID } = useParams();

  const [html, setHtml] = useState("loading...");
  const [css, setCss] = useState("loading...");
  const [js, setJs] = useState("loading...");
  const [delayedCodes, setDelayedCodes] = useState({ html, css, js });
  const [socket, setSocket] = useState(null);

  // connect socket
  useEffect(() => {
    const s = io(BASE_URL, {
      transports: ["websocket"],
      query: { room: projectID },
    });
    setSocket(s);
    return () => s.disconnect();
  }, [projectID]);

  // get project
  useEffect(() => {
    if (socket) {
      socket.emit("project_get", { room: projectID });
    }
  }, [socket, projectID]);

  // delayed run
  useEffect(() => {
    const interval = setInterval(() => {
      setDelayedCodes({ html, css, js });
    }, RUN_INTERVAL_MS);
    return () => clearInterval(interval);
  }, [html, css, js]);

  // auto save
  useEffect(() => {
    if (!socket || html === "loading...") return;
    const interval = setInterval(() => {
      socket.emit("project_save", {
        room: projectID,
        html,
        css,
        js,
      });
    }, SAVE_INTERVAL_MS);
    return () => clearInterval(interval);
  }, [socket, html, css, js, projectID]);

  const notifyChange = (value, type) => {
    if (!socket) return;
    socket.emit("project_write", {
      data: value,
      room: projectID,
      type,
    });
  };

  // socket listeners
  useEffect(() => {
    if (!socket) return;

    const project_read = ({ data, type }) => {
      if (type === "HTML") setHtml(data);
      if (type === "CSS") setCss(data);
      if (type === "JS") setJs(data);
    };

    const project_retrieve = (data) => {
      setHtml(data.html);
      setCss(data.css);
      setJs(data.js);
    };

    socket.on("project_read", project_read);
    socket.on("project_retrieved", project_retrieve);

    return () => {
      socket.off("project_read", project_read);
      socket.off("project_retrieved", project_retrieve);
    };
  }, [socket]);

  return (
      <div className="playground">
        <div className="editor-row">
          <Editor
              height="30vh"
              defaultLanguage="html"
              value={html}
              onChange={(v) => {
                setHtml(v);
                notifyChange(v, "HTML");
              }}
              theme="vs-dark"
          />

          <Editor
              height="30vh"
              defaultLanguage="css"
              value={css}
              onChange={(v) => {
                setCss(v);
                notifyChange(v, "CSS");
              }}
              theme="vs-dark"
          />

          <Editor
              height="30vh"
              defaultLanguage="javascript"
              value={js}
              onChange={(v) => {
                setJs(v);
                notifyChange(v, "JS");
              }}
              theme="vs-dark"
          />
        </div>

        <Emulator values={delayedCodes} />
      </div>
  );
};
