package org.nastya.filestorage.DTO.folder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoveFolderRequestDTO {
    private String nameFolder;
    private String path;
}
