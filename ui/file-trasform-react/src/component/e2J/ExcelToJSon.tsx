import axios from "axios";
import React, { useEffect, useRef, useState } from "react";
import { openaxios } from "../../api/baseAxios";

// interface SheetData {
//   [key: string]: string[];
// }
interface SheetHeaders {
  [sheetName: string]: string[];
}

const ExcelToJSon = () => {
  const [file, setFile] = useState<File | null>(null);
  const [headers, setHeaders] = useState<SheetHeaders | null>(null);
  const fileInputRef = useRef<HTMLInputElement | null>(null);

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files) {
      setFile(event.target.files[0]);
    }
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    if (!file) {
      return;
    }

    const formData = new FormData();
    formData.append("file", file);

    try {
      const response = await openaxios.post<SheetHeaders>(
        "/excel/upload",
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );
      console.log(response.data);
      setHeaders(response.data);
    } catch (error) {
      console.error("Error uploading file:", error);
    }finally {
      // Clear the file input and reset file state
      setFile(null);
      if (fileInputRef.current) {
          fileInputRef.current.value = '';
      }
  }
  };

  return (
    <div>
      <form onSubmit={handleSubmit}>
        <input type="file" onChange={handleFileChange}  ref={fileInputRef} />
        <button type="submit">Upload</button>
      </form>

      {headers && (
        <div>
          {Object.keys(headers).map((sheetName, index) => (
            <div key={index}>
              <h3>{sheetName}</h3>
              <table border={1}>
                <thead>
                  <tr>
                    {headers[sheetName].map((header, idx) => (
                      <th key={idx}>{header}</th>
                    ))}
                  </tr>
                </thead>
              </table>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default ExcelToJSon;
