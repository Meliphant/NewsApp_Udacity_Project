package com.example.android.newsapp_udacity_project.data;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.newsapp_udacity_project.model.News;

import java.util.List;

public class AsyncLoader extends AsyncTaskLoader<List<News>> {
    private static final String LOG_TAG = AsyncLoader.class.getSimpleName();
    private String url;

    public AsyncLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if (url == null) {
            return null;
        }
        List<News> news = Utils.fetchNewsData(url);
        return news;
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
    }
}
