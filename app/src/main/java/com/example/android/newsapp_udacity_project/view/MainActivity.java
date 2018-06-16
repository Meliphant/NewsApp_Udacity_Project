package com.example.android.newsapp_udacity_project.view;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.newsapp_udacity_project.data.AsyncLoader;
import com.example.android.newsapp_udacity_project.model.News;
import com.example.android.newsapp_udacity_project.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>, SwipeRefreshLayout.OnRefreshListener {

    private static final String REQUEST_URL = "http://content.guardianapis.com/search?order-by=newest&page-size=20&q=&api-key=test&show-fields=thumbnail,trailText,byline";
    private static final int NEWS_LOADER_ID = 1;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ArrayAdapter adapter;
    private AsyncLoader loader;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView emptyStateTextView;
    private ListView newsListView;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newsListView = findViewById(R.id.list_view);
        emptyStateTextView = findViewById(R.id.empty_view);
        ;

        mSwipeRefreshLayout = findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_dark));
        mSwipeRefreshLayout.setRefreshing(true);

        adapter = new ArrayAdapter(this, new ArrayList<News>());
        newsListView.setAdapter(adapter);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
            emptyStateTextView.setVisibility(View.GONE);
        }

        newsListView.setOnItemClickListener((parent, view, position, id) -> {
            News currentNews = adapter.getItem(position);
            Uri newsUri = Uri.parse(currentNews.getUrl());
            Intent newsIntent = new Intent(Intent.ACTION_VIEW, newsUri);
            startActivity(newsIntent);
        });

        loader = (AsyncLoader) getLoaderManager().initLoader(NEWS_LOADER_ID, null, this);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        return new AsyncLoader(this, REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        emptyStateTextView.setText(R.string.no_news_update);
        adapter.clear();
        mSwipeRefreshLayout.setRefreshing(true);
        if (data != null && !data.isEmpty()) {
            adapter.addAll(data);
            mSwipeRefreshLayout.setRefreshing(false);
            emptyStateTextView.setVisibility(View.GONE);
        } else {
            emptyStateTextView.setVisibility(View.VISIBLE);
            emptyStateTextView.setText(R.string.no_internet_connection);
            mSwipeRefreshLayout.setRefreshing(false);
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        adapter.clear();
    }

    @Override
    public void onRefresh() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            adapter.clear();
            loader.setUrl(REQUEST_URL);
            loader.forceLoad();
            emptyStateTextView.setVisibility(View.GONE);
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            emptyStateTextView.setVisibility(View.VISIBLE);
            emptyStateTextView.setText(R.string.no_internet_connection);
            adapter.clear();
        }
    }
}
