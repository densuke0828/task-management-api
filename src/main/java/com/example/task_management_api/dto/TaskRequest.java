package com.example.task_management_api.dto;

import com.example.task_management_api.enums.Priority;
import com.example.task_management_api.enums.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    @NotBlank(message = "タイトルは必須です")
    private String title;
    private String description;
    private Status status = Status.TODO;
    private LocalDateTime deadline;
    private Priority priority = Priority.MEDIUM;
}
