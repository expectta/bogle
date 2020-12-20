package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
//
public class PostModifyActivity extends AppCompatActivity {
    ImageView modifyImage;
    String title, contents;
    EditText modifyEditTitle, modifyEditContents;
    String TAG = "로그";
    private Uri imageUri;
    private int PICK_IMAGE_REQUEST=0;

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "ModifyActivity - onStart() called");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "ModifyActivity - onResume() called");
    }
    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "ModifyActivity - onPause() called");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "ModifyActivity - onStop() called");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ModifyActivity - onDestroy() called");
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_modify);
        Button modifyAddPicture, modifySubmit, modifyCancel;
        modifyEditTitle = findViewById(R.id.modifyEditTitle);
        modifyEditContents = findViewById(R.id.modifyEditContents);
        modifyAddPicture = findViewById(R.id.modifyAddPicture);
        modifySubmit = findViewById(R.id.modifySubmit);
        modifyCancel = findViewById(R.id.modifyCancel);
        modifyImage = findViewById(R.id.modifyImage);
        //사진추가하는 버튼
        modifyAddPicture.setOnClickListener(new View.OnClickListener() {
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
        //수정 취소 버튼
        modifyCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyEditTitle.setText(null);
                modifyEditContents.setText(null);
               finish();
            }
        });
        //게시판에 아이템 클릭 이벤트처리 함수.
        modifySubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = modifyEditTitle.getText().toString();
                contents = modifyEditContents.getText().toString();
                //현재 년/월/일을 표시.
                @SuppressLint("SimpleDateFormat") String today = new SimpleDateFormat("yy/MM/dd").format(new Date());
                //사용자가 수정할 데이터를 입력받는 다.
                ShareData shareData = new ShareData(String.valueOf(imageUri), title,
                        AccountActivity.getUserInformation().get(Integer.parseInt(MainActivity.loginUserNumber)).getName(),
                        today, contents);
                //사용자로부터 입력받은 수정데이터를 인탠트로 전달한다.
                Intent intent = new Intent(PostModifyActivity.this, ShareActivity.class);
                Intent modifyIntent = getIntent();
                String position = modifyIntent.getStringExtra("position");
                Log.d(TAG, position+"포스트 액티비티에서 아이템 위치 값");
                //ShareAdapter 로 부터 전달받은 item position 데이터를 shareActivity 로 전달.
                intent.putExtra("itemPosition",position );
                intent.putExtra("postData", shareData);
                startActivity((intent));
                finish();
            }
        });
    }
    //되돌아 오는 함수.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                // 선택한 이미지에서 비트맵 생성
                imageUri = data.getData();
                this.grantUriPermission(this.getPackageName(), imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (imageUri != null) {
                        //SecurityException
                        //리소스 접근에 대한 권한이 없을경우 발생함
                        this.getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
                    }
                }
                modifyImage.setImageURI(imageUri);
                modifyImage.invalidate();
            }
        }
    }
}
