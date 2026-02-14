package com.creativestudio.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class WidgetRequest {

    @NotBlank(message = "Widget type is required")
    private String type;

    private String name;
    private int x;
    private int y;
    private int width = 400;
    private int height = 400;
    private Map<String, Object> data;
}