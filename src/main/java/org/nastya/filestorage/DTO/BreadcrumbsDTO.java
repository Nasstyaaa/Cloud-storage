package org.nastya.filestorage.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BreadcrumbsDTO {
    private String name;
    private String path;
}
