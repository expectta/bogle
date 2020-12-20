package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class HistoryActivity extends AppCompatActivity {
    String TAG = "로그";
    RecyclerView recyclerView;
    static HistoryAdapter historyAdapter;
    ArrayList<PlanListData> myPlanList = new ArrayList<>();
    TextView tvUserName;
    TextView tvUserEmail;
    TextView tvUerPhone;
    TextView tvUserBirthday;
    TextView tvCounter;
    Thread thread;
    String readData;
    Handler handler;
    ArrayList<String> result = new ArrayList<>();
    Button btModifyUserInfo;

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "HistoryActivity - onResume() called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "HistoryActivity - onRestart() called");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "HistoryActivity - onStart() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
        Log.d(TAG, "HistoryActivity - onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "HistoryActivity - onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "HistoryActivity - onDestroy() called");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button btMembershipCancel;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        //화면에 출력해야 할 뷰를 id값으로 찾아서 변수에 대입

        btModifyUserInfo = findViewById(R.id.historyModify);
        //관리자가 등록한 게시글에 buttonGoMain버튼을 눌렀을때 회원정보에 내가 등록한 글의 목록을 보여주는 리사이클러 뷰
        recyclerView = findViewById(R.id.historyList);
        Intent intent = getIntent();
        historyAdapter = new HistoryAdapter(myPlanList, this);
        //활동계획에서 사용자가 아이템을 터치하고 참여버튼을 눌렀을때
        //SharedPreferences 에 데이터 저장.
        PlanListData myPlan = (PlanListData) intent.getSerializableExtra("plan");
        String checkedNumber = intent.getStringExtra("checkedItemNumber");

        //접속한 회원의 정보를 보여준다.
        //로그인시 MainActivity 에서 회원 번호를 지정함.
        readSharedData(MainActivity.loginUserNumber);
        if (myPlan != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                saveData(myPlan, checkedNumber);
            }
            //SharedPreferences  에 데이터 저장 완료 후 인텐트 데이터 삭제
            getIntent().removeExtra("plan");
            getIntent().removeExtra("checkedItemNumber");
        }
        //활동계획에서 사용자가 아이템을 터치하고 삭제버튼을 눌렀을때
        //SharedPreferences 에서 데이터를 삭제
        String currentItemNumber = intent.getStringExtra("position");
        if (currentItemNumber != null) {
//            deleteData(currentItemNumber);
            //SharedPreferences  에 데이터 삭제 완료 후 인텐트 데이터 삭제
            getIntent().removeExtra("position");
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(historyAdapter);
        recyclerView.setHasFixedSize(true);
        //삭제가 완료되고 리스트 데이터를 읽는다.
        readData();


//TODO : 회원가입하고나서 바로 회원의 리스트 인덱스를 적용해줘야함.
        //회원탈퇴 버튼
        btMembershipCancel = findViewById(R.id.membershipCancel);
        btMembershipCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            //회원탈퇴 버튼 누르면 경고창을 보여주고 예, 아니오 선택지를 출력.
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                //다이얼로그에 출력할 정보를 설정.
                builder.setTitle("회원탈퇴").setMessage("탈퇴하시겠습니까?");
                //다이얼로그 버튼 생성
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //탈퇴버튼 눌렀을때 호출되는 함수
                        deleteSharedData(MainActivity.loginUserNumber);
                        Toast.makeText(getApplicationContext(), "탈퇴하셨습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "취소하셨습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                //설정한 빌더 객체생성
                AlertDialog alertDialog = builder.create();
                //버튼 동작시 화면에 출력.
                alertDialog.show();
            }
        });
        btModifyUserInfo.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this , MyInfomationActivity.class);
                startActivity(intent);
            }
        });
        recyclerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
    }

    //사용자가 회원탈퇴 버튼을 눌렀을때 회원 정보를 삭제하는 함수
    //SharedPreferences 에서 데이터 삭제.
    public void deleteSharedData(String userNumber) {
        SharedPreferences sharedPreferences = getSharedPreferences("memberList", MODE_PRIVATE);
        //planListBackup 폴더에서 "planList"로 저장되어있는 리스트항목을 읽는다.
        String readData = sharedPreferences.getString("member", "");
        String[] splitText = readData.split(",");
        result.addAll(Arrays.asList(splitText));
        result.remove(Integer.parseInt(userNumber));
        String text = result.toString().replace("[", "").replace("]", "");
        SharedPreferences.Editor dataEditor = sharedPreferences.edit();
        dataEditor.putString("member", text);
        dataEditor.apply();
    }

    //회원정보 액티비티에서 회원 정보를 확인.
    //SharedPreferences 에서 데이터 참조.
    public void readSharedData(String userNumber) {
        tvUserName = findViewById(R.id.historyCurrentUserName);
        tvUserEmail = findViewById(R.id.historyCurrentUserEmail);
        tvUerPhone = findViewById(R.id.historyCurrentUserPhone);
        tvUserBirthday = findViewById(R.id.historyCurrentUserBirthday);
        SharedPreferences sharedPreferences = getSharedPreferences("memberList", MODE_PRIVATE);
        //planListBackup 폴더에서 "planList"로 저장되어있는 리스트항목을 읽는다.
        readData = sharedPreferences.getString("member", "");
        String[] splitText = readData.split(",");
        int userName = 0;
        int userEmail = 1;
        int userBirthday = 3;
        int userPhone = 4;
        if (!readData.equals("")) {
            String[] userInfo = splitText[Integer.parseInt(userNumber)].split("\\|");
            tvUserName.setText(userInfo[userName]);
            tvUserEmail.setText(userInfo[userEmail]);
            tvUserBirthday.setText(userInfo[userBirthday]);
            tvUerPhone.setText(userInfo[userPhone]);
        }
    }

    //관리자가 등록한 데이터를 SHaredPreferences 로부터 읽는 함수.
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void saveData(PlanListData data, String position) {
        int myItemNumber = Integer.parseInt(position);
        SharedPreferences sharedPreferences = getSharedPreferences("planListBackup", MODE_PRIVATE);
        //planListBackup 폴더에서 "planListBackup"로 저장되어있는 리스트항목을 읽는다.
        String readData = sharedPreferences.getString("planList", "");
        //SharedPreferences 에 "planLsit"에 저장되어 있는 데이터를 모두 읽어온다.
        //데이터 형식은 Array
        try {
            JSONArray jsonArray = new JSONArray(readData);
            //SharedPreferences 에서 불러온 데이터를 JSONArray 형싱으로 읽는다.
//            for (int i = 0; i < jsonArray.length(); i++) {
            //jsonArray 에 포함되어 있는 리스트의 갯수를 파악하여 객체형태로 변경한다.
            JSONObject jsonObject = jsonArray.getJSONObject(myItemNumber);
            SharedPreferences myItemList = getSharedPreferences("myItemList", MODE_PRIVATE);
            SharedPreferences.Editor dataEditor = myItemList.edit();
            if (myItemList.getString("myPlanList", "").equals("")) {
                JSONArray newJsonArray = new JSONArray();
                jsonObject.put("planDate", data.getPlanDate());
                jsonObject.put("time", data.getTime());
                jsonObject.put("contents", data.getContents());
                jsonObject.put("maxMemberNumber", data.getMaxMemberNumber());
                newJsonArray.put(jsonObject);
                dataEditor.putString("myPlanList", newJsonArray.toString());
                dataEditor.apply();
            } else if (!myItemList.getString("myPlanList", "").equals("")) {
//                SharedPreferences myItemListShared = getSharedPreferences("myItemList", MODE_PRIVATE);
                String readList = myItemList.getString("myPlanList", "");
                JSONArray newJsonArray = new JSONArray(readList);
                jsonObject.put("planDate", data.getPlanDate());
                jsonObject.put("time", data.getTime());
                jsonObject.put("contents", data.getContents());
                jsonObject.put("maxMemberNumber", data.getMaxMemberNumber());
                newJsonArray.put(jsonObject);
                dataEditor.putString("myPlanList", newJsonArray.toString());
                dataEditor.apply();
            }
//                planItemList.add(new PlanListData(jsonObject.getString("planDate"),
//                        jsonObject.getString("time"),
//                        jsonObject.getString("contents"),
//                        jsonObject.getString("maxMemberNumber")));
//                Log.d(TAG, jsonObject.toString() + "제이슨 리스트 > 오브젝트 변환 값");
//                //{"planDate":"","time":"","contents":"wfshwjs","maxMemberNumber":"5"}
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void readData() {
        SharedPreferences sharedPreferences = getSharedPreferences("myItemList", MODE_PRIVATE);
        //planListBackup 폴더에서 "myItemList"로 저장되어있는 리스트항목을 읽는다.
        String readData = sharedPreferences.getString("myPlanList", "");
        //SharedPreferences 에 "myPlanList"에 저장되어 있는 데이터를 모두 읽어온다.
        //데이터 형식은 Array
        try {
            JSONArray jsonArray = new JSONArray(readData);
            //SharedPreferences 에서 불러온 데이터를 JSONArray 형싱으로 읽는다.
            for (int i = 0; i < jsonArray.length(); i++) {
                //jsonArray 에 포함되어 있는 리스트의 갯수를 파악하여 객체형태로 변경한다.
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                myPlanList.add(new PlanListData(jsonObject.getString("planDate"),
                        jsonObject.getString("time"),
                        jsonObject.getString("contents"),
                        jsonObject.getString("maxMemberNumber")));
                Log.d(TAG, jsonObject.toString() + "제이슨 리스트 > 오브젝트 변환 값");
                //{"planDate":"","time":"","contents":"wfshwjs","maxMemberNumber":"5"}
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        thread = new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.arg1 = myPlanList.size();
                handler.sendMessage(msg);
            }
        };
        thread.start();
        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            //루퍼에의해 실행되는 함수.
            //송신 쓰레드에서 전달받은 파라메터를 통해 코드가 진행됨.
            public void handleMessage(@NonNull Message msg) {
                int count = msg.arg1;
                Log.d(TAG, String.valueOf(count));
                tvCounter = findViewById(R.id.totalList);
                tvCounter.setText(String.valueOf(count));
            }
        };
        thread.interrupt();
    }

    //선택된 아이템을 SharedPreferences 에서 삭제.

}

//회원정보 액티비티에서 회원이 참여한 활동에 대해 리사이클러뷰 목록으로 보여주는 어댑터클래스.
class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>  {
    private ArrayList<PlanListData> items;
    Context mContext = null;
    private int itemPosition;

    public HistoryAdapter(ArrayList<PlanListData> items, Context mContext) {
        this.items = items;
        this.mContext = mContext;
    }

    public HistoryAdapter(Context mContext) {
        this.mContext = mContext;
    }

    // 화면을 최초 로딩하여 만들어진 View 가 없는 경우, xml 파일을 inflate 하여 ViewHolder 를 생성.
    //뷰홀더와 레이아웃파일을 연결해주는 역할.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_history_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //onCreateViewHolder 에서 만든 view 와 실제 입력되는 각각의 데이터를 연결
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        itemPosition = position;
        Log.d("position","히스토리 엑티비티 : " + itemPosition);
        viewHolder.date.setText(items.get(position).getPlanDate());
        viewHolder.time.setText(items.get(position).getTime());
        viewHolder.contents.setText(items.get(position).getContents());
        viewHolder.maxMemberNumber.setText(items.get(position).getMaxMemberNumber());
        viewHolder.currentJoinInMember.setText(items.get(position).getCureentJoinInMember());
        viewHolder.itemView.setTag(position);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
                dialogBuilder.setTitle("활동계획");
                dialogBuilder.setMessage(items.get(position).getContents());
                dialogBuilder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            //PlanListAdapter 로 부터 받아온 item position 값을 Int 데이터 타입으로 변경.
                            SharedPreferences sharedPreferences = mContext.getSharedPreferences("myItemList", MODE_PRIVATE);
                            //planListBackup 폴더에서 "myItemList"로 저장되어있는 리스트항목을 읽는다.
                            String readData = sharedPreferences.getString("myPlanList", "");
                            try {
                                //jsonArray.size()로 사이즈 확인가능
                                JSONArray jsonArray = new JSONArray(readData);
                                //삭제하고 싶은 리스트를 인덱스(itemPosition)으로 삭제한다.
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    jsonArray.remove(position);
                                }
                                SharedPreferences.Editor dataEditor = sharedPreferences.edit();
                                //인덱스로 선택된 리스트를 값을 제거 후 저장.
                                dataEditor.putString("myPlanList", jsonArray.toString());
                                dataEditor.apply();
                                items.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, items.size());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        dialog.dismiss();
                    }
                });
//                dialogBuilder.setNegativeButton("알람테스트", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(mContext, AlarmActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        mContext.startActivity(intent);
//                        dialog.dismiss();
//                    }
//                });
                //다이얼로그에서 알람을 설정 할 수 있는 버튼.
                dialogBuilder.setNeutralButton("firstIinputStream정", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int planYear = 0;
                        int planMonth = 1;
                        int planDay = 2;
                        Calendar calender = Calendar.getInstance();
                        calender.setTime(new Date());
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String currentTime = dateFormat.format(calender.getTime());
                        Log.d("로그", currentTime + " 현재날짜");
                        SharedPreferences sharedPreferences = mContext.getSharedPreferences("myItemList", MODE_PRIVATE);
//                        planListBackup 폴더에서 "myItemList"로 저장되어있는 리스트항목을 읽는다.
                        String readData = sharedPreferences.getString("myPlanList", "");
                        try {
                            JSONArray jsonArray = new JSONArray(readData);
                            //SharedPreferences 에서 불러온 데이터를 JSONArray 형싱으로 읽는다.
                            //jsonArray 에 포함되어 있는 리스트의 갯수를 파악하여 객체형태로 변경한다.
                            JSONObject jsonObject = jsonArray.getJSONObject(position);
                            String planDate = jsonObject.getString("planDate");
                            Log.d("로그", planDate + "쉐어드에서 가지고 온 날짜");
                            //2020/06/11 결과값
                            String[] splitText = planDate.split("/");
                            if(splitText[planYear]!="" || splitText[planMonth] != "" ||  splitText[planDay] != "") {
                                int alarmYear = Integer.parseInt(splitText[planYear]);
                                int alarmMonth = Integer.parseInt(splitText[planMonth]);
                                int alarmDay = (Integer.parseInt(splitText[planDay]) - 1);
                                Log.d("로그", alarmYear + " 스플릿 년도" + alarmMonth + " 스플릿 월 " + alarmDay + " 스필릿 일");
                                Toast.makeText(mContext, "출발하루 전인 " + alarmYear + " 년 " + alarmMonth + " 월 " + alarmDay + " 일 알람이 설정 되었습니다.",
                                        Toast.LENGTH_LONG).show();
                                itemPosition = 0;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        현재 시간에 양수를 입력하면 다가올날짜
//                        현재 시간에 음수를 입력하면 날짜 이전날짜
                        calender.add(Calendar.MONTH,-1);
                    }
                });
                dialogBuilder.show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
    //RecyclerView 를 상속받아 뷰홀더를 위한 클래스 ViewHolder 를 구현한다.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView time;
        TextView contents;
        TextView maxMemberNumber;
        TextView currentJoinInMember;
        public ViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.myPlanDate);
            time = itemView.findViewById(R.id.myPlanTime);
            contents = itemView.findViewById(R.id.myContents);
            maxMemberNumber = itemView.findViewById(R.id.myMaxMember);
            currentJoinInMember = itemView.findViewById(R.id.myCurrentMember);

        }
    }
}
class HistoryData {
    private int image1;
    private String text1;
    public HistoryData(int image1, String text1) {
        this.image1 = image1;
        this.text1 = text1;
    }
    public int getImage1() {
        return image1;
    }
    public String getText1() {
        return text1;
    }

}