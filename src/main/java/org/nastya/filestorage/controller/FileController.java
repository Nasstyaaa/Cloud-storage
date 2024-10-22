package org.nastya.filestorage.controller;

import org.nastya.filestorage.security.CustomUserDetails;
import org.nastya.filestorage.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        fileService.upload(userDetails.getId(), file);

        return "redirect:home";
    }
}
