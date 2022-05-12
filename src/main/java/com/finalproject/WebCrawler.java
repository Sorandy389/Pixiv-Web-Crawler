package com.finalproject;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebCrawler {

    /* Web Crawler Configuration */
    // base url
    private static String baseUrl = "https://www.pixiv.net/ranking.php?mode=male";
    // desired number of rank pages
    private static int pageDownload = 1;
    // cookies are required to crawl pixiv.net
    private static Map<String, String> cookies = new LinkedHashMap<>();
    // two file path for threaded method and serial method
    private static String filePath = "D://webCrawler/imgDownload/";
    private static String filePath_threaded = "D://webCrawler/threadedImgDownload/";
    // Since my computer is 8-core and 16-thread, I think 16 threads would be a reasonable choice
    private static int threadCount = 16;

    public static void main(String[] args) {
        // copy the cookies from the browser with pixiv account log in
        // here is my cookie for test usage
        cookies.put("PHPSESSID", "73818768_O4yqNjMhFIGKqLyMa7UKInowL7iClaRG");

        long startTime, endTime, urlMapTime, notThreadedTime, threadedTime;

        // initialize the url pool
        startTime = System.currentTimeMillis();
        UrlPool urlMap = new UrlPool(cookies, threadCount);
        urlMap.getUrlMap(baseUrl, pageDownload);
        Map<String, String> urlMapContent = urlMap.urlMap;
        List<Map<String, String>> urlThreadMapList = urlMap.urlThreadMapList;
        endTime = System.currentTimeMillis();
        urlMapTime = endTime - startTime;

        // fetch images in serial
        startTime = System.currentTimeMillis();
        DataCrawl contentCrawl = new DataCrawl(cookies, filePath);
        for (Map.Entry<String, String> entry : urlMapContent.entrySet()) {
            contentCrawl.imgFetch(entry.getKey(), entry.getValue());
        }
        endTime = System.currentTimeMillis();
        notThreadedTime = endTime - startTime;

        // fetch images in threads
        startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i=0; i<threadCount; i++) {
            ThreadedDataCrawl thread = new ThreadedDataCrawl(cookies, urlThreadMapList.get(i), filePath_threaded);
            executor.execute(thread);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {}
        endTime = System.currentTimeMillis();
        threadedTime = endTime - startTime;

        // compare the time interval result
        System.out.println("Result in millisecond");
        System.out.println("Initialize UrlPool time: \t" + urlMapTime);
        System.out.println("Crawler in Serial time: \t" + notThreadedTime);
        System.out.println("Crawler in Thread Time: \t" + threadedTime);

    }
}
