package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.error.exceptions.BusinessException;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
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
@RequestMapping("note")
public class NoteController {
    private final NoteService noteService;

    public NoteController(final NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping("create-or-update")
    public String createOrUpdateNote(final @ModelAttribute("newNote") Note note,
                                     final Authentication authentication) {
        String username = (String) authentication.getPrincipal();
        int rows;
        if (note.getNoteId() == null) {
            rows = noteService.createNote(username, note);
        } else {
            rows = noteService.updateNote(username, note);
        }

        if (rows <= 0) {
            throw new RuntimeException(); // Internal server error
        }
        return "redirect:/";
    }

    @GetMapping("delete")
    public String deleteNote(final @RequestParam("noteId") Integer noteId,
                             final Authentication authentication) {
        String username = (String) authentication.getPrincipal();
        noteService.deleteNote(username, noteId);
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
