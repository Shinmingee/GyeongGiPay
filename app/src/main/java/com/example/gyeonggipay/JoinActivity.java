package com.example.gyeonggipay;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

/*회원가입 화면

 회원 정보 입력받고 저장하는 화면
 >> 액티비티 생명주기에서 해야할 일?
  : 저장? / 화면전환 시 전 화면 죽어야 해/

 >>이메일(인증)&비번설정화면 다음 화면 : 닉네임 설정 화면
  -> 기본으로 이메일 @ 앞 문자로 이루어진 닉네임 부여 예정. 추 후 자신이 변경할 수 있음
  -> 필수입력 사항 : 닉네임 설정, 거주하는 시/군
  -> 선택사항 : 프로필 (없어도 넘어가게)

 */
public class JoinActivity extends AppCompatActivity {

    //xml 화면 요소
    EditText join_email_editText, join_pw_editText, join_pw_confirm_editText;
    Button join_email_certify_btn, join_goNext_Btn;
    TextView join_goLogin_Btn, textView4, textView44;
    ImageView setImage;

    //class에서 사용할
    // 데이터의 최종 변수
    private String email, pw, pwCheck;
    // 임시데이터의 변수
    private String tmpEmail;

    //로그인
   private int REQUEST_TEST = 1;

   //이메일 중복확인을 위한 셰어드
   SharedPreferences sp;

//   //이메일 체크 메시지 전달할 Handler
//    private EmailCheckingHandler emailCheckingHandler;
//    //이메일 체크 작업 수행할 Thread
//    private EmailCheckThread emailCheckThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_page);

        //xml 연결
        join_email_editText = findViewById(R.id.join_email_editText);
        //email 인증 버튼
        join_email_certify_btn = findViewById(R.id.join_email_certify_btn);
        textView44 = findViewById(R.id.textView44);

        join_pw_editText = findViewById(R.id.join_pw_editText);
        textView4 = findViewById(R.id.textView4);
        join_pw_confirm_editText = findViewById(R.id.join_pw_confirm_editText);

        setImage = findViewById(R.id.join_pw_image);


