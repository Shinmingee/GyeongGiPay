package com.example.gyeonggipay;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoadingPageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_loading_page);

        startLoading();
    }

    private void startLoading(){
        //
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //현재 엑티비티 없애기.
                setResult(RESULT_OK);
                finish();
            }
        },3000); //3초 뒤에
    }


}
