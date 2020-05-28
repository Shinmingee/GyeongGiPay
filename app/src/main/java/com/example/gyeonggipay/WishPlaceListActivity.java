package com.example.gyeonggipay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/*
내가 북마크한 장소의 리스트가 나오는 페이지
북마크할 때 저장된 셰어드에서 꺼내와서 뿌려주기만 하면 됨.
* */

public class WishPlaceListActivity extends AppCompatActivity {

    RecyclerView recyclerView = null;
    PlaceItemAdapter bookmarkPlaceAdapter = null;
    private ArrayList<PlaceInfoData> bookmarkPlaceInfoList = new ArrayList<PlaceInfoData>();

    TextView wish_toolbar_title;

    String login_email;

    //셰어드
    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wish_place);

        //툴바
        //툴바를 이 화면의 앱바로 연결
        Toolbar tb = (Toolbar) findViewById(R.id.wish_toolbar);
        setSupportActionBar(tb);

        // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //기본 타이틀 보여줄지 말지 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        wish_toolbar_title = findViewById(R.id.wish_toolbar_title);
        wish_toolbar_title.setText("가고싶은 곳");

        sp = getSharedPreferences("login_id", MODE_PRIVATE);
        login_email = sp.getString("login_email", "");


        //리사이클러 뷰 매핑
        recyclerView = findViewById(R.id.wish_recycler);
        //리사이클러뷰에 LinearLayoutManager 지정 (vertical //세로)
        //인스턴스를 만들 때 orientation에 대한 파라미터를 지정하지 않으면, orientation이 "VERTICAL"로 지정됩니다.

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookmarkPlaceInfoList = loadBookmarkPlaceInfo(login_email);

        //리사이클러뷰에 어뎁터 객체 지정 => 리사이클러뷰와 나타날 아이템의 데이터를 연결시킴
        bookmarkPlaceAdapter = new PlaceItemAdapter(bookmarkPlaceInfoList, WishPlaceListActivity.this);

        recyclerView.setAdapter(bookmarkPlaceAdapter);


    }//onCreate() END


    //저장된 리뷰 객체들을 꺼내서 다시 어뎁터에 넣어주기
    @Override
    protected void onResume() {
        super.onResume();
        bookmarkPlaceInfoList = loadBookmarkPlaceInfo(login_email);
        bookmarkPlaceAdapter = new PlaceItemAdapter(bookmarkPlaceInfoList, WishPlaceListActivity.this);
        Log.d("리뷰리스트 onResume", ":" + bookmarkPlaceInfoList);
        recyclerView.setAdapter(bookmarkPlaceAdapter);

        bookmarkPlaceAdapter.notifyDataSetChanged();

        Log.d("onResume", "ReviewPlaceListActivity의 onResume 가 실행되었습니다. ");
    }


//    //북마크 장소 정보 셰어드에 저장
//    private void saveBookmarkPlaceInfo(String key, ArrayList<PlaceInfoData> mArrayList){
//        SharedPreferences sharedPreferences = getSharedPreferences("BookmarkPlaceList",MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(mArrayList);
//        editor.putString(key,json);
//        editor.apply();
//    }

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
