package com.khoavdse170395.questionservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "MockTest")
public class MockTest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    private Duration duration;

    private Integer totalPoint;

    private Long lessonPlanId;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Long> subscriptionPackageIds;

    @OneToMany(fetch = FetchType.EAGER)
    private List<MockQuestion> questions;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;
}
