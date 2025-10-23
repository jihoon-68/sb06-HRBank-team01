package com.sprint.hrbank_sb6_1.dto.data;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDistributionDto {
    private String groupKey;
    private Long count;
    private Double percentage;

    public EmployeeDistributionDto(String groupKey, Long count) {
        this.groupKey = groupKey;
        this.count = count;
    }
}
