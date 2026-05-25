package com.atesti.management.application.query.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyItemResponse {
    private String month;
    private long count;
}
