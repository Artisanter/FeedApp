package com.artisanter.feedapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class MainActivity extends NetworkActivity {
    ListView articlesView;
    TextView feedName;
    EditText rssEdit;
    SwipeRefreshLayout refreshLayout;
    Feed feed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setPicasso();
        articlesView = findViewById(R.id.list_view);
        articlesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                Article article = (Article) articlesView.getAdapter().getItem(position);
                intent.putExtra("Article", article);
                startActivity(intent);
            }
        });
        refreshLayout = findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFeed();
            }
        });
        feedName = findViewById(R.id.rss_name);
        rssEdit = findViewById(R.id.rss_edit);
        rssEdit.setText(rssurl.get());
        feedName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.INVISIBLE);
                rssEdit.requestFocus();
            }
        });
        final MainActivity thisOne = this;
        rssEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    feedName.setVisibility(View.VISIBLE);
                    if(!rssurl.get().equals(v.getText().toString())) {
                        new DownloadTask(thisOne, v.getText().toString()).execute();
                        refreshLayout.setRefreshing(true);
                    }
                    return true;
                }
                return false;
            }
        });
        loadFeed();
    }

    @Override
    void onGetFeed(Feed feed){
        this.feed = feed;
        feedName.setText(feed.getTitle());
        rssurl.set(rssEdit.getText().toString());
        int width = articlesView.getWidth();
        articlesView.setAdapter(new ArticleAdapter(
                this, R.layout.article_layout, feed.getArticles(), width));
        //recyclerView.setAdapter(new RecycleArticleAdapter(this, feed.getArticles()));
        refreshLayout.setRefreshing(false);
    }

    @Override
    void onError(Exception e){
        refreshLayout.setRefreshing(false);
        super.onError(e);
    }

    void loadFeed(){
        if(isOnline()){
            new DownloadTask(this, rssEdit.getText().toString()).execute();
        }
        else if(feed == null){
            new LoadTask(this).execute();
            offlineToast.show();
        }
    }

    public void resizeWebView(View view) {
        int height = 200;
        WebView wv = (WebView)view.getTag();
        if(wv.getHeight() < height)
            return;
        if(wv.getHeight() == height) {
            wv.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            ((ImageButton)view).setImageResource(R.drawable.ic_up);
        }
        else {
            wv.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, height));
            ((ImageButton)view).setImageResource(R.drawable.ic_down);
        }
    }


    void setPicasso(){
        Picasso picasso = new Picasso.Builder(this)
                .memoryCache(new CustomCache(1024 * 1024 * 100))
                .build();
        picasso.setIndicatorsEnabled(true);
        Picasso.setSingletonInstance(picasso);
    }
}
