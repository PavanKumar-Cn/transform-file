package com.learn.excel_to_json.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SheetMapDto {
    private String sheetName;
    private Map<String, String> fieldMappings;
}
