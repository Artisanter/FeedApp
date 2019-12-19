package com.artisanter.feedapp;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import java.util.Objects;

public class ArticleActivity extends NetworkActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = Objects.requireNonNull(getIntent().getExtras());
        setContentView(R.layout.activity_article);

        ((TextView)findViewById(R.id.title)).setText(extras.getString("Title"));
        ((WebView)findViewById(R.id.description))
                .loadDataWithBaseURL(null,
                        "<style>img{display: inline;height: auto;max-width: 100%;}</style>"
                                + extras.getString("Description")
                        , "text/html"
                        , "UTF-8"
                        , null);
    }
}
