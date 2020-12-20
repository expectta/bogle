package com.example.myapplication;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {
    static ArrayList<UserList> userInformation = new ArrayList<>();
    String TAG = "로그";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String inputUserInfo;

    public static void setUserInformation(ArrayList<UserList> userInformation) {
        AccountActivity.userInformation = userInformation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RadioGroup checkUserRadioGroup;
        Button buttonSignUp;
        final LinearLayout visibleOnlyTutor;
        //인자가 두개 들어간다.
        super.onCreate(savedInstanceState);
        Log.d(TAG, "AccountActivity - onCreate() called");
        setContentView(R.layout.activity_account);
        //라디오 그룹은 자식들까지 언급할 필요없이그룹만으로 대입.
        checkUserRadioGroup = findViewById(R.id.radioGroupCheckUser);
        //회원가입시 강사만 추가로 회원가입 내용을 입력 받을 수 있도록 일반회원이 가입시 레이아웃을 감추기 위해 INVISIBLE사용.
        visibleOnlyTutor = findViewById(R.id.forTutor);
        //회원가입 액티비티실행 시 강사에게만 보여 줄 레이아웃을 숨기고 화면 출력.
        visibleOnlyTutor.setVisibility(View.INVISIBLE);
        //RadioGroup의 자식 버튼이 눌렸는지 안눌렸는지를 판단
        checkUserRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            //TODO : 강사와 일반회원의 회원 가입 내용을 다르게 해야 한다. 기본은 일반회원
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int radioButtonNormalUser) {
                //라디오버튼이 일반회원일 경우 레이아웃 감추기
                if (radioButtonNormalUser == R.id.radioButtonNormalUser) {
                    visibleOnlyTutor.setVisibility(View.INVISIBLE);
                    //라디오버튼이 강사일경우 레이아웃 보이기
                } else if (radioButtonNormalUser == R.id.radioButtonTutor) {
                    visibleOnlyTutor.setVisibility(View.VISIBLE);
                }
            }
        });
        //회원등록버튼
        buttonSignUp = findViewById(R.id.accountButtonSignUp);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText etUserName;
                EditText etUserEmail;
                EditText etUserPassword;
                EditText etUserPasswordCheck;
                EditText etUserPhoneNumber;
                EditText etUserBirthday;
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                etUserName = findViewById(R.id.accountEditTextName);
                etUserEmail = findViewById(R.id.accountEditTextEmail);
                etUserPassword = findViewById(R.id.accountEditTextPassword);
                etUserBirthday = findViewById(R.id.accountEditTextBirthday);
                etUserPasswordCheck = findViewById(R.id.accountEditTextCheckPassword);
                etUserPhoneNumber = findViewById(R.id.accountEditTextPhoneNumber);
                if (etUserName.getText().toString().length() == 0 ||
                        etUserEmail.getText().toString().length() == 0 ||
                        etUserPhoneNumber.getText().toString().length() == 0 ||
                        etUserPassword.getText().toString().length() == 0 ||
                        etUserPasswordCheck.getText().toString().length() == 0 ||
                        etUserBirthday.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "모든항목을 작성 해 주세요", Toast.LENGTH_SHORT).show();
                } else {
                    if (!etUserPassword.getText().toString().equals(etUserPasswordCheck.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "회원등록 되었습니다", Toast.LENGTH_SHORT).show();
                        //회원가입 정보 입력 후 SharedPreferences 에 저장.
                        sharedPreferences = getSharedPreferences("memberList", MODE_PRIVATE);
                        if (sharedPreferences.getString("member", "").equals("")) {
                            SharedPreferences.Editor dataEditor = sharedPreferences.edit();
                            inputUserInfo = etUserName.getText().toString() + "|" + etUserEmail.getText().toString() + "|" +
                                    etUserPassword.getText().toString() + "|" +
                                    etUserBirthday.getText().toString() + "|" +
                                    etUserPhoneNumber.getText().toString() ;
                            dataEditor.putString("member", inputUserInfo);
                            dataEditor.apply();
                            //[{"planDate":"","time":"","contents":"85285285285","maxMemberNumber":""}]
                            //"planList"라는 키 값으로 저장되어있는 데이터가 있을때
                        } else if (!sharedPreferences.getString("member", "").equals("")) {
                            String  readData = sharedPreferences.getString("member", "");
                            SharedPreferences.Editor dataEditor = sharedPreferences.edit();
                            readData += "," + etUserName.getText().toString() + "|" + etUserEmail.getText().toString() + "|" +
                                    etUserPassword.getText().toString() + "|" +
                                    etUserBirthday.getText().toString() + "|" +
                                    etUserPhoneNumber.getText().toString();
                            dataEditor.putString("member", readData);
                            dataEditor.apply();
                        }
                        Log.d(TAG, String.valueOf(userInformation.size()));
                        startActivity(intent);
                    }
                }
            }
        });
    }

    //회원등록 후 액티비티 종료.
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "AccountActivity - onPause() called");
        finish();
    }

    public static ArrayList<UserList> getUserInformation() {
        return userInformation;
    }
}

//회원가입 시 회원정보 클래스.
class UserList {
    private String name;
    private String email;
    private String password;
    private String birthday;
    private String phoneNumber;
    private String passwordCheck;
    private ArrayList<ShareData> myContentsList;

    public ArrayList<ShareData> getMyContentsList() {
        return myContentsList;
    }

    public void setMyContentsList(ArrayList<ShareData> myContentsList) {
        this.myContentsList = myContentsList;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public UserList(String name, String email, String password, String birthday, String phoneNumber, ArrayList<ShareData> myContentsList) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.passwordCheck = passwordCheck;
        this.myContentsList = myContentsList;
    }
}
