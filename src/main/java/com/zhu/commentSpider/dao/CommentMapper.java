package com.zhu.commentSpider.dao;

import com.zhu.commentSpider.entities.Comment;

import java.util.List;

public interface CommentMapper {

    List<Comment> getAllComment();
    Integer addComment(Comment comment);
    Comment getCommentById(Integer id);

}
