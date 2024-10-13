package org.nastya.filestorage;

import org.junit.jupiter.api.Test;
import org.nastya.filestorage.DTO.UserDTO;
import org.nastya.filestorage.exception.UserAlreadyExistsException;
import org.nastya.filestorage.repository.UserRepository;
import org.nastya.filestorage.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class RegistrationServiceTest {

    @Container
    private static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.1");

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void register_userWithUniqueUsername_shouldRegisteredSuccessful(){
        UserDTO userDTO = new UserDTO("Bob", "1234");

        registrationService.register(userDTO);

        assertEquals("Bob", userRepository.findUserByUsername("Bob").get().getUsername());
    }

    @Test
    public void register_userWithNotUniqueUsername_shouldThrowUserAlreadyExistsException(){
        UserDTO user = new UserDTO("Tom", "1234");
        registrationService.register(new UserDTO("Tom", "1718"));

        assertThrows(UserAlreadyExistsException.class, () -> registrationService.register(user));
    }
}
