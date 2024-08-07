package com.mn.pdv.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginDTO {

    @NotBlank(message = "O campo login é obrigatório")
    private String username;

    @NotBlank(message = "O campo senha é obrigatório")
    private String password;

}
