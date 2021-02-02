package com.soulfriends.meditation.view;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoBaseActivity extends BaseActivity {

    private static final int MY_PERMISSION_CAMERA = 1111;
    private static final int REQUEST_TAKE_PHOTO = 2222;
    private static final int REQUEST_TAKE_ALBUM = 3333;
    private static final int REQUEST_IMAGE_CROP = 4444;


    private static final int SELECT_IMAGE = 5555;
    private static final int SELECT_AUDIO = 6666;
    private static final int SELECT_MOVIE = 7777;

    ImageView iv_view = null;

    String mCurrentPhotoPath;

    Uri imageUri;
    Uri photoURI, albumURI;

    String Thumb_FilePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPermission();
    }

    public void captureCamera() {
        String state = Environment.getExternalStorageState();
        // 외장 메모리 검사
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Log.e("captureCamera Error", ex.toString());
                }
                if (photoFile != null) {
                    // getUriForFile의 두 번째 인자는 Manifest provier의 authorites와 일치해야 함

                    Uri providerURI;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        // API 24 이상 일경우..
                        String strpa = getApplicationContext().getPackageName();
                        providerURI = FileProvider.getUriForFile(this,
                                getApplicationContext().getPackageName() + ".fileprovider", photoFile);
                    } else {// API 24 미만 일경우..
                        providerURI = Uri.fromFile(photoFile);
                    }

                    //Uri providerURI = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    imageUri = providerURI;

                    // 인텐트에 전달할 때는 FileProvier의 Return값인 content://로만!!, providerURI의 값에 카메라 데이터를 넣어 보냄
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI);

                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        } else {
            Toast.makeText(this, "저장공간이 접근 불가능한 기기입니다", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "gyeom");

        if (!storageDir.exists()) {
            Log.i("mCurrentPhotoPath1", storageDir.toString());
            storageDir.mkdirs();
        }

        imageFile = new File(storageDir, imageFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }


    public void getAlbum() {
//        Log.i("getAlbum", "Call");
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
//        intent.action = Intent.ACTION_GET_CONTENT;//	//추가됨
//        startActivityForResult(intent, REQUEST_TAKE_ALBUM);

        Intent intent = new Intent();
        //intent.setAction(Intent.ACTION_GET_CONTENT); // ACTION_PICK은 사용하지 말것, deprecated + formally
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(Intent.createChooser(intent, "Get Album"), REQUEST_TAKE_ALBUM);

        // startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_TAKE_ALBUM);


    }

    public void galleryAddPic() {
        Log.i("galleryAddPic", "Call");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // 해당 경로에 있는 파일을 객체화(새로 파일을 만든다는 것으로 이해하면 안 됨)
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();

    }

    // 카메라 전용 크랍
    public void cropImage() {

        // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
        // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(photoURI, "image/*");
//
//
//        // CROP할 이미지를 200*200 크기로 저장
//        intent.putExtra("outputX", 200); // CROP한 이미지의 x축 크기
//        intent.putExtra("outputY", 200); // CROP한 이미지의 y축 크기
//        intent.putExtra("aspectX", 1); // CROP 박스의 X축 비율
//        intent.putExtra("aspectY", 1); // CROP 박스의 Y축 비율
//        intent.putExtra("scale", true);
//        intent.putExtra("return-data", true);
//        startActivityForResult(intent, REQUEST_IMAGE_CROP); // CROP_FROM_CAMERA case문 이동

        Log.i("cropImage", "Call");
        Log.i("cropImage", "photoURI : " + photoURI + " / albumURI : " + albumURI);

        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        // 50x50픽셀미만은 편집할 수 없다는 문구 처리 + 갤러리, 포토 둘다 호환하는 방법
        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoURI, "image/*");
        //cropIntent.putExtra("outputX", 200); // crop한 이미지의 x축 크기, 결과물의 크기
        //cropIntent.putExtra("outputY", 200); // crop한 이미지의 y축 크기
        cropIntent.putExtra("aspectX", 1); // crop 박스의 x축 비율, 1&1이면 정사각형
        cropIntent.putExtra("aspectY", 1); // crop 박스의 y축 비율
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("output", albumURI); // 크랍된 이미지를 해당 경로에 저장
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO: {
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Log.i("REQUEST_TAKE_PHOTO", "OK");
                        //galleryAddPic();

                        photoURI = imageUri;
                        File albumFile = null;
                        albumFile = createImageFile();
                        albumURI = Uri.fromFile(albumFile);
                        cropImage();

                        //iv_view.setImageURI(imageUri);
                    } catch (Exception e) {
                        Log.e("REQUEST_TAKE_PHOTO", e.toString());
                    }
                } else {
                    Toast.makeText(this, "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            break;

            case REQUEST_TAKE_ALBUM: {
                if (resultCode == Activity.RESULT_OK) {

                    if (data.getData() != null) {
                        try {
                            File albumFile = null;
                            albumFile = createImageFile();
                            photoURI = data.getData();
                            albumURI = Uri.fromFile(albumFile);
                            cropImage();
                        } catch (Exception e) {
                            Log.e("TAKE_ALBUM_SINGLE ERROR", e.toString());
                        }
                    }
                }
            }
            break;

            case REQUEST_IMAGE_CROP: {
                if (resultCode == Activity.RESULT_OK) {

                    galleryAddPic();

                    // 이미지 show
                    //iv_view.setImageURI(albumURI);

                    OnSuccess_ImageCrop();
                }
            }
            break;
            case SELECT_IMAGE: {
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();
                    String path = getPath(uri);
                    String name = getName(uri);
                    String uriId = getUriId(uri);
                    //Log.e("###", "실제경로 : " + path + "\n파일명 : " + name + "\nuri : " + uri.toString() + "\nuri id : " + uriId);
                }
            }
            break;
            case SELECT_AUDIO: {
                if (resultCode == Activity.RESULT_OK) {

                    Toast.makeText(this, "오디오 파일 선택", Toast.LENGTH_SHORT).show();
                    Uri uri = data.getData();
                    String path = getPath(uri);
                    String name = getName(uri);
                    String uriId = getUriId(uri);

                    MediaPlayer mediaPlayer = MediaPlayer.create(this, uri);

                    int duration = mediaPlayer.getDuration();

                    OnSuccess_SelectAudioFile(path, duration);

                    mediaPlayer.release();

                    //mediaPlayer.start();
                    //Log.e("###", "실제경로 : " + path + "\n파일명 : " + name + "\nuri : " + uri.toString() + "\nuri id : " + uriId);
                }
            }
            break;
        }
    }

    public void OnSuccess_SelectAudioFile(String path_file, int duration) {

    }


    public void OnSuccess_ImageCrop() {

    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{..}의 요청으로 넘어감
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))) {
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_CAMERA:
                for (int i = 0; i < grantResults.length; i++) {
                    // grantResults[] : 허용된 권한은 0, 거부한 권한은 -1
                    if (grantResults[i] < 0) {
                        Toast.makeText(this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // 허용했다면 이 부분에서..

                break;
        }
    }

    //-------------------------------------------------------------------------
    //
    //-------------------------------------------------------------------------

    // 이미지 선택
    public void doSelectImage() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            startActivityForResult(i, SELECT_IMAGE);
        } catch (android.content.ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 오디오 선택
    public void doSelectAudio() {

       // Intent i = new Intent(Intent.ACTION_GET_CONTENT, Uri.fromFile(Environment.getExternalStorageDirectory()));
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("audio/*");
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            startActivityForResult(i, SELECT_AUDIO);
        } catch (android.content.ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 동영상선택
    public void doSelectMovie() {

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("video/*");
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            startActivityForResult(i, SELECT_MOVIE);
        } catch (android.content.ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 실제 경로 찾기
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    // 파일명 찾기
    public String getName(Uri uri) {
        String[] projection = {MediaStore.Images.ImageColumns.DISPLAY_NAME};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    // uri 아이디 찾기
    public String getUriId(Uri uri) {
        String[] projection = {MediaStore.Images.ImageColumns._ID};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    //----------------------------------------------------------
    // 오디오 파일 재생
    //----------------------------------------------------------

    private MediaPlayer mediaPlayer = null;

    public void Play_Audio(String path_file)  {

        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        try {

            mediaPlayer.setDataSource(path_file);
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
        catch(IllegalStateException e)
        {
            e.printStackTrace();
        }
    }

    public void Stop_Audio()
    {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
