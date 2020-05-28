package com.example.gyeonggipay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/*내가 리뷰를 등록한 가맹점의 리스트가 나오는 페이지
*
* 여기에서는 셰어드에 저장되어 있는 데이터를 불러서 뿌려주기만 하면 된다.
*
* */

public class ReviewPlaceListActivity extends AppCompatActivity{
//implements FragmentReview.ReviewPlaceSetListener{

    RecyclerView recyclerView = null;
    ReviewPlaceItemAdapter reviewPlaceItemAdapter = null;
    ArrayList<ReviewInfoData> reviewInfoDataArrayList = new ArrayList<ReviewInfoData>();

    //java에서 쓸 변수
    private String email;
    private String reviewStoreName, reviewContent, reviewRegion, reviewCategory, nickname;
    //별점 평점
    private float starRating;
    //ArrayList<Uri> photoList; //사진 여러장
    private Uri reviewPhoto;

    private int index;

    //리뷰 프레그먼트에서 넘어온 데이터
    Intent reviewInfo;

    //셰어드
    private SharedPreferences sp;

    TextView review_toolbar_title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_place);

        //툴바
        //툴바를 이 화면의 앱바로 연결
        Toolbar tb = (Toolbar) findViewById(R.id.review_toolbar);
        setSupportActionBar(tb);

        // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //기본 타이틀 보여줄지 말지 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        review_toolbar_title = findViewById(R.id.review_toolbar_title);
        review_toolbar_title.setText("다녀온 곳");


        //리사이클러 뷰 매핑
        recyclerView = findViewById(R.id.review_recycler);
        //리사이클러뷰에 LinearLayoutManager 지정 (vertical //세로)
        //인스턴스를 만들 때 orientation에 대한 파라미터를 지정하지 않으면, orientation이 "VERTICAL"로 지정됩니다.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        loadReviewList();

        //리사이클러뷰에 어뎁터 객체 지정 => 리사이클러뷰와 나타날 아이템의 데이터를 연결시킴
        reviewPlaceItemAdapter = new ReviewPlaceItemAdapter(reviewInfoDataArrayList,ReviewPlaceListActivity.this);

        recyclerView.setAdapter(reviewPlaceItemAdapter);


        SharedPreferences sp = getSharedPreferences("login_id",MODE_PRIVATE);
        email = sp.getString("login_email","");

    }//onCreate() END



    //저장된 리뷰 객체들을 꺼내서 다시 어뎁터에 넣어주기
    @Override
    protected void onResume() {
        super.onResume();
        loadReviewList();
        reviewPlaceItemAdapter = new ReviewPlaceItemAdapter(reviewInfoDataArrayList,ReviewPlaceListActivity.this);
        Log.d("리뷰리스트 onResume", ":"+reviewInfoDataArrayList);
        recyclerView.setAdapter(reviewPlaceItemAdapter);

        reviewPlaceItemAdapter.notifyDataSetChanged();

        Log.d("onResume", "ReviewPlaceListActivity의 onResume 가 실행되었습니다. ");
    }




    //loadDate
    private void loadReviewList() {
        SharedPreferences sharedPreferences = getSharedPreferences("ReviewList", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("reviewInfoDataArrayList"+email, null);
        Type type = new TypeToken<ArrayList<ReviewInfoData>>() {}.getType();
        reviewInfoDataArrayList = gson.fromJson(json, type);
        Log.d("로드리뷰리스트",":"+reviewInfoDataArrayList);

        if (reviewInfoDataArrayList == null) {
            reviewInfoDataArrayList = new ArrayList<>();
        }


    }



    //뒤로 갈때
    @Override
    protected void onPause() {
        super.onPause();
        finish();
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


}
