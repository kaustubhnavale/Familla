package familla.mipl.familla.activity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    public static String db_name = "data_familla";
    SQLiteDatabase db;
    public static final String TABLE_USERDETAILS = "userdetails";
    public static final String TABLE_MEMBERDETAILS = "memberdetails";
    public static final String TABLE_TASKDETAILS = "taskdetails";
    public static final String TABLE_SHARINGDETAILS = "trn_sharingdetails";
    public static final String TABLE_REMINDERDETAILS = "trn_reminderdetails";
    public static final String TABLE_TASKNAMES = "tasknames";
    public static final String TABLE_COMMONTASK = "commontask";
    public static final String TABLE_SYNCSTATUS = "syncstatus";
    public static final String TABLE_MEDICINE = "medicine";
    public static final String TABLE_Groceries = "groceries";
    public static final String TABLE_CONTACT = "contact";

    public static final String TAG_USERDETAILSID = "_id";
    public static final String TAG_USERDETAILSEMAIL = "ud_email";
    public static final String TAG_USERDETAILSTYPEID = "ud_typeid";
    public static final String TAG_USERDETAILSUSERNAME = "ud_username";
    public static final String TAG_USERDETAILSGROUPID = "ud_groupid";
    public static final String TAG_USERDETAILSGROUPPASSWORD = "ud_grouppassword";
    public static final String TAG_USERDETAILSLOGIN = "ud_loginstatus";
    public static final String TAG_USERADDEDBY = "ud_addedby";
    public static final String TAG_STATUS = "ud_status";
    public static final String TAG_LOGIN_STATUS = "ud_login_status";
    public static final String TAG_REBOOT = "ud_reboot";
    public static final String TAG_PHONE = "ud_phone";
    public static final String TAG_FAMILYNAME = "ud_fname";

    public static final String TAG_CONTACTID = "_id";
    public static final String TAG_NAME = "name";
    public static final String TAG_PHONENUMBER = "contactno";
    public static final String TAG_Role = "role";

    public static final String TAG_MEMBERDETAILSID = "_id";
    public static final String TAG_MEMBERDETAILSGROUPID = "md_groupid";
    public static final String TAG_MEMBERDETAILSUSERNAME = "md_username";
    public static final String TAG_MEMBERDETAILSUSERID = "md_userid";
    public static final String TAG_MEMBERDETAILSTYPEID = "md_typeid";
    public static final String TAG_MEMBERDETAILSTYPENAME = "md_typename";
    public static final String TAG_MEMBERDETAILSUSEREMAIL = "md_useremail";
    public static final String TAG_USERDETAILSUSERID = "ud_userid";
    public static final String TAG_MEMBERDETAILSTIMESTAMP = "md_timestamp";
    public static final String TAG_MEMBERADDEDBY = "md_addedby";
    public static final String TAG_MEMBERSTATUS = "md_status";
    public static final String TAG_MemberRole = "mem_role";

    public static final String TAG_TASKID = "_id";
    public static final String TAG_TASKNAME = "task_name";
    public static final String TAG_TASKOWNER = "task_owner";
    public static final String TAG_TASKOWNERID = "task_ownerid";
    public static final String TAG_TASKTYPE = "task_type";
    public static final String TAG_TASKREMINDER = "task_reminder";
    public static final String TAG_TASKNOTE = "task_note";
    public static final String TAG_TASKTIMESTAMP = "task_timestamp";
    public static final String TAG_TASKGROUPID = "task_groupid";
    public static final String TAG_TASKONLINESYNCID = "task_onlinesyncid";
    public static final String TAG_TASKSYNCDATETIME = "task_syncdatetime";

    public static final String TAG_SHAREID = "_id";
    public static final String TAG_SHARELOCALID = "share_localid";
    public static final String TAG_SHAREUSERID = "share_userid";

    public static final String TAG_REMINDERID = "_id";
    public static final String TAG_REMINDERDATETIME = "reminder_datetime";
    public static final String TAG_REMINDERTASKID = "reminder_taskid";

    public static final String TAG_TASKNAMEID = "_id";
    public static final String TAG_TASKNAMELABEL = "taskname_label";
    public static final String TAG_TASKNAMETYPE = "taskname_type";

    public static final String TAG_COMMONTASKID = "_id";
    public static final String TAG_COMMONTASKTYPE = "commontask_type";
    public static final String TAG_COMMONTASKREMINDERDATETIME = "commontask_reminderdatetime";
    public static final String TAG_COMMONTASKCREATEDDATETIME = "commontask_createddatetime";
    public static final String TAG_COMMONTASKNAME = "commontask_name";
    public static final String TAG_COMMONTASKEXTRANOTE = "commontask_extranote";
    public static final String TAG_COMMONTASKDETAIL = "commontask_detail";
    public static final String TAG_COMMONTASKOWNERID = "commontask_ownerid";
    public static final String TAG_COMMONTASKGROUPID = "commontask_groupid";
    public static final String TAG_COMMONTASKSYNCID = "commontask_syncid";
    public static final String TAG_COMMONTASKSYNCDATETIME = "commontask_syncdatetime";
    public static final String TAG_COMMONTASKEDITEDDATETIME = "commontask_editeddatetime";
    public static final String TAG_COMMONTASKSETREPEATING = "commontask_setrepeating";
    public static final String TAG_COMMONTASKREIMNDERCANCELLED = "commontask_remindercancelled";
    public static final String TAG_COMMONTASKSTATUS = "commontask_status";
    public static final String TAG_COMMONTASKREMINDER = "commontask_reminder";

    public static final String TAG_COMMONTASKISACTIVE = "commontask_isactive";
    public static final String TAG_COMMONTASKNotificationid = "commontask_notificationid";

    public static final String TAG_MEDICINEID = "_id";
    public static final String TAG_TASK_ID = "task_id";
    public static final String TAG_PERSONNAME = "person_name";
    public static final String TAG_PARTOFDAY = "part_day";
    public static final String TAG_SPECIALINSTRUCTION = "special_instruction";
    public static final String TAG_FREQUENCY = "frequency";
    public static final String TAG_MEDICINECOUNT = "medicine_count";
    public static final String TAG_DOCTORNAME = "doctor_name";
    public static final String TAG_DOSE = "dose";

    public static final String TAG_SYNCSTATUSID = "_id";
    public static final String TAG_SYNCSTATUSONILNETIME = "syncstatus_onlinetime";
    public static final String TAG_SYNCSTATUSOFFLINETIME = "syncstatus_offlinetime";
    public static final String TAG_SYNCSTATUSCALLED = "syncstatus_called";
    public static final String TAG_SYNCSTATUSCOMPLETED = "syncstatus_completed";
    public static final String TAG_SYNCSTATUSDONE = "syncstatus_done";

    public static final String TAG_GROCERIESID = "_id";
    public static final String TAG_GROCERIESNAME = "groceries_item";

    public DatabaseHandler(Context context) {
        super(context, db_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String TABLE_USER = " create table " + TABLE_USERDETAILS + " ( " + TAG_USERDETAILSID + " integer primary key autoincrement , "
                + TAG_USERDETAILSUSERID + " text , "
                + TAG_USERDETAILSEMAIL + " text , "
                + TAG_USERDETAILSTYPEID + " text , "
                + TAG_USERDETAILSUSERNAME + " text , "
                + TAG_USERDETAILSGROUPID + " text , "
                + TAG_USERDETAILSGROUPPASSWORD + " text , "
                + TAG_USERDETAILSLOGIN + " boolean ,"
                + TAG_USERADDEDBY + " text , "
                + TAG_STATUS + " text ,"
                + TAG_Role + " text,"
                + TAG_LOGIN_STATUS + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + TAG_REBOOT + " DATETIME,"
                + TAG_PHONE + " text,"
                + TAG_FAMILYNAME + " text ) ";

        String TABLE_Contact = " create table " + TABLE_CONTACT + " ( " + TAG_CONTACTID + " integer primary key autoincrement , " + TAG_NAME + " text , " + TAG_PHONENUMBER + " text ) ";

        String TABLE_MEMBERS = " create table " + TABLE_MEMBERDETAILS + " ( " + TAG_MEMBERDETAILSID + " integer primary key autoincrement , "
                + TAG_MEMBERDETAILSGROUPID + " text , "
                + TAG_MEMBERDETAILSUSERNAME + " text , "
                + TAG_MEMBERDETAILSUSERID + " text , "
                + TAG_MEMBERDETAILSTYPEID + " text , "
                + TAG_MEMBERDETAILSTYPENAME + " text , "
                + TAG_MEMBERDETAILSUSEREMAIL + " text , "
                + TAG_MEMBERDETAILSTIMESTAMP + " text , "
                + TAG_MEMBERADDEDBY + " text , "
                + TAG_MEMBERSTATUS + " text ,"
                + TAG_MemberRole + " text ) ";

        String TABLE_SHARE = " create table " + TABLE_SHARINGDETAILS + " ( " + TAG_SHAREID + " integer primary key autoincrement , "
                + TAG_SHARELOCALID + " text ," + TAG_SHAREUSERID + " text )";

        String TABLE_REMINDER = " create table " + TABLE_REMINDERDETAILS + " ( " + TAG_REMINDERID + " integer primary key autoincrement , "
                + TAG_REMINDERDATETIME + " long , " + TAG_REMINDERTASKID + " text ) ";

        String TABLE_TASKNAME = " create table " + TABLE_TASKNAMES + " ( " + TAG_TASKNAMEID + " integer primary key autoincrement , "
                + TAG_TASKNAMELABEL + " text , " + TAG_TASKNAMETYPE + " text ) ";

        String INSERTDATA = " insert into " + TABLE_TASKNAMES +
                " SELECT '1' as '" + TAG_TASKNAMEID + "' , 'Credit Card' As '" + TAG_TASKNAMELABEL + "' , 'bill' As '" + TAG_TASKNAMETYPE + "'" +
                " UNION ALL SELECT '2' , 'Electricity' , 'bill'" +
                " UNION ALL SELECT '3' , 'Groceries' , 'bill' " +
                " UNION ALL SELECT '4' , 'Mobile' , 'bill'" +
                " UNION ALL SELECT '5' , 'Milk' , 'bill'" +
                " UNION ALL SELECT '6' , 'Society' , 'bill'" +
                " UNION ALL SELECT '7' , 'Maintainance' , 'bill'" +
                " UNION ALL SELECT '8' , 'Water' , 'bill'" +
                " UNION ALL SELECT '9' , 'Paper' , 'bill'" +
                " UNION ALL SELECT '10' , 'Other' , 'bill'";

        String TABLE_COMMON = " create table " + TABLE_COMMONTASK +
                " ( " + TAG_COMMONTASKID + " integer primary key autoincrement , "
                + TAG_COMMONTASKTYPE + " text , "
                + TAG_COMMONTASKREMINDERDATETIME + " timestamp , "
                + TAG_COMMONTASKCREATEDDATETIME + " timestamp , "
                + TAG_COMMONTASKNAME + " text , "
                + TAG_COMMONTASKEXTRANOTE + " text , "
                + TAG_COMMONTASKDETAIL + " text , "
                + TAG_COMMONTASKOWNERID + " text , "
                + TAG_COMMONTASKGROUPID + " text , "
                + TAG_COMMONTASKSYNCID + " text , "
                + TAG_COMMONTASKSYNCDATETIME + " timestamp , "
                + TAG_COMMONTASKEDITEDDATETIME + " timestamp , "
                + TAG_COMMONTASKREIMNDERCANCELLED + " bool , "
                + TAG_COMMONTASKSETREPEATING + " text ,"
                + TAG_COMMONTASKSTATUS + " INTEGER DEFAULT 0,"
                + TAG_COMMONTASKREMINDER + " timestamp)";

        String TABLE_MEDI = " create table " + TABLE_MEDICINE +
                " ( " + TAG_MEDICINEID + " integer primary key autoincrement , "
                + TAG_TASK_ID + " text , "
                + TAG_PERSONNAME + " text , "
                + TAG_PARTOFDAY + " timestamp , "
                + TAG_SPECIALINSTRUCTION + " text , "
                + TAG_FREQUENCY + " text , "
                + TAG_MEDICINECOUNT + " text , "
                + TAG_DOCTORNAME + " text , "
                + TAG_DOSE + " text)";

        String TABLE_SYNC = " create table " + TABLE_SYNCSTATUS + " ( " + TAG_SYNCSTATUSID + " integer primary key autoincrement , "
                + TAG_SYNCSTATUSONILNETIME + " timestamp , " + TAG_SYNCSTATUSOFFLINETIME + " timestamp , " + TAG_SYNCSTATUSCALLED + " timestamp ,"
                + TAG_SYNCSTATUSCOMPLETED + " timestamp , " + TAG_SYNCSTATUSDONE + " integer ) ";


        String TABLE_GROC = " create table " + TABLE_Groceries +
                " ( " + TAG_GROCERIESID + " integer primary key autoincrement , " + TAG_GROCERIESNAME + " text   )";

        String INSERTSYNC = "insert into " + TABLE_SYNCSTATUS + " ( " + TAG_SYNCSTATUSID + " , " + TAG_SYNCSTATUSDONE + " ) values (1 , 2)";

        db.execSQL(TABLE_USER);
        db.execSQL(TABLE_MEMBERS);
        db.execSQL(TABLE_SHARE);
        db.execSQL(TABLE_REMINDER);
        db.execSQL(TABLE_TASKNAME);
        db.execSQL(INSERTDATA);
        db.execSQL(TABLE_COMMON);
        db.execSQL(TABLE_SYNC);
        db.execSQL(TABLE_MEDI);
        db.execSQL(INSERTSYNC);
        db.execSQL(TABLE_GROC);
        db.execSQL(TABLE_Contact);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public List<String> getAllLabels() {
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MEMBERDETAILS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(2));

            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }

    public boolean getStatus() {
        return false;
    }
}