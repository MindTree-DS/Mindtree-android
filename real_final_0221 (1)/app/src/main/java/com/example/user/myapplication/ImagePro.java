package com.example.user.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImagePro
{
    public static String TAG = "ImagePro";
    Activity activity;
    Uri mImageCaptureUri;
    public static int CAMERA_CODE = 64;
    public static int GALLERY_CODE = 74;
    public static int CROPPING_CODE = 84;
    private final static int REQUEST_PERMISSION_REQ_CODE = 704;

    public ImagePro(Activity activity) {

        this.activity = activity;
        this.outPutFile = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_REQ_CODE);
        }
    }

    private void LogToast(String message) {

        try {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, message);
    }

    private void Toast(String message) {

        try {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Log(String message) {

        Log.d(TAG, message);
    }

    /**
     * This function return captured image path
     *
     * @param requestCode on activity result requestCode
     * @param resultCode on activity result resultCode
     * @param intent on activity result intent
     * @return ImageDetails values
     */
    public ImageDetails getImagePath(int requestCode, int resultCode, Intent intent) {

        ImageDetails imageDetails = new ImageDetails();
        if(resultCode == Activity.RESULT_OK) {

            if(requestCode == CAMERA_CODE) {
                imageDetails.setUri(mImageCaptureUri);
                imageDetails.setPath(mImageCaptureUri.getPath());
                Bitmap bitmap = null;

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), mImageCaptureUri);

                    ExifInterface exif = new ExifInterface(mImageCaptureUri.getPath());

                    Log.d(" ### ",exif.toString());
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                    bitmap = rotateBitmap(bitmap, orientation);

                } catch (IOException e) {
                    LogToast(e.getMessage());
                    e.printStackTrace();
                }
                imageDetails.setBitmap(bitmap);
                imageDetails.setFile(new File(mImageCaptureUri.getPath()));

            } else if(requestCode == GALLERY_CODE) {

                Uri uri = intent.getData();
                imageDetails.setUri(uri);
                Bitmap bitmap = null;

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);

                    ExifInterface exif = new ExifInterface(getRealPathFromURI(activity,uri));
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                    bitmap = rotateBitmap(bitmap, orientation);

                } catch (IOException e) {
                    LogToast(e.getMessage());
                    e.printStackTrace();
                }
                imageDetails.setBitmap(bitmap);
                imageDetails.setFile(new File(getRealPathFromURI(activity,uri) ));
                imageDetails.setPath(getRealPathFromURI(activity,uri));

            } else if(requestCode == CROPPING_CODE) {
                try {
                    if(outPutFile.exists()){
                        imageDetails.setUri(FileProvider.getUriForFile(activity, "com.example.user.myapplication.fileprovider", outPutFile));//바꾼코드
                        imageDetails.setFile(outPutFile);
                        imageDetails.setPath(outPutFile.getPath());
                        Bitmap photo = decodeFile(outPutFile);


                        imageDetails.setBitmap(photo);
                    }
                    else {
                        LogToast("Error while save image");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogToast(e.getMessage());
                }
            }

        } else {
            LogToast("user cancelled.");
        }
        return imageDetails;
    }
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Open image pick dialog.<br/>
     * CAMERA_CODE</br>
     * GALLERY_CODE
     */
    public void openImagePickOption() {

        final CharSequence[] items = { "Capture Photo", "Choose from Gallery", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("사진 선택!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("사진 찍기")) {

                    captureImage();

                } else if (items[item].equals("앨범에서 사진선택")) {

                    pickImage();

                } else if (items[item].equals("취소")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /**
     * decode from file to bitmap
     * @param f file
     * @return Bitmap data
     */
    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 512;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {

            Log(e.getMessage());
        }
        return null;
    }

    /**
     * Capture image using camera <br/>
     * REQUEST_CODE = ImagePro.CAMERA_CODE
     */
    public void captureImage() {
        if(activity != null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp1.jpg");
            mImageCaptureUri = FileProvider.getUriForFile(activity,"com.example.user.myapplication.fileprovider", f);// Change
            Log.i("%%%%%%", f.toString());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            activity.startActivityForResult(intent, CAMERA_CODE);
        } else {
            LogToast("Activity not assigned");
        }
    }

    /**
     * pick image from gallery
     */
    public void pickImage() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(i, GALLERY_CODE);
    }

    /**
     * cropping the uri image
     * @param uri - open cropping dialog using the uri data
     */
    public void croppingImage(Uri uri) {
        CroppingIMG(uri);
    }

    int CROP_IMG_X=512;
    int CROP_IMG_Y=512;

    public void croppingImage(Uri uri, int cropX, int cropY) {

        CROP_IMG_X = cropX;
        CROP_IMG_Y = cropY;

        CroppingIMG(uri);
    }

    File outPutFile=null;

    private void CroppingIMG(Uri uri) {

        final ArrayList<CroppingOption> cropOptions = new ArrayList<>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = activity.getPackageManager().queryIntentActivities( intent, 0 );
        int size = list.size();
        if (size == 0) {
            LogToast("Can't find image croping app");
        } else {
            intent.setData(uri);
            intent.putExtra("outputX", CROP_IMG_X);
            intent.putExtra("outputY", CROP_IMG_Y);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);

            //Create output file here
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(activity, "com.example.user.myapplication.fileprovider", outPutFile));//바꾼코드
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPutFile));

            if (size == 1) {
                Intent i   = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                activity.startActivityForResult(i, CROPPING_CODE);
            } else {
                for (ResolveInfo res : list) {
                    final CroppingOption co = new CroppingOption();

                    co.title = activity.getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = activity.getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }

//                CropingOptionAdapter adapter = new CropingOptionAdapter(activity.getApplicationContext(), cropOptions);/////////////////////////////////////////////////////////////

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Choose Cropping App");
                builder.setCancelable(false);
//                builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int item ) {
//                        activity.startActivityForResult( cropOptions.get(item).appIntent, CROPPING_CODE);
//                    }
//                });

                builder.setOnCancelListener( new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel( DialogInterface dialog ) {

                        if (mImageCaptureUri != null ) {
                            activity.getContentResolver().delete(mImageCaptureUri, null, null );
                            mImageCaptureUri = null;
                        }
                    }
                } );

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    /**
     * Capture image using camera<br/>
     * REQUEST_CODE = User defined code<br/>
     * <br/>
     * @param iRequestCode User defined code
     */
    public void captureImage(int iRequestCode) {

        CAMERA_CODE = iRequestCode;
        captureImage();
    }

    /**
     * get path, bitmap, file and uri from image details object
     */
    public class ImageDetails {

        String path="";
        Bitmap bitmap=null;
        File file=null;
        Uri uri=null;

        public Uri getUri() {
            return uri;
        }

        public void setUri(Uri uri) {
            this.uri = uri;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }
    }

    /**
     * Created by DP on 7/12/2016.
     */
    public class CroppingOption {
        public CharSequence title;
        public Drawable icon;
        public Intent appIntent;
    }

//    public class CropingOptionAdapter extends ArrayAdapter {
//        private ArrayList<CroppingOption> mOptions;
//        private LayoutInflater mInflater;
//
//        public CropingOptionAdapter(Context context, ArrayList<CroppingOption> options) {
//
//            super(context, R.layout.croping_selector, options);
//            mOptions  = options;
//            mInflater = LayoutInflater.from(context);
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup group) {
//            if (convertView == null)
//                convertView = mInflater.inflate(R.layout.croping_selector, null);
//
//            CroppingOption item = mOptions.get(position);
//
//            if (item != null) {
//                ((ImageView) convertView.findViewById(R.id.img_icon)).setImageDrawable(item.icon);
//                ((TextView) convertView.findViewById(R.id.txt_name)).setText(item.title);
//
//                return convertView;
//            }
//
//            return null;
//        }
//    }
}