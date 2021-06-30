package com.zhu.commentSpider.spider.resultHandler;

import java.util.Map;

public interface ResultHandler {

    void handle(Map<String, Object> parsedInfo);
}
