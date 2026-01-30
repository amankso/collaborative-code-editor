import React from "react";
import Editor from "@monaco-editor/react";
import "./CodeEditor.css";

export const CodeEditor = ({ name, lang, value, handleChange }) => {
    return (
        <div className="editor-container">
            <div className="editor-title">{name}</div>

            <Editor
                height="100%"
                language={lang}
                value={value}
                theme="vs-dark"
                onChange={(v) => handleChange(v)}
                options={{
                    fontSize: 14,
                    minimap: { enabled: false },
                    wordWrap: "on",
                    automaticLayout: true,
                }}
            />
        </div>
    );
};
