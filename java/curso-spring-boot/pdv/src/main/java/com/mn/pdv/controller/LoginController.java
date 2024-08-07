package com.mn.pdv.controller;

import com.mn.pdv.dto.LoginDTO;
import com.mn.pdv.dto.ResponseDTO;
import com.mn.pdv.dto.TokenDTO;
import com.mn.pdv.security.CustomUserDetailService;
import com.mn.pdv.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private CustomUserDetailService userDetailService;

    @Autowired
    private JwtService jwtService;

    @Value("${security.jwt.expiration}")
    private String expirtaion;

    @PostMapping()
    public ResponseEntity post(@Valid @RequestBody LoginDTO loginData) {
        try {
            userDetailService.verifyUserCredentials(loginData);
            String token = jwtService.generateToken(loginData.getUsername());
            return new ResponseEntity<>(new TokenDTO(token, expirtaion), HttpStatus.OK);
        } catch (Exception error) {
            return new ResponseEntity<>(new ResponseDTO(error.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }
}
