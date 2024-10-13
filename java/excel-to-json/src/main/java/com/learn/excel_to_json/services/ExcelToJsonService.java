package com.learn.excel_to_json.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.learn.excel_to_json.dto.SheetDto;
import com.learn.excel_to_json.dto.SheetMapDto;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExcelToJsonService {
    public List<ObjectNode> convertExcelToJson(MultipartFile excelFile) throws IOException {
        List<ObjectNode> jsonList = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(excelFile.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        Row headerRow = sheet.getRow(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            Map<String, String> data = new HashMap<>();
            for (Cell cell : row) {
                String columnName = headerRow.getCell(cell.getColumnIndex()).getStringCellValue();
                String cellValue = getCellValueAsString(cell);
                data.put(columnName, cellValue);
            }
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode jsonNode = mapper.convertValue(data, ObjectNode.class);
            jsonList.add(jsonNode);
        }
        workbook.close();
        return jsonList;
    }


    public Map<String, List<String>> getSheetHeaders(MultipartFile file) throws IOException {
        Map<String, List<String>> sheetHeaders = new HashMap<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                Row headerRow = sheet.getRow(0);
                List<String> headers = new ArrayList<>();
                for (Cell cell : headerRow) {
                    String headerValue = cell.getStringCellValue().trim(); // Trim to remove leading/trailing spaces
                    if (!headerValue.isEmpty()) {
                        headers.add(headerValue);
                    }
                }
                sheetHeaders.put(sheet.getSheetName(), headers);
            }
        }

        return sheetHeaders;
    }

    public List<ObjectNode> getSheetData(MultipartFile file, String sheetName, List<String> fields) throws IOException {
        List<ObjectNode> jsonList = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheet(sheetName);
            Row headerRow = sheet.getRow(0);

            Map<Integer, String> columnIndexToField = new HashMap<>();
            for (Cell cell : headerRow) {
                String headerName = cell.getStringCellValue();
                if (fields.contains(headerName)) {
                    columnIndexToField.put(cell.getColumnIndex(), headerName);
                }
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                Map<String, String> data = new HashMap<>();
                for (Map.Entry<Integer, String> entry : columnIndexToField.entrySet()) {
                    Cell cell = row.getCell(entry.getKey());
                    String cellValue = getCellValueAsString(cell);
                    data.put(entry.getValue(), cellValue);
                }
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode jsonNode = mapper.convertValue(data, ObjectNode.class);
                jsonList.add(jsonNode);
            }
        }

        return jsonList;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
