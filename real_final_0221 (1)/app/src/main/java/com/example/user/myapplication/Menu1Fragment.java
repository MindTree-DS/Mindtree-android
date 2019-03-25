package com.example.user.myapplication;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;

public class Menu1Fragment extends Fragment {

    private ImagePro imagePro;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private ImageView ivCrop;
    private ImagePro.ImageDetails imageDetails;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImagePro.CAMERA_CODE) {
            imageDetails = imagePro.getImagePath(ImagePro.CAMERA_CODE, RESULT_OK, data);
            Log.e("^^","requestCode"+requestCode);
            Log.d(" ### ", imageDetails.getPath() + ", " + imageDetails.getBitmap().getConfig());
        }
        else if (requestCode == ImagePro.GALLERY_CODE) {
//            Log.d(" $$$ ","resultCode:"+resultCode);
            imageDetails = imagePro.getImagePath(ImagePro.GALLERY_CODE, RESULT_OK, data);

        }


       ivCrop.setImageBitmap(imageDetails.getBitmap());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_menu1, container, false);
        ImageButton fab = (ImageButton) v.findViewById(R.id.add_photo);
        Button btn1=(Button)v.findViewById(R.id.push_button1);
        Button btn2=(Button)v.findViewById(R.id.push_button2);
        Button btn3=(Button)v.findViewById(R.id.push_button3);
        Button btn4=(Button)v.findViewById(R.id.push_button4);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
                }
                imagePro.openImagePickOption();

            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "행복", Toast.LENGTH_LONG).show();


            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "화남", Toast.LENGTH_LONG).show();

            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "즐거움", Toast.LENGTH_LONG).show();

            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getContext(), "슬픔", Toast.LENGTH_LONG).show();
            }
        });

        imagePro = new ImagePro(this.getActivity());
        ivCrop = (ImageView) v.findViewById(R.id.user);
        return v;
    }
}