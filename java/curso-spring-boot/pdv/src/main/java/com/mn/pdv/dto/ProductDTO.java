package com.mn.pdv.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDTO {
    private Long id;

    @NotBlank(message = "O campo descrição é obrigatório")
    private String description;

    @NotNull(message = "O campo preço é obrigatório")
    private BigDecimal price;

    @NotNull(message = "O campo quantidade é obrigatório")
    @Min(1)
    private int quantity;
}
