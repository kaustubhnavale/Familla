package familla.mipl.familla.activity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import familla.mipl.familla.R;

public class Main extends Fragment {
    Cursor c1;
    Cursor c_user1;
    int mem_id;
    ListView memlist;
    Cursor c_user;
    MyCursorAdapter adapter;
    public Dialog dialog;
    int height, width, firstx, firsty;
    DatabaseHandler handler;
    SQLiteDatabase db;
    String quer;
    ImageView img;
    ConnectionDetector cd;
    String counttype;
    String grocecount, medicount, appointcouunt, wakecount, eventcount, billcount;
    public static String status;
    Calendar c;
    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
    Integer day, date;
    String dat, da;

    public Main() {
    }

    WebView top_advt, bottom_advt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity) getActivity()).setActionBarTitle("Home");

        handler = new DatabaseHandler(getActivity().getApplicationContext());
        db = handler.getReadableDatabase();
        c = Calendar.getInstance();
        date = c.get(Calendar.DAY_OF_MONTH);
        day = c.get(Calendar.DAY_OF_WEEK);

        da = "";

        if (day.equals(1)) {
            da = "Sun";
        }
        if (day.equals(2)) {
            da = "Mon";
        }
        if (day.equals(3)) {
            da = "Tue";
        }
        if (day.equals(4)) {
            da = "Wed";
        }
        if (day.equals(5)) {
            da = "Thu";
        }
        if (day.equals(6)) {
            da = "Fri";
        }
        if (day.equals(7)) {
            da = "Sat";
        }
        dat = date.toString();

        counttype = "Bill";
        quer = "select * from commontask where commontask_type= '" + counttype + "' and commontask_status ='0'";
        c_user = db.rawQuery(quer, null);
        c_user.moveToFirst();
        billcount = String.valueOf(c_user.getCount());

        if (c_user.getCount() > 10) {
            billcount = "10+";
        }

        counttype = "Event";
        String quer = "select * from commontask where commontask_type= '" + counttype + "' and commontask_status ='0'";
        c_user = db.rawQuery(quer, null);
        c_user.moveToFirst();
        eventcount = String.valueOf(c_user.getCount());

        if (c_user.getCount() > 10) {
            eventcount = "10+";
        }

        counttype = "Appointment";
        quer = "select * from commontask where commontask_type= '" + counttype + "' and commontask_status ='0'";
        c_user = db.rawQuery(quer, null);
        c_user.moveToFirst();
        appointcouunt = String.valueOf(c_user.getCount());

        if (c_user.getCount() > 10) {
            appointcouunt = "10+";
        }

        counttype = "Medicine";
        quer = "select * from commontask where commontask_type= '" + counttype + "' and commontask_status ='0'";
        c_user = db.rawQuery(quer, null);
        c_user.moveToFirst();
        medicount = String.valueOf(c_user.getCount());
        if (c_user.getCount() > 10) {
            medicount = "10+";
        }

        counttype = "Alarm";
        quer = "select * from commontask where commontask_type= '" + counttype + "' and commontask_status ='0'";
        c_user = db.rawQuery(quer, null);
        c_user.moveToFirst();
        wakecount = String.valueOf(c_user.getCount());
        if (c_user.getCount() > 10) {
            wakecount = "10+";
        }

        counttype = "Groceries";
        quer = "select * from commontask where commontask_type= '" + counttype + "' and commontask_status ='0'";
        c_user = db.rawQuery(quer, null);
        c_user.moveToFirst();
        grocecount = String.valueOf(c_user.getCount());
        if (c_user.getCount() > 10) {
            grocecount = "10+";
        }

        if (Login.status.equals("online")) {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHandler.TAG_SYNCSTATUSONILNETIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            db.update(DatabaseHandler.TABLE_SYNCSTATUS, cv, "_id = 1", null);
        }
        if (Login.status.equals("offline")) {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHandler.TAG_SYNCSTATUSOFFLINETIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            db.update(DatabaseHandler.TABLE_SYNCSTATUS, cv, "_id = 1", null);
        }
        cd = new ConnectionDetector(getActivity());
        if (cd.isConnectingToInternet()) {
            status = "online";
        } else {
            status = "offline";
            // onStop();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        DisplayMetrics displaymetrics = new DisplayMetrics();

        //top_advt.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        top_advt = (WebView) rootView.findViewById(R.id.top_advt);
        top_advt.getSettings().setJavaScriptEnabled(true);
        top_advt.loadUrl("http://thefamilla.com/android/test.html");

        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;

        TextView thisdate = (TextView) rootView.findViewById(R.id.thisdate);
        AbsoluteLayout.LayoutParams absParams_date =
                (AbsoluteLayout.LayoutParams) thisdate.getLayoutParams();
        absParams_date.x = 14 * width / 30;
        absParams_date.y = 2 * height / 17;
        absParams_date.width = width / 10;
        absParams_date.height = absParams_date.width;
        thisdate.setText(dat);

        TextView thisday = (TextView) rootView.findViewById(R.id.thisday);
        AbsoluteLayout.LayoutParams absParams_day =
                (AbsoluteLayout.LayoutParams) thisday.getLayoutParams();
        absParams_day.x = 14 * width / 30;
        absParams_day.y = 10 * height / 62;
        absParams_day.width = width / 10;
        absParams_day.height = absParams_day.width;
        thisday.setText(da);

        Button toggle = (Button) rootView.findViewById(R.id.toggle);
        AbsoluteLayout.LayoutParams absParamst =
                (AbsoluteLayout.LayoutParams) toggle.getLayoutParams();
        absParamst.x = 0;
        absParamst.y = 0;
        absParamst.width = width / 10;
        absParamst.height = absParamst.width;

        LinearLayout head = (LinearLayout) rootView.findViewById(R.id.head);
        AbsoluteLayout.LayoutParams absParams0 =
                (AbsoluteLayout.LayoutParams) head.getLayoutParams();
        absParams0.x = 0;
        absParams0.y = 0;
        absParams0.width = width;
        absParams0.height = height / 5;


        ImageView groceries = (ImageView) rootView.findViewById(R.id.first);
        AbsoluteLayout.LayoutParams absParams1 =
                (AbsoluteLayout.LayoutParams) groceries.getLayoutParams();
        absParams1.x = 1 * width / 12;
        absParams1.y = 2 * height / 13;
        absParams1.width = 2 * width / 6;
        absParams1.height = absParams1.width;

        TextView first_cnt = (TextView) rootView.findViewById(R.id.first_cnt);
        AbsoluteLayout.LayoutParams absParams1_cnt =
                (AbsoluteLayout.LayoutParams) first_cnt.getLayoutParams();
        absParams1_cnt.x = 3 * width / 13;
        absParams1_cnt.y = 2 * height / 7;
        absParams1_cnt.width = 2 * width / 6;
        absParams1_cnt.height = absParams1_cnt.width;

        first_cnt.setText(grocecount);

        ImageView all = (ImageView) rootView.findViewById(R.id.sec);
        AbsoluteLayout.LayoutParams absParams2 =
                (AbsoluteLayout.LayoutParams) all.getLayoutParams();
        absParams2.x = 14 * width / 30;
        absParams2.y = 2 * height / 9;
        absParams2.width = width / 5;
        absParams2.height = absParams2.width;

        ImageView event = (ImageView) rootView.findViewById(R.id.third);
        AbsoluteLayout.LayoutParams absParams3 =
                (AbsoluteLayout.LayoutParams) event.getLayoutParams();
        absParams3.x = 7 * width / 10;
        absParams3.y = height / 6;
        absParams3.width = width / 4;
        absParams3.height = absParams3.width;

        ImageView appointment = (ImageView) rootView.findViewById(R.id.fourth);
        AbsoluteLayout.LayoutParams absParams4 =
                (AbsoluteLayout.LayoutParams) appointment.getLayoutParams();
        absParams4.x = 2 * width / 9;
        absParams4.y = 10 * height / 27;
        absParams4.width = 2 * width / 8;
        absParams4.height = absParams4.width;

        ImageView bill = (ImageView) rootView.findViewById(R.id.fifth);
        AbsoluteLayout.LayoutParams absParams5 =
                (AbsoluteLayout.LayoutParams) bill.getLayoutParams();
        absParams5.x = 7 * width / 13;
        absParams5.y = 6 * height / 18;
        absParams5.width = 5 * width / 12;
        absParams5.height = absParams5.width;


        ImageView medicine = (ImageView) rootView.findViewById(R.id.sixth);
        AbsoluteLayout.LayoutParams absParams6 =
                (AbsoluteLayout.LayoutParams) medicine.getLayoutParams();
        absParams6.x = width / 11;
        absParams6.y = 6 * height / 11;
        absParams6.width = 10 * width / 29;
        absParams6.height = absParams6.width;

        ImageView wake = (ImageView) rootView.findViewById(R.id.seventh);
        AbsoluteLayout.LayoutParams absParams7 =
                (AbsoluteLayout.LayoutParams) wake.getLayoutParams();
        absParams7.x = 14 * width / 28;
        absParams7.y = 6 * height / 10;
        absParams7.width = width / 4;
        absParams7.height = absParams7.width;

        Button button = (Button) rootView.findViewById(R.id.button);
        AbsoluteLayout.LayoutParams absParams8 =
                (AbsoluteLayout.LayoutParams) button.getLayoutParams();
        absParams8.x = width * 10 / 12;
        absParams8.y = 6 * height / 10;
        absParams8.width = width / 6;
        absParams8.height = absParams8.width;

        TextView third_cnt = (TextView) rootView.findViewById(R.id.third_cnt);
        AbsoluteLayout.LayoutParams absParams3_cnt =
                (AbsoluteLayout.LayoutParams) third_cnt.getLayoutParams();
        absParams3_cnt.x = 17 * width / 21;
        absParams3_cnt.y = 3 * height / 11;
        absParams3_cnt.width = width / 4;
        absParams3_cnt.height = absParams3.width;
        third_cnt.setText(eventcount);

        TextView fourth_cnt = (TextView) rootView.findViewById(R.id.fourth_cnt);
        AbsoluteLayout.LayoutParams absParams4_cnt =
                (AbsoluteLayout.LayoutParams) fourth_cnt.getLayoutParams();
        absParams4_cnt.x = 3 * width / 9;
        absParams4_cnt.y = 13 * height / 28;
        absParams4_cnt.width = 2 * width / 8;
        absParams4_cnt.height = absParams4_cnt.width;
        fourth_cnt.setText(appointcouunt);

        TextView fifth_cnt = (TextView) rootView.findViewById(R.id.fifth_cnt);
        AbsoluteLayout.LayoutParams absParams5_cnt =
                (AbsoluteLayout.LayoutParams) fifth_cnt.getLayoutParams();
        absParams5_cnt.x = 11 * width / 15;
        absParams5_cnt.y = 9 * height / 18;
        absParams5_cnt.width = 5 * width / 12;
        absParams5_cnt.height = absParams5_cnt.width;
        fifth_cnt.setText(billcount);

        TextView sixth_cnt = (TextView) rootView.findViewById(R.id.sixth_cnt);
        AbsoluteLayout.LayoutParams absParams6_cnt =
                (AbsoluteLayout.LayoutParams) sixth_cnt.getLayoutParams();
        absParams6_cnt.x = 6 * width / 22;
        absParams6_cnt.y = 15 * height / 22;
        /*absParams6_cnt.width = 10*width/29;
        absParams6_cnt.height = absParams6_cnt.width;*/
        sixth_cnt.setText(medicount);

        TextView seveneth_cnt = (TextView) rootView.findViewById(R.id.seventh_cnt);
        AbsoluteLayout.LayoutParams absParams7_cnt =
                (AbsoluteLayout.LayoutParams) seveneth_cnt.getLayoutParams();
        absParams7_cnt.x = 17 * width / 28;
        absParams7_cnt.y = 7 * height / 10;
        absParams7_cnt.width = width / 4;
        absParams7_cnt.height = absParams7_cnt.width;
        seveneth_cnt.setText(wakecount);

        final DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        toggle.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          drawer.openDrawer(Gravity.LEFT);
                                      }
                                  }
        );

        all.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       Intent i = new Intent(getActivity(), Todo.class);
                                       startActivity(i);
                                   }
                               }

        );
        groceries.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             Intent i = new Intent(getActivity(), Groceries.class);
                                             startActivity(i);
                                         }
                                     }
        );

        wake.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String s = "Alarm";
                                        String str = "select * from commontask where commontask_type='" + s + "' order by commontask_createddatetime desc  ";

                                        c1 = db.rawQuery(str, null);
                                        c1.moveToFirst();

                                        if (c1.getCount() == 0) {
                                            Intent i = new Intent(getActivity(), Wake_up.class);
                                            startActivity(i);
                                        } else {
                                            Intent i = new Intent(getActivity(), Wake.class);
                                            startActivity(i);
                                        }
                                    }

                                }
        );

        appointment.setOnClickListener(new View.OnClickListener()

                                       {
                                           @Override
                                           public void onClick(View v) {

                                               String s = "Appointment";
                                               String str = "select * from commontask where commontask_type='" + s + "' order by commontask_createddatetime desc  ";

                                               c1 = db.rawQuery(str, null);
                                               c1.moveToFirst();

                                               if (c1.getCount() == 0) {
                                                   Intent i = new Intent(getActivity(), AddAppointment.class);
                                                   startActivity(i);
                                               } else {
                                                   Intent i = new Intent(getActivity(), Appointment.class);
                                                   startActivity(i);
                                               }
                                           }

                                       }
        );

        medicine.setOnClickListener(new View.OnClickListener()

                                    {
                                        @Override
                                        public void onClick(View v) {
                                            String s = "Medicine";
                                            String str = "select * from commontask where commontask_type='" + s + "' order by commontask_createddatetime desc  ";

                                            c1 = db.rawQuery(str, null);
                                            c1.moveToFirst();

                                            if (c1.getCount() == 0) {
                                                Intent i = new Intent(getActivity(), AddMedicines.class);
                                                startActivity(i);

                                            } else {
                                                Intent i = new Intent(getActivity(), Medicine.class);
                                                startActivity(i);
                                            }
                                        }
                                    }
        );

        bill.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        String s = "Bill";
                                        String str = "select * from commontask where commontask_type='" + s + "' order by commontask_createddatetime desc  ";

                                        c1 = db.rawQuery(str, null);
                                        c1.moveToFirst();

                                        if (c1.getCount() == 0) {
                                            Intent i = new Intent(getActivity(), AddBill.class);
                                            i.addCategory(Intent.CATEGORY_HOME);
                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(i);
                                        } else {
                                            Intent i = new Intent(getActivity(), Bill.class);
                                            i.addCategory(Intent.CATEGORY_HOME);
                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(i);
                                        }
                                    }

                                }
        );

        event.setOnClickListener(new View.OnClickListener()

                                 {
                                     @Override
                                     public void onClick(View v) {
                                         String s = "Event";
                                         String str = "select * from commontask where commontask_type='" + s + "' order by commontask_createddatetime desc  ";

                                         c1 = db.rawQuery(str, null);
                                         c1.moveToFirst();

                                         if (c1.getCount() == 0) {

                                             Intent i = new Intent(getActivity(), AddEvent.class);
                                             startActivity(i);

                                         } else {

                                             Intent i = new Intent(getActivity(), Event.class);
                                             startActivity(i);
                                         }
                                     }
                                 }
        );

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // creating custom Floating Action button
                CustomDialog();
            }
        });
        return rootView;
    }

    public void CustomDialog() {

        String quer1 = "select * from memberdetails ";
        c_user1 = db.rawQuery(quer1, null);
        c_user1.moveToFirst();

        dialog = new Dialog(getActivity());
        // it remove the dialog title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // set the laytout in the dialog
        dialog.setContentView(R.layout.dialogbox);
        // set the background partial transparent

        // set the layout at right bottom

        // it dismiss the dialog when click outside the dialog frame
        dialog.setCanceledOnTouchOutside(true);
        // initialize the item of the dialog box, whose id is demo1
        View demodialog = dialog.findViewById(R.id.cross);

        memlist = (ListView) dialog.findViewById(R.id.member_details);
        adapter = new MyCursorAdapter(getActivity(), c_user1);
        memlist.setAdapter(adapter);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        Window window = dialog.getWindow();
        img = (ImageView) dialog.findViewById(R.id.img_1);

        WindowManager.LayoutParams param = window.getAttributes();
        param.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        // it call when click on the item whose id is demo1.
        demodialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // diss miss the dialog
                dialog.dismiss();
            }
        });

        memlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedFromList = (memlist.getItemAtPosition(position).toString());
                mem_id = (int) parent.getItemIdAtPosition(position);

                String quer1 = "select * from memberdetails where _id='" + mem_id + "' ";
                c_user1 = db.rawQuery(quer1, null);
                c_user1.moveToFirst();

                String Owner = (c_user1.getString(c_user1.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSUSERID)));
                Intent i = new Intent(getActivity(), MemberDetails.class);
                i.putExtra("member_id", Owner);
                startActivity(i);
                //Toast.makeText(getActivity(),String.valueOf(Owner),Toast.LENGTH_LONG).show();
            }
        });
        // it show the dialog box
        dialog.show();
    }

    public class MyCursorAdapter extends CursorAdapter {
        public MyCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.member_details, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            TextView text = (TextView) view.findViewById(R.id.demotext1);
            text.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_MEMBERDETAILSUSERNAME)));
        }

    }
}