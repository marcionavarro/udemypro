package com.mn.pdv.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductSaleDTO {

    @NotNull(message = "O item da venda é obrigatório")
    private long productid;

    @NotNull(message = "O campo quantidade é obrigatório")
    @Min(1)
    private int quantity;

}
