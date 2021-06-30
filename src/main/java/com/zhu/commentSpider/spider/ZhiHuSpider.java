package com.zhu.commentSpider.spider;

import com.zhu.commentSpider.utils.FileUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ZhiHuSpider implements PageProcessor {

    private List<Cookie> cookies = new ArrayList<>();

    private Site site = Site.me()
            .setCharset("utf-8")
            .setSleepTime(5000)
            .setTimeOut(10000)
            .setRetryTimes(3)
            .setCycleRetryTimes(1000)
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.864.37")
            .setRetrySleepTime(10000);

    public void process(Page page) {
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe");

        WebDriver driver = new ChromeDriver();

        driver.get(page.getUrl().toString());

        //添加cookie
        for (int i = 0; i < cookies.size(); i++) {
            driver.manage().addCookie(cookies.get(i));
        }

        //暂停等待操作
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //执行滚动js命令
        JavascriptExecutor jse= (JavascriptExecutor)driver;
        jse.executeScript("window.scrollBy(0,3301)", "");


        List<WebElement> elements = driver.findElements(By.xpath("//div[@class=List-item]//p"));
        for (WebElement webElement:
             elements) {
            System.out.println(webElement.getText());
        }

        driver.close();
    }



    public void fillZhihuCookies(){
        Properties properties = FileUtil.readProperties("zhihu_cookie");

        for (Map.Entry<Object, Object> entry:
            properties.entrySet()){
            cookies.add(new Cookie((String) entry.getKey(), (String) entry.getValue()));
            site.addCookie((String) entry.getKey(), (String) entry.getValue());
        }
    }

    // TODO 模拟登录获取知乎Cookies : OpenCV、Selenium
//    public void login(){
//        System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe");
//
//        WebDriver driver = new ChromeDriver();
//        driver.get("https://www.zhihu.com/");// 打开网址
//
//        // 防止页面未能及时加载出来而设置一段时间延迟
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        WebElement webElement = driver.findElement(By.xpath("//html"));
//        System.out.println(webElement.getAttribute("outerHTML"));
//        driver.close();
//    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        ZhiHuSpider zhiHuSpider = new ZhiHuSpider();
        zhiHuSpider.fillZhihuCookies();
        Spider.create(zhiHuSpider)
                .addUrl("https://www.zhihu.com/question/382377283")
                .addPipeline(new ConsolePipeline())
                .thread(5).run();
    }
}
