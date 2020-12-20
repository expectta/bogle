package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button accountButton;
    Button loginButton;
    EditText emailInput;
    EditText passwordInput;
    static String loginUserNumber;
    String superUserId = "1";
    String superUserPass = "1";
    String inputEmailValue = null;
    String inputPasswordValue = null;
    boolean isloginInfoCheck = false;
    String TAG = "로그";


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "MainActivity - onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "MainActivity - onStop() called");
        if (isloginInfoCheck) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MainActivity - onDestroy() called");
    }

    //액티비티의 생명주기 1.onCreate()로 구성요소를 생성 시키는 함수.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AccountActivity.getUserInformation().add(new UserList("관리자", "1", "1",
                "990909", "01012345678", new ArrayList<ShareData>()));
        //회원가입, 로그인 버튼객체 생성.
        accountButton = findViewById(R.id.mainButtonSignIn);
        loginButton = findViewById(R.id.mainButtonLogin);
        emailInput = findViewById(R.id.editTextEmail);
        passwordInput = findViewById(R.id.editTextPassword);

        //회원가입 화면으로 이동.
        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            //메인 액티비티에서 로그인 화면을 클릭 할때.
            public void onClick(View view) {
                //현재 액티비티 activity_main.xml 에서 전환 될 액티비티 activity_account.xml로 화면전환.
                Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                //실행할 액티비티가 태스크에 이미 있다면 동일한 액티비티부터
                //최상단 액티비티까지 모두 제거하고 새로운 액티비티르 실행.
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        //로그인 버튼 입력시
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                inputPasswordValue = passwordInput.getText().toString();
                inputEmailValue = emailInput.getText().toString();

                //email =1 , 비밀번호 =1로 로그인 할 경우 관리자모드 가능
                if (superUserId.equals(inputEmailValue) && superUserPass.equals(inputPasswordValue)) {
                    Toast.makeText(getApplicationContext(), "관리자로 로그인 성공!", Toast.LENGTH_SHORT).show();
                    Intent superUserIntent = new Intent(MainActivity.this, HomeActivity.class);
                    loginUserNumber = "0";
                    //로그인 화면에서 회원등록 화면으로 이동 하면 현재 액티비티는 onStop()되고
                    //onStop()함수 안에 finish()를 호출하게되어 액티비티 종료를 종료하게 코딩했다.
                    //예외로 회원등록 화면 이동 후 뒤로가기 버튼을 눌렀을 경우 되돌아 갈 화면이 없기 때문에
                    //어플리케이션이 종료 하게 되기 때문에
                    //로그인 화면을 더이상 사용할 필요가 없는경우
                    //즉, 회원이 정상적으로 이메일,비번 입력 후 로그인 했을때만 액티비티가 종료 될 수 있도록
                    //onStop()함수 안에에 inish(); 호출 할때 조건으로 loginInfoCheck가 true일때만 로그인 액티비티종료.
                    superUserIntent.putExtra("superUser", "super");
                    isloginInfoCheck = true;
                    startActivity(superUserIntent);
                    //email,password를 입력 하지 않을경우 토스트 메시지로 경고한다.
                } else {
                    //회원정보와 정보 일치여부 판다.
                    SharedPreferences sharedPreferences = getSharedPreferences("memberList", MODE_PRIVATE);
                    String readData = sharedPreferences.getString("member", "");
                    String[] splitText = readData.split(",");
                    String[] splitMemberInfo;
                    int sharedDataUserEmail = 1;
                    int sharedDataUserPassword = 2;
                    for (int i = 0; i < splitText.length; i++) {
                        String memberInfo = splitText[i];
                        splitMemberInfo = memberInfo.split("\\|");
                        if (splitMemberInfo[sharedDataUserEmail].equals(emailInput.getText().toString()) &&
                                splitMemberInfo[sharedDataUserPassword].equals(passwordInput.getText().toString())) {
                            emailInput.setText(null);
                            passwordInput.setText(null);
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            loginUserNumber = String.valueOf(i);
                            intent.putExtra("userNumber", loginUserNumber);
                            Log.d(TAG, "Main에서회원 번호:" + loginUserNumber);
                            isloginInfoCheck = true;
                            //로그인시 editText내 text 삭제.
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            Log.d(TAG, "회원정보일치");
                            Toast.makeText(getApplicationContext(), "로그인되었습니다!", Toast.LENGTH_SHORT).show();
                            break;

                        }
//                        else if (AccountActivity.getUserInformation().get(i).getEmail().equals(inputEmailValue) ||
//                                AccountActivity.getUserInformation().get(i).getPassword().equals(inputPasswordValue)) {
//                            Toast.makeText(getApplicationContext(), "회원정보가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
//                            break;
//                        }
                    }
                }
            }
        });
    }
//        });
//        emailInput.addTextChangedListener(new TextWatcher() {
////            //입력하기 전
////            @Override
////            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
////            }
////            //입력 중
////            @Override
////            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
////            }
////            //입력 후
////            @Override
////            public void afterTextChanged(Editable editable) {
////            }
////        });
////        //비밀번호 입력시 실시간 입력값 확인
////        passwordInput.addTextChangedListener(new TextWatcher() {
////            //입력하기 전
////            @Override
////            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
////            }
////            //입력중
////            @Override
////            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
////            }
////            // 입력후
////            @Override
////            public void afterTextChanged(Editable editable) {
}
