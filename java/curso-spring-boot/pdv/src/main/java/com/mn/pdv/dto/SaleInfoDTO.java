package com.mn.pdv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SaleInfoDTO {

    private String user;
    private String date;
    private BigDecimal total;
    private List<ProductInfoDTO> products;
}
