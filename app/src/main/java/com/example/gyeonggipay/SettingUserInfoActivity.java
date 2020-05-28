package com.example.gyeonggipay;

import android.app.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.Date;

public class SettingUserInfoActivity extends AppCompatActivity {

    //클래스에서 쓸 변수
    private String nickname, region;
    private Uri profile;
    //닉네임 중복체크 여부 확인 변수
    private int nickname_c;

    //xml
    private EditText setting_nickname;
    private ImageView setting_profile_img, setting_region_btn;
    private TextView setting_profile_btn, setting_region;
    private Button setting_nickname_btn, setting_btn;
    private LinearLayout region_layout;


    //--------------------사진관련 변수 선언---------------------------//

    //프로필 사진(카메라 찍기, 앨범접근, 크롭, 앨범에 저장)
    private static final int REQUEST_TAKE_PHOTO = 2222;
    private static final int REQUEST_TAKE_ALBUM = 3333;
    private static final int REQUEST_IMAGE_CROP = 4444;

    //?
    String mCurrentPhotoPath;

    //이미지uri
    Uri imageUri;
    //사진 앨범
    Uri photoURI, albumURI;

    Intent userInfo = new Intent();


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

    //항목별 수정한 데이터는 userInfo 인텐트에 임시로 저장.
    //변경버튼 누르면 userInfo 인텐트 안의 데이터를 셰어드에 저장.
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_user_info);

        //툴바 설정
        Toolbar toolbar = findViewById(R.id.setting_userInfo_toolbar);
        setSupportActionBar(toolbar);

        //뒤로 가기 버튼, 디폴트로 true만 해도 백버튼 생김
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //기본 타이틀 보여줄지 말지 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView textView = findViewById(R.id.setting_userInfo_toolbar_title);
        textView.setText("내 정보 수정");

        //설정리스트 페이지에서 거쳐서 넘어와야 함...
        Intent userEmail = getIntent();
        email = userEmail.getStringExtra("email");

        //셰어드에 저장되어 있는 회원정보
        callUserInfoData(email);
        nickname = userInfoData.getNickname();
        profile = Uri.parse(userInfoData.getProfile());
        region = userInfoData.getRegion();

        Log.d("메인페이지의 값 잘 들어오나 닉넴", nickname);
        Log.d("메인페이지의 값 잘 들어오나 사진", ""+profile);
        Log.d("메인페이지의 값 잘 들어오나 지역", region);


        //화면과 연결
        //닉네임
        setting_nickname = findViewById(R.id.setting_nickname);
        //닉네임 중복확인 버튼
        setting_nickname_btn = findViewById(R.id.setting_nickname_btn);

        //거주 지역
        setting_region = findViewById(R.id.setting_region);
        setting_region_btn = findViewById(R.id.setting_region_btn);
        region_layout = findViewById(R.id.region_layout);

        //프로필사진 뷰
        setting_profile_img = findViewById(R.id.setting_profile_img);
        //프로필 사진 변경 버튼
        setting_profile_btn = findViewById(R.id.setting_profile_btn);


        //회원정보 수정 버튼
        setting_btn = findViewById(R.id.setting_userInfo_btn);


        //--------------저장되어 있는 정보를 먼저 보여준다-----------------//
        //닉네임
        setting_nickname.setText(nickname);
        //지역
        setting_region.setText(region);
        //프로필사진
        setting_profile_img.setImageURI(profile);
        //원형으로 보이기
        setting_profile_img.setBackground(new ShapeDrawable(new OvalShape()));
        setting_profile_img.setClipToOutline(true);


        //------------------------------수정---------------------------------//

        //닉네임이 현재와 같으면 중복체크 안해도 됨-버튼 비활성화
        setting_nickname_btn.setEnabled(false);
        setting_nickname_btn.setBackgroundResource(R.drawable.btn_enable_false);

        //닉넴 실시간 체크
        setting_nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (setting_nickname.getText().toString().equals(nickname)) {
                    setting_nickname_btn.setEnabled(false);
                    setting_nickname_btn.setBackgroundResource(R.drawable.btn_enable_false);
                    nickname_c = 50;
                    Log.i("닉네임 임시 저장 번호", ":"+nickname_c);
                } else {
                    setting_nickname_btn.setEnabled(true);
                    setting_nickname_btn.setBackgroundResource(R.drawable.button_shape);
                    nickname_c = 51;
                    Log.i("닉네임 임시 저장 번호", ":"+nickname_c);
                    setting_nickname_btn.setOnClickListener(new View.OnClickListener() {
                        /*todo
                        닉네임 중복체크 if문으로 체크하기
                         */
                        @Override
                        public void onClick(View v) {//중복체크 버튼 눌렀을 때.
                            Toast.makeText(getApplicationContext(), "중복체크 버튼 눌림", Toast.LENGTH_LONG).show();
                            //닉네임 변경 완료는 수정하기 버튼 눌렸을 때!
                            //임시 저장소에 넣어 놓기
                            String userInfo_nickname = setting_nickname.getText().toString();
                            nickname_c = 50;
                            Log.i("닉네임 임시 저장", userInfo_nickname);
                            Log.i("닉네임 임시 저장 번호", ":"+nickname_c);
                            userInfo.putExtra("userInfo_nickname", userInfo_nickname);
                            userInfo.putExtra("userInfo_nickname_c", nickname_c);
                        }
                    });
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //프로필 수정 버튼 눌렀을 때
        setting_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //클릭 시, 다이얼로그 나옴
                ListClick(v);
            }
        });



        //지역
        //지역 설정하는 스피너 버튼누르게
