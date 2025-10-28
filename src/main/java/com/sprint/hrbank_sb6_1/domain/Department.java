package com.sprint.hrbank_sb6_1.domain;

import com.sprint.hrbank_sb6_1.dto.DepartmentUpdateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Department {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column
  private String description;

  @Column(name = "established_date", nullable = false)
  private LocalDate establishedDate;

  public Department(String name, String description, LocalDate establishedDate) {
    this.name = name;
    this.description = description;
    this.establishedDate = establishedDate;
  }

  public void update(DepartmentUpdateRequest req) {
    if (req == null) {
      return;
    }
    if (req.name() != null && !Objects.equals(req.name(), this.name)) {
      this.name = req.name();
    }
    if (req.description() != null && !Objects.equals(req.description(), this.description)) {
      this.description = req.description();
    }
    if (req.establishedDate() != null && !Objects.equals(req.establishedDate(),
        this.establishedDate)) {
      this.establishedDate = req.establishedDate();
    }
  }
}