//    public Map<String, List<ObjectNode>> getMultipleSheetsData(MultipartFile file , List<SheetDto> sheetDtos) throws IOException {
//        Map<String, List<ObjectNode>> result = new HashMap<>();
//
//        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
//            for (SheetDto config : sheetDtos) {
//                String sheetName = config.getSheetName();
//                List<String> fields = config.getFields();
//                List<ObjectNode> jsonList = new ArrayList<>();
//
//                Sheet sheet = workbook.getSheet(sheetName);
//                if (sheet == null) {
//                    throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in the Excel file.");
//                }
//
//                Row headerRow = sheet.getRow(0);
//                if (headerRow == null) {
//                    throw new IllegalArgumentException("Header row not found in the sheet '" + sheetName + "'.");
//                }
//
//                Map<Integer, String> columnIndexToField = new HashMap<>();
//                for (Cell cell : headerRow) {
//                    String headerName = cell.getStringCellValue().trim(); // Trim to handle leading/trailing spaces
//                    if (fields.contains(headerName)) {
//                        columnIndexToField.put(cell.getColumnIndex(), headerName);
//                    }
//                }
//
//                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
//                    Row row = sheet.getRow(i);
//                    if (row != null) {
//                        Map<String, String> data = new HashMap<>();
//                        for (Map.Entry<Integer, String> entry : columnIndexToField.entrySet()) {
//                            Cell cell = row.getCell(entry.getKey());
//                            String cellValue = getCellValueAsString(cell);
//                            data.put(entry.getValue(), cellValue);
//                        }
//                        ObjectMapper mapper = new ObjectMapper();
//                        ObjectNode jsonNode = mapper.convertValue(data, ObjectNode.class);
//                        jsonList.add(jsonNode);
//                    }
//                }
//
//                result.put(sheetName, jsonList);
//            }
//        }
//
//        return result;
//    }

    public Map<String, List<ObjectNode>> getMultipleSheetsData(MultipartFile file, List<SheetDto> sheetDtos) throws IOException {
        Map<String, List<ObjectNode>> result = new HashMap<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            for (SheetDto config : sheetDtos) {
                String sheetName = config.getSheetName();
                List<String> fields = config.getFields();
                Map<String, String> fieldMappings = config.getFieldMappings();
                List<ObjectNode> jsonList = new ArrayList<>();

                Sheet sheet = workbook.getSheet(sheetName);
                if (sheet == null) {
                    throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in the Excel file.");
                }

                Row headerRow = sheet.getRow(0);
                if (headerRow == null) {
                    throw new IllegalArgumentException("Header row not found in the sheet '" + sheetName + "'.");
                }

                Map<Integer, String> columnIndexToField = new HashMap<>();
                for (Cell cell : headerRow) {
                    String headerName = cell.getStringCellValue().trim(); // Trim to handle leading/trailing spaces
                    if (fields.contains(headerName)) {
                        String alternativeName = fieldMappings.getOrDefault(headerName, headerName);
                        columnIndexToField.put(cell.getColumnIndex(), alternativeName);
                    }
                }

                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        Map<String, String> data = new HashMap<>();
                        for (Map.Entry<Integer, String> entry : columnIndexToField.entrySet()) {
                            Cell cell = row.getCell(entry.getKey());
                            String cellValue = getCellValueAsString(cell);
                            data.put(entry.getValue(), cellValue);
                        }
                        ObjectMapper mapper = new ObjectMapper();
                        ObjectNode jsonNode = mapper.convertValue(data, ObjectNode.class);
                        jsonList.add(jsonNode);
                    }
                }

                result.put(sheetName, jsonList);
            }
        }

        return result;
    }


    public Map<String, List<ObjectNode>> getMultipleSheetsDataWithMap(File file, List<SheetMapDto> sheetDtos) throws IOException {
        Map<String, List<ObjectNode>> result = new HashMap<>();

        try (Workbook workbook = WorkbookFactory.create(new FileInputStream(file))) {
            for (SheetMapDto config : sheetDtos) {
                String sheetName = config.getSheetName();
                Map<String, String> fieldMappings = config.getFieldMappings();
                List<ObjectNode> jsonList = new ArrayList<>();

                Sheet sheet = workbook.getSheet(sheetName);
                if (sheet == null) {
                    throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in the Excel file.");
                }

                Row headerRow = sheet.getRow(0);
                if (headerRow == null) {
                    throw new IllegalArgumentException("Header row not found in the sheet '" + sheetName + "'.");
                }

                Map<Integer, String> columnIndexToField = new HashMap<>();
                for (Cell cell : headerRow) {
                    String headerName = cell.getStringCellValue().trim(); // Trim to handle leading/trailing spaces
                    if (fieldMappings.containsKey(headerName)) {
//                        String alternativeName = fieldMappings.get(headerName);
                        String alternativeName = fieldMappings.get(headerName).isEmpty() ? headerName : fieldMappings.get(headerName);
                        columnIndexToField.put(cell.getColumnIndex(), alternativeName);
                    }
                }


                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        Map<String, String> data = new HashMap<>();
                        for (Map.Entry<Integer, String> entry : columnIndexToField.entrySet()) {
                            Cell cell = row.getCell(entry.getKey());
                            String cellValue = getCellValueAsString(cell);
                            data.put(entry.getValue(), cellValue);
                        }
                        ObjectMapper mapper = new ObjectMapper();
                        ObjectNode jsonNode = mapper.convertValue(data, ObjectNode.class);
                        jsonList.add(jsonNode);
                    }
                }

                result.put(sheetName, jsonList);
            }
        }

        return result;
    }
}
