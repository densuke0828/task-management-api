package com.example.task_management_api.entity;

import com.example.task_management_api.enums.Priority;
import com.example.task_management_api.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "tasks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "description", length = 100)
    private String description;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", nullable = false, length = 10)
    private Status status = Status.TODO;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "priority", nullable = false, length = 10)
    private Priority priority = Priority.MEDIUM;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static Task create(
            String title, String description, Status status,
            Priority priority, LocalDateTime deadline) {
        return Task.builder()
                .title(title)
                .description(description)
                .status(status)
                .priority(priority)
                .deadline(deadline)
                .build();
    }

    public void update(
            String title, String description, Status status,
            LocalDateTime deadline, Priority priority) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.deadline = deadline;
        this.priority = priority;
    }

    public void updateStatus(Status status) {
        this.status = status;
    }
}
