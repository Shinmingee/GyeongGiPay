package com.example.gyeonggipay;

import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//리뷰 수정 할때 아이템의 이미지도 수정해야 한다,
//전체 이미지의 수정을 위해 photos의 값을 불러와서 arrayList로 바꿔준후 어뎁터에 넘긴다.

public class ReviewImgEditActivity extends AppCompatActivity {

    /*리뷰 에딧 액티비티에서 이미지 임시저장소 셰어드에 수정할 리뷰의 전체 이미지 담아놓은걸 불러오기 */


    ImageView review_lmg_edit_add_btn;
    TextView review_lmg_edit_Photo_total;
    final String[] words = new String[]{"카메라로 사진 찍기", "앨범에서 선택 하기", "취소"};

    //프로필 사진(카메라 찍기, 앨범접근, 크롭, 앨범에 저장)
    //private static final int MY_PERMISSION_CAMERA = 1111;
    private static final int REQUEST_TAKE_PHOTO = 2222;
    private static final int REQUEST_TAKE_ALBUM = 3333;
    //private static final int REQUEST_IMAGE_CROP = 4444;
    private final int PICTURE_REQUEST_CODE = 100;

    ArrayList<String> list = null;
    ReviewImgAdapter reviewImgAdapter;

    //?
    String mCurrentPhotoPath;

    //이미지uri
    Uri imageUri;
    //사진 앨범
    Uri photoURI, albumURI;

    //저장소
    Intent reviewPhoto = new Intent();

    ////////셰어드에 사진들 ArrayList->JSON 저장할 때 키값////////////////
    //사진 리스트 임시저장소 : reviewPhotoList.xml
    //사진 리스트 최종저장소 : reviewPhotoListALL.xml
    //사진 arraylist => key=REVIEW_PHOTO_LIST
    //사진 갯수 int => key=REVIEW_NUM

    //대표사진 임시저장소 : reviewPhotoMain.xml
    //대표사진 최종저장소 : reviewPhotoMainAll.xml
    //사진 string => key=REVIEW_MAIN_PHOTO

    SharedPreferences sharedPreferences;
    private final String photoListKey = "REVIEW_PHOTO_LIST";
    private final String photoListNumKey = "REVIEW_NUM";
    private final String photoMainKey = "REVIEW_MAIN_PHOTO";
/////////////////////////////////////////////////////////////////////

    //사진 총갯수 변수
    private int photoTotalNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_img_edit);

        //xml 연결
        //툴바를 이 화면의 앱바로 연결
        Toolbar tb = (Toolbar) findViewById(R.id.review_lmg_edit_toolbar);
        setSupportActionBar(tb);

        // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //기본 타이틀 보여줄지 말지 설정

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView apptitle = findViewById(R.id.review_lmg_edit_toolbar_title);
        apptitle.setText("사진편집");

        //추가한 사진의 갯수 mapping
        review_lmg_edit_Photo_total = findViewById(R.id.review_lmg_edit_Photo_total);
        review_lmg_edit_add_btn = findViewById(R.id.review_lmg_edit_add_btn);

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        final RecyclerView recyclerView = findViewById(R.id.recycler_edit_photo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //셰어드에서 먼저 화면에 보여줄
        //(리사이클러뷰에 표시할=어뎁터에 넘겨줄) 데이터 리스트를 불러온다.
        list = getStringArrayPref(photoListKey);
        Log.d("리스트",list.get(0));


        //어뎁터에 넘긴다
        reviewImgAdapter= new ReviewImgAdapter(list,ReviewImgEditActivity.this);

        //이 리스트의 총 갯수
        photoTotalNum = list.size();
        //사진 총 갯수를 셋
        review_lmg_edit_Photo_total.setText(String.valueOf(photoTotalNum));

        recyclerView.setAdapter(reviewImgAdapter);


        //사진 추가 버튼
        review_lmg_edit_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ReviewImgEditActivity.this);
                builder.setTitle("사진추가")
                        .setItems(words, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which) {
                                    case 0:
                                        //카메라로 사진찍기
                                        captureCamera();

                                        break;
                                    case 1:
                                        //앨범에서 선택
                                        Intent intent = new Intent();
                                        intent.setType("image/*");
                                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICTURE_REQUEST_CODE);

                                        Log.d("앨범선택","11");


                                        break;
                                    case 2:
                                        //취소

                                        break;
                                }


                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }

        });



    }//onCreate() END


    @Override
    public void onBackPressed() {
        Toast.makeText(ReviewImgEditActivity.this,"backButton",Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(ReviewImgEditActivity.this);
        builder.setTitle("[알림] 사진 저장 미완료")
                .setMessage("추가된 사진이 저장되지 않습니다. 계속 진행하시겠습니까?")
                .setPositiveButton("계속 진행", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeStringArrayPref(photoListKey, photoListNumKey);
                        Log.d("사진저장 미완료 reset","");
                        //setResult(RESULT_OK);
                        finish();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    //툴바 상단 액션 버튼
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.review_appbar, menu);

        return true;
    }



    //툴바 버튼 control
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:{ //툴바의 back키 눌렀을 때
                //사진 추가 페이지 종료
                AlertDialog.Builder builder = new AlertDialog.Builder(ReviewImgEditActivity.this);
                builder.setTitle("[알림] 사진 저장 미완료")
                        .setMessage("수정한 사진이 저장되지 않습니다. 계속 진행하시겠습니까?")
                        .setPositiveButton("계속 진행", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                removeStringArrayPref(photoListKey, photoListNumKey);
                                Log.d("사진 수정 미완료 reset","");
                                //setResult(RESULT_OK);
                                finish();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;

            }
            case R.id.img_complete: {
                //리뷰 수정 엑티비티로
//                Intent goSetting = new Intent(this, SettingActivity.class);
//                startActivity(goSetting);

                //((TextView)findViewById(R.id.textView)).setText("SETTINGS") ;

                sharedPreferences = getSharedPreferences("reviewPhotoMain",MODE_PRIVATE);

                AlertDialog.Builder builder = new AlertDialog.Builder(ReviewImgEditActivity.this);
                if(sharedPreferences.contains("REVIEW_MAIN_PHOTO")){
                    builder.setTitle("[알림] 사진 저장 완료")
                            .setMessage("사진이 저장되었습니다.")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //임시저장소 저장
                                        if(list.size() != 0){
                                            Log.d("전체 리스트를 보고 싶다",":"+list);
                                            //전체리스트 있으면 sharedPreferences에 저장
                                            setStringArrayPref(photoListKey,photoListNumKey,list);
                                        }else{
                                            Log.d("전체 리스트가 안나온다아ㅏ",":"+list);
                                        }
                                    //Log.d("전체 리스트를 보고 싶다",":"+list);

                                    //최종 저장본
                                    setReviewPhotoListAll(photoListKey, photoListNumKey, list);

                                    //최종 대표 사진 저장본
                                    sharedPreferences = getSharedPreferences("reviewPhotoMain",MODE_PRIVATE);
                                    String tmp = sharedPreferences.getString(photoMainKey,"");

                                    Log.d("사진저장 완료", tmp);
                                    sharedPreferences = getSharedPreferences("reviewPhotoMainAll",MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(photoMainKey,tmp);
                                    editor.apply();

                                    //임시저장소 지우는 걸 사진저장 완료가 아니라, 리뷰 수정 완료일 때!
                                    //removeStringArrayPref(photoListKey, photoListNumKey);

                                    setResult(RESULT_OK);
                                    finish();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }else{
                    builder.setTitle("[알림] 대표 사진 설정")
                            .setMessage("사진추가를 완료하시려면\n대표사진 한장을 선택해야 합니다.")
                            .setPositiveButton("확인",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }



                return true;

            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //카메라 켜서 사진찍고 저장
    private void captureCamera(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {// 외장 메모리가 현재 read와 write를 할 수 있는 상태인지 확인한다

            //암시적 인텐트 사용 : 카메라에서 이미지 캡쳐해서 반환하도록 하는 액션.
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            //먼저 암시적 인텐트를 처리할 수 있는 앱이 단말에 존재하는지를 확인하기 위하여
            //Intent 오브젝트를 사용해 resolveActivity() 메서드를 호출한다.
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Log.e("captureCamera Error", ex.toString());
                }
                if (photoFile != null) {

                    // getUriForFile의 두 번째 인자는 Manifest provier의 authorites와 일치해야 함
                    Uri providerURI = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    imageUri = providerURI;

                    //인텐트에 전달할 때는 FileProvier의 Return값인 content://로만!!, providerURI의 값에 카메라 데이터를 넣어 보냄
                    //EXTRA_OUTPUT : 이미지가 작성 될 위치를 제어하기 위해 추가 EXTRA_OUTPUT을 전달할 수 있습니다.
                    //EXTRA_OUTPUT이 있으면 전체 크기 이미지가 EXTRA_OUTPUT의 Uri 값에 기록됩니다.
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI);

                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        } else {
            Toast.makeText(ReviewImgEditActivity.this, "저장공간이 접근 불가능한 기기입니다", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    //기기에 저장될 이미지 파일경로, 폴더 만들기
    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imageFile = null;
        //getExternalStorageDirectory()는 API lv29에서는 없어짐
        //이 방법은 API 레벨 29에서 사용되지 않습니다.
        // 사용자 개인 정보를 향상시키기 위해 공유 / 외부 저장 장치에 대한 직접 액세스는 사용되지 않습니다.
        // 앱이 대상인 Build.VERSION_CODES.Q경우이 메소드에서 반환 된 경로는 더 이상 앱에 직접 액세스 할 수 없습니다.
        // 앱은 다음과 같은 대안을 마이그레이션하여 공유 / 외부 저장 장치에 저장된 콘텐츠에 액세스를 계속 할 수 있습니다
        // => Context#getExternalFilesDir(String), MediaStore또는 Intent#ACTION_OPEN_DOCUMENT.
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "ggp");
        if (!storageDir.exists()) {
            Log.i("mCurrentPhotoPath1", storageDir.toString());
            storageDir.mkdirs();
        }

        imageFile = new File(storageDir, imageFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }


    //사진첩에 찍은 사진 저장/ 크롭한 사진 저장
    //누가버전 이후 잘 안될 수 있음
    private void galleryAddPic(){
        Log.i("galleryAddPic", "Call");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // 해당 경로에 있는 파일을 객체화(새로 파일을 만든다는 것으로 이해하면 안 됨)
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);

        sendBroadcast(mediaScanIntent);
        Toast.makeText(ReviewImgEditActivity.this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }



    //앨범
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        RecyclerView recyclerView = findViewById(R.id.recycler_edit_photo);
        review_lmg_edit_Photo_total = findViewById(R.id.review_lmg_edit_Photo_total);

        switch (requestCode){
            //앨범에서 다중 선택
            case PICTURE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    //ClipData 또는 Uri를 가져온다
                    Uri uri = data.getData();
                    Log.d("URI여기여기",":"+uri);
                    ClipData clipData = data.getClipData();
                    Log.d("ClipData", ":" + clipData);

                    //이미지 URI 를 이용하여 이미지뷰에 순서대로 세팅한다.
                    if (clipData != null) {
                        for (int i = 0; i < clipData.getItemCount(); i++) {
//                    if (i < clipData.getItemCount()) {
                            Uri urione = clipData.getItemAt(i).getUri();

                            String rwPhoto = urione.toString();
                            //reviewPhoto.putExtra(String.valueOf(i),rwPhoto);
                            Log.d("앨범 다중선택 uri",rwPhoto);
                            Log.d("앨범선택","22");


                            //String item = reviewPhoto.getStringExtra(String.valueOf(i));
                            list.add(0,rwPhoto);

                            Log.d("리사이클러뷰 츄가",";"+list.get(i));
                            Log.d("리사이클러뷰 추가",";"+list.get((list.size())-1));

                            // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                            reviewImgAdapter = new ReviewImgAdapter(list, ReviewImgEditActivity.this);
                            recyclerView.setAdapter(reviewImgAdapter);

                            //변경된 데이터 어뎁터에 알려주기
                            reviewImgAdapter.notifyDataSetChanged();
                            Log.d("리사이클러뷰 앨범 list",":"+list);

                            //추가한 사진 갯수 저장하기
                            String listSize = String.valueOf(list.size());
                            Log.d("리사이클러뷰 앨범 list 크기",":"+listSize);

                            review_lmg_edit_Photo_total.setText(listSize);

                        }

                    }else if(uri != null){

                        String rwPhoto = uri.toString();
                        Log.d("앨범 한장선택 uri",rwPhoto);
                        Log.d("앨범선택","333");

                        list.add(0,rwPhoto);

                        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                        reviewImgAdapter = new ReviewImgAdapter(list, ReviewImgEditActivity.this);
                        recyclerView.setAdapter(reviewImgAdapter);

                        //변경된 데이터 어뎁터에 알려주기
                        reviewImgAdapter.notifyDataSetChanged();

                        Log.d("리사이클러뷰 앨범 list",":"+list);

                        //추가한 사진 갯수 저장하기
                        String listSize = String.valueOf(list.size());
                        Log.d("리사이클러뷰 앨범 list 크기",":"+listSize);

                        review_lmg_edit_Photo_total.setText(listSize);
                    }
                }else {
                    Toast.makeText(ReviewImgEditActivity.this, "앨범에서 선택하기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            //사진찍기
            case REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    try {

                        Log.i("REQUEST_TAKE_PHOTO", "OK");
//                       //갤러리에 찍은 사진 저장
                        galleryAddPic();

                        String rwPhoto = imageUri.toString();
                        reviewPhoto.putExtra("0",rwPhoto);

                        Log.d("카메라 찍기 uri",":"+imageUri);
                        Log.d("카메라 찍기 string",":"+rwPhoto);


                        list.add(0,rwPhoto);

                        Log.d("리사이클러뷰 츄가",";"+list);

                        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                        reviewImgAdapter = new ReviewImgAdapter(list, ReviewImgEditActivity.this);
                        recyclerView.setAdapter(reviewImgAdapter);

//                        //ItemTouchHelper 생성
//                        helper = new ItemTouchHelper(new ItemTouchHelperCallback(reviewImgAdapter));
//                        //RecyclerView에 ItemTouchHelper 붙이기
//                        helper.attachToRecyclerView(recyclerView);

                        //변경된 데이터 어뎁터에 알려주기
                        reviewImgAdapter.notifyDataSetChanged();

                        //추가한 사진 갯수 저장하기
                        String listSize = String.valueOf(list.size());
                        Log.d("리사이클러뷰 앨범 list 크기",":"+listSize);

                        review_lmg_edit_Photo_total.setText(listSize);


                    } catch (Exception e) {
                        Log.e("REQUEST_TAKE_PHOTO", e.toString());
                    }
                } else {
                    Toast.makeText(ReviewImgEditActivity.this, "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
                }
                break;


        }

//        //여기서 List출력하면 전체가 나오나? 수정
//        //사진추가 버튼 눌렀을 때 저장되는 곳.
//        if(list.size() != 0){
//            Log.d("전체 리스트를 보고 싶다",":"+list);
//            //전체리스트 있으면 sharedPreferences에 저장
//            setStringArrayPref(photoListKey,photoListNumKey,list);
//        }else{
//            Log.d("전체 리스트가 안나온다아ㅏ",":"+list);
//        }


        //추가한 사진 갯수 저장하기
        String listSize = String.valueOf(list.size());
        Log.d("리사이클러뷰 앨범 list 크기",":"+listSize);
        review_lmg_edit_Photo_total.setText(listSize);
        Log.d("사진추가 후 onResult() get?","  ");

    }








    //셰어드에 저장된 리스트 값 불러오기
    //SharedPreferences에서 Json형식의 String을 가져와서 다시 ArrayList로 변환
    private ArrayList<String> getStringArrayPref(String key) {
        sharedPreferences = getSharedPreferences("reviewPhotoList",MODE_PRIVATE);
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
        sharedPreferences = getSharedPreferences("reviewPhotoList",MODE_PRIVATE);

        int arraySize = sharedPreferences.getInt(num_key,0);
        Log.d("불러오기 Num", ":"+arraySize);

        return arraySize;
    }

    //셰어드에 저장
    //ArrayList를 Json으로 변환하여 SharedPreferences에 String을 저장
    //key값을 무엇으로 할지_여기서는 중요하지 않음 최종 저장할때가 중요
    //여기서 리뷰 작성 페이지로 넘길때 같이 넘겨야 할 중요 정보는, 대표사진의 index값.
    private void setStringArrayPref(String key, String num_key, ArrayList<String> values) {
        sharedPreferences = getSharedPreferences("reviewPhotoList",MODE_PRIVATE);
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
        sharedPreferences = getSharedPreferences("reviewPhotoList",MODE_PRIVATE);
        String json = sharedPreferences.getString(key, null);
        Log.d("저장 >> 삭제 전", ":"+json);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
        editor.remove(num_key);
        editor.apply();

        sharedPreferences = getSharedPreferences("reviewPhotoList",MODE_PRIVATE);
        int n = sharedPreferences.getInt(num_key,9999);
        Log.d("저장 >> 삭제 후 Num", ":"+n);
        String js = sharedPreferences.getString(key, null);
        Log.d("저장 >> 삭제", ":"+js);
    }


    private void setReviewPhotoListAll(String key, String num_key, ArrayList<String> values) {
        sharedPreferences = getSharedPreferences("reviewPhotoListALL",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        removeReviewPhotoListAll(key, num_key);


        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            String item = values.get(i);
            Log.d("setReviewPhotoListAll","어뎁터 저장되는 String 값:"+item);
            Log.d("setReviewPhotoListAll","어뎁터 저장되는 JSON 값: "+a.put(item));
//            a.put(item);
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
            Log.d("setReviewPhotoListAll","어뎁터 저장되는 pref 값:"+sharedPreferences);

        } else {
            editor.putString(key, null);
        }
        Log.d("setReviewPhotoListAll"," 어뎁터_저장되는 아템 전체 갯수:"+values.size());
        editor.putInt(num_key, values.size());

        editor.apply();
    }

    private void removeReviewPhotoListAll(String key, String num_key) {
        sharedPreferences = getSharedPreferences("reviewPhotoListALL",MODE_PRIVATE);
        String json = sharedPreferences.getString(key, null);
        Log.d("setReviewPhotoListAll", " 저장 >> 삭제 전:"+json);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
        editor.remove(num_key);
        editor.apply();

        sharedPreferences = getSharedPreferences("reviewPhotoListALL",MODE_PRIVATE);
        int n = sharedPreferences.getInt(num_key,9999);
        Log.d("setReviewPhotoListAll", " 저장 >> 삭제 후 Num:"+n);
        String js = sharedPreferences.getString(key, null);
        Log.d("setReviewPhotoListAll", " 저장 >> 삭제:"+js);
    }






}
