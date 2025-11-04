package com.khoavdse170395.questionservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "MockQuestion")
public class MockQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String question;

    private Integer point;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private MockTest test;

    @OneToMany(cascade = CascadeType.ALL,  fetch = FetchType.EAGER)
    private List<MockOption> options;

    @OneToOne(cascade = CascadeType.ALL,  fetch = FetchType.EAGER)
    private MockOption answer;

    private QuestionType questionType;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;
}
