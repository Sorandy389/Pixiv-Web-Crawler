package com.finalproject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataCrawl {

    private static Map<String, String> cookies = new LinkedHashMap<>();
    private static String filePath;

    DataCrawl(Map<String, String> cookies, String filePath) {
        this.cookies = cookies;
        this.filePath = filePath;
    }

    public void imgFetch(String imgUrl, String imgId) {
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
        imgDownload(srcUrl, imgId);
        // if pageCount > 1, there are multiple pages
        for (int i = 1; i < pageCount; i++) {
            String downloadUrl = srcUrl.replaceAll("p0", "p" + i);
            imgDownload(downloadUrl, imgId);
        }
    }

    private void imgDownload(String imgUrl, String imgId) {
        try {
            Connection.Response response = Jsoup.connect(imgUrl)
                    .cookies(cookies)
                    .ignoreContentType(true)
                    .referrer("https://www.pixiv.net/artworks/" + imgId)
                    .execute();
            String filename = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
            ByteArrayInputStream stream = new ByteArrayInputStream((response.bodyAsBytes()));
            FileUtils.copyInputStreamToFile(stream, new File(filePath + filename));
            System.out.println("image download: " + filePath + filename);
        } catch (Exception e) {
            System.out.println("Error: image download failed with url=" + imgUrl);
        }
    }

}
