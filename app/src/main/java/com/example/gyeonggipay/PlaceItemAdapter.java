package com.example.gyeonggipay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.content.Context.MODE_PRIVATE;

//검색 내용 리사이클러뷰 어뎁터
public class PlaceItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    //아이템 클릭 리스너
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    //Context
    private Context mContext;

    //아이템 객체를 리스트로
    private ArrayList<PlaceInfoData> placeInfoDataArrayList;

    //북마크 저장
    private ArrayList<String> bookmarkList;
    private ArrayList<PlaceInfoData> bookmarkPlaceInfoList;


    //생성자에서 데이터 리스트 객체를 전달 받음
    PlaceItemAdapter(ArrayList<PlaceInfoData> list, Context context){
        placeInfoDataArrayList = list;
        mContext = context;
    }

    //생성자에서 데이터 리스트 객체를 전달 받음
    PlaceItemAdapter(ArrayList<PlaceInfoData> list, Context context, OnLoadMoreListener onLoadMoreListener, LinearLayoutManager linearLayoutManager) {
        placeInfoDataArrayList = list;
        mContext = context;
        this.onLoadMoreListener = onLoadMoreListener;
        this.mLinearLayoutManager = linearLayoutManager;
    }

    //페이징
    private OnLoadMoreListener onLoadMoreListener;
    private LinearLayoutManager mLinearLayoutManager;

    private final int VIEW_TYPE_NORMAL = 1;
    private final int VIEW_TYPE_LOADING = 0;

    private boolean isMoreLoading = false;
    private int visibleThreshold = 0;
    private int firstVisibleItem, visibleItemCount, totalItemCount, lastVisibleItem;

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setLinearLayoutManager(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }


    //현 위치
    //내위치 관련 변수
    private GpsTracker gpsTracker;

    //버튼과 아이템 뷰 클릭 리스너

    //onCreateViewHolder() : 아이템 뷰를 위한 뷰 홀더 객체 생성하여 리턴
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Context에서 리사이클러뷰 화면의 context를 가져와
        Context context = parent.getContext();
        //xml 레이아웃을 inflater로 객체로 만들꺼야.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (viewType == VIEW_TYPE_NORMAL) {//view_item 이 뷰 타입이랑 같으면 => 아마도 아이템이 표시될 때
            //뷰에 아이템 레이아웃을 객체로 만들어서 담을 꺼야.
            View view = inflater.inflate(R.layout.place_item, parent, false);
            //어뎁터에 정의 되어 있는 대로 레이아웃 xml의 요소와 화면 구성을 연결시킨다.
            PlaceItemAdapter.PlaceViewHolder vh = new PlaceItemAdapter.PlaceViewHolder(view);

            //return new StudentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, parent, false));
            //연결된 뷰를 반환한다.
            return vh;

        } else { //아마도 아이템이 끝에 도달했을 때

            //뷰에 아이템 레이아웃을 객체로 만들어서 담을 꺼야.
            View view = inflater.inflate(R.layout.place_search_progress, parent, false);
            //어뎁터에 정의 되어 있는 대로 레이아웃 xml의 요소와 화면 구성을 연결시킨다.
            PlaceItemAdapter.ProgressViewHolder pvh = new PlaceItemAdapter.ProgressViewHolder(view);

            //return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false));
            //연결된 뷰를 반환한다.
            return pvh;
        }
    }

    //onBindViewHolder() : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시한다.
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof PlaceViewHolder) {

            final PlaceInfoData placeInfoData = placeInfoDataArrayList.get(position);

            Log.d("어뎁터 온바인드뷰홀더 포지션", ":" + position);

            ((PlaceViewHolder) holder).place_storename.setText(placeInfoData.getStoreName());
            ((PlaceViewHolder) holder).place_item_sectors.setText(placeInfoData.getCategory());

            //holder.place_address.setText(placeInfoData.getAddress());
            ((PlaceViewHolder) holder).place_address_road.setText(placeInfoData.getAddress_road());

            //현재 내 위치
            gpsTracker = new GpsTracker(mContext);

            //위도
            double latitude = gpsTracker.getLatitude();
            //경도
            double longitude = gpsTracker.getLongitude();

//        Log.d("어뎁터에서 내 위치(위도)", ":"+latitude);
//        Log.d("어뎁터에서 내 위치(경도)", ":"+longitude);
//
//        Log.d("어뎁터에서 가맹점 위치(위도)", ":"+placeInfoData.getLAT());
//        Log.d("어뎁터에서 가맹점 위치(경도)", ":"+placeInfoData.getLOGT());
//
//        Log.d("어뎁터에서(위도 비었어?isEmpty)", ":"+placeInfoData.getLAT().isEmpty());
//        Log.d("어뎁터에서(위도 비었어?null)", ":"+(placeInfoData.getLAT()==null));
//        Log.d("어뎁터에서(위도 비었어?equals)", ":"+(placeInfoData.getLAT().equals("")));

            //***알아야할 사항***
            //비어있는 곳은 길이가 5 / 위도 경도가 나오면 길이가 12 | placeInfoData.getLAT().isEmpty()=false(비어있어도)

            //Log.d("어뎁터에서(위도 비었어?)", ":"+(placeInfoData.getLAT().length()));


            if (placeInfoData.getLAT().length() < 12) {
                ((PlaceViewHolder) holder).place_distance.setText("거리좌표 미등록");
                Log.d("어뎁터에서 가맹점 위치 없을 때", ":거리좌표 미등록");
            } else {
                double distance = getDistance(latitude, longitude, Double.parseDouble(placeInfoData.getLAT()), Double.parseDouble(placeInfoData.getLOGT()));
                int dis = (int) Math.round(distance);
                //Log.d("어뎁터에서 가맹점까지의 거리", ":"+distance);
                //Log.d("어뎁터에서 내 위치",":"+latitude);
                Log.d("어뎁터에서 가맹점 이름", ":" + placeInfoData.getStoreName());
                Log.d("어뎁터에서 가맹점까지의 거리", ":" + dis);
                //현재 위치와 가맹점의 위도경도를 이용한 거리 측정
                //위도:placeInfoData.getLAT()
                //경도:placeInfoData.getLOGT()
                //placeInfoData.setDistance(distance);
                if (dis >= 1000) {
                    ((PlaceViewHolder) holder).place_distance.setText((dis / 1000) + "." + (dis % 1000) + "km");
                } else {
                    ((PlaceViewHolder) holder).place_distance.setText(dis + "m");
                }
                placeInfoData.setDistance(dis);
            }

            ((PlaceViewHolder) holder).place_telnum.setText(placeInfoData.getTelNum());
            //리뷰의 갯수
            //가맹점의 이름으로 남겨진 리뷰의 총 갯수를 파악
            //((PlaceViewHolder) holder).place_reviewNum.setText("");

            Log.d("어뎁터 홀더 가게이름", ":" + placeInfoData.getStoreName());

            ((PlaceViewHolder) holder).itemView.setTag(position);

            //로그인 되어 있는 계정을 불러온다
            SharedPreferences sp = mContext.getSharedPreferences("login_id", MODE_PRIVATE);
            String login_email = sp.getString("login_email","");

            //그 계정으로 저장되어 있는 회원 정보 목록을 꺼내와서 북마크목록에 가게이름 저장
//            sp = mContext.getSharedPreferences("bookmarkList", MODE_PRIVATE);
//            if(sp.contains(login_email)) {
//
//                ((PlaceViewHolder) holder).place_item_bookmark.setImageResource(R.drawable.bookmark_colorfull);
//                placeInfoData.setBookMark(true);
//
//            }else{

            ((PlaceViewHolder) holder).place_item_bookmark.setImageResource(R.drawable.bookmark_outline);
                //placeInfoData.setBookMark(false);

            //}

            final String tmpStoreName = placeInfoData.getStoreName();
            //북마크버튼 표시되어 있는지
            ((PlaceViewHolder) holder).place_item_bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (placeInfoData.isBookMark()) {//눌러져있으면 => true
                        placeInfoData.setBookMark(false);
                        ((PlaceViewHolder) holder).place_item_bookmark.setImageResource(R.drawable.bookmark_outline);

                        //로그인 되어 있는 계정을 불러온다
                        SharedPreferences sp = mContext.getSharedPreferences("login_id", MODE_PRIVATE);
                        String login_email = sp.getString("login_email","");

                        //그 계정으로 저장되어 있는 회원 정보 목록을 꺼내와서 북마크목록에 가게이름 저장
                        sp = mContext.getSharedPreferences("bookmarkList", MODE_PRIVATE);
                        if(sp.contains(login_email)){
                            bookmarkList = getBookmarkList(login_email);
                            for(int i = 0; i<bookmarkList.size(); i++){
                                String list = bookmarkList.get(i);
                                if(list.equals(tmpStoreName)){
                                    bookmarkList.remove(i);
                                    setBookmarkList(login_email,bookmarkList);
                                    //Toast.makeText(mContext,"[마이페이지 > 가고싶은 곳]에서 삭제되었습니다.",Toast.LENGTH_LONG).show();
                                    break;
                                }
                            }
                        }
                        bookmarkPlaceInfoList = loadBookmarkPlaceInfo(login_email);
                        for(int i = 0; i<bookmarkPlaceInfoList.size();i++){
                            PlaceInfoData placeInfoData1 = new PlaceInfoData();
                            placeInfoData1 = bookmarkPlaceInfoList.get(i);

                            if(placeInfoData1.getStoreName().equals(tmpStoreName)){
                                bookmarkPlaceInfoList.remove(i);
                                saveBookmarkPlaceInfo(login_email,bookmarkPlaceInfoList);
                                Toast.makeText(mContext,"[마이페이지 > 가고싶은 곳]에서 삭제되었습니다.",Toast.LENGTH_LONG).show();
                                break;
                            }

                        }

                    } else {//안눌러져있으면
                        placeInfoData.setBookMark(true);
                        ((PlaceViewHolder) holder).place_item_bookmark.setImageResource(R.drawable.bookmark_colorfull);
                        //여기서 계정별 북마크 목록이 저장되어야 한다.

                        //로그인 되어 있는 계정을 불러온다
                        SharedPreferences sp = mContext.getSharedPreferences("login_id", MODE_PRIVATE);
                        String login_email = sp.getString("login_email","");

                        //그 계정으로 저장되어 있는 회원 정보 목록을 꺼내와서 북마크목록에 가게이름 저장
                        sp = mContext.getSharedPreferences("bookmarkList", MODE_PRIVATE);
                        if(sp.contains(login_email)){ // 추가하는 거
                            bookmarkList = getBookmarkList(login_email);
                            bookmarkList.add(0,tmpStoreName);
                            setBookmarkList(login_email,bookmarkList);

                            bookmarkPlaceInfoList = loadBookmarkPlaceInfo(login_email);
                            bookmarkPlaceInfoList.add(placeInfoData);
                            saveBookmarkPlaceInfo(login_email,bookmarkPlaceInfoList);

                            Toast.makeText(mContext,"[마이페이지 > 가고싶은 곳]에 저장되었습니다.",Toast.LENGTH_LONG).show();

                        }else{ //처음 저장
                            bookmarkList = new ArrayList<String>();
                            bookmarkList.add(0,tmpStoreName);
                            setBookmarkList(login_email,bookmarkList);

                            bookmarkPlaceInfoList = loadBookmarkPlaceInfo(login_email);
                            bookmarkPlaceInfoList.add(placeInfoData);
                            saveBookmarkPlaceInfo(login_email,bookmarkPlaceInfoList);

                            Toast.makeText(mContext,"[마이페이지 > 가고싶은 곳]에 저장되었습니다.",Toast.LENGTH_LONG).show();
                        }

                    }
                }
            });

            //아이템 눌렀을 때
            ((PlaceViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String storeName = placeInfoData.getStoreName();
                    String address_road = placeInfoData.getAddress_road();
                    String telNum = placeInfoData.getTelNum();
                    String latitude = placeInfoData.getLAT();
                    String longitude = placeInfoData.getLOGT();
                    String category = placeInfoData.getCategory();
                    boolean bookmark = placeInfoData.getBookMark();

                    Intent goPlaceInfo = new Intent(mContext, PlaceInfoActivity.class);
                    goPlaceInfo.putExtra("storeName", storeName);
                    goPlaceInfo.putExtra("address_road", address_road);
                    goPlaceInfo.putExtra("telNum", telNum);
                    goPlaceInfo.putExtra("latitude", latitude);
                    goPlaceInfo.putExtra("longitude", longitude);
                    goPlaceInfo.putExtra("category", category);
                    goPlaceInfo.putExtra("bookmark", bookmark);

                    mContext.startActivity(goPlaceInfo);

                    Log.d("아이템 클릭시 가맹점 이름", storeName);


                }
            });


        }
    }

    //getItemCount() : 전체 데이터 갯수 리턴
