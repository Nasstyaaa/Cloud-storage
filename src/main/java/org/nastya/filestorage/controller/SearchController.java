package org.nastya.filestorage.controller;

import org.nastya.filestorage.DTO.BreadcrumbsDTO;
import org.nastya.filestorage.security.CustomUserDetails;
import org.nastya.filestorage.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.nastya.filestorage.util.PathUtil.getFullPath;


@Controller
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }


    @GetMapping
    public String searchPage(@RequestParam(name = "query", defaultValue = "") String query, Model model,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        String path = getFullPath(userDetails.getId(), "");
        List<BreadcrumbsDTO> foundedObjects = searchService.searchObject(query, path);

        model.addAttribute("searchResult", foundedObjects);
        model.addAttribute("username", userDetails.getUsername());
        return "search";
    }
}
