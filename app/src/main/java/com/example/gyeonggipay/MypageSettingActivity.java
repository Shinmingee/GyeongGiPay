package com.example.gyeonggipay;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;

public class MypageSettingActivity extends AppCompatActivity implements TextView.OnClickListener{

    private TextView mypg_setting_userInfo_setting, mypg_setting_userInfo_setting_btn,
            mypg_setting_pw_setting,mypg_setting_pw_setting_btn,
            mypg_setting_notice, mypg_setting_notice_btn,
            mypg_setting_faq, mypg_setting_faq_btn,
            mypg_setting_policy, mypg_setting_policy_btn,
            mypg_setting_privacy, mypg_setting_privacy_btn,
            mypg_setting_logout, mypg_setting_logout_btn,
            mypg_setting_close_account, mypg_setting_close_account_btn;


    //내정보 수정하기 액티비티로 보내는 요청 값
    private int REQUEST_SETTING_USER_INFO = 11;


    //넘겨받은 인텐트
    String email;

    Gson gson = new Gson();
    UserInfoData userInfoData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypgae_setting_page);

        //툴바 설정
        Toolbar tb = findViewById(R.id.mypage_setting_toolbar);
        //ActionBar actionBar = getSupportActionBar();
        setSupportActionBar(tb);


        // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //기본 타이틀 보여줄지 말지 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView apptitle = findViewById(R.id.mypage_setting_toolbar_title);
        apptitle.setText("설정");

        //설정 버튼 눌렀을 때 전달되는 email_key값
        Intent email_key = getIntent();
        email = email_key.getStringExtra("email");

        SharedPreferences sharedPreferences = getSharedPreferences("login_id",MODE_PRIVATE);
        email = sharedPreferences.getString("login_email",email);

        // 각 TextView 이벤트 리스너로 this(MypageSettingActivity) 지정.

        //xml
        //회원정보수정
        mypg_setting_userInfo_setting = findViewById(R.id.mypg_setting_userInfo_setting);
        mypg_setting_userInfo_setting_btn = findViewById(R.id.mypg_setting_userInfo_setting_btn);
        mypg_setting_userInfo_setting.setOnClickListener(this);
        mypg_setting_userInfo_setting_btn.setOnClickListener(this);

        //비밀번호 변경
        mypg_setting_pw_setting = findViewById(R.id.mypg_setting_pw_setting);
        mypg_setting_pw_setting_btn = findViewById(R.id.mypg_setting_pw_setting_btn);
        mypg_setting_pw_setting.setOnClickListener(this);
        mypg_setting_pw_setting_btn.setOnClickListener(this);

        //공지사항
        mypg_setting_notice = findViewById(R.id.mypg_setting_notice);
        mypg_setting_notice_btn = findViewById(R.id.mypg_setting_notice_btn);
        mypg_setting_pw_setting.setOnClickListener(this);
        mypg_setting_pw_setting_btn.setOnClickListener(this);

        //FAQ
        mypg_setting_faq = findViewById(R.id.mypg_setting_faq);
        mypg_setting_faq_btn = findViewById(R.id.mypg_setting_faq_btn);
        mypg_setting_faq.setOnClickListener(this);
        mypg_setting_faq_btn.setOnClickListener(this);

        //이용약관
        mypg_setting_policy = findViewById(R.id.mypg_setting_policy);
        mypg_setting_policy_btn = findViewById(R.id.mypg_setting_policy_btn);
        mypg_setting_policy.setOnClickListener(this);
        mypg_setting_policy_btn.setOnClickListener(this);

        //개인정보이용약관
        mypg_setting_privacy = findViewById(R.id.mypg_setting_privacy);
        mypg_setting_privacy_btn = findViewById(R.id.mypg_setting_privacy_btn);
        mypg_setting_privacy.setOnClickListener(this);
        mypg_setting_privacy_btn.setOnClickListener(this);

        //로그아웃
        mypg_setting_logout = findViewById(R.id.mypg_setting_logout);
        mypg_setting_logout_btn = findViewById(R.id.mypg_setting_logout_btn);
        mypg_setting_logout.setOnClickListener(this);
        mypg_setting_logout_btn.setOnClickListener(this);

        //회원탈퇴
        mypg_setting_close_account = findViewById(R.id.mypg_setting_close_account);
        mypg_setting_close_account_btn = findViewById(R.id.mypg_setting_close_account_btn);
        mypg_setting_close_account.setOnClickListener(this);
        mypg_setting_close_account_btn.setOnClickListener(this);

    }


    // TextView.OnclickListener를 implements하므로 onClick() 함수를 오버라이딩.
    @Override
    public void onClick(View v) {
        Intent goSettingActivity = new Intent(this, SettingUserInfoActivity.class);

        switch (v.getId()) {
            case R.id.mypg_setting_userInfo_setting:
            case  R.id.mypg_setting_userInfo_setting_btn:
                goSettingActivity.putExtra("email", email);
                startActivityForResult(goSettingActivity, REQUEST_SETTING_USER_INFO);
                break;

            case R.id.mypg_setting_pw_setting:
            case R.id.mypg_setting_pw_setting_btn:
                goSettingActivity = new Intent(this, SettingPwActivity.class);
                goSettingActivity.putExtra("email", email);
                startActivity(goSettingActivity);
                break;

            case R.id.mypg_setting_notice:
            case R.id.mypg_setting_notice_btn:
                break;

            case R.id.mypg_setting_faq:
            case R.id.mypg_setting_faq_btn:
                break;

            case R.id.mypg_setting_policy:
            case R.id.mypg_setting_policy_btn:
                break;

            case R.id.mypg_setting_privacy:
            case R.id.mypg_setting_privacy_btn:
                break;

            case R.id.mypg_setting_logout:
            case R.id.mypg_setting_logout_btn: //로그아웃

                Toast.makeText(MypageSettingActivity.this,"로그아웃",Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(MypageSettingActivity.this);
                builder.setTitle("[알림] 로그아웃")
                        .setMessage("로그아웃 하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //로그인된 이메일 => 저장 셰어드 값 지우기
                                SharedPreferences sp = getSharedPreferences("login_id",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                if (sp.contains("login_email")) {
                                    editor.remove("login_email");
                                    editor.apply();
                                }
                                sp = getSharedPreferences("auto",MODE_PRIVATE);
                                SharedPreferences.Editor editor1 = sp.edit();
                                if(sp.contains("login_key")){
                                    editor1.remove("login_key");
                                    editor1.apply();
                                }
                                sp = getSharedPreferences("userInfo", MODE_PRIVATE);
                                String strUserInfoData = sp.getString(email, "");
                                Log.d("여기 userInfo 값으로, ", strUserInfoData);
                                //UserInfoData 클래스 선언(string으로 된 json->gson->javaObject)
                                userInfoData = gson.fromJson(strUserInfoData, UserInfoData.class);
                                if (userInfoData != null) {
                                    Log.d("UserInfoData 데이터", strUserInfoData);
                                    if (userInfoData.getAutoLoginCheckBox()) {
                                        //체크박스가 true(check 됨) -> 바로 메인으로 감
                                        userInfoData.setAutoLoginCheckBox(false);
                                    }
                                    //JSON으로 변환
                                    String strData = gson.toJson(userInfoData, UserInfoData.class);
                                    SharedPreferences sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
                                    SharedPreferences.Editor ed = sharedPreferences.edit();
                                    //JSON으로 변환한 객체를 저장한다.
                                    String keyName = email;
                                    ed.putString(keyName,strData);
                                    //완료한다.
                                    ed.apply();

                                }
                                //로그인 화면으로 이동
                                Intent intent = new Intent(MypageSettingActivity.this, LoginActivity.class);
                                Log.d("로그아웃됨","");

                                startActivity(intent);

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
                break;

            case R.id.mypg_setting_close_account:
            case R.id.mypg_setting_close_account_btn:

                AlertDialog.Builder b = new AlertDialog.Builder(MypageSettingActivity.this);
                b.setTitle("[알림] 회원탈퇴")
                        .setMessage("회원 탈퇴 하시겠습니까?\n등록했던 가맹점의 리뷰는 삭제되지 않습니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //userInfo 셰어드에 email키로 저장했던 정보를 지운다.
                                SharedPreferences sp = getSharedPreferences("userInfo",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                if (sp.contains(email)) {
                                    editor.remove(email);
                                    editor.apply();
                                }
                                sp = getSharedPreferences("auto",MODE_PRIVATE);
                                SharedPreferences.Editor e = sp.edit();
                                if(sp.contains(email)){
                                    e.remove(email);
                                    e.apply();
                                }
                                sp = getSharedPreferences("login_id",MODE_PRIVATE);
                                SharedPreferences.Editor edit = sp.edit();
                                if (sp.contains("login_email")) {
                                    edit.remove("login_email");
                                    edit.apply();
                                }

                                Toast.makeText(MypageSettingActivity.this,"회원 탈퇴 완료",Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(MypageSettingActivity.this, StartActivity.class);

                                startActivity(intent);

                                finish();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog alert_Dialog = b.create();
                alert_Dialog.show();


                break;


        }
    }

    //보낸 화면에 대한 응답
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //내정보 수정하기에서 온 응답
        if (requestCode == REQUEST_SETTING_USER_INFO) {
            if (resultCode == RESULT_OK) {
                //Toast.makeText(MypageSettingActivity.this, "Result: " + data.getStringExtra("email"), Toast.LENGTH_SHORT).show();
            } else {   // RESULT_CANCEL
                //Toast.makeText(MypageSettingActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
//        } else if (requestCode == REQUEST_ANOTHER) {
//            ...
        }
    }//보낸 화면에 대한 응답 END


    //툴바 뒤로가기버튼
    public boolean onOptionsItemSelected(MenuItem item){
        switch ((item.getItemId())){
            case android.R.id.home:{
                //sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);

                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);

        }
    }




}
