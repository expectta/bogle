package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

//notification 에 알람을 등록하는 서비스.
public class NoticeService extends Service {
    NotificationManager notification;
    ServiceThread thread;

    public NoticeService() {
    }
    //서비스가 생성될 때 가장 먼저 호출되는 메서드로, 서비스 자신의 초기 설정
    @Override
    public void onCreate() {
        super.onCreate();
    }
    //startService() 함수가 실행되면 호출되는 함수.
    //intent를 전달받는다.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notification = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread(handler);
        thread.start();
        return START_STICKY;
    }
    //서비스가 소멸될때 호출되는 메서드.
    @Override
    public void onDestroy() {
        thread.stopForever();
        thread = null;
    }
    //bindService()를 실행하면 호출되는 메서드
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @SuppressLint("HandlerLeak")
    //서비스에대한 핸들러 클래스.
    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            //인텐트처럼 데이터를 전달하는 역할을한다.
            //세번째 인자에 인탠트를 생성하고 등록된 알람을 터치 했을때 변경하고자 하는 액티비티를
            //설정하여 화면을 바로 전환 할 수 있다.
            PendingIntent pendingIntent = PendingIntent.getActivity(NoticeService.this,
                    0, new Intent(getApplication(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = "내가 원하는 체널ID, 문자열";
            //notification 을 셋팅하고 등록한다.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("그룹", "채널", NotificationManager.IMPORTANCE_DEFAULT);
                channel.setShowBadge(true);
                notificationManager.createNotificationChannel(channel);
                channelId = channel.getId();
            }
            //알람의 구성 요소를 셋팅한다.
            Notification notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                    .setContentTitle("공지!!!")
                    .setContentText("관리자가 새로운 활동 계획을 등록했습니다")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setVibrate(new long[]{0, 500, 100})
                    .build();
            notificationManager.notify(777, notification);
            //소리추가
//            Notifi.defaults = Notification.DEFAULT_SOUND;
//
//            //알림 소리를 한번만 내도록
//            Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;
//
//            //확인하면 자동으로 알림이 제거 되도록
//            Notifi.flags = Notification.FLAG_AUTO_CANCEL;


//            notification.notify(777, Notifi);

            //토스트 띄우기
            Toast.makeText(NoticeService.this, "공지사항이 왔습니다.", Toast.LENGTH_LONG).show();
        }
    }
}
