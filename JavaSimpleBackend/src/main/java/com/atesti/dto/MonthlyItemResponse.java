package com.atesti.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyItemResponse {
    private String month;
    private long count;
}
