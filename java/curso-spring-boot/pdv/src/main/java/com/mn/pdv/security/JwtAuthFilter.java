package com.mn.pdv.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mn.pdv.dto.ResponseDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    private JwtService jwtService;
    private CustomUserDetailService customUserDetailService;

    public JwtAuthFilter(JwtService jwtService, CustomUserDetailService customUserDetailService) {
        this.jwtService = jwtService;
        this.customUserDetailService = customUserDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String authorization = request.getHeader("Authorization");

            if (authorization != null && authorization.startsWith("Bearer")) {
                String token = authorization.split(" ")[1];
                String username = jwtService.getUserName(token);

                UserDetails user = customUserDetailService.loadUserByUsername(username);

                // Cria um usuário que será inserido no contexto do spring security
                UsernamePasswordAuthenticationToken userCtx =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                // Configurando o spring security como uma autenticação web
                userCtx.setDetails(new WebAuthenticationDetailsSource().buildDetails((request)));

                SecurityContextHolder.getContext().setAuthentication(userCtx);
            }

            filterChain.doFilter(request, response);
        } catch (RuntimeException error) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(convertObjectToJson(new ResponseDTO("Token inválido!")));
        }
    }

    public String convertObjectToJson(ResponseDTO responseDTO) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(responseDTO);
    }

}
