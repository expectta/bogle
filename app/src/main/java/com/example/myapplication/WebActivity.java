package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.airbnb.lottie.L;
import com.airbnb.lottie.LottieAnimationView;

import java.net.URL;

public class WebActivity extends AppCompatActivity {
    WebView webView;
    Thread thread;
    Handler handler;
    Message msg;
    LottieAnimationView lottieAnimationView;
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        lottieAnimationView= findViewById(R.id.webLottie);
        webView = findViewById(R.id.webView);
        //웹뷰에서 자바 스크립트가 사용 가능하도록한다.
        //자바스크립트 허용
        webView.getSettings().setJavaScriptEnabled(true);
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        lottieAnimationView.setVisibility(View.GONE);
            //라디오버튼이 강사일경우 레이아웃 보이기

        //인텐트로 보여줄 홈페이지의 URL을 받아온다
        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            //루퍼에의해 실행되는 함수.
            //송신 쓰레드에서 전달받은 파라메터를 통해 코드가 진행됨.
            public void handleMessage(@NonNull Message msg) {
                        lottieAnimationView.setVisibility(View.GONE);
                thread.interrupt();
            }
        };
        Intent intent = getIntent();
        String webViewUrl = intent.getStringExtra("url");
        if(webView!=null){
            webView.loadUrl(webViewUrl);
            webView.setWebChromeClient(new WebChromeClient());
            webView.setWebViewClient(new WebViewClientClass());
        }
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        };
        thread.start();


    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("check URL",url);
            view.loadUrl(url);
            return true;
        }
    }
}
