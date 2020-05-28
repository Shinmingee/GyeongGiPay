package com.example.gyeonggipay;

import java.util.ArrayList;

//가맹점 정보 데이터
public class PlaceInfoData {
    //가맹점 이름
    private String storeName;

    //업종 카테고리
    private String category;

    //가맹점 지역(주소)
    private String address;

    private String address_road;

    //가맹점 전화번호
    private String telNum;

    //가맹점 좌표
    //위도
    private String LAT;
    //경도
    private String LOGT;

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    //거리
    private int distance;

    //북마크
    private boolean bookMark;

    //리뷰내용
    private String reviewContent;

    //리뷰 사진
    private ArrayList<String> photos;

    //리뷰 사진 대표
    private String photoMain;

    //리뷰 작성 날짜(2020년 05월 10일)
    private String reviewDate;

    //평점
    private Float starNum;

    //닉네임
    private String nickname;

    //이메일
    private String email;

    //프로필사진
    private String profile;

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelNum() {
        return telNum;
    }

    public void setTelNum(String telNum) {
        this.telNum = telNum;
    }

    public String getLAT() {
        return LAT;
    }

    public void setLAT(String LAT) {
        this.LAT = LAT;
    }

    public String getLOGT() {
        return LOGT;
    }

    public void setLOGT(String LOGT) {
        this.LOGT = LOGT;
    }

    public boolean getBookMark(){return bookMark;}

    public void setBookMark(boolean bookMark){this.bookMark = bookMark;}

    public String getAddress_road() {
        return address_road;
    }

    public void setAddress_road(String address_road) {
        this.address_road = address_road;
    }

    public boolean isBookMark() {
        return bookMark;
    }



    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public ArrayList<String> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<String> photos) {
        this.photos = photos;
    }

    public String getPhotoMain() {
        return photoMain;
    }

    public void setPhotoMain(String photoMain) {
        this.photoMain = photoMain;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Float getStarNum() {
        return starNum;
    }

    public void setStarNum(Float starNum) {
        this.starNum = starNum;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }


}