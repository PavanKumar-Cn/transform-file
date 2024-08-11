package com.learn.excel_to_json.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.excel_to_json.dto.SheetDto;
import com.learn.excel_to_json.dto.SheetJsonDto;
import com.learn.excel_to_json.dto.SheetMapDto;
import com.learn.excel_to_json.services.ExcelToJsonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/excel")
@Slf4j
@RequiredArgsConstructor
public class ExcelController {

    @Autowired
    private ExcelToJsonService excelToJsonService;
    private  final ObjectMapper mapper;
    private final JavaMailSender javaMailSender;
    @PostMapping("/convert")
    public ResponseEntity<List<ObjectNode>> convertExcelToJson(@RequestParam("file") MultipartFile file) throws IOException {
        List<ObjectNode> json = excelToJsonService.convertExcelToJson(file);
        return ResponseEntity.ok(json);
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, List<String>>> uploadExcelFile(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("Entered into uploadExcelFile()");
        Map<String, List<String>> sheetHeaders = excelToJsonService.getSheetHeaders(file);
        log.info("out from uploadExcelFile()");
        return ResponseEntity.ok(sheetHeaders);
    }

    @PostMapping("/data")
    public ResponseEntity<List<ObjectNode>> getSheetData(@RequestParam("file") MultipartFile file,
                                                         @RequestParam("sheetName") String sheetName,
                                                         @RequestParam("fields") List<String> fields) throws IOException {
        log.info("Entered into getSheetData()");
        List<ObjectNode> json = excelToJsonService.getSheetData(file, sheetName, fields);
        log.info("out from getSheetData()");
        return ResponseEntity.ok(json);
    }

//    @PostMapping("/sheets")
//    public ResponseEntity<Map<String, List<ObjectNode>>> getSheetsData(
//            @RequestPart MultipartFile file,
//            @RequestPart String sheetConfigs) throws IOException {
//        log.info("Entered into getSheetsData()");
//        List<SheetDto> sheetConfig = mapper.readValue(sheetConfigs,new TypeReference<List<SheetDto>>() {});
//        System.err.println(sheetConfigs);
//        Map<String, List<ObjectNode>> json = excelToJsonService.getMultipleSheetsData(file,sheetConfig);
//        log.info("out from getSheetsData()");
//        return ResponseEntity.ok(json);
//    }
    @PostMapping("/sheets")
    public ResponseEntity<Map<String, List<ObjectNode>>> getSheetsData(
            @RequestPart MultipartFile file,
            @RequestPart String sheetConfigs) throws IOException {
        log.info("Entered into getSheetsData()");
        List<SheetDto> sheetConfig = mapper.readValue(sheetConfigs, new TypeReference<List<SheetDto>>() {});
        Map<String, List<ObjectNode>> json = excelToJsonService.getMultipleSheetsData(file, sheetConfig);
        log.info("out from getSheetsData()");
        return ResponseEntity.ok(json);
    }
    @PostMapping("/map/sheets")
    public ResponseEntity<Map<String, List<ObjectNode>>> getSheetsDataWithMap(
            @RequestPart MultipartFile file,
            @RequestPart String sheetConfigs) throws IOException {
        log.info("Entered into getSheetsData()");
        List<SheetMapDto> sheetConfig = mapper.readValue(sheetConfigs, new TypeReference<List<SheetMapDto>>() {});
        Map<String, List<ObjectNode>> json = excelToJsonService.getMultipleSheetsDataWithMap(file, sheetConfig);
        log.info("out from getSheetsData()");
        return ResponseEntity.ok(json);
    }








}

