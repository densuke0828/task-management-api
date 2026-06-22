package com.example.task_management_api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    TODO("未完了"),
    IN_PROGRESS("進行中"),
    DONE("完了");

    private final String displayName;
}
