package com.example.admin.androidsqlite.view;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.admin.androidsqlite.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startLoading();
        LottieAnimationView animationView = findViewById(R.id.animationView);
        setUpAnimation(animationView);
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3000);
    }

    private void setUpAnimation(LottieAnimationView animationView) {
        // 재생할 애니메이션 넣어준다.
        animationView.setAnimation("open.json");
        // 시작
        animationView.playAnimation();
    }
}