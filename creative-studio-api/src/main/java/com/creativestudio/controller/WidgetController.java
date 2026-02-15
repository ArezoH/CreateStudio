package com.creativestudio.controller;

import com.creativestudio.dto.WidgetRequest;
import com.creativestudio.dto.WidgetResponse;
import com.creativestudio.model.User;
import com.creativestudio.service.WidgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/dashboards/{dashboardId}/widgets")
@RequiredArgsConstructor
public class WidgetController {

    private final WidgetService widgetService;

    @GetMapping
    public ResponseEntity<List<WidgetResponse>> getWidgets(
            @AuthenticationPrincipal User user,
            @PathVariable UUID dashboardId) {
        return ResponseEntity.ok(widgetService.getWidgets(user.getEmail(), dashboardId));
    }

    @PostMapping
    public ResponseEntity<WidgetResponse> createWidget(
            @AuthenticationPrincipal User user,
            @PathVariable UUID dashboardId,
            @Valid @RequestBody WidgetRequest request) {
        return ResponseEntity.ok(widgetService.createWidget(user.getEmail(), dashboardId, request));
    }

    @PutMapping("/{widgetId}")
    public ResponseEntity<WidgetResponse> updateWidget(
            @AuthenticationPrincipal User user,
            @PathVariable UUID dashboardId,
            @PathVariable UUID widgetId,
            @Valid @RequestBody WidgetRequest request) {
        return ResponseEntity.ok(widgetService.updateWidget(user.getEmail(), dashboardId, widgetId, request));
    }

    @PatchMapping("/{widgetId}/data")
    public ResponseEntity<WidgetResponse> updateWidgetData(
            @AuthenticationPrincipal User user,
            @PathVariable UUID dashboardId,
            @PathVariable UUID widgetId,
            @RequestBody Map<String, Object> data) {
        return ResponseEntity.ok(widgetService.updateWidgetData(user.getEmail(), dashboardId, widgetId, data));
    }

    @DeleteMapping("/{widgetId}")
    public ResponseEntity<Void> deleteWidget(
            @AuthenticationPrincipal User user,
            @PathVariable UUID dashboardId,
            @PathVariable UUID widgetId) {
        widgetService.deleteWidget(user.getEmail(), dashboardId, widgetId);
        return ResponseEntity.ok().build();
    }
}