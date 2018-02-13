package com.supergolf500.golfchat.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.supergolf500.golfchat.Entity.FriendChatEntity;

/**
 * Created by supergolf500 on 7/11/2559.
 */

public class DbHelper extends SQLiteOpenHelper{

    private static final String databaseName="GOLF_Chat.sqlite";
    private static final int databaseVersion = 1;
    Context ctx;

    private static final String SQL_CREATE_TABLE1 = "CREATE TABLE Message("+
                                                    "RunNo INTEGER PRIMARY KEY,"+
                                                    "MessageFrom VARCHAR(10),"+
                                                    "MessageTo VARCHAR(10),"+
                                                    "SendTime DATETIME DEFAULT CURRENT_TIMESTAMP,"+
                                                    "ReadTime DATETIME DEFAULT CURRENT_TIMESTAMP,"+
                                                    "MessageType VARCHAR(10),"+
                                                    "value TEXT,"+
                                                    "Tag1 TEXT,"+
                                                    "Tag2 TEXT);";



    private static final String SQL_CREATE_TABLE2 = "CREATE TABLE FriendMapping("+
                                                    "FriendKey INTEGER PRIMARY KEY AUTOINCREMENT," +
                                                    "UserID VARCHAR(10),"+
                                                    "FriendID VARCHAR(10),"+
                                                    "EditName VARCHAR(40),"+
                                                    "IsBlock BOOLEAN,"+
                                                    "NotificationBlock BOOLEAN,"+
                                                    "TextSpeech BOOLEAN,"+
                                                    //-----เพิ่ม 3  ฟิวล่างมาเพื่อแสดงในหน้ารายการ Chat
                                                    "LastMessage TEXT,"+
                                                    "LastTimeMessage DATETIME DEFAULT CURRENT_TIMESTAMP,"+
                                                    "CountMessage INTEGER);";





    public DbHelper(Context context) {
        super(context, databaseName, null, databaseVersion);
        ctx = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE1);
        db.execSQL(SQL_CREATE_TABLE2);

//        db.execSQL("INSERT INTO Message(MessageFrom,MessageTo,value) VALUES('U01','U99','XXXX')");
//        db.execSQL("INSERT INTO Message(MessageFrom,MessageTo,value) VALUES('U02','U88','YYYY')");


        FriendChatEntity f1 = new FriendChatEntity();
        f1.UserID="xx";
        f1.FriendID = "U1";
        f1.EditName = "กอล์ฟ";

        FriendChatEntity f2 = new FriendChatEntity();
        f2.UserID="xx";
        f2.FriendID = "U2";
        f2.EditName = "ปุ๊ก";

        FriendChatEntity f3 = new FriendChatEntity();
        f3.UserID="xx";
        f3.FriendID = "U3";
        f3.EditName = "โปรแกรม";

        FriendChatEntity f4 = new FriendChatEntity();
        f4.UserID="xx";
        f4.FriendID = "U4";
        f4.EditName = "แม่กอล์ฟ";


        InsertFriendIDummy(db,f1);
        InsertFriendIDummy(db,f2);
        InsertFriendIDummy(db,f3);
        InsertFriendIDummy(db,f4);

    }

    void InsertFriendIDummy(SQLiteDatabase database,FriendChatEntity result)
    {

        ContentValues values = new ContentValues();
        values.put("UserID", result.UserID);
        values.put("FriendID", result.FriendID);
        values.put("EditName", result.EditName);
        values.put("IsBlock", result.IsBlock);
        values.put("NotificationBlock", result.NotificationBlock);
        values.put("TextSpeech", result.TextSpeech);
        values.put("LastMessage", result.LastMessage);
        values.put("LastTimeMessage", result.LastMessage);
        values.put("CountMessage", result.CountMessage);

        // insert the row
        database.insert("FriendMapping", null, values);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE Message;");
        db.execSQL("DROP TABLE FriendMapping;");
        onCreate(db);




    }



}
