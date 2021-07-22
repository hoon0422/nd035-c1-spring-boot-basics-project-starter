package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.error.exceptions.entity.EntityNotFoundException;
import com.udacity.jwdnd.course1.cloudstorage.error.exceptions.FileNotDownloadedException;
import com.udacity.jwdnd.course1.cloudstorage.error.exceptions.FileNotUploadedException;
import com.udacity.jwdnd.course1.cloudstorage.error.exceptions.InvalidValueException;
import com.udacity.jwdnd.course1.cloudstorage.error.exceptions.entity.UserNotFoundWithUsernameException;
import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class FileService {
    private final FileMapper fileMapper;
    private final UserService userService;

    public FileService(
            final FileMapper fileMapper,
            final UserService userService) {
        this.fileMapper = fileMapper;
        this.userService = userService;
    }

    public List<File> getFilesOfUser(final String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundWithUsernameException();
        }
        return fileMapper.getFilesOfUser(user.getUserId());
    }

    public File getFileById(final Integer fileId) {
        return fileMapper.getFileById(fileId);
    }

    public File getFileByName(final String username, final String fileName) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundWithUsernameException();
        }
        return fileMapper.getFileByName(user.getUserId(), fileName);
    }

    public int uploadFile(final String username,
                          final MultipartFile fileUpload) {
        if (fileUpload.isEmpty()) {
            throw new InvalidValueException("Please choose a file to upload.");
        }
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundWithUsernameException();
        }
        if (fileMapper
                .fileExistsWithName(user.getUserId(),
                        fileUpload.getOriginalFilename())) {
            throw new InvalidValueException(
                    "A file with the same name already exists.");
        }

        File file = new File();
        file.setFileName(fileUpload.getOriginalFilename());
        file.setFileSize(byteToSize(fileUpload.getSize()));
        file.setContentType(fileUpload.getContentType());
        file.setUserId(user.getUserId());

        try {
            InputStream fileIS = fileUpload.getInputStream();
            byte[] data = new byte[fileIS.available()];
            fileIS.read(data);
            fileIS.close();
            file.setFileData(data);
        } catch (IOException e) {
            throw new FileNotUploadedException(
                    "Error happens on uploading a file. " + e.getMessage());
        }

        return fileMapper.insert(file);
    }

    public void downloadFile(final String username, final String fileName,
                             final HttpServletResponse response) {
        File file = getFileByName(username, fileName);
        if (file == null) {
            throw new EntityNotFoundException(
                    "File with the name is not found.");
        }
        response.setContentType(file.getContentType());
        response.addHeader("Content-Disposition",
                "attachment; filename=" + file.getFileName());

        try {
            InputStream is = new ByteArrayInputStream(file.getFileData());
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new FileNotDownloadedException(
                    "Error happens on downloading a file. " + e.getMessage());
        }
    }

    public void deleteFileById(final Integer fileId) {
        fileMapper.deleteById(fileId);
    }

    public void deleteFileByFileName(final String username,
                                     final String fileName) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundWithUsernameException();
        }
        fileMapper.deleteByName(user.getUserId(), fileName);
    }

    private static String byteToSize(final long byteSize) {
        double size = (double) byteSize;
        int unit = 0; // byte - 0, KB - 1, MB - 2, GB - 3, TB - 4, ZB - 5
        while (size / 1024 >= 1 && unit < 5) {
            size /= 1024;
            ++unit;
        }
        size = ((long) (size * 100)) / 100.0;
        switch (unit) {
            case 0:
                return size + "byte";
            case 1:
                return size + "KB";
            case 2:
                return size + "MB";
            case 3:
                return size + "GB";
            case 4:
                return size + "TB";
            default:
                return size + "ZB";
        }
    }
}
