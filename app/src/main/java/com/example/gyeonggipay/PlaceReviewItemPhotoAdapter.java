package com.example.gyeonggipay;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


/*중첩이 되어있는 recycler view의 xml 파일과 상위 recycler view의 holder를 연결할 adapter를 만들어 줍니다.

inner class로 holder 클래스를 만들어서 활용합니다.

이 클래스는 단순히 레이아웃을 맵핑해주는 수준에 그칩니다.*/

public class PlaceReviewItemPhotoAdapter extends RecyclerView.Adapter<PlaceReviewItemPhotoAdapter.ViewHolder> {

    //Context
    private Context mContext;

    private ArrayList<String> photos;

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public PlaceReviewItemPhotoAdapter(ArrayList<String> list, Context context) {
        photos = list ;
        mContext = context;
    }


    @NonNull
    @Override
    public PlaceReviewItemPhotoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext() ;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.place_info_review_photo_item, parent, false) ;

        PlaceReviewItemPhotoAdapter.ViewHolder vh = new PlaceReviewItemPhotoAdapter.ViewHolder(view) ;

        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceReviewItemPhotoAdapter.ViewHolder holder, int position) {


        final String reviewPhoto = photos.get(position);
        Uri uri = Uri.parse(reviewPhoto);
        holder.place_info_imageView.setImageURI(uri);


    }

    @Override
    public int getItemCount() {
        return photos.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView place_info_imageView;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            //뷰 객체에 대한 참조(연결)
            //가맹점 이름
            place_info_imageView = itemView.findViewById(R.id.place_info_imageView);

        }
    }



}
