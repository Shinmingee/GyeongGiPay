package com.example.gyeonggipay;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class FragmentHome extends Fragment{// implements PlaceItemAdapter.OnLoadMoreListener{

    //프래그먼트요소
    private View view;

    //xml요소
    LinearLayout spot_LinearLayout;
    TextView frg_search_spot, frg_search_category, textView21;
    Button frg_search_spot_btn, frg_search_category_btn, frg_search_store_btn;
    EditText frg_search_store_name;

    //넘겨받은 데이터 변수
    String region, email, nickname = null;
    Uri profile = null;

    //세부장소
    private int REQUEST_PLACE_INFO = 100;

//    //각각의 프래그먼트마다 인스턴스를 반환해 줄 메소드를 생성
//    public static FragmentHome newInstance(){
//        return new FragmentHome();
//    }

    //Gson
    private Gson gson = new GsonBuilder().create();


    //지도 파싱
    private XmlPullParser xpp;

    //내 위치 위도/경도
    private double latitude;
    private double longitude;

    //내위치 관련 변수
    GpsTracker gpsTracker;

    Intent gpsIntent = new Intent();


    //리사이클러뷰 관련
    private PlaceInfoData placeInfoData; //= new PlaceInfoData();
    private ArrayList<PlaceInfoData> placeInfoDataArrayList;// = new ArrayList<>();
    private PlaceItemAdapter placeItemAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;


    //경기데이터드림 인증키
    String key = "37ec7ca432f6462e941bf2b3d5862ec0";

    int index = 1;
    int total;

    int totalData;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);

        //sharedPreferences
        Context context = getActivity();
        assert context != null;
        SharedPreferences sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        //출처: https://unikys.tistory.com/351 [All-round programmer]


//        //전달받은 bundle저장
        Bundle bundle = getArguments();
        if (bundle != null) {
//            region = bundle.getString("region");
            email = bundle.getString("email");
//            pw = bundle.getString("pw");
//            nickname = bundle.getString("nickname");
//            profile =bundle.getParcelable("profile");
        }

        //userInfo.xml에 저장되어 있는 회원 정보 가져오기_email/region/nickname/profile
        String strUserInfoData = sp.getString(email, "");
        UserInfoData userInfoData = gson.fromJson(strUserInfoData, UserInfoData.class);

        nickname = userInfoData.getNickname();
        region = userInfoData.getRegion();
        profile = Uri.parse(userInfoData.getProfile());

