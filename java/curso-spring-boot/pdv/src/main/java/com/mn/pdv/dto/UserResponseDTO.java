package com.mn.pdv.dto;

import com.mn.pdv.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponseDTO {
    private Long id;
    private String name;
    private String username;
    private boolean isEnable;
}
