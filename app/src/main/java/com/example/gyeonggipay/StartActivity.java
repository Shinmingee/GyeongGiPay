package com.example.gyeonggipay;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/*시작화면
 로그인 화면이나 회원가입 화면으로 유도하는 화면
 >> 액티비티 생명주기에서 해야할 일?
 */

public class StartActivity extends AppCompatActivity {

    //로그인 화면. 회원가입 화면과 연결하는 버튼
    TextView joinBtn, loginBtn;

    int REQUEST_LOADING = 1000;

    //자동로그인이 여기서 구현되어야 함
    //생성되었을 때,
    //저장되어있는 값이 true라면 바로 화면 전환할 것.
    //자동로그인 체크박스 값 가져옴

    //Gson
    private Gson gson = new GsonBuilder().create();

    //로그인 페이지 임으로 저장소에서 데이터 불러와서 key값 구분이 필요


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);

        //로딩화면 나오고 그 화면에서 다시 화면 전환
        Intent loading = new Intent(this, LoadingPageActivity.class);
        startActivityForResult(loading, REQUEST_LOADING);

        Log.d("스타트 액티비티","여기들어와?");

        //xml textView와 연결
        joinBtn = findViewById(R.id.start_join_btn);
        loginBtn = findViewById(R.id.start_login_btn);


        //기존회원 클릭, 로그인 화면 나오기
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goLogin = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(goLogin);
                //로그인 화면 클릭하면 시작 페이지 사라짐
                //finish();
            }
        });


        //처음오셨으면 클릭, 회원가입 화면 나오기
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goJoin = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(goJoin);
                //회원가입 화면 클릭하면 시작 페이지 사라짐
                //finish();
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //여기 들어오나
        Log.d("스타트 액티비티","여기들어와?22");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOADING) {
            if (resultCode == RESULT_OK) {

                //세어드에 저장되어 있는 userInfo.xml 과 auto.xml 이용해서
                //체크박스 확인하고 넘김.

                //sharedPreferences : auto에 저장되어 있는 자동 로그인 할 key값
                String login_key;

                //SharedPreferences (셰어드 저장소) 불러오기
                SharedPreferences sp = getSharedPreferences("auto", MODE_PRIVATE);
                Log.d("스타트 페이지 여기 지나가나?", "!!");

                //auto.xml 파일이 있으면 체크할 것.
                if (sp != null) {
                    login_key = sp.getString("login_key", "");
                    Log.d("여기 auto.xml 값있어? ", login_key);

                    sp = getSharedPreferences("userInfo", MODE_PRIVATE);
                    String strUserInfoData = sp.getString(login_key, "");
                    Log.d("여기 userInfo 값으로, ", strUserInfoData);

                    //UserInfoData 클래스 선언(string으로 된 json->gson->javaObject)
                    UserInfoData userInfoData = gson.fromJson(strUserInfoData, UserInfoData.class);
                    if (userInfoData != null) {
                        Log.d("UserInfoData 데이터", strUserInfoData);
                        if (userInfoData.getAutoLoginCheckBox()) {
                            //체크박스가 true(check 됨) -> 바로 메인으로 감
                            Intent goMain = new Intent(StartActivity.this, MainActivity.class);
                            Toast.makeText(this, userInfoData.getNickname() + "님, 자동로그인 되었습니다.\n 환영합니다.", Toast.LENGTH_LONG).show();
                            goMain.putExtra("email",userInfoData.getEmail());
                            startActivity(goMain);
                            finish();
                        }
//
                    }//자동 로그인 됨

                }//auto.xml이 있다는 거 자체가 로그인을 성공, 그 로그인 할때 checkBox의 값이 true였다는 거 니까.


                //Toast.makeText(StartActivity.this, "Result: " + data.getStringExtra("result"), Toast.LENGTH_SHORT).show();
            } else {   // RESULT_CANCEL
                Toast.makeText(StartActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
//        } else if (requestCode == REQUEST_ANOTHER) {
//            ...
        }
    }


    //출처: http://zeany.net/54 [소소한 IT 이야기]

    @Override
    protected void onResume() {
        super.onResume();

    }

}

