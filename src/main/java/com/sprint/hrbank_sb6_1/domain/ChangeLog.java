package com.sprint.hrbank_sb6_1.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class ChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int status;

    @Lob
    @Column(nullable = false)
    private String description;

    @Column
    private String memo;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private LocalDateTime at;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

}