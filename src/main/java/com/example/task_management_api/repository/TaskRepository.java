package com.example.task_management_api.repository;

import com.example.task_management_api.entity.Task;
import com.example.task_management_api.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(Status status);
}
