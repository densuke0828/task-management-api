package com.example.task_management_api;

import com.example.task_management_api.dto.TaskRequest;
import com.example.task_management_api.dto.TaskStatusRequest;
import com.example.task_management_api.entity.Task;
import com.example.task_management_api.enums.Priority;
import com.example.task_management_api.enums.Status;
import com.example.task_management_api.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TaskIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void POST_tasks_タスクが登録される() throws Exception {
        TaskRequest request = new TaskRequest();
        ReflectionTestUtils.setField(request, "title", "タイトル");
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("タイトル"));

        assertThat(taskRepository.count()).isEqualTo(1);
    }

    @Test
    void GET_tasks_指定したStatusのタスクを取得() throws Exception {
        taskRepository.save(
                Task.create("タイトル1", "説明1", Status.TODO,
                            Priority.MEDIUM, null));
        taskRepository.save(
                Task.create("タイトル2", "説明2", Status.IN_PROGRESS,
                            Priority.MEDIUM, null));

        mockMvc.perform(get("/tasks").param("status", "TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("TODO"));
    }

    @Test
    void GET_tasks_登録タスクを全件取得() throws Exception {
        taskRepository.save(
                Task.create("タイトル1", "説明1", Status.TODO,
                        Priority.MEDIUM, null));
        taskRepository.save(
                Task.create("タイトル2", "説明2", Status.IN_PROGRESS,
                        Priority.MEDIUM, null));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("タイトル1"))
                .andExpect(jsonPath("$[1].title").value("タイトル2"));
    }

    @Test
    void PUT_tasks_id_タスク全体が更新される() throws Exception {
        Task saved = taskRepository.save(Task.create("タイトル1", "説明1", Status.TODO,
                                                        Priority.MEDIUM, null));
        TaskRequest request = new TaskRequest();
        ReflectionTestUtils.setField(request, "title", "新タイトル");
        ReflectionTestUtils.setField(request, "description", "新説明");
        ReflectionTestUtils.setField(request, "status", Status.IN_PROGRESS);
        ReflectionTestUtils.setField(request, "priority", Priority.HIGH);
        ReflectionTestUtils.setField(request, "deadline", LocalDateTime.now().plusDays(2));
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(put("/tasks/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("新タイトル"));

    }

    @Test
    void PATCH_tasks_id_status_Statusのみ更新される() throws Exception {
        Task saved = taskRepository.save(Task.create("タイトル", "説明", Status.TODO,
                                                    Priority.MEDIUM, null));
        TaskStatusRequest request = new TaskStatusRequest();
        ReflectionTestUtils.setField(request, "status", Status.IN_PROGRESS);
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(patch("/tasks/" + saved.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("タイトル"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void DELETE_tasks_id_タスクが削除される() throws Exception {
        Task saved = taskRepository.save(Task.create("タイトル", "説明", Status.TODO,
                                                    Priority.MEDIUM, null));

        mockMvc.perform(delete("/tasks/" + saved.getId()))
                .andExpect(status().isNoContent());

        assertThat(taskRepository.findById(saved.getId())).isEmpty();
    }
}
