package com.ljy.lambdademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      WebView wv = (WebView) findViewById(R.id.wv);
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient());
        wv.loadUrl("http://blog.163.com/hero_213/blog/static/3989121420115393913734/");
    }

}
