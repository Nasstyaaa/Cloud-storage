package org.nastya.filestorage.controller;

import com.google.common.net.HttpHeaders;
import org.nastya.filestorage.DTO.folder.DownloadFolderRequestDTO;
import org.nastya.filestorage.DTO.folder.UploadFolderRequestDTO;
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
        String fullPath = MinioUtil.getUserPrefix(userDetails.getId()) + requestDTO.getPath();
        requestDTO.setPath(fullPath);
        folderService.upload(requestDTO);
        return "redirect:/home";
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadFolder(@ModelAttribute("folderDownload") DownloadFolderRequestDTO requestDTO,
                                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String fullPath = MinioUtil.getUserPrefix(userDetails.getId()) + requestDTO.getPath();
        String fullName = MinioUtil.addSeparator(requestDTO.getNameFolder());
        requestDTO.setPath(fullPath);
        requestDTO.setNameFolder(fullName);

        ByteArrayResource fileData = folderService.download(requestDTO);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                        + URLEncoder.encode(requestDTO.getNameFolder(), StandardCharsets.UTF_8) + ".zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileData);
    }

//    @PostMapping("/remove")
//    public String removeFolder(@RequestParam("folder") String folder,
//                               @AuthenticationPrincipal CustomUserDetails userDetails) {
//        folderService.remove(userDetails.getId(), folder + "/");
//
//        return "redirect:/home";
//    }
//
//    @PostMapping("/rename")
//    public String renameFolder(@RequestParam("folderName") String sourceFolder, @RequestParam("newFolder") String newFolder,
//                             @AuthenticationPrincipal CustomUserDetails userDetails) {
//        folderService.rename(userDetails.getId(), sourceFolder + "/", newFolder + "/");
//
//        return "redirect:/home";
//    }
}
