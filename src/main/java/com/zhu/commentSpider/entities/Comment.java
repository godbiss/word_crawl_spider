package com.zhu.commentSpider.entities;

import lombok.Data;

@Data
public class Comment {

    private Integer id;
    private String authorName;
    private String content;
    private String question;

}
