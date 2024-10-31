package org.nastya.filestorage.DTO.folder;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadFolderRequestDTO extends FolderRequestDTO {
    private MultipartFile[] folder;
    private String path;

    public UploadFolderRequestDTO(MultipartFile[] folder, String path) {
        super(path);
        this.folder = folder;
    }
}
