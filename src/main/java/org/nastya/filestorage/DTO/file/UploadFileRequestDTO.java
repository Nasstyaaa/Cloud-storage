package org.nastya.filestorage.DTO.file;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadFileRequestDTO extends FileRequestDTO{
    private MultipartFile file;

    public UploadFileRequestDTO(MultipartFile file, String path) {
        super(path);
        this.file = file;
    }
}