//    @Override
//    public int getItemCount() {
//        return placeInfoDataArrayList.size();
//    }
    @Override
    public int getItemCount() {
        if (placeInfoDataArrayList.size() > 0) {
            return placeInfoDataArrayList.size();
        } else {
            return 0;
        }
    }


    //아이템 뷰를 저장하는 뷰홀더 클래스
    public class PlaceViewHolder extends RecyclerView.ViewHolder {

        TextView place_storename, place_reviewNum, place_item_sectors, place_address, place_address_road,
                place_distance, place_telnum;

        ImageView place_item_bookmark;


        public PlaceViewHolder(@NonNull final View itemView) {
            super(itemView);

            //뷰 객체에 대한 참조(연결)
            //가맹점 이름
            place_storename = itemView.findViewById(R.id.place_storename);
            //별로 준 평점(소수점 한자리수)
            place_telnum = itemView.findViewById(R.id.place_telnum);
            //가맹점 카테고리 이름
            place_item_sectors = itemView.findViewById(R.id.place_item_sectors);
            //가맹점 주소 (시/군)
            //place_address = itemView.findViewById(R.id.place_address);
            place_address_road = itemView.findViewById(R.id.place_address_road);
            //가맹점과 현재 위치의 거리
            place_distance = itemView.findViewById(R.id.place_distance);
            //가맹점의 리뷰 갯수
            //place_reviewNum = itemView.findViewById(R.id.place_reviewNum);


            //리뷰 북마크 버튼(boolean)
            place_item_bookmark = itemView.findViewById(R.id.place_item_bookmark);
//            //리뷰 설정 버튼(길찾기/공유/수정/삭제 / 취소)
//            review_item_setting_btn = itemView.findViewById(R.id.review_item_setting_btn);


            //아이템 뷰를 눌렀을 때
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(v, pos);
                        }
                    }
                }
            });

        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar pBar;

        public ProgressViewHolder(View v) {
            super(v);
            pBar = (ProgressBar) v.findViewById(R.id.pBar);
        }
    }


    //필터관련
    //필터관련~!!!
    private Filter categoryFilter;

    @Override
    public Filter getFilter() {

        if (categoryFilter == null) {
            categoryFilter = new CategoryFilter();
        }

        return categoryFilter;
    }

    //필터 클래스
    private class CategoryFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) { //필터링 전

            FilterResults results = new FilterResults();
            String charString = constraint.toString(); //여기예제에서는 필터링 값을 입력 받는다.

            Log.d("필터 클래스", charString);

            if (charString.isEmpty()) {//필터링 없을 때!
                results.values = placeInfoDataArrayList;
                results.count = placeInfoDataArrayList.size();

                //filteredList = placeInfoDataArrayList;
            } else {

                ArrayList<PlaceInfoData> filteringList = new ArrayList<>();

                for (PlaceInfoData pData : placeInfoDataArrayList) {

                    Log.d("필터클래스 포문", pData.getCategory());

                    if (pData.getCategory().equals(charString)) {//필터링 한 내용이 있으면 새로운 리스트에 추가

                        Log.d("필러 클랫 가맹점이름:", pData.getStoreName());
                        Log.d("필러 클랫 거리", ":" + pData.getDistance());
                        filteringList.add(pData);
                        Log.d("필터 클랫 리스트 갯수", ":" + filteringList.size());
                    } else {//필터링한 문구열이 없으면
                        Log.d("필터 클래스 포문 문구없음", pData.getCategory());
                    }
                }
                results.values = filteringList;
                results.count = filteringList.size();

                //filteredList = filteringList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(results.count <= 0){
                Toast.makeText(mContext,"찾는 가맹점이 없습니다.",Toast.LENGTH_LONG).show();
            }else {
                placeInfoDataArrayList = (ArrayList<PlaceInfoData>) results.values;
                Log.d("필터된리스트", placeInfoDataArrayList.get(0).getStoreName());
                if(placeInfoDataArrayList != null){
                    Collections.sort(placeInfoDataArrayList, new Comparator<PlaceInfoData>() {
                        @Override
                        public int compare(PlaceInfoData o1, PlaceInfoData o2) {
                            return (o1.getDistance() - o2.getDistance());
                        }
                    });
                }
            }
            notifyDataSetChanged();

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


    //페이징관련
    public void setRecyclerView(RecyclerView mView) {
        mView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
               /*다행히 LayoutManager(일반적으로는 LinearLayoutManager)에서
                getChildCount()함수를 통해
                recyclerView에 보여지고 있는 item의 갯수를 알 수 있고
                getItemCount()함수를 통해 전체 item의 갯수를 알 수 있습니다. */
                visibleItemCount = recyclerView.getChildCount();
                Log.d("!!어뎁터 setRecyclerview", ":" + visibleItemCount);

                totalItemCount = mLinearLayoutManager.getItemCount();

                firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

                lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                Log.d("total", totalItemCount + "");//필터링 한 전체 아이템 수
                Log.d("visible", visibleItemCount + "");//리사이클러뷰 화면에 보이는 아이템 수

                Log.d("first", firstVisibleItem + "");
                Log.d("last", lastVisibleItem + "");//필터링 한 전체 아이템에서 하나 뺀 수

                if (!isMoreLoading && (totalItemCount - 1) <= lastVisibleItem) {//(firstVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        Log.d("어뎁터 ; 여기 들어와?", "onLoadMore?");

                        onLoadMoreListener.onLoadMore();
                        //Log.d("어뎁터 ; 여기 들어와?","onLoadMore?");
                    }
                    isMoreLoading = true;
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return placeInfoDataArrayList.get(position) != null ? VIEW_TYPE_NORMAL : VIEW_TYPE_LOADING;
    }


    public void addAll(ArrayList<PlaceInfoData> lst) {
        placeInfoDataArrayList.clear();
        placeInfoDataArrayList.addAll(lst);
        notifyDataSetChanged();
    }

    public void addItemMore(ArrayList<PlaceInfoData> lst) {
        placeInfoDataArrayList.addAll(lst);
        notifyItemRangeChanged(0, placeInfoDataArrayList.size());
    }


    public void setMoreLoading(boolean isMoreLoading) {
        this.isMoreLoading = isMoreLoading;
    }


    public void setProgressMore(final boolean isProgress) {
        if (isProgress) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    placeInfoDataArrayList.add(null);
                    notifyItemInserted(placeInfoDataArrayList.size() - 1);
                    Log.d("여기 셋 프로그래스 모어 메소드 트류", ":" + placeInfoDataArrayList.size());
                }
            });
        } else {
            Log.d("여기 셋 프로그래스 모어 메소드 펄스", ":" + placeInfoDataArrayList.size());
            //내 예상으로는 저 사이즈 0
           if(placeInfoDataArrayList.size() > 0){
               placeInfoDataArrayList.remove(placeInfoDataArrayList.size() - 1);
               notifyItemRemoved(placeInfoDataArrayList.size());
            }

        }
    }

    //북마크 장소 정보 셰어드에 저장
    private void saveBookmarkPlaceInfo(String key, ArrayList<PlaceInfoData> mArrayList){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("BookmarkPlaceList",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mArrayList);
        editor.putString(key,json);
        editor.apply();
    }

    //북마크 장소 불러오기
    //loadDate
    private ArrayList<PlaceInfoData> loadBookmarkPlaceInfo(String key) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("BookmarkPlaceList", MODE_PRIVATE);
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
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("bookmarkList",MODE_PRIVATE);
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
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("bookmarkList",MODE_PRIVATE);
        String json = sharedPreferences.getString(key, null);
        Log.d("저장 >> 삭제 전", ":"+json);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();

        sharedPreferences = mContext.getSharedPreferences("bookmarkList",MODE_PRIVATE);

        String js = sharedPreferences.getString(key, null);
        Log.d("저장 >> 삭제", ":"+js);
    }


    //셰어드에 저장된 리스트 값 불러오기
    //SharedPreferences에서 Json형식의 String을 가져와서 다시 ArrayList로 변환
    private ArrayList<String> getBookmarkList(String key) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("bookmarkList",MODE_PRIVATE);
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


}






