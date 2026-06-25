package com.example.task_management_api;

import com.example.task_management_api.dto.TaskRequest;
import com.example.task_management_api.dto.TaskStatusRequest;
import com.example.task_management_api.entity.Task;
import com.example.task_management_api.enums.Priority;
import com.example.task_management_api.enums.Status;
import com.example.task_management_api.exception.TaskNotFoundException;
import com.example.task_management_api.repository.TaskRepository;
import com.example.task_management_api.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;

import static org.assertj.core.api.Assertions.assertThat;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    /**
     * searchByStatus(正常系)
     * 中身有りのリスト
     */
    @Test
    void searchByStatus_正常系_指定したStatusのListが返る() {
        Task task1 = Task.create("タイトル1", "説明1",
                Status.TODO, Priority.MEDIUM, LocalDateTime.now().plusDays(7));
        Task task2 = Task.create("タイトル2", "説明2",
                Status.TODO, Priority.MEDIUM, LocalDateTime.now().plusDays(7));
        given(taskRepository.findByStatus(Status.TODO)).willReturn(List.of(task1, task2));
        List<Task> result = taskService.searchByStatus(Status.TODO);
        assertThat(result)
                .hasSize(2)
                .extracting(Task::getTitle)
                .containsExactly("タイトル1", "タイトル2");
    }

    /**
     * searchByStatus(正常系)
     * 中身が空のリスト
     */
    @Test
    void searchByStatus_正常系_空のリストが返る() {
        given(taskRepository.findByStatus(Status.TODO)).willReturn(List.of());
        List<Task> result = taskService.searchByStatus(Status.TODO);
        assertThat(result).isEmpty();
    }

    /**
     * findAll(正常系)
     * 中身ありのリスト
     */
    @Test
    void findAll_正常系_タスクリストが返る() {
        Task task1 = Task.create("タイトル1", "説明1",
                Status.TODO, Priority.MEDIUM, LocalDateTime.now().plusDays(7));
        Task task2 = Task.create("タイトル2", "説明2",
                Status.DONE, Priority.LOW, LocalDateTime.now().plusDays(7));
        given(taskRepository.findAll()).willReturn(List.of(task1, task2));
        List<Task> result = taskService.findAll();
        assertThat(result)
                .hasSize(2)
                .extracting(Task::getTitle)
                .containsExactly("タイトル1", "タイトル2");
    }

    /**
     * findAll(正常系)
     * 中身空のリスト
     */
    @Test
    void findAll_正常系_空のリストが返る() {
        given(taskRepository.findAll()).willReturn(List.of());
        List<Task> result = taskService.findAll();
        assertThat(result).isEmpty();
    }

    /**
     * createTask(正常系)
     */
    @Test
    void createTask_正常系_登録したタスクが返る() {
        TaskRequest request = new TaskRequest();
        ReflectionTestUtils.setField(request, "title", "タイトル");
        Task task =  Task.create(request.getTitle(), request.getDescription(),
                request.getStatus(), request.getPriority(), request.getDeadline());
        given(taskRepository.save(any(Task.class))).willReturn(task);

        Task result = taskService.createTask(request);

        assertThat(result.getTitle()).isEqualTo("タイトル");
        assertThat(result.getDescription()).isNull();
        assertThat(result.getStatus()).isEqualTo(Status.TODO);
        assertThat(result.getPriority()).isEqualTo(Priority.MEDIUM);
        assertThat(result.getDeadline()).isNull();
    }

    @Test
    void updateTask_正常系_更新されたTaskが返る() {
        TaskRequest request = new TaskRequest();
        LocalDateTime deadline = LocalDateTime.now().plusDays(3);
        ReflectionTestUtils.setField(request, "title", "新タイトル");
        ReflectionTestUtils.setField(request, "description", "新説明");
        ReflectionTestUtils.setField(request, "status", Status.IN_PROGRESS);
        ReflectionTestUtils.setField(request, "priority", Priority.LOW);
        ReflectionTestUtils.setField(request, "deadline", deadline);
        Task foundTask = Task.create(
                "タイトル", "説明", Status.TODO,
                Priority.MEDIUM, LocalDateTime.now().plusDays(7));
        given(taskRepository.findById(1L)).willReturn(Optional.of(foundTask));

        Task result = taskService.updateTask(1L, request);

        assertThat(result.getTitle()).isEqualTo("新タイトル");
        assertThat(result.getDescription()).isEqualTo("新説明");
        assertThat(result.getStatus()).isEqualTo(Status.IN_PROGRESS);
        assertThat(result.getPriority()).isEqualTo(Priority.LOW);
        assertThat(result.getDeadline()).isEqualTo(deadline);
    }

    @Test
    void updateTask_異常系_TaskNotFoundExceptionがスローされる() {
        given(taskRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(1L, new TaskRequest()))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void updateStatus_正常系_Statusが更新されたTaskが返る() {
        TaskStatusRequest request = new TaskStatusRequest();
        ReflectionTestUtils.setField(request, "status", Status.IN_PROGRESS);
        Task foundTask = Task.create(
                "タイトル", "説明", Status.TODO,
                Priority.MEDIUM, LocalDateTime.now().plusDays(7));
        given(taskRepository.findById(1L)).willReturn(Optional.of(foundTask));

        Task result = taskService.updateStatus(1L, request);

        assertThat(result.getStatus()).isEqualTo(Status.IN_PROGRESS);
    }

    @Test
    void updateStatus_異常系_TaskNotFoundExceptionがスローされる() {
        given(taskRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateStatus(1L, new TaskStatusRequest()))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void deleteTask_正常系_登録積みのタスクが削除される() {
        Task foundTask = Task.create(
                "タイトル", "説明", Status.TODO,
                Priority.MEDIUM, LocalDateTime.now().plusDays(7));
        given(taskRepository.findById(1L)).willReturn(Optional.of(foundTask));

        taskService.deleteTask(1L);

        then(taskRepository).should().delete(foundTask);
    }

    @Test
    void deleteTask_異常系_TaskNotFoundExceptionがスローされる() {
        given(taskRepository.findById(1L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> taskService.deleteTask(1L))
                .isInstanceOf(TaskNotFoundException.class);
    }
}
