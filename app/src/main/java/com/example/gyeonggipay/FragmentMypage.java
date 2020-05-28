package com.example.gyeonggipay;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;



import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.zip.Inflater;

public class FragmentMypage extends Fragment {

    View view;

    //xml요소
    //프로필사진
    ImageView mypage_profile;
    TextView mypage_nickname, mypage_lv_nickname, mypage_level, mypage_fg_toolbar_title;
    Button level_info_btn, mypage_myplace_Btn, mypage_wishplace_Btn;
    SeekBar seekBar;

    //회원정보 메인activity에서 넘어온 정보
    String region,email,pw,nickname;
    Uri profile;

//    //리뷰 프래그먼트에서 온 데이터
//    String reviewStoreName, reviewContent, reviewCategory, reviewRegion;
//    Float reviewStar;
//    Uri reviewPhoto;

    //화면전환 확인 코드
    private final int REQUEST_ReviewList = 1000;

    //Gson
    private Gson gson = new GsonBuilder().create();

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_mypage, container, false);

        //툴바를 이화면의 앱바로 연결
        Toolbar toolbar = view.findViewById(R.id.mypage_fg_toolbar);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setSupportActionBar(toolbar);

        //앱바의 여러기능을 사용하기 위해 툴바에 대한 참조를 획득할 수 있다
        //툴바에 대한 참조 획득하기
        ActionBar actionBar = mainActivity.getSupportActionBar();

        //기본타이틀 보여주지 않기
        actionBar.setDisplayShowTitleEnabled(false);
        //타이틀 설정
        TextView mypage_fg_toolbar_title = view.findViewById(R.id.mypage_fg_toolbar_title);
        mypage_fg_toolbar_title.setText("내 정보");

        //onCreateOptionMenu에서 바뀔 menu 를 승인
        setHasOptionsMenu(true);


//        //sharedPreferences
//        Context context = getActivity();
//        assert context != null;
//        SharedPreferences sp = context.getSharedPreferences("userInfo",Context.MODE_PRIVATE);

        //전달받은 bundle저장
        Bundle bundle = getArguments();
        if (bundle != null) {
            //리뷰에서 온 데이터
//            reviewRegion = bundle.getString("reviewRegion","REGION");
//            reviewCategory = bundle.getString("reviewCategory","CATEGORY");
//            reviewContent = bundle.getString("reviewContent","CONTENT");
//            reviewPhoto = bundle.getParcelable("reviewPhoto");
//            reviewStoreName = bundle.getString("reviewStoreName","STORENAME");
//            reviewStar = bundle.getFloat("reviewStarRating");

//            region = bundle.getString("region");
            email = bundle.getString("email");
//            pw = bundle.getString("pw");
//            nickname = bundle.getString("nickname",null);
//            profile =bundle.getParcelable("profile");
            //이 로그가 안찍힘
            //
//            Log.d("메인 엑티비티서 들어온 데이터","는"+ reviewContent);
//            Log.d("메인 엑티비티서 들어온 데이터","는"+ nickname);
//            Log.d("메인 엑티비티서 들어온 데이터","이미지 URI 는"+ reviewPhoto);

        }

//        String strUserInfoData = sp.getString(email, "");
//        UserInfoData userInfoData = gson.fromJson(strUserInfoData, UserInfoData.class);
//
//        nickname = userInfoData.getNickname();
//        region = userInfoData.getRegion();
//        profile = Uri.parse(userInfoData.getProfile());


        //화면과 연결
        //프로필 사진
        mypage_profile = view.findViewById(R.id.mypage_profile);
        //프로필 사진 원형모양
        mypage_profile.setBackground(new ShapeDrawable(new OvalShape()));
        mypage_profile.setClipToOutline(true);

        //닉네임
        mypage_nickname = view.findViewById(R.id.mypage_nickname);

        //리뷰 등록 갯수에 따른 레벨 이름 알려 줄 때, 닉네임 이름 들어갈 자리
        mypage_lv_nickname = view.findViewById(R.id.mypage_lv_nickname);
        //리뷰 등록 갯수에 따른 레벨 이름
        mypage_level = view.findViewById(R.id.mypage_level);
        //레벨 별 해택 보기 버튼
        level_info_btn = view.findViewById(R.id.level_info_btn);
        //다음 레벨까지 몇개의 리뷰가 남았는지 보여주는 seekBar
        seekBar = view.findViewById(R.id.seekBar);

