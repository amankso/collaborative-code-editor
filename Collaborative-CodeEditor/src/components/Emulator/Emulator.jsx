import React from "react";
import "./Emulator.css";
export const Emulator = ({ values }) => {
    const { html, css, js } = values;

    return (
        <div className="emulator">
            <iframe
                srcDoc={`
          <html>
            <head><style>${css || ""}</style></head>
            <body>
              ${html || ""}
              <script>
                try { ${js || ""} }
                catch(e){ document.body.innerHTML += '<pre style="color:red">'+e+'</pre>'; }
              </script>
            </body>
          </html>
        `}
                title="output"
                sandbox="allow-scripts"
                frameBorder="0"
                width="100%"
                height="100%"
            />
        </div>
    );
};
