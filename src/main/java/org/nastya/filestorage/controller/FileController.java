package org.nastya.filestorage.controller;

import com.google.common.net.HttpHeaders;
import jakarta.validation.Valid;
import org.nastya.filestorage.DTO.file.UploadFileRequestDTO;
import org.nastya.filestorage.security.CustomUserDetails;
import org.nastya.filestorage.service.FileService;
import org.nastya.filestorage.util.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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
    public String uploadFile(@ModelAttribute("files") UploadFileRequestDTO requestDTO,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        String fullPath = MinioUtil.getUserPrefix(userDetails.getId()) + requestDTO.getPath();
        requestDTO.setPath(fullPath);
        fileService.upload(requestDTO);
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


    @PostMapping("/rename")
    public String renameFile(@RequestParam("fileName") String sourceFile, @RequestParam("newFile") String newFile,
                             @AuthenticationPrincipal CustomUserDetails userDetails){
        fileService.rename(userDetails.getId(), sourceFile, newFile);

        return "redirect:/home";
    }

}
