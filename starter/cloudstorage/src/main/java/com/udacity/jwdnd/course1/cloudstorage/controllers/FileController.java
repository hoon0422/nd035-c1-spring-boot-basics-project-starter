package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.error.exceptions.BusinessException;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("file")
public class FileController {
    private final FileService fileService;

    public FileController(final FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("upload")
    public String uploadFile(
            final @RequestParam("fileUpload") MultipartFile fileUpload,
            final Authentication authentication) {
        String username = (String) authentication.getPrincipal();
        fileService.uploadFile(username, fileUpload);
        return "redirect:/";
    }

    @GetMapping("download")
    public void downloadFile(final @RequestParam("name") String fileName,
                             final Authentication authentication,
                             final HttpServletResponse response) {
        String username = (String) authentication.getPrincipal();
        fileService.downloadFile(username, fileName, response);
    }

    @GetMapping("delete")
    public String deleteFile(final @RequestParam("name") String fileName,
                             final Authentication authentication,
                             final Model model) {
        String username = (String) authentication.getPrincipal();
        fileService.deleteFileByFileName(username, fileName);
        model.addAttribute("files", fileService
                .getFilesOfUser((String) authentication.getPrincipal()));
        return "redirect:/";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(BusinessException.class)
    public String handleUserNotFoundWithUsernameException(
            final BusinessException e,
            final RedirectAttributes redirectAttributes
    ) {
        String exceptionName = e.getClass().getSimpleName();
        if (exceptionName.contains(".")) {
            String[] names = exceptionName.split("\\.");
            exceptionName = names[names.length - 1];
        }
        exceptionName = "FileView" + exceptionName;
        redirectAttributes.addFlashAttribute(exceptionName, e.getMessage());
        return "forward:/";
    }
}
