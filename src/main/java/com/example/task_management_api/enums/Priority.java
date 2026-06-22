package com.example.task_management_api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Priority {
    LOW("低"),
    MEDIUM("中"),
    HIGH("高");

    private final String displayName;
}
