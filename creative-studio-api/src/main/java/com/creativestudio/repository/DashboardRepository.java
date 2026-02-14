package com.creativestudio.repository;

import com.creativestudio.model.Dashboard;
import com.creativestudio.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DashboardRepository extends JpaRepository<Dashboard, UUID> {

    // Find all dashboards belonging to a user
    List<Dashboard> findByUser(User user);

    // Find a specific dashboard by id and user (security: user can only access their own)
    Optional<Dashboard> findByIdAndUser(UUID id, User user);
}