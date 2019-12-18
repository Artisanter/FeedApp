package com.artisanter.feedapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.Objects;

public class WebActivity extends AppCompatActivity {

    private String link;
    private SwipeRefreshLayout refreshLayout;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        link = Objects.requireNonNull(getIntent().getExtras()).getString("Link");

        webView = findViewById(R.id.main_web_view);
        refreshLayout = findViewById(R.id.web_refresh_view);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                refreshLayout.setRefreshing(false);
            }
        });

        OnRefreshListener listener = new OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.loadUrl(link);
            }
        };
        refreshLayout.setOnRefreshListener(listener);
        listener.onRefresh();
    }
}
