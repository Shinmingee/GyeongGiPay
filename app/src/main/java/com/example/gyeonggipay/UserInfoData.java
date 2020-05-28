package com.example.gyeonggipay;

import java.util.List;

//회원정보 클래스
public class UserInfoData {

    //이메일
    private String email;
    //닉네임
    private String nickname;
    //비밀번호
    private String pw;
    //프로필 사진
    private String profile;
    //거주지역
    private String region;
    //자동로그인 체크 박스
    private boolean autoLoginCheckBox;

    //북마크한 장소 리스트
    private List<String> BookmarkStoreNames;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public boolean getAutoLoginCheckBox() {
        return autoLoginCheckBox;
    }

    public void setAutoLoginCheckBox(boolean autoLoginCheckBox) {
        this.autoLoginCheckBox = autoLoginCheckBox;
    }

    public List<String> getBookmarkStoreNames() {
        return BookmarkStoreNames;
    }

    public void setBookmarkStoreNames(List<String> bookmarkStoreNames) {
        BookmarkStoreNames = bookmarkStoreNames;
    }

}
