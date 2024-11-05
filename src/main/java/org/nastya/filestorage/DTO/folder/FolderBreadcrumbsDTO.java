package org.nastya.filestorage.DTO.folder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FolderBreadcrumbsDTO {
    private String name;
    private String path;
}
