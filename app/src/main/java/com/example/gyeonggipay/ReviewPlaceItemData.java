//package com.example.gyeonggipay;
//
//import android.net.Uri;
//import android.os.Parcel;
//import android.os.Parcelable;
//
////리뷰한 가맹점 리스트의 아이템 데이터
//public class ReviewPlaceItemData implements Parcelable {
//
//    //가맹점 이름
//    private String storeName;
//    //별점 점수(소수 점 한자리)
//    private float starNum;
//    //업종 카테고리(카테고리 이름)
//    private String categoryName;
//    //내 현재위치에서의 거리(소수 점 한자리)
//    private float distance;
//    //닉네임
//    private String nickName;
//    //리뷰내용 한줄
//    private String review;
//    //북마크버튼(true, false)
//    //private boolean bookMark;
//    //설정버튼
//    //사진뷰
//    //private Uri reviewPhoto;
//
//    private String reviewPhoto;
//
//    //지역->수정할 때 필요
//    private String region;
//
//    public ReviewPlaceItemData(){
//
//    }
//
//    public ReviewPlaceItemData(String storeName, String categoryName,
//                               String nickName,String review,
//                               float distance,float starNum,
//                               String reviewPhoto, String region){
//
//        super();
//        this.storeName = storeName;
//        this.categoryName = categoryName;
//        this.nickName = nickName;
//        this.distance = distance;
//        this.starNum = starNum;
//        this.reviewPhoto = reviewPhoto;
//        this.region = region;
//    }
//
//    public ReviewPlaceItemData(Parcel in) {
//        readFromParcel(in);
//
//    }
//
//
//    public String getStoreName() {
//        return storeName;
//    }
//
//    public void setStoreName(String storeName) {
//        this.storeName = storeName;
//    }
//
//    public float getStarNum() {
//        return starNum;
//    }
//
//    public void setStarNum(float starNum) {
//        this.starNum = starNum;
//    }
//
//    public String getCategoryName() {
//        return categoryName;
//    }
//
//    public void setCategoryName(String categoryName) {
//        this.categoryName = categoryName;
//    }
//
//    public double getDistance() {
//        return distance;
//    }
//
//    public void setDistance(Float distance) {
//        this.distance = distance;
//    }
//
//    public String getNickName() {
//        return nickName;
//    }
//
//    public void setNickName(String nickName) {
//        this.nickName = nickName;
//    }
//
//    public String getReview() {
//        return review;
//    }
//
//    public void setReview(String review) {
//        this.review = review;
//    }
//
////    public boolean isBookMark() {
////        return bookMark;
////    }
////
////    public void setBookMark(boolean bookMark) {
////        this.bookMark = bookMark;
////    }
//
//    public String getReviewPhoto() {
//        return reviewPhoto;
//    }
//
//    public void setReviewPhoto(String reviewPhoto) {
//        this.reviewPhoto = reviewPhoto;
//    }
//
//    public String getRegion(){return region;}
//
//    public void setRegion(String region){ this.region = region;}
//
//
//    //병렬화 하려는 오브젝트의 종류를 정의한다.
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    //실제 오브젝트
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        //dest.writeString(reviewPhoto.toString());
//        dest.writeString(storeName);
//        dest.writeString(categoryName);
//        dest.writeString(nickName);
//        dest.writeString(review);
//        dest.writeFloat(distance);
//        dest.writeFloat(starNum);
//        dest.writeString(reviewPhoto);
//        dest.writeString(region);
//    }

//    //복구하는 생성자 writeToParcel에서 기록한 순서를 똑같이 해야함
//    private void readFromParcel(Parcel in){
//        //reviewPhoto = Uri.parse(in.readString());
//        storeName = in.readString();
//        categoryName = in.readString();
//        nickName = in.readString();
//        review = in.readString();
//        distance = in.readFloat();
//        starNum = in.readFloat();
//        reviewPhoto = in.readString();
//        region = in.readString();
//    }
//
//    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
//        public ReviewPlaceItemData createFromParcel(Parcel in){
//            return new ReviewPlaceItemData(in);
//        }
//
//        @Override
//        public ReviewPlaceItemData[] newArray(int size) {
//            return new ReviewPlaceItemData[size];
//        }
//    };
//
//}
