package org.nastya.filestorage.DTO.folder;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadFolderRequestDTO {
    private MultipartFile[] folder;
    private String userPrefix;

    public UploadFolderRequestDTO(MultipartFile[] folder) {
        this.folder = folder;
    }
}
