package com.example.gyeonggipay;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.regex.Pattern;

public class SettingPwActivity extends AppCompatActivity {


    private EditText mypage_setting_current_pw,
            mypage_setting_change_pw,mypage_setting_change_pw_check;

    private TextView textView4;
    private ImageView setting_pw_image;

    private Button setting_pw_btn;


    //----------저장에 필요한 변수선언-------------//
    //Gson
    private Gson gson = new GsonBuilder().create();

    //먼저 저장소를 필드에 세팅해두자.

    //SharedPreferences (셰어드 저장소) 불러오기
    private SharedPreferences sp;
    //key에 맞는 데이터를 문자열로 변환
    private String strUserInfoData;
    //UserInfoData 클래스 선언(string으로 된 json->gson->javaObject)
    private UserInfoData userInfoData;
    //key값인 email
    private String email;
    //shared에 저장된 비번
    private String pw;

    //입력된 값들의 변수
    private String current_pw, change_pw, change_pw_check;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_pw);

        //툴바
        //툴바 설정
        Toolbar toolbar = findViewById(R.id.setting_pw_toolbar);
        setSupportActionBar(toolbar);

        //뒤로 가기 버튼, 디폴트로 true만 해도 백버튼 생김
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //기본 타이틀 보여줄지 말지 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView textView = findViewById(R.id.setting_pw_toolbar_title);
        textView.setText("비밀번호 변경");

        //인텐트로 email값이 넘어와야 함
        Intent userEmail = getIntent();
        email = userEmail.getStringExtra("email");

        //셰어드에 저장되어 있는 회원정보
        callUserInfoData(email);
        pw = userInfoData.getPw();
        Log.d("메인페이지의 값 잘 들어오나 비번", pw);


        //비밀번호 변경
        //현재비번
        mypage_setting_current_pw = findViewById(R.id.mypage_setting_current_pw);
        //바꿀비번
        mypage_setting_change_pw = findViewById(R.id.mypage_setting_change_pw);
        //바꿀비번 확인
        mypage_setting_change_pw_check = findViewById(R.id.mypage_setting_change_pw_check);
        textView4 = findViewById(R.id.textView4);
        //비밀번호 확인 체크 이미지
        setting_pw_image = findViewById(R.id.setting_pw_image);

        //비밀번호 변경 버튼
        setting_pw_btn = findViewById(R.id.setting_pw_btn);

        //현재비밀번호 입력->바꿀 비밀번호


        //비밀번호 정규식 체크
        // 비밀번호 규칙 정규식
        // : 숫자, 특문 각 1회 이상, 영문은 2개 이상 사용하여 8자리 이상 입력
        //String regExpPw = " /(?=.*\d{1,12})(?=.*[~`!@#$%\^&*()-+=]{1,12})(?=.*[a-zA-Z]{1,12}).{8,12}$/ ";
        //^(?=.*\d)(?=.*[~`!@#$%\^&*()-])(?=.*[a-zA-Z]).{8,12}$
        mypage_setting_change_pw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //비밀번호 유효성
                //8-12자리, 영어/숫자/특수문자 포함
                if(Pattern.matches("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&]).{8,12}.$", mypage_setting_change_pw.getText().toString()))
                {//형식이 맞다면
                    mypage_setting_change_pw.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#44494F")));
                    textView4.setTextColor(ColorStateList.valueOf(Color.parseColor("#44494F")));

                }else{//형식이 아니라면
                    mypage_setting_change_pw.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFB71C1C")));
                    textView4.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFB71C1C")));
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //비밀번호 확인에 실시간 체크
        mypage_setting_change_pw_check.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mypage_setting_change_pw.getText().toString().equals(mypage_setting_change_pw_check.getText().toString())) {
                    setting_pw_image.setImageResource(R.drawable.join_pw_right);
                } else {
                    setting_pw_image.setImageResource(R.drawable.join_pw_not);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        //변경하기 버튼 눌렀을 때 비밀번호 수정 가능 한 조건
        //1. 입력받은 현재 비밀번호와 셰어드에 저장되어있는 비밀 번호가 맞아야 함
        //2. 변경할 비밀번호가 같아야 함

        setting_pw_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                current_pw = mypage_setting_current_pw.getText().toString();
                change_pw = mypage_setting_change_pw.getText().toString();
                change_pw_check = mypage_setting_change_pw_check.getText().toString();

                if(current_pw.equals(pw)){//1. 입력받은 현재 비밀번호와 셰어드에 저장되어있는 비밀 번호가 맞아야 함

                    if(Pattern.matches("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&]).{8,12}.$", change_pw)){
                        //2. 변경할 비밀번호가 정규식이 맞는지 확인

                        if(change_pw.equals(change_pw_check)){ //3. 변경할 비밀번호가 같아야 함
                            pw = change_pw;
                            Log.d("변경할 비번 >> ",pw);
                            callUserInfoData(email);
                            Log.d("비번 변경 중 이멜주소>> ",userInfoData.getEmail());
                            saveUserInfoData(pw);
                            Log.d("비번 변경 완료>> ",userInfoData.getPw());

                            AlertDialog.Builder builder = new AlertDialog.Builder(SettingPwActivity.this);
                            builder.setTitle("[알림] 비밀번호 변경 완료")
                                    .setMessage("비밀번호가 변경되었습니다.\n변경된 비밀번호로 다시 로그인 해주세요!")
                                    .setPositiveButton("계속 진행", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //로그인 화면으로 이동
                                            Intent intent = new Intent(SettingPwActivity.this, LoginActivity.class);
                                            Log.d("비밀번호가 변경되었습니다","");

                                            startActivity(intent);

                                            finish();
                                        }
                                    });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();


                        }else {
                            Toast.makeText(SettingPwActivity.this, "변경할 비밀번호 확인 오류",Toast.LENGTH_SHORT).show();
                            mypage_setting_change_pw_check.setText("");
                            mypage_setting_change_pw_check.requestFocus();
                        }

                    }else {
                        Toast.makeText(SettingPwActivity.this, "비밀번호는 8-12자리(숫자/영문/특수문자 포함)",Toast.LENGTH_SHORT).show();
                        mypage_setting_change_pw.setText("");
                        mypage_setting_change_pw.requestFocus();
                    }

                }else {
                    Toast.makeText(SettingPwActivity.this, "현재 비밀번호 입력 오류",Toast.LENGTH_SHORT).show();
                    mypage_setting_current_pw.setText("");
                    mypage_setting_current_pw.requestFocus();
                }



            }
        });

    }


    public boolean onOptionsItemSelected(MenuItem item){
        switch ((item.getItemId())){
            case android.R.id.home:{
                Toast.makeText(SettingPwActivity.this,"backButton",Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingPwActivity.this);
                builder.setTitle("[알림] 비밀번호 수정 미완료")
                        .setMessage("수정하신 내용이 저장되지 않습니다.\n계속 진행하시겠습니까?")
                        .setPositiveButton("계속 진행", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //removeStringArrayPref(photoListKey, photoListNumKey);
                                Log.d("사진저장 미완료 reset","");
                                //setResult(RESULT_OK);
                                finish();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            }
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(SettingPwActivity.this,"backButton",Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingPwActivity.this);
        builder.setTitle("[알림] 비밀번호 수정 미완료")
                .setMessage("수정하신 내용이 저장되지 않습니다.\n계속 진행하시겠습니까?")
                .setPositiveButton("계속 진행", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //removeStringArrayPref(photoListKey, photoListNumKey);
                        Log.d("사진저장 미완료 reset","");
                        //setResult(RESULT_OK);
                        finish();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    //회원정보 불러오기
    protected void callUserInfoData(String check_email){
        sp = getSharedPreferences("userInfo",MODE_PRIVATE);

        if(sp.contains(check_email)){

            //저장되있는 userInfo 를 문자열로 받아옴.
            strUserInfoData = sp.getString(check_email, "");
            //문자열을 객체로
            userInfoData = gson.fromJson(strUserInfoData, UserInfoData.class);

            Log.d("메인 페이지","해당 키 값: "+check_email);
            Log.d("메인 페이지","인텐트로 넘어온 이메일 값: "+email);
            Log.d("메인 페이지 ","문자열"+strUserInfoData);

        }else{

        }

    }//회원정보 셰어드에서 불러오기



    //변경한 회원정보 다시 셰어드에 넣기
    //saveData
    protected void saveUserInfoData(String pw){
        Log.d("메인 페이지 데이터저장 ","수정전 회원데이터"+strUserInfoData);

        userInfoData.setEmail(email);
        userInfoData.setPw(pw);
        userInfoData.setAutoLoginCheckBox(false);

        //JSON으로 변환
        String strData = gson.toJson(userInfoData, UserInfoData.class);

        Log.d("메인 페이지 데이터저장 ","수정후 회원데이터"+strData);

        SharedPreferences sp = getSharedPreferences("userInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        //JSON으로 변환한 객체를 저장한다.
        String keyName = email;
        Log.d("회원가입때 저장한 key값",keyName);
        Log.d("비밀번호 수정한 값",pw);

        editor.putString(keyName,strData);
        //완료한다.
        editor.apply();
    }//회원정보 수정하기 END


}
