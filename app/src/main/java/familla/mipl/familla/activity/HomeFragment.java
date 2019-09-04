package familla.mipl.familla.activity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import familla.mipl.familla.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    SharedPreferences sharedpreferences;
    public Dialog dialog;
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    ImageView event, appoint, bill, medicine, wake, groceries, all;
    Calendar c;
    Cursor c1;
    Cursor c_user1;
    ListView memlist;
    MyCursorAdapter adapter;
    DatabaseHandler handler;
    SQLiteDatabase db;
    ConnectionDetector cd;
    public static String status;
    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
    Integer day, date;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity) getActivity()).setActionBarTitle("Home");
        handler = new DatabaseHandler(getActivity().getApplicationContext());

        db = handler.getReadableDatabase();

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
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        Button btn = (Button) rootView.findViewById(R.id.button);

        c = Calendar.getInstance();

        TextView Day = (TextView) rootView.findViewById(R.id.Day);
        TextView Date = (TextView) rootView.findViewById(R.id.Date);
        date = c.get(Calendar.DAY_OF_MONTH);
        bill = (ImageView) rootView.findViewById(R.id.bill);
        all = (ImageView) rootView.findViewById(R.id.all);
        event = (ImageView) rootView.findViewById(R.id.event);
        medicine = (ImageView) rootView.findViewById(R.id.medi);
        appoint = (ImageView) rootView.findViewById(R.id.appoint);
        groceries = (ImageView) rootView.findViewById(R.id.grocery);
        wake = (ImageView) rootView.findViewById(R.id.wake);
        day = c.get(Calendar.DAY_OF_WEEK);

        String da = "";

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
        String dat = date.toString();
        Day.setText(da);
        Date.setText(dat);
        final DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        btn.setOnClickListener(new View.OnClickListener() {
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
       /* groceries.setOnClickListener(new View.OnClickListener()
                               {
                                   @Override
                                   public void onClick(View v) {
                                       Intent i = new Intent(getActivity(), Groceries.class);
                                       startActivity(i);
                                   }
                               }

        );*/

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

        appoint.setOnClickListener(new View.OnClickListener()

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
                Log.e("siddesh", "siddesh");
            }
        });

        dialog.show();
        // it show the dialog box
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

    @Override
    public void onDestroy() {
        Log.e("on", "destroy");
        new CallSyncData(getActivity()).stopDownloading();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        Log.e("on", "stop");
        new CallSyncData(getActivity()).stopDownloading();
        super.onStop();
    }
}