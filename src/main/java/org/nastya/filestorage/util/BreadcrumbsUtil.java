package org.nastya.filestorage.util;

import org.nastya.filestorage.DTO.BreadcrumbsDTO;

import java.util.ArrayList;
import java.util.List;

public class BreadcrumbsUtil {

    public static List<BreadcrumbsDTO> createBreadcrumbs(String path){
        List<BreadcrumbsDTO> breadcrumbs = new ArrayList<>();

        if (path.trim().isBlank()) {
            return breadcrumbs;
        }

        String[] parts = path.split("/");
        StringBuilder fullPath = new StringBuilder();

        for (String part : parts) {
            if (!part.isEmpty()) {
                fullPath.append(part).append("/");

                BreadcrumbsDTO breadcrumb = new BreadcrumbsDTO();
                breadcrumb.setName(part);
                breadcrumb.setPath(fullPath.toString());

                breadcrumbs.add(breadcrumb);
            }
        }

        return breadcrumbs;
    }
}
