package com.example.android.newsapp_udacity_project.view;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.newsapp_udacity_project.data.AsyncLoader;
import com.example.android.newsapp_udacity_project.model.News;
import com.example.android.newsapp_udacity_project.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>, SwipeRefreshLayout.OnRefreshListener {

    private static final String REQUEST_URL = "http://content.guardianapis.com/search";
    private static final int NEWS_LOADER_ID = 1;
    private static final String PAGE_SIZE = "page-size";
    private static final String API_KEY = "api-key";
    private static final String KEY = "test";
    private static final String SHOW_FIELDS = "show-fields";
    private static final String THUMBNAIL_TRAIL_TEXT_BYLINE = "thumbnail,trailText,byline";
    private static final String NONE = "none";
    private static final String SECTION = "section";
    private static final String ORDER_BY = "order-by";
    private static final String NEWEST = "newest";
    private static final String RELEVANCE = "relevance";
    private static final String QUERY = "q";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ArrayAdapter adapter;
    private AsyncLoader loader;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView emptyStateTextView;
    private ListView newsListView;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private ArrayList<News> news;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        newsListView = findViewById(R.id.list_view);
        news = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);

        emptyStateTextView = findViewById(R.id.empty_view);

        mSwipeRefreshLayout = findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_dark));
        mSwipeRefreshLayout.setRefreshing(true);

        recyclerAdapter = new RecyclerAdapter(this, news);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
//        adapter = new ArrayAdapter(this, new ArrayList<News>());
//        newsListView.setAdapter(adapter);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
            emptyStateTextView.setVisibility(View.GONE);
        }

//        newsListView.setOnItemClickListener((parent, view, position, id) -> {
//            News currentNews = adapter.getItem(position);
//            Uri newsUri = Uri.parse(currentNews.getUrl());
//            Intent newsIntent = new Intent(Intent.ACTION_VIEW, newsUri);
//            startActivity(newsIntent);
//        });

        loader = (AsyncLoader) getLoaderManager().initLoader(NEWS_LOADER_ID, null, this);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        return new AsyncLoader(this, searchResult(null));
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        emptyStateTextView.setText(R.string.no_news_update);
        news.clear();
        mSwipeRefreshLayout.setRefreshing(true);
        if (data != null && !data.isEmpty()) {
            news.addAll(data);
            recyclerAdapter.notifyDataSetChanged();
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
        news.clear();
    }

    @Override
    public void onRefresh() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            news.clear();
            loader.setUrl(searchResult(null));
            loader.forceLoad();
            emptyStateTextView.setVisibility(View.GONE);
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            emptyStateTextView.setVisibility(View.VISIBLE);
            emptyStateTextView.setText(R.string.no_internet_connection);
            news.clear();
        }
    }

    private String searchResult(String query) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Uri baseUri = Uri.parse(REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        String pageSize = sharedPreferences.getString(getString(R.string.settings_page_size_key),
                getString(R.string.settings_page_size_default));
        if (TextUtils.isEmpty(pageSize)) {
            pageSize = "0";
        }
        uriBuilder.appendQueryParameter(PAGE_SIZE, pageSize);
        uriBuilder.appendQueryParameter(API_KEY, KEY);
        uriBuilder.appendQueryParameter(SHOW_FIELDS, THUMBNAIL_TRAIL_TEXT_BYLINE);
        String section = sharedPreferences.getString(getString(R.string.settings_category_to_show_key),
                getString(R.string.settings_category_to_show_default));
        if (!section.equals(NONE)) {
            uriBuilder.appendQueryParameter(SECTION, section);
        }

        if (query == null) {
            uriBuilder.appendQueryParameter(ORDER_BY, NEWEST);
            return uriBuilder.toString();
        }
        uriBuilder.appendQueryParameter(ORDER_BY, RELEVANCE);
        uriBuilder.appendQueryParameter(QUERY, query);

        return uriBuilder.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
