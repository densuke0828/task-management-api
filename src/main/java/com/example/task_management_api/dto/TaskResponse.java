package com.example.task_management_api.dto;

import com.example.task_management_api.entity.Task;
import com.example.task_management_api.enums.Priority;
import com.example.task_management_api.enums.Status;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private Status status;
    private LocalDateTime deadline;
    private Priority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TaskResponse from(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .deadline(task.getDeadline())
                .priority(task.getPriority())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
