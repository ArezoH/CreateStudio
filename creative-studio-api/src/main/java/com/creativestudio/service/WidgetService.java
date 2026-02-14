package com.creativestudio.service;

import com.creativestudio.dto.WidgetRequest;
import com.creativestudio.dto.WidgetResponse;
import com.creativestudio.model.Dashboard;
import com.creativestudio.model.User;
import com.creativestudio.model.Widget;
import com.creativestudio.repository.DashboardRepository;
import com.creativestudio.repository.UserRepository;
import com.creativestudio.repository.WidgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WidgetService {

    private final WidgetRepository widgetRepository;
    private final DashboardRepository dashboardRepository;
    private final UserRepository userRepository;

    public List<WidgetResponse> getWidgets(String email, UUID dashboardId) {
        Dashboard dashboard = findDashboard(email, dashboardId);
        return widgetRepository.findByDashboardOrderByCreatedAtAsc(dashboard).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public WidgetResponse createWidget(String email, UUID dashboardId, WidgetRequest request) {
        Dashboard dashboard = findDashboard(email, dashboardId);

        Widget widget = Widget.builder()
                .dashboard(dashboard)
                .type(request.getType())
                .name(request.getName())
                .x(request.getX())
                .y(request.getY())
                .width(request.getWidth())
                .height(request.getHeight())
                .data(request.getData())
                .build();

        widgetRepository.save(widget);
        return toResponse(widget);
    }

    public WidgetResponse updateWidget(String email, UUID dashboardId, UUID widgetId, WidgetRequest request) {
        findDashboard(email, dashboardId);
        Widget widget = widgetRepository.findById(widgetId)
                .orElseThrow(() -> new RuntimeException("Widget not found"));

        widget.setName(request.getName());
        widget.setX(request.getX());
        widget.setY(request.getY());
        widget.setWidth(request.getWidth());
        widget.setHeight(request.getHeight());
        widget.setData(request.getData());

        widgetRepository.save(widget);
        return toResponse(widget);
    }

    public WidgetResponse updateWidgetData(String email, UUID dashboardId, UUID widgetId, Map<String, Object> data) {
        findDashboard(email, dashboardId);
        Widget widget = widgetRepository.findById(widgetId)
                .orElseThrow(() -> new RuntimeException("Widget not found"));

        widget.setData(data);
        widgetRepository.save(widget);
        return toResponse(widget);
    }

    public void deleteWidget(String email, UUID dashboardId, UUID widgetId) {
        findDashboard(email, dashboardId);
        Widget widget = widgetRepository.findById(widgetId)
                .orElseThrow(() -> new RuntimeException("Widget not found"));
        widgetRepository.delete(widget);
    }

    private Dashboard findDashboard(String email, UUID dashboardId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return dashboardRepository.findByIdAndUser(dashboardId, user)
                .orElseThrow(() -> new RuntimeException("Dashboard not found"));
    }

    private WidgetResponse toResponse(Widget widget) {
        return WidgetResponse.builder()
                .id(widget.getId())
                .type(widget.getType())
                .name(widget.getName())
                .x(widget.getX())
                .y(widget.getY())
                .width(widget.getWidth())
                .height(widget.getHeight())
                .zIndex(widget.getZIndex())
                .data(widget.getData())
                .createdAt(widget.getCreatedAt())
                .updatedAt(widget.getUpdatedAt())
                .build();
    }
}