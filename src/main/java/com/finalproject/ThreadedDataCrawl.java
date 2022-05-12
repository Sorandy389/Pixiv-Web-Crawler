package com.finalproject;

import java.util.Map;

public class ThreadedDataCrawl implements Runnable{
    private static Map<String, String> cookies;
    private Map<String, String> urlMap;
    private String filePath;
    ThreadedDataCrawl(Map<String, String> cookies, Map<String, String> urlMap, String filePath) {
        this.cookies = cookies;
        this.urlMap = urlMap;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        DataCrawl dataCrawl = new DataCrawl(cookies, filePath);
        for (Map.Entry<String, String> entry : urlMap.entrySet()) {
            dataCrawl.imgFetch(entry.getKey(), entry.getValue());
        }
    }
}

