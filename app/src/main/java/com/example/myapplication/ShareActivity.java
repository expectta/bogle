package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;

public class ShareActivity extends AppCompatActivity {
    String TAG = "로그";
    Button btWrite;
    ImageView ivImage;
    ArrayList<ShareData> arrayList = new ArrayList<>();
    Handler handler;
    Thread thread;
    TextView counter;
    ShareAdapter shareAdapter;
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "ShareActivity - onStart() called");
        handler = new Handler();
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "ShareActivity - onResume() called");
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
        Log.d(TAG, "ShareActivity - onPause() called");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "ShareActivity - onStop() called");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ShareActivity - onDestroy() called");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "ShareActivity - onCreate() called");
        String count = String.valueOf(arrayList.size());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        ivImage = findViewById(R.id.itemImage);
        btWrite = findViewById(R.id.writeButton);


        //공유게시판 리사이클러뷰 어댑터

        //리사이클러뷰의 화면이 될 리소스를 찾아 리사이클러 뷰 선언.
        RecyclerView recyclerView = findViewById(R.id.shareListView);
        //아이템 뷰들이 리사이클러뷰 내부에서  배치되는 형태를 관리하는 요소
        //아이템 뷰 세로 정렬.
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        Intent intent = getIntent();
        shareAdapter = new ShareAdapter(arrayList, this);
        //데이터와 어댑터 연결
        recyclerView.setAdapter(shareAdapter);
        //WriteCardActivity 에서 글 게시버튼을 눌렀을 경우 ShareAdapter.arrayList 에 추가적으로 데이터 저장.
        ShareData shareData = (ShareData) intent.getSerializableExtra("submit");
        if (shareData != null) {
            //SharedPreferences 에 데이터 저장함수.
            saveData(shareData);
            //게시글을 저장하고 인탠드 데이터 삭제
            getIntent().removeExtra("submit");
        }
        //리사이클러뷰에서 선택한 아이템 삭제
        String itemPosition = intent.getStringExtra("position");
        if (itemPosition != null) {
            //SharedPreferences 의 데이터 삭제함수.
            deleteData(itemPosition);
            //리사이클러뷰에 아이템을 삭제하고 인텐트 데이터 삭제
            getIntent().removeExtra("position");
        }
        //리사이클러뷰에서 선택한 아이템을 수정.
        //수정할 데이터(사용자가 입력한 값), 아이템 포지션을 인탠트로 전달받아 수정.
        ShareData modifySharedData = (ShareData) intent.getSerializableExtra("postData");
        String position = intent.getStringExtra("itemPosition");
        if (modifySharedData != null) {
            modifyData(modifySharedData, position);
        }
        //OnCreate 함수가 호출 될 시 SharedPreferences 에 저장 되어 있는 데이터를 읽고 리사이클러뷰에 출력.
        readData();
        shareAdapter.notifyDataSetChanged();
        btWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShareActivity.this, WriteCardActivity.class);
                startActivity(intent);
            }
        });
    }
    //SharedPreferences 에 Data 저장 flow
    //JSONObject > JSONArray > SharedPreferences 저장
    public void saveData(ShareData data) {
        JSONObject jsonObject = new JSONObject();
        SharedPreferences sharedPreferences = getSharedPreferences("shareList", MODE_PRIVATE);
        //현재 저장되어있는 데이터를 읽는다.
        try {
            //"shareData"라는 키 값으로 저장되어있는 데이터가 없을때.
            if (sharedPreferences.getString("shareData", "").equals("")) {
                JSONArray jsonArray = new JSONArray();
                //SharedPreferences 에 저장하기 위해 Editor 생성.
                SharedPreferences.Editor dataEditor = sharedPreferences.edit();
                jsonObject.put("image", data.getImage());
                jsonObject.put("title", data.getTitle());
                jsonObject.put("name", data.getName());
                jsonObject.put("date", data.getDate());
                jsonObject.put("contents", data.getContents());
                jsonArray.put(jsonObject);
                dataEditor.putString("shareData", jsonArray.toString());
                dataEditor.apply();
            } else if (!sharedPreferences.getString("shareData", "").equals("")) {
                String readData = sharedPreferences.getString("shareData", "");
                JSONArray jsonArray = new JSONArray(readData);
                //SharedPreferences 에 저장하기 위해 Editor 생성.
                SharedPreferences.Editor dataEditor = sharedPreferences.edit();
                jsonObject.put("image", data.getImage());
                jsonObject.put("title", data.getTitle());
                jsonObject.put("name", data.getName());
                jsonObject.put("date", data.getDate());
                jsonObject.put("contents", data.getContents());
                jsonArray.put(jsonObject);
                dataEditor.putString("shareData", jsonArray.toString());
                dataEditor.apply();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //SharedPreferences 에 저장 되어 있는 데이터를 확인하여 리스트뷰의 리스트에 대입.
    public void readData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shareList", MODE_PRIVATE);
        //planListBackup 폴더에서 "shareList"로 저장되어있는 리스트항목을 읽는다.
        String readData = sharedPreferences.getString("shareData", "");
        //SharedPreferences 에 "shareList"에 저장되어 있는 데이터를 모두 읽어온다.
        try {
            JSONArray jsonArray = new JSONArray(readData);
            //SharedPreferences 에서 불러온 데이터를 JSONArray 형싱으로 읽는다.
            for (int i = 0; i < jsonArray.length(); i++) {
                //jsonArray 에 포함되어 있는 리스트의 갯수를 파악하여 객체형태로 변경한다.
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ShareAdapter.arrayList.add(new ShareData(jsonObject.getString("image"),
                        jsonObject.getString("title"),
                        jsonObject.getString("name"),
                        jsonObject.getString("date"),
                        jsonObject.getString("contents")));
                Log.d(TAG, jsonObject.toString() + "제이슨 리스트 > 오브젝트 변환 값");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        thread =new Thread(){
            @Override
            public void run() {
                Message msg = new Message();
                msg.arg1 = arrayList.size();
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
                Log.d(TAG,String.valueOf(count));
                counter = findViewById(R.id.shareTip);
                counter.setText(String.valueOf(count));
            }
        };
        thread.interrupt();
    }
    //선택된 아이템을 SharedPreferences 에서 삭제.
    public void deleteData(String position) {
        //아이템의 position 은 0부터시작
        int checkedItemPosition = Integer.parseInt(position);

    }
    //선택된 아이템 수정하는 함수.
    public void modifyData(ShareData data, String position) {
        //아이템의 position 은 0부터시작
        int checkedItemPosition = Integer.parseInt(position);
        SharedPreferences sharedPreferences = getSharedPreferences("shareList", MODE_PRIVATE);
        //planListBackup 폴더에서 "planList"로 저장되어있는 리스트항목을 읽는다.
        //TODO : NPE 발생..
        String readData = sharedPreferences.getString("shareData", "");
        try {
            //jsonArray.size()로 사이즈 확인가능
            JSONArray jsonArray = new JSONArray(readData);
            JSONObject jsonObject = new JSONObject();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                SharedPreferences.Editor dataEditor = sharedPreferences.edit();
                jsonObject.put("image", data.getImage());
                jsonObject.put("title", data.getTitle());
                jsonObject.put("name", data.getName());
                jsonObject.put("date", data.getDate());
                jsonObject.put("contents", data.getContents());
                jsonArray.put(checkedItemPosition, jsonObject);
                dataEditor.putString("shareData", jsonArray.toString());
                dataEditor.apply();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
//비트맵으로 이미지를 쉐어드프리퍼런스에 저장하는 코드.
//    public void readImage() {
//        SharedPreferences sharedPreferences = getSharedPreferences("imageBackUp", MODE_PRIVATE);
//        String image = sharedPreferences.getString("bitmapImage", "");
//        ivImage.setImageBitmap(StringToBitamp(image));
//    }
//
//    public Bitmap StringToBitamp(String encodedString) {
//        try {
//            byte[] encodeByte = Base64.decode(encodedString, 0);
//            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
//            return bitmap;
//        } catch (Exception e) {
//            e.getMessage();
//            return null;
//        }
//    }
}

class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.CustomViewHolder> {
    public static ArrayList<ShareData> arrayList;
    public static int itemPosition;
    Context mContext = null;

    public ShareAdapter(ArrayList<ShareData> arrayList, Context mContext) {
        this.arrayList = arrayList;
        this.mContext = mContext;
    }
    //뷰홀더를 만드는 함수
    @NonNull
    @Override
    public ShareAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_share_item, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    //위에서 만든 뷰 홀더를 파라메터 첫번째로 가져온다.
    @Override
    public void onBindViewHolder(@NonNull final ShareAdapter.CustomViewHolder holder, final int position) {
        itemPosition = position;
        holder.image.setImageURI(Uri.parse(arrayList.get(position).getImage()));
        holder.name.setText(arrayList.get(position).getName());
        holder.title.setText(arrayList.get(position).getTitle());
        holder.date.setText(arrayList.get(position).getDate());
        holder.contents.setText(arrayList.get(position).getContents());
        holder.itemView.setTag(position);
        //item을 터치했을때 이벤트 처리
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            //TODO : 레이아웃을 만들자
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
                dialogBuilder.setTitle("작성자 : " + arrayList.get(position).getName());
                dialogBuilder.setMessage(arrayList.get(position).getContents());
                dialogBuilder.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialogBuilder.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences = mContext.getSharedPreferences("shareList", Context.MODE_PRIVATE );
                        //planListBackup 폴더에서 "shareList"로 저장되어있는 리스트항목을 읽는다.
                        String readData = sharedPreferences.getString("shareData", "");
                        try {
                            //jsonArray.size()로 사이즈 확인가능
                            JSONArray jsonArray = new JSONArray(readData);
                            //삭제하고 싶은 리스트를 인덱스(itemPosition)으로 삭제한다.
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                //사용자가 선택한 아이템의 위치를 파악하고 해당 위치를 배열의 인덱스로 접근해서
                                //삭제 시킨다.
                                jsonArray.remove(position);
                            }
                            SharedPreferences.Editor dataEditor = sharedPreferences.edit();
                            //인덱스로 선택된 리스트를 값을 제거 후 저장.
                            dataEditor.putString("shareData", jsonArray.toString());
                            dataEditor.apply();
                            arrayList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, arrayList.size());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
                dialogBuilder.setNeutralButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(mContext, PostModifyActivity.class);
//                        ((ShareActivity)mContext).startActivityForResult(intent,1);
                        intent.putExtra("position", String.valueOf(position));
                        Log.d("로그", position + "쉐어 아답타에서 아이템 위치값");
                        mContext.startActivity(intent);
                        dialog.dismiss();

                    }
                });
                dialogBuilder.show();
            }
        });
    }
    //TODO : 아이템을 길게 눌렀을때 이벤트 리스너

