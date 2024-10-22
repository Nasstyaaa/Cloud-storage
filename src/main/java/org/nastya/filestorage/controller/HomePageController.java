package org.nastya.filestorage.controller;

import org.nastya.filestorage.security.CustomUserDetails;
import org.nastya.filestorage.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
@RequestMapping("/home")
public class HomePageController {

    private final FileService fileService;

    @Autowired
    public HomePageController(FileService fileService) {
        this.fileService = fileService;
    }


    @GetMapping
    public String homePage(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<String> fileList = fileService.findAll(userDetails.getId());

        model.addAttribute("files", fileList);
        model.addAttribute("username", userDetails.getUsername());
        return "home";
    }
}
