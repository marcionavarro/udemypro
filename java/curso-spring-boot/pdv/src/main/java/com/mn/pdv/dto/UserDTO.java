package com.mn.pdv.dto;

import com.mn.pdv.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {
    private Long id;

    @NotBlank(message = "Campo nome é obrigatório")
    private String name;

    @NotBlank(message = "O Campo username é obrigatório")
    private String username;

    @NotBlank(message = "O Campo senha é obrigatório")
    private String password;

    private boolean isEnable;
}
