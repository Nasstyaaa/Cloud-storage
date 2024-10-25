package org.nastya.filestorage.controller;

import org.nastya.filestorage.security.CustomUserDetails;
import org.nastya.filestorage.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/folder")
public class FolderController {
    private final FolderService folderService;

    @Autowired
    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @PostMapping("/upload")
    public String uploadFolder(@RequestParam("folder") MultipartFile[] files,
                               @AuthenticationPrincipal CustomUserDetails userDetails){
        folderService.upload(userDetails.getId(), files);

        return "redirect:/home";
    }
}