//        region_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                resgionList(v);
//            }
//        });
        setting_region_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regionList(v);
            }
        });


        //변경버튼 눌렀을 때
        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
            //지역선택과 닉네임이 비어있지 않고 닉네임 번호가 50이면 통과
            //지금 닉네임 수정안하고 진행하면 오류뜸//해결
             Log.d("정보수정 버튼",":"+nickname_c);
             Log.d("정보수정 버튼",setting_region.getText().toString());
             Log.d("정보수정 버튼",setting_nickname.getText().toString());
             Log.d("닉네임", nickname);

             if(setting_nickname.getText().toString().equals(nickname)) {
                 nickname_c = 50;

                 Log.d("정보수정>>", ":" + nickname_c);
                 Log.d("정보수정>>", setting_region.getText().toString());
                 Log.d("정보수정>>", setting_nickname.getText().toString());

                 if ((setting_region.getText().toString() != null) && (nickname_c == 50)
                         && (setting_nickname.getText().toString() != null)) {

                     //데이터가 수정되지 않은 부분은 원래 데이터로 저장하게끔 saveUserInfoData() 에서 처리
                     saveUserInfoData();

                     AlertDialog.Builder builder = new AlertDialog.Builder(SettingUserInfoActivity.this);
                     builder.setTitle("내 정보 수정 완료!")
                             .setMessage("수정하신 내용이 저장되었습니다.")
                             .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialog, int which) {

                                     Log.i("저장지역", region);
                                     Log.i("닉네임", nickname);
                                     Log.i("닉네임 숫자", "!" + nickname_c);
                                     Log.i("이메일", email);

                                     //설정 화면에서 startActivityforResult()의 반응 값
                                     //설정화면으로 보낼 데이터
                                     Intent userInfo_setting_ok = new Intent();
                                     userInfo_setting_ok.putExtra("email", email);
                                     setResult(RESULT_OK, userInfo_setting_ok);
                                     finish();

                                 }
                             });
                     AlertDialog alertDialog = builder.create();
                     alertDialog.show();


                 } else {
                     Toast.makeText(getApplicationContext(), "수정 내용을 다시 확인해주세요. 변경하신 내용이 저장되지 않았습니다.", Toast.LENGTH_SHORT).show();
                 }
             }else
                 {
                     //닉네임 수정이 일어났을 경우
                     Log.d("정보수정 버튼",":"+nickname_c);
                     Log.d("정보수정 버튼",setting_region.getText().toString());
                     Log.d("정보수정 버튼",setting_nickname.getText().toString());

                     if ((setting_region.getText().toString() != null) && (nickname_c == 50)
                             && (setting_nickname.getText().toString() != null)) {

                         //데이터가 수정되지 않은 부분은 원래 데이터로 저장하게끔 saveUserInfoData() 에서 처리
                         saveUserInfoData();

                         AlertDialog.Builder builder = new AlertDialog.Builder(SettingUserInfoActivity.this);
                         builder.setTitle("내 정보 수정 완료!")
                                 .setMessage("수정하신 내용이 저장되었습니다.")
                                 .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                     @Override
                                     public void onClick(DialogInterface dialog, int which) {

                                         Log.i("저장지역", region);
                                         Log.i("닉네임", nickname);
                                         Log.i("닉네임 숫자", "!" + nickname_c);
                                         Log.i("이메일", email);

                                         //설정 화면에서 startActivityforResult()의 반응 값
                                         //설정화면으로 보낼 데이터
                                         Intent userInfo_setting_ok = new Intent();
                                         userInfo_setting_ok.putExtra("email", email);
                                         setResult(RESULT_OK, userInfo_setting_ok);
                                         finish();

                                     }
                                 });
                         AlertDialog alertDialog = builder.create();
                         alertDialog.show();


                     } else {
                         Toast.makeText(getApplicationContext(), "수정 내용을 다시 확인해주세요. 변경하신 내용이 저장되지 않았습니다.", Toast.LENGTH_SHORT).show();
                     }
                 }//닉네임 수정이 있었을 경우 END

            }//정보 수정하기 버튼 onClick END
        });//정보 수정하기 버튼 리스너 END

    }//onCreate END


    public boolean onOptionsItemSelected(MenuItem item){
        switch ((item.getItemId())){
            case android.R.id.home:{
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);

        }
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


    //수정한 데이터만 저장해야 함. 회원정보를 불러올때, userInfoData 가 수정할 그 회원의 데이터 객체이다.
    //saveData
    protected void saveUserInfoData(){
        Log.d("메인 페이지 데이터저장 ","수정전 회원데이터"+strUserInfoData);

        userInfoData.setEmail(email);
        //userInfoData.setPw(userInfo.getStringExtra("pw"));
        //데이터가 수정되지 않은 부분은 원래 데이터로 저장하게끔
        if(userInfo.getStringExtra("userInfo_nickname")==null){
            userInfo.putExtra("userInfo_nickname",nickname);
        }userInfoData.setNickname(userInfo.getStringExtra("userInfo_nickname"));

        if(userInfo.getStringExtra("userInfo_region")==null){
            userInfo.putExtra("userInfo_region",region);
        }userInfoData.setRegion(userInfo.getStringExtra("userInfo_region"));

        if(userInfo.getParcelableExtra("userInfo_profile")==null){
            userInfo.putExtra("userInfo_profile",profile);
        }
        Uri profile_uri = userInfo.getParcelableExtra("userInfo_profile");
        userInfoData.setProfile(profile_uri.toString());

        //JSON으로 변환
        String strData = gson.toJson(userInfoData, UserInfoData.class);

        Log.d("메인 페이지 데이터저장 ","수정후 회원데이터"+strData);

        SharedPreferences sp = getSharedPreferences("userInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        //JSON으로 변환한 객체를 저장한다.
        String keyName = email;
        Log.d("회원가입때 저장한 key값",keyName);
        Log.d("회원가입때 저장한 지역",userInfo.getStringExtra("userInfo_region"));
        //Log.d("회원가입때 저장한 이메일",userInfo.getStringExtra("userInfo_email"));
        Log.d("회원가입때 저장한 닉네임",userInfo.getStringExtra("userInfo_nickname"));

        editor.putString(keyName,strData);
        //완료한다.
        editor.apply();
    }//회원정보 수정하기 END


    //지역 다이얼로그_Menu
    ////지역 선택 버튼 : 버튼 누르면 다이얼로그로 지역 선택
    public void regionList(View view) {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("시/군 선택");

        builder.setItems(R.array.home_choice_region2, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int pos)
            {
                String[] items = getResources().getStringArray(R.array.home_choice_region2);
                for(int i =0; i<items.length; i++){
                    if(pos==i){
                        String choice_region = items[pos];
                        setting_region.setText(choice_region);
                        Toast.makeText(getApplicationContext(),choice_region+"(이)가 선택되었습니다.",
                                Toast.LENGTH_SHORT).show();
                        region = choice_region;
                        userInfo.putExtra("userInfo_region", region);
                        Log.d("지역선택 다이얼로그 임시저장", ":"+region);

//                        frg_search_spot.setText(choice_region);
//                        //인텐트에 넣어야한다.
//                        //번들에다가 넣어야 하나..
//                        Bundle bundle = new Bundle();
//                        bundle.putString("region", choice_region);
                    }
                }

                //Toast.makeText(getApplicationContext(),items[pos],Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }// //지역 다이얼로그_Menu END






    //프로필 설정 버튼 눌렀을 때 다이얼로그
    public void ListClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("프로필 설정하기");

        builder.setItems(R.array.profile_setting, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int pos)
            {
                String[] items = getResources().getStringArray(R.array.profile_setting);
                if(pos == 0){
                    //사진촬영
                    captureCamera();
                    Log.i("cropImage", "photoURI : " + photoURI + " / albumURI : " + albumURI);
                }else if(pos == 1){
                    //앨범
                    getAlbum();
                    Log.i("cropImage", "photoURI : " + photoURI + " / albumURI : " + albumURI);

                }else if(pos ==2){
                    //취소
                }

                //Toast.makeText(getApplicationContext(),items[pos],Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }//프로필 설정 버튼 눌렀을 때 다이얼로그 END

    //카메라 켜서 사진찍고 저장
    private void captureCamera(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {// 외장 메모리가 현재 read와 write를 할 수 있는 상태인지 확인한다


            //암시적 인텐트 사용 : 카메라에서 이미지 캡쳐해서 반환하도록 하는 액션.
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            //먼저 암시적 인텐트를 처리할 수 있는 앱이 단말에 존재하는지를 확인하기 위하여
            //Intent 오브젝트를 사용해 resolveActivity() 메서드를 호출한다.
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Log.e("captureCamera Error", ex.toString());
                }
                if (photoFile != null) {

                    // getUriForFile의 두 번째 인자는 Manifest provier의 authorites와 일치해야 함
                    Uri providerURI = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    imageUri = providerURI;

                    //인텐트에 전달할 때는 FileProvier의 Return값인 content://로만!!, providerURI의 값에 카메라 데이터를 넣어 보냄
                    //EXTRA_OUTPUT : 이미지가 작성 될 위치를 제어하기 위해 추가 EXTRA_OUTPUT을 전달할 수 있습니다.
                    //EXTRA_OUTPUT이 있으면 전체 크기 이미지가 EXTRA_OUTPUT의 Uri 값에 기록됩니다.
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI);

                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        } else {
            Toast.makeText(this, "저장공간이 접근 불가능한 기기입니다", Toast.LENGTH_SHORT).show();
            return;
        }
    }//카메라 켜서 사진찍고 저장 END

    //기기에 저장될 이미지 파일경로, 폴더 만들기
    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imageFile = null;
        //getExternalStorageDirectory()는 API lv29에서는 없어짐
        //이 방법은 API 레벨 29에서 사용되지 않습니다.
        // 사용자 개인 정보를 향상시키기 위해 공유 / 외부 저장 장치에 대한 직접 액세스는 사용되지 않습니다.
        // 앱이 대상인 Build.VERSION_CODES.Q경우이 메소드에서 반환 된 경로는 더 이상 앱에 직접 액세스 할 수 없습니다.
        // 앱은 다음과 같은 대안을 마이그레이션하여 공유 / 외부 저장 장치에 저장된 콘텐츠에 액세스를 계속 할 수 있습니다
        // => Context#getExternalFilesDir(String), MediaStore또는 Intent#ACTION_OPEN_DOCUMENT.
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "ggp");
        if (!storageDir.exists()) {
            Log.i("mCurrentPhotoPath1", storageDir.toString());
            storageDir.mkdirs();
        }

        imageFile = new File(storageDir, imageFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }//기기에 저장될 이미지 파일경로, 폴더 만들기 END

    //앨범에 접근하기
    private void getAlbum(){
        Log.i("getAlbum", "Call");

        //ACTION_PICK vs ACTION_GET_CONTENT

        //여기서 주의할 점은 ACTION_GET_CONTENT를 사용하라는 것!
        //ACTION_PICK보다 공식적으로 지원하는 것이 ACTION_GET_CONTENT라는 것,
        //ACTION_PICK은 INTENT.setAction()의 두 번째 파라미터에 들어가는 Uri의 값을 명시적 지정하여
        //해당 Uri를 사용하는 앱을 호출할 때 사용한다.
        //Intent intentGallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        /*  Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT); // ACTION_PICK은 사용하지 말것, deprecated + formally
            intent.setType("image/*");
            ((Activity)mContext).startActivityForResult(Intent.createChooser(intent, "Get Album"), REQUEST_TAKE_ALBUM);
        */

        //암시적 인텐트사용
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);
    }//앨범에 접근하기 END


    //사진첩에 찍은 사진 저장/ 크롭한 사진 저장
    //누가버전 이후 잘 안될 수 있음
    private void galleryAddPic(){
        Log.i("galleryAddPic", "Call");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // 해당 경로에 있는 파일을 객체화(새로 파일을 만든다는 것으로 이해하면 안 됨)
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);

        sendBroadcast(mediaScanIntent);
        Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();

    }//사진첩에 찍은 사진 저장/ 크롭한 사진 저장 END

    // 카메라 전용 크랍
    public void cropImage(){
        Log.i("cropImage", "Call");
        Log.i("cropImage", "photoURI : " + photoURI + " / albumURI : " + albumURI);

        //암시적 인텐트 카메라 크롭기능 모두(사진자르기와 포토(권한허용하면 됨) 앱이 켜짐)
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        // 50x50픽셀미만은 편집할 수 없다는 문구 처리 + 갤러리, 포토 둘다 호환하는 방법
        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoURI, "image/*");
        cropIntent.putExtra("outputX", 120); // crop한 이미지의 x축 크기, 결과물의 크기
        cropIntent.putExtra("outputY", 120); // crop한 이미지의 y축 크기
        cropIntent.putExtra("aspectX", 1); // crop 박스의 x축 비율, 1&1이면 정사각형
        cropIntent.putExtra("aspectY", 1); // crop 박스의 y축 비율
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("output", albumURI); // 크랍된 이미지를 해당 경로에 저장
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);

    }//사진첩에 찍은 사진 저장/ 크롭한 사진 저장 END


    //REQUEST_TAKE_PHOTO, REQUEST_TAKE_ALBUM, REQUEST_IMAGE_CROP
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //카메라로 사진 찍을 때
            case REQUEST_TAKE_PHOTO:

                if (resultCode == Activity.RESULT_OK) {
                    try {
                        /*Todo
                         *  사진찍고 크롭하는 거, 인텐트에 저장되는 거*/
                        //사진찍고 크롭할 수 있도록!!!!!!!
                        //사진찍은 그 파일(주소? 데이터) 크롭이미지할때 넘겨줘야 함.
                        //cropImage();
                        Log.i("REQUEST_TAKE_PHOTO", "OK");
//                        //갤러리에 찍은 사진 저장
                        galleryAddPic();
                        //이미지 뷰에 뿌리기
                        //원으로 만들면 더 좋구
                        setting_profile_img.setImageURI(imageUri);

                        //인텐트에 넣어보기
                        //intent.putExtra("imageUri", mUri);
                        userInfo.putExtra("userInfo_profile", imageUri);
                        //startActivityForResult(userInfo, IMAGE_ACTIVITY);

                    } catch (Exception e) {
                        Log.e("REQUEST_TAKE_PHOTO", e.toString());
                    }
                } else {
                    Toast.makeText(SettingUserInfoActivity.this, "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            //앨범에서 선택
            case REQUEST_TAKE_ALBUM:
                if (resultCode == Activity.RESULT_OK) {

                    if (data.getData() != null) {
                        try {
                            File albumFile = null;
                            albumFile = createImageFile();
                            photoURI = data.getData();
                            albumURI = Uri.fromFile(albumFile);

                            //앨범에서 선택하면 이미지 크롭으로 넘어감
                            cropImage();
                        } catch (Exception e) {
                            Log.e("TAKE_ALBUM_SINGLE ERROR", e.toString());
                        }
                    }
                }
                break;

            case REQUEST_IMAGE_CROP:
                if (resultCode == Activity.RESULT_OK) {
                    //이미지 크롭하면 갤러리에 저장됨
                    galleryAddPic();
                    setting_profile_img.setImageURI(albumURI);
                    //인텐트에 넣어보기
                    userInfo.putExtra("userInfo_profile", albumURI);
                }
                break;
        }
    }




}
