package com.mn.pdv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductInfoDTO {
    private long id;
    private String description;
    private int quantity;
    private BigDecimal price;
}
