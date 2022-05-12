package com.finalproject;

import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class ContentCrawl {

    private static Map<String, String> cookies = new LinkedHashMap<>();

    ContentCrawl(String s1, String s2) {
        cookies.put(s1, s2);
    }

    public void imgDownload(String url, String imgId) {
        try {
            Connection.Response response = Jsoup.connect(url).cookies(cookies).ignoreContentType(true).maxBodySize(1073741824)
                    .referrer("https://www.pixiv.net/artworks/" + imgId).execute();
            String filename = url.substring(url.lastIndexOf("/") + 1);
            ByteArrayInputStream stream = new ByteArrayInputStream((response.bodyAsBytes()));
            FileUtils.copyInputStreamToFile(stream, new File("D://webCrawler/"+filename));
            System.out.println("image download: D://webCrawler/"+filename);
        } catch (Exception e) {
            System.out.println("Error: image download failed with url=" + url);
        }
    }

}
