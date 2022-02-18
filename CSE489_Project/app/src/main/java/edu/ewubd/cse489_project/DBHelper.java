package edu.ewubd.cse489_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "Login.db";
    public DBHelper(Context context) {
        super(context, "Login.db", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        MyDB.execSQL("create Table users(name TEXT, email TEXT, Nid TEXT primary key, password TEXT, phone TEXT, gender TEXT)");
    }



    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1){
        MyDB.execSQL("drop Table if exists users"); // If a table named users already exist then drop it
    }



    public Boolean insertData(String name, String email, String Nid, String password, String phone, String gender){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", name);
        contentValues.put("email", email);
        contentValues.put("Nid", Nid);
        contentValues.put("password", password);
        contentValues.put("phone", phone);
        contentValues.put("gender", gender);

        long result = MyDB.insert("users", null, contentValues);
        if(result == -1){
            return false;
        }
        else{
            return true;
        }

    }



    public Boolean checkNidNumber(String Nid){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where Nid = ?", new String[] {Nid});

        if(cursor.getCount() > 0){
            return true;
        }
        else{
            return false;
        }

    }



    public Boolean checkNidPassword(String Nid, String password){
        SQLiteDatabase MyDB = this.getWritableDatabase();

        Cursor cursor = MyDB.rawQuery("Select * from users where Nid = ? and password = ?", new String[] {Nid, password});

        if(cursor.getCount()>0){
            return true;
        }

        else{
            return false;
        }

    }
}