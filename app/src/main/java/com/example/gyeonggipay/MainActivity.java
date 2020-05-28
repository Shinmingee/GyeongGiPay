package com.example.gyeonggipay;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MainActivity extends AppCompatActivity{ //implements FragmentReview.ReviewPlaceSetListener {

    private FragmentManager fragmentManager = getSupportFragmentManager();

    private FragmentHome fragmentHome = new FragmentHome();
    private FragmentReview fragmentReview = new FragmentReview();
    private FragmentMypage fragmentMypage = new FragmentMypage();

    //가입한 회원정보 변수
    private String region,email,pw,nickname;
    private Uri profile;

    //리뷰 변수
    //java에서 쓸 변수
    private String reviewStoreName, reviewContent, reviewRegion, reviewCategory =null;
    //별점 평점
    private float reviewStarRating = 0;
    //ArrayList<Uri> photoList; //사진 여러장
    private Uri reviewPhoto = null;

//----------저장에 필요한 변수선언-------------//
    //Gson
    private Gson gson = new GsonBuilder().create();

    //먼저 저장소를 필드에 세팅해두자.

    //SharedPreferences (셰어드 저장소) 불러오기
    private SharedPreferences sp;
    //key에 맞는 데이터를 문자열로 변환
    String strUserInfoData;
    //UserInfoData 클래스 선언(string으로 된 json->gson->javaObject)
    UserInfoData userInfoData;

    //저장에 필요한 객체
    private Bundle bundle;


    //위치 퍼미션
    private final int GPS_ENABLE_REQUEST_CODE = 2001;
    private final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};



    //엑티비티 생성될 때
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        //내 현재 위치 받아오기 위한 퍼미션
        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {

            checkRunTimePermission();
        }

//        //툴바적용을 위해
//        Toolbar toolbar = findViewById(R.id.main_toolbar);
//        setActionBar(toolbar);
//
//        ActionBar actionBar = getSupportActionBar();

        //회원가입-로그인 후 회원정보 인텐트
        /*goMain.putExtra("email", login_email);
                    goMain.putExtra("pw", login_pw);
                    goMain.putExtra("nickname",nickname);
                    goMain.putExtra("region", region);
                    goMain.putExtra("profile", userInfo.getParcelableExtra("profile"));*/

        //회원가입, 로그인, 스타트 액티비티 에서 넘겨받은 회원정보를 인텐트로 꺼냄
        Intent userInfo = getIntent();
        email = userInfo.getStringExtra("email");

//        pw = userInfo.getStringExtra("pw");
//        nickname = userInfo.getStringExtra("nickname");
//        region = userInfo.getStringExtra("region");
//        //사진전달이 아직 안됨
//        profile = userInfo.getParcelableExtra("profile");

        callUserInfoData(email);

        nickname = userInfoData.getNickname();
        profile = Uri.parse(userInfoData.getProfile());
        region = userInfoData.getRegion();

        Log.d("메인페이지의 값 잘 들어오나 닉넴", nickname);
        Log.d("메인페이지의 값 잘 들어오나 사진", "" + profile);
        Log.d("메인페이지의 값 잘 들어오나 지역", region);


//        //번들객체 생성, text값 저장
        bundle = new Bundle();
        bundle.putString("email", email);
//        bundle.putString("pw",pw);
        bundle.putString("nickname", nickname);
//        bundle.putString("region",region);
//        //uri 넣음
        bundle.putParcelable("profile", profile);

        //fragment1로 번들 전달
        //fragment1.setArguments(bundle);
        //출처: https://everyshare.tistory.com/22?category=779150 [에브리셰어]

        //처음 화면 전환 했을 때 프래그먼트->home으로 오게.
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentLayout_main, fragmentHome).commitAllowingStateLoss();
        fragmentHome.setArguments(bundle);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch (item.getItemId()){

                    case R.id.homeItem:
                        item.setChecked(true);
                        fragmentTransaction.replace(R.id.fragmentLayout_main, fragmentHome).commitAllowingStateLoss();
                        //프래그먼트에 인텐트정보를 bundle로 변환하여 전달

                        fragmentHome.setArguments(bundle);


                        break;

                    case R.id.reviewItem:
                        item.setChecked(true);
                        fragmentTransaction.replace(R.id.fragmentLayout_main, fragmentReview).commitAllowingStateLoss();
                        fragmentReview.setArguments(bundle);

                        break;
                    case R.id.mypageItem:
                        item.setChecked(true);
                        fragmentTransaction.replace(R.id.fragmentLayout_main,fragmentMypage).commitAllowingStateLoss();
                        //리뷰 프래그먼트에서 리스너로 받아온 데이터를 마이페이지 프래그먼트에 보낸다.

//                        bundle.putString("reviewStoreName",reviewStoreName);
//                        bundle.putString("reviewContent",reviewContent);
//                        bundle.putString("reviewRegion",reviewRegion);
//                        bundle.putString("reviewCategory",reviewCategory);
//                        bundle.putFloat("reviewStarRating",reviewStarRating);
//                        bundle.putParcelable("reviewPhoto",reviewPhoto);

                        Log.d("메인엑티비티","가맹점 이름"+reviewStoreName);
                        Log.d("메인엑티비티","이미지 uri"+reviewPhoto);

                        fragmentMypage.setArguments(bundle);
                        break;
                }


                return false;
            }

    }


    //리뷰작성 프래그먼트에서 전달할 데이터 셋 인터페이스
//    @Override
//    public void reviewPlaceSet(String storeName, String content, String region, String category, Uri photo, float starRating) {
//        reviewStoreName=storeName;
//        reviewContent=content;
//        reviewRegion=region;
//        reviewCategory=category;
//        reviewStarRating=starRating;
//        reviewPhoto=photo;
//        Log.d("메인엑티비티 reviewplaceSet()","이미지 uri"+reviewPhoto);
//
//    }

//    // Fragment로 사용할 MainActivity내의 layout공간을 선택합니다.
//    public void replaceFragment(Fragment fragment) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.fragmentLayout_main, fragment).commit();
//
//    }

    //출처: https://mc10sw.tistory.com/16 [Make it possible]


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

    }
/* public boolean onOptionsItemSelected(MenuItem item){
        switch ((item.getItemId())){
            case android.R.id.home:{
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);

        }
    }*/

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            //myPage의 설정메뉴 클릭하면
            case R.id.setting_menu:
                /*설정 리스트 페이지로 이동*/
                Log.d("설정리스트 페이지 이동버튼","mainActivity");
                Intent goSettingUserInfo = new Intent(this, MypageSettingActivity.class);
                goSettingUserInfo.putExtra("email", email);
                Log.d("설정리스트 페이지 이동버튼","mainActivity"+email);
                startActivity(goSettingUserInfo);
                break;
            case android.R.id.home:{

                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }



    //위치 퍼미션 관련

    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                //위치 값을 가져올 수 있음
                ;
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                }else {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음



        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


//    public String getCurrentAddress( double latitude, double longitude) {
//
//        //지오코더... GPS를 주소로 변환
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//
//        List<Address> addresses;
//
//        try {
//            addresses = geocoder.getFromLocation(
//                    latitude,
//                    longitude,
//                    7);
//        } catch (IOException ioException) {
//            //네트워크 문제
//            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
//            return "지오코더 서비스 사용불가";
//        } catch (IllegalArgumentException illegalArgumentException) {
//            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
//            return "잘못된 GPS 좌표";
//
//        }
//
//
//
//        if (addresses == null || addresses.size() == 0) {
//            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
//            return "주소 미발견";
//
//        }
//
//        Address address = addresses.get(0);
//        return address.getAddressLine(0).toString()+"\n";
//
//    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }





}
