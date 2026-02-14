package com.creativestudio.service;

import com.creativestudio.dto.DashboardRequest;
import com.creativestudio.dto.DashboardResponse;
import com.creativestudio.dto.WidgetResponse;
import com.creativestudio.model.Dashboard;
import com.creativestudio.model.User;
import com.creativestudio.repository.DashboardRepository;
import com.creativestudio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final UserRepository userRepository;

    public List<DashboardResponse> getUserDashboards(String email) {
        User user = findUserByEmail(email);
        return dashboardRepository.findByUser(user).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public DashboardResponse createDashboard(String email, DashboardRequest request) {
        User user = findUserByEmail(email);

        Dashboard dashboard = Dashboard.builder()
                .name(request.getName())
                .user(user)
                .build();

        dashboardRepository.save(dashboard);
        return toResponse(dashboard);
    }

    public DashboardResponse getDashboard(String email, UUID dashboardId) {
        User user = findUserByEmail(email);
        Dashboard dashboard = dashboardRepository.findByIdAndUser(dashboardId, user)
                .orElseThrow(() -> new RuntimeException("Dashboard not found"));
        return toResponse(dashboard);
    }

    public DashboardResponse updateDashboard(String email, UUID dashboardId, DashboardRequest request) {
        User user = findUserByEmail(email);
        Dashboard dashboard = dashboardRepository.findByIdAndUser(dashboardId, user)
                .orElseThrow(() -> new RuntimeException("Dashboard not found"));

        dashboard.setName(request.getName());
        dashboardRepository.save(dashboard);
        return toResponse(dashboard);
    }

    public void deleteDashboard(String email, UUID dashboardId) {
        User user = findUserByEmail(email);
        Dashboard dashboard = dashboardRepository.findByIdAndUser(dashboardId, user)
                .orElseThrow(() -> new RuntimeException("Dashboard not found"));
        dashboardRepository.delete(dashboard);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private DashboardResponse toResponse(Dashboard dashboard) {
        List<WidgetResponse> widgetResponses = dashboard.getWidgets().stream()
                .map(w -> WidgetResponse.builder()
                        .id(w.getId())
                        .type(w.getType())
                        .name(w.getName())
                        .x(w.getX())
                        .y(w.getY())
                        .width(w.getWidth())
                        .height(w.getHeight())
                        .zIndex(w.getZIndex())
                        .data(w.getData())
                        .createdAt(w.getCreatedAt())
                        .updatedAt(w.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        return DashboardResponse.builder()
                .id(dashboard.getId())
                .name(dashboard.getName())
                .gridSize(dashboard.getGridSize())
                .widgets(widgetResponses)
                .createdAt(dashboard.getCreatedAt())
                .build();
    }
}