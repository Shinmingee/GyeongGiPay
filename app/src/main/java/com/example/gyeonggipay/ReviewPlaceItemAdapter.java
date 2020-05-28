package com.example.gyeonggipay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class ReviewPlaceItemAdapter extends RecyclerView.Adapter<ReviewPlaceItemAdapter.ViewHolder> {

    //Context
    private Context mContext;

    //아이템 객체를 리스트로
    //private ArrayList<ReviewPlaceItemData> reviewPlaceItemDataArrayList;
    private ArrayList<ReviewInfoData> reviewInfoDataArrayList;


    //생성자에서 데이터 리스트 객체를 전달 받음
//    ReviewPlaceItemAdapter(ArrayList<ReviewPlaceItemData> list, Context context){
//        reviewPlaceItemDataArrayList = list;
//        mContext = context;
//    }
    ReviewPlaceItemAdapter(ArrayList<ReviewInfoData> list, Context context){
        reviewInfoDataArrayList = list;
        mContext = context;
    }

    //아이템 안에 버튼 -> 다이얼로그 -> 선택지
    final String[] menuList = new String[]{"길찾기","공유","수정","삭제","취소"};

    //버튼과 아이템 뷰 클릭 리스너



    //onCreateViewHolder() : 아이템 뷰를 위한 뷰 홀더 객체 생성하여 리턴
    @NonNull
    @Override
    public ReviewPlaceItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Context에서 리사이클러뷰 화면의 context를 가져와
        Context context = parent.getContext();

        //xml 레이아웃을 inflater로 객체로 만들꺼야.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //뷰에 아이템 레이아웃을 객체로 만들어서 담을 꺼야.
        View view = inflater.inflate(R.layout.review_place_item, parent, false);
        //어뎁터에 정의 되어 있는 대로 레이아웃 xml의 요소와 화면 구성을 연결시킨다.
        ReviewPlaceItemAdapter.ViewHolder vh = new ReviewPlaceItemAdapter.ViewHolder(view);

        //연결된 뷰를 반환한다.
        return vh;
    }

    //onBindViewHolder() : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시한다.
    @Override
    public void onBindViewHolder(@NonNull final ReviewPlaceItemAdapter.ViewHolder holder, final int position) {

        final ReviewInfoData reviewInfoData = reviewInfoDataArrayList.get(position);

        Log.d("어뎁터 온바인드뷰홀더 포지션",":"+position);

        //ReviewPlaceItemData reviewPlaceItemData = reviewPlaceItemDataArrayList.get(position);

        holder.review_storename.setText(reviewInfoData.getStoreName());
        holder.review_item_context.setText(reviewInfoData.getReviewContent());
        holder.review_item_nickname.setText(reviewInfoData.getNickname());
        holder.review_item_star_score.setText(String.valueOf(reviewInfoData.getStarNum()));
        holder.review_item_sectors.setText(reviewInfoData.getCategory());

        holder.review_address.setText(reviewInfoData.getAddress());
        //이미지 저장 자료형 변경 Uri->String
        holder.review_item_photo.setImageURI(Uri.parse(reviewInfoData.getPhotoMain()));
        Log.d("어뎁터 홀더 사진 URI",":"+reviewInfoData.getPhotoMain());
        Log.d("어뎁터 홀더 가게이름",":"+reviewInfoData.getStoreName());

        holder.itemView.setTag(position);

        final String tmpStoreName = reviewInfoData.getStoreName();
        //holder.review_item_km.setText(String.valueOf(reviewInfoData.getDistance()));

        //장소 상세페이지로 이동 2가지 => 사진 & 가맹점이름
        //사진 눌렀을 때
        holder.review_item_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goPlaceDetail = new Intent(mContext,PlaceInfoActivity.class);
                goPlaceDetail.putExtra("index",position);
                goPlaceDetail.putExtra("storeName",tmpStoreName);
                //goPlaceDetail.putExtra("arrayList",reviewInfoDataArrayList);
                mContext.startActivity(goPlaceDetail);
            }
        });

        //가맹점 이름 눌렀을 때
        holder.review_storename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goPlaceDetail = new Intent(mContext,PlaceInfoActivity.class);
                goPlaceDetail.putExtra("index",position);
                goPlaceDetail.putExtra("storeName",tmpStoreName);
                //goPlaceDetail.putExtra("arrayList",reviewInfoDataArrayList);
                mContext.startActivity(goPlaceDetail);
            }
        });

        Log.d("리뷰리스트 버튼에서 position>>",":"+position);
        //설정 버튼 눌렀을 때
        holder.review_item_setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("어뎁터 홀더 버튼 클릭 됨?",": 여기여기");

                Log.d("리뷰리스트 position",":"+position);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("리뷰 설정")
                        .setItems(menuList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int pos) {
                        if (pos == 0) {
                            //길찾기
                            Toast.makeText(mContext, menuList[pos], Toast.LENGTH_LONG).show();

                        } else if (pos == 1) {
                            //공유
                            Toast.makeText(mContext, menuList[pos], Toast.LENGTH_LONG).show();

                        } else if (pos == 2) {
                            //수정 누르면 수정하는 화면으로 간다.
                            //수정할 아이템리스트의 포지션이 같이 가야한다.
                            Intent goReviewEdit = new Intent(mContext, ReviewEditActivity.class);
                            int position = holder.getAdapterPosition();
                            goReviewEdit.putExtra("position", position);

                            Log.d("menuList[pos]",menuList[pos]);
                            Log.d("수정 클릭 : position",":"+ position);

                            mContext.startActivity(goReviewEdit);

//                            Toast.makeText(mContext, menuList[pos], Toast.LENGTH_LONG).show();
//                            Toast.makeText(mContext, position, Toast.LENGTH_LONG).show();

                            //Bundle args = new Bundle();

//                            String storeName = (reviewInfoDataArrayList.get(holder.getAdapterPosition()).getStoreName());
//                            String reviewContent =(reviewInfoDataArrayList.get(holder.getAdapterPosition()).getReviewContent());
//                            String category=(reviewInfoDataArrayList.get(holder.getAdapterPosition()).getCategory());
//                            //이미지 저장 자료형 변경 Uri->String
//                            Uri photo = (Uri.parse(reviewInfoDataArrayList.get(holder.getAdapterPosition()).getPhotoMain()));
//                            float starRating = (reviewInfoDataArrayList.get(holder.getAdapterPosition()).getStarNum());
//                            String region = (reviewInfoDataArrayList.get(holder.getAdapterPosition()).getAddress());
//                            String nickname = (reviewInfoDataArrayList.get(holder.getAdapterPosition()).getNickname());



                            //args.putParcelable("arrayList", (Parcelable) reviewPlaceItemDataArrayList);


//                            Iterator<ReviewPlaceItemData>list2 =reviewPlaceItemDataArrayList.iterator();
//                            while (list2.hasNext()) {
//                                Log.d("어뎁터 어래이 객체 내용",":"+(list2.next())+"\n");
//                            }
                            //출처: https://farcanada.tistory.com/entry/Java-배열을-전체출력하는-5가지-방법-Arraylist이용 [남다른 캐나다]

//                            Log.d("어래이 인덱스[0] 객체 주소",":"+reviewInfoDataArrayList.get(holder.getAdapterPosition()));
//                            Log.d("어래이 인덱스[0] 객체",":"+(reviewInfoDataArrayList.get(holder.getAdapterPosition())).toString());
//                            Log.d("어래이 ",":"+reviewInfoDataArrayList);
//                            Log.d("어댑터어어어 이건 꺼낼 수 있는겨?","가게이름:"+storeName);
//                            Log.d("어댑터어어어어어 여기 이미지uri","이미지이미지:"+photo);
//                            Log.d("어댑터어어어어어 뷰는 없는 지역data","region :"+region);
//                            Log.d("어댑터어어어어어 인덱스",": "+index);


//                            goReviewEdit.putExtra("index", index);
//
//                            goReviewEdit.putExtra("storeName", storeName);
//                            goReviewEdit.putExtra("reviewContent",reviewContent);
//                            goReviewEdit.putExtra("starRating",starRating);
//                            goReviewEdit.putExtra("category",category);
//                            goReviewEdit.putExtra("nickname",nickname);
//                            //uri->string
//                            goReviewEdit.putExtra("photo", photo.toString());
//                            goReviewEdit.putExtra("region",region);

                            //goReviewEdit.putParcelableArrayListExtra("arrayList",reviewPlaceItemDataArrayList);

                            //goReviewEdit.putExtra("BUNDLE",args);



                            //Log.d("포지션",":"+holder.getAdapterPosition());

                        } else if (pos == 3) {
                            //삭제
                            Toast.makeText(mContext, menuList[pos], Toast.LENGTH_LONG).show();

                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                            builder.setTitle("리뷰 삭제");
                            builder.setMessage("삭제하시겠습니까?");
                            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    remove(holder.getAdapterPosition());
//                                    // 6. ArratList에서 해당 데이터를 삭제하고
//                                    reviewInfoDataArrayList.remove(holder.getAdapterPosition());
//                                    // 7. 어댑터에서 RecyclerView에 반영하도록 합니다.
//                                    notifyItemRemoved(holder.getAdapterPosition());
//                                    notifyItemRangeChanged(holder.getAdapterPosition(), reviewInfoDataArrayList.size());
                                }
                            });
                            builder.setNegativeButton("아니오", null);
                            builder.show();

                        } else if (pos == 4) {
                            //취소
                            Toast.makeText(mContext, menuList[pos], Toast.LENGTH_LONG).show();

                        }
                    }
                });
                builder.show();

            }
        });

    }

    //getItemCount() : 전체 데이터 갯수 리턴
    @Override
    public int getItemCount() {
        return reviewInfoDataArrayList.size();
    }


    //아이템 뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView review_storename, review_item_star_score, review_item_sectors, review_address,
                review_item_nickname, review_item_context;

        ImageView review_item_photo,review_item_bookmark,review_item_setting_btn;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            //뷰 객체에 대한 참조(연결)
            //가맹점 이름
            review_storename = itemView.findViewById(R.id.review_storename);
            //별로 준 평점(소수점 한자리수)
            review_item_star_score = itemView.findViewById(R.id.review_item_star_score);
            //가맹점 카테고리 이름
            review_item_sectors = itemView.findViewById(R.id.review_item_sectors);
            //가맹점 주소 (시/군)
            review_address = itemView.findViewById(R.id.review_address);
            //리뷰 닉네임
            review_item_nickname = itemView.findViewById(R.id.review_item_nickname);
            //리뷰 내용 한줄만
            review_item_context = itemView.findViewById(R.id.review_item_context);


            //리뷰 사진
            review_item_photo = itemView.findViewById(R.id.review_item_photo);
            //리뷰 북마크 버튼(boolean)
            review_item_bookmark  = itemView.findViewById(R.id.review_item_bookmark);
            //리뷰 설정 버튼(길찾기/공유/수정/삭제 / 취소)
            review_item_setting_btn = itemView.findViewById(R.id.review_item_setting_btn);

        }
    }


    //
    public void remove(int position) {

        SharedPreferences sp = mContext.getSharedPreferences("login_id",Context.MODE_PRIVATE);
        String login_email = sp.getString("login_email","");

        try {
            reviewInfoDataArrayList.remove(position);
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("ReviewList",mContext.MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            Gson gson = new Gson();
            String json  = gson.toJson(reviewInfoDataArrayList);
            edit.putString("reviewInfoDataArrayList"+login_email, json);
            edit.apply();
            // 새로고침
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, reviewInfoDataArrayList.size());
            //notifyDataSetChanged();
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }


}
