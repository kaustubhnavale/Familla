package familla.mipl.familla.activity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    static String db_name = "data_db";

    public static final String TABLE_CHAT = "chat";
    public static final String TAG_CHATID = "_id";
    public static final String TAG_SENDERID = "senderid";
    public static final String TAG_RECEIPIENT = "receipient";
    public static final String TAG_GROUPID = "groupid";
    public static final String TAG_CONTENT = "content";
    public static final String TAG_STATUS = "status";
    public static final String TAG_SYNCID = "syncid";
    public static final String TAG_SYNCTIME = "synctime";

    SQLiteDatabase db;

    public Database(Context context) {
        super(context, db_name, null, 1);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        String TABLE_COM = " create table " + TABLE_CHAT + " ( "
                + TAG_CHATID + " integer primary key autoincrement , "
                + TAG_SENDERID + " text , " + TAG_RECEIPIENT + " text ,"
                + TAG_GROUPID + " text , " + TAG_CONTENT + " text , "
                + TAG_STATUS + " text , "
                + TAG_SYNCID + " int , "
                + TAG_SYNCTIME + " text ) ";

        db.execSQL(TABLE_COM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        //db.execSQL("DROP TABLE IF EXISTS TABLE_COMPANY");
        //db.execSQL("DROP TABLE IF EXISTS TABLE_BUYER");
        onCreate(db);
    }
}