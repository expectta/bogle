package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//다이빙 계획 클래스
public class CommunityActivity extends AppCompatActivity {
    String TAG = "로그";
    ListView btPlanListView;
    String itemPosition = "";
    String loginUserNumber;
    TextView counter;
    Handler handler;
    PlanListAdapter planListAdapter;
    ArrayList<PlanListData> planItemList = new ArrayList<>();
    SharedPreferences sharedPreferences;
    Thread thread;

    private String planDate;

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "CommunityActivity - onRestart() called");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "CommunityActivity - onStart() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
        Log.d(TAG, "CommunityActivity - onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();

        Log.d(TAG, "CommunityActivity - onStop() called");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "CommunityActivity - onResume() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "CommunityActivity - onDestroy() called");
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        Log.d(TAG, "CommunityActivity - onCreate() called");


        btPlanListView = findViewById(R.id.planListView);
        planListAdapter = new PlanListAdapter(this, planItemList);
        btPlanListView.setAdapter(planListAdapter);
        Log.d(TAG, "commnunity에서 회원 번호" + MainActivity.loginUserNumber);

        Intent intent = getIntent();
        //접속자가 관리자인지 일반회원인지 확인이 필요함.
        //superUser = 접속자가 관리자
        loginUserNumber = intent.getStringExtra("userNumber");


        //리스트뷰에 있는 item 을 석택하여 수정버튼을 눌렀을때.
        PlanListData modifyListItem = (PlanListData) intent.getSerializableExtra("result");
        if (modifyListItem != null) {
            modifyData(modifyListItem, intent.getStringExtra("position"));
            Log.d(TAG, intent.getStringExtra("curItem") + "현재 내가 누른 아이템 번호");
            getIntent().removeExtra("result");
        }
        //AdministratorActivity 에서 작성한 게시글 등록.
        PlanListData planListData = (PlanListData) intent.getSerializableExtra("submitPlan");
        Log.d(TAG, planListData + "계획 등록 후 리스트 글");
        if (planListData != null) {
            //전달받은 데이터를 SharedPreferences 에 저장하는 함수.
            saveData(planListData);
            //데이터 저장이 완료되고 intent 로 전달받은 데이터를 삭제.
            getIntent().removeExtra("submitPlan");
        }
        //리스트뷰 아이템 삭제버튼눌었을때 데이터 삭제
        itemPosition = intent.getStringExtra("checkedItemNumber");
        if (itemPosition != null) {
            //선택된 item 을 sharedPreferences 에서 삭제
            deleteData(itemPosition);
            //데이터 삭제 후 intent 로 전달받은 데이터 삭제.
            getIntent().removeExtra("checkedItemNumber");
        }
        //SharedPreferences 에 저장되어 있는 데이터를 읽고 리스트뷰에 대입.
        readData();
        stopService(intent);
        //인자는 float형 이고 투명도를 의미
//        animation = new AlphaAnimation(0.0f, 1.0f);
//        //투명도 지속시간을 정의 100 = 1초
//        animation.setDuration(1000);
//        //다음 에니메이션이 동작할때까지의 시간
//        animation.setStartOffset(1500);
//        //에니메이션을 반복할지 안할지.. 필요 없으면 코드삭제
//        animation.setRepeatMode(Animation.REVERSE);
//        //에니메이션의 반복 횟수를 인자로 넣는다.
//        animation.setRepeatCount(Animation.INFINITE);
//        tvTipText.startAnimation(animation);
         /*
         ArrayList 를 함수 내 지역변수에서 실체화 시키면 list 의 값들이 계속 초기화 된다. ※주의
        관리자가 게시글을 작성 했을경우 submitPlan 이름으로 객체를 전달받아 리스트에
        전달받은 객체로 리스트에 추가한다.
        */
        btPlanListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //아이템 터치 이벤트에대한 정보를 얻어올때
                ApplicationInfo info = (ApplicationInfo) parent.getAdapter().getItem(position);
                Log.d(TAG, String.valueOf(position + id));
            }
        });

    }


    //SharedPreferences 에 Data 저장 flow
    //JSONObject > JSONArray > SharedPreferences 저장
    //활동계획 게시글을 등록하고 SharedPreferences 에 저장.
    public void saveData(PlanListData data) {
        JSONObject jsonObject = new JSONObject();
        sharedPreferences = getSharedPreferences("planListBackup", MODE_PRIVATE);
        //현재 저장되어있는 데이터를 읽는다.
        try {
            //"planList"라는 키 값으로 저장되어있는 데이터가 없을때.
            if (sharedPreferences.getString("planList", "").equals("")) {
                JSONArray jsonArray = new JSONArray();
                SharedPreferences.Editor dataEditor = sharedPreferences.edit();
                jsonObject.put("planDate", data.getPlanDate());
                jsonObject.put("time", data.getTime());
                jsonObject.put("contents", data.getContents());
                jsonObject.put("maxMemberNumber", data.getMaxMemberNumber());
                jsonArray.put(jsonObject);
                dataEditor.putString("planList", jsonArray.toString());
                dataEditor.apply();
                Log.d(TAG, jsonArray.toString() + "데이터 저장 완료");
                //"셀렉리스트" : [{"이미지유알엘":" ","제품명":" ","사이트링크":""}, == index = 0
                //              {"이미지유알엘":" ","제품명":" ","사이트링크":""}, == index = 1
                //              {"planDate":"","time":"","contents":"85285285285","maxMemberNumber":""}] == index = 2
                //"planList"라는 키 값으로 저장되어있는 데이터가 있을때
            } else if (!sharedPreferences.getString("planList", "").equals("")) {
                String readData = sharedPreferences.getString("planList", "");
                JSONArray jsonArray = new JSONArray(readData);
                SharedPreferences.Editor dataEditor = sharedPreferences.edit();
                jsonObject.put("planDate", data.getPlanDate());
                jsonObject.put("time", data.getTime());
                jsonObject.put("contents", data.getContents());
                jsonObject.put("maxMemberNumber", data.getMaxMemberNumber());
                jsonArray.put(jsonObject);
                dataEditor.putString("planList", jsonArray.toString());
                dataEditor.apply();
                Log.d(TAG, jsonArray.toString() + "데이터 저장 완료");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //SharedPreferences 에 저장 되어 있는 데이터를 확인하여 리스트뷰의 리스트에 대입.
    @SuppressLint("HandlerLeak")
    public void readData() {
        SharedPreferences sharedPreferences = getSharedPreferences("planListBackup", MODE_PRIVATE);
        //planListBackup 폴더에서 "planList"로 저장되어있는 리스트항목을 읽는다.
        String readData = sharedPreferences.getString("planList", "");
        //SharedPreferences 에 "planLsit"에 저장되어 있는 데이터를 모두 읽어온다.
        //데이터 형식은 Array
        try {
            JSONArray jsonArray = new JSONArray(readData);
            //SharedPreferences 에서 불러온 데이터를 JSONArray 형싱으로 읽는다.
            for (int i = 0; i < jsonArray.length(); i++) {
                //jsonArray 에 포함되어 있는 리스트의 갯수를 파악하여 객체형태로 변경한다.
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                planDate = jsonObject.getString("planDate");
                planItemList.add(new PlanListData(jsonObject.getString("planDate"),
                        jsonObject.getString("time"),
                        jsonObject.getString("contents"),
                        jsonObject.getString("maxMemberNumber")));
                Log.d(TAG, jsonObject.toString() + "제이슨 리스트 > 오브젝트 변환 값");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        thread = new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.arg1 = planItemList.size();
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
                counter = findViewById(R.id.counterText);
                counter.setText(String.valueOf(count));
            }
        };
        thread.interrupt();
    }

    //선택된 아이템을 SharedPreferences 에서 삭제.
    public void deleteData(String position) {
        //TODO : 만약 2라면.
        //TODO : 이렇게 지워야한 sp.edit().remove("clientToken").commit();
        //PlanListAdapter 로 부터 받아온 item position 값을 Int 데이터 타입으로 변경.
        //position은 0부터시작
        int checkedItemPosition = Integer.parseInt(position);
        SharedPreferences sharedPreferences = getSharedPreferences("planListBackup", MODE_PRIVATE);
        //planListBackup 폴더에서 "planList"로 저장되어있는 리스트항목을 읽는다.
        String readData = sharedPreferences.getString("planList", "");
        try {
            //jsonArray.size()로 사이즈 확인가능
            JSONArray jsonArray = new JSONArray(readData);
            //삭제하고 싶은 리스트를 인덱스(itemPosition)으로 삭제한다.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                jsonArray.remove(checkedItemPosition);
            }
            Log.d(TAG, jsonArray.toString() + "현재 파일 전채 내용.");
            SharedPreferences.Editor dataEditor = sharedPreferences.edit();
            //인덱스로 선택된 리스트를 값을 제거 후 저장.
            dataEditor.putString("planList", jsonArray.toString());
            dataEditor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //쉐어드에 저장되어 있는 파일읅 읽는 함수.
    public void modifyData(PlanListData data, String position) {
        int checkedItemPosition = Integer.parseInt(position);
        SharedPreferences sharedPreferences = getSharedPreferences("planListBackup", MODE_PRIVATE);
        //planListBackup 폴더에서 "planList"로 저장되어있는 리스트항목을 읽는다.
        String readData = sharedPreferences.getString("planList", "");
        try {
            //jsonArray.size()로 사이즈 확인가능
            JSONArray jsonArray = new JSONArray(readData);
            JSONObject jsonObject = new JSONObject();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                SharedPreferences.Editor dataEditor = sharedPreferences.edit();
                jsonObject.put("planDate", data.getPlanDate());
                jsonObject.put("time", data.getTime());
                jsonObject.put("contents", data.getContents());
                jsonObject.put("maxMemberNumber", data.getMaxMemberNumber());
                jsonArray.put(checkedItemPosition, jsonObject);
                dataEditor.putString("planList", jsonArray.toString());
                dataEditor.apply();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //로그아웃 버튼을 누르면 쉐어드 프리퍼런스에 있는 데이터 모두 삭제.
    public void sharedPreferencesAllClear() {
        SharedPreferences planListBackup = getSharedPreferences("planListBackup", MODE_PRIVATE);
        SharedPreferences.Editor edit = planListBackup.edit();
        edit.clear();
        edit.apply();
        SharedPreferences myItemList = getSharedPreferences("myItemList", MODE_PRIVATE);
        SharedPreferences.Editor myItemListEdit = myItemList.edit();
        myItemListEdit.clear();
        myItemListEdit.apply();
        SharedPreferences shareList = getSharedPreferences("shareList", MODE_PRIVATE);
        SharedPreferences.Editor shareListEdit = shareList.edit();
        shareListEdit.clear();
        shareListEdit.apply();
        SharedPreferences memberList = getSharedPreferences("memberList", MODE_PRIVATE);
        SharedPreferences.Editor memberListEdit = memberList.edit();
        memberListEdit.clear();
        memberListEdit.apply();
    }
}
//메세지 객체가 아닌 Runnable 객체를 보낼때 핸들러 생성만 하면 된다.
       /* //메인 스레드가 아닌 다른 스레드는 뷰에대한 직접적인 접근을 할 수 없다.
        //수신 핸들러
        //핸들러 객체 생성과 동시에 해당 스레드에서 동작중인 루터, 메시지큐와 자동 연결된다.
        handler = new Handler() {
            @Override
            //루퍼에의해 실행되는 함수.
            //송신 쓰레드에서 전달받은 파라메터를 통해 코드가 진행됨.
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 0:
                        tvTipText.setText("테스트1");

                        break;
                    case 1:
                        break;
                    default:
                }
            }
        };
        //메세지 송신 쓰레드
        class TipText implements Runnable {
            ArrayList<String> tipArray = new ArrayList<>();
            //송신스레드에서는
            //수신스레드의 핸들러 객체 참조를 획득해야 한다.
            Handler sendhandler = handler;

            //동작해야하는 쓰레드 코드
            @Override
            public void run() {
                tipArray.add("TIP : 다이빙 전 스트레칭 필수!!");
                tipArray.add("TIP : 수신호를 꼭 숙지하세요!!");
                tipArray.add("TIP : 해양생물은 만지면 안돼요~");
                while (true) {
                    //메세지 객체를 획득하기 위해서 사용하는 메서드.
                    //메세지 클래스 변수 , int What ,int arg1, int arg2, Object obj ,Messenger replyTo(메신저 회신용)
                        Message message = sendhandler.obtainMessage();
                    for (int i = 0; i < tipArray.size(); i++) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        tipArray.get(i);
                    }
                    //송실할 메세지객체
                    handler.sendEmptyMessage(0);
                }
            }
        }
        //만들어진 쓰레드클래스를 실체화 하고 쓰레드를 실행한다.
        TipText tvTipText = new TipText();
        Thread tipTextThread = new Thread(tvTipText);
        tipTextThread.start();
    }*/

//    class TipTextThread extends AsyncTask<Void, Integer, Void> {
//        //백그라운드에서 동작하는 쓰레드
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//
////            for(int i=0; i<10; i++) {
////                try {
////                    Thread.sleep(100);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
////                final int percent = i;
//            //퍼블리쉬프로그래스 함수를 꼭 사용해서 호출해야함.
//            //onProgressUpdate를 호출 함.
////                publishProgress(percent);
//            //UI에서 동작이 가능하게 하는 쓰레드
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    while (true) {
//                        tvTipText.setText("테스트1");
//                        try {
//                            Thread.sleep(100);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        tvTipText.setText("테스트2");
//                    }
//                }
//            });
//
//            return null;
//    }
//이것이 Ui쓰레드에서 돈다.
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
//...은 여러개 값을 가져 올 수 있다.
//여기서는 0번째 값만 가져온다
//            mTextView.setText(values[0] + %);
//                    mProgressBar.setProgress(values[0]);

//}
//public class CustomDialog{
//    private Context context;
//
//    public CustomDialog(Context context) {
//        this.context = context;
//    }
//
//    // 호출할 다이얼로그 함수를 정의한다.
//    public void callFunction(final TextView main_label) {
//
//        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
//        final Dialog dlg = new Dialog(context);
//
//        // 액티비티의 타이틀바를 숨긴다.
//        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        // 커스텀 다이얼로그의 레이아웃을 설정한다.
//        dlg.setContentView(R.layout.custom_dialog);
//
//        // 커스텀 다이얼로그를 노출한다.
//        dlg.show();
//
//        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
//        final EditText message = (EditText) dlg.findViewById(R.id.mesgase);
//        final Button okButton = (Button) dlg.findViewById(R.id.okButton);
//        final Button cancelButton = (Button) dlg.findViewById(R.id.cancelButton);
//
//        okButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // '확인' 버튼 클릭시 메인 액티비티에서 설정한 main_label에
//                // 커스텀 다이얼로그에서 입력한 메시지를 대입한다.
//                main_label.setText(message.getText().toString());
//                Toast.makeText(context, "\"" +  message.getText().toString() + "\" 을 입력하였습니다.", Toast.LENGTH_SHORT).show();
//
//                // 커스텀 다이얼로그를 종료한다.
//                dlg.dismiss();
//            }
//        });
//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(context, "취소 했습니다.", Toast.LENGTH_SHORT).show();
//
//                // 커스텀 다이얼로그를 종료한다.
//                dlg.dismiss();
//            }
//        });


//데이터 정경로 data/data/(package_name)/shared_prefs/SharedPreference
//getSharedPreferences() 첫번째 매개변수로 지정하는 이름으로 식별되는 여러 공유앱의 모든 Context에서 이 메서드를 호출할 수 있습니다
//환경 서정 파일이 필요한경우 사용.
//getPreferences() — 활동에 하나의 공유 환경설정 파일만 사용해야 하는 경우 Activity에서 이 메서드를 사용합니다
//getDefaultSharedPreferences() 파일을 가지로 올때 사용하는 함수.
//공유 환경설정 파일에 쓰려면 SharedPreferences에서 edit()를 호출하여 SharedPreferences.Editor를 생성해야 합니다.
//대신 EncryptedSharedPreferences 개체에서 edit() 메서드를 호출하여 더 안전한 방식
//apply()는 메모리 내 SharedPreferences 개체를 즉시 변경하지만 업데이트를 디스크에 비동기적으로 씁니다.
// 또는 commit()을 사용하여 데이터를 디스크에 동기적으로 쓸 수도 있습니다.
// 그러나 commit()은 동기적이므로 기본 스레드에서 이 메서드를 호출하는 것을 피해야 합니다.
// UI 렌더링이 일시중지될 수 있기 때문입니다.
            /*SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getString(R.string.saved_high_score_key), newHighScore);
            editor.commit();*/
//PlanListView 가있는 CommnunityActivity 에서 item 이 등록 되었을때 값을 저장한다
            /*MODE_PRIVATE : 자기 앱 내에서 사용. 외부 앱에서 접근 불가
            MODE_WORLD_READABLE : 외부 앱에서 읽기 가능
            MODE_WORLD_WRITEABLE : 외부 앱에서 쓰기 가능
            */
//            SharedPreferences preferences =getSharedPreferences("planList", MODE_PRIVATE);
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.putInt("num", 10);
//            editor.commit();


//        JSONArray planArrayJson = new JSONArray();
//        for(int i = 0 ; i < planItemList.size(); i++){
//            JSONObject planObject = new JSONObject();
//            try {
//                //JOSN으로 데이터를 하나씩 저장한다.
//                planObject.put("planDate",planListData.getPlanDate());
//                planObject.put("time",planListData.getTime());
//                planObject.put("getContents",planListData.getContents());
//                planObject.put("getMaxMemberNumber",planListData.getMaxMemberNumber());
//                planArrayJson.put(planObject);
//                Log.d(TAG, planArrayJson.toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        for(int i= 0; i <planArrayJson.length(); i++ ){
//            try{
//                JSONObject jsonObject = planArrayJson.getJSONObject(i);
//                planItemList.add(new PlanListData(
//                        jsonObject.getString("planDate"),
//                        jsonObject.getString("time"),
//                        jsonObject.getString("getContents"),
//                        jsonObject.getString("getMaxMemberNumber")));
//
//
//            }catch (JSONException e){
//                e.printStackTrace();
//            }
//        }
//        if(planDate != null && time !=null && getContents != null &&getMaxMemberNumber!=null ) {
//            SharedPreferences sharedPreferences = getSharedPreferences("planInfo", MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString("name", planDate);
//            editor.putString("time", time);
//            editor.putString("getContents", getContents);
//            editor.putString("getMaxMemberNumber", getMaxMemberNumber);
//            editor.commit();
//
//        }
//    }
//    public ArrayList getSvaedData(String key){
//        SharedPreferences sharedPreferences = getSharedPreferences("planInfo", MODE_PRIVATE);
//        String json = sharedPreferences.getString(key,null);
//        ArrayList arrayList = new ArrayList<>();
//        if(json != null){
//            try{
//                JSONArray jsonArray = new JSONArray(json);
//                for(int i = 0 ; i < jsonArray.length(); i++){
//                    String url = jsonArray.optString(i);
//                    arrayList.add(url);
//                }
//            }catch (JSONException e){
//                e.printStackTrace();
//            }
//        }
//         return arrayList;


//        joinIn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//Intent getIntent()호출하여 전달받을 내용을 대입.
//        Intent intent = getIntent();
//        //전달받은 데이터를 사용할 TextView를 선언
//        TextView notice = findViewById(R.id.communityTextNotice);
//        //TextView에 인탠트가가지고 있는 데이터를 키 값으로 찾아서 대입.
//        notice.setText(intent.getStringExtra("notice"));




