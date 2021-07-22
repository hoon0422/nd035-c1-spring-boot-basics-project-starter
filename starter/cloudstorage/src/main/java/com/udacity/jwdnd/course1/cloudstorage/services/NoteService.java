package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.error.exceptions.InvalidValueException;
import com.udacity.jwdnd.course1.cloudstorage.error.exceptions.entity.EntityNotFoundException;
import com.udacity.jwdnd.course1.cloudstorage.error.exceptions.entity.UserNotFoundWithUsernameException;
import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {
    private final NoteMapper noteMapper;
    private final UserService userService;

    public NoteService(
            final NoteMapper noteMapper,
            final UserService userService) {
        this.noteMapper = noteMapper;
        this.userService = userService;
    }

    public Note getNoteById(final String username, final Integer noteId) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundWithUsernameException();
        }
        Note note = noteMapper.getNoteById(noteId);
        if (!note.getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException(
                    "Note with the ID does not belong to the current user.");
        }
        return note;
    }

    public List<Note> getNotesOfUser(final String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundWithUsernameException();
        }
        return noteMapper.getNotesOfUser(user.getUserId());
    }

    public int createNote(final String username, final Note note) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundWithUsernameException();
        }

        if (note.getNoteTitle().equals("")) {
            throw new InvalidValueException("Title cannot be an empty string.");
        }

        if (note.getNoteDescription().equals("")) {
            throw new InvalidValueException("Title cannot be an empty string.");
        }

        return noteMapper.insert(new Note(
                0,
                note.getNoteTitle(),
                note.getNoteDescription(),
                user.getUserId()
        ));
    }

    public int updateNote(final String username, final Note note) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundWithUsernameException();
        }

        if (note.getNoteTitle().equals("")) {
            throw new InvalidValueException("Title cannot be an empty string.");
        }

        if (note.getNoteDescription().equals("")) {
            throw new InvalidValueException("Title cannot be an empty string.");
        }

        Note noteInDB = noteMapper.getNoteById(note.getNoteId());
        if (noteInDB == null) {
            throw new EntityNotFoundException("Note with the ID is not found.");
        }
        if (!noteInDB.getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException(
                    "Note with the ID does not belong to the current user.");
        }
        noteInDB.setNoteTitle(note.getNoteTitle());
        noteInDB.setNoteDescription(note.getNoteDescription());
        return noteMapper.update(noteInDB);
    }

    public void deleteNote(final String username, final Integer noteId) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundWithUsernameException();
        }

        Note note = noteMapper.getNoteById(noteId);
        if (note == null) {
            throw new EntityNotFoundException("Note with the ID is not found.");
        }

        if (!note.getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException(
                    "Note with the ID does not belong to the current user.");
        }

        noteMapper.delete(noteId);
    }
}
