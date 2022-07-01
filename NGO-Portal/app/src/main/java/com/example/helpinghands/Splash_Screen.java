package com.example.helpinghands;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.Instant;

public class Splash_Screen extends AppCompatActivity {
private FirebaseAuth mFirebaseAuth;

    private static int timer = 2000;
    ImageView app_logo;
    Animation app_logo_animation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        app_logo=findViewById(R.id.app_logo);
        app_logo_animation= AnimationUtils.loadAnimation(this,R.anim.app_logo_animation);

        app_logo.setAnimation(app_logo_animation);

        mFirebaseAuth= FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser mFirebaseUser= mFirebaseAuth.getCurrentUser();

        if(mFirebaseUser!=null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                Intent intent=new Intent(Splash_Screen.this,final_page.class);
                startActivity(intent);
                finish();
                }
            },timer);

        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                Intent intent=new Intent(Splash_Screen.this,sign_in_page.class);
                startActivity(intent);
                finish();
                }
            },timer);

        }

    }
}