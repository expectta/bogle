package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.Serializable;
import java.util.ArrayList;


public class AdministratorActivity extends AppCompatActivity {
    PlanListData planListData;
    String date, time, contents, member;
    String TAG = "로그";
    TextView tvInputDate, tvInputTime, tvTotalUser;
    EditText etInputContents, etMaxMember;
    ArrayList<String> memberList = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "AdministratorActivity - onResume() called");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "AdministratorActivity - onRestart() called");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "AdministratorActivity - onStart() called");
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
        Log.d(TAG, "AdministratorActivity - onPause() called");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "AdministratorActivity - onStop() called");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "AdministratorActivity - onDestroy() called");
    }
    @Override
    public void onBackPressed() {
        Log.d(TAG, "AdministratorActivity - onBackPressed() called");
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Button btPost;
        Button btGoMain;
        Button buttoncalender;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator);
        Log.d(TAG, "AdministratorActivity - onCreate() called");
        tvTotalUser = findViewById(R.id.administratorTextViewTotalMember);
        btPost = findViewById(R.id.submitButton);
        buttoncalender = findViewById(R.id.dateButton);
        //공자사항 등록에 필요한 뷰
        tvInputDate = findViewById(R.id.inputDate);
        tvInputTime = findViewById(R.id.inputTime);
        etInputContents = findViewById(R.id.inputContents);
        etMaxMember = findViewById(R.id.inputMemberNumber);
        //회원정보 리스트 ( 리스트뷰)
        readData();
        //어댑터 생성
        //파라메터 Activity , TextView 위젯으로 구성된 listview아이템 리소스 id, 배열로 선언된 사용자 데이터
        ArrayAdapter memberListAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, memberList);
        final ListView listView = findViewById(R.id.listview);
        listView.setAdapter(memberListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final int memberNumber = position;
                Toast.makeText(getApplicationContext(), position + "위치 전화걸기", Toast.LENGTH_SHORT).show();
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AdministratorActivity.this);
                dialogBuilder.setTitle("전화");
                dialogBuilder.setMessage("회원에게 전화를 걸겠습니까?");
                dialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //리스트뷰 터치 이벤트 시 다이얼로그 출력
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        //TODO : callBySharedData 에서 전화번호를 정상적으로 읽어 오지만 가장 앞 0이 빠진
                        //채로 전화걸기가 된다. 다시 확인 필요.
                        intent.setData(Uri.parse("tel: " + "0"+callBySharedData(memberNumber)));
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                });
                dialogBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialogBuilder.show();
            }
        });
        //TODO : 관리자 화면에 전체 회원수를 출력 나중에 필요함.
//        if (AccountActivity.getUserInformation().size() > noMember) {
//            //회원가입을 한 후 관리자 화면에 총 회원수를 출력한다.
//            totalUser.setText(AccountActivity.getUserInformation().size() + "명");
//            //회원 가입을 한 후 관리자 화면에 회원가입한 유저의 정보가 리스트로 표현된다.
//            TextView userName = findViewById(R.id.administratorTextViewName);
//            userName.setText("이름 : " + AccountActivity.getUserInformation().get(0).getName());
//            TextView userEmail = findViewById(R.id.administratorTextViewEmail);
//            userEmail.setText("  Email : " + AccountActivity.getUserInformation().get(0).getEmail());
//
//        } else {
//            //회원수가 0일때 출력
//            totalUser.setText(" 0명");
//        }
        //공지사항 등록하는 버튼 리스너.
        btPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //텍스트 받아서 변수에 저장할 때 onCreate() 에서 받아오면 읽어오지를 못함.
                date = tvInputDate.getText().toString();
                member = etMaxMember.getText().toString();
                time = tvInputTime.getText().toString();
                contents = etInputContents.getText().toString();
                planListData = new PlanListData(date, time, contents, member);
                Intent intent = new Intent(AdministratorActivity.this, CommunityActivity.class);
                intent.putExtra("submitPlan", planListData);
                intent.putExtra("startService","true");
                Intent serviceIntent = new Intent(getApplication(),NoticeService.class);
                Toast.makeText(AdministratorActivity.this,"알림을 띄웁니다.",Toast.LENGTH_SHORT).show();
                startService(serviceIntent);
                startActivity(intent);

                //데이터를 전달 완료하고 해당 editText의 내용을 삭제.
            }
        });
        //메인화면으로 가는 버튼 리스너.

        //달력 다이얼로그 리스너.
        buttoncalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDate();
            }
        });
