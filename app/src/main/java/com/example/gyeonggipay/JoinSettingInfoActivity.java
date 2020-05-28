package com.example.gyeonggipay;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*회원가입(2) 화면.

 닉네임, 프로필사진, 거주지 입력받고 저장하는 화면
 >> 액티비티 생명주기에서 해야할 일?
  : 저장? / 화면전환 시 전 화면 죽어야 해/

 >> 닉네임에는 이메일 @앞 아이디가 자동으로 입력되어 있으려고 함.
 >> 닉네임, 지역은 반드시 기재되어 있어야 함.
   : spinner로 받아야 할 거 같은데.. 지역...


 */

public class JoinSettingInfoActivity extends AppCompatActivity {

    //xml 화면 요소
    TextView join_profile_Btn, join_goLogin_Btn;
    EditText join_nickname;
    ImageView join_profile;
    Button join_nick_certify_btn, join_JoinBtn;
    Spinner region_spinner;

    //class에서 사용할 변수
    private String nickname, region;
    private String tmpNick;

    //intent에서 받아온 값 담아놓을 변수
    private String email, pw;

    //spinner
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;

    //프로필 사진(카메라 찍기, 앨범접근, 크롭, 앨범에 저장)
    private static final int MY_PERMISSION_CAMERA = 1111;
    private static final int REQUEST_TAKE_PHOTO = 2222;
    private static final int REQUEST_TAKE_ALBUM = 3333;
    private static final int REQUEST_IMAGE_CROP = 4444;

    //?
    String mCurrentPhotoPath;

    //이미지uri
    Uri imageUri;
    //사진 앨범
    Uri photoURI, albumURI;

    final Intent userInfo = new Intent();

    //gson 인스턴스 생성
    private Gson gson = new GsonBuilder().create();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_setting_nickname);

        //화면과 연결

        //기존 회원인가요? 버튼
        join_goLogin_Btn = findViewById(R.id.join_goLogin_Btn);
        //닉네임 Edit
        join_nickname = findViewById(R.id.join_nickname);

        //프로필 사진
        join_profile = findViewById(R.id.join_profile);

        //프로필 사진 원형모양
        join_profile.setBackground(new ShapeDrawable(new OvalShape()));
        join_profile.setClipToOutline(true);

        //출처: https://chocorolls.tistory.com/47 [초코롤의 개발이야기]

        //프로필 변경 버튼
        join_profile_Btn = findViewById(R.id.join_profile_Btn);

        //닉네임 중복검사
        join_nick_certify_btn = findViewById(R.id.join_nick_certify_btn);

        //가입하기 버튼
        join_JoinBtn = findViewById(R.id.join_JoinBtn);


        //인텐트 받아온거
        Intent goSettingNick = getIntent();
        email = goSettingNick.getStringExtra("email");
        pw = goSettingNick.getStringExtra("pw");
        Log.i("INTENT (비밀번호 + 이메일)" ,pw + email);

        //인텐트

        userInfo.putExtra("email",email);
        userInfo.putExtra("pw",pw);

        //닉네임 : 등록한 이메일의 @앞 아이디를 닉네임의 default 값으로 준다.
        //이멜을 @로 나눈다.
        tmpNick = email.split("@")[0];
        Log.i("임시닉네임",tmpNick);
        //등록한 이메일의 @앞 아이디를 닉네임의 default 값으로 준다.
        join_nickname.setText(tmpNick);
        //등록한 이메일의 @앞 아이디를 닉네임으로 설정한다.
        nickname = tmpNick;
        Log.i("닉네임저장",nickname);
        userInfo.putExtra("nickname", nickname);


        //닉네임이 default와 같으면 중복체크 안해도 됨-버튼 비활성화
        join_nick_certify_btn.setEnabled(false);
        join_nick_certify_btn.setBackgroundResource(R.drawable.btn_enable_false);

        //닉넴 실시간 체크
        join_nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (join_nickname.getText().toString().equals(tmpNick)) {
                    join_nick_certify_btn.setEnabled(false);
                    join_nick_certify_btn.setBackgroundResource(R.drawable.btn_enable_false);
                } else {
                    join_nick_certify_btn.setEnabled(true);
                    join_nick_certify_btn.setBackgroundResource(R.drawable.button_shape);
                    join_nick_certify_btn.setOnClickListener(new View.OnClickListener() {
                        /*todo
                        닉네임 중복체크 if문으로 체크하기
                         */
                        @Override
                        public void onClick(View v) {//중복체크 버튼 눌렀을 때.
                            Toast.makeText(getApplicationContext(), "중복체크 버튼 눌림", Toast.LENGTH_LONG).show();
                            //닉네임에 적혀있는 닉네임이 최종 닉네임으로 저장
                            nickname = join_nickname.getText().toString();
                            Log.i("닉네임저장",nickname);
                            userInfo.putExtra("nickname", nickname);
                        }
                    });
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        //경기도 지역 spinner
        arrayList = new ArrayList<>();
        arrayList.add("거주하는 시/군 입력(필수입력)");
        arrayList.add("가평군");
        arrayList.add("고양시");
        arrayList.add("과천시");
        arrayList.add("광명시");
        arrayList.add("광주시");
        arrayList.add("구리시");
        arrayList.add("군포시");
        arrayList.add("남양주시");
        arrayList.add("동두천시");
        arrayList.add("부천시");
        arrayList.add("수원시");
        arrayList.add("안산시");
        arrayList.add("안성시");
        arrayList.add("안양시");
        arrayList.add("양주시");
        arrayList.add("양평시");
        arrayList.add("여주시");
        arrayList.add("연천군");
        arrayList.add("오산시");
        arrayList.add("용인시");
        arrayList.add("의왕시");
        arrayList.add("의정부시");
        arrayList.add("이천시");
        arrayList.add("파주시");
        arrayList.add("평택시");
        arrayList.add("포천시");
        arrayList.add("하남시");
        arrayList.add("화성시");


        arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                arrayList);

        region_spinner = findViewById(R.id.region_spinner);
        region_spinner.setAdapter(arrayAdapter);
        region_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){

                }else{
                    Toast.makeText(getApplicationContext(),arrayList.get(i)+"(이)가 선택되었습니다.",
                            Toast.LENGTH_SHORT).show();
                    region = arrayList.get(i);
                    //선택한 지역을 인텐트에 저장.
                    userInfo.putExtra("region",region);
                    Log.i("지역선택", region);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
            });//출처: https://bottlecok.tistory.com/63 [잡캐의 IT 꿀팁]


        //프로필 사진
        //기본이미지 등록->수정가능하게
        //프로필 사진
        //join_profile = findViewById(R.id.join_profile);
        //프로필 변경 버튼 누르면 앨범,카메라 선택 후 해당 이미지 받아오기
        join_profile_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                //권한 체크 -> 내부 저장소에 사진 저장할 때 이거 필요 접근 권한
                checkPermission();
                ListClick(view);
            }
        });

        //가입하기 버튼 눌렀을 때(거주지, 닉네임, 프로필사진 있으면(default data) pass 하나라도 없음 바꾸)
        join_JoinBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick (View view){
                //지역선택과 닉네임이 비어있지 않으면 통과
                //지금 닉네임 수정안하고 진행하면 오류뜸//해결
                if(!region.equals(null)&&!join_nickname.getText().toString().equals(null)){
                    Log.i("저장지역",region);
                    Log.i("닉네임", nickname);

                    userInfo.getStringExtra("email");
                    Log.i("넘겨줄 이메일" , userInfo.getStringExtra("email"));
                    Log.i("넘겨줄 패스워드" , userInfo.getStringExtra("pw"));
                    Log.i("넘겨줄 닉네임" , userInfo.getStringExtra("nickname"));
                    Log.i("넘겨줄 지역" , userInfo.getStringExtra("region"));

                    //로그인 화면으로 넘어갈때 인텐트에 데이터 넣어간다.
                    Intent goLogin = new Intent(getApplicationContext(), LoginActivity.class);

                    goLogin.putExtra("email", userInfo.getStringExtra("email"));
//                    goLogin.putExtra("pw", userInfo.getStringExtra("pw"));
//                    goLogin.putExtra("nickname", userInfo.getStringExtra("nickname"));
//                    goLogin.putExtra("region", userInfo.getStringExtra("region"));
//                    //사진전달이 어케 되는지 잘 모름
//                    goLogin.putExtra("profile", userInfo.getParcelableExtra("profile"));

                    //로그인 화면으로 넘어갈 때 prefUserInfo 에 넣는다.
                    saveUserInfoData();

                    startActivity(goLogin);

                    finish();

//                    intent.putExtra("result", "some value");
//                    setResult(RESULT_OK, intent);
//                    finish();


                    //출처: http://zeany.net/54 [소소한 IT 이야기]

                }else{
                    Toast.makeText(getApplicationContext(), "닉네임 값과 지역 값 입력이 저장되지 않습니다.", Toast.LENGTH_SHORT).show();
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

    //saveNickname
    protected void saveNickname(){

    }


    //saveData
    protected void saveUserInfoData(){
        UserInfoData userInfoData = new UserInfoData();

        userInfoData.setEmail(userInfo.getStringExtra("email"));
        userInfoData.setPw(userInfo.getStringExtra("pw"));
        userInfoData.setNickname(userInfo.getStringExtra("nickname"));
        userInfoData.setRegion(userInfo.getStringExtra("region"));
        Uri profile_uri = userInfo.getParcelableExtra("profile");
        userInfoData.setProfile(profile_uri.toString());

        //JSON으로 변환
        String strData = gson.toJson(userInfoData, UserInfoData.class);

        SharedPreferences sp = getSharedPreferences("userInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        //JSON으로 변환한 객체를 저장한다.
        String keyName = userInfo.getStringExtra("email");
        Log.d("회원가입때 저장한 key값",keyName);
        Log.d("회원가입때 저장한 지역",userInfo.getStringExtra("region"));
        Log.d("회원가입때 저장한 이메일",userInfo.getStringExtra("email"));
        Log.d("회원가입때 저장한 닉네임",userInfo.getStringExtra("nickname"));

        editor.putString(keyName,strData);
        //완료한다.
        editor.apply();
    }

    //callData ..이런 경우는 언제 발생할까? > 아무래도 갑자기 꺼지고 다시 앱을 실행할 때? 근데 회원가입에서 그렇게 하나?? 보통 안할 듯 : 안해야지




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
    }

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
    }

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
    }




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
    }

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
    }

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
    }

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
                        join_profile.setImageURI(imageUri);

                        //인텐트에 넣어보기
                        //intent.putExtra("imageUri", mUri);
                        userInfo.putExtra("profile", imageUri);
                        //startActivityForResult(userInfo, IMAGE_ACTIVITY);


                    } catch (Exception e) {
                        Log.e("REQUEST_TAKE_PHOTO", e.toString());
                    }
                } else {
                    Toast.makeText(JoinSettingInfoActivity.this, "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
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
                            cropImage();
                        } catch (Exception e) {
                            Log.e("TAKE_ALBUM_SINGLE ERROR", e.toString());
                        }
                    }
                }
                break;

            case REQUEST_IMAGE_CROP:
                if (resultCode == Activity.RESULT_OK) {
                    galleryAddPic();
                    join_profile.setImageURI(albumURI);
                    //인텐트에 넣어보기
                    userInfo.putExtra("profile", albumURI);
                }
                break;
        }
    }


    //권한 체크
    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{..}의 요청으로 넘어감
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))) {
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //앱 정보 화면까지는 이동가능,  하지만 앱 권한 설정 화면으로는 직접 이동 시킬 수는 없다.
                                //android.provider.Settings 클래스에 정의된 ACTION_APPLICATION_DETAILS_SETTINGS을 이용하면
                                //특정앱의 설정으로 이동할 수 있다.
                                //명시적인텐트
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_CAMERA:
                for (int i = 0; i < grantResults.length; i++) {
                    // grantResults[] : 허용된 권한은 0, 거부한 권한은 -1
                    if (grantResults[i] < 0) {
                        Toast.makeText(JoinSettingInfoActivity.this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // 허용했다면 이 부분에서..

                break;
        }
    }


//    @Override
//    protected void onPause() {
//        super.onPause();
//        //join_nickname.setText("");
//
//
//
//    }
}
