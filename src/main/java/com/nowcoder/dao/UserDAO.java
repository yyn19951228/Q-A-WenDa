package com.nowcoder.dao;

import com.nowcoder.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserDAO {
    String TABLE_NAME = " user ";
    String INSERT_FIELDS = " name, password, salt, head_url ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    // here fill in sql command
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") " +
            "values(#{name}, #{password}, #{salt}, #{headUrl})"}) // the reason use headUrl is because of
    //  <setting name="mapUnderscoreToCamelCase" value="true"/> in mybatis-config.xml
    int addUser(User user);

    @Select({"select", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    User selectByID(int id);

    @Select({"select", SELECT_FIELDS, " from ", TABLE_NAME, " where name=#{name}"})
    User selectByName(String name);

    @Update({"update", TABLE_NAME, " set password=#{password} where id=#{id}"})
    void updatePasswd(User user);

    @Delete({"delete from ", TABLE_NAME, " where id=#{id}"})
    void deleteById(User user);
}
