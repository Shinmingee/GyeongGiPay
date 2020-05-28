package com.example.gyeonggipay;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;




public class JavaMailAPI extends AsyncTask<Void,Void,Void>  {

    //Add those line in dependencies
    //implementation files('libs/activation.jar')
    //implementation files('libs/additionnal.jar')
    //implementation files('libs/mail.jar')

    //Need INTERNET permission

    /*Dialog에 관련된 필드*/

    LayoutInflater dialog; //LayoutInflater
    View dialogLayout; //layout을 담을 View
    Dialog authDialog; //dialog 객체


    /*카운트 다운 타이머에 관련된 필드*/
//
    TextView time_counter; //시간을 보여주는 TextView
    EditText emailAuth_number; //인증 번호를 입력 하는 칸
    Button emailAuth_btn; // 인증버튼

    //
    //EditText join_pw_editText;

    //총 시간 3분(180초)
    int totalCount = 180;

    //Variables
    private Context mContext;
    private Session mSession;

    private String mEmail;
    private String mSubject;
    private String mMessage;

    private SharedPreferences sp;

    private ProgressDialog mProgressDialog;

    //Constructor
    public JavaMailAPI(Context mContext, String mEmail, String mSubject, String mMessage) {
        this.mContext = mContext;
        this.mEmail = mEmail;
        this.mSubject = mSubject;
        this.mMessage = mMessage;
    }

    //AsyncTask가 메인 엑티비티에서 실행되면
    //onPreExecute()에서 백그라운드(AsyncTask의 task)로 신호를 보냄
    //프로그래스 바를 띄워서 진행상황을 알림
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Show progress dialog while sending email
        mProgressDialog = ProgressDialog.show(mContext,"입력하신 이메일로 인증번호 8자리를 보냅니다.", "잠시만 기다려주세요...",false,false);
    }


    //doInBackground( ) 메소드에서 작업이 끝나면 onPostExcuted() 로
    // 결과 파라미터를 리턴하면서 그 리턴값을 통해 스레드 작업이 끝났을 때의 동작을 구현합니다.
    //출처: https://itmining.tistory.com/7 [IT 마이닝]
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismiss progress dialog when message successfully send
        mProgressDialog.dismiss();

        //Show success toast
        Toast.makeText(mContext, "인증번호 전송완료!", Toast.LENGTH_SHORT).show();

        Log.d("테스트 onPostExcuted()", "여기 먼저인가요?");


        dialog = LayoutInflater.from(mContext);
        dialogLayout = dialog.inflate(R.layout.auth_dialog, null); // LayoutInflater를 통해 XML에 정의된 Resource들을 View의 형태로 반환 시켜 줌
        authDialog = new Dialog(mContext); //Dialog 객체 생성
        authDialog.setContentView(dialogLayout); //Dialog에 inflate한 View를 탑재 하여줌
        //authDialog.setCanceledOnTouchOutside(true);
        authDialog.setCanceledOnTouchOutside(false); //Dialog 바깥 부분을 선택해도 닫히지 않게 설정함.
        authDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //다이얼로그를 닫을 때 일어날 일을 정의하기 위해 onCancelListener 설정
            }
        });
        authDialog.show(); //Dialog를 나타내어 준다.
        countDown();


    }


    //백그라운드에서 진행되는 메일 보내기
    @Override
    protected Void doInBackground(Void... params) {


        Log.d("여기 지나?", "백그라운드");
        //Creating properties
        Properties props = new Properties();

        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Creating a new session
        mSession = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Utils.EMAIL, Utils.PASSWORD);
                    }
                });

        try {
            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(mSession);

            //Setting sender address
            mm.setFrom(new InternetAddress(Utils.EMAIL));
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(mEmail));
            //Adding subject
            mm.setSubject(mSubject);
            //Adding message
            mm.setText(mMessage);
            //Sending email
            Transport.send(mm);

//            BodyPart messageBodyPart = new MimeBodyPart();
//
//            messageBodyPart.setText(message);
//
//            Multipart multipart = new MimeMultipart();
//
//            multipart.addBodyPart(messageBodyPart);
//
//            messageBodyPart = new MimeBodyPart();
//
//            DataSource source = new FileDataSource(filePath);
//
//            messageBodyPart.setDataHandler(new DataHandler(source));
//
//            messageBodyPart.setFileName(filePath);
//
//            multipart.addBodyPart(messageBodyPart);

//            mm.setContent(multipart);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }



    private void countDown() {

        sp = mContext.getSharedPreferences("email_auth",Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();

        time_counter = (TextView) dialogLayout.findViewById(R.id.emailAuth_time_counter);
        //줄어드는 시간을 나타내는 TextView
        emailAuth_number = (EditText) dialogLayout.findViewById(R.id.emailAuth_number);
        //사용자 인증 번호 입력창
        emailAuth_btn = (Button) dialogLayout.findViewById(R.id.emailAuth_btn);
        //인증하기 버튼

        emailAuth_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkStr = emailAuth_number.getText().toString();
                Log.d("확인!!checkStr",":"+checkStr);
                Log.d("확인!!mMessage",":"+mMessage);
                Log.d("확인",":"+checkStr.equals(mMessage));

                //인증문자 같으면
                if(checkStr.equals(mMessage)){
                    Toast.makeText(mContext,"이메일 인증되었습니다.",Toast.LENGTH_SHORT).show();
                    editor.putBoolean("auth_check",true);
                    editor.apply();

                    if (timerHandler != null) {
                        timerHandler.removeCallbacks(timerRunnable);
                    }
                    totalCount = 0;

                    //다이얼로그 삭제
                    authDialog.dismiss();

                }else{
                    Toast.makeText(mContext,"이메일 인증을 실패했습니다.",Toast.LENGTH_SHORT).show();

//                    if (timerHandler != null) {
//                        timerHandler.removeCallbacks(timerRunnable);
//                    }
//                    //다이얼로그 삭제
//                    authDialog.dismiss();

                }
            }
        });
        timerHandler.postDelayed(timerRunnable, 1000);

    }

    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (totalCount > 0) {
                totalCount--;
                //화면에 표시되는 게 초가 10보다 작으면 앞에 0을 붙여서 같이 출력 하기 위한 if문
                if((totalCount - ((totalCount/60)*60))>=10) {
                    time_counter.setText((totalCount / 60) + "분 " + (totalCount - ((totalCount / 60) * 60)) + "초");
                }else {
                    time_counter.setText((totalCount/60) + "분 0"+(totalCount - ((totalCount / 60) * 60))+"초");
                }
                timerHandler.postDelayed(timerRunnable, 1000);
                Log.d("타임러너블 확인",":"+totalCount);
            } else {
                //시간이 지나면
                //다이얼로그 삭제
                Toast.makeText(mContext,"이메일 인증 시간이 초과되었습니다. 다시 인증해주세요.",Toast.LENGTH_LONG).show();

                //노티 알림되면 참 좋겠...

                authDialog.dismiss();

                if (timerHandler != null) {
                    timerHandler.removeCallbacks(timerRunnable);
                }
            }
        }
    };



}