//                String currentName = holder.name.getText().toString();
//                Toast.makeText(v.getContext(), currentName, Toast.LENGTH_SHORT).show();
//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                remove(holder.getAdapterPosition());
//                return true;
//            }
//        });

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }
    //TODO : 아이템 삭제

    //    public void remove(int position) {
//        try {
//            arrayList.remove(position);
//            //노티파이는 새로고침. 아이템 추가/삭제하고 노티파이 해야함.
//            notifyItemRemoved(position);
//        } catch (IndexOutOfBoundsException ex) {
//            ex.printStackTrace();
//        }
//    }
    //아이템 뷰를 저장하는 뷰 홀더 클래스
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView image;
        protected TextView name;
        protected TextView date;
        protected TextView title;
        protected TextView contents;

        //view는 아이템
        public CustomViewHolder(View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.itemImage);
            this.name = itemView.findViewById(R.id.name);
            this.title = itemView.findViewById(R.id.itemTitle);
            this.date = itemView.findViewById(R.id.date);
            this.contents = itemView.findViewById(R.id.contents);
        }
    }
}

class ShareData implements Serializable {
    String image;
    String title;
    String name;
    String date;
    String contents;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ShareData(String image, String title, String name, String date, String contents) {
        this.image = image;
        this.title = title;
        this.name = name;
        this.date = date;
        this.contents = contents;
    }

    public ShareData() {
        super();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}