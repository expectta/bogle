package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
//회원이 등록하는 게시글 수정하는 클래스
public class NoticeActivity extends AppCompatActivity {
    String TAG = "로그";
    TextView inputDate, inputTime;
    String date, member, time, contents;
    EditText inputContents, maxMember;

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "NoticeActivity - onStart() called");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "NoticeActivity - onResume() called");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "NoticeActivity - onRestart() called");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "NoticeActivity - onPause() called");
    }
    @Override
    protected void onStop() {
        super.onStop();
        finish();
        Log.d(TAG, "NoticeActivity - onStop() called");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "NoticeActivity - onDestroy() called");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button cancel,modify, calender;
        Log.d(TAG, "NoticeActivity - onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        cancel = findViewById(R.id.noticeButtonCancel);
        modify = findViewById(R.id.noticeButtonModify);
        calender = findViewById(R.id.calenderButton);
        inputDate = findViewById(R.id.modifyInputDate);
        inputTime = findViewById(R.id.modifyInputTime);
        inputContents = findViewById(R.id.modifyInputContents);
        maxMember = findViewById(R.id.modifyInputMemberNumber);

        //취소버튼
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //수정이 완료 된 게시글을 등록하는 함수
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date = inputDate.getText().toString();
                time = inputTime.getText().toString();
                member = maxMember.getText().toString();
                contents = inputContents.getText().toString();
                //활동계획 게시판의 아이템을 수정
                Intent intent = getIntent();
                String number = intent.getStringExtra("checkedPosition");
                PlanListData finalPlanListData = new PlanListData(date, time, contents, member);
                Intent resultIntent = new Intent(NoticeActivity.this, CommunityActivity.class);
                //최종 수정완료 된 데이터를 인텐트로 전달.
                resultIntent.putExtra("result", finalPlanListData);
                resultIntent.putExtra("position",number);
                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(resultIntent);
            }
        });
        calender.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showDate();
            }
        });
    }
    //달력을 출력 하는 함수.
    public void showDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                inputDate.setText(year + "/" + month + "/" + dayOfMonth);
                //달력으로 날짜를 지정하고 시간함수를 바로 호출한다.
                showTime();
            }
            //달력을 출력할때 초기값.
        }, 2020, 5, 11);
        datePickerDialog.setMessage("달력");
        datePickerDialog.show();
    }
    //시간 다이얼로그 출력 핰수.
    public void showTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                inputTime.setText(hourOfDay + "시" + minute + "분");
            }
        }, 0, 0, true);
        timePickerDialog.setMessage("시간");
        timePickerDialog.show();
    }
}

































