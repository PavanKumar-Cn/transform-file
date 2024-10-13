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
public class FileNameHeadersResponse {
    private String fileName;
    private Map<String, List<String>> fileHeaders;

}
