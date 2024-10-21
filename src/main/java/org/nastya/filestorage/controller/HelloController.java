package org.nastya.filestorage.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("/hello")
    public String hello(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        return "hello";
    }
}
