package org.nastya.filestorage.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(UserAlreadyExistsException.class)
    public String handleUserAlreadyExistsException(UserAlreadyExistsException e,
                                                        RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/registration";
    }

    @ExceptionHandler({FolderException.class, FileException.class, EmptyObjectNameException.class})
    public String handleFileOrFolderException(RuntimeException e,
                                            RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/home";
    }

    @ExceptionHandler(SearchFileError.class)
    public String handleSearchException(RuntimeException e,
                                        RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/search";
    }


    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadException(MaxUploadSizeExceededException e,
                                           RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("errorMessage", "The size of the uploaded " +
                "object is too large (max - 10MB)");
        return "redirect:/home";
    }
}
