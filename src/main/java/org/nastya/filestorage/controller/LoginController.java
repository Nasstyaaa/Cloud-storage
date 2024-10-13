package org.nastya.filestorage.controller;

import org.nastya.filestorage.DTO.UserDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping
    public String loginPage(@ModelAttribute("user") UserDTO userDTO){
        return "login";
    }
}
