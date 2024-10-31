package org.nastya.filestorage.DTO.folder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RenameFolderRequestDTO extends FolderRequestDTO {
    private String nameFolder;
    private String newName;

    public RenameFolderRequestDTO(String nameFolder, String newName, String path) {
        super(path);
        this.nameFolder = nameFolder;
        this.newName = newName;
    }
}
