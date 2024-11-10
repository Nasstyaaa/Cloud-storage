package org.nastya.filestorage.service;

import lombok.extern.slf4j.Slf4j;
import org.nastya.filestorage.model.User;
import org.nastya.filestorage.repository.UserRepository;
import org.nastya.filestorage.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public CustomUserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));

        log.info("User with username {} successfully uploaded", user.getUsername());
        return new CustomUserDetails(user);
    }
}
