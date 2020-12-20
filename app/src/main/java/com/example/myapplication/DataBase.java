package com.example.myapplication;

import android.content.SharedPreferences;



//사용자가 입력한 정보를 SharedPreference 에 저장하고 저장되어있는 데이터를 읽어오는 클래스.
public class DataBase {
    private SharedPreferences sharedPreferences;


    public void saveData (PlanListData targetData ){
//        sharedPreferences = getSharedPreferences("planList", MODE_PRIVATE);
//        SharedPreferences.Editor dataEditor = sharedPreferences.edit();
//        try {
//            jsonObject.put("planDate", targetData.getPlanDate());
//            jsonObject.put("time", targetData.getTime());
//            jsonObject.put("contents", targetData.getContents());
//            jsonObject.put("maxMemberNumber", targetData.getMaxMemberNumber());
//            jsonArray.put(jsonObject);
//            dataEditor.putString("num", jsonArray.toString());
//            dataEditor.commit();
//        }catch(JSONException e){
//            e.printStackTrace();
//        }

    }
    public void readData(Object dataClass){
//        if(planListData !=null){
////            parseJson();
//            planObject = new JSONObject();
//            Log.d(TAG, planArrayJson.length() +"제이슨 리스트");
//
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
//
//
//            Log.d(TAG, planItemList.toString());
//        }
//        //쉐어드 데이터 읽기
//        preferences = getSharedPreferences("planInfo", MODE_PRIVATE);
//        String asdf= preferences.getString("num","");
//        Log.d(TAG, asdf + "데이터 읽었다!!!");

    }


}
