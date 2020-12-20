package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WriteCardActivity extends AppCompatActivity {
    EditText editTitle;
    EditText editContents;
    String sendTitle;
    String sendContents;
    Button submitButton;
    String TAG = "로그";
    ImageView ivImage;
    Button cancel;
    String currentUser;
    ShareData shareData;
    Button addPicture;
    Button btPhotoTaking;
    Uri imageUri;
    private static final int TAKE_PICTURE = 2;
    private final int REQ_IMAGE_SELFECT = 1;
    private final int PICK_IMAGE_REQUEST = 0;

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "WriteCardActivity - onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "WriteCardActivity - onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "WriteCardActivity - onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "WriteCardActivity - onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "WriteCardActivity - onDestroy() called");
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_card);

        Log.d(TAG, "WriteCardActivity - onCreate() called");
        editTitle = findViewById(R.id.editTitle);
        editContents = findViewById(R.id.editContents);
        submitButton = findViewById(R.id.submit);
        cancel = findViewById(R.id.cancel);
        addPicture = findViewById(R.id.addPicture);
        ivImage = findViewById(R.id.image);
        btPhotoTaking = findViewById(R.id.photoTaking);

        Intent intent = getIntent();
        currentUser = intent.getStringExtra("userNumber");
        //카메라접근 권한 요청 함수.
        checkPermission();
        //글 게시버튼
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //게시글을 작성한 년,월,일 출력
                @SuppressLint("SimpleDateFormat") String today = new SimpleDateFormat("yy/MM/dd").format(new Date());
                sendTitle = editTitle.getText().toString();
                sendContents = editContents.getText().toString();
                //게시글 작성하는 회원의 정보를 확인하여 회원의 정보를 인덱스로 찾는다
                //회원의 list 인덱스는 로그인 할때 회원의 인덱스를 확인하고 currentUser 변수에 저장.
                shareData = new ShareData(String.valueOf(imageUri), sendTitle,
                        AccountActivity.getUserInformation().get(Integer.parseInt(MainActivity.loginUserNumber)).getName(),
                        today, sendContents);
                //게시글을 작성한 회원은 myContents 리스트에 작성글을 저장 한다.
                AccountActivity.getUserInformation().get(Integer.parseInt(MainActivity.loginUserNumber)).getMyContentsList().add(shareData);
                Intent intent = new Intent(WriteCardActivity.this, ShareActivity.class);
                //인텐트로 객체를 전달하기 위해서 전달할 객체에 implements Serializable 한다.
                editTitle.setText(null);
                editContents.setText(null);
                intent.putExtra("submit", shareData);
                startActivity(intent);
                finish();
            }
        });
        //사진 추가 번튼
        addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                }
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                        PICK_IMAGE_REQUEST);
            }
        });
        //사진촬영버튼
        btPhotoTaking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //사진촬영 후 갤러리에 저장하고 Uri로 이미지를 읽어온다.
                //썸네일을 ivImage에 등록 한다.
                dispatchTakePictureIntent();
            }
        });
        //취소버튼
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //취소버튼 눌렀을때 액티비티 종료 하고 editText 내용 삭제
                editTitle.setText(null);
                editContents.setText(null);
                Intent intent = new Intent(WriteCardActivity.this, ShareActivity.class);
                startActivity(intent);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 설정 완료");
            } else {
                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(WriteCardActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    public static Bitmap rotatelImage(Bitmap source, float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source,0,0,source.getWidth(), source.getHeight(),matrix,true);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkPermission() {
        //카메라촬영을 하기위한 권한 확인.
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "권한설정완료");
        } else {
            ActivityCompat.requestPermissions(WriteCardActivity.this, new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            Log.d(TAG, "권한설정요청");
        }
    }
    String currentPhotoPath;

    private File createImageFile() throws IOException {
        //파일을 생성할때 사용될 현재 시간
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //저장될 파일의 이름 형식.
        String imageFileName = "JPEG_" + timeStamp + "_";
        //저장소의 폴더위치
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //파일의 전체 이름 형식.
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        //이미지 파을의 절대위치를 저장.
        currentPhotoPath = image.getAbsolutePath();
        //파일이름을 리턴
        return image;
    }
    static final int REQUEST_TAKE_PHOTO = 1;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.myapplication.fileprovider",
                        photoFile);
                imageUri = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    //사진첩에 접근해서 응답처리하는 함수.
    //requestCode = startActivityForResult 에서  요청한 int 값 , resultCode 는 startActivityForResult 에서 요청 받은 후 다시 결과를 전달해줄 int
    //onActivityResult 에서 요청값, 결과값, 인텐트를 모두 받아 처리한다.
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //사진 촬영에 대한 조건문
        try {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO: {
                    if (resultCode == RESULT_OK) {
                        File file = new File(currentPhotoPath);
                         Bitmap bitmap = MediaStore.Images.Media
                                .getBitmap(getContentResolver(), Uri.fromFile(file));
                        if (bitmap != null) {
                            ExifInterface ei = new ExifInterface(currentPhotoPath);
                            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_UNDEFINED);
                            Bitmap rotatedBitmap = null;
                            switch(orientation) {
                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    rotatedBitmap = rotatelImage(bitmap, 90);
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    rotatedBitmap = rotatelImage(bitmap, 180);
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    rotatedBitmap = rotatelImage(bitmap, 270);
                                    break;
                                case ExifInterface.ORIENTATION_NORMAL:
                                default:
                                    rotatedBitmap = bitmap;
                            }
                            ivImage.setImageBitmap(rotatedBitmap);
                        }
                    }
                    break;
                }
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
        if (requestCode == PICK_IMAGE_REQUEST) {
            //요청에대한 결과값이 ture면 진행.
            if (resultCode == RESULT_OK) {
                try {
                    if (intent != null) {
                        //선택한 이미지를 Uri 로저장.
                        imageUri = intent.getData();
                        this.grantUriPermission(this.getPackageName(), imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            if (imageUri != null) {
                                this.getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
                            }
                        }
                        SharedPreferences preferences = getSharedPreferences("imageUri", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("image", String.valueOf(imageUri));
                        editor.apply();
                        ivImage.setImageURI(imageUri);
                        ivImage.invalidate();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}


//                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
//                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                    // 이미지 표시
//                    chosenImage.setImageBitmap(bitmap);
//                    SharedPreferences sharedPreferences = getSharedPreferences("imageBackUp",MODE_PRIVATE);
//                    SharedPreferences.Editor imageEditor = sharedPreferences.edit();
//
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
//                    byte[] imageByte = stream.toByteArray();
//                    String temp = Base64.encodeToString(imageByte, Base64.DEFAULT);
//                    imageEditor.putString("bitmapImage",temp);
//                    Toast.makeText(this, "사진을 등록했습니다.", Toast.LENGTH_SHORT).show();
//                    imageEditor.apply();
                    //content://com.android.providers.media.documents/document/image%3A170375
                    //선택된 URI 형식
//비트맵을 스트링으로 전환하는 함수
//    public String BitmapToString(Bitmap bitmp) {
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] imageByte = stream.toByteArray();
//        String temp = Base64.encodeToString(imageByte, Base64.DEFAULT);
//        return temp;
//    }

//권한이 있는지 없는지 확인하는 함수.
//    public void checkSelfPermission() {
//        String permissionCheck = "";
//        //파일 읽기 권한 확인
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
//                PackageManager.PERMISSION_GRANTED) {
//            permissionCheck += Manifest.permission.READ_EXTERNAL_STORAGE + " ";
//        }
//        // 파일 쓰기 권한 확인
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            permissionCheck += Manifest.permission.WRITE_EXTERNAL_STORAGE + " ";
//        }
//        if (TextUtils.isEmpty(permissionCheck) == false) {
//            // 권한 요청
//            ActivityCompat.requestPermissions(this, permissionCheck.trim().split(" "), 1);
//        } else {
//            // 모두 허용 상태
//            Toast.makeText(this, "권한허용", Toast.LENGTH_SHORT).show();
//        }
//    }
/*
갤러리를 통해 이미지를 받을 경우 해당 이미지의 파일 경로

즉, uri 정보를 받게 됩니다.

uri = (Uri) data.getData();



intent 에 담아서 넘기려면 uri 정보를 string으로 변환해서 넘기는게 간편하고,

intent.putExtra("uri", uri.toString());



받는 쪽에서는 아래와 같이 uri 정보를 받을 수 있습니다.

Uri uri = Uri.parse(extras.getString("uri"));



해당 이미지의 비트맵을 받고 싶으면 이런형태가 되면 비트맵 이미지를 얻을 수 있습니다.

BitmapFactory.decodeFile(extras.getString("uri"));
 */

/*
intent intent = new intent(this, Main.class);

intent.putExtra("image", BITMAP);
startActivity(intent);





받는 Activity

Intent intent = getIntent();

(Bitmap)intent.getParcelableExtra("image")



출처: https://it77.tistory.com/221 [시원한물냉의 사람사는 이야기]
 */
