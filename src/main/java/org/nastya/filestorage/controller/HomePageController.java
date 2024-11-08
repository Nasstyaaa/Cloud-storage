package org.nastya.filestorage.controller;

import org.nastya.filestorage.DTO.BreadcrumbsDTO;
import org.nastya.filestorage.DTO.folder.FolderRequestDTO;
import org.nastya.filestorage.security.CustomUserDetails;
import org.nastya.filestorage.service.FileService;
import org.nastya.filestorage.service.FolderService;
import org.nastya.filestorage.util.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.nastya.filestorage.util.BreadcrumbsUtil.createBreadcrumbs;
import static org.nastya.filestorage.util.PathUtil.getFullPath;


@Controller
@RequestMapping("/home")
public class HomePageController {

    private final FileService fileService;
    private final FolderService folderService;

    @Autowired
    public HomePageController(FileService fileService, FolderService folderService) {
        this.fileService = fileService;
        this.folderService = folderService;
    }


    @GetMapping
    public String homePage(@RequestParam(name = "path", required = false, defaultValue = "") String path, Model model,
                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        String fullPath = getFullPath(userDetails.getId(), path);
        FolderRequestDTO requestDTO = new FolderRequestDTO(fullPath);

        List<BreadcrumbsDTO> fileList = fileService.getAll(requestDTO);
        List<BreadcrumbsDTO> folderList = folderService.getAll(requestDTO);
        List<BreadcrumbsDTO> breadcrumbsDTOList = createBreadcrumbs(path);

        model.addAttribute("breadcrumbs", breadcrumbsDTOList);
        model.addAttribute("files", fileList);
        model.addAttribute("folders", folderList);
        model.addAttribute("username", userDetails.getUsername());
        return "home";
    }
}
