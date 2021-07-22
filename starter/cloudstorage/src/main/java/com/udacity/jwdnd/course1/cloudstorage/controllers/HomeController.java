package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.error.exceptions.BusinessException;
import com.udacity.jwdnd.course1.cloudstorage.error.exceptions.entity.UserNotFoundWithUsernameException;
import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping()
public class HomeController {
    private final FileService fileService;
    private final NoteService noteService;
    private final CredentialService credentialService;

    public HomeController(
            final FileService fileService,
            final NoteService noteService,
            final CredentialService credentialService) {
        this.fileService = fileService;
        this.noteService = noteService;
        this.credentialService = credentialService;
    }

    @GetMapping()
    public String homeView(final HttpServletRequest request,
                           final @ModelAttribute("newNote") Note note,
                           final @ModelAttribute("newCredential")
                                   Credential credential,
                           final Model model,
                           final Authentication authentication) {
        String username = (String) authentication.getPrincipal();
        model.addAttribute("files", fileService.getFilesOfUser(username));
        model.addAttribute("notes", noteService.getNotesOfUser(username));
        final List<Credential> credentials =
                credentialService.getCredentialsOfUser(username, false);
        final List<Credential> decrypted =
                credentialService.getCredentialsOfUser(username, true);
        List<Credential[]> credentialsWithDecrypted =
                IntStream.range(0, credentials.size())
                        .mapToObj(idx -> (new Credential[] {
                                credentials.get(idx), decrypted.get(idx)
                        })).collect(Collectors.toList());
        model.addAttribute("credentials", credentialsWithDecrypted);

        final Map<String, ?> inputFlashMap =
                RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            model.addAllAttributes(inputFlashMap);
        }
        return "home";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundWithUsernameException.class)
    public String handleUserNotFoundWithUsernameException(
            final UserNotFoundWithUsernameException e,
            final RedirectAttributes redirectAttributes
    ) {
        String exceptionName = e.getClass().getSimpleName();
        if (exceptionName.contains(".")) {
            String[] names = exceptionName.split("\\.");
            exceptionName = names[names.length - 1];
        }
        exceptionName = "HomeView" + exceptionName;
        redirectAttributes.addFlashAttribute(exceptionName, e.getMessage());

        return "forward:/login";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(
            final BusinessException e,
            final RedirectAttributes redirectAttributes
    ) {
        String exceptionName = e.getClass().getSimpleName();
        if (exceptionName.contains(".")) {
            String[] names = exceptionName.split("\\.");
            exceptionName = names[names.length - 1];
        }
        exceptionName = "HomeView" + exceptionName;
        redirectAttributes.addFlashAttribute(exceptionName, e.getMessage());
        return "forward:/";
    }
}
