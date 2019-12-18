package com.artisanter.feedapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import java.util.ArrayList;

public class MainActivity extends NetworkActivity {
    ListView articlesView;
    TextView feedName;
    AutoCompleteTextView rssEdit;
    SwipeRefreshLayout refreshLayout;
    Feed feed;
    ChannelHistory history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        articlesView = findViewById(R.id.list_view);

        articlesView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                Article article = (Article) articlesView.getAdapter().getItem(position);
                intent.putExtra("Title", article.getTitle());
                intent.putExtra("Description", article.getDescription());
                startActivity(intent);
                return true;
            }
        });

        articlesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), WebActivity.class);
                Article article = (Article) articlesView.getAdapter().getItem(position);
                intent.putExtra("Link", article.getLink());
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
        refreshLayout.setRefreshing(true);

        history = new ChannelHistory(this);
        feedName = findViewById(R.id.rss_name);
        rssEdit = findViewById(R.id.rss_edit);
        rssEdit.setText(rssurl.get());
        rssEdit.setAdapter(new ArrayAdapter<>(this
                , R.layout.support_simple_spinner_dropdown_item
                , new ArrayList<>(history.get())));
        feedName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.INVISIBLE);
                rssEdit.requestFocus();

            }
        });
        rssEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    showTitle();
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
        history.put(rssEdit.getText().toString());
        rssEdit.setAdapter(new ArrayAdapter<>(this
                , R.layout.support_simple_spinner_dropdown_item
                , new ArrayList<>(history.get())));
        int width = articlesView.getWidth();
        articlesView.setAdapter(new ArticleAdapter(
                this, R.layout.article_layout, feed.getArticles(), width));
        refreshLayout.setRefreshing(false);
    }

    @Override
    void onError(Exception e){
        refreshLayout.setRefreshing(false);
        super.onError(e);
    }

    @Override
    protected void onResume() {
        super.onResume();
        rssEdit.clearFocus();
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

    void showTitle(){
        feedName.setVisibility(View.VISIBLE);
        rssEdit.clearFocus();
        if(!rssurl.get().equals(rssEdit.getText().toString())) {
            new DownloadTask(this, rssEdit.getText().toString()).execute();
            refreshLayout.setRefreshing(true);
        }
    }
}
