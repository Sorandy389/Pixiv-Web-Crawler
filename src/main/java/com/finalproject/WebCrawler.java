package com.finalproject;

import java.util.Map;

public class WebCrawler {

    private static String baseUrl = "http://www.nipic.com";

    public static void main(String[] args) {
        Map<String, Boolean> urlMap = UrlMap.getUrl(baseUrl);
        for (Map.Entry<String, Boolean> entry : urlMap.entrySet()) {
            try {
                ContentCrawl.imgDownload(entry.getKey());
            } catch (Exception e) {
                System.out.println("Error: image download failed with url = " + entry.getKey());
            }
        }
    }
}
