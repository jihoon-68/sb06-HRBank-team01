package com.sprint.hrbank_sb6_1.dto.data;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeTrendDto {
    private String date;
    private Long count;
    private Integer change;
    private Double changeRate;

    public EmployeeTrendDto(String date, Long count) {
        this.date = date;
        this.count = count;
    }
}
