import React from "react";
import "./App.css";
import { Route, Routes } from "react-router-dom";
import Home from "./component/home/Home";
import ExcelToJSon from "./component/e2J/ExcelToJSon";
import ProtectedLayout from "./component/layout/ProtectedLayout";

function App() {
  return (
    <>
      <div>Pavan Kumar Reddy</div>
      <div>File Conversion Project Started</div>
      <Routes>
        <Route path="/" element={<Home />}></Route>
        <Route element={<ProtectedLayout />}>
          <Route path="/e2j" element={<ExcelToJSon />} />
        </Route>
      </Routes>
    </>
  );
}

export default App;
