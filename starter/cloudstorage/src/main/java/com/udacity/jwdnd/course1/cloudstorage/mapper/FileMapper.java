package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.File;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FileMapper {
    @Select("SELECT * FROM FILES WHERE userid = #{userId}")
    List<File> getFilesOfUser(Integer userId);

    @Select("SELECT * FROM FILES WHERE fileid = #{fileId}")
    File getFileById(Integer fileId);

    @Select("SELECT EXISTS("
            + "SELECT 1 FROM FILES "
            + "WHERE fileid = #{fileId}"
            + ")")
    boolean fileExistsWithId(Integer fileId);

    @Select("SELECT * FROM FILES "
            + "WHERE filename=#{fileName} and userid=#{userId}")
    File getFileByName(Integer userId, String fileName);

    @Select("SELECT EXISTS("
            + "SELECT 1 FROM FILES "
            + "WHERE filename=#{fileName} and userid=#{userId}"
            + ")")
    boolean fileExistsWithName(Integer userId, String fileName);

    @Insert("INSERT INTO "
            + "FILES (filename, contenttype, filesize, userid, filedata) "
            + "VALUES ("
            + "#{fileName}, #{contentType}, #{fileSize}, #{userId}, #{fileData}"
            + ")")
    int insert(File file);

    @Delete("DELETE FROM FILES WHERE fileid = #{fileId}")
    void deleteById(Integer fileId);

    @Delete("DELETE FROM FILES "
            + "WHERE filename=#{fileName} and userid=#{userId}")
    void deleteByName(Integer userId, String fileName);
}
