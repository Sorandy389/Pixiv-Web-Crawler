package com.finalproject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UrlPool {

    private static Map<String, String> cookies = new LinkedHashMap<>();
    public Map<String, String> urlMap = new LinkedHashMap<>();

    // variables support for multi-thread
    private int threadCount;
    public List<Map<String, String>> urlThreadMapList = new ArrayList<Map<String, String>>();
    private int urlCount = 0;

    UrlPool(Map<String, String> cookies, int threadCount) {
        this.cookies = cookies;
        this.threadCount = threadCount;
        for (int i=0; i<threadCount; i++) {
            urlThreadMapList.add(new LinkedHashMap<String, String>());
        }
    }

    // Get the URL map for download images
    public void getUrlMap(String url, int page) {
        for (int i=0; i<page; i++) {
            // each link refers to a daily ranked list
            // extract first 50 ranked pic download url into the map
            // get the next url
            url = updateUrlMap(url);
            // check if the next url is null
            if (url == null) {
                break;
            }
        }
    }

    private String updateUrlMap(String url) {
        Document rankDocument = null;
        try {
            rankDocument = Jsoup.connect(url)
                    .cookies(cookies)
                    .get();
        } catch (Exception e) {
            System.out.println("Error: rank url open failed");
        }
        // get elements of image urls
        Elements imgElements = rankDocument.select("#wrapper")
                .select("div.layout-body")
                .select("div")
                .select("div.ranking-items-container")
                .select("div.ranking-items.adjust")
                .select("section.ranking-item");
        // put image url into the url pool
//        int limit = 0;
        for (Element imgElement : imgElements) {
            String imgId = imgElement.attr("data-id");
            String imgUrl = imgElement.select("div.ranking-image-item")
                    .select("a")
                    .get(0)
                    .absUrl("href");
            putUrlMap(imgUrl, imgId);
//            limit++;
//            if (limit >= 9) {
//                break;
//            }
        }

        // fetch the url of next page
        String nextUrl = null;
        Elements pages = rankDocument.select("#wrapper")
                .select("div.layout-body")
                .select("div")
                .select("div.ui-fixed-container")
                .select("div")
                .select("nav:nth-child(2)")
                .select("ul")
                .select("li.after")
                .select("a");
        try {
            nextUrl = pages.get(0).absUrl("href");
        } catch (Exception e) {
            System.out.println("Error: cannot fetch next page url");
        }
        return nextUrl;
    }

    private void putUrlMap(String imgUrl, String imgId) {
        urlMap.put(imgUrl, imgId);
        // support for threaded usage
        urlThreadMapList.get(urlCount%threadCount).put(imgUrl, imgId);
        urlCount++;
        System.out.println("Add download url: " + imgUrl);
    }

    public static void main(String[] args) {
        // the main page of target website
        cookies.put("PHPSESSID", "73818768_O4yqNjMhFIGKqLyMa7UKInowL7iClaRG");
        UrlPool urlMap = new UrlPool(cookies,8);
        urlMap.getUrlMap("https://www.pixiv.net/ranking.php?mode=male", 10);
    }
}
