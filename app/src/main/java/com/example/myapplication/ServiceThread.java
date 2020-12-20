package com.example.myapplication;

import android.os.Handler;

public class ServiceThread extends Thread{
    Handler handler;
    boolean isRun = true;

    public ServiceThread(NoticeService.myServiceHandler handler){
        this.handler = handler;
    }
    public void stopForever(){
        synchronized (this) {
            this.isRun = false;
        }
    }
    public void run(){
        //반복적으로 수행할 작업을 한다.
        try{
            //관리자가 등록버튼을 누르면 6초 후에 서비스 시작.
            Thread.sleep(6000);
        }catch (Exception e) {}
        //쓰레드에 있는 핸들러에게 메세지를 보냄
        handler.sendEmptyMessage(0);
    }
}