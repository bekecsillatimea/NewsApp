package com.example.beket.newsapp.News;

import java.util.List;
import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.beket.newsapp.Utils.QueryUtils;


public class NewsLoader extends AsyncTaskLoader<List<News>> {

    private String mUrl;

    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if (mUrl == null){
            return null;
        }

        return QueryUtils.fetchNewsData(mUrl);
    }
}
