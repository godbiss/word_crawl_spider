package com.zhu.commentSpider.utils;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import java.io.*;
import java.util.Map;
import java.util.Properties;

@Log4j2
public class FileUtil {

    public static Properties readProperties(String fileName) {
        Properties ps = new Properties();
        try {
            ps.load(FileUtil.class.getClassLoader().getResourceAsStream(fileName+".properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Properties tempPs = new Properties();
        for (Map.Entry<Object, Object> entry:
             ps.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            if(value.contains(":")){
                value.replace("\\", "");
            }

            tempPs.put(key, value);
        }

        return tempPs;
    }

    @SneakyThrows
    public static String saveContent(String fileName, String content){
        String saveFilePath = new String();
        String folderPath = new File("src/main/resources/content_info").getAbsolutePath();
        File folder = new File(folderPath);
        if(!folder.exists()){
            folder.mkdirs();
        }

        saveFilePath = folderPath + "\\" + fileName + ".txt";
        File saveFile = new File(saveFilePath);
        saveFile.createNewFile();
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(saveFile));
        bufferedWriter.write(content);

        bufferedWriter.close();
        return saveFilePath;
    }

    public static void dealZhihuCookieProp() {
        File file = new File("src/main/resources/zhihu_cookie.properties");
        String newContent = new String();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))
             ){
            StringBuffer content = new StringBuffer();
            while (true){
                String line = reader.readLine();
                if(line == null){
                    break;
                }
                content.append(line);
            }

            newContent = content.toString().replace("; ", "\r\n")
                    .replace("\\", "\\\\")
                    .replace(":", "\\:");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (PrintWriter writer = new PrintWriter(file)){
            writer.print(newContent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void dealCookie(){
        FileUtil.dealZhihuCookieProp();
    }

    @Test
    public void testFilePath(){
        String path = new File("src/main/resources").getAbsolutePath();
        log.info(new File(path).exists());
    }
}
