package com.nowcoder.dao;

import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionDAO {
    String TABLE_NAME = " question ";
    String INSERT_FIELDS = " title, content, created_date, user_id, comment_count";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    // here fill in sql command
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") " +
            "values(#{title}, #{content}, #{createdDate}, #{userId}, #{commentCount})"}) // the reason use headUrl is because of
    //  <setting name="mapUnderscoreToCamelCase" value="true"/> in mybatis-config.xml
    int addQuestion(Question question);


    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Question selectById(int id);

    // use method defined by xml
    // the .xml file should have the same name like this interface
    // the namesapce of .xml file should be the path of this DAO file

    List<Question> selectLatestQuestions(@Param("userId") int userId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

}
