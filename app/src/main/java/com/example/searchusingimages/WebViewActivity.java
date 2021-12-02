package com.example.searchusingimages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class WebViewActivity extends AppCompatActivity {
    WebView webView;
     MyWebViewClient MyWebViewClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
         webView =findViewById(R.id.webView);

         getSupportActionBar().hide();
         Intent intent = getIntent();

        // receive the value by getStringExtra() method
        // and key must be same which is send by first activity
        String str = intent.getStringExtra("urlLink");
        String googleSearch = intent.getStringExtra("googleLink");
       // Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        // display the string into textView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());

        if(str!=null)
        webView.loadUrl(str);
        else
        webView.loadUrl("https://www.google.com/search?q="+googleSearch);

    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // your code here
       if(webView.canGoBack()) {
           webView.goBack();
           return false;
       }
        }
        return super.onKeyDown(keyCode, event);
    }


    private class MyWebViewClient extends WebViewClient{

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            view.loadUrl(String.valueOf(request.getUrl()));
            return true;
        }
//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            super.onPageStarted(view, url, favicon);
//
//        }
//
//        @Override
//        public void onPageFinished(WebView view, String url) {
//            super.onPageFinished(view, url);
//
//
//        }


    }


}