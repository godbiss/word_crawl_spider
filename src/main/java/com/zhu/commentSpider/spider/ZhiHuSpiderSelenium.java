package com.zhu.commentSpider.spider;

import com.zhu.commentSpider.spider.resultHandler.MysqlResultHandler;
import com.zhu.commentSpider.spider.resultHandler.ResultHandler;
import com.zhu.commentSpider.utils.FileUtil;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Log4j2
public class ZhiHuSpiderSelenium {


    private List<String> urls = new ArrayList<>();
    private List<Cookie> cookies = new ArrayList<>();
    private WebDriver driver;
    private Map<String, Object> parsedInfo = new HashMap<>();

    public ZhiHuSpiderSelenium(){
        init();
        getContent();
        kill();
    }

    public ZhiHuSpiderSelenium(String... url) {
        this.urls.addAll(Arrays.asList(url));
        init();
        getContent();
        kill();
    }

    public void init(){
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe");
        driver = new ChromeDriver();
    }

    public void fillCookies(){
        Properties properties = FileUtil.readProperties("zhihu_cookie");

        driver.manage().deleteAllCookies();

        for (Map.Entry<Object, Object> entry:
                properties.entrySet()){
            Cookie tempCookie = new Cookie((String) entry.getKey(), (String) entry.getValue());
            cookies.add(tempCookie);
            driver.manage().addCookie(tempCookie);
        }

    }


    @SneakyThrows
    public void getContent(){
        driver.get("https://www.zhihu.com/");
        fillCookies();
        //设置超时
        driver.manage().timeouts().implicitlyWait(8, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(8, TimeUnit.SECONDS);

        // TODO 将爬虫做成可爬取多url
        // 循环urls列表
        for (String url:
             this.urls) {

            driver.get(url);

            List<Integer> itemIds = new ArrayList<>();
            List<String> authors = new ArrayList<>();
            List<String> contents = new ArrayList<>();
            String question = new String();

            //滑尽滚动条
            int i = 1;
            while (true){
                int step = 10000;
                //执行滚动js命令
                JavascriptExecutor jse= (JavascriptExecutor)driver;
                jse.executeScript(String.format("window.scrollBy(0,%d)", step*i), "");

                Long scrollHeight = (Long) jse.executeScript("return document.body.scrollHeight");
                System.out.println("第"+i+"次： scrollHeight="+scrollHeight.intValue());


                //解析页面信息
                List<WebElement> itemId_elements = driver.findElements(By.xpath("//div[@class='List-item']" +
                        "/div[@class='ContentItem AnswerItem']"));
                WebElement question_element = driver.findElements(By.xpath("//div[@class='QuestionHeader']/div[@class='QuestionHeader-content']" +
                        "//h1[@class='QuestionHeader-title']")).get(0);
                List<WebElement> author_elements = driver.findElements(By
                        .xpath("//div[@class='List-item']//div[@class='AuthorInfo']/meta[@itemprop='name']"));
                List<WebElement> content_elements = driver.findElements(By
                        .xpath("//div[@class='List-item']//div[@class='RichContent-inner']/span"));

                List<WebElement> nextButton = driver.findElements(By.xpath("//div[@class='Pagination']" +
                        "/button[@class='Button PaginationButton PaginationButton-next Button--plain']"));
                if(nextButton.size() == 0){
                    break;
                }

                // 将数据读入数组
                System.out.println("Question: "+question_element.getText());
                question = question_element.getText();
                for (int j = 0; j < itemId_elements.size(); j++) {
                    //防止重复数据
                    if(!itemIds.contains(Integer.parseInt(itemId_elements.get(j).getAttribute("name")))){
                        System.out.println("itemId: " + itemId_elements.get(j).getAttribute("name"));
                        System.out.println("Author: " + author_elements.get(j).getAttribute("content"));
                        System.out.println(content_elements.get(j).getText());
                        System.out.println("==============================================================");

                        itemIds.add(Integer.parseInt(itemId_elements.get(j).getAttribute("name")));
                        authors.add(author_elements.get(j).getAttribute("content"));
                        contents.add(content_elements.get(j).getText());
                    }

                }

                try {
                    Thread.sleep(8000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                nextButton.get(0).click();
                i++;
            }

            parsedInfo.put("itemIds", itemIds);
            parsedInfo.put("question", question);
            parsedInfo.put("authors", authors);
            parsedInfo.put("contents", contents);

        }


    }

    public void addResultHandler(ResultHandler resultHandler){
        resultHandler.handle(parsedInfo);
    }

    public void addUrl(String url){
        this.urls.add(url);
    }

    public void kill(){
        driver.close();
    }

    public static void main(String[] args) {
        new ZhiHuSpiderSelenium("https://www.zhihu.com/question/366792695/answers/updated")
            .addResultHandler(new MysqlResultHandler());
    }
}
