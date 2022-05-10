package com.finalproject;

import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class ThreadedContentCrawl {

    public static void imgDownload(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        Elements imgElements = document.select("div.ranking-image-item").select("a");
        for (Element imgElement : imgElements) {
            String imgLink = imgElement.absUrl("href");
            if (imgLink.startsWith("//")) {
                imgLink = "http:" + imgLink;
            }
            Connection.Response response = Jsoup.connect(imgLink)
//                    .proxy("127.0.0.1", 8080) // sets a HTTP proxy
//                    .userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2") //
                    .ignoreContentType(true)
                    .execute();
            String imgName = imgElement.attr("alt");
            ByteArrayInputStream stream = new ByteArrayInputStream((response.bodyAsBytes()));
            FileUtils.copyInputStreamToFile(stream, new File("D://webCrawler/"+i+imgName+".png"));
            System.out.println("image download: D://webCrawler/"+i+imgName+".png");
        }
    }

}

