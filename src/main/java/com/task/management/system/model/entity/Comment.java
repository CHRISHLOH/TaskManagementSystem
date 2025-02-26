package com.task.management.system.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comments", schema = "management_system")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false, updatable = false)
    private Task task;
}

