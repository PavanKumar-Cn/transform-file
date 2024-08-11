package com.learn.excel_to_json.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SheetJsonDto {
    private MultipartFile file;
    private List<SheetDto> sheetDtos;
}
