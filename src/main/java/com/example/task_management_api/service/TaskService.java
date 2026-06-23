package com.example.task_management_api.service;

import com.example.task_management_api.dto.TaskRequest;
import com.example.task_management_api.dto.TaskStatusRequest;
import com.example.task_management_api.entity.Task;
import com.example.task_management_api.enums.Status;
import com.example.task_management_api.exception.TaskNotFoundException;
import com.example.task_management_api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {
    private final TaskRepository taskRepository;

    /**
     * タスク新規登録
     */
    @Transactional(readOnly = false)
    public Task createTask(TaskRequest task) {
        return taskRepository.save(
                Task.create(
                        task.getTitle(),
                        task.getDescription(),
                        task.getStatus(),
                        task.getPriority(),
                        task.getDeadline()
                )
        );
    }

    /**
     * 登録タスク全件取得
     */
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    /**
     * 条件付き取得(Status)
     */
    public List<Task> searchByStatus(Status status) {
        return taskRepository.findByStatus(status);
    }

    /**
     * 登録タスク更新処理(PUT)
     */
    @Transactional(readOnly = false)
    public Task updateTask(Long id, TaskRequest task) {
        Task foundTask = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        foundTask.update(
                task.getTitle(), task.getDescription(), task.getStatus(),
                task.getDeadline(), task.getPriority()
        );
        return foundTask;
    }

    /**
     * 登録タスク更新処理(PATCH)
     */
    @Transactional(readOnly = false)
    public Task updateStatus(Long id, TaskStatusRequest request) {
        Task foundTask = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        foundTask.updateStatus(request.getStatus());
        return foundTask;
    }

    /**
     * 登録タスク削除処理
     */
    @Transactional(readOnly = false)
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        taskRepository.delete(task);

    }
}