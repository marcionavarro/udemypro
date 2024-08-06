package com.mn.pdv.service;

import com.mn.pdv.dto.UserDTO;
import com.mn.pdv.dto.UserResponseDTO;
import com.mn.pdv.entity.User;
import com.mn.pdv.exceptions.NoItemException;
import com.mn.pdv.repository.UserRepository;
import com.mn.pdv.security.SecurityConfig;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    private ModelMapper mapper = new ModelMapper();


    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream().map(user -> new UserResponseDTO(
                user.getId(), user.getName(), user.getUsername(), user.isEnable()))
                .collect(Collectors.toList()
                );
    }

    public UserDTO save(UserDTO user) {
        user.setPassword(SecurityConfig.passwordEncoder().encode(user.getPassword()));
        user.setEnable(true);
        User userToSave = mapper.map(user, User.class);
        userRepository.save(userToSave);
        return new UserDTO(userToSave.getId(), userToSave.getName(), userToSave.getUsername(),
                userToSave.getPassword(), userToSave.isEnable());
    }

    public UserDTO findById(long id) {
        Optional<User> optional = userRepository.findById(id);

        if (!optional.isPresent()) {
            throw new NoItemException("Usuário não encontrado!");
        }

        User user = optional.get();
        return new UserDTO(user.getId(), user.getName(), user.getUsername(),
                user.getPassword(), user.isEnable());
    }

    public UserDTO update(UserDTO user) {
        user.setPassword(SecurityConfig.passwordEncoder().encode(user.getPassword()));
        User userToSave = mapper.map(user, User.class);
        Optional<User> userToEdit = userRepository.findById(userToSave.getId());

        if (!userToEdit.isPresent()) {
            throw new NoItemException("Usuário não encontrado!");
        }

        userRepository.save(userToSave);
        return new UserDTO(userToSave.getId(), userToSave.getName(), userToSave.getUsername(),
                userToSave.getPassword(), userToSave.isEnable());
    }

    public void deleteById(long id) {
        Optional<User> optional = userRepository.findById(id);

        if (!optional.isPresent()) {
            throw new NoItemException("Usuário não encontrado!");
        }

        userRepository.deleteById(id);
    }

    public User getByUserName(String username) {
        return userRepository.findUserByUsername(username);
    }
}
