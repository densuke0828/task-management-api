package com.example.task_management_api.dto;

import com.example.task_management_api.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TaskStatusRequest {
    @NotNull(message = "ステータスは必須です")
    private Status status;
}
