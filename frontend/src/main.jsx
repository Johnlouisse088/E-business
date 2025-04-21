import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App.jsx";
import Login from "./Login.jsx";         // Delete later
// import "./index.css";             // Uncomment later
import { AppProvider } from "./Context/Context.jsx";
// import { BrowserRouter as Router } from "react-router-dom";
ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    {/* <Router> */}
    <AppProvider>
      <Login />
    </AppProvider>
    {/* </Router> */}
  </React.StrictMode>
);
