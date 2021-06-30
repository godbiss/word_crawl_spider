package com.zhu.commentSpider.test;

import com.zhu.commentSpider.dao.CommentMapper;
import com.zhu.commentSpider.entities.Comment;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Log4j2
public class MybatisTest {

    @Test
    public void testSelect() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            CommentMapper commentMapper = session.getMapper(CommentMapper.class);
            List<Comment> allComment = commentMapper.getAllComment();
            for (Comment comment:
                 allComment) {
                log.info(comment);
            }

        }
    }
}
