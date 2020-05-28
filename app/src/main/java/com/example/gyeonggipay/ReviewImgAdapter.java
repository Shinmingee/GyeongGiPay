package com.example.gyeonggipay;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ReviewImgAdapter extends RecyclerView.Adapter<ReviewImgAdapter.ViewHolder>{
//        implements ItemTouchHelperListener{

    private ArrayList<String> mData = null ;
    private Context mContext;

    //아이템 안에 버튼 -> 다이얼로그 -> 선택지
    private final String[] choice = new String[]{"대표사진으로 선택", "사진 삭제", "취소"};


    //셰어드에 사진들 ArrayList->JSON 저장할 때 키값
    //사진 리스트 임시저장소 : reviewPhotoList.xml
    //사진 리스트 최종저장소 : reviewPhotoListALL.xml
    //사진 arraylist => key=REVIEW_PHOTO_LIST
    //사진 갯수 int => key=REVIEW_NUM

    //대표사진 임시저장소 : reviewPhotoMain.xml
    //대표사진 최종저장소 : reviewPhotoMainAll.xml
    //사진 string => key=REVIEW_MAIN_PHOTO

    private SharedPreferences sharedPreferences;
    private final String photoListKey = "REVIEW_PHOTO_LIST";
    private final String photoListNumKey = "REVIEW_NUM";
    //private final String photoMainKey = "REVIEW_MAIN_PHOTO";


//    //리사이클러뷰 클릭 리스너
//    public interface ReviewImgAdapterRecyclerViewListener {
//        void onItemClicked(View view, int position);
//
//    }
//
//    ReviewImgAdapterRecyclerViewListener reviewImgAdapterRecyclerViewListener;
//
//    public void setReviewImgAdapterRecyclerViewListener(ReviewImgAdapterRecyclerViewListener listener) {
//        reviewImgAdapterRecyclerViewListener = listener;
//    }


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView review_photo, review_edit_btn, star;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            review_photo = itemView.findViewById(R.id.review_img) ;
            review_edit_btn = itemView.findViewById(R.id.review_edit_btn);
            star = itemView.findViewById(R.id.star);

//            SharedPreferences preferences = mContext.getSharedPreferences("reviewPhotoMain",MODE_PRIVATE);
//            if(preferences.contains("REVIEW_MAIN_PHOTO_position")){
//                int pre_position = preferences.getInt("REVIEW_MAIN_PHOTO_position",9999);
//                itemView.findViewWithTag(pre_position).findViewById(R.id.star).setVisibility(View.INVISIBLE);
//            }


            //출처: https://itpangpang.xyz/274 [ITPangPang]


        }

    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public ReviewImgAdapter(ArrayList<String> list, Context context) {
        mData = list ;
        mContext = context;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public ReviewImgAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext() ;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.review_img_list_item, parent, false) ;

        ReviewImgAdapter.ViewHolder vh = new ReviewImgAdapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(final ReviewImgAdapter.ViewHolder holder, final int position) {
        //String형을 uri로 바꿔서 아이템의 ImageView에 set시켜줘야 함

        final String text = mData.get(position);
        Uri uri = Uri.parse(text);
        holder.review_photo.setImageURI(uri);

        holder.review_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("사진세팅 어뎁터 홀더 버튼 클릭 됨?",": 여기여기");

                Log.d("position",":"+position);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("선택 사진 설정")
                        .setItems(choice, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        //"대표사진으로 선택" >>완료 버튼 누를 때 그 인덱스 값 넘겨줄 거얌

//                                        if (reviewImgAdapterRecyclerViewListener != null) {
//                                            reviewImgAdapterRecyclerViewListener.onItemClicked(holder.itemView, position);
//                                        }
                                        SharedPreferences preferences = mContext.getSharedPreferences("reviewPhotoMain",MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        if(preferences.contains("REVIEW_MAIN_PHOTO")){
                                            Toast.makeText(mContext,"선택하신 사진으로 대표사진을 변경합니다",Toast.LENGTH_LONG).show();
                                            //원래 대표사진이었던 곳의 별표는 사라지고/ 현재 포지션의 사진에 별표가 표시되어야 함
                                            //원래 대표사진은 지워지고 / 현재 포지션의 사진이 셰어드에 저장되어야 함


                                            //홀더 아이템의 속성이 아닌 다른 포지션의 아이템 속성을 바꿀 때는 어떻게 해야하나?
                                            //=>처음 누른 아이템에 별표 표시할때,  holder.itemView.setTag(position); 을 설정해서 뷰홀더에서
                                            //별표 보이게 함.

                                            //셰어드에서 대표사진과 포지션 지움
                                            editor.remove("REVIEW_MAIN_PHOTO");
                                            editor.remove("REVIEW_MAIN_PHOTO_position");
                                            editor.apply();

                                            //새로 누른 아이템에 별표 표시
                                            holder.itemView.setTag(position);
                                            holder.star.setVisibility(View.VISIBLE);
                                            //메인사진 저장
                                            editor.putString("REVIEW_MAIN_PHOTO",text);
                                            //포지션 저장
                                            editor.putInt("REVIEW_MAIN_PHOTO_position",position);
                                            Log.d("대표사진 : ",text);
                                            Log.d("대표사진 포지션",":"+position);
                                            editor.apply();


                                        }else{
                                            //대표사진 처음 설정할 때
                                            //Log.d("대표사진 인덱스", ":"+index);
                                            holder.itemView.setTag(position);
                                            holder.star.setVisibility(View.VISIBLE);
                                            editor.putString("REVIEW_MAIN_PHOTO",text);
                                            //포지션 저장
                                            editor.putInt("REVIEW_MAIN_PHOTO_position",position);
                                            Log.d("대표사진 : ",text);
                                            Log.d("대표사진 포지션",":"+position);
                                            editor.apply();
                                        }


                                        break;
                                    case 1:
                                        //"사진 삭제"
                                        Toast.makeText(mContext, choice[which], Toast.LENGTH_LONG).show();

                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                                        builder.setTitle("사진 삭제");
                                        builder.setMessage("삭제하시겠습니까?");
                                        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // 6. ArrayList에서 해당 데이터를 삭제하고
                                                //myPlaceList.remove(position);
                                                mData.remove(holder.getAdapterPosition());
                                                // 7. 어댑터에서 RecyclerView에 반영하도록 합니다.
                                                notifyItemRemoved(holder.getAdapterPosition());
                                                //notifyItemRangeChanged(position, myPlaceList.size());
                                                notifyItemRangeChanged(holder.getAdapterPosition(), mData.size());
                                                notifyDataSetChanged();

                                                Log.d("사진삭제 클릭시 남아있는 아이템 갯수",":"+getItemCount());

                                                //삭제하고 남아있는 값 저장..
                                                setStringArrayPref(photoListKey,photoListNumKey,mData);

                                                Log.d("사진삭제 클릭시 남아있는 아이템 갯수",":"+getItemCount());
                                                Log.d("사진삭제 클릭시 남아있는 아이템 list",":"+mData.toString());


                                                //삭제하고 남아있는 갯수 셋해주기
                                                //Intent intent = new Intent(mContext, ReviewImgListSettingActivity.class);
                                                //intent.putExtra("rwP_num",mData.size());
                                                //mContext.startActivity(intent);

                                            }
                                        });
                                        builder.setNegativeButton("아니오", null);
                                        builder.show();
                                        break;
                                    case 2:
                                        //"취소"

                                        break;
                                }
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });




    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }


////    //아이템이 움직일때(position 관리)
//    @Override
//    public boolean onItemMove(int from_position, int to_position) {
//        //이동할 객체 저장
//        String text = mData.get(from_position);
//        //Person person = items.get(from_position);
//
//        //이동할 객체 삭제
//        mData.remove(from_position);
//        //items.remove(from_position);
//
//        //이동하고 싶은 position에 추가
//        mData.add(to_position,text);
//        //items.add(to_position,person);
//
//        //Adapter에 데이터 이동알림
//        notifyItemMoved(from_position, to_position);
//        Log.d("아이템 이동 포지션","여기서:"+from_position+" 여기로: "+to_position);
//
//        return true;
//    } //출처: https://everyshare.tistory.com/27 [에브리셰어]
//
//
//
//    //아이템이 스와프될 때(삭제)
//    @Override
//    public void onItemSwipe(final int position) {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//
//        builder.setTitle("사진 삭제");
//        builder.setMessage("삭제하시겠습니까?");
//        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // 6. ArrayList에서 해당 데이터를 삭제하고
//                //myPlaceList.remove(position);
//                mData.remove(position);
//                // 7. 어댑터에서 RecyclerView에 반영하도록 합니다.
//                notifyItemRemoved(position);
//                //notifyItemRangeChanged(position, myPlaceList.size());
//                //notifyItemRangeChanged(position, mData.size());
//                notifyDataSetChanged();
//                Log.d("사진삭제 클릭시 남아있는 아이템 갯수",":"+getItemCount());
//                Log.d("사진삭제 클릭시 남아있는 아이템 list",":"+mData.toString());
//            }
//        });
//        builder.setNegativeButton("아니오", null);
//        builder.show();
//
//
////        mData.remove(position);
////
////        notifyItemRemoved(position);
//    } //출처: https://everyshare.tistory.com/27 [에브리셰어]



    //셰어드에 저장
    //ArrayList를 Json으로 변환하여 SharedPreferences에 String을 저장
    //key값을 무엇으로 할지_여기서는 중요하지 않음 최종 저장할때가 중요
    //여기서 리뷰 작성 페이지로 넘길때 같이 넘겨야 할 중요 정보는, 대표사진의 index값.
    private void setStringArrayPref(String key, String num_key, ArrayList<String> values) {
        sharedPreferences = mContext.getSharedPreferences("reviewPhotoList",MODE_PRIVATE);
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
//
    private void removeStringArrayPref(String key, String num_key) {
        sharedPreferences = mContext.getSharedPreferences("reviewPhotoList",MODE_PRIVATE);
        String json = sharedPreferences.getString(key, null);
        Log.d("저장 >> 삭제 전", ":"+json);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
        editor.remove(num_key);
        editor.apply();

        sharedPreferences = mContext.getSharedPreferences("reviewPhotoList",MODE_PRIVATE);
        int n = sharedPreferences.getInt(num_key,9999);
        Log.d("저장 >> 삭제 후 Num", ":"+n);
        String js = sharedPreferences.getString(key, null);
        Log.d("저장 >> 삭제", ":"+js);
    }



//
    //셰어드에 저장된 리스트 값 불러오기
    //SharedPreferences에서 Json형식의 String을 가져와서 다시 ArrayList로 변환
    private ArrayList<String> getStringArrayPref(String key) {
        sharedPreferences = mContext.getSharedPreferences("reviewPhotoList",MODE_PRIVATE);
        String json = sharedPreferences.getString(key, null);
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

    private int getStringArraySize(String num_key){
        sharedPreferences = mContext.getSharedPreferences("reviewPhotoList",MODE_PRIVATE);
        int arraySize = sharedPreferences.getInt(num_key,0);
        Log.d("불러오기 Num", ":"+arraySize);

        return arraySize;
    }



}

