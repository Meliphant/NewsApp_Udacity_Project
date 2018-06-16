package com.example.android.newsapp_udacity_project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ArrayAdapter extends android.widget.ArrayAdapter<News> {

    public ArrayAdapter(Context context, ArrayList<News> list) {
        super(context, 0, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        @SuppressLint("ViewHolder") View rootView = LayoutInflater.from(getContext()).inflate(R.layout.news_item, parent, false);

        News currentNews = getItem(position);

        assert currentNews != null;

        TextView titleTextView = rootView.findViewById(R.id.title);
        TextView categoryTextView = rootView.findViewById(R.id.category);
        TextView dateTextView = rootView.findViewById(R.id.date);
        TextView authorTextView = rootView.findViewById(R.id.author);

        String title = String.valueOf(currentNews.getTitle());
        String category = String.valueOf(currentNews.getCategory());
        String date = String.valueOf(currentNews.getDate());
        String author = String.valueOf(currentNews.getAuthor());

        titleTextView.setText(title);
        categoryTextView.setText(category);
        dateTextView.setText(date);
        authorTextView.setText(author);

        return rootView;
    }
}
