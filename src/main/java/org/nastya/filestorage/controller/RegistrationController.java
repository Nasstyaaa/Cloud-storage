package org.nastya.filestorage.controller;

import jakarta.validation.Valid;
import org.nastya.filestorage.DTO.UserDTO;
import org.nastya.filestorage.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping
    public String registrationPage(@ModelAttribute("user") UserDTO userDTO){
        return "registration";
    }

    @PostMapping
    public String performRegistration(@ModelAttribute("user") @Valid UserDTO userDTO,
                                      BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return "registration";
        }
        registrationService.register(userDTO);

        return "redirect:/login";
    }
}
