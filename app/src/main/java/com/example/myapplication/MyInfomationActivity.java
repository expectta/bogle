package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
//유저 정보를 확인한느 클래스
public class MyInfomationActivity extends AppCompatActivity {
    TextView tvUserName, tvUserEmail;
    EditText etUserPhoneNumber, etUserBirthday;
    Button btCancel, btModify;
    private String TAG = "로그";
    //뒤로가기 버튼을 눌렀을때 호출되는 함수
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MyInfomationActivity.this, HistoryActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_infomation);
        tvUserName = findViewById(R.id.userName);
        tvUserEmail = findViewById(R.id.email);
        etUserPhoneNumber = findViewById(R.id.modifyPhoneNumber);
        etUserBirthday = findViewById(R.id.changeBirthday);
        btCancel = findViewById(R.id.modifyCancel);
        btModify = findViewById(R.id.modifyUserInfo);
        checkUser(MainActivity.loginUserNumber);
        //취소버튼
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyInfomationActivity.this, HistoryActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //수정완료 버튼
        btModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SharedPreferences 에 저장되어있는 데이터를 수정하는 함수
                modifyUserInfo(MainActivity.loginUserNumber);
                Intent intent = new Intent(MyInfomationActivity.this, HistoryActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
//회원정보를 수정하는 함수.
    public void modifyUserInfo(String userNumber) {
        int userBirthday = 3;
        int userEmail = 1;
        int userPassword = 2;
        int userName = 0;
        int userPhone = 4;
        SharedPreferences sharedPreferences = getSharedPreferences("memberList", MODE_PRIVATE);
        SharedPreferences.Editor dataEditor = sharedPreferences.edit();
        //planListBackup 폴더에서 "planList"로 저장되어있는 리스트항목을 읽는다.
        String readData = sharedPreferences.getString("member", "");
        Log.d(TAG, readData+ "첫번째 스플릿");
        String[] splitText = readData.split(",");
        Log.d(TAG, splitText.toString()+ "두번째 스플릿");
        if (!readData.equals("")) {
            String inputBirthday =etUserBirthday.getText().toString();
            String inputPhone =etUserPhoneNumber.getText().toString();
            String[] userInfo = splitText[Integer.parseInt(userNumber)].split("\\|");
            Log.d(TAG, userInfo.toString()+ "최종 스플릿");
            userInfo[userBirthday] = inputBirthday;
            userInfo[userPhone] =inputPhone;
            String finalText = userInfo[userName] +"|"
                    + userInfo[userEmail]+"|"
                    + userInfo[userPassword]+"|"
                    + userInfo[userBirthday]+"|"
                    + userInfo[userPhone];
            splitText[Integer.parseInt(userNumber)] = finalText;
            String text = Arrays.toString(splitText).replace("[", "").replace("]", "");
            dataEditor.putString("member", text);
            dataEditor.apply();
        }else{
            return;
        }
    }
    //현재 접속한 유저의 정보를 보여주는 함수.
    public void checkUser(String userNumber) {
        int userName = 0;
        int userEmail = 1;
        int userBirthday = 3;
        int userPhone = 4;
        SharedPreferences sharedPreferences = getSharedPreferences("memberList", MODE_PRIVATE);
        //planListBackup 폴더에서 "planList"로 저장되어있는 리스트항목을 읽는다.
        String readData = sharedPreferences.getString("member", "");
        String[] splitText = readData.split(",");
        if (!readData.equals("")) {
            String[] userInfo = splitText[Integer.parseInt(userNumber)].split("\\|");
            tvUserName.setText(userInfo[userName]);
            tvUserEmail.setText(userInfo[userEmail]);
            etUserBirthday.setText(userInfo[userBirthday]);
            etUserPhoneNumber.setText(userInfo[userPhone]);
        }


//          String asdf =   김지운|asdf|870820|폰넘버;
        // Name = 0
        // Email = 1
        // 생일 =  2
        // 폰넘버 = 3
        //이름|메일|생일|폰넘버 , , 이름|메일|생일|폰넘버 ,이름|메일|생일|폰넘버 ,이름|메일|생일|폰넘버
        //맞네~! readData
//        for(int i= 0 ; i < splitText[Integer.parseInt(userNumber)].length(); i++){
//
//        }
//        String readData = sharedPreferences.getString("member", "");
//        String[] splitText = readData.split(",");
//        String finalText = null;
//        int userName = 0;
//        int userEmail = 1;
//        int userBirthday = 3;
//        int userPhone = 4;
//        if (!readData.equals("")) {
//            String[] userInfo = splitText[Integer.parseInt(userNumber)].split("\\|");
//            tvUserName.setText(userInfo[userName]);
//            tvUserEmail.setText(userInfo[userEmail]);
//            tvUserBirthday.setText(userInfo[userBirthday]);
//            tvUerPhone.setText(userInfo[userPhone]);
//    }

    }}
//    public void gg(){
//        //재사용해서 사용 할 수 없다.
//        new DownLoadTask().execute();
//        //백그라운드 쓰레드!!
//        //백그라운드 쓰레드에서 UI를 조작하는 쓰레드는 동작하지 않는다.
////        new Thread(new Runnable(){
////            @Override
////            public void run() {
////                for(int i=0; i<10; i++) {
////                    try {
////                        Thread.sleep(100);
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
////                    //UI에서 동작이 가능하게 하는 쓰레드
////                    runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////
////                        }
////                    });
////                }
////            }
////        }).start();
//    }
    //추상화 쓰레드 백그라운드.
    //제네릭 1 이태스클르 실행하면서 넘길 인자
    //제네릭 2 프로그래스 (작업 실행되는동안 반환되는 데이터 타입)
    //제네릭 3 결과 데이터 타입
    //AsyncTask 기본형태
//    class DownLoadTask extends AsyncTask<Void, Integer,Void>{
//        //백그라운드에서 동작하는 쓰레드
//        @Override
//        protected Void doInBackground(Void... voids) {
//            for(int i=0; i<10; i++) {
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                final int percent = i;
//                //퍼블리쉬프로그래스 함수를 꼭 사용해서 호출해야함.
//                //onProgressUpdate를 호출 함.
//                publishProgress(percent);
//                //UI에서 동작이 가능하게 하는 쓰레드
////                runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////
////                    }
////                });
//            }
//            return null;
//        }
//        //이것이 Ui쓰레드에서 돈다.
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
//            //...은 여러개 값을 가져 올 수 있다.
//            //여기서는 0번째 값만 가져온다
////            mTextView.setText(values[0] + %);
////                    mProgressBar.setProgress(values[0]);
//        }
//    }

//}
