package com.creativestudio.repository;

import com.creativestudio.model.Dashboard;
import com.creativestudio.model.Widget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WidgetRepository extends JpaRepository<Widget, UUID> {

    List<Widget> findByDashboard(Dashboard dashboard);

    List<Widget> findByDashboardOrderByCreatedAtAsc(Dashboard dashboard);
}