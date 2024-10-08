package com.mn.pdv.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SaleDTO {

    private long userid;
    List<ProductSaleDTO> items;

}
