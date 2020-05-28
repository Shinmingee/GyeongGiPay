package com.example.gyeonggipay;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.Rating;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;

//1.홈에서 넘어올 때(인텐트로 넘어온다)
//2.내가 쓴 리뷰에서 넘어올 때(셰어드에서 검색 후 뿌려주기)
//3.북마크 한 곳에서 넘어올 때(셰어드에서 검색 후 뿌려주기)
public class PlaceInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    //xml요소
    Button place_info_infoBtn, place_info_reviewBtn;
    ImageButton place_info_goMap_btn, place_info_tel_btn, place_info_share_btn;
    ImageView place_info_bookmark_btn;
    TextView place_info_address, place_info_toolbar_title, place_info_rating_num, place_info_review_rating_num;
    RatingBar ratingBar, ratingBar2;

    private GoogleMap mMap;

    //리사이클러뷰 관련
    RecyclerView place_info_recyclerview, place_info_review_recylerview;
    PlaceReviewItemAdapter placeReviewItemAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    LinearLayout place_info_review_frame, place_info_frame;


    //1.홈에서 검색 후 넘어올 때
    Intent fromHome;
    //2.리뷰작성 후 리뷰 리스트에서 넘어올 때 / 해당 가맹점이름으로 저장되어 있는 리뷰 표시
    //3.북마크 리스트에서 넘어올 때
    SharedPreferences sharedPreferences;

    //PlaceReview.xml에 가맹점 이름으로 저장되어 있는 정보들 담는 변수
    private ArrayList<ReviewInfoData> reviewInfoDataArrayList = new ArrayList<>();

    //class에서 쓸 변수
    private String storeName, address_road, telNum, latitude, longitude, category;
    float starNum;
    private double lat;
    private double logt;
    private String email;
    private boolean bookmark;

    //북마크 저장
    private ArrayList<String> bookmarkList;
    ArrayList<PlaceInfoData> bookmarkPlaceInfoList;
    PlaceInfoData placeInfoData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_info_page);

        //툴바
        //툴바를 이 화면의 앱바로 연결
        Toolbar tb = (Toolbar) findViewById(R.id.place_info_toolbar);
        setSupportActionBar(tb);

        // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //기본 타이틀 보여줄지 말지 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        //로그인 되어있는 계정
        sharedPreferences = getSharedPreferences("login_id", MODE_PRIVATE);
        final String login_email = sharedPreferences.getString("login_email", "");

        //홈검색이든, 리뷰작성으로 들어오든! 가맹점 이름을 인텐트로 받아오기
        fromHome = getIntent();
        storeName = fromHome.getStringExtra("storeName");

        //장소셰어드 판별
        //1.셰어드에 저장되어 있는 장소면, 셰어드의 정보 꺼내서 셋.
        //2. 셰어드에 저장되어 있는 장소가 아니라면 인텐트로 넘겨서 받을 것.
        sharedPreferences = getSharedPreferences("PlaceReview", MODE_PRIVATE);
        Log.d("이게 트루야?",":"+sharedPreferences.contains(storeName));

        if (sharedPreferences.contains(storeName)) {
            Log.d("PlaceReview에 저장되어 있어?",storeName);
            //셰어드에 저장되어 있는 정보 꺼내서 셋
            loadPlaceInfo_PlaceReview();
        } else {
            address_road = fromHome.getStringExtra("address_road");
            telNum = fromHome.getStringExtra("telNum");
            latitude = fromHome.getStringExtra("latitude");
            longitude = fromHome.getStringExtra("longitude");
            category = fromHome.getStringExtra("category");
            starNum = 0.0f;
        }


        //정보프래임 안에 있는 리뷰 관련 정보
        //리뷰 평균 평점
        place_info_rating_num = findViewById(R.id.place_info_rating_num);
        place_info_rating_num.setText(String.valueOf(starNum));
        //리뷰 평균 별점
        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setRating(starNum);

        //리뷰 프래임
        //리뷰 평균 평점
        place_info_review_rating_num = findViewById(R.id.place_info_review_rating_num);
        place_info_review_rating_num.setText(String.valueOf(starNum));
        //리뷰 평균 별점
        ratingBar2 = findViewById(R.id.ratingBar2);
        ratingBar2.setRating(starNum);



        //리사이클러 뷰 매핑
        place_info_recyclerview = findViewById(R.id.place_info_recyclerview);
        place_info_review_recylerview = findViewById(R.id.place_info_review_recylerview);

        //리사이클러뷰에 LinearLayoutManager 지정 (vertical //세로)
        //인스턴스를 만들 때 orientation에 대한 파라미터를 지정하지 않으면, orientation이 "VERTICAL"로 지정됩니다.

        mLayoutManager = new LinearLayoutManager(this);
        place_info_recyclerview.setLayoutManager(mLayoutManager);
        place_info_review_recylerview.setLayoutManager(new LinearLayoutManager(this));

        if(sharedPreferences.contains(storeName)){

            loadPlaceReview_PlaceReview();
            //리사이클러뷰에 어뎁터 객체 지정 => 리사이클러뷰와 나타날 아이템의 데이터를 연결시킴
            placeReviewItemAdapter = new PlaceReviewItemAdapter(reviewInfoDataArrayList,PlaceInfoActivity.this);
            Log.d("장소 세부정보", ":"+reviewInfoDataArrayList);
            place_info_recyclerview.setAdapter(placeReviewItemAdapter);
            place_info_review_recylerview.setAdapter(placeReviewItemAdapter);

            placeReviewItemAdapter.notifyDataSetChanged();
        }

        lat = Double.parseDouble(latitude);
        logt = Double.parseDouble(longitude);

        //현재 내 위치
        GpsTracker gpsTracker = new GpsTracker(PlaceInfoActivity.this);
        //위도
        final double my_latitude = gpsTracker.getLatitude();
        //경도
        final double my_longitude = gpsTracker.getLongitude();

        double distance = getDistance(my_latitude, my_longitude, lat, logt);
        final int dis = (int) Math.round(distance);



        //북마크 여부 표시해주기
        SharedPreferences sp = getSharedPreferences("bookmarkList", MODE_PRIVATE);
        if (sp.contains(login_email)) {
            bookmarkList = getBookmarkList(login_email);
            for (int i = 0; i < bookmarkList.size(); i++) {
                String tmpBookmarkStore = bookmarkList.get(i);
                if (storeName.equals(tmpBookmarkStore)) {
                    bookmark = true;
                    break;
                }
            }

        } else {
            bookmark = false;
        }


        place_info_toolbar_title = findViewById(R.id.place_info_toolbar_title);
        place_info_toolbar_title.setText(storeName);


        //정보-리뷰 선택 버튼
        place_info_infoBtn = findViewById(R.id.place_info_infoBtn);
        place_info_reviewBtn = findViewById(R.id.place_info_reviewBtn);


        //매핑
        //장소 정보 프래임
        place_info_frame = findViewById(R.id.place_info_frame);
        //장소 리뷰 프래임
        place_info_review_frame = findViewById(R.id.place_info_review_frame);

        //지도 뷰
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.place_info_mapView);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        //메뉴_장소 정보 프래임 안에있는 메뉴
        place_info_goMap_btn = findViewById(R.id.place_info_goMap_btn);
        place_info_goMap_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        place_info_tel_btn = findViewById(R.id.place_info_tel_btn);
        place_info_tel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + telNum));
                //Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:12345"));
                startActivity(intent);
            }
        });


        place_info_bookmark_btn = findViewById(R.id.place_info_bookmark_btn);
        if (bookmark) {
            place_info_bookmark_btn.setImageResource(R.drawable.bookmark_colorfull);
        } else {
            place_info_bookmark_btn.setImageResource(R.drawable.bookmark_outline);
        }

        place_info_bookmark_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookmark) {//
                    place_info_bookmark_btn.setImageResource(R.drawable.bookmark_outline);
                    //그 계정으로 저장되어 있는 회원 정보 목록을 꺼내와서 북마크목록에 가게이름 저장
                    SharedPreferences sp = getSharedPreferences("bookmarkList", MODE_PRIVATE);
                    if (sp.contains(login_email)) {
                        bookmarkList = getBookmarkList(login_email);
                        for (int i = 0; i < bookmarkList.size(); i++) {
                            String list = bookmarkList.get(i);
                            if (list.equals(storeName)) {
                                bookmarkList.remove(i);
                                setBookmarkList(login_email, bookmarkList);
                                //Toast.makeText(PlaceInfoActivity.this, "[마이페이지 > 가고싶은 곳]에서 삭제되었습니다.", Toast.LENGTH_LONG).show();
                                break;
                            }
                        }
                        bookmarkPlaceInfoList = loadBookmarkPlaceInfo(login_email);
                        for(int i = 0; i<bookmarkPlaceInfoList.size();i++){
                            placeInfoData = new PlaceInfoData();
                            placeInfoData = bookmarkPlaceInfoList.get(i);

                            if(placeInfoData.getStoreName().equals(storeName)){
                                bookmarkPlaceInfoList.remove(i);
                                saveBookmarkPlaceInfo(login_email,bookmarkPlaceInfoList);
                                Toast.makeText(PlaceInfoActivity.this,"[마이페이지 > 가고싶은 곳]에서 삭제되었습니다.",Toast.LENGTH_LONG).show();
                                break;
                            }

                        }




                    }
                    bookmark = false;
                } else { // 안눌려져 있었을 때
                    place_info_bookmark_btn.setImageResource(R.drawable.bookmark_colorfull);
                    //눌림

                    //그 계정으로 저장되어 있는 회원 정보 목록을 꺼내와서 북마크목록에 가게이름 저장
                    SharedPreferences sp = getSharedPreferences("bookmarkList", MODE_PRIVATE);
                    if (sp.contains(login_email)) { // 추가하는 거
                        bookmarkList = getBookmarkList(login_email);
                        bookmarkList.add(0, storeName);
                        setBookmarkList(login_email, bookmarkList);

                        placeInfoData = new PlaceInfoData();
                        placeInfoData.setBookMark(true);
                        placeInfoData.setAddress_road(address_road);
                        placeInfoData.setCategory(category);
                        placeInfoData.setDistance(dis);
                        placeInfoData.setLAT(latitude);
                        placeInfoData.setLOGT(longitude);
                        placeInfoData.setStoreName(storeName);
                        placeInfoData.setTelNum(telNum);

                        bookmarkPlaceInfoList = loadBookmarkPlaceInfo(login_email);
                        bookmarkPlaceInfoList.add(placeInfoData);
                        saveBookmarkPlaceInfo(login_email,bookmarkPlaceInfoList);

                        Toast.makeText(PlaceInfoActivity.this, "[마이페이지 > 가고싶은 곳]에 저장되었습니다.", Toast.LENGTH_LONG).show();

                    } else { //처음 저장
                        bookmarkList = new ArrayList<String>();
                        bookmarkList.add(0, storeName);
                        setBookmarkList(login_email, bookmarkList);

                        bookmarkPlaceInfoList = loadBookmarkPlaceInfo(login_email);
                        bookmarkPlaceInfoList.add(placeInfoData);
                        saveBookmarkPlaceInfo(login_email,bookmarkPlaceInfoList);

                        Toast.makeText(PlaceInfoActivity.this, "[마이페이지 > 가고싶은 곳]에 저장되었습니다.", Toast.LENGTH_LONG).show();
                    }
                    bookmark = true;
                }
            }
        });


        place_info_share_btn = findViewById(R.id.place_info_share_btn);

        //장소의 주소_정보프래임 안에 있는 주소
        place_info_address = findViewById(R.id.place_info_address);
        place_info_address.setText(address_road);




        //정보 버튼
        place_info_infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //버튼 색 바뀌기
                place_info_infoBtn.setBackgroundResource(R.drawable.place_info_button);
                place_info_reviewBtn.setBackgroundResource(R.drawable.bottom_navi);
                place_info_frame.setVisibility(View.VISIBLE);
                place_info_review_frame.setVisibility(View.INVISIBLE);


            }
        });
        //리뷰 버튼
        place_info_reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                place_info_infoBtn.setBackgroundResource(R.drawable.bottom_navi);
                place_info_reviewBtn.setBackgroundResource(R.drawable.place_info_button);
                place_info_review_frame.setVisibility(View.VISIBLE);
                place_info_frame.setVisibility(View.INVISIBLE);
            }
        });

    }//onCreate() END


    //2. 리뷰작성한 장소 리스트에서 장소리스트 보기
    // 셰어드에서 장소 정보 불러서
    //리스트 저장소 불러오기
    void loadPlaceInfo_PlaceReview() {
        sharedPreferences = getSharedPreferences("PlaceReview", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(storeName, null);
        Type type = new TypeToken<ArrayList<ReviewInfoData>>() {
        }.getType();
        reviewInfoDataArrayList = gson.fromJson(json, type);

        if (reviewInfoDataArrayList != null || reviewInfoDataArrayList.size() > 0) {
            //Log.d("포지션 어뎁터에서 넘어온 인텐트", ":" + position);

            //장소이름으로 되어 있는 셰어드 값에는
            //장소에 대한 정보, 장소에 대한 리뷰, 장소에 대한 리뷰를 작성한 회원의 정보 가 있다.
            //장소에 대한 총 리뷰의 갯수는 reviewInfoDataArrayList.size() 로 파악할 수 있다.

            //장소정보에 대한 내용 셋
            address_road = reviewInfoDataArrayList.get(0).getAddress();
            telNum = reviewInfoDataArrayList.get(0).getTelNum();
            latitude = reviewInfoDataArrayList.get(0).getLAT();
            longitude = reviewInfoDataArrayList.get(0).getLOGT();
            category = reviewInfoDataArrayList.get(0).getCategory();

            ReviewInfoData reviewInfoData = new ReviewInfoData();
            float starNum_arver = 0.0f;
            
            //평균 평점 구하기
            for(int i= 0; i<reviewInfoDataArrayList.size(); i++){
                reviewInfoData = reviewInfoDataArrayList.get(i);
                float tmpStar = reviewInfoData.getStarNum();
                starNum_arver = starNum_arver + tmpStar;
            }
            starNum = starNum_arver/(reviewInfoDataArrayList.size());

        }

    }

    //3.북마크 리스트에서 넘어오는 경우



    //////////////////////////////////////////////////////////////
    //가맹점의 리뷰 정보들 리사이클러뷰에 뿌려주기
    void loadPlaceReview_PlaceReview() {
        sharedPreferences = getSharedPreferences("PlaceReview", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(storeName, null);
        Type type = new TypeToken<ArrayList<ReviewInfoData>>() {
        }.getType();
        reviewInfoDataArrayList = gson.fromJson(json, type);

        Log.d("리뷰 정보들 리사이클러뷰에 뿌려주기",storeName);
        assert json != null;
        Log.d(storeName,json);

        //여기에 저장되어 있는 리뷰의 관련 내용들을 리사이클러뷰로 보여줘야 함
        if (reviewInfoDataArrayList == null) {
            reviewInfoDataArrayList = new ArrayList<>();
        }


    }


    //북마크 장소 정보 셰어드에 저장
    private void saveBookmarkPlaceInfo(String key, ArrayList<PlaceInfoData> mArrayList){
        SharedPreferences sharedPreferences = getSharedPreferences("BookmarkPlaceList",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mArrayList);
        editor.putString(key,json);
        editor.apply();
    }

    //북마크 장소 불러오기
    //loadDate
    private ArrayList<PlaceInfoData> loadBookmarkPlaceInfo(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("BookmarkPlaceList", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        Type type = new TypeToken<ArrayList<PlaceInfoData>>() {}.getType();
        bookmarkPlaceInfoList = gson.fromJson(json, type);
        Log.d("로드리뷰리스트",":"+bookmarkPlaceInfoList);

        if (bookmarkPlaceInfoList == null) {
            bookmarkPlaceInfoList = new ArrayList<>();
            return bookmarkPlaceInfoList;
        }

        return bookmarkPlaceInfoList;
    }





    //셰어드에 저장
    //ArrayList를 Json으로 변환하여 SharedPreferences에 String을 저장
    //key값을 무엇으로 할지_여기서는 중요하지 않음 최종 저장할때가 중요
    //여기서 리뷰 작성 페이지로 넘길때 같이 넘겨야 할 중요 정보는, 대표사진의 index값.
    private void setBookmarkList(String key, ArrayList<String> values) {
        SharedPreferences sharedPreferences = getSharedPreferences("bookmarkList",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        removeBookmarkList(key);

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
        //editor.putInt(num_key, values.size());

        editor.apply();
    }
    //
    private void removeBookmarkList(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("bookmarkList",MODE_PRIVATE);
        String json = sharedPreferences.getString(key, null);
        Log.d("저장 >> 삭제 전", ":"+json);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();

        sharedPreferences = getSharedPreferences("bookmarkList",MODE_PRIVATE);

        String js = sharedPreferences.getString(key, null);
        Log.d("저장 >> 삭제", ":"+js);
    }


    //셰어드에 저장된 리스트 값 불러오기
    //SharedPreferences에서 Json형식의 String을 가져와서 다시 ArrayList로 변환
    private ArrayList<String> getBookmarkList(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("bookmarkList",MODE_PRIVATE);
        String json = sharedPreferences.getString(key, null);
        ArrayList<String> storeNames = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String storeName = a.optString(i);
                    storeNames.add(storeName);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return storeNames;
    }





    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng STORE = new LatLng(lat, logt);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(STORE);
        markerOptions.title(storeName);
        markerOptions.snippet(address_road);
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(STORE));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch ((item.getItemId())) {
            case android.R.id.home: {
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);

        }
    }

//    @Override
//    public void onBackPressed() {
//        finish();
//    }


    @Override
    protected void onResume() {
        super.onResume();

        if(sharedPreferences.contains(storeName)){

            loadPlaceReview_PlaceReview();
            //리사이클러뷰에 어뎁터 객체 지정 => 리사이클러뷰와 나타날 아이템의 데이터를 연결시킴
            placeReviewItemAdapter = new PlaceReviewItemAdapter(reviewInfoDataArrayList,PlaceInfoActivity.this);
            Log.d("장소 세부정보", ":"+reviewInfoDataArrayList);
            place_info_recyclerview.setAdapter(placeReviewItemAdapter);
            place_info_review_recylerview.setAdapter(placeReviewItemAdapter);

            placeReviewItemAdapter.notifyDataSetChanged();
        }


    }


    //거리 계산
    //m 단위여서 필요시 변경하여 사용하시면됩니다.
    public double getDistance(double lat1, double lng1, double lat2, double lng2) {//위도,경도
        double distance;

        Location locationA = new Location("point A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lng1);

        Location locationB = new Location("point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lng2);

        distance = locationA.distanceTo(locationB);

        return distance;
    }


}
