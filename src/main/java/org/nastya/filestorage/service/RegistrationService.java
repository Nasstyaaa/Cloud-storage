package org.nastya.filestorage.service;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.nastya.filestorage.DTO.UserDTO;
import org.nastya.filestorage.exception.UserAlreadyExistsException;
import org.nastya.filestorage.model.User;
import org.nastya.filestorage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.View;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder, View error) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(UserDTO userDTO) {
        User user = new ModelMapper().map(userDTO, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");

        try{
            userRepository.save(user);
        } catch (DataIntegrityViolationException e){
            throw new UserAlreadyExistsException();
        }
    }
}
