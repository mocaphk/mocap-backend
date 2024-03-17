package com.mocaphk.backend.endpoints.mocap.workspace.model;

import com.mocaphk.backend.endpoints.mocap.user.model.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Attempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "question_id")
    private Long questionId;

    @ManyToOne
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    private Question question;

    private String code;

    private Boolean isSubmitted;

    @OneToOne
    @JoinColumn(name = "result_id")
    private AttemptResult result;

    private String createdAt;

    private String updatedAt;

    private String executedAt;
}
