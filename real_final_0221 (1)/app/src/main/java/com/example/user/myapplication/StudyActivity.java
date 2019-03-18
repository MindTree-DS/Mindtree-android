//package com.example.user.myapplication;
//
//import android.Manifest;
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.AsyncTask;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.design.widget.BottomNavigationView;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Base64;
//import android.util.Log;
//import android.util.Pair;
//import android.view.LayoutInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.ByteArrayOutputStream;
//import java.util.ArrayList;
//
//public class StudyActivity extends AppCompatActivity {
//
//    private static final int MY_CAMERA_REQUEST_CODE = 100;
//    private ImagePro imagePro;
//    private ImagePro.ImageDetails imageDetails;
//    private ImageView ivCrop;
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == ImagePro.CAMERA_CODE) {
//            imageDetails = imagePro.getImagePath(ImagePro.CAMERA_CODE, RESULT_OK, data);
////             Log.d(" ### ", imageDetails.getPath() + ", " + imageDetails.getBitmap().getConfig());
//        }
//        else if (requestCode == ImagePro.GALLERY_CODE) {
////            Log.d(" $$$ ","resultCode:"+resultCode);
//            imageDetails = imagePro.getImagePath(ImagePro.GALLERY_CODE, RESULT_OK, data);
//
//        }
//
//        this.showRegisterDialog(imageDetails.getBitmap());
//        ivCrop.setImageBitmap(imageDetails.getBitmap());
//    }
//
//    protected String bitmapToBase64String(byte [] b) {
//        String b64Encoded = Base64.encodeToString(b, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING );
//        return b64Encoded;
//    }
//
//    protected String getRecogReqString(byte [] b) {
//        String b64Encoded=bitmapToBase64String(b);
//        JSONObject jsonObject = new JSONObject();
//        try {
////            jsonObject.put("req",""+System.currentTimeMillis());
////            jsonObject.put("name",name);
//            jsonObject.put("ext","png");
//            jsonObject.put("img",b64Encoded);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return jsonObject.toString();
//    }
//
//    public void showRegisterDialog(final Bitmap b){
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        b.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        final byte[] ba = stream.toByteArray();
//
//        final LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
//        View mView = layoutInflaterAndroid.inflate(R.layout.activity_study, null);
//        ImageView imageView=mView.findViewById(R.id.user);
//        imageView.setImageBitmap(b);
//
//        String reqMsg=getRecogReqString(ba);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_study);
//
//        ImageButton fab = (ImageButton) findViewById(R.id.add_photo);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
//                }
//                imagePro.openImagePickOption();
//
//            }
//        });
//
//        imagePro = new ImagePro(this);
//        ivCrop = (ImageView) findViewById(R.id.user);
//
//        // 버튼 클릭시 사용되는 리스너를 구현합니다.
//
//        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView_main_menu);
//        bottomNavigationView.setOnNavigationItemSelectedListener(
//                new BottomNavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//
//                        // 어떤 메뉴 아이템이 터치되었는지 확인합니다.
//                        switch (item.getItemId()) {
//
//                            case R.id.study:
//
//                                Intent startIntent = new Intent(StudyActivity.this,StudyActivity.class);
//                                startActivity(startIntent);
//
//                                return true;
//
//                            case R.id.home:
//
//                                Intent start2Intent = new Intent(StudyActivity.this,MainActivity.class);
//                                startActivity(start2Intent);
//
//                                return true;
//
//                            case R.id.graph:
//
//                                Intent start3Intent = new Intent(StudyActivity.this,GraphActivity.class);
//                                startActivity(start3Intent);
//
//                                return true;
//                        }
//                        return false;
//                    }
//                });
//    }
//
//
//
//
//
//}