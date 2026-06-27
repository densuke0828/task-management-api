package com.example.task_management_api;

import com.example.task_management_api.entity.Task;
import com.example.task_management_api.enums.Priority;
import com.example.task_management_api.enums.Status;
import com.example.task_management_api.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@DataJpaTest
public class TaskRepositoryTest {
    @Autowired
    private TaskRepository taskRepository;

    @Test
    void findByStatus_指定したStatusのタスクが返る() {
        Task task = Task.create(
                "タイトル", "説明", Status.TODO,
                Priority.MEDIUM, null);
        taskRepository.save(task);

        List<Task> result = taskRepository.findByStatus(Status.TODO);
        assertThat(result)
                .hasSize(1)
                .extracting(Task::getStatus)
                .containsExactly(Status.TODO);
    }

    @Test
    void findByStatus_別Statusのタスクは返らない() {
        Task task = Task.create(
                "タイトル", "説明", Status.IN_PROGRESS,
                Priority.MEDIUM, null);
        taskRepository.save(task);

        List<Task> result = taskRepository.findByStatus(Status.TODO);
        assertThat(result).isEmpty();
    }

    @Test
    void findByStatus_Statusが混在していても指定したStatusのタスクのみ返る() {
        Task task1 = Task.create(
                "タイトル", "説明", Status.TODO,
                Priority.MEDIUM, null);
        Task task2 = Task.create(
                "タイトル", "説明", Status.IN_PROGRESS,
                Priority.MEDIUM, null);
        taskRepository.save(task1);
        taskRepository.save(task2);

        List<Task> result = taskRepository.findByStatus(Status.TODO);
        assertThat(result)
                .hasSize(1)
                .extracting(Task::getStatus)
                .containsExactly(Status.TODO);
    }
}