//        Intent intent =getIntent();
//        TextView userName = (TextView)findViewById(R.id.administratorTextViewName);
////        String userNameVar = intent.getStringExtra("userName");
//        userName.setText(userList.getName());
//
//        TextView userEmail = (TextView)findViewById(R.id.administratorTextViewEmail);
////        String userEmailVar = intent.getStringExtra("userEmail");
//        userEmail.setText(userList.getEmail());
//
//        TextView userPhone = (TextView)findViewById(R.id.administratorTextViewPhoneNumber);
////        String userPhoneVar = intent.getStringExtra("etUserPhoneNumber");
//        userPhone.setText(userList.getPhoneNumber());

//        if(AccountActivity.totalUserNumber != null){
//            TextView userNumber = (TextView)findViewById(R.id.administratorPrintMemberNumber);
//            userNumber.setText(AccountActivity.totalUserNumber.size());
//        }
    }
    //가입을 완료한 회원들의 목록을 SharedPreferences 로 부터 읽는다.
    public void readData() {
        SharedPreferences sharedPreferences = getSharedPreferences("memberList", MODE_PRIVATE);
        //planListBackup 폴더에서 "planList"로 저장되어있는 리스트항목을 읽는다.
        String readData = sharedPreferences.getString("member", "");
        String[] splitText = readData.split(",");
        String finalText = null;
        int userName = 0;
        int userEmail = 1;
        int userPhone = 4;
        if (!readData.equals("")) {
            for (int i = 0; i < splitText.length; i++) {
                String memberInfo = splitText[i];
                String[] splitMemberInfo = memberInfo.split("\\|");
                //회원 목록을 표시할때 i+1을 하지 않으면 넘버링이 0부터 시작한다.
                for (int j = 0; j < splitMemberInfo.length; j++) {
                    finalText = (i + 1) + " 이름 : " + splitMemberInfo[userName]
                            + " 메일 : " + splitMemberInfo[userEmail]
                            + " 전화 : " + splitMemberInfo[userPhone];
                }
                //화면에 표시 될 회원의 목록 수  i에 +1을 했을때 1부터 넘버링 할 수 있도록 함.
                memberList.add(finalText);
                tvTotalUser.setText((splitText.length + " 명"));
            }
        }
    }
    //회원목록 listView 에서 선택된 회원의 전화번호를 가지고 온다.
    public int callBySharedData(int checkedMember) {
        SharedPreferences sharedPreferences = getSharedPreferences("memberList", MODE_PRIVATE);
        String readData = sharedPreferences.getString("member", "");
        String[] splitText = readData.split(",");
        int userPhone = 4;
        String memberInfo = splitText[checkedMember];
        String[] splitMemberInfo = memberInfo.split("\\|");
        return  Integer.parseInt(splitMemberInfo[userPhone]);
    }

    //달력 다이얼로그 출력 함수.
    //년/월/일
    public void showDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                tvInputDate.setText(year + "/" + (month+1) + "/" + dayOfMonth);
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
                tvInputTime.setText(hourOfDay + "시" + minute + "분");
            }
        }, 0, 0, true);
        timePickerDialog.setMessage("시간");
        timePickerDialog.show();
    }
}
class PlanListAdapter extends BaseAdapter {
    String itemNumber;
    Context mContext = null;
    LayoutInflater layoutInflater = null;
    static ArrayList<PlanListData> planListData;
    public static int checkedNumber;

    public PlanListAdapter(Context mContext, ArrayList<PlanListData> planListData) {
        this.mContext = mContext;
        this.layoutInflater = LayoutInflater.from(mContext);
        this.planListData = planListData;
    }
    //listData에 값이 몇개 들어있는지 알려주는 함수
    @Override
    public int getCount() {
        return planListData.size();
    }

    //아이템의 id를 알려주는 함수. itemdata가 list에서 몇번째 위치해 있는지 확인
    @Override
    public long getItemId(int position) {
        return position;
    }

    //itemDatalist에서 아이템을 가져오는 함수.
    @Override
    public PlanListData getItem(int position) {
        return planListData.get(position);
    }

