package com.creativestudio.controller;

import com.creativestudio.dto.DashboardRequest;
import com.creativestudio.dto.DashboardResponse;
import com.creativestudio.model.User;
import com.creativestudio.service.DashboardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/dashboards")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<List<DashboardResponse>> getUserDashboards(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(dashboardService.getUserDashboards(user.getEmail()));
    }

    @PostMapping
    public ResponseEntity<DashboardResponse> createDashboard(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody DashboardRequest request) {
        return ResponseEntity.ok(dashboardService.createDashboard(user.getEmail(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DashboardResponse> getDashboard(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return ResponseEntity.ok(dashboardService.getDashboard(user.getEmail(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DashboardResponse> updateDashboard(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody DashboardRequest request) {
        return ResponseEntity.ok(dashboardService.updateDashboard(user.getEmail(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDashboard(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        dashboardService.deleteDashboard(user.getEmail(), id);
        return ResponseEntity.ok().build();
    }
}