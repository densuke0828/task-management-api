package com.example.task_management_api;

import com.example.task_management_api.controller.TaskController;
import com.example.task_management_api.dto.TaskRequest;
import com.example.task_management_api.dto.TaskStatusRequest;
import com.example.task_management_api.entity.Task;
import com.example.task_management_api.enums.Priority;
import com.example.task_management_api.enums.Status;
import com.example.task_management_api.exception.TaskNotFoundException;
import com.example.task_management_api.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willThrow;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    TaskService taskService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void searchTask_正常系_statusあり_絞り込み結果が_返る() throws Exception{
        Task foundTask1 = Task.create(
                "タイトル1", "説明1", Status.TODO,
                Priority.MEDIUM, LocalDateTime.now().plusDays(7));
        Task foundTask2 = Task.create(
                "タイトル2", "説明2", Status.TODO,
                Priority.MEDIUM, LocalDateTime.now().plusDays(7));
        given(taskService.searchByStatus(Status.TODO)).willReturn(List.of(foundTask1, foundTask2));

        mockMvc.perform(get("/tasks").param("status", "TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("TODO"));
    }

    @Test
    void searchTask_正常系_statusなし_全件返る() throws Exception{
        Task foundTask1 = Task.create(
                "タイトル1", "説明1", Status.TODO,
                Priority.MEDIUM, LocalDateTime.now().plusDays(7));
        Task foundTask2 = Task.create(
                "タイトル2", "説明2", Status.IN_PROGRESS,
                Priority.HIGH, LocalDateTime.now().plusDays(3));
        given(taskService.findAll()).willReturn(List.of(foundTask1, foundTask2));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("タイトル1"))
                .andExpect(jsonPath("$[1].title").value("タイトル2"));
    }

    @Test
    void createTask_正常系_登録されたタスクが返る() throws Exception{
        TaskRequest request = new TaskRequest();
        ReflectionTestUtils.setField(request, "title", "タイトル");
        ReflectionTestUtils.setField(request, "description", "説明");
        ReflectionTestUtils.setField(request, "status", Status.TODO);
        ReflectionTestUtils.setField(request, "priority", Priority.MEDIUM);
        ReflectionTestUtils.setField(request, "deadline", LocalDateTime.now().plusDays(7));
        Task createdTask = Task.create(
                request.getTitle(), request.getDescription(), request.getStatus(),
                request.getPriority(), request.getDeadline());
        String json = objectMapper.writeValueAsString(request);
        given(taskService.createTask(any(TaskRequest.class))).willReturn(createdTask);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("タイトル"));
    }

    @Test
    void updateTask_正常系_全体更新されたタスクが返る() throws Exception{
        TaskRequest request = new TaskRequest();
        ReflectionTestUtils.setField(request, "title", "新タイトル");
        ReflectionTestUtils.setField(request, "description", "新説明");
        ReflectionTestUtils.setField(request, "status", Status.IN_PROGRESS);
        ReflectionTestUtils.setField(request, "priority", Priority.HIGH);
        ReflectionTestUtils.setField(request, "deadline", LocalDateTime.now().plusDays(2));
        Task savedTask = Task.create(
                request.getTitle(), request.getDescription(), request.getStatus(),
                request.getPriority(), request.getDeadline());
        String json = objectMapper.writeValueAsString(request);
        given(taskService.updateTask(anyLong(), any(TaskRequest.class))).willReturn(savedTask);

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("新タイトル"));
    }

    @Test
    void updateTask_異常系_404が返る() throws Exception{
        TaskRequest request = new TaskRequest();
        ReflectionTestUtils.setField(request, "title", "新タイトル");
        String json = objectMapper.writeValueAsString(request);
        given(taskService.updateTask(anyLong(), any(TaskRequest.class)))
                .willThrow(new TaskNotFoundException(1L));

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStatus_正常系_Statusが更新されたタスクが返る() throws Exception {
        TaskStatusRequest request = new TaskStatusRequest();
        ReflectionTestUtils.setField(request, "status", Status.IN_PROGRESS);
        Task savedTask = Task.create(
                "タイトル", "説明", request.getStatus(),
                Priority.MEDIUM, LocalDateTime.now().plusDays(7));
        String json = objectMapper.writeValueAsString(request);
        given(taskService.updateStatus(anyLong(), any(TaskStatusRequest.class))).willReturn(savedTask);

        mockMvc.perform(patch("/tasks/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void updateStatus_異常系_404が返る() throws Exception{
        TaskStatusRequest request = new TaskStatusRequest();
        ReflectionTestUtils.setField(request, "status", Status.IN_PROGRESS);
        String json = objectMapper.writeValueAsString(request);
        given(taskService.updateStatus(anyLong(), any(TaskStatusRequest.class)))
                .willThrow(new TaskNotFoundException(1L));

        mockMvc.perform(patch("/tasks/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_正常系_タスクが削除される() throws Exception {
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void  deleteTask_異常系_404が返る() throws Exception {
        willThrow(new TaskNotFoundException(1L)).given(taskService).deleteTask(1L);
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNotFound());
    }
 }
