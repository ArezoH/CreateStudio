package com.creativestudio.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DashboardRequest {

    @NotBlank(message = "Dashboard name is required")
    private String name;
}