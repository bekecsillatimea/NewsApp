package com.example.beket.newsapp.News;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.beket.newsapp.CustomClickListener;
import com.example.beket.newsapp.LoadMoreNewsListener;
import com.example.beket.newsapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.Holder> {

    private ArrayList<News> mNews;
    private Context mContext;
    private CustomClickListener mCustomClickListener;
    private LoadMoreNewsListener mLoadMoreNewsListener;
    private boolean isLoading = false;


    public NewsAdapter(@NonNull Activity context, ArrayList<News> news, RecyclerView recyclerView) {
        mContext = context;
        mNews = news;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView,
                                       int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    int totalItemCount = linearLayoutManager.getItemCount();
                    int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if ((totalItemCount == lastVisibleItem + 1) && !isLoading) {
                        mLoadMoreNewsListener.loadMore();
                        isLoading = true;
                    } else if (totalItemCount != lastVisibleItem + 1) {
                        isLoading = false;
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new Holder(listItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") final int position) {
        News currentNews = mNews.get(position);
        holder.titleTextView.setText(currentNews.getNewsTitle());
        holder.sectionTextView.setText(currentNews.getNewsSection());
        if (!currentNews.getNewsDate().contains("No")) {
            holder.dateTextView.setText(dateSplit(currentNews.getNewsDate()));
        } else {
            holder.dateTextView.setText(currentNews.getNewsDate());
        }
        if (currentNews.getNewsAuthor().length() == 0) {
            holder.authorTextView.setVisibility(View.GONE);
        } else {
            holder.authorTextView.setText(currentNews.getNewsAuthor());
        }
        Glide.with(mContext).load(currentNews.getNewsImageURL()).into(holder.newsImageView);

        holder.bind(mNews.get(position), mCustomClickListener);
        holder.sectionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomClickListener.onSectionClick(mNews.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNews.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView titleTextView, dateTextView, sectionTextView, authorTextView;
        ImageView newsImageView;

        Holder(final View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            sectionTextView = itemView.findViewById(R.id.section_text_view);
            authorTextView = itemView.findViewById(R.id.author_text_view);
            newsImageView = itemView.findViewById(R.id.news_image_view);
        }

        void bind(final News news, final CustomClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(news);
                }
            });
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String dateSplit(String toSplit) {
        String[] parts = toSplit.split("T");
        String date = parts[0];
        String time = parts[1].substring(0, parts[1].length() - 1);
        String dateAndTime = date + " " + time;
        try {
            return new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss")
                    .format(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dateAndTime));
        } catch (ParseException e) {
            e.printStackTrace();
            return dateAndTime;
        }
    }

    public void setLoadMoreNewsListener(LoadMoreNewsListener loadMoreNewsListener) {
        mLoadMoreNewsListener = loadMoreNewsListener;
    }

    public void setCustomClickListener(CustomClickListener customClickListener){
        mCustomClickListener = customClickListener;
    }

    public void clear() {
        mNews.clear();
    }

    public void addAll(List<News> data) {
        mNews.addAll(data);
    }
}
