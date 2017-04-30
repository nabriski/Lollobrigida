package com.github.nabriski.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView wv = (WebView)findViewById(R.id.webview);
        wv.getSettings().setJavaScriptEnabled(true);

        RandomQuoteBridge bridge = new RandomQuoteBridge(wv,"quotes");
        String html = getString(R.string.html);
        wv.loadData(html,"text/html",null);

    }
}
