package com.finalproject;

import java.util.Map;

public class WebCrawler {

    private static String baseUrl = "https://www.pixiv.net/ranking.php?mode=male";
    private static int pageDownload = 10;

    private static String cookie1 = "PHPSESSID";
    private static String cookie2 = "73818768_O4yqNjMhFIGKqLyMa7UKInowL7iClaRG";

    public static void main(String[] args) {
        UrlMap urlMap = new UrlMap(cookie1, cookie2);
        ContentCrawl contentCrawl = new ContentCrawl(cookie1, cookie2);
        Map<String, String> urlMapContent = urlMap.getUrlMap(baseUrl, pageDownload);
        for (Map.Entry<String, String> entry : urlMapContent.entrySet()) {
            contentCrawl.imgDownload(entry.getKey(), entry.getValue());
        }
    }
}