//        //내 현재 위치 받아오기 위한 퍼미션
//        if (!checkLocationServicesStatus()) {
//
//            showDialogForLocationServiceSetting();
//        }else {
//
//            checkRunTimePermission();
//        }

        gpsTracker = new GpsTracker(getContext());

        //내 위치 위도/경도
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();

        gpsIntent.putExtra("latitude",latitude);
        gpsIntent.putExtra("longitude",longitude);

        String address = getCurrentAddress(latitude, longitude);
        //textview_address.setText(address);

        //Toast.makeText(getContext(), "현재위치 \n위도 " + latitude + "\n경도 " + longitude, Toast.LENGTH_LONG).show();

        //내위치 지오코딩
        TextView textView16 = view.findViewById(R.id.textView16);
        textView16.setText(address);


        //화면과 매핑

        //textView21 = view.findViewById(R.id.textView21);
        spot_LinearLayout = view.findViewById(R.id.spot_LinearLayout);

        //1. 지역
        frg_search_spot = view.findViewById(R.id.frg_search_spot);
        //지역 선택에 가입할때 받았던 지역이 default로 들어오도록
        frg_search_spot.setText(region);
        //지역 선택 버튼 : 버튼 누르면 다이얼로그로 지역 선택
        frg_search_spot_btn = view.findViewById(R.id.frg_search_spot_btn);
        frg_search_spot_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resgionList(v);
            }
        });

        //2.카데고리
        frg_search_category = view.findViewById(R.id.frg_search_category);
        //카테고리 선택 버튼 : 버튼 누르면 다이얼로그로 카테고리 선택
        frg_search_category_btn = view.findViewById(R.id.frg_search_category_btn);
        frg_search_category_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryList(v);
            }
        });


        //3.가맹점 검색
        //가맹점 이름 받는 곳
        frg_search_store_name = view.findViewById(R.id.frg_search_store_name);
        //가맹점 이름 검색버튼
        frg_search_store_btn = view.findViewById(R.id.frg_search_store_btn);
        //검색버튼 눌렀을 때 장소아이템의 레이아웃이 보여야 함.
        frg_search_store_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("선택된 지역", frg_search_spot.getText().toString());

                if (frg_search_spot.getText().toString().length() <= 0) {
                    Toast.makeText(getContext(), "지역을 선택해주세요", Toast.LENGTH_LONG).show();

                } else {
                    if (frg_search_category.getText().toString().length() <= 0) {
                        Toast.makeText(getContext(), "카테고리를 선택해주세요", Toast.LENGTH_LONG).show();
                    } else {

                        spot_LinearLayout.setVisibility(View.VISIBLE);
                        getPlaceTask();

                        //textView21.setText("검색결과("+total+")");
//                        PlaceAsyncTask placeAsyncTask = new PlaceAsyncTask();
//                        placeAsyncTask.execute();
                    }
                }

            }
        });

        recyclerView = view.findViewById(R.id.place_item_recyclerview);

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        //recyclerView.setLayoutManager(linearLayoutManager);

        /////////////////////////////////////////////////////////////////
        placeInfoDataArrayList = new ArrayList<PlaceInfoData>();
        placeItemAdapter = new PlaceItemAdapter(placeInfoDataArrayList, getContext(), onLoadMoreListener, linearLayoutManager);
        //placeItemAdapter.setLinearLayoutManager(linearLayoutManager);


        placeItemAdapter.setRecyclerView(recyclerView);
        recyclerView.setAdapter(placeItemAdapter);




        ///////////////////item mapping//////////////////////
//        //화면과 매칭
//        //장소검색시 나오는 아이템 전체 레이아웃
//        spot_LinearLayout = view.findViewById(R.id.spot_LinearLayout);
//
//        //아이템의 레이아웃
//        home_item_layout = view.findViewById(R.id.home_item_layout);
//        //레이아웃 눌렀을 때, 세부장소로 이동
//        home_item_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent goPlaceInfo = new Intent(getActivity(), PlaceInfoActivity.class);
//                startActivityForResult(goPlaceInfo, REQUEST_PLACE_INFO);
//            }
//        });
//
//
//        home_item_sectors = view.findViewById(R.id.home_item_sectors);
//
//        home_item_km = view.findViewById(R.id.home_item_km);
//
//        home_storename=view.findViewById(R.id.home_storename);
//
//        home_item_context=view.findViewById(R.id.home_item_context);
//
//        //전화번호 누르면 바로 갈 수있도록
//        home_item_phonenum=view.findViewById(R.id.home_item_phonenum);
//        home_item_phonenum.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//
//                intent.setAction(Intent.ACTION_DIAL);
//                intent.setData(Uri.parse("tel:0319992222"));
//                //Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:12345"));
//                startActivity(intent);
//
//
//                //출처: https://wowon.tistory.com/97 [원원이의 블로그]
//            }
//        });
//
//        home_item_bookmark=view.findViewById(R.id.home_item_bookmark);
//
//        home_item_setting_btn=view.findViewById(R.id.home_item_setting_btn);


        return view;
    }

    ////지역 선택 다이얼로그 : 버튼 누르면 다이얼로그로 지역 선택
    public void resgionList(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));

        builder.setTitle("시/군 선택");

        builder.setItems(R.array.home_choice_region, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                String[] items = getResources().getStringArray(R.array.home_choice_region);
                for (int i = 0; i < items.length; i++) {
                    if (pos == i) {
                        String choice_region = items[pos];
                        frg_search_spot.setText(choice_region);
                        //인텐트에 넣어야한다.
                        //번들에다가 넣어야 하나..
                        Bundle bundle = new Bundle();
                        bundle.putString("region", choice_region);
                    }
                }

                //Toast.makeText(getApplicationContext(),items[pos],Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    ///카테고리 선택 다이얼로그 : 버튼 누르면 다이얼로그로 카테고리 선택
    public void categoryList(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("카테고리 선택");

        builder.setItems(R.array.home_category, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                String[] items = getResources().getStringArray(R.array.home_category);
                for (int i = 0; i < items.length; i++) {
                    if (pos == i) {
                        String choice_cate = items[pos];
                        frg_search_category.setText(choice_cate);
//                        //인텐트에 넣어야한다.
//                        //번들에다가 넣어야 하나..
//                        Bundle bundle = new Bundle();
//                        bundle.putString("", choice_cate);
                    }
                }

                //Toast.makeText(getApplicationContext(),items[pos],Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    //세부장소로 이동 후 반환받는 응답
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PLACE_INFO) {
            if (resultCode == RESULT_OK) {

                Toast.makeText(getActivity(), "Result: " + data.getStringExtra("result"), Toast.LENGTH_SHORT).show();
            } else {   // RESULT_CANCEL
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
            }
//        } else if (requestCode == REQUEST_ANOTHER) {
//            ...
        }
    }//출처: http://zeany.net/54 [소소한 IT 이야기]


    class ConnectionListener{
        Handler handler = adapterFrontTask;
        public void responseEnd() {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    Message msg = handler.obtainMessage();
                    handler.sendMessage(msg);
                }
            }.run();
        }
    }

    public Handler adapterFrontTask = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(index == 1){

                linearLayoutManager = new LinearLayoutManager(getContext());
                placeItemAdapter = new PlaceItemAdapter(placeInfoDataArrayList, getContext(), onLoadMoreListener , linearLayoutManager);
                recyclerView.setLayoutManager(linearLayoutManager);
                Log.d("필터링",frg_search_category.getText().toString());
                //카테고리 필터
                placeItemAdapter.getFilter().filter(frg_search_category.getText().toString());

                //페이징
                placeItemAdapter.setLinearLayoutManager(linearLayoutManager);
                //placeItemAdapter.setRecyclerView(recyclerView);

                recyclerView.setAdapter(placeItemAdapter);

                placeItemAdapter.setRecyclerView(recyclerView);
//                Collections.sort(placeInfoDataArrayList, new Comparator<PlaceInfoData>() {
//                    @Override
//                    public int compare(PlaceInfoData o1, PlaceInfoData o2) {
//                        return (o1.getDistance() - o2.getDistance());
//                    }
//                });
                //notifyDataSetChanged();
                placeItemAdapter.notifyDataSetChanged();

            }
            else{
                //placeInfoDataArrayList = new ArrayList<PlaceInfoData>();
                placeItemAdapter.addItemMore(placeInfoDataArrayList);
                //카테고리 필터
                placeItemAdapter.getFilter().filter(frg_search_category.getText().toString());
//                Collections.sort(placeInfoDataArrayList, new Comparator<PlaceInfoData>() {
//                    @Override
//                    public int compare(PlaceInfoData o1, PlaceInfoData o2) {
//                        return (o1.getDistance() - o2.getDistance());
//                    }
//                });
//                placeItemAdapter.notifyDataSetChanged();

                placeItemAdapter.setMoreLoading(false);
            }
        }
    };


    public void getPlaceTask(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    String SIGUN = frg_search_spot.getText().toString();//EditText에 작성된 Text얻어오기
                    String STORENAME = frg_search_store_name.getText().toString();
                    String queryUrl;

                    if(STORENAME.length()>0){ //&pIndex="+index+"&pSize=100
                        queryUrl = "https://openapi.gg.go.kr/RegionMnyFacltStus?KEY=" + key + "&SIGUN_NM=" + SIGUN +"&CMPNM_NM="+ STORENAME;

                    }else{
                        queryUrl = "https://openapi.gg.go.kr/RegionMnyFacltStus?KEY=" + key + "&pIndex="+index+"&pSize=100&SIGUN_NM=" + SIGUN;
                    }

                    String count = null;
                    String resCode = null;
                    InputStream is = null;
                    try {
                        boolean storeName = false;
                        boolean address_road = false;
                        boolean address = false;
                        boolean category = false;
                        boolean telNum=false;
                        boolean lat = false;
                        boolean logt = false;

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

                                    placeInfoDataArrayList  = new ArrayList<>();

                                    break;

                                case XmlPullParser.START_TAG:
                                    tag = xpp.getName();//테그 이름 얻어오기

                                    if(tag.equals("list_total_count"))
                                        totalcount = true;

                                    if(tag.equals("CODE"))
                                        response = true;

                                    if (tag.equals("row"))// 첫번째 검색결과
                                        placeInfoData = new PlaceInfoData();

                                    //Log.d("객체 없어?",":"+placeInfoData);

                                    if (tag.equals("REFINE_LOTNO_ADDR"))
                                        address = true;

                                    if (tag.equals("REFINE_ROADNM_ADDR"))
                                        address_road = true;

                                    if (tag.equals("TELNO"))
                                        telNum = true;

                                    if(tag.equals("INDUTYPE_NM"))
                                        category = true;

                                    if (tag.equals("REFINE_WGS84_LAT"))
                                        lat = true;
                                    if (tag.equals("REFINE_WGS84_LOGT"))
                                        logt = true;

                                    if (tag.equals("CMPNM_NM"))
                                        storeName = true;
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

                                    Log.d("객체 없어?22",":"+placeInfoData);
                                    if(storeName){
                                        if(xpp.getText() != null && xpp.getText().length()>0){
                                            placeInfoData.setStoreName(xpp.getText());
                                        }else{
                                            placeInfoData.setStoreName("");
                                        }
                                        storeName = false;

                                    }

                                    else if(address){

                                        if(xpp.getText() != null && xpp.getText().length()>0){
                                            placeInfoData.setAddress(xpp.getText());
                                        }else{
                                            placeInfoData.setAddress("");
                                        }

                                        address = false;
                                    }

                                    else if(address_road){

                                        if(xpp.getText() != null && xpp.getText().length()>0){
                                            placeInfoData.setAddress_road(xpp.getText());
                                        }else{
                                            placeInfoData.setAddress_road("");
                                        }

                                        address_road = false;
                                    }

                                    else if(telNum){

                                        if(xpp.getText() != null && xpp.getText().length()>0){
                                            placeInfoData.setTelNum(xpp.getText());
                                        }else{
                                            placeInfoData.setTelNum("");
                                        }
                                        telNum = false;
                                    }

                                    else if(category){
                                        if(xpp.getText() != null && xpp.getText().length()>0){
                                            //카테고리 분류 해서 넣기
                                            String cate = xpp.getText();
                                            if(cate.contains("숙박")||cate.contains("여행")){
                                                placeInfoData.setCategory("숙박/여행");

                                            }else if(cate.contains("레저")||cate.contains("레져")||cate.contains("문화")||cate.contains("취미")){
                                                placeInfoData.setCategory("문화생활");

                                            }else if(cate.contains("의류")||cate.contains("잡화")||cate.contains("신발")||cate.contains("시계")||cate.contains("안경")){
                                                placeInfoData.setCategory("의류/생활잡화");

                                            }else if(cate.contains("음료식품")||cate.contains("제과")){
                                                placeInfoData.setCategory("카페/음료");

                                            }else if(cate.contains("일반휴게음식")||cate.contains("한식")){
                                                placeInfoData.setCategory("식당");

                                            }else if(cate.contains("의료")||cate.contains("병원")||cate.contains("의원")||cate.contains("약국")||cate.contains("건강")){
                                                placeInfoData.setCategory("의료/보건");

                                            }else if(cate.contains("유통")){
                                                placeInfoData.setCategory("편의점/마트");

                                            }else if(cate.contains("서적")||cate.contains("문구")){
                                                placeInfoData.setCategory("서적/문구");

                                            }else if(cate.contains("화장품")||cate.contains("미용")||cate.contains("사우나")||cate.contains("세탁소")){
                                                placeInfoData.setCategory("미용/사우나/세탁소");

                                            }else if(cate.contains("학원")){
                                                placeInfoData.setCategory("학원");

                                            }else {
                                                placeInfoData.setCategory("기타");
                                            }
                                        }else{
                                            placeInfoData.setCategory("기타");
                                        }
                                        category = false;

                                    }
                                    else if(lat){

                                        if(xpp.getText() != null&& xpp.getText().length()>0){
                                            placeInfoData.setLAT(xpp.getText());
                                        }else{
                                            placeInfoData.setLAT("");
                                        }
                                        lat = false;
                                    }
                                    else if(logt){

                                        if(xpp.getText() != null&& xpp.getText().length()>0){
                                            placeInfoData.setLOGT(xpp.getText());
                                        }else{
                                            placeInfoData.setLOGT("");
                                        }
                                        logt = false;

                                    }

                                    break;

                                case XmlPullParser.END_TAG:
                                    tag = xpp.getName(); //테그 이름 얻어오기
                                    if (tag.equals("row")&&placeInfoData != null){

                                        placeInfoDataArrayList.add(placeInfoData);

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
                            ConnectionListener connectionListener = new ConnectionListener();
                            connectionListener.responseEnd();
                        }}else if(resCode.equals("INFO-200")) {
                        Toast.makeText(getContext(), "해당하는 가맹점이 없습니다.", Toast.LENGTH_LONG).show();
                        //인풋스트링 닫는다
                        is.close();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    PlaceItemAdapter.OnLoadMoreListener onLoadMoreListener = new PlaceItemAdapter.OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            Log.d("!!!!@@@@!!!MainActiity_", "onLoadMore");
            placeItemAdapter.setProgressMore(true); //리스트 널값넣었더
            new Handler().postDelayed(new Runnable() { //핸들러 사용_딜레이~
                @Override
                public void run() {
                    placeInfoDataArrayList.clear(); //모든 배열 값을 null로 만들기, size = 0.;
                    placeItemAdapter.setProgressMore(false); //

                    ///////이부분에을 자신의 프로젝트에 맞게 설정하면 됨
                    //다음 페이지? 내용을 불러오는 부분
//                if (total % 100 == 0) { //이미 한 페이지 돌았으므로
//                    int num = (total / 100) - 1;
//                    for (int i = 1; i < num; i++) {
//                        index++;
//                        getPlaceTask();
//                    }
//                }else {//100으로 안 나눠 떨어짐. 올림한 횟수 만큼 요청해야함
//                    int num = (total / 100);
//                    Log.d("스크롤링",":"+total);
//                    for (int i = 1; i < num; i++) {
//                        index++;
//                        getPlaceTask();
//                        Log.d("스크롤링 index",":"+index);
//                    //AsyncTask의 객체는 한번 만 사용 가능하다.
//                    //placeAsyncTask.execute();
//                    }
//                }
                    index++;
                    getPlaceTask();

                    //Toast.makeText(getContext(),index+"data",Toast.LENGTH_SHORT).show();
//
                    //////////////////////////////////////////////////
                    //placeItemAdapter.addItemMore(placeInfoDataArrayList);
                    //placeItemAdapter.setMoreLoading(false);
                }
            }, 2000);
        }
    };
//    //스크롤이 끝에 도달하였을 때 실행 내용
//    @Override
//    public void onLoadMore() {
//
//    }








    //거리 계산
    //m 단위여서 필요시 변경하여 사용하시면됩니다.
    public double getDistance(double lat1 , double lng1 , double lat2 , double lng2 ){//위도,경도
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



    public String getCurrentAddress(double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(getContext(), "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(getContext(), "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(getContext(), "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString() + "\n";

    }
}



