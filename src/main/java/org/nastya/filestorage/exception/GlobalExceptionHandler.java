package org.nastya.filestorage.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(UserAlreadyExistsException.class)
    public String handleUserAlreadyExistsException(UserAlreadyExistsException e,
                                                        RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/registration";
    }

    @ExceptionHandler(FileUploadException.class)
    public String handleFileUploadException(FileUploadException e,
                                            RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/home";
    }
}
