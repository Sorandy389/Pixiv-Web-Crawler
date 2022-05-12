package com.finalproject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedHashMap;
import java.util.Map;

public class UrlMap {

    private static Map<String, String> cookies = new LinkedHashMap<>();
    private Map<String, String> urlMap = new LinkedHashMap<>();

    UrlMap(String s1, String s2) {
        cookies.put(s1, s2);
    }

    // Get the URL map for download images
    public Map<String, String> getUrlMap(String url, int page) {
        for (int i=0; i<page; i++) {
            // each link refers to a daily ranked list
            // extract first 50 ranked pic download url into the map
            updateUrlMap(url);
            // get the next url
            url = getNextUrl(url);
            // check if the next url is null
            if (url == null) {
                break;
            }
        }
        return urlMap;
    }

    private void updateUrlMap(String url) {
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
        // resolve each image url
        for (Element imgElement : imgElements) {
            String imgId = imgElement.attr("data-id");
            String imgUrl = imgElement.select("div.ranking-image-item")
                    .select("a")
                    .get(0)
                    .absUrl("href");
            // resolve the origin image download url
            originImg(imgUrl, imgId);
        }
    }

    private void originImg(String imgUrl, String imgId) {
        Document imgDocument = null;
        try {
            imgDocument = Jsoup.connect(imgUrl)
                    .cookies(cookies)
                    .get();
        } catch (Exception e) {
            System.out.println("Error: image url open failed");
        }

        Element metaElement = imgDocument.select("#meta-preload-data").first();
        String JSONContent = metaElement.attr("content");
        JSONObject obj = JSON.parseObject(JSONContent);
        int pageCount = obj.getJSONObject("illust")
                .getJSONObject(imgId)
                .getIntValue("pageCount");
        String srcUrl = obj.getJSONObject("illust")
                .getJSONObject(imgId)
                .getJSONObject("urls")
                .getString("original");
        // if pageCount == 1, there is only one page
        urlMap.put(srcUrl, imgId);
        System.out.println("Add download url: " + srcUrl);
        // if pageCount > 1, there are multiple pages
        for (int i = 1; i < pageCount; i++) {
            String downloadUrl = srcUrl.replaceAll("p0", "p" + i);
            urlMap.put(downloadUrl, imgId);
            System.out.println("Add download url: " + downloadUrl);
        }
    }

    private String getNextUrl(String url) {
        return null;
    }

    public static void main(String[] args) {
        // the main page of target website
        UrlMap urlMap = new UrlMap("PHPSESSID", "73818768_O4yqNjMhFIGKqLyMa7UKInowL7iClaRG");
        urlMap.getUrlMap("https://www.pixiv.net/ranking.php?mode=male", 10);
    }
}
