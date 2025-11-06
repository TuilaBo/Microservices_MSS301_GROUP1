package com.khoavdse170395.questionservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "MockAnswer")
public class MockAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String answerText;

    private String comments;

    private Integer answerPoint;

    private QuestionType questionType;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private MockOption mockOption;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private MockQuestion mockQuestion;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private MockAttempt mockAttempt;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;
}
