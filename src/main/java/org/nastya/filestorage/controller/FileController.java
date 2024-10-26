package org.nastya.filestorage.controller;

import com.google.common.net.HttpHeaders;
import org.nastya.filestorage.security.CustomUserDetails;
import org.nastya.filestorage.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile[] files,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        for (MultipartFile file : files) {
            fileService.upload(userDetails.getId(), file);
        }

        return "redirect:/home";
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadFile(@RequestParam("file") String file,
                                                          @AuthenticationPrincipal CustomUserDetails userDetails){
        ByteArrayResource fileData = fileService.download(userDetails.getId(), file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                        + URLEncoder.encode(file, StandardCharsets.UTF_8) + "\"")
                .body(fileData);

    }

    @PostMapping("/remove")
    public String removeFile(@RequestParam("file") String file,
                             @AuthenticationPrincipal CustomUserDetails userDetails){
        fileService.remove(userDetails.getId(), file);
        return "redirect:/home";
    }


}
