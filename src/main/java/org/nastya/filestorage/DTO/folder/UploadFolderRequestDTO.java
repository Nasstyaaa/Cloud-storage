package org.nastya.filestorage.DTO.folder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
public class UploadFolderRequestDTO {
    private MultipartFile[] folder;
    private String path;
}
