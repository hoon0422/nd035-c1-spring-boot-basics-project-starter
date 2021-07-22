package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.error.exceptions.BusinessException;
import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("credential")
public class CredentialController {
    private final CredentialService credentialService;

    public CredentialController(final CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    @PostMapping("create-or-update")
    public String createOrUpdateCredential(
            final @ModelAttribute("newCredential")
                    Credential credential, final
            Authentication authentication) {
        String username = (String) authentication.getPrincipal();
        int rows;
        if (credential.getCredentialId() == null) {
            rows = credentialService.createCredential(username, credential);
        } else {
            rows = credentialService.updateCredential(username, credential);
        }

        if (rows <= 0) {
            throw new RuntimeException(); // Internal Server Error
        }

        return "redirect:/";
    }

    @GetMapping("delete")
    public String deleteCredential(
            final @RequestParam("credentialId") Integer credentialId,
            final Authentication authentication) {
        String username = (String) authentication.getPrincipal();
        credentialService.deleteCredential(username, credentialId);
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
        exceptionName = "CredentialView" + exceptionName;
        redirectAttributes.addFlashAttribute(exceptionName, e.getMessage());
        return "forward:/";
    }
}
