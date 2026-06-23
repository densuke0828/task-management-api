package com.example.task_management_api.controller;

import com.example.task_management_api.dto.TaskRequest;
import com.example.task_management_api.dto.TaskResponse;
import com.example.task_management_api.dto.TaskStatusRequest;
import com.example.task_management_api.entity.Task;
import com.example.task_management_api.enums.Status;
import com.example.task_management_api.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Validated @RequestBody TaskRequest request) {
        Task task = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(TaskResponse.from(task));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> searchTask(
            @RequestParam(required = false) Status status) {
        List<Task> tasks =
                status != null ? taskService.searchByStatus(status) : taskService.findAll();
        List<TaskResponse> response = tasks.stream()
                .map(TaskResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id, @Validated @RequestBody TaskRequest request) {
        Task task = taskService.updateTask(id, request);
        return ResponseEntity.ok(TaskResponse.from(task));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable Long id, @Validated @RequestBody TaskStatusRequest request) {
        Task task =taskService.updateStatus(id, request);
        return ResponseEntity.ok(TaskResponse.from(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
