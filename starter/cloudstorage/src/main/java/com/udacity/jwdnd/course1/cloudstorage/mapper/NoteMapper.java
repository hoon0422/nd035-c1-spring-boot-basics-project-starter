package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface NoteMapper {
    @Select("SELECT * FROM NOTES WHERE noteid=#{noteId}")
    Note getNoteById(Integer noteId);

    @Select("SELECT * FROM NOTES WHERE userid=#{userId}")
    List<Note> getNotesOfUser(Integer userId);

    @Insert("INSERT INTO "
            + "NOTES (notetitle, notedescription, userid) "
            + "VALUES (#{noteTitle}, #{noteDescription}, #{userId})")
    int insert(Note note);

    @Update("UPDATE NOTES "
            + "SET notetitle=#{noteTitle}, notedescription=#{noteDescription} "
            + "WHERE noteid=#{noteId}")
    int update(Note note);

    @Delete("DELETE FROM NOTES WHERE noteid=#{noteId}")
    void delete(Integer noteId);
}
