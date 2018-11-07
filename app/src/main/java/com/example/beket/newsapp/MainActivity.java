package com.example.beket.newsapp;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.beket.newsapp.ExpandableList.ExpandableListAdapter;
import com.example.beket.newsapp.ExpandableList.ExpandableListData;
import com.example.beket.newsapp.News.News;
import com.example.beket.newsapp.News.NewsAdapter;
import com.example.beket.newsapp.News.NewsLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>,
        SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.recycle_view)
    RecyclerView recyclerView;
    @BindView(R.id.empty_state_text_view)
    TextView emptyTextView;
    @BindView(R.id.loading_spinner)
    ProgressBar progressBar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.navList)
    ExpandableListView mExpandableListView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.navigation_header)
    ImageView navHeader;

    private ArrayList<String> mExpandableListTitle;
    private Map<String, List<String>> mExpandableListData;
    private NewsAdapter mAdapter;
    private Bundle args;
    private String section = "";
    private int pageSize = 15;
    private int addToPageSize = 15;
    private int maxPageSize = 200;
    private static final String GUARDIAN_BASE_REQUEST_URL = "https://content.guardianapis.com/search?";
    private static final int NEWS_LOADER_ID = 1;
    private static final String HELP_URL = "https://www.theguardian.com/help";
    private static final String GUARDIAN_EMAiL = "guardian.letters@theguardian.com";
    private static final String GUARDIAN_IMAGE_URL = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            setTitle(R.string.app_name);
        }

        mExpandableListData = ExpandableListData.getData(this);
        mExpandableListTitle = new ArrayList<>(mExpandableListData.keySet());

        Glide.with(this).load(GUARDIAN_IMAGE_URL).into(navHeader);
        addDrawerItems();
        ExpandableListData.setSectionId(this);

        args = new Bundle();
        args.putInt("page-size", 15);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new NewsAdapter(this, new ArrayList<News>(), recyclerView);
        recyclerView.setAdapter(mAdapter);
        setListenersToAdapter();
        getInternetConnection();
    }

    private void setListenersToAdapter(){
        mAdapter.setLoadMoreNewsListener(new LoadMoreNewsListener() {
            @Override
            public void loadMore() {
                if (maxPageSize - pageSize > addToPageSize) pageSize += addToPageSize;
                getLoaderManager().restartLoader(NEWS_LOADER_ID, null, MainActivity.this);
            }
        });

        mAdapter.setCustomClickListener(new CustomClickListener() {
            @Override
            public void onItemClick(News news) {
                if (news != null) {
                    Uri newsUri = Uri.parse(news.getNewsWebURL());
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                    startActivity(websiteIntent);
                }
            }

            @Override
            public void onSectionClick(News news) {
                section = news.getNewsSectionId();
                setTitle(news.getNewsSection());
                recyclerView.scrollToPosition(0);
                getLoaderManager().restartLoader(NEWS_LOADER_ID, args, MainActivity.this);
            }
        });
    }

    private void getInternetConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                LoaderManager loaderManager = getLoaderManager();
                loaderManager.initLoader(NEWS_LOADER_ID, null, this);
                if (recyclerView.getAdapter() == null) {
                    emptyTextView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyTextView.setVisibility(View.GONE);
                }
            } else {
                progressBar.setVisibility(View.GONE);
                emptyTextView.setText(R.string.no_internet);
            }
        }
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void addDrawerItems() {
        ExpandableListAdapter mExpandableListAdapter = new ExpandableListAdapter(this, mExpandableListTitle, mExpandableListData);
        mExpandableListView.setAdapter(mExpandableListAdapter);

        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                List<String> sectionList = mExpandableListData.get(mExpandableListTitle.get(groupPosition));
                section = ExpandableListData.getSectionId(sectionList.get(childPosition));
                setTitle(sectionList.get(childPosition));
                recyclerView.scrollToPosition(0);
                getLoaderManager().restartLoader(NEWS_LOADER_ID, args, MainActivity.this);

                if (recyclerView.getAdapter() == null) {
                    emptyTextView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_help:
                Intent helpIntent = new Intent(Intent.ACTION_VIEW);
                helpIntent.setData(Uri.parse(HELP_URL));
                if (helpIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(helpIntent);
                }
                return true;
            case R.id.action_contact_us:
                Intent contactUsIntent = new Intent(Intent.ACTION_SENDTO);
                contactUsIntent.setData(Uri.parse("mailto:" + GUARDIAN_EMAiL));
                if (contactUsIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(contactUsIntent);
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String searchFor = sharedPreferences.getString(getString(R.string.settings_search_for_key), "");
        String orderBy = sharedPreferences.getString(getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        if (args != null) pageSize = args.getInt("page-size");

        Uri baseUri = Uri.parse(GUARDIAN_BASE_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("page-size", String.valueOf(pageSize));
        uriBuilder.appendQueryParameter("api-key", BuildConfig.MY_GUARDIAN_API_KEY);
        if (searchFor.length() > 0) {
            uriBuilder.appendQueryParameter("q", searchFor);
        }
        if (section.length() > 0) {
            uriBuilder.appendQueryParameter("section", section);
        }
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("show-fields", "thumbnail");
        uriBuilder.appendQueryParameter("show-tags", "contributor");

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        mAdapter.clear();
        progressBar.setVisibility(View.GONE);
        emptyTextView.setText(R.string.no_news);
        if (data != null && !data.isEmpty()) {
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
            mAdapter.addAll(data);
            mAdapter.notifyDataSetChanged();
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
    }

    @Override
    public void onRefresh() {
        getLoaderManager().restartLoader(NEWS_LOADER_ID, null, MainActivity.this);
    }
}
