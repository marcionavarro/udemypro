package com.mn.pdv.security;

import com.mn.pdv.dto.LoginDTO;
import com.mn.pdv.entity.User;
import com.mn.pdv.exceptions.PassworNotFoundException;
import com.mn.pdv.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getByUserName(username);

        if (user == null) {
            throw new UsernameNotFoundException("Login inválido!");
        }

        return new UserPrincipal(user);
    }

    public void verifyUserCredentials(LoginDTO login) {
        UserDetails user = loadUserByUsername(login.getUsername());

        boolean passwordIsTheSams = SecurityConfig.passwordEncoder()
                .matches(login.getPassword(), user.getPassword());

        if (!passwordIsTheSams) {
            throw new PassworNotFoundException("Senha inválida");
        }
    }
}
