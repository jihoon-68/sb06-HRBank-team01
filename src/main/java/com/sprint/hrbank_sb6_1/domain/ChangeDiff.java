package com.sprint.hrbank_sb6_1.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChangeDiff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String propertyName;

    @Column(nullable = false)
    private String oldValue;

    @Column(nullable = false)
    private String newValue;

    @ManyToOne
    @JoinColumn(name = "change_log_id")
    private ChangeLog changeLog;
}
