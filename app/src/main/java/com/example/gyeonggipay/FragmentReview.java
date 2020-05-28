package com.example.gyeonggipay;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


//리뷰 작성 후 여기서 바로 어레이리스트에 넣어 저장한다.
//SharedPreferences : ReviewList.xml | key = reviewInfoDataArrayList
public class FragmentReview extends Fragment {
    View view;

    //xml 요소
    ImageView frag_review_photo,review_photo_multi_icon;
    Button frg_review_addphotoBtn, frg_review_store_searchBtn, frg_review_ok_button;
    TextView frg_review_content;//, totalNum;
    Spinner frg_review_region, frg_review_category;
    EditText frg_review_storename;
    RatingBar frg_review_ratingBar;

    //java에서 쓸 변수

    private String storeName, reviewContent, reviewRegion, reviewCategory, reviewDate;
    //별점 평점
    private float starRating;

    //가맹점
    private String address, telNum, LAT, LOGT;
    private boolean bookmark;

    //가맹점 좌표
    //위도 : LAT;
    //경도 : LOGT;


    //ArrayList<Uri> photoList; //사진 여러장
    private Uri photo;
    private Date now;

    private int photoNum;

    //지역 spinner
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;


    //가맹점 spinner
    ArrayList<String> categoryList;
    ArrayAdapter<String> categoryAdapter;



    //프로필 사진(카메라 찍기, 앨범접근, 크롭, 앨범에 저장)
    //private static final int MY_PERMISSION_CAMERA = 1111;
//    private final int REQUEST_TAKE_PHOTO = 2222;
//    private final int REQUEST_TAKE_ALBUM = 3333;
//    private final int REQUEST_IMAGE_CROP = 4444;

    //?
//    String mCurrentPhotoPath;

    //이미지uri
    Uri imageUri;
    //사진 앨범
//    Uri photoURI, albumURI;

    //회원정보 메인activity에서 넘어온 정보
    private String region,email,pw,nickname;
    private Uri profile;

    //private ReviewPlaceSetListener reviewPlaceSetListener;

    //리뷰 사진 세팅 리스트 화면으로 보내는 신호
    private int REQUEST_SETTING_PHOTO = 700;


    //----------저장에 필요한 변수선언-------------//
    //Gson
    private Gson gson = new GsonBuilder().create();

    //먼저 저장소를 필드에 세팅해두자.

    //SharedPreferences (셰어드 저장소) 불러오기
    private SharedPreferences sharedPreferences;



    //---------------리사이클러뷰 리스트 아이템 저장-------------//
    private ReviewInfoData reviewInfoData = new ReviewInfoData();

    private String item_storeName, item_storeContent, item_nickname, item_category, item_photo;
    private float item_starNum;


    private ArrayList<ReviewInfoData> reviewInfoDataArrayList = new ArrayList<>();
    private ArrayList<ReviewInfoData> placeReviewDataArrayList = new ArrayList<>();

    //셰어드에 사진들 ArrayList->JSON 저장할 때 키값

    //사진 리스트 임시저장소 : reviewPhotoList.xml
    //사진 리스트 최종저장소 : reviewPhotoListALL.xml
    //사진 arraylist => key=REVIEW_PHOTO_LIST
    //사진 갯수 int => key=REVIEW_NUM

    //대표사진 임시저장소 : reviewPhotoMain.xml
    //대표사진 최종저장소 : reviewPhotoMainAll.xml
    //사진 string => key=REVIEW_MAIN_PHOTO

    //리뷰 정보 저장소 : ReviewInfo.xml(x)
    //json 형식 -> 객체로 저장 key = 가맹점 이름 (x) 리뷰만큼 갖고 있어야 하기 때문에 객체의 arrayList로!

    private final String photoListKey = "REVIEW_PHOTO_LIST";
    private final String photoListNumKey = "REVIEW_NUM";
    private final String photoMainKey = "REVIEW_MAIN_PHOTO";


    //장소 이름 키값으로 하는 장소 중심 셰어드 저장
    //PlaceReview.xml | key값 = storeName
    //경기데이터드림 인증키
    String key = "37ec7ca432f6462e941bf2b3d5862ec0";

    int index = 1;
    int total;


    //ArrayList<ReviewPlaceItemData> list;

//
    //가맹점 리뷰데이터를  메인엑티비티에 전달할 데이터 인터페이스
//    public interface ReviewPlaceSetListener{
//
//        void reviewPlaceSet(String storeName, String reviewContent, String region, String category, Uri photo, float starRating);
//
//    }

    //각각의 프래그먼트마다 인스턴스를 반환해 줄 메소드를 생성
//    public FragmentReview newInstance(){
//        return new FragmentReview();
//    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_review, container, false);

        //툴바를 이화면의 앱바로 연결
        Toolbar toolbar = view.findViewById(R.id.review_fg_toolbar);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setSupportActionBar(toolbar);

        //앱바의 여러기능을 사용하기 위해 툴바에 대한 참조를 획득할 수 있다
        //툴바에 대한 참조 획득하기
        ActionBar actionBar = mainActivity.getSupportActionBar();
        //뒤로가기 버튼
        actionBar.setDisplayHomeAsUpEnabled(true);

        //기본타이틀 보여주지 않기
        actionBar.setDisplayShowTitleEnabled(false);
        //타이틀 설정
        TextView mypage_fg_toolbar_title = view.findViewById(R.id.review_fg_toolbar_title);
        mypage_fg_toolbar_title.setText("리뷰 작성");



//        전달받은 bundle저장
//        메인 Activity에서 넘어온 회원정보 데이터
        Bundle bundle = getArguments();
        if (bundle != null) {
            //region = bundle.getString("region");
            email = bundle.getString("email");
            //pw = bundle.getString("pw");
            nickname = bundle.getString("nickname", null);
            profile = bundle.getParcelable("profile");

            Log.d("여기는 프래그먼트 리뷰 작성 화면","가입이멜>>"+email);
            Log.d("여기는 프래그먼트 리뷰 작성 화면","가입닉넴>>"+nickname);
            Log.d("여기는 프래그먼트 리뷰 작성 화면","가입프로필>>"+profile);
        }else{
            Log.d("여기는 프래그먼트 리뷰 작성 화면","데이터 전달 노노노!!");
        }

        //화면과 연결
        //리뷰 사진
        frag_review_photo = view.findViewById(R.id.frag_review_photo);
        //totalNum = view.findViewById(R.id.totalNum);

        //리뷰 내용적기
        frg_review_content = view.findViewById(R.id.frg_review_content);

        if(frg_review_content.getText().toString().length()>0){
            SharedPreferences sp = Objects.requireNonNull(getContext()).getSharedPreferences("reviewPhotoMain",MODE_PRIVATE);
            if(sp != null && sp.contains(photoMainKey)){
                //imageUri = Uri.parse(sp.getString(photoMainKey,""));

                //Log.d("이미지uri", sp.getString(photoMainKey,""));
                //frag_review_photo.setImageURI(imageUri);
                //photo = imageUri;
            }

            //여러장일 때 표시하기 위해
            // 저장되어 있는 전체 리뷰 사진 리스트의 갯수를 파악
            sp = getContext().getSharedPreferences("reviewPhotoList",MODE_PRIVATE);
            if(sp != null && sp.contains(photoListNumKey)){
                int n = sp.getInt(photoListNumKey,0);
                Log.d("저장해둔 이미지 갯수",":"+n);
                if(n>1){
                    review_photo_multi_icon = view.findViewById(R.id.review_photo_multi_icon);
                    review_photo_multi_icon.setVisibility(View.VISIBLE);
                    review_photo_multi_icon.bringToFront();
                    //totalNum.setText(String.valueOf(n));
                }else if(n<0){
                    review_photo_multi_icon = view.findViewById(R.id.review_photo_multi_icon);
                    review_photo_multi_icon.setVisibility(View.INVISIBLE);
                }
                photoNum = n;
            }
        }



        //사진추가 버튼
        frg_review_addphotoBtn = view.findViewById(R.id.frg_review_addphotoBtn);
        frg_review_addphotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //사진 추가
                //다이얼로그 [카메라/앨범/취소]
                //ListClick(v);

                //사진추가 버튼 -> 사진추가 할 리스트 화면으로 넘어감
                Intent goSettingReviewPhoto = new Intent(getActivity(),ReviewImgListSettingActivity.class);
                //goSettingReviewPhoto.putExtra("email",email);

                startActivityForResult(goSettingReviewPhoto,REQUEST_SETTING_PHOTO);


            }
        });



        //경기도 지역 spinner
        arrayList = new ArrayList<>();
        arrayList.add("시/군 입력");
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
        arrayList.add("성남시");
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


        arrayAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                arrayList);

        //스피너
        //가맹정 지역
        frg_review_region = view.findViewById(R.id.frg_review_region);

        frg_review_region.setAdapter(arrayAdapter);
        frg_review_region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){

                }else{
                    Toast.makeText(getContext(),arrayList.get(i)+"(이)가 선택되었습니다.",
                            Toast.LENGTH_SHORT).show();
                    reviewRegion = arrayList.get(i);
                    //선택한 지역을 인텐트에 저장.
                    //userInfo.putExtra("region",region);
                    Log.i("지역선택", reviewRegion);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });//출처: https://bottlecok.tistory.com/63 [잡캐의 IT 꿀팁]




        //가맹점 spinner
        categoryList = new ArrayList<>();
        categoryList.add("카테고리 입력");
        categoryList.add("식당");
        categoryList.add("카페/제과");
        categoryList.add("문화생활");
        categoryList.add("숙박/여행");
        categoryList.add("의류/생활잡화");
        categoryList.add("서적/문구");
        categoryList.add("의료/보건");
        categoryList.add("미용/사우나/세탁소");
        categoryList.add("편의점/마트/슈퍼");
        categoryList.add("기타");

        categoryAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                categoryList);

        //가맹점 카테고리
        frg_review_category = view.findViewById(R.id.frg_review_category);
        frg_review_category.setAdapter(categoryAdapter);
        frg_review_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){

                }else{
                    Toast.makeText(getContext(),categoryList.get(i)+"(이)가 선택되었습니다.",
                            Toast.LENGTH_SHORT).show();
                    reviewCategory = categoryList.get(i);
                    //선택한 지역을 인텐트에 저장.
                    //userInfo.putExtra("region",region);
                    Log.i("선택카테고리", reviewCategory);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //categoryList.get(0);
            }
        });


        //가맹점 이름 검색
        frg_review_storename = view.findViewById(R.id.frg_review_storename);
        //가맹점 이름 검색 버튼
        frg_review_store_searchBtn = view.findViewById(R.id.frg_review_store_searchBtn);
        frg_review_store_searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("선택된 지역", reviewRegion);
                Log.d("선택된 카테고리", reviewCategory);

                if (reviewRegion.equals("시/군 입력") || reviewRegion.length()<=0) {
                    Toast.makeText(getContext(), "지역을 선택해주세요", Toast.LENGTH_LONG).show();

                } else {
                    if (reviewRegion.equals("카테고리 입력")||reviewCategory.length() <= 0) {
                        Toast.makeText(getContext(), "카테고리를 선택해주세요", Toast.LENGTH_LONG).show();
                    } else {

                        getPlaceTask();
//                        PlaceAsyncTask placeAsyncTask = new PlaceAsyncTask();
//                        placeAsyncTask.execute();
                        Toast.makeText(getContext(),"검색완료",Toast.LENGTH_SHORT).show();

                    }
                }


            }
        });


        //리뷰 평점 별점 => default = 1
        frg_review_ratingBar = view.findViewById(R.id.frg_review_ratingBar);
        //이벤트 리스너를 등록하여 해당 Rating 변경에 따라 뷰가 변경되도록
        frg_review_ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                starRating = rating;
                Log.i("별표 수 (등급)",":"+starRating);
            }
        });



        //


        //리뷰 남기기 완료 버튼
        //빈칸있는 지 검사
        //필수 정보 : 사진, 리뷰내용, 지역, 카테고리,별점 모두 채워져야 함
        frg_review_ok_button = view.findViewById(R.id.frg_review_ok_button);
        frg_review_ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이미지검사는 shared에 reviewPhotoMainAll.xml | Key = REVIEW_MAIN_PHOTO 가 있으면 OK
                SharedPreferences sp = getContext().getSharedPreferences("reviewPhotoMainAll", MODE_PRIVATE);
                if (!sp.contains(photoMainKey) || frg_review_content.getText().toString().equals("") ||
                        reviewRegion.equals("") || reviewCategory.equals("") || storeName.equals("")) {
                    Toast.makeText(getContext(), "빈 항목이 있으면 리뷰등록을 할 수 없습니다.", Toast.LENGTH_LONG).show();

                }else if (existPlaceReview() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("리뷰 등록 실패").setMessage("한 가맹점에 대한 리뷰는 한 번만 남길 수 있습니다.");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //임시저장소 삭제
                            removeStringArrayPref(photoListKey, photoListNumKey, photoMainKey);
                            reset();
                        }
                    });
                    builder.show();
                }else if(existPlaceReview() == 1) {//처음 저장
                    reviewContent = frg_review_content.getText().toString();
                    Log.i("리뷰내용은 이렇게", ":" + reviewContent);

                    //리뷰 등록 날짜
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
                    now = new Date();
                    reviewDate = simpleDateFormat.format(now);

                    //장소 정보
                    //주소, 전화번호, 위도, 경도, 북마크버튼 여부

                    Log.i("리뷰날짜", ":" + reviewDate);

                    //리뷰 객체 저장
                    //saveReviewInfoData();

                    //데이터 처음 저장일때

                    //리뷰 아이템 리스트 저장
                    reviewInfoData.setEmail(email);
                    reviewInfoData.setNickname(nickname);
                    Uri profile_uri = profile;
                    reviewInfoData.setProfile(profile_uri.toString());

                    reviewInfoData.setStoreName(storeName);
                    reviewInfoData.setStarNum(starRating);
                    reviewInfoData.setReviewContent(reviewContent);
                    reviewInfoData.setAddress(reviewRegion);
                    Log.d("리뷰남기기 프래그", reviewRegion);
                    reviewInfoData.setCategory(reviewCategory);

                    //장소 검색(api)
                    reviewInfoData.setAddress(address);
                    reviewInfoData.setTelNum(telNum);
                    reviewInfoData.setLAT(LAT);
                    reviewInfoData.setLOGT(LOGT);
                    reviewInfoData.setBookMark(bookmark);


                    reviewInfoData.setPhotoMain(photo.toString());

                    //리뷰 사진 전체 저장
                    ArrayList<String> stringArrayList = getreviewPhotoListALL(photoListKey);
                    reviewInfoData.setPhotos(stringArrayList);

                    reviewInfoData.setReviewDate(reviewDate);


                    reviewInfoDataArrayList = loadReviewList();
                    reviewInfoDataArrayList.add(0, reviewInfoData);

                    placeReviewDataArrayList = loadPlaceReview();
                    placeReviewDataArrayList.add(0, reviewInfoData);

                    //리뷰 리스트_키값으로 계정 구별
                    saveReviewList(reviewInfoDataArrayList);
                    //장소 리스트_장소이름에서 getEmail()로 계정구별
                    savePlaceReview(placeReviewDataArrayList);


                    //임시저장소 삭제
                    removeStringArrayPref(photoListKey, photoListNumKey, photoMainKey);

                    Toast.makeText(getContext(), "리뷰남기기 완료", Toast.LENGTH_SHORT).show();
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("리뷰 등록 완료").setMessage("작성하신 리뷰는 마이페이지의 다녀온 곳에서 볼 수 있습니다.\n확인하시겠습니까?");
                    builder.setPositiveButton("바로 확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //화면전환이 필요 : 칸에 적었던 내용 비워야함
                            //프래그먼트의 생명주기가 필요.. => 리스트엑티비티에서 뒤로가기 눌렀을 때 마이페이지 프래그먼트로
                            Toast.makeText(getContext(), "OK Click", Toast.LENGTH_SHORT).show();
                            Intent goMyplaceList = new Intent(getContext(), ReviewPlaceListActivity.class);

//                            goMyplaceList.putExtra("reviewRegion",reviewRegion);
//                            goMyplaceList.putExtra("reviewStoreName",storeName);
//                            goMyplaceList.putExtra("reviewCategory",reviewCategory);
//                            goMyplaceList.putExtra("reviewContent",reviewContent);
//                            goMyplaceList.putExtra("reviewPhoto",photo);
//                            goMyplaceList.putExtra("reviewStar",starRating);
                            goMyplaceList.putExtra("email", email);

                            startActivity(goMyplaceList);

                            //화면 전달하고 리셋
                            reset();
                        }
                    });
                    builder.setNegativeButton("나중에 확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //화면전환 => home 한테 가도록
                            //프래그먼트의 생명주기 필요.. => 다이얼로그 창 닫고 home프래그먼트로
                            Toast.makeText(getContext(), "no Click", Toast.LENGTH_SHORT).show();

                            //화면전달하고 리셋
                            reset();

                            //home프래그먼트
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

            }
        });
        return view;
    }

    //Activity는 ReviewPlaceSetListener를 implement한 것이여서
    //Fragment가 얻은 context를 형변환하면 리스너로 얻어올 수 있다.

    //Fragment의 라이프사이클 중 하나인 onAttach()메소드에서 얻어온 context를 사용하여 형변환 하고
    //reviewPlaceSetListener 변수에 담아놓고 사용하면 된다.
//   @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        if(context instanceof ReviewPlaceSetListener){
//            reviewPlaceSetListener = (ReviewPlaceSetListener) context;
//        }else {
//            throw new RuntimeException(context.toString()
//                    + "must implement ReviewPlaceSetListener");
//        }
//    }

//    //Fragment의 생명주기인 onDetach()때 리스너에 null값을 주어 해제하여 준다.
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        reviewPlaceSetListener = null;
//
//    }


//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        Log.d(" 프래그먼트:","onActivityCreated");
//
//    }

    int existPlaceReview(){ //한계정에는 한 장소의 리뷰만 쓸 수있다.
        reviewInfoDataArrayList = loadReviewList();
        for (int i = 0; i < reviewInfoDataArrayList.size(); i++) {
            ReviewInfoData reviewInfoData1 = new ReviewInfoData();
            reviewInfoData1 = reviewInfoDataArrayList.get(i);

            if (reviewInfoData1.getStoreName().equals(storeName)) {
                return 0;//중복됨
            }
        }
        return 1;//중복안됨
    }

    public void reset(){

        frag_review_photo.setImageURI(null);
        frg_review_content.setText("");
        frg_review_storename.setText("");
        frg_review_ratingBar.setRating(1);

        frg_review_region.setAdapter(arrayAdapter);
        frg_review_region.setSelection(0);
        frg_review_category.setAdapter(categoryAdapter);
        frg_review_category.setSelection(0);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //임시저장소 삭제
//        removeStringArrayPref(photoListKey,photoListNumKey);
//        reset();

//        if(frg_review_content.getText().toString() != null && photo.toString() != null){
//            if(frg_review_content.getText().toString().length()>0 || photo.toString().length()>0){
//                Toast.makeText(getContext(),"작성된 리뷰가 임시저장 됩니다!",Toast.LENGTH_SHORT).show();
//            }else if(frg_review_content.getText().toString().length()<=0||photo.toString().length()<=0){
//                removeStringArrayPref(photoListKey,photoListNumKey,photoMainKey);
//            }
//        }

        Log.d("온 디스트로이드 뷰","리셋되?");
    }







    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SETTING_PHOTO) {
            if (resultCode == RESULT_OK) {

                Log.d("리뷰 프래그먼트 REQUEST OK","");

                //메인사진 셰어드에 저장해둔거 가져오기
                sharedPreferences = getContext().getSharedPreferences("reviewPhotoMainAll",MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
                //이미지 uri에 저장하기
                imageUri = Uri.parse(sharedPreferences.getString(photoMainKey,""));

                Log.d("이미지uri", sharedPreferences.getString(photoMainKey,""));
                frag_review_photo.setImageURI(imageUri);
                photo = imageUri;

                //여러장일 때 표시하기 위해
                // 저장되어 있는 전체 리뷰 사진 리스트의 갯수를 파악
                sharedPreferences = getContext().getSharedPreferences("reviewPhotoListALL",MODE_PRIVATE);
                int n = sharedPreferences.getInt(photoListNumKey,0);
                Log.d("저장해둔 이미지 갯수",":"+n);
                if(n>1){
                    review_photo_multi_icon = view.findViewById(R.id.review_photo_multi_icon);
                    review_photo_multi_icon.setVisibility(View.VISIBLE);
                    review_photo_multi_icon.bringToFront();
                    //totalNum.setText(String.valueOf(n));
                }else if(n<=1){
                    review_photo_multi_icon = view.findViewById(R.id.review_photo_multi_icon);
                    review_photo_multi_icon.setVisibility(View.INVISIBLE);
                }
                photoNum = n;

            }
        }
    }

    //저장했던 리뷰사진 ArrayList->String
    private ArrayList<String> getreviewPhotoListALL(String key) {
        sharedPreferences = getContext().getSharedPreferences("reviewPhotoListALL",MODE_PRIVATE);
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

    private void removeStringArrayPref(String key, String num_key, String key2) {
        sharedPreferences = getContext().getSharedPreferences("reviewPhotoList",MODE_PRIVATE);
        String json = sharedPreferences.getString(key, null);
        Log.d("저장 >> 삭제 전", ":"+json);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
        editor.remove(num_key);
        editor.apply();

        sharedPreferences = getContext().getSharedPreferences("reviewPhotoList",MODE_PRIVATE);
        int n = sharedPreferences.getInt(num_key,9999);
        Log.d("저장 >> 삭제 후 Num", ":"+n);
        String js = sharedPreferences.getString(key, null);
        Log.d("저장 >> 삭제", ":"+js);

        sharedPreferences = getContext().getSharedPreferences("reviewPhotoMain",MODE_PRIVATE);
        //String str = sharedPreferences.getString(key2,"");
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.remove(key2);
        editor1.apply();

    }


    //리스트 저장소 불러오기
    ArrayList<ReviewInfoData> loadReviewList() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("ReviewList", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("reviewInfoDataArrayList" + email, null);
        Type type = new TypeToken<ArrayList<ReviewInfoData>>() {
        }.getType();
        reviewInfoDataArrayList = gson.fromJson(json, type);

        if (reviewInfoDataArrayList == null || reviewInfoDataArrayList.size() == 0) {
            //데이터 처음 저장일때
            reviewInfoDataArrayList = new ArrayList<>();
            return reviewInfoDataArrayList;
        }

        return reviewInfoDataArrayList;
    }

    ArrayList<ReviewInfoData> loadPlaceReview(){
        SharedPreferences sharedPreferences1 = getContext().getSharedPreferences("PlaceReview",MODE_PRIVATE);
        Gson gson1 = new Gson();
        String json1 = sharedPreferences1.getString(storeName, null);
        Type type1 = new TypeToken<ArrayList<ReviewInfoData>>() {}.getType();
        placeReviewDataArrayList = gson1.fromJson(json1, type1);

        if (placeReviewDataArrayList == null || placeReviewDataArrayList.size() == 0) {
            //데이터 처음 저장일때
            placeReviewDataArrayList = new ArrayList<>();
            return placeReviewDataArrayList;
        }

        return placeReviewDataArrayList;
    }



    /*//key에 맞는 데이터를 문자열로 변환
    String reviewInfoDataStr;
    //UserInfoData 클래스 선언(string으로 된 json->gson->javaObject)
    ReviewInfoData reviewInfoData;*/

    //saveData

    //장소별, 이메일별
    //arrraylist 리스트형식으로도 저장해야함


//    protected void saveReviewInfoData() { //리뷰 작성한 걸 객체로 저장함.
//
//        SharedPreferences sp;
//
//        ReviewInfoData reviewInfoData = new ReviewInfoData();
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
//        sp = getContext().getSharedPreferences("ReviewInfo", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
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

    //saveData
    private void saveReviewList(ArrayList<ReviewInfoData> mArrayList) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("ReviewList", MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mArrayList);
        edit.putString("reviewInfoDataArrayList"+email, json);
        edit.apply();
    }

    private void savePlaceReview(ArrayList<ReviewInfoData> mArrayList){
        SharedPreferences sharedPreferences1 = getContext().getSharedPreferences("PlaceReview",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences1.edit();
        Gson gson1 = new Gson();
        String json1 = gson1.toJson(mArrayList);
        editor.putString(storeName,json1);
        editor.apply();
    }

    //검색
    public void getPlaceTask(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    String SIGUN = reviewRegion;//EditText에 작성된 Text얻어오기
                    String STORENAME = frg_review_storename.getText().toString();

                    String queryUrl = null;

                    if(STORENAME.length()>0) { //&pIndex=" + index + "&pSize=100
                        queryUrl = "https://openapi.gg.go.kr/RegionMnyFacltStus?KEY=" + key + "&SIGUN_NM=" + SIGUN + "&CMPNM_NM=" + STORENAME;
                    }

                    String count = null;
                    String resCode = null;

                    //밖으로 뺌 -> 수정한 부분분
                   InputStream is = null;
                    try {
                        boolean storeName_b = false;
                        boolean address_road_b = false;
                        boolean address_b = false;
                        boolean category_b = false;
                        boolean telNum_b =false;
                        boolean lat_b = false;
                        boolean logt_b = false;

                        boolean totalcount = false;
                        boolean response = false;


                        URL url = new URL(queryUrl);//문자열로 된 요청 url을 URL 객체로 생성.
                        is = url.openStream(); //url위치로 입력스트림 연결

                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();//xml파싱을 위한
                        XmlPullParser xpp = factory.newPullParser();
                        xpp.setInput(new InputStreamReader(is, "UTF-8")); //inputstream 으로부터 xml 입력받기

                        String tag;


                        //xpp.next();

                        int eventType = xpp.getEventType();

                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            switch (eventType) {
                                case XmlPullParser.START_DOCUMENT: //xml 문서의 시작
                                    //buffer.append("파싱 시작...\n\n");

                                    //placeInfoDataArrayList  = new ArrayList<>();

                                    break;

                                case XmlPullParser.START_TAG:
                                    tag = xpp.getName();//테그 이름 얻어오기

                                    if(tag.equals("list_total_count"))
                                        totalcount = true;

                                    if(tag.equals("CODE"))
                                        response = true;

                                    if (tag.equals("row"))// 첫번째 검색결과
                                        //placeInfoData = new PlaceInfoData();

                                    //Log.d("객체 없어?",":"+placeInfoData);

                                    if (tag.equals("REFINE_LOTNO_ADDR"))
                                        address_b = true;

                                    if (tag.equals("REFINE_ROADNM_ADDR"))
                                        address_road_b = true;

                                    if (tag.equals("TELNO"))
                                        telNum_b = true;

                                    if(tag.equals("INDUTYPE_NM"))
                                        category_b = true;

                                    if (tag.equals("REFINE_WGS84_LAT"))
                                        lat_b = true;
                                    if (tag.equals("REFINE_WGS84_LOGT"))
                                        logt_b = true;

                                    if (tag.equals("CMPNM_NM"))
                                        storeName_b = true;
                                    break;

                                case XmlPullParser.TEXT:

                                    if(response){
                                        if(xpp.getText() != null){
                                            resCode = xpp.getText();
                                            Log.d("응답 코드:(INFO-000) 맞아?",":"+resCode);
                                        }
                                        response = false;
                                    }


                                    if(totalcount){
                                        if(xpp.getText() !=null){
                                            count = xpp.getText();
                                            Log.d("총 데이터의 값1 ",count);
                                        }
                                        totalcount=false;
                                    }

                                    //Log.d("객체 없어?22",":"+placeInfoData);
                                    if(storeName_b){
                                        if(xpp.getText() != null && xpp.getText().length()>0){
                                            storeName= xpp.getText();
                                        }else{
                                            storeName = "";
                                        }
                                        storeName_b = false;

                                    }

                                    else if(address_b){

                                        if(xpp.getText() != null && xpp.getText().length()>0){
                                            address = xpp.getText();
                                        }else{
                                            address ="";
                                        }

                                        address_b = false;
                                    }

                                    else if(address_road_b){

                                        if(xpp.getText() != null && xpp.getText().length()>0){
                                            address = xpp.getText();
                                        }else{
                                            address = "";
                                        }

                                        address_road_b = false;
                                    }

                                    else if(telNum_b){

                                        if(xpp.getText() != null && xpp.getText().length()>0){
                                            telNum = xpp.getText();
                                        }else{
                                            telNum = "";
                                        }
                                        telNum_b = false;
                                    }

                                    else if(category_b){
                                        if(xpp.getText() != null && xpp.getText().length()>0){
                                            //카테고리 분류 해서 넣기
                                            String cate = xpp.getText();
                                            if(cate.contains("숙박")||cate.contains("여행")){
                                                reviewCategory = "숙박/여행";

                                            }else if(cate.contains("레저")||cate.contains("레져")||cate.contains("문화")||cate.contains("취미")){
                                                reviewCategory = "문화생활";

                                            }else if(cate.contains("의류")||cate.contains("잡화")||cate.contains("신발")||cate.contains("시계")||cate.contains("안경")){
                                                reviewCategory = "의류/생활잡화";

                                            }else if(cate.contains("음료식품")||cate.contains("제과")){
                                                reviewCategory = "카페/음료";

                                            }else if(cate.contains("일반휴게음식")||cate.contains("한식")){
                                                reviewCategory = "식당";

                                            }else if(cate.contains("의료")||cate.contains("병원")||cate.contains("의원")||cate.contains("약국")||cate.contains("건강")){
                                                reviewCategory = "의료/보건";

                                            }else if(cate.contains("유통")){
                                                reviewCategory = "편의점/마트";

                                            }else if(cate.contains("서적")||cate.contains("문구")){
                                                reviewCategory = "서적/문구";

                                            }else if(cate.contains("화장품")||cate.contains("미용")||cate.contains("사우나")||cate.contains("세탁소")){
                                                reviewCategory = "미용/사우나/세탁소";

                                            }else if(cate.contains("학원")){
                                                reviewCategory = "학원";

                                            }else {
                                                reviewCategory = "기타";
                                            }
                                        }else{
                                            reviewCategory = "기타";
                                        }
                                        category_b = false;

                                    }
                                    else if(lat_b){

                                        if(xpp.getText() != null&& xpp.getText().length()>0){
                                            LAT = xpp.getText();
                                        }else{
                                            LAT = "";
                                        }
                                        lat_b = false;
                                    }
                                    else if(logt_b){

                                        if(xpp.getText() != null&& xpp.getText().length()>0){
                                            LOGT = xpp.getText();
                                        }else{
                                            LOGT ="";
                                        }
                                        logt_b = false;

                                    }

                                    break;

                                case XmlPullParser.END_TAG:
                                    tag = xpp.getName(); //테그 이름 얻어오기
                                    if (tag.equals("row")){ //&&placeInfoData != null){

                                        //placeInfoDataArrayList.add(placeInfoData);

                                        //buffer.append("\n");// 첫번째 검색결과종료..줄바꿈
                                    }

                                    break;
                            }

                            eventType = xpp.next();
                        }


                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if(count != null){
                        Log.i("토탈22",count);
                        total = Integer.parseInt(count);
                        Log.i("토탈 형변환 가능?",":"+total);
                    }
                    if(resCode != null){
                        Log.i("응답코드",resCode);
                        if(resCode.equals("INFO-000")){
                            Log.d("검색 : 가맹점 이름>>", storeName);
                            Toast.makeText(getContext(),"검색 : 가맹점 이름>>"+storeName,Toast.LENGTH_SHORT).show();
                            //FragmentHome.ConnectionListener connectionListener = new FragmentHome.ConnectionListener();
                            //connectionListener.responseEnd();
                        }else if(resCode.equals("INFO-200")) {
                            Toast.makeText(getContext(), "해당하는 가맹점이 없습니다.", Toast.LENGTH_LONG).show();
                            //인풋스트링 닫는다
                            is.close();
                        }

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }




        //지금 스트링 값이니까
        /*
        * private void saveData(ArrayList<MainData> mArrayList) {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedData", MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mArrayList);
        edit.putString("mainDataArrayList", json);
        edit.apply();
    }
    *
    * 이렇게 저장 틀 만들어서
String json = gson.toJson(담을 어레이리스트 값);
edit.putString("담을 어레이리스트 키",json);
        *
        *
        *
        * private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedData", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("mainDataArrayList", null);
        Type type = new TypeToken<ArrayList<MainData>>() {
        }.getType();
        mainDataArrayList = gson.fromJson(json, type);

        if (mainDataArrayList == null) {
            mainDataArrayList = new ArrayList<>();
        }
    }
        * */



//        //계정 하나당, 작성한 리뷰객체를 저장한다.
//        //저장 하는 장소 : ReviewList.xml | key = email
//        sp = getContext().getSharedPreferences("ReviewList",MODE_PRIVATE);
//        SharedPreferences.Editor edit = sp.edit();
//        //JSON으로 변환한 객체를 저장한다.
//        String keyName = "review_list";
//        Log.d("리뷰남길때 저장한 key값",keyName);
//        Log.d("리뷰남길때 저장한 지역","가맹점 이름:"+storeName);
//        Log.d("리뷰남길때 저장한 이메일","별표평점:"+starRating);
//        Log.d("리뷰남길때 저장한 닉네임","가입 이메일 : "+email);
//
//        edit.putString(keyName,strData);
//        //완료한다.
//        edit.apply();





}





//    //사진추가 버튼 누를 때, 다이얼로그
//    public void ListClick(View view) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
//            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//                File photoFile = null;
//                try {
//                    photoFile = createImageFile();
//                } catch (IOException ex) {
//                    Log.e("captureCamera Error", ex.toString());
//                }
//                if (photoFile != null) {
//
//                    // getUriForFile의 두 번째 인자는 Manifest provier의 authorites와 일치해야 함
//                    Uri providerURI = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName(), photoFile);
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
//            Toast.makeText(getActivity(), "저장공간이 접근 불가능한 기기입니다", Toast.LENGTH_SHORT).show();
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
//        getActivity().sendBroadcast(mediaScanIntent);
//        Toast.makeText(getActivity(), "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();
//    }
//
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
//
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
//                        frag_review_photo.setImageURI(imageUri);
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
//                    Toast.makeText(getActivity(), "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
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
//                    frag_review_photo.setImageURI(albumURI);
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