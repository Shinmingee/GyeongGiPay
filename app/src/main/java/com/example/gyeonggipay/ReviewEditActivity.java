package com.example.gyeonggipay;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//리뷰 수정하는 곳
public class ReviewEditActivity extends AppCompatActivity {
    //xml요소
    ImageView review_edit_photo, review_edit_photo_multi_icon;
    Button review_edit_addphotoBtn, review_edit_ok_button;
    //Spinner review_edit_region, review_edit_category;
    EditText review_edit_content;
    RatingBar review_edit_ratingBar;
    TextView review_edit_region_textview,review_edit_category_textview, review_edit_storename;

    //넘어온 인텐트
    Intent formEditReview;
    //인텐트에 담겨진 position
    int position;

    //----------저장에 필요한 변수선언-------------//
    //Gson
    private Gson gson = new GsonBuilder().create();

    //먼저 저장소를 필드에 세팅해두자.

    //SharedPreferences (셰어드 저장소) 불러오기=>사진리스트
    private SharedPreferences sharedPreferences;

    //---------------리사이클러뷰 리스트 아이템 저장-------------//
    private ReviewInfoData reviewInfoData = new ReviewInfoData();

    private String item_storeName, item_storeContent, item_address, item_nickname,
            item_category, item_photo, item_profile;
    private float item_starNum;

    private ArrayList<ReviewInfoData> reviewInfoDataArrayList = new ArrayList<>();


    //ArrayList<ReviewPlaceItemData> list = new ArrayList<ReviewPlaceItemData>();
    //ReviewPlaceItemData reviewPlaceItemData = new ReviewPlaceItemData();

    //java에서 쓸 변수
    private String storeName, reviewContent, reviewRegion, reviewCategory,reviewDate;
    //별점 평점
    private float starRating;
    //ArrayList<Uri> photoList; //사진 여러장
    private Uri photo;
    private Date now;


    //회원정보 메인activity에서 넘어온 정보
    private String email, nickname;
    private Uri profile;


    //spinner
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;

    //
    //리뷰 사진 세팅 리스트 화면으로 보내는 신호
    private int REQUEST_SETTING_PHOTO = 700;

//    //프로필 사진(카메라 찍기, 앨범접근, 크롭, 앨범에 저장)
//    //private static final int MY_PERMISSION_CAMERA = 1111;
//    private static final int REQUEST_TAKE_PHOTO = 2222;
//    private static final int REQUEST_TAKE_ALBUM = 3333;
//    private static final int REQUEST_IMAGE_CROP = 4444;

    //?
    String mCurrentPhotoPath;

    //이미지uri
    Uri imageUri;
    //사진 앨범
    Uri photoURI, albumURI;


    ////////셰어드에 사진들 ArrayList->JSON 저장할 때 키값////////////////
    //사진 리스트 임시저장소 : reviewPhotoList.xml
    //사진 리스트 최종저장소 : reviewPhotoListALL.xml
    //사진 arraylist => key=REVIEW_PHOTO_LIST
    //사진 갯수 int => key=REVIEW_NUM

    //대표사진 임시저장소 : reviewPhotoMain.xml
    //대표사진 최종저장소 : reviewPhotoMainAll.xml
    //사진 string => key=REVIEW_MAIN_PHOTO

    //SharedPreferences sharedPreferences;
    private final String photoListKey = "REVIEW_PHOTO_LIST";
    private final String photoListNumKey = "REVIEW_NUM";
    private final String photoMainKey = "REVIEW_MAIN_PHOTO";
/////////////////////////////////////////////////////////////////////


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_edit);


        //xml 연결
        //툴바를 이 화면의 앱바로 연결
        Toolbar tb = (Toolbar) findViewById(R.id.review_edit_toolbar);
        setSupportActionBar(tb);

        // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //기본 타이틀 보여줄지 말지 설정

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView apptitle = findViewById(R.id.review_edit_toolbar_title);
        apptitle.setText("리뷰 수정");

        SharedPreferences sp = getSharedPreferences("login_id",MODE_PRIVATE);
        final String login_email = sp.getString("login_email","");

        //화면과 매핑
        //대표사진이 보여질 부분
        review_edit_photo = findViewById(R.id.review_edit_photo);
        //사진 여러장일때 나오는 아이콘
        review_edit_photo_multi_icon = findViewById(R.id.review_edit_photo_multi_icon);

        //사진 편집 버튼
        review_edit_addphotoBtn = findViewById(R.id.review_edit_addphotoBtn);
        //지역 선택 스피너
        //review_edit_region = findViewById(R.id.review_edit_region);
        //지역 textView
        review_edit_region_textview = findViewById(R.id.review_edit_region_textview);

        //카테고리 스피너
        //review_edit_category = findViewById(R.id.review_edit_category);
        //카테고리 textView
        review_edit_category_textview = findViewById(R.id.review_edit_category_textview);

        //리뷰
        review_edit_content = findViewById(R.id.review_edit_content);
        //가맹점 이름
        review_edit_storename = findViewById(R.id.review_edit_storename);
        //가맹점 검색버튼
        //review_edit_store_searchBtn = findViewById(R.id.review_edit_store_searchBtn);

        //평점 별점
        review_edit_ratingBar = findViewById(R.id.review_edit_ratingBar);

        //수정 완료 버튼
        review_edit_ok_button = findViewById(R.id.review_edit_ok_button);


        //로그인 되어있는 이메일, 셰어드에서 부르기 _ 객체 같이 씀 리스트 저장소 불러오기랑. 이거 되는 지 확인
        sharedPreferences = getSharedPreferences("login_id",MODE_PRIVATE);
        String email_key = sharedPreferences.getString("login_email","");

        //수정 인텐트 받아온 거 => 수정할 포지션
        formEditReview = getIntent();
        position = formEditReview.getIntExtra("position", 9999);


        //리스트 저장소 불러오기
        sharedPreferences = getSharedPreferences("ReviewList", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("reviewInfoDataArrayList"+email_key, null);
        Type type = new TypeToken<ArrayList<ReviewInfoData>>() {
        }.getType();
        reviewInfoDataArrayList = gson.fromJson(json, type);

        if (reviewInfoDataArrayList != null || reviewInfoDataArrayList.size() > 0) {
            Log.d("포지션 어뎁터에서 넘어온 인텐트", ":" + position);

            //position으로 리스트의 값 꺼내와서 변수에 매핑

            item_storeName = reviewInfoDataArrayList.get(position).getStoreName();
            item_storeContent = reviewInfoDataArrayList.get(position).getReviewContent();
            item_address = reviewInfoDataArrayList.get(position).getAddress();
            item_nickname = reviewInfoDataArrayList.get(position).getNickname();
            item_category = reviewInfoDataArrayList.get(position).getCategory();
            item_photo = reviewInfoDataArrayList.get(position).getPhotoMain();
            item_profile = reviewInfoDataArrayList.get(position).getProfile();
            item_starNum = reviewInfoDataArrayList.get(position).getStarNum();
            email = reviewInfoDataArrayList.get(position).getNickname();
            profile = Uri.parse(reviewInfoDataArrayList.get(position).getProfile());
            nickname = reviewInfoDataArrayList.get(position).getNickname();


            //대표사진 set
            review_edit_photo.setImageURI(Uri.parse(item_photo));

            //리뷰
            review_edit_content.setText(item_storeContent);

            //주소 set
            review_edit_region_textview.setText(item_address);
            //카테고리
            review_edit_category_textview.setText(item_category);
            //가맹점 이름
            review_edit_storename.setText(item_storeName);

            reviewRegion = item_address;
            reviewCategory = item_category;
            storeName = item_storeName;

            //별점 평점
            review_edit_ratingBar.setRating(item_starNum);

            //수정할 사진 shared에 저장
            // _수정할 전체 사진 arrayList => reviewPhotoList.xml key=REVIEW_PHOTO_LIST

            ArrayList<String> item_photos = reviewInfoDataArrayList.get(position).getPhotos();
            setStringArrayPref(photoListKey,photoListNumKey,item_photos);
            Log.d("처음 시작할때만 나와야하는데...",""); //이게 안나옴

            Log.d("리뷰 사진 전체 >>",":"+item_photos.toString());
            int item_photos_num = item_photos.size();
            if(item_photos_num>1){
                //이게 계속 유지가 되려나?
                //onResume 일때도 설정을 해줘야 하나.? -> test해볼것 //안됨// 왜 안되지?
                review_edit_photo_multi_icon.setVisibility(View.VISIBLE);
                review_edit_photo_multi_icon.bringToFront();
            }

            // _메인 사진 string => reviewPhotoMain.xml : key=REVIEW_MAIN_PHOTO
            sp = getSharedPreferences("reviewPhotoMain",MODE_PRIVATE);
            SharedPreferences.Editor edi = sp.edit();
            edi.putString(photoMainKey,item_photo);
            edi.apply();

//            //여러장일 때 표시하기 위해
//            // 저장되어 있는 전체 리뷰 사진 리스트의 갯수를 파악
//            sharedPreferences = getSharedPreferences("reviewPhotoListALL",MODE_PRIVATE);
//            int n = sharedPreferences.getInt(photoListNumKey,0);
//            Log.d("저장해둔 이미지 갯수",":"+n);
//            if(n>1){
//                review_edit_photo_multi_icon.setVisibility(View.VISIBLE);
//            }else if(n == 1){
//                review_edit_photo_multi_icon.setVisibility(View.INVISIBLE);
//            }


        }//포지션으로 리뷰 정보 받아와서 화면에 셋/포토들은 셰어드에 저장 END


//        list= formEditReview.getParcelableArrayListExtra("arrayList");
//        if(list!=null){
//            Log.d("수정리뷰 페이지 : 리스트",": "+list.get(0).getStoreName());
//        }
//
//
//        final int index = formEditReview.getIntExtra("index",0);
//        Log.d("수정리뷰 페이지", ":"+ index);
//
//        /*출처 : ArrayList를 객체로 만들어서 intent로 넘기기 => 안됨
//        https://hashcode.co.kr/questions/882/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C%EC%97%90%EC%84%9C-parcelable%EC%9D%B4-%EB%AD%94%EC%A7%80-%EC%9E%90%EC%84%B8%ED%9E%88-%EC%84%A4%EB%AA%85%ED%95%B4%EC%A3%BC%EC%84%B8%EC%9A%94
//        * */
//
//        //받아온 데이터를 화면에 뿌려주기
//
//        review_edit_photo.setImageURI(Uri.parse(formEditReview.getStringExtra("photo")));
//        review_edit_storename.setText(formEditReview.getStringExtra("storeName"));
//        review_edit_content.setText(formEditReview.getStringExtra("reviewContent"));
//        review_edit_ratingBar.setRating(formEditReview.getFloatExtra("starRating",0));
//
//
//        //받아온 데이터를 변수에 넣기
//        storeName = formEditReview.getStringExtra("storeName");
//        reviewContent = formEditReview.getStringExtra("reviewContent");
//        reviewRegion =  formEditReview.getStringExtra("region");
//        reviewCategory = formEditReview.getStringExtra("category");
//        nickname = formEditReview.getStringExtra("nickname");
//        photo = Uri.parse(formEditReview.getStringExtra("photo"));
//        starRating = formEditReview.getFloatExtra("starRating",0);
//
//
//
//        Log.d("수정리뷰 페이지","가맹점 이름:"+formEditReview.getStringExtra("storeName"));
//        Log.d("수정리뷰 페이지","사진 Uri:"+formEditReview.getData());


        //수정시작

//        //경기도 지역 spinner
//        arrayList = new ArrayList<>();
//        arrayList.add("거주하는 시/군 입력(필수입력)");
//        arrayList.add("가평군");
//        arrayList.add("고양시");
//        arrayList.add("과천시");
//        arrayList.add("광명시");
//        arrayList.add("광주시");
//        arrayList.add("구리시");
//        arrayList.add("군포시");
//        arrayList.add("남양주시");
//        arrayList.add("동두천시");
//        arrayList.add("부천시");
//        arrayList.add("수원시");
//        arrayList.add("안산시");
//        arrayList.add("안성시");
//        arrayList.add("안양시");
//        arrayList.add("양주시");
//        arrayList.add("양평시");
//        arrayList.add("여주시");
//        arrayList.add("연천군");
//        arrayList.add("오산시");
//        arrayList.add("용인시");
//        arrayList.add("의왕시");
//        arrayList.add("의정부시");
//        arrayList.add("이천시");
//        arrayList.add("파주시");
//        arrayList.add("평택시");
//        arrayList.add("포천시");
//        arrayList.add("하남시");
//        arrayList.add("화성시");
//
//
//        arrayAdapter = new ArrayAdapter<>(this,
//                android.R.layout.simple_spinner_dropdown_item,
//                arrayList);
//
//        //스피너
//        //가맹정 지역
//
//        review_edit_region.setAdapter(arrayAdapter);
//        review_edit_region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i == 0) {
//
//                } else {
//                    Toast.makeText(getApplicationContext(), arrayList.get(i) + "(이)가 선택되었습니다.",
//                            Toast.LENGTH_SHORT).show();
//                    reviewRegion = arrayList.get(i);
//                    //선택한 지역을 인텐트에 저장.
//                    //userInfo.putExtra("region",region);
//                    Log.i("지역선택", reviewRegion);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });//출처: https://bottlecok.tistory.com/63 [잡캐의 IT 꿀팁]


        //reviewCategory = "카테고리";


//        //가맹점 이름 검색
//        //review_edit_storename = findViewById(R.id.frg_review_storename);
//        //가맹점 이름 검색 버튼
//        review_edit_store_searchBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //검색
//                storeName = review_edit_storename.getText().toString();
//                Toast.makeText(getApplicationContext(), "검색 완료", Toast.LENGTH_SHORT).show();
//
//            }
//        });


        //이벤트 리스너를 등록하여 해당 Rating 변경에 따라 뷰가 변경되도록
        review_edit_ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                starRating = rating;
                Log.i("별표 수 (등급)", ":" + starRating);
            }
        });

        //사진수정
        review_edit_addphotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //다이얼로그 [카메라/앨범/취소]
                //ListClick(v);

                Intent goSettingReviewPhoto = new Intent(ReviewEditActivity.this, ReviewImgEditActivity.class);
                //goSettingReviewPhoto.putExtra("email",email);

                startActivityForResult(goSettingReviewPhoto, REQUEST_SETTING_PHOTO);


            }
        });


        //리뷰 남기기 완료 버튼 //리스트로 던져야해
        review_edit_ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewContent = review_edit_content.getText().toString();
                Log.i("리뷰내용은 이렇게", ":" + reviewContent);

                //이미지검사는 shared에 reviewPhotoMainAll.xml | Key = REVIEW_MAIN_PHOTO 가 있으면 OK
                SharedPreferences sp = getSharedPreferences("reviewPhotoMainAll", MODE_PRIVATE);

                if (!sp.contains(photoMainKey) || review_edit_content.getText().toString().equals("")) {
                    Toast.makeText(ReviewEditActivity.this, "빈 항목이 있으면 리뷰등록을 할 수 없습니다.", Toast.LENGTH_LONG).show();


                } else {

                    reviewContent = review_edit_content.getText().toString();
                    Log.i("리뷰내용은 이렇게", ":" + reviewContent);

                    //리뷰 등록 날짜
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
                    now = new Date();
                    reviewDate = simpleDateFormat.format(now);

                    Log.i("리뷰날짜", ":" + reviewDate);

                    //리뷰 객체 저장
                    //saveReviewInfoData();
                    //데이터 수정

                    reviewInfoDataArrayList.get(position).setStoreName(review_edit_storename.getText().toString());
                    reviewInfoDataArrayList.get(position).setCategory(review_edit_category_textview.getText().toString());
                    reviewInfoDataArrayList.get(position).setAddress(review_edit_region_textview.getText().toString());

                    reviewInfoDataArrayList.get(position).setReviewDate(reviewDate);

                    reviewInfoDataArrayList.get(position).setReviewContent(reviewContent);

                    if(photo==null){
                        photo = Uri.parse(item_photo);
                    }reviewInfoDataArrayList.get(position).setPhotoMain(photo.toString());

                    reviewInfoDataArrayList.get(position).setStarNum(starRating);

                    //리뷰 사진 전체 저장
                    ArrayList<String> stringArrayList = getreviewPhotoListALL(photoListKey);
                    reviewInfoDataArrayList.get(position).setPhotos(stringArrayList);

                    reviewInfoDataArrayList.get(position).setEmail(email);
                    reviewInfoDataArrayList.get(position).setProfile(profile.toString());
                    reviewInfoDataArrayList.get(position).setNickname(nickname);


                    saveReviewList(login_email,reviewInfoDataArrayList);



//
//                    //리뷰 아이템 리스트 저장
//                    reviewInfoData.setEmail(email);
//                    reviewInfoData.setNickname(nickname);
//                    Uri profile_uri = profile;
//                    reviewInfoData.setProfile(profile_uri.toString());
//
//                    reviewInfoData.setStoreName(storeName);
//                    reviewInfoData.setStarNum(starRating);
//                    reviewInfoData.setReviewContent(reviewContent);
//                    reviewInfoData.setAddress(reviewRegion);
//                    Log.d("리뷰남기기 프래그", reviewRegion);
//                    reviewInfoData.setCategory("업종별");
//
//                    reviewInfoData.setPhotoMain(photo.toString());

//                    //리뷰 사진 전체 저장
//                    ArrayList<String> stringArrayList = getreviewPhotoListALL(photoListKey);
//                    reviewInfoData.setPhotos(stringArrayList);
//
//                    reviewInfoData.setReviewDate(reviewDate);

                    //reviewInfoDataArrayList.add(position, reviewInfoData);




                    Toast.makeText(ReviewEditActivity.this, "리뷰남기기 완료", Toast.LENGTH_SHORT).show();
                    //임시저장소 삭제
                    removeStringArrayPref(photoListKey,photoListNumKey);

                    Log.d("프래그먼트로 데이터 전달", "가맹점 이름:" + storeName);
                    Log.d("프래그먼트로 데이터 전달", "리뷰내용:" + reviewContent);
                    Log.d("프래그먼트로 데이터 전달", "지역:" + reviewRegion);
                    Log.d("프래그먼트로 데이터 전달", "별표평점:" + starRating);
                    Log.d("프래그먼트로 데이터 전달", "사진Uri:" + photo);
                    Log.d("프래그먼트로 데이터 전달달달달달달", "가입 이메일 : " + email);

                /* Todo
              리뷰 남기기 버튼 누르면 완료되었다는 화면 띄워주고 -> 다이얼로그 띄워주기기
              면전환 어디로? 메인으로?
                */
                    //리뷰남기기 끝난 후 화면띄워주기

                    /*이 방법은 새로운 프래그먼트를 띄워주는 거라 Home에서 새 데이터를 번들로 받아오는게 다 비어있음.*/
                    // getActivity()로 MainActivity의 replaceFragment를 불러옵니다.
                    // 새로 불러올 Fragment의 Instance를 Main으로 전달
                    //((MainActivity)getActivity()).replaceFragment(FragmentHome.newInstance());
                    //출처: https://mc10sw.tistory.com/16 [Make it possible]

                    AlertDialog.Builder builder = new AlertDialog.Builder(ReviewEditActivity.this);
                    builder.setTitle("리뷰 수정 완료").setMessage("수정하신 리뷰가 저장되었습니다");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {




                            //화면전환이 필요 : 칸에 적었던 내용 비워야함
                            //프래그먼트의 생명주기가 필요.. => 리스트엑티비티에서 뒤로가기 눌렀을 때 마이페이지 프래그먼트로
                            Toast.makeText(ReviewEditActivity.this, "OK Click", Toast.LENGTH_SHORT).show();
                            Intent goMyplaceList = new Intent(ReviewEditActivity.this, ReviewPlaceListActivity.class);

//                            goMyplaceList.putExtra("reviewRegion",reviewRegion);
//                            goMyplaceList.putExtra("reviewStoreName",storeName);
//                            goMyplaceList.putExtra("reviewCategory",reviewCategory);
//                            goMyplaceList.putExtra("reviewContent",reviewContent);
//                            goMyplaceList.putExtra("reviewPhoto",photo);
//                            goMyplaceList.putExtra("reviewStar",starRating);
//                            goMyplaceList.putExtra("nickname",nickname);

                            startActivity(goMyplaceList);
                            finish();

                        }//리뷰 등록 완료_바로확인 클릭 onClick END
                    });//리뷰 등록 완료_바로확인 리스너 END
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

            }


        });

    }//onCreate() END


    public boolean onOptionsItemSelected(MenuItem item){
        switch ((item.getItemId())){
            case android.R.id.home:{

                Toast.makeText(ReviewEditActivity.this,"backButton",Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(ReviewEditActivity.this);
                builder.setTitle("[알림] 리뷰 수정 미완료")
                        .setMessage("수정하신 내용이 저장되지 않습니다.\n계속 진행하시겠습니까?")
                        .setPositiveButton("계속 진행", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeStringArrayPref(photoListKey, photoListNumKey);
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
        Toast.makeText(ReviewEditActivity.this,"backButton",Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(ReviewEditActivity.this);
        builder.setTitle("[알림] 리뷰 수정 미완료")
                .setMessage("수정하신 내용이 저장되지 않습니다.\n계속 진행하시겠습니까?")
                .setPositiveButton("계속 진행", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeStringArrayPref(photoListKey, photoListNumKey);
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



    //사진 리스트 응답
    //수정된 사진 리스트와 메인 사진 셰어드에 저장된 거 가져와서
    // 메인사진=> string/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SETTING_PHOTO) {
            if (resultCode == RESULT_OK) {

                Log.d("리뷰 수정 엑티비티 REQUEST OK","");
                sharedPreferences = getSharedPreferences("reviewPhotoList",MODE_PRIVATE);
                if(sharedPreferences.contains(photoListKey)){
                    Log.d("리뷰수정엑티비티 여기","reviewPhotoList 저장 됨?");
                }


                //메인사진 셰어드에 저장해둔거 가져오기
                sharedPreferences = getSharedPreferences("reviewPhotoMainAll",MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
                //이미지 uri에 저장하기
                imageUri = Uri.parse(sharedPreferences.getString(photoMainKey,""));

                Log.d("이미지uri", sharedPreferences.getString(photoMainKey,""));
                review_edit_photo.setImageURI(imageUri);
                photo = imageUri;

                //여러장일 때 표시하기 위해
                // 저장되어 있는 전체 리뷰 사진 리스트의 갯수를 파악
                sharedPreferences = getSharedPreferences("reviewPhotoListALL",MODE_PRIVATE);
                int n = sharedPreferences.getInt(photoListNumKey,0);
                Log.d("저장해둔 이미지 갯수",":"+n);
                if(n>1){
                    review_edit_photo_multi_icon.setVisibility(View.VISIBLE);
                    review_edit_photo_multi_icon.bringToFront();
                }else if(n == 1){
                    review_edit_photo_multi_icon.setVisibility(View.INVISIBLE);
                }
                //photoNum = n;

            }
        }
    }

    //전체사진=> arraylist<String>형식으로
    //저장했던 리뷰사진 ArrayList->String
    private ArrayList<String> getreviewPhotoListALL(String key) {
        sharedPreferences = getSharedPreferences("reviewPhotoListALL",MODE_PRIVATE);
        String json = sharedPreferences.getString(key, null);

        Log.d("리뷰사진 불러오기 함수 안",":"+json);

        ArrayList<String> urls = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }



    //수정할 리뷰 중 사진 리스트를 셰어드에  저장
    //ArrayList를 Json으로 변환하여 SharedPreferences에 String을 저장
    private void setStringArrayPref(String key, String num_key, ArrayList<String> values) {
        sharedPreferences = getSharedPreferences("reviewPhotoList",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        removeStringArrayPref(key, num_key);

        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            String item = values.get(i);
            Log.d("어뎁터 저장되는 String 값",":"+item);
            Log.d("어뎁터 저장되는 JSON 값",":"+a.put(item));
//            a.put(item);
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
            Log.d("어뎁터 저장되는 pref 값",":"+sharedPreferences);

        } else {
            editor.putString(key, null);
        }
        Log.d("어뎁터_저장되는 아템 전체 갯수",":"+values.size());
        editor.putInt(num_key, values.size());

        editor.apply();
    }


    private void removeStringArrayPref(String key, String num_key) {
        sharedPreferences = getSharedPreferences("reviewPhotoList",MODE_PRIVATE);
        String json = sharedPreferences.getString(key, null);
        Log.d("저장 >> 삭제 전", ":"+json);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
        editor.remove(num_key);
        editor.apply();

        sharedPreferences = getSharedPreferences("reviewPhotoList",MODE_PRIVATE);
        int n = sharedPreferences.getInt(num_key,9999);
        Log.d("저장 >> 삭제 후 Num", ":"+n);
        String js = sharedPreferences.getString(key, null);
        Log.d("저장 >> 삭제", ":"+js);
    }


    ///////////////셰어드 저장///////////////////////////
    //saveData
    private void saveReviewList(String login_email, ArrayList<ReviewInfoData> mArrayList) {
        SharedPreferences sharedPreferences = getSharedPreferences("ReviewList", MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mArrayList);
        edit.putString("reviewInfoDataArrayList"+login_email, json);
        edit.apply();
    }


    ////////////////객체 저장//////////////////
//    protected void saveReviewInfoData() { //리뷰 작성한 걸 객체로 저장함.
//
//        SharedPreferences sp;
//
//        ReviewInfoData reviewInfoData = new ReviewInfoData();
//
//        ArrayList<String> stringArrayList = getreviewPhotoListALL(photoListKey);
//
//
//        Log.d("셰어드로 데이터 저장", "가맹점 이름:" + storeName);
//        Log.d("셰어드로 데이터 저장", "리뷰내용:" + reviewContent);
//        Log.d("셰어드로 데이터 저장", "지역:" + reviewRegion);
//        Log.d("셰어드로 데이터 저장", "별표평점:" + starRating);
//        Log.d("셰어드로 데이터 저장", "사진Uri:" + photo);
//        Log.d("셰어드로 데이터 저장", "가입 이메일 : " + email);
//        Log.d("셰어드로 데이터 저장", "리뷰사진list:" + stringArrayList);
//        Log.d("셰어드로 데이터 저장", "작성 시간:" + reviewDate);
//
//
//        reviewInfoData.setEmail(email);
//        reviewInfoData.setNickname(nickname);
//        Uri profile_uri = profile;
//        reviewInfoData.setProfile(profile_uri.toString());
//
//        reviewInfoData.setStoreName(storeName);
//        reviewInfoData.setStarNum(starRating);
//        reviewInfoData.setReviewContent(reviewContent);
//        reviewInfoData.setAddress(reviewRegion);
//        reviewInfoData.setCategory("업종별");
//
//        reviewInfoData.setPhotoMain(photo.toString());
//        reviewInfoData.setPhotos(stringArrayList);
//
//        reviewInfoData.setReviewDate(reviewDate);
//
//
//        //JSON으로 변환
//        String strData = gson.toJson(reviewInfoData, ReviewInfoData.class);
//
//        sp = getSharedPreferences("ReviewInfo", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.remove(storeName);
//        editor.apply();
//
//        //JSON으로 변환한 객체를 저장한다.
//        String keyName_store = storeName;
//        Log.d("리뷰남길때 저장한 key값", keyName_store);
//        Log.d("리뷰남길때 저장한 지역", "가맹점 이름:" + storeName);
//        Log.d("리뷰남길때 저장한 이메일", "별표평점:" + starRating);
//        Log.d("리뷰남길때 저장한 닉네임", "가입 이메일 : " + email);
//
//        editor.putString(keyName_store, strData);
//        //완료한다.
//        editor.apply();
//
//    }



////                //리뷰에 적힌 내용을 main 엑티비티로 넘긴다
////                //mainActivity가 구현한 메소드인 reviewPlaceSet()을 이용해 액티비티 쪽으로 데이터를 보낸다.
////                reviewPlaceSetListener.reviewPlaceSet(storeName,reviewContent,reviewRegion,
////                        reviewCategory,photo,starRating);
//
//                Toast.makeText(getApplicationContext(),"리뷰남기기 완료",Toast.LENGTH_SHORT).show();
////                Log.d("프래그먼트로 데이터 전달","가맹점 이름:"+storeName);
////                Log.d("프래그먼트로 데이터 전달","리뷰내용:"+reviewContent);
////                Log.d("프래그먼트로 데이터 전달","지역:"+reviewRegion);
////                Log.d("프래그먼트로 데이터 전달","별표평점:"+starRating);
////                Log.d("프래그먼트로 데이터 전달","사진Uri:"+photo);
////                Log.d("프래그먼트로 데이터 전달달달달달달","가입 이메일 : "+email);
//
//                /* Todo
//              리뷰 남기기 버튼 누르면 완료되었다는 화면 띄워주고 -> 다이얼로그 띄워주기기
//              면전환 어디로? 메인으로?
//                */
//                //리뷰남기기 끝난 후 화면띄워주기
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(ReviewEditActivity.this);
//                builder.setTitle("리뷰 수정 완료").setMessage("작성하신 리뷰가 수정되었습니다.");
//                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        //화면전환이 필요 : 칸에 적었던 내용 비워야함
//                        //프래그먼트의 생명주기가 필요.. => 리스트엑티비티에서 뒤로가기 눌렀을 때 마이페이지 프래그먼트로
//                        Toast.makeText(getApplicationContext(), "OK Click", Toast.LENGTH_SHORT).show();
//                        Intent goMyplaceList = new Intent(getApplicationContext(), ReviewPlaceListActivity.class);
//                        goMyplaceList.putExtra("index", index);
//                        Log.d("리뷰 수정 페이지 INDEX",":"+index);
//                        goMyplaceList.putExtra("reviewRegion",reviewRegion);
//                        goMyplaceList.putExtra("reviewStoreName",storeName);
//                        goMyplaceList.putExtra("reviewCategory",reviewCategory);
//                        goMyplaceList.putExtra("reviewContent",reviewContent);
//                        goMyplaceList.putExtra("reviewPhoto",photo);
//                        Log.d("리뷰 수정 페이지 Photo Uri",": "+photo);
//                        goMyplaceList.putExtra("reviewStar",starRating);
//                        goMyplaceList.putExtra("nickname",nickname);
//
//                        goMyplaceList.putExtra("arrayList",list);
//                        Log.d("리뷰 수정페이지","리스트[0]의 가게이름:"+list.get(index).getStoreName());
//
//                        //startActivityForResult(goMyplaceList,REQUEST_OK);
//                        startActivity(goMyplaceList);
//                        finish();
//
//                    }
//                });
//
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//
//                //finish();
//
//            }
//
//        });





    //앨범으로 갈때 onPause(), onStop() 되서 안됨
//    @Override
//    protected void onPause() {
//        super.onPause();
//        finish();
//    }

//
//    //
//    @Override
//    protected void onStop() {
//        super.onStop();
//        finish();
//    }

//    //사진추가 버튼 누를 때, 다이얼로그
//    public void ListClick(View view) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(ReviewEditActivity.this);
//
//        builder.setTitle("프로필 설정하기");
//
//        builder.setItems(R.array.profile_setting, new DialogInterface.OnClickListener(){
//            @Override
//            public void onClick(DialogInterface dialog, int pos)
//            {
//                String[] items = getResources().getStringArray(R.array.profile_setting);
//                if(pos == 0){
//                    //사진촬영
//                    captureCamera();
//                    Log.i("cropImage", "photoURI : " + photoURI + " / albumURI : " + albumURI);
//                }else if(pos == 1){
//                    //앨범
//                    getAlbum();
//                    Log.i("cropImage", "photoURI : " + photoURI + " / albumURI : " + albumURI);
//
//                }else if(pos ==2){
//                    //취소
//                }
//
//                //Toast.makeText(getApplicationContext(),items[pos],Toast.LENGTH_LONG).show();
//            }
//        });
//
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//    }
//
//    //카메라 켜서 사진찍고 저장
//    private void captureCamera(){
//        String state = Environment.getExternalStorageState();
//        if (Environment.MEDIA_MOUNTED.equals(state)) {// 외장 메모리가 현재 read와 write를 할 수 있는 상태인지 확인한다
//
//
//            //암시적 인텐트 사용 : 카메라에서 이미지 캡쳐해서 반환하도록 하는 액션.
//            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//            //먼저 암시적 인텐트를 처리할 수 있는 앱이 단말에 존재하는지를 확인하기 위하여
//            //Intent 오브젝트를 사용해 resolveActivity() 메서드를 호출한다.
//            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                File photoFile = null;
//                try {
//                    photoFile = createImageFile();
//                } catch (IOException ex) {
//                    Log.e("captureCamera Error", ex.toString());
//                }
//                if (photoFile != null) {
//
//                    // getUriForFile의 두 번째 인자는 Manifest provier의 authorites와 일치해야 함
//                    Uri providerURI = FileProvider.getUriForFile(getApplicationContext(), getPackageName(), photoFile);
//                    imageUri = providerURI;
//
//                    //인텐트에 전달할 때는 FileProvier의 Return값인 content://로만!!, providerURI의 값에 카메라 데이터를 넣어 보냄
//                    //EXTRA_OUTPUT : 이미지가 작성 될 위치를 제어하기 위해 추가 EXTRA_OUTPUT을 전달할 수 있습니다.
//                    //EXTRA_OUTPUT이 있으면 전체 크기 이미지가 EXTRA_OUTPUT의 Uri 값에 기록됩니다.
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI);
//
//                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
//                }
//            }
//        } else {
//            Toast.makeText(getApplicationContext(), "저장공간이 접근 불가능한 기기입니다", Toast.LENGTH_SHORT).show();
//            return;
//        }
//    }
//
//
//    //기기에 저장될 이미지 파일경로, 폴더 만들기
//    public File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + ".jpg";
//        File imageFile = null;
//        //getExternalStorageDirectory()는 API lv29에서는 없어짐
//        //이 방법은 API 레벨 29에서 사용되지 않습니다.
//        // 사용자 개인 정보를 향상시키기 위해 공유 / 외부 저장 장치에 대한 직접 액세스는 사용되지 않습니다.
//        // 앱이 대상인 Build.VERSION_CODES.Q경우이 메소드에서 반환 된 경로는 더 이상 앱에 직접 액세스 할 수 없습니다.
//        // 앱은 다음과 같은 대안을 마이그레이션하여 공유 / 외부 저장 장치에 저장된 콘텐츠에 액세스를 계속 할 수 있습니다
//        // => Context#getExternalFilesDir(String), MediaStore또는 Intent#ACTION_OPEN_DOCUMENT.
//        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "ggp");
//        if (!storageDir.exists()) {
//            Log.i("mCurrentPhotoPath1", storageDir.toString());
//            storageDir.mkdirs();
//        }
//
//        imageFile = new File(storageDir, imageFileName);
//        mCurrentPhotoPath = imageFile.getAbsolutePath();
//
//        return imageFile;
//    }
//
//
//    //앨범에 접근하기
//    private void getAlbum(){
//        Log.i("getAlbum", "Call");
//
//        //ACTION_PICK vs ACTION_GET_CONTENT
//
//        //여기서 주의할 점은 ACTION_GET_CONTENT를 사용하라는 것!
//        //ACTION_PICK보다 공식적으로 지원하는 것이 ACTION_GET_CONTENT라는 것,
//        //ACTION_PICK은 INTENT.setAction()의 두 번째 파라미터에 들어가는 Uri의 값을 명시적 지정하여
//        //해당 Uri를 사용하는 앱을 호출할 때 사용한다.
//        //Intent intentGallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//
//        /*  Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_GET_CONTENT); // ACTION_PICK은 사용하지 말것, deprecated + formally
//            intent.setType("image/*");
//            ((Activity)mContext).startActivityForResult(Intent.createChooser(intent, "Get Album"), REQUEST_TAKE_ALBUM);
//        */
//
//        //암시적 인텐트사용
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
//        startActivityForResult(intent, REQUEST_TAKE_ALBUM);
//    }
//
//    //사진첩에 찍은 사진 저장/ 크롭한 사진 저장
//    //누가버전 이후 잘 안될 수 있음
//    private void galleryAddPic(){
//        Log.i("galleryAddPic", "Call");
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        // 해당 경로에 있는 파일을 객체화(새로 파일을 만든다는 것으로 이해하면 안 됨)
//        File f = new File(mCurrentPhotoPath);
//        Uri contentUri = Uri.fromFile(f);
//        mediaScanIntent.setData(contentUri);
//
//        sendBroadcast(mediaScanIntent);
//        Toast.makeText(getApplicationContext(), "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();
//    }
//
//    // 카메라 전용 크랍
//    public void cropImage(){
//        Log.i("cropImage", "Call");
//        Log.i("cropImage", "photoURI : " + photoURI + " / albumURI : " + albumURI);
//
//        //암시적 인텐트 카메라 크롭기능 모두(사진자르기와 포토(권한허용하면 됨) 앱이 켜짐)
//        Intent cropIntent = new Intent("com.android.camera.action.CROP");
//
//        // 50x50픽셀미만은 편집할 수 없다는 문구 처리 + 갤러리, 포토 둘다 호환하는 방법
//        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        cropIntent.setDataAndType(photoURI, "image/*");
//        cropIntent.putExtra("outputX", 120); // crop한 이미지의 x축 크기, 결과물의 크기
//        cropIntent.putExtra("outputY", 120); // crop한 이미지의 y축 크기
//        cropIntent.putExtra("aspectX", 1); // crop 박스의 x축 비율, 1&1이면 정사각형
//        cropIntent.putExtra("aspectY", 1); // crop 박스의 y축 비율
//        cropIntent.putExtra("scale", true);
//        cropIntent.putExtra("output", albumURI); // 크랍된 이미지를 해당 경로에 저장
//        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            //카메라로 사진 찍을 때
//            case REQUEST_TAKE_PHOTO:
//                if (resultCode == Activity.RESULT_OK) {
//                    try {
//                        /*Todo
//                         *  사진찍고 크롭하는 거, 인텐트에 저장되는 거*/
//                        //사진찍고 크롭할 수 있도록!!!!!!!
//                        //사진찍은 그 파일(주소? 데이터) 크롭이미지할때 넘겨줘야 함.
//                        //cropImage();
//                        Log.i("REQUEST_TAKE_PHOTO", "OK");
////                       //갤러리에 찍은 사진 저장
//                        galleryAddPic();
//
//                        //이미지 뷰에 뿌리기
//                        review_edit_photo.setImageURI(imageUri);
//                        //프래그먼트 변수에 넣기
//                        photo = imageUri;
//
//                        //인텐트에 넣어보기
//                        //userInfo.putExtra("profile", imageUri);
//
//
//
//                    } catch (Exception e) {
//                        Log.e("REQUEST_TAKE_PHOTO", e.toString());
//                    }
//                } else {
//                    Toast.makeText(getApplicationContext(), "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
//                }
//                break;
//
//            //앨범에서 선택
//            case REQUEST_TAKE_ALBUM:
//                if (resultCode == Activity.RESULT_OK) {
//
//                    if (data.getData() != null) {
//                        try {
//                            File albumFile = null;
//                            albumFile = createImageFile();
//                            photoURI = data.getData();
//                            albumURI = Uri.fromFile(albumFile);
//                            cropImage();
//                        } catch (Exception e) {
//                            Log.e("TAKE_ALBUM_SINGLE ERROR", e.toString());
//                        }
//                    }
//                }
//                break;
//
//            case REQUEST_IMAGE_CROP:
//                if (resultCode == Activity.RESULT_OK) {
//                    //galleryAddPic();
//                    review_edit_photo.setImageURI(albumURI);
//
//                    //프래그먼트 변수에 넣기
//                    photo = albumURI;
//
//                    //인텐트에 넣어보기
//                    //userInfo.putExtra("profile", albumURI);
//                }
//                break;
//        }
//    }











    }






