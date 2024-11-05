package org.nastya.filestorage.controller;

import com.google.common.net.HttpHeaders;
import org.nastya.filestorage.DTO.folder.DownloadFolderRequestDTO;
import org.nastya.filestorage.DTO.folder.RemoveFolderRequestDTO;
import org.nastya.filestorage.DTO.folder.RenameFolderRequestDTO;
import org.nastya.filestorage.DTO.folder.UploadFolderRequestDTO;
import org.nastya.filestorage.exception.EmptyObjectNameException;
import org.nastya.filestorage.security.CustomUserDetails;
import org.nastya.filestorage.service.FolderService;
import org.nastya.filestorage.util.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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
        String folderPath = requestDTO.getPath().replace(Arrays.toString(requestDTO.getFolder()) + "/", "");
        String fullPath = MinioUtil.getUserPrefix(userDetails.getId()) + requestDTO.getPath();
        requestDTO.setPath(fullPath);

        folderService.upload(requestDTO);
        return "redirect:/home?path=" + folderPath;
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadFolder(@ModelAttribute("folder") DownloadFolderRequestDTO requestDTO,
                                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String fullPath = MinioUtil.getUserPrefix(userDetails.getId()) + requestDTO.getPath();
        requestDTO.setPath(fullPath);

        ByteArrayResource fileData = folderService.download(requestDTO);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                        + URLEncoder.encode(requestDTO.getNameFolder(), StandardCharsets.UTF_8) + ".zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileData);
    }

    @PostMapping("/remove")
    public String removeFolder(@ModelAttribute("folder") RemoveFolderRequestDTO requestDTO,
                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        String folderPath = requestDTO.getPath().replace(requestDTO.getNameFolder() + "/", "");
        String fullPath = MinioUtil.getUserPrefix(userDetails.getId()) + requestDTO.getPath();
        requestDTO.setPath(fullPath);

        folderService.remove(requestDTO);

        return "redirect:/home?path=" + folderPath;
    }

    @PostMapping("/rename")
    public String renameFolder(@ModelAttribute("folder") RenameFolderRequestDTO requestDTO,
                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (requestDTO.getNewPath().isEmpty()){
            throw new EmptyObjectNameException();
        }

        String folderPath = requestDTO.getPath().replace(requestDTO.getNameFolder() + "/", "");
        String fullPath = MinioUtil.getUserPrefix(userDetails.getId()) + requestDTO.getPath();
        String newFullPath = fullPath.replace(requestDTO.getNameFolder(), requestDTO.getNewPath());
        requestDTO.setPath(fullPath);
        requestDTO.setNewPath(newFullPath);

        folderService.rename(requestDTO);

        return "redirect:/home?path=" + folderPath;
    }
}
