package com.creativestudio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class WidgetResponse {
    private UUID id;
    private String type;
    private String name;
    private int x;
    private int y;
    private int width;
    private int height;
    private int zIndex;
    private Map<String, Object> data;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}