package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class CalenderActivity extends AppCompatActivity {
    //반드시 구현되어야하는 메소드
    //Activity의 필수 구성요소(버튼,레이아웃,뷰)를 초기화하는 함수
    //액티비가 실행하기 전 준비해야하는 함수.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
    }
    //onCreate 이후 리소스 설정
    @Override
    protected void onStart() {
        super.onStart();
    }
    //화면을 출력하고 사용자와 상호작용을 한다. 액티비티 활성화 되어있는 상태
    @Override
    protected void onResume() {
        super.onResume();
        finish();
    }
    //홈버튼이나 다음 액티비티로 넘어갈때.
    //현재 변경사항을 저장한다.
    @Override
    protected void onPause() {
        super.onPause();
    }
    //다음 액티비티로 화면이 넘어가서 화면을 완전히 가린 상태.(소멸되지 않음)
    //onRestart()로 되돌릴 수 있다.
    @Override
    protected void onStop() {
        super.onStop();
    }
    //finish()호출로 강제로 액티비티를 종료했다거나.
    //화면종료, 어플 강제종료 했을때.
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
