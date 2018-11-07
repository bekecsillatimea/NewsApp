package com.example.beket.newsapp.Utils;

import android.util.Log;

import com.example.beket.newsapp.News.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    private QueryUtils() {
    }

    public static List<News> fetchNewsData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("", "Problem making the HTTP request.", e);
        }
        return extractFeatureFromJson(jsonResponse);
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;

        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("", "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        int readTimeout = 10000;
        int connectTimeout = 15000;
        int responseCode = 200;

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(readTimeout /* milliseconds */);
            urlConnection.setConnectTimeout(connectTimeout /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == responseCode) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("", "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("", "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static ArrayList<News> extractFeatureFromJson(String newsJson) {

        ArrayList<News> newsArrayList = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(newsJson);
            JSONObject responseJsonObject = baseJsonResponse.getJSONObject("response");
            JSONArray resultsArray = responseJsonObject.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject currentNews = resultsArray.getJSONObject(i);
                String date = "No publication date";
                String author = "";
                String imageURL = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";
                String title = currentNews.getString("webTitle");
                String section = currentNews.getString("sectionName");
                String sectionId = currentNews.getString("sectionId");
                if (currentNews.has("webPublicationDate")) {
                    date = currentNews.getString("webPublicationDate");
                }
                JSONArray tags = currentNews.getJSONArray("tags");
                if (tags.length() > 0) {
                    StringBuilder authorString = new StringBuilder();
                    for (int j = 0; j < tags.length(); j++) {
                        JSONObject currentReference = tags.getJSONObject(j);
                        if (currentReference.has("webTitle")) {
                            if (authorString.length() != 0) {
                                authorString.append("/");
                            }
                            authorString.append(currentReference.getString("webTitle"));
                        }
                    }
                    author = authorString.toString();
                }
                String webURL = currentNews.getString("webUrl");

                if (currentNews.has("fields")) {
                    JSONObject fieldsObject = currentNews.getJSONObject("fields");
                    imageURL = fieldsObject.getString("thumbnail");
                }
                News news = new News(title, section, sectionId, date, webURL, imageURL, author);
                newsArrayList.add(news);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }
        return newsArrayList;
    }
}
