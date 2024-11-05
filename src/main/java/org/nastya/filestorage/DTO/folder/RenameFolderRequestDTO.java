package org.nastya.filestorage.DTO.folder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RenameFolderRequestDTO extends FolderRequestDTO {
    private String nameFolder;
    private String newPath;

    public RenameFolderRequestDTO(String nameFolder, String newPath, String path) {
        super(path);
        this.nameFolder = nameFolder;
        this.newPath = newPath;
    }
}
