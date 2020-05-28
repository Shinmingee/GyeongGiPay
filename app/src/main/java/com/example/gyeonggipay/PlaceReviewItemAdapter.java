package com.example.gyeonggipay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class PlaceReviewItemAdapter extends RecyclerView.Adapter<PlaceReviewItemAdapter.ViewHolder> {

    //Context
    private Context mContext;

    //아이템 객체를 리스트로
    private ArrayList<ReviewInfoData> reviewInfoDataArrayList;
    private ArrayList<String> photos;

    SharedPreferences sp;


    //생성자에서 데이터 리스트 객체를 전달 받음
    PlaceReviewItemAdapter(ArrayList<ReviewInfoData> list, Context context){
        reviewInfoDataArrayList = list;
        mContext = context;
    }

    //아이템 안에 버튼 -> 다이얼로그 -> 선택지
    final String[] menuList = new String[]{"수정","삭제"};



    //아이템 뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView place_info_item_nickname, place_info_date, place_info_item_star_score, place_info_item_context;

        ImageView place_info_profile,place_info_item_setting_btn;

        RecyclerView place_info_review_photo_recyclerview;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            //리뷰_ 회원 정보
            //프로필
            place_info_profile = itemView.findViewById(R.id.place_info_profile);
            //닉네임
            place_info_item_nickname = itemView.findViewById(R.id.place_info_item_nickname);

            //리뷰
            //별로 준 평점(소수점 한자리수)
            place_info_item_star_score = itemView.findViewById(R.id.place_info_item_star_score);
            //리뷰 내용
            place_info_item_context = itemView.findViewById(R.id.place_info_item_context);

            //작성 날짜
            place_info_date = itemView.findViewById(R.id.place_info_date);

            //설정 버튼
            place_info_item_setting_btn = itemView.findViewById(R.id.place_info_item_setting_btn);

            //사진 리사이클러뷰
            place_info_review_photo_recyclerview = itemView.findViewById(R.id.place_info_review_photo_recyclerview);
        }

    }


    @NonNull
    @Override
    public PlaceReviewItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Context에서 리사이클러뷰 화면의 context를 가져와
        Context context = parent.getContext();

        //xml 레이아웃을 inflater로 객체로 만들꺼야.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //뷰에 아이템 레이아웃을 객체로 만들어서 담을 꺼야.
        View view = inflater.inflate(R.layout.place_info_review_item, parent, false);
        //어뎁터에 정의 되어 있는 대로 레이아웃 xml의 요소와 화면 구성을 연결시킨다.
        PlaceReviewItemAdapter.ViewHolder vh = new PlaceReviewItemAdapter.ViewHolder(view);

        //연결된 뷰를 반환한다.
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceReviewItemAdapter.ViewHolder holder, int position) {

        final ReviewInfoData reviewInfoData = reviewInfoDataArrayList.get(position);

        photos = reviewInfoData.getPhotos();

        Log.d("장소 리뷰 아이템 어뎁터 리뷰작성 닉네임",reviewInfoData.getNickname());

        holder.place_info_profile.setBackground(new ShapeDrawable(new OvalShape()));
        holder.place_info_profile.setClipToOutline(true);

        holder.place_info_profile.setImageURI(Uri.parse(reviewInfoData.getProfile()));

        holder.place_info_item_nickname.setText(reviewInfoData.getNickname());
        holder.place_info_date.setText(reviewInfoData.getReviewDate());
        float tmpStarNum = reviewInfoData.getStarNum();
        holder.place_info_item_star_score.setText(String.valueOf(tmpStarNum));
        holder.place_info_item_context.setText(reviewInfoData.getReviewContent());

        PlaceReviewItemPhotoAdapter photoAdapter = new PlaceReviewItemPhotoAdapter(photos,mContext);

        holder.place_info_review_photo_recyclerview.setHasFixedSize(true);
        holder.place_info_review_photo_recyclerview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        holder.place_info_review_photo_recyclerview.setAdapter(photoAdapter);


        sp = mContext.getSharedPreferences("login_id",Context.MODE_PRIVATE);

        final String login_email = sp.getString("login_email","");
        Log.d("장소 리뷰 아이템 어뎁터|로그인이멜",login_email);

        final String review_email = reviewInfoData.getEmail();

        if(review_email.equals(login_email)){
            holder.place_info_item_setting_btn.setVisibility(View.VISIBLE);

            holder.place_info_item_setting_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("내 리뷰 설정")
                            .setItems(menuList, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int pos) {
                                    if (pos == 0) { //수정
                                        //리뷰 수정하는 페이지 이동.
                                        //가맹점 이름으로 찾아서... 슈정



                                    } else if (pos == 1) { //삭제
                                        //삭제
                                        Toast.makeText(mContext, menuList[pos], Toast.LENGTH_LONG).show();

                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                                        builder.setTitle("리뷰 삭제");
                                        builder.setMessage("삭제하시겠습니까?");
                                        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                //ReviewList.xml에서 내 계정으로 된 key 값 중
                                                //가맹점 이름 찾아서 삭제

                                                //여기서 포지션 받아서 삭제
                                                //

                                                //셰어드에 리뷰 저장 목록 중, 해당 가맹점 찾아서 그 객체를 지워야 함.
                                                //remove(holder.getAdapterPosition());

//                                    // 6. ArratList에서 해당 데이터를 삭제하고
//                                    reviewInfoDataArrayList.remove(holder.getAdapterPosition());
//                                    // 7. 어댑터에서 RecyclerView에 반영하도록 합니다.
//                                    notifyItemRemoved(holder.getAdapterPosition());
//                                    notifyItemRangeChanged(holder.getAdapterPosition(), reviewInfoDataArrayList.size());
                                            }
                                        });
                                        builder.setNegativeButton("아니오", null);
                                        builder.show();

                                    }
                                }
                            }).show();
                }
            });

        }else{
            holder.place_info_item_setting_btn.setVisibility(View.INVISIBLE);
        }


    }



    //getItemCount() : 전체 데이터 갯수 리턴
    @Override
    public int getItemCount() {
        return reviewInfoDataArrayList.size();
    }

}
