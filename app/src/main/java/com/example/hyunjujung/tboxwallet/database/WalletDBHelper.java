package com.example.hyunjujung.tboxwallet.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 *
 *  [ 모바일 지갑의 계정을 관리할 데이터베이스 클래스 ]
 *
 *  - 모바일 지갑의 json 파일 경로를 데이터베이스에 저장한다
 */

public class WalletDBHelper extends SQLiteOpenHelper{

    public WalletDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        /* 새로운 테이블 생성 */
        sqLiteDatabase.execSQL("CREATE TABLE WalletPath (idx INTEGER PRIMARY KEY AUTOINCREMENT, walletID TEXT, walletPW TEXT, walletPath TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /* 데이터베이스에 모든 지갑을 가져온다 */
    public ArrayList<JSONObject> selectAll() {
        SQLiteDatabase database = getReadableDatabase();
        ArrayList<JSONObject> allWallet = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT walletID FROM WalletPath", null);
        while(cursor.moveToNext()) {
            JSONObject allJson = new JSONObject();
            try{
                allJson.put("WalletID", cursor.getString(0));
            }catch (Exception e) {
                e.printStackTrace();
            }
            allWallet.add(allJson);
        }
        return allWallet;
    }

    /* 해당 되는 지갑 아이디에 지갑 파일이 존재하면 비밀번호, 경로를 모두 가져온다 */
    public JSONObject selectWallet(String walletID) {
        SQLiteDatabase database = getReadableDatabase();
        JSONObject walletJson = new JSONObject();
        Cursor cursor = database.rawQuery("SELECT * FROM WalletPath WHERE walletID= '" + walletID + "'", null);
        while(cursor.moveToNext()) {
            try{
                walletJson.put("idx", cursor.getInt(0));
                walletJson.put("WalletID", cursor.getString(1));
                walletJson.put("WalletPW", cursor.getString(2));
                walletJson.put("WalletPath", cursor.getString(3));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return walletJson;
    }

    /* 데이터베이스에 지갑 아이디, 비밀번호, 지갑 파일 경로 넣는다 */
    public void insertWallet(String walletID, String walletPw, String walletP) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("INSERT INTO WalletPath(walletId, walletPW, walletPath) VALUES('" + walletID + "', '" + walletPw + "', '" + walletP + "');");
        database.close();
    }

}
