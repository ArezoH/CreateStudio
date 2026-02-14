package com.creativestudio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class DashboardResponse {
    private UUID id;
    private String name;
    private int gridSize;
    private List<WidgetResponse> widgets;
    private LocalDateTime createdAt;
}