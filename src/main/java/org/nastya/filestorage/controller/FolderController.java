package org.nastya.filestorage.controller;

import com.google.common.net.HttpHeaders;
import io.minio.errors.*;
import org.nastya.filestorage.DTO.folder.UploadFolderRequestDTO;
import org.nastya.filestorage.security.CustomUserDetails;
import org.nastya.filestorage.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Controller
@RequestMapping("/folder")
public class FolderController {
    private final FolderService folderService;

    @Autowired
    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @PostMapping("/upload")
    public String uploadFolder(@ModelAttribute("folder") UploadFolderRequestDTO requestDTO,
                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        folderService.upload(requestDTO);

        return "redirect:/home";
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadFolder(@RequestParam("folder") String folder,
                                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ByteArrayResource fileData = folderService.download(userDetails.getId(), folder + "/"); //TODO поправить фронт
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                        + URLEncoder.encode(folder, StandardCharsets.UTF_8) + ".zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileData);
    }

    @PostMapping("/remove")
    public String removeFolder(@RequestParam("folder") String folder,
                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        folderService.remove(userDetails.getId(), folder + "/");

        return "redirect:/home";
    }

    @PostMapping("/rename")
    public String renameFolder(@RequestParam("folderName") String sourceFolder, @RequestParam("newFolder") String newFolder,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        folderService.rename(userDetails.getId(), sourceFolder + "/", newFolder + "/");

        return "redirect:/home";
    }
}
