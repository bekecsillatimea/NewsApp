package com.example.beket.newsapp.News;

public class News {

    private String mTitle;
    private String mSection;
    private String mSectionId;
    private String mDate;
    private String mImageURL;
    private String mWebURL;
    private String mAuthor;

    public News(String title, String section, String sectionId, String date, String webURL, String imageURL, String author) {
        mTitle = title;
        mSection = section;
        mSectionId = sectionId;
        mDate = date;
        mImageURL = imageURL;
        mWebURL = webURL;
        mAuthor = author;
    }

    public String getNewsTitle() {
        return mTitle;
    }

    public String getNewsSection() {
        return mSection;
    }

    public String getNewsSectionId() {return mSectionId; }

    public String getNewsDate() {
        return mDate;
    }

    public String getNewsImageURL() {
        return mImageURL;
    }

    public String getNewsWebURL() {
        return mWebURL;
    }

    public String getNewsAuthor() { return mAuthor; }
}