        //비밀번호 정규식 체크
        // 비밀번호 규칙 정규식
        // : 숫자, 특문 각 1회 이상, 영문은 2개 이상 사용하여 8자리 이상 입력
        //String regExpPw = " /(?=.*\d{1,12})(?=.*[~`!@#$%\^&*()-+=]{1,12})(?=.*[a-zA-Z]{1,12}).{8,12}$/ ";
        //^(?=.*\d)(?=.*[~`!@#$%\^&*()-])(?=.*[a-zA-Z]).{8,12}$
        join_pw_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //비밀번호 유효성
                //8-12자리, 영어/숫자/특수문자 포함
                if(Pattern.matches("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&]).{8,12}.$", join_pw_editText.getText().toString()))
                {//형식이 맞다면
                    join_pw_editText.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#44494F")));
                    textView4.setTextColor(ColorStateList.valueOf(Color.parseColor("#44494F")));

                }else{//형식이 아니라면
                    join_pw_editText.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFB71C1C")));
                    textView4.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFB71C1C")));
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        //비밀번호 확인에 실시간 체크
        join_pw_confirm_editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { //입력하여 변화가 생기기전에 처리

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { //변화와 동시에 처리를 해주고자 할 때
                if (join_pw_editText.getText().toString().equals(join_pw_confirm_editText.getText().toString())) {
                    setImage.setImageResource(R.drawable.join_pw_right);
                } else {
                    setImage.setImageResource(R.drawable.join_pw_not);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { //입력이 끝났을 때

            }

        });


        //email 실시간 정규표현식 맞으면 > 인증버튼 활성화 > 버튼 눌렀을 때 > 중복확인 후, 인증번호 이메일로 발송

        join_email_editText.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tmpEmail = join_email_editText.getText().toString();
                //이메일 형식
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(tmpEmail).matches()) {
                    join_email_certify_btn.setEnabled(false);
                    join_email_certify_btn.setBackgroundResource(R.drawable.btn_enable_false);
                    join_email_editText.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFB71C1C")));
                    textView44.setVisibility(View.VISIBLE);
                    textView44.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFB71C1C")));
                    //Toast.makeText(JoinActivity.this,"이메일 형식을 확인해주세요.",Toast.LENGTH_SHORT).show();
                } else {
                    //이메일 중복체크
                    sp = getSharedPreferences("userInfo", MODE_PRIVATE);
                    Log.d("키값 중복 있니?", ":"+sp.contains(tmpEmail));
                    if (!sp.contains(tmpEmail)) {//겹치는 이메일 값 없으면,

                        join_email_certify_btn.setEnabled(true);
                        join_email_certify_btn.setBackgroundResource(R.drawable.button_shape);
                        join_email_editText.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#44494F")));
                        textView44.setVisibility(View.GONE);
                        textView44.setTextColor(ColorStateList.valueOf(Color.parseColor("#44494F")));
                        join_email_certify_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) { // 인증화면으로
                                Log.d("인증화면 들어갔니?","!!");
                                sendMail();

                            }

                        });
                    }else{
                        Toast.makeText(JoinActivity.this,"이미 가입된 이메일 입니다.",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });








        //다음으로 버튼
        join_goNext_Btn = findViewById(R.id.join_goNext_Btn);
        //버튼 눌렀을 때, 닉네임 설정 화면으로 이동
        //인텐트로 데이터 저장시켜서 계속 갖고 있어야 함 - 이메일, 비번

        join_goNext_Btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //입력한 내용 받아옴
                email = join_email_editText.getText().toString();
                Log.i("이멜", email);
                pw = join_pw_editText.getText().toString();
                Log.i("비번", pw);
                pwCheck = join_pw_confirm_editText.getText().toString();
                Log.i("비번", pwCheck);


                //데이터가 입력되어 있는 지 확인 후 넘어가야 함 //이메일 중복체크(SharedPreferences의 userInfo서 key값 가져와서 체크하기)

                /* Todo 비밀번호. 이메일 정규표현식 체크!
                 */

                //비밀번호 일치되게 입력?
                if (pw.equals(pwCheck)) { //비밀번호 일치
                    Toast.makeText(getApplicationContext(), "비밀번호 확인완료", Toast.LENGTH_LONG).show();
                    //인증되면 셰어드에 저장하도록 했음
                    sp = getSharedPreferences("email_auth",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    //이멜 인증 받았는지 체크
                    if (sp.contains("auth_check")) {//이메일 인증을 해서 셰어드에 저장되어 있으면 통과

                        Toast.makeText(getApplicationContext(), "이메일 입력 확인", Toast.LENGTH_LONG).show();
                        Intent goSettingNick = new Intent(JoinActivity.this, JoinSettingInfoActivity.class);
                        //인텐트에 정보 넣기
                        goSettingNick.putExtra("email", email);
                        goSettingNick.putExtra("pw", pw);

                        //startActivityForResult(goSettingNick, REQUEST_TEST);
                        // startActivityForResult(intent);
                        // 출처: http://zeany.net/54 [소소한 IT 이야기]
                        startActivity(goSettingNick);
                        //다음화면 클릭하면 이 화면은 사라짐 ?
                        // (뒤에도 가입 화면이라... 뒤로 넘어가면 남아있어야 할 정보인가?)

                        //이메일 인증 셰어드 지우기
                        editor.remove("auth_check");
                        editor.apply();

                        finish();
                    }


                } else { //비밀번호 불일치
                    Toast.makeText(getApplicationContext(), "비밀번호 틀림 다시 입력해주세요", Toast.LENGTH_LONG).show();
                    //비번 재입력 다이얼로그 -> 회원가입 화면에서 비번만 다시 입력하도록해야 할 듯..
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(JoinActivity.this);
                    alertBuilder
                            .setTitle("비밀번호 불일치")
                            .setMessage("비밀번호가 일치하지 않습니다.")
                            .setCancelable(true)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //입력되었던 내용 지우고 포커스를 비번에 놓기?
                                    //입력된 내용 지우기
                                    join_pw_editText.setText("");
                                    join_pw_confirm_editText.setText("");

                                    //포커스 비번에 놓기
                                    join_pw_editText.requestFocus();
                                    Log.d("TEST", "현재 포커스=>" + getCurrentFocus());

                                    //출처: https://itpangpang.xyz/303 [ITPangPang]

                                }
                            });
                    AlertDialog dialog = alertBuilder.create();
                    dialog.show();

//                    출처: https://cholol.tistory.com/404?category=572900 [IT, I Think ]
                }
            }
        });


        //기존 회원일 경우, 로그인 화면으로 넘어감
        join_goLogin_Btn = findViewById(R.id.join_goLogin_Btn);
        join_goLogin_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goLogin = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(goLogin);
                //로그인 화면으로 클릭하면 이 화면은 사라짐
                finish();
            }
        });


    }


    private void sendMail() {

        String mail = join_email_editText.getText().toString().trim();
        //보낼 내용
        String message = createEmailCode();

        //보낼 메시지 제목
        String subject = "[GyeongGiPay] 이메일 인증";


        Log.d("mail",mail);

        //Send Mail
        JavaMailAPI javaMailAPI = new JavaMailAPI(JoinActivity.this,mail,subject,message);

        Log.d("javaMailAPI", "여기 들어와?");

        //AsyncTask 실행
        javaMailAPI.execute();

        Log.d("테스트 sendMail()", "여기 먼저인가요?2");


    }


    private String createEmailCode() { //이메일 인증코드 생성
        String[] str = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
                "t", "u", "v", "w", "x", "y", "z", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        String newCode = new String();

        for (int x = 0; x < 8; x++) {
            int random = (int) (Math.random() * str.length);
            newCode += str[random];
        }


        return newCode;
    }





    //화면 전환-> 뒤에 있는 상태
    protected void onStop() {
        super.onStop();

       // Toast.makeText(getApplicationContext(), "JoinActivity onStop()상태", Toast.LENGTH_LONG).show();
        //이메일 인증과 비밀번호 확인한 후의 데이터가 남아있으면 안되므로, 지운다.
//        join_email_editText.setText("");
//        join_pw_editText.setText("");
//        join_pw_confirm_editText.setText("");

    }








}

