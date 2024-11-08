package org.nastya.filestorage.controller;

import com.google.common.net.HttpHeaders;
import org.nastya.filestorage.DTO.file.DownloadFileRequestDTO;
import org.nastya.filestorage.DTO.file.RemoveFileRequestDTO;
import org.nastya.filestorage.DTO.file.RenameFileRequestDTO;
import org.nastya.filestorage.DTO.file.UploadFileRequestDTO;
import org.nastya.filestorage.exception.EmptyObjectNameException;
import org.nastya.filestorage.security.CustomUserDetails;
import org.nastya.filestorage.service.FileService;
import org.nastya.filestorage.util.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.nastya.filestorage.util.PathUtil.getFullPath;
import static org.nastya.filestorage.util.PathUtil.getPathToFile;

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
        String encodedFilePath = URLEncoder.encode(requestDTO.getPath(), StandardCharsets.UTF_8);
        String fullPath = getFullPath(userDetails.getId(), requestDTO.getPath());
        requestDTO.setPath(fullPath);

        fileService.upload(requestDTO);
        return "redirect:/home?path=" + encodedFilePath;
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadFile(@ModelAttribute("files") DownloadFileRequestDTO requestDTO,
                                                          @AuthenticationPrincipal CustomUserDetails userDetails){
        String fullPath = getFullPath(userDetails.getId(), requestDTO.getPath());
        requestDTO.setPath(fullPath);

        ByteArrayResource fileData = fileService.download(requestDTO);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                        + URLEncoder.encode(requestDTO.getNameFile(), StandardCharsets.UTF_8) + "\"")
                .body(fileData);

    }

    @PostMapping("/remove")
    public String removeFile(@ModelAttribute("files") RemoveFileRequestDTO requestDTO,
                             @AuthenticationPrincipal CustomUserDetails userDetails){
        String pathToFile = getPathToFile(requestDTO.getPath(), requestDTO.getNameFile());
        String fullPath = getFullPath(userDetails.getId(), requestDTO.getPath());
        requestDTO.setPath(fullPath);

        fileService.remove(requestDTO);
        return "redirect:/home?path=" + pathToFile;
    }


    @PostMapping("/rename")
    public String renameFile(@ModelAttribute("files") RenameFileRequestDTO requestDTO,
                             @AuthenticationPrincipal CustomUserDetails userDetails){
        if (requestDTO.getNewPath().isEmpty()){
            throw new EmptyObjectNameException();
        }

        String pathToFile = getPathToFile(requestDTO.getPath(), requestDTO.getNameFile());
        String fullPath = getFullPath(userDetails.getId(), requestDTO.getPath());
        String newFullPath = fullPath.replace(requestDTO.getNameFile(), requestDTO.getNewPath());
        requestDTO.setPath(fullPath);
        requestDTO.setNewPath(newFullPath);

        fileService.rename(requestDTO);

        return "redirect:/home?path=" + pathToFile;
    }

}
