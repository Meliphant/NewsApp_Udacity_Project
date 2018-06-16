package com.example.android.newsapp_udacity_project;

import android.text.TextUtils;
import android.util.Log;
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

public class Utils {

    private static final String LOG_TAG = Utils.class.getSimpleName();
    private static final String RESPONSE = "response";
    private static final String RESULTS = "results";
    private static final String TITLE = "webTitle";
    private static final String CATEGORY = "sectionName";
    private static final String DATE = "webPublicationDate";
    private static final String URL = "webUrl";
    private static final String FIELDS = "fields";
    private static final String AUTHOR = "byline";

    private Utils() {
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results", e);
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

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    public static List<News> fetchNewsData(String requestUrl) {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request", e);
        }

        List<News> news = extractFeatureFromJson(jsonResponse);
        return news;
    }

    private static List<News> extractFeatureFromJson(String newsJSON) {
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        List<News> newsArray = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject response = baseJsonResponse.getJSONObject(RESPONSE);
            JSONArray results = response.getJSONArray(RESULTS);

            for (int i = 0; i < response.length(); i++) {

                JSONObject currentNewsObj = results.getJSONObject(i);
                String newsTitle = currentNewsObj.optString(TITLE);
                String newsCategory = currentNewsObj.optString(CATEGORY);
                String newsDate = currentNewsObj.optString(DATE);
                String newsURL = currentNewsObj.getString(URL);
                JSONObject fields = currentNewsObj.getJSONObject(FIELDS);
                String newsAuthor = fields.getString(AUTHOR);

                String newsDateFormated;

                if (newsDate == null) {
                    newsDateFormated = " ";
                } else {
                    newsDateFormated = newsDate.substring(11, 16) + "    " + newsDate.substring(0, 10);
                    newsDateFormated = newsDateFormated.replaceAll("-", ".");
                }

                News news = new News(newsTitle, newsDateFormated, newsCategory, newsURL, newsAuthor);
                newsArray.add(news);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the news JSON results", e);
        }
        return newsArray;
    }
}
