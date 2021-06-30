package com.zhu.commentSpider.spider.resultHandler;

import com.zhu.commentSpider.dao.CommentMapper;
import com.zhu.commentSpider.entities.Comment;
import com.zhu.commentSpider.utils.FileUtil;
import com.zhu.commentSpider.utils.SqlUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.Map;

@Log4j2
public class MysqlResultHandler implements ResultHandler {
    @Override
    public void handle(Map<String, Object> parsedInfo) {
        SqlSessionFactory sqlSessionFactory = SqlUtil.getSqlSessionFactory();

        //具体操纵
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            CommentMapper commentMapper = session.getMapper(CommentMapper.class);

            List<Integer> itemIds = (List<Integer>) parsedInfo.get("itemIds");
            String question = (String) parsedInfo.get("question");
            List<String> authors = (List<String>) parsedInfo.get("authors");
            List<String> contents = (List<String>) parsedInfo.get("contents");

            for (int i = 0; i < authors.size(); i++) {
                if(commentMapper.getCommentById(itemIds.get(i)) == null){
                    Comment comment = new Comment();
                    comment.setId(itemIds.get(i));
                    comment.setQuestion(question);
                    comment.setAuthorName(authors.get(i));
                    String contentPath = FileUtil.saveContent(String.valueOf(itemIds.get(i)), contents.get(i));
                    comment.setContent(contentPath);

                    commentMapper.addComment(comment);
                }
            }

        }


    }
}
