package com.learn.excel_to_json.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.learn.excel_to_json.dto.FileNameHeadersResponse;
import com.learn.excel_to_json.dto.SheetDto;
import com.learn.excel_to_json.dto.SheetMapDto;
import com.learn.excel_to_json.services.ExcelToJsonService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("" +
        "")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin("*")
public class ExcelController {

    @Autowired
    private ExcelToJsonService excelToJsonService;
    private  final ObjectMapper mapper;
    private final JavaMailSender javaMailSender;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/convert")
    public ResponseEntity<List<ObjectNode>> convertExcelToJson(@RequestParam("file") MultipartFile file) throws IOException {
        List<ObjectNode> json = excelToJsonService.convertExcelToJson(file);
        return ResponseEntity.ok(json);
    }

//    @PostMapping("/upload")
//    public ResponseEntity<Map<String, List<String>>> uploadExcelFile(@RequestParam("file") MultipartFile file) throws IOException {
//        log.info("Entered into uploadExcelFile()");
//        Map<String, List<String>> sheetHeaders = excelToJsonService.getSheetHeaders(file);
//        log.info("out from uploadExcelFile()");
//        return ResponseEntity.ok(sheetHeaders);
//    }
    @PostMapping("/upload")
    public ResponseEntity<FileNameHeadersResponse> uploadExcelFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        try {
            // Generate or retrieve session ID
            String sessionId = (String) request.getSession().getAttribute("sessionId");
            if (sessionId == null) {
                sessionId = UUID.randomUUID().toString();
                request.getSession().setAttribute("sessionId", sessionId);
            }

            String userDir = uploadDir + "/" + sessionId;
            Files.createDirectories(Paths.get(userDir));

            String fileName = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
            Path targetLocation = Paths.get(userDir).resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
//            return ResponseEntity.ok(fileName);
            log.info("Entered into uploadExcelFile()");
            Map<String, List<String>> sheetHeaders = excelToJsonService.getSheetHeaders(file);
            log.info("out from uploadExcelFile()");
            return ResponseEntity.ok(FileNameHeadersResponse.builder().fileName(fileName).fileHeaders(sheetHeaders).build());
        } catch (IOException ex) {
            log.error("Could not store file : {}" + file.getOriginalFilename());
            throw  ex;
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not store file: " + file.getOriginalFilename());
        }

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
//    @PostMapping("/map/sheets")
//    public ResponseEntity<Map<String, List<ObjectNode>>> getSheetsDataWithMap(
//            @RequestPart MultipartFile file,
//            @RequestPart String sheetConfigs) throws IOException {
//        log.info("Entered into getSheetsData()");
//        List<SheetMapDto> sheetConfig = mapper.readValue(sheetConfigs, new TypeReference<List<SheetMapDto>>() {});
//        Map<String, List<ObjectNode>> json = excelToJsonService.getMultipleSheetsDataWithMap(file, sheetConfig);
//        log.info("out from getSheetsData()");
//        return ResponseEntity.ok(json);
//    }

    @PostMapping("/map/sheets")
    public ResponseEntity<Map<String, List<ObjectNode>>> getSheetsDataWithMap(
            @RequestPart String fileName,
            @RequestPart String sheetConfigs,
            HttpServletRequest request) throws IOException {
        // Retrieve the session ID
        String sessionId = (String) request.getSession().getAttribute("sessionId");
        if (sessionId == null) {
            return ResponseEntity.status(400).body(null);
        }

        // Construct the file path
        Path filePath = Paths.get(uploadDir, sessionId, fileName);

        // Check if file exists
        File file = filePath.toFile();
        if (!file.exists()) {
            return ResponseEntity.status(404).body(null);
        }
        log.info("Entered into getSheetsData()");
        List<SheetMapDto> sheetConfig = mapper.readValue(sheetConfigs, new TypeReference<List<SheetMapDto>>() {});
        Map<String, List<ObjectNode>> json = excelToJsonService.getMultipleSheetsDataWithMap(file, sheetConfig);
        log.info("out from getSheetsData()");
        return ResponseEntity.ok(json);
    }

    @PostMapping("/uploadMultiple")
    public ResponseEntity<String> uploadMultipleFiles(@RequestParam("files") List<MultipartFile> files) {
        try {
            for (MultipartFile file : files) {
                // Here, handle each file as per your requirements
                String fileName = file.getOriginalFilename();
                System.out.println("Uploaded File: " + fileName);

                // You can save the file to the server, database, etc.
                // file.transferTo(new File("/path/to/save/" + fileName));  // Example save logic
            }
            return ResponseEntity.status(HttpStatus.OK).body("Files uploaded successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed!");
        }
    }







}

