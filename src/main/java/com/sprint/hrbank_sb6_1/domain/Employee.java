package com.sprint.hrbank_sb6_1.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String employeeNumber;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private LocalDate hireDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeStatus status;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToOne
    @JoinColumn(name = "profile_image_id")
    private File profileImage;

    public void update(Department department, String name, String position,
                       String email, EmployeeStatus status, File profileImage,
                       LocalDate hireDate) {
        this.department = department;
        this.name = name;
        this.position = position;
        this.email = email;
        this.status = status;
        this.profileImage = profileImage;
        this.hireDate = hireDate;
    }

    public Employee clone() {
        return Employee.builder()
                .id(id)
                .employeeNumber(employeeNumber)
                .profileImage(this.getProfileImage())
                .status(this.getStatus())
                .position(this.getPosition())
                .email(this.getEmail())
                .department(this.getDepartment())
                .name(this.getName())
                .hireDate(this.getHireDate())
                .build();
    }
}