    //XML에 정의된 리소스들을 view 형태로 반환해 준다.
    //액티비티를 만들면 setContentView(R.layout.activity_main)메서드와 같은 원리.
    @Override
    public View getView(final int position, View converView, ViewGroup parent) {
        itemNumber = String.valueOf(position);
        View view = layoutInflater.inflate(R.layout.activity_plan_list_item, null);
        TextView planDate = view.findViewById(R.id.communityPlanDate);
        TextView planTime = view.findViewById(R.id.communityPlanTime);
        TextView joinInMember = view.findViewById(R.id.communityCurrentMember);
        TextView maxMember = view.findViewById(R.id.communityMaxMember);
        TextView contants = view.findViewById(R.id.communityContents);
        planDate.setText(planListData.get(position).getPlanDate());
        planTime.setText(planListData.get(position).getTime());
        joinInMember.setText(planListData.get(position).getCureentJoinInMember());
        maxMember.setText(planListData.get(position).getMaxMemberNumber());
        contants.setText(planListData.get(position).getContents());
        ConstraintLayout layoutClick = view.findViewById(R.id.itemLayout);
        layoutClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //item 터치했을떄 참여 할 수 있는 다이얼 로그 표시.
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
                dialogBuilder.setTitle("활동계획");
                dialogBuilder.setMessage("활동에 참여하시겠습니까?");
                dialogBuilder.setPositiveButton("참여", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //리스트뷰 터치 이벤트 시 다이얼로그 출력
                        Intent intent = new Intent(mContext, HistoryActivity.class);
                        //회원이 관리자가 올린 계획을 자신의 정보 list에 저장 할 수있도록 다이얼로그에서 ok를 눌렀을때 해당 item의 정보르 인탠트로 전달한다.
                        intent.putExtra("plan", new PlanListData(planListData.get(position).getPlanDate(), planListData.get(position).getTime()
                                , planListData.get(position).getContents(), planListData.get(position).getMaxMemberNumber()));
                        intent.putExtra("checkedItemNumber", String.valueOf(position));
                        //액티비티가 아닌곳에서 인탠트로 화면전환, 데이터 전달할때는 Comtext로 실행해야 한다.
                        mContext.startActivity(intent);
                        dialog.dismiss();
                    }
                });
                dialogBuilder.setNegativeButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedNumber = position;
                        Intent intent = new Intent(mContext, NoticeActivity.class);
                        //adapter 에서 startActivityForResult를 사용할때.
                        //일반 클래스에서 사용할때는 startActivityForResult(intent,1);
                        intent.putExtra("date", new PlanListData(
                                planListData.get(position).getPlanDate(),
                                planListData.get(position).getTime(),
                                planListData.get(position).getContents(),
                                planListData.get(position).getMaxMemberNumber()));
                        intent.putExtra("checkedPosition", String.valueOf(checkedNumber));
                        //이전의 엑티비티화면
                        ((CommunityActivity) mContext).startActivityForResult(intent, 1);
                    }
                });
                dialogBuilder.setNeutralButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        planListData.remove(position);
                        Intent intent = new Intent(mContext, CommunityActivity.class);
                        intent.putExtra("checkedItemNumber", String.valueOf(position));
                        ((CommunityActivity) mContext).startActivityForResult(intent, 1);
                        notifyDataSetChanged();
                    }
                });
                dialogBuilder.show();
            }
        });
        return view;
    }
}

//커뮤니티 화면에 보여줄 (리스트뷰) 아이템 클래스.
//인텐트로 객체를 전달하기 위해서 Serializable 확장.
class PlanListData implements Serializable {
    String planDate;
    String time;
    String contents;
    String maxMemberNumber;
    String cureentJoinInMember = "0";

    public void setCureentJoinInMember(String cureentJoinInMember) {
        this.cureentJoinInMember = cureentJoinInMember;
    }

    public PlanListData() {
    }

    public PlanListData(String planDate, String time, String contents, String maxMemberNumber) {
        this.planDate = planDate;
        this.time = time;
        this.contents = contents;
        this.maxMemberNumber = maxMemberNumber;
    }

    public String getPlanDate() {
        return planDate;
    }

    public void setPlanDate(String planDate) {
        this.planDate = planDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getMaxMemberNumber() {
        return maxMemberNumber;
    }

    public void setMaxMemberNumber(String maxMemberNumber) {
        this.maxMemberNumber = maxMemberNumber;
    }

    public String getCureentJoinInMember() {
        return cureentJoinInMember;
    }
}