import React from "react";
import "./Emulator.css";

export const Emulator = ({ values }) => {
    const { html, css, js } = values;

    const srcDoc = `
<!DOCTYPE html>
<html>
  <head>
    <style>
      ${css || ""}
    </style>

    <script>
      try {
        ${js || ""}
      } catch (e) {
        document.addEventListener("DOMContentLoaded", () => {
          document.body.innerHTML += 
            '<pre style="color:red; white-space:pre-wrap;">' + e + '</pre>';
        });
      }
    </script>
  </head>

  <body>
    ${html || ""}
  </body>
</html>
`;

    return (
        <div className="emulator">
            <iframe
                srcDoc={srcDoc}
                title="output"
                sandbox="allow-scripts"
                frameBorder="0"
                width="100%"
                height="100%"
            />
        </div>
    );
};
