package com.example.task_management_api.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long id) {
        super("タスクID: " + id + " が見つかりません");
    }

    public String getUserMessage() {
        return "指定されたタスクは登録されていません";
    }
}
