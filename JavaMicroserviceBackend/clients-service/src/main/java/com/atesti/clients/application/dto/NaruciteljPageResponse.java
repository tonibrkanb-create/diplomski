package com.atesti.clients.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaruciteljPageResponse {
    private List<?> items;
    private int page;
    private int pageSize;
    private long totalItems;
    private int totalPages;
    private String sortBy;
    private String sortOrder;
}
