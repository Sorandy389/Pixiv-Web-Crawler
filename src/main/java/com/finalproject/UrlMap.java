package com.finalproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlMap {

    public static void main(String[] args) {
        // the main page of target website
        getUrl("http://www.nipic.com");
    }

    // Get the URL
    public static Map<String, Boolean> getUrl(String link) {
        Map<String, Boolean> urlMap = new LinkedHashMap<String, Boolean>();
        String parentLinkPrefix = "";
        Pattern p = Pattern.compile("(https?://)?[^/\\s]");
        Matcher m = p.matcher(link);

        if (m.find()) {
            parentLinkPrefix = m.group();
        }
        urlMap.put(link, false);
        urlMap = updateUrlMap("http://www.nipic.com", urlMap);

        return urlMap;
    }

    private static Map<String, Boolean> updateUrlMap(String parentLinkPrefix, Map<String, Boolean> urlMap) {
        Map<String, Boolean> childUrlMap = new LinkedHashMap<String, Boolean>();

        for (Map.Entry<String, Boolean> entry : urlMap.entrySet()) {
            if (entry.getValue()) {
                continue;
            }
            String parentLink = entry.getKey();

            try {
                URL url = new URL(parentLink);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                if (connection.getResponseCode() == -1) {
                    System.out.println("Error: No response code");
                } else if (connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP
                        || connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM) {
                    url = new URL(connection.getHeaderField("Location"));
                    // open the new connnection again
                    connection = (HttpURLConnection) url.openConnection();
                }
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    Pattern p = Pattern.compile("<a.*?href=[\"']?((https?://)?/?[^\"']+)[\"']?.*?>(.+)</a>");
                    Matcher matcher = null;
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        matcher = p.matcher(line);
                        if (matcher.find()) {
                            String childLink = matcher.group(1).trim();
                            if (childLink.endsWith(";")) {
                                continue;
                            }
                            if (!childLink.startsWith("http")) {
                                if (childLink.startsWith("//")) {
                                    childLink = childLink.substring(2, childLink.length() - 2);
                                } else if (childLink.startsWith("/")) {
                                    childLink = parentLinkPrefix + childLink;
                                } else {
                                    childLink = parentLinkPrefix + "/" + childLink;
                                }
                            }
                            if (childLink.endsWith("/")) {
                                childLink = childLink.substring(0, childLink.length() - 1);
                            }
                            if (!urlMap.containsKey(childLink)
                                    && !childUrlMap.containsKey(childLink)
                                    && childLink.startsWith(parentLinkPrefix)) {
                                childUrlMap.put(childLink, false);
                                System.out.println("url: " + childLink);

                            }
                        }
                    }
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            urlMap.replace(parentLink, false, true);
        }
        if (!childUrlMap.isEmpty()) {
            urlMap.putAll(childUrlMap);
            // for test
            if (urlMap.size() > 500) {
                return urlMap;
            }
            urlMap.putAll(updateUrlMap(parentLinkPrefix, urlMap));
        }
        return urlMap;
    }
}