//        //넘겨 받은 데이터를 보여주자
//        mypage_nickname.setText(nickname);
//        mypage_lv_nickname.setText(nickname);
//        mypage_profile.setImageURI(profile);


//        //회원정보 변경 페이지로 이동하는 버튼
//        //회원정보 수정 버튼 역할
//        userInfoEdit_btn = view.findViewById(R.id.userInfoEdit_btn);
//        userInfoEdit_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                /*회원 정보 수정 페이지로 이동*/
//                Intent goSettingUserInfo = new Intent(getContext(), SettingUserInfoActivity.class);
//                goSettingUserInfo.putExtra("email", email);
//                startActivity(goSettingUserInfo);
//
//            }
//        });
        //2020.05.08. 툴바의 설정 버튼으로 접근하는 걸로 변경함


        //내가 리뷰 남긴 장소 리스트 화면으로 이동하는 버튼
        mypage_myplace_Btn = view.findViewById(R.id.mypage_myplace_Btn);
        mypage_myplace_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*내가 리뷰를 등록한 가맹점의 리스트가 나온다*/
                Intent goMyplaceList = new Intent(getContext(), ReviewPlaceListActivity.class);

//                if(reviewStoreName.equals("STORENAME")){
//                    startActivity(goMyplaceList);
//                }else{
                    //리뷰프래그먼트에서 넘어온 데이터 그대로 보내기
//                    goMyplaceList.putExtra("reviewRegion",reviewRegion);
//                    goMyplaceList.putExtra("reviewStoreName",reviewStoreName);
//                    goMyplaceList.putExtra("reviewCategory",reviewCategory);
//                    goMyplaceList.putExtra("reviewContent",reviewContent);
//                    goMyplaceList.putExtra("reviewPhoto",reviewPhoto);
//                    goMyplaceList.putExtra("reviewStar",reviewStar);
//                    goMyplaceList.putExtra("nickname",nickname);
                    //startActivityForResult(goMyplaceList,REQUEST_ReviewList);
//                    Log.d("리뷰리스트로 데이터 넘기기","화면전환>>가맹점 이름: "+reviewStoreName);
//                    Log.d("리뷰리스트로 데이터 넘기기","화면전환>>닉네임: "+nickname);
//                    Log.d("리뷰리스트로 데이터 넘기기","화면전환>>이미지: "+reviewPhoto);
                    startActivity(goMyplaceList);
//                }

            }
        });

        //내가 북마크한 장소 리스트 화면으로 이동하는 버튼
        mypage_wishplace_Btn =view.findViewById(R.id.mypage_wishplace_Btn);
        mypage_wishplace_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*내가 북마크한 가맹점의 리스트가 나온다*/
                Intent goWishplaceList = new Intent(getActivity(), WishPlaceListActivity.class);

                startActivity(goWishplaceList);
            }
        });



        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(" 프래그먼트:","onActivityCreated");

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(" 프래그먼트:","onResume");
        Context context = getActivity();
        assert context != null;
        SharedPreferences sp = context.getSharedPreferences("userInfo",Context.MODE_PRIVATE);

        String strUserInfoData = sp.getString(email, "");
        UserInfoData userInfoData = gson.fromJson(strUserInfoData, UserInfoData.class);

        nickname = userInfoData.getNickname();
        region = userInfoData.getRegion();
        profile = Uri.parse(userInfoData.getProfile());

        //넘겨 받은 데이터를 보여주자
        mypage_nickname.setText(nickname);
        mypage_lv_nickname.setText(nickname);
        mypage_profile.setImageURI(profile);




    }

    //// res/menu 에서 마이페이지 탭에서 작동 할 menu를 가져온다.
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.appbar_setting_btn, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }



}
