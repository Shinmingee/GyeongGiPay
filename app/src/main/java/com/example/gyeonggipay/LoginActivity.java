package com.example.gyeonggipay;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//로그인 화면 : 회원가입 후 띄워지는 화면/기존에 가입되어 있는 회원이 접근할 때 띄워지는 화면
//>>회원정보가 불려지는 화면.
//자동로그인도 진행(자동로그인 버튼 true 값이면 바로 메인으로 화면 전환!
// 입력한 이메일과 저장되어있는 userInfo 저장소의 key값들 중 일치하는 게 있다면 진행
public class LoginActivity extends AppCompatActivity {

    //xml요소
    EditText login_email_et, login_pw_et;
    Button login_btn;
    CheckBox login_auto_login_checkBox;
    TextView login_pg_joinBtn, login_pg_findpwBtn;

    //넘어올 인텐트
    Intent userinfo = new Intent();

    //class 변수_셰어드에 저장되있는 데이터를 담는 변수
    String login_email, login_pw;
    boolean login_auto_check;

    //class변수_이메일 editText에 적힌 문자열 저장(shared에 저장되어 있는 유저 정보의 key)
    String check_email;

    //Gson
    private Gson gson = new GsonBuilder().create();

    //로그인 페이지 임으로 저장소에서 데이터 불러와서 key값 구분이 필요
    //먼저 저장소를 필드에 세팅해두자.
    //SharedPreferences (셰어드 저장소) 불러오기
    private SharedPreferences sp;
    //key에 맞는 데이터를 문자열로 변환
    String strUserInfoData;
    //UserInfoData 클래스 선언(string으로 된 json->gson->javaObject)
    UserInfoData userInfoData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        //화면과 연결

        //로그인 이메일/pw
        login_email_et = findViewById(R.id.login_email_et);
        login_pw_et = findViewById(R.id.login_pw_et);
        //로그인하기 버튼
        login_btn = findViewById(R.id.login_btn);
        //자동로그인 체크박스
        login_auto_login_checkBox = findViewById(R.id.login_auto_login_checkBox);
        //회원가입하기/비밀번호찾기 텍스트 버튼
        login_pg_joinBtn = findViewById(R.id.login_pg_joinBtn);
        login_pg_findpwBtn = findViewById(R.id.login_pg_findpwBtn);

        //회원가입에서 넘어온 인텐트
        userinfo = getIntent();
        String email = userinfo.getStringExtra("email");
//        final String pw = userinfo.getStringExtra("pw");
//
//        final String region = userinfo.getStringExtra("region");
//        final String nickname = userinfo.getStringExtra("nickname");


        //로그인 버튼을 눌렀을 때
        //자동로그인 체크박스 확인 필요
        //넘어온 인텐트에서 이메일, pw가 입력한 값과 같으면 로그인 성공, 아니면 실패
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){

                //입력한 값
                check_email = login_email_et.getText().toString();
                String tmpPw = login_pw_et.getText().toString();

                //저장되어 있는 정보불러오기(json->gson->javaObject) /인텐트 대신에~
                //callUserInfoData(check_email);
                sp = getSharedPreferences("userInfo",MODE_PRIVATE);

                Log.d("callUserInfoData",":"+sp.contains(check_email));

                if(sp.contains(check_email)){

                    Log.d("callUserInfoData",":"+sp.contains(check_email));

                    strUserInfoData = sp.getString(check_email, "");
                    userInfoData = gson.fromJson(strUserInfoData, UserInfoData.class);

                    Log.d("로그인 페이지","해당 키 값: "+check_email);
                    Log.d("로그인 페이지","적은 이메일 값: "+login_email);

                    login_email = userInfoData.getEmail();
                    Log.d("로그인 데이터 불러오는 함수안 email:",login_email);
                    login_pw = userInfoData.getPw();
                    Log.d("로그인 데이터 불러오는 함수안 pw:",login_pw);
                    login_auto_check = userInfoData.getAutoLoginCheckBox();
                    Log.d("로그인 데이터 불러오는 함수안", "auto:"+login_auto_check);

                    Log.d("저장되어있는 정보 불러졌어?",":" + login_email);
                    Log.d("저장되어있는 정보 불려졌어?",":"+ login_pw);
                    Log.d("저장되어있는 정보 불려졌어?",":"+login_auto_check);
                    Log.d("저장되어 있는 변수 >",":"+strUserInfoData);
                    Log.d("저장되어 있는 객체,닉넴>",":"+userInfoData.getNickname());


                    //입력한 메일, 저장된 메일이 같고 입력한 비번, 저장된 비번이 같으면
                    // checkBox 저장하고 로그인 진행
                    if(check_email.equals(login_email) && tmpPw.equals(login_pw)){

                        //자동로그인 체크박스 값 가져옴
                        login_auto_check = login_auto_login_checkBox.isChecked();

                        //만들어진 유저정보 객체에 체크 최신값 셋 해줌
                        userInfoData.setAutoLoginCheckBox(login_auto_check);

                        Log.d("로그인 버튼->세어드 데이터 key값은?",check_email);
                        Log.d("로그인 버튼->저장되어있는 이멜로 가는가?",userInfoData.getEmail());
                        //유저의 정보데이터 객체를 gson을 통해 String으로 만듬
                        strUserInfoData = gson.toJson(userInfoData,UserInfoData.class);
                        Log.d("이멜,비번이 같을 때 저장할 데이터",">"+strUserInfoData);

                        //셰어드 저장소 수정함
                        SharedPreferences.Editor editor = sp.edit();
                        //JSON으로 변환한 객체의 데이터를 저장한다.
                        //여기서 !! json의 String으로 변한 데이터 중 체크박스 데이터만 바껴야 하잖아?
                        //이렇게하면 새롭게 저장 될 거 같은데..? => ㅇㅇ 맞음.

                        Log.d("로그인 버튼/셰어드에 저장된 데이터 key" ," "+login_email);
                        editor.putString(login_email,strUserInfoData);
                        //완료한다.
                        editor.apply();

                        //변경된 체크박스의 최신 값이 true 일 때 저장소에 auto라는 xml파일 만들어서
                        //이 기기에서 자동 로그인 할 수 있는 최신계정 구분을 해주자
                        if(login_auto_check){
                            SharedPreferences auto = getSharedPreferences("auto", MODE_PRIVATE);
                            SharedPreferences.Editor editAuto = auto.edit();
                            editAuto.putString("login_key",login_email);
                            editAuto.apply();
                        }


                        Intent goMain = new Intent(getApplicationContext(), MainActivity.class);
                        //셰어드에 현재 로그인 해있는 이멜 저장. _ 로그아웃하거나 탈퇴시 삭제
                        SharedPreferences sharedPreferences = getSharedPreferences("login_id",MODE_PRIVATE);
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putString("login_email",login_email);
                        edit.apply();

                        goMain.putExtra("email", login_email);
//                    goMain.putExtra("pw", login_pw);
//                    goMain.putExtra("nickname",nickname);
//                    goMain.putExtra("region", region);
//                    goMain.putExtra("profile", userinfo.getParcelableExtra("profile"));

                        startActivity(goMain);
                        finish();

                    }else{//이메일이나 비번 틀렀을 때
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("[알림] 회원정보 불일치")
                                .setMessage("입력하신 회원정보가 일치하지 않습니다.\n다시 입력해주세요.");
                        builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                login_email_et.setText("");
                                login_pw_et.setText("");

                                //포커스 메일에 놓기
                                login_email_et.requestFocus();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }


                }else{
                    Log.d("callUserInfoData ElSE",":"+sp.contains(check_email));

                    //입력한 이멜이 셰어드에 저장되어 있는 데이터의 키값으로 없을 때.
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("[알림] 회원정보 불일치")
                            .setMessage("입력하신 회원정보가 존재하지 않습니다.\n다시 입력해주세요.");
                    builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            login_email_et.setText("");
                            login_pw_et.setText("");

                            //포커스 메일에 놓기
                            login_email_et.requestFocus();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }
            }
        });

        //신규회원이라면 회원가입 페이지로
        login_pg_joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goJoin = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(goJoin);
                //회원가입으로 클릭하면 이 화면은 사라짐
                finish();
            }
        });

    }


    //회원정보 중 이메일과 pw 불러오기(callUserInfo)
//    protected void callUserInfoData(String check_email){
//        sp = getSharedPreferences("userInfo",MODE_PRIVATE);
//
//        Log.d("callUserInfoData",":"+sp.contains(check_email));
//
//        if(sp.contains(check_email)){
//
//            Log.d("callUserInfoData",":"+sp.contains(check_email));
//
//            strUserInfoData = sp.getString(check_email, "");
//            userInfoData = gson.fromJson(strUserInfoData, UserInfoData.class);
//
//            Log.d("로그인 페이지","해당 키 값: "+check_email);
//            Log.d("로그인 페이지","적은 이메일 값: "+login_email);
//
//            login_email = userInfoData.getEmail();
//            Log.d("로그인 데이터 불러오는 함수안 email:",login_email);
//            login_pw = userInfoData.getPw();
//            Log.d("로그인 데이터 불러오는 함수안 pw:",login_pw);
//            login_auto_check = userInfoData.getAutoLoginCheckBox();
//            Log.d("로그인 데이터 불러오는 함수안", "auto:"+login_auto_check);
//
//        }else{
//            Log.d("callUserInfoData ElSE",":"+sp.contains(check_email));
//
//            //입력한 이멜이 셰어드에 저장되어 있는 데이터의 키값으로 없을 때.
//            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//            builder.setTitle("회원정보 불일치")
//                    .setMessage("입력하신 회원정보가 일치하지 않습니다.\n다시 입력해주세요.");
//            builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    login_email_et.setText("");
//                    login_pw_et.setText("");
//
//                    //포커스 메일에 놓기
//                    login_email_et.requestFocus();
//                }
//            });
//            AlertDialog alertDialog = builder.create();
//            alertDialog.show();
//
//        }
//
//        //class 변수
//        //String login_email, login_pw;
//
//    }




    @Override
    protected void onPause() {
        super.onPause();
        //화면 전환-> 뒤에 있는 상태
        //Toast.makeText(getApplicationContext(),"로그인 액티비티 LoginActivity onPause()상태", Toast.LENGTH_LONG).show();


    }
}
