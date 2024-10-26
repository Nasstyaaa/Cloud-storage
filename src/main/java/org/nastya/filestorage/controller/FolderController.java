package org.nastya.filestorage.controller;

import com.google.common.net.HttpHeaders;
import org.nastya.filestorage.security.CustomUserDetails;
import org.nastya.filestorage.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadFile(@RequestParam("folder") String folder,
                                                          @AuthenticationPrincipal CustomUserDetails userDetails){
        ByteArrayResource fileData = folderService.download(userDetails.getId(), folder);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                        + URLEncoder.encode(folder, StandardCharsets.UTF_8) + ".zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileData);
    }
}
