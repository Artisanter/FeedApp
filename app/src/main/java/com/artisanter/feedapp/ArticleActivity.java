package com.artisanter.feedapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ArticleActivity extends NetworkActivity {
    private Article article;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        article = (Article) Objects.requireNonNull(getIntent().getExtras()).getSerializable("Article");
        setContentView(R.layout.activity_article);


        ((TextView)findViewById(R.id.title)).setText(article.getTitle());
        ((WebView)findViewById(R.id.description))
                .loadData(article.getDescription(), "text/html", null);
        ((TextView)findViewById(R.id.date)).setText(article.getDate());
        final ImageView image = findViewById(R.id.image);
        final View view = findViewById(R.id.article);
        final Activity thisOne = this;
        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Picasso.with(thisOne).load(article.getImageURL())
                                .placeholder(R.drawable.ic_placeholder)
                                .resize(view.getWidth(), 0)
                                .into(image);
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
        });
        if(!article.getImageURL().isEmpty()){
            image.setVisibility(View.VISIBLE);
        }
        else
            image.setVisibility(View.GONE);
    }

    @Override
    void onGetFeed(Feed feed) {
    }
}
