package com.example.beket.newsapp;

import com.example.beket.newsapp.News.News;

public interface CustomClickListener {
    void onItemClick(News news);

    void onSectionClick(News news);
}
