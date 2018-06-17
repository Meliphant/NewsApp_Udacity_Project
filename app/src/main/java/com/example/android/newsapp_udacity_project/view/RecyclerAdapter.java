package com.example.android.newsapp_udacity_project.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.newsapp_udacity_project.R;
import com.example.android.newsapp_udacity_project.model.News;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.NewsHolder> {

    private Context context;
    private List<News> news;

    public RecyclerAdapter(Context context, List<News> news) {
        this.context = context;
        this.news = news;
    }

    @Override
    public NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new NewsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsHolder holder, int position) {

        News currentNews = news.get(position);
        holder.titleTextView.setText(currentNews.getTitle());
        holder.categoryNameTextView.setText(currentNews.getCategory());
        holder.authorTextView.setText(currentNews.getAuthor());
        holder.dateTextView.setText(currentNews.getDate());

        holder.cardView.setOnClickListener(v -> {
            Uri newsUri = Uri.parse(currentNews.getUrl());
            Intent newsIntent = new Intent(Intent.ACTION_VIEW, newsUri);
            context.startActivity(newsIntent);
        });
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public class NewsHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView categoryNameTextView;
        public TextView authorTextView;
        public TextView dateTextView;
        public CardView cardView;

        public NewsHolder(View v) {
            super(v);
            cardView = v.findViewById(R.id.card_view);
            titleTextView = v.findViewById(R.id.title);
            categoryNameTextView = v.findViewById(R.id.category);
            dateTextView = v.findViewById(R.id.date);
            authorTextView = v.findViewById(R.id.author);
        }
    }
}
