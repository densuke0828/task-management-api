package com.example.task_management_api;

import com.example.task_management_api.entity.Task;
import com.example.task_management_api.enums.Priority;
import com.example.task_management_api.enums.Status;
import com.example.task_management_api.repository.TaskRepository;
import com.example.task_management_api.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;


import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void searchByStatus_正常系_指定したStatusのListが返る() {
        Task task1 = Task.create("タイトル1", "説明", Status.TODO, Priority.MEDIUM, LocalDateTime.now());
        Task task2 = Task.create("タイトル2", "説明", Status.TODO, Priority.MEDIUM, LocalDateTime.now());
        given(taskRepository.findByStatus(Status.TODO)).willReturn(List.of(task1, task2));

        List<Task> result = taskService.searchByStatus(Status.TODO);

        assertThat(result)
                .hasSize(2)
                .extracting(Task::getTitle)
                .containsExactly("タイトル1", "タイトル2");
    }
}
