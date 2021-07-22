package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM USERS WHERE username = #{username}")
    User getUserByUsername(String username);

    @Select("SELECT EXISTS("
            + "SELECT 1 FROM USERS WHERE username = #{username}"
            + ")")
    boolean userExistsWithUsername(String username);

    @Insert("INSERT INTO "
            + "USERS (username, salt, password, firstname, lastname) "
            + "VALUES ("
            + "#{username}, #{salt}, #{password}, #{firstName}, #{lastName})")
    int insert(User user);
}
