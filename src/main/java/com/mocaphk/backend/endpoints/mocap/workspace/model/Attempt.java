package com.mocaphk.backend.endpoints.mocap.workspace.model;

import com.mocaphk.backend.endpoints.mocap.user.model.MocapUser;

import com.mocaphk.backend.utils.DateUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Attempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private MocapUser user;

    @Column(name = "question_id")
    private Long questionId;

    @ManyToOne
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    private Question question;

    @Column(columnDefinition = "TEXT")
    private String code;

    private Boolean isSubmitted;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "result_id")
    private AttemptResult result;

    private String createdAt;

    private String updatedAt;

    private String executedAt;

    public void setUserId(String userId) {
        this.userId = userId;
        this.updatedAt = DateUtils.now();
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
        this.updatedAt = DateUtils.now();
    }

    public void setCode(String code) {
        this.code = code;
        this.updatedAt = DateUtils.now();
    }

    public void setIsSubmitted(Boolean submitted) {
        isSubmitted = submitted;
        this.updatedAt = DateUtils.now();
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        this.updatedAt = DateUtils.now();
    }

    public void setExecutedAt(String executedAt) {
        this.executedAt = executedAt;
        this.updatedAt = DateUtils.now();
    }
}
