package familla.mipl.familla.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;

import familla.mipl.familla.R;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

import static android.content.Context.MODE_PRIVATE;

public class GridHome extends Fragment {
    public static String status;
    Cursor c1;
    Cursor c_user1;
    Cursor c_user;
    DatabaseHandler handler;
    SQLiteDatabase db;
    String quer, role;
    ConnectionDetector cd;
    int height, width, firstx, firsty;
    ImageView bill, all;
    ImageView img;
    Typeface tf;

    private static final String SHOWCASE_ID = "simple example";

    public GridHome() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tf = Typeface.createFromAsset(getActivity().getAssets(), "bariol.ttf");

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        handler = new DatabaseHandler(getActivity().getApplicationContext());
        db = handler.getReadableDatabase();

        Cursor c = db.rawQuery("select * from userdetails where _id=1 ", null);
        c.moveToFirst();

        role = c.getString(c.getColumnIndex(DatabaseHandler.TAG_Role));

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;

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
        View rootView = inflater.inflate(R.layout.fragment_grid_home, container, false);
        DisplayMetrics displaymetrics = new DisplayMetrics();

        //top_advt.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        Button man = (Button) rootView.findViewById(R.id.man);
        man.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       Intent i = new Intent(getActivity(), showmember.class);
                                       startActivity(i);
                                   }
                               }
        );

        // man.setBackgroundResource(R.drawable.man);
        if (role.equals("Father")) {
            man.setBackgroundResource(R.drawable.fatherbar);
        } else if (role.equals("Mother")) {
            man.setBackgroundResource(R.drawable.motherbar);
        } else if (role.equals("Husband")) {
            man.setBackgroundResource(R.drawable.man);
        } else if (role.equals("Wife")) {
            man.setBackgroundResource(R.drawable.wifebar);
        } else if (role.equals("Son")) {
            man.setBackgroundResource(R.drawable.boybar);
        } else if (role.equals("Boy")) {
            man.setBackgroundResource(R.drawable.boybar);
        } else if (role.equals("Girl")) {
            man.setBackgroundResource(R.drawable.girlbar);
        } else if (role.equals("Daughter")) {
            man.setBackgroundResource(R.drawable.girlbar);
        } else if (role.equals("Grandfather")) {
            man.setBackgroundResource(R.drawable.grandfatherbar);
        } else if (role.equals("Grandmother")) {
            man.setBackgroundResource(R.drawable.grandmotherbar);
        } else {
            man.setBackgroundResource(R.drawable.man);
        }
        ImageView groceries = (ImageView) rootView.findViewById(R.id.first);
        AbsoluteLayout.LayoutParams absParams1 =
                (AbsoluteLayout.LayoutParams) groceries.getLayoutParams();
        absParams1.x = width / 12;
        absParams1.y = 2 * height / 40;
        absParams1.width = 3 * width / 8;
        absParams1.height = absParams1.width;


        ImageView event = (ImageView) rootView.findViewById(R.id.sec);
        AbsoluteLayout.LayoutParams absParams2 =
                (AbsoluteLayout.LayoutParams) event.getLayoutParams();
        absParams2.x = 8 * width / 15;
        absParams2.y = 2 * height / 40;
        absParams2.width = 3 * width / 8;
        absParams2.height = absParams2.width;


        all = (ImageView) rootView.findViewById(R.id.third);
        AbsoluteLayout.LayoutParams absParams3 =
                (AbsoluteLayout.LayoutParams) all.getLayoutParams();
        absParams3.x = 4 * width / 13;
        absParams3.y = 3 * height / 16;
        absParams3.width = 3 * width / 8;
        absParams3.height = absParams3.width;


        ImageView appointment = (ImageView) rootView.findViewById(R.id.fourth);
        AbsoluteLayout.LayoutParams absParams4 =
                (AbsoluteLayout.LayoutParams) appointment.getLayoutParams();
        absParams4.x = width / 12;
        absParams4.y = 6 * height / 18;
        absParams4.width = 3 * width / 8;
        absParams4.height = absParams4.width;

        bill = (ImageView) rootView.findViewById(R.id.fifth);
        AbsoluteLayout.LayoutParams absParams5 =
                (AbsoluteLayout.LayoutParams) bill.getLayoutParams();
        absParams5.x = 8 * width / 15;
        absParams5.y = 6 * height / 18;
        absParams5.width = 3 * width / 8;
        absParams5.height = absParams5.width;


        ImageView add = (ImageView) rootView.findViewById(R.id.sixth);
        AbsoluteLayout.LayoutParams absParams6 =
                (AbsoluteLayout.LayoutParams) add.getLayoutParams();
        absParams6.x = 4 * width / 13;
        absParams6.y = 10 * height / 21;
        absParams6.width = 3 * width / 8;
        absParams6.height = absParams6.width;


        ImageView wake = (ImageView) rootView.findViewById(R.id.seventh);
        AbsoluteLayout.LayoutParams absParams7 =
                (AbsoluteLayout.LayoutParams) wake.getLayoutParams();
        absParams7.x = width / 12;
        absParams7.y = 11 * height / 18;
        absParams7.width = 3 * width / 8;
        absParams7.height = absParams7.width;


        ImageView medicine = (ImageView) rootView.findViewById(R.id.eight);
        AbsoluteLayout.LayoutParams absParams8 =
                (AbsoluteLayout.LayoutParams) medicine.getLayoutParams();
        absParams8.x = 8 * width / 15;
        absParams8.y = 11 * height / 18;
        absParams8.width = 3 * width / 8;
        absParams8.height = absParams8.width;


        ImageView left = (ImageView) rootView.findViewById(R.id.nine);
        AbsoluteLayout.LayoutParams absParams9 =
                (AbsoluteLayout.LayoutParams) left.getLayoutParams();
        absParams9.x = 0;
        absParams9.y = 3 * height / 15;
        absParams9.width = 4 * width / 20;
        absParams9.height = 6 * width / 17;

        ImageView right = (ImageView) rootView.findViewById(R.id.ten);
        AbsoluteLayout.LayoutParams absParams10 =
                (AbsoluteLayout.LayoutParams) right.getLayoutParams();
        absParams10.x = 17 * width / 21;
        absParams10.y = 3 * height / 15;
        absParams10.width = 4 * width / 19;
        absParams10.height = 6 * width / 16;


        ImageView left1 = (ImageView) rootView.findViewById(R.id.ele);
        AbsoluteLayout.LayoutParams absParams11 =
                (AbsoluteLayout.LayoutParams) left1.getLayoutParams();
        absParams11.x = 0;
        absParams11.y = 9 * height / 19;
        absParams11.width = 4 * width / 20;
        absParams11.height = 6 * width / 17;


        ImageView right1 = (ImageView) rootView.findViewById(R.id.twelve);
        AbsoluteLayout.LayoutParams absParams12 =
                (AbsoluteLayout.LayoutParams) right1.getLayoutParams();
        absParams12.x = 17 * width / 21;
        absParams12.y = 9 * height / 19;
        absParams12.width = 4 * width / 19;
        absParams12.height = 6 * width / 16;


        final DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        ImageView toggle = (ImageView) rootView.findViewById(R.id.toggle);
        toggle.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          drawer.openDrawer(Gravity.RIGHT);
                                      }
                                  }
        );

        event.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         Boolean isFirstRuntest2 = getActivity().getSharedPreferences("PREFERENCE3", MODE_PRIVATE)
                                                 .getBoolean("isFirstRuntest2", true);
                                         if (isFirstRuntest2) {
                                             //show start activity
                                             startActivity(new Intent(getActivity(), WelcomeEvent.class)
                                             );
                                             getActivity().getSharedPreferences("PREFERENCE3", MODE_PRIVATE).edit()
                                                     .putBoolean("isFirstRuntest2", false).commit();
                                         } else {
                                             String s = "Event";
                                             String str = "select * from commontask where commontask_type='" + s + "' and commontask_status ='0' order by commontask_createddatetime desc  ";

                                             c1 = db.rawQuery(str, null);
                                             c1.moveToFirst();

                                             if (c1.getCount() == 0) {
                                                 Intent i = new Intent(getActivity(), AddEvent.class);
                                                 i.addCategory(Intent.CATEGORY_HOME);
                                                 i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                 startActivity(i);
                                             } else {
                                                 Intent i = new Intent(getActivity(), Event.class);
                                                 i.addCategory(Intent.CATEGORY_HOME);
                                                 i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                 startActivity(i);
                                             }
                                         }
                                     }
                                 }
        );

        groceries.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             Boolean isFirstRuntest = getActivity().getSharedPreferences("PREFERENCE2", MODE_PRIVATE)
                                                     .getBoolean("isFirstRuntest1", true);
                                             if (isFirstRuntest) {
                                                 //show start activity
                                                 startActivity(new Intent(getActivity(), WelcomeGroce.class)
                                                 );
                                                 getActivity().getSharedPreferences("PREFERENCE2", MODE_PRIVATE).edit()
                                                         .putBoolean("isFirstRuntest1", false).commit();
                                             } else {
                                                 String s = "Groceries";
                                                 String str = "select * from commontask where commontask_type='" + s + "' and commontask_status ='0' order by commontask_createddatetime desc  ";

                                                 c1 = db.rawQuery(str, null);
                                                 c1.moveToFirst();

                                                 if (c1.getCount() == 0) {
                                                     Intent i = new Intent(getActivity(), Groceries.class);
                                                     i.addCategory(Intent.CATEGORY_HOME);
                                                     i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                     startActivity(i);
                                                 } else {
                                                     Intent i = new Intent(getActivity(), Show_groceries.class);
                                                     i.addCategory(Intent.CATEGORY_HOME);
                                                     i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                     startActivity(i);
                                                 }
                                             }
                                         }
                                     }
        );

        add.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       Intent i = new Intent(getActivity(), Add_memberbycontact.class);
                                       startActivity(i);
                                   }
                               }
        );

        wake.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        // make first time launch TRUE
                                        Boolean isFirstRun = getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                                                .getBoolean("isFirstRun", true);
                                        if (isFirstRun) {
                                            //show start activity
                                            startActivity(new Intent(getActivity(), WelcomeActivity.class)
                                            );
                                            getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                                    .putBoolean("isFirstRun", false).commit();
                                        } else {

                                            String s = "Alarm";
                                            String str = "select * from commontask where commontask_type='" + s + "' and commontask_status ='0' order by commontask_createddatetime desc  ";

                                            c1 = db.rawQuery(str, null);
                                            c1.moveToFirst();

                                            if (c1.getCount() == 0) {
                                                Intent i = new Intent(getActivity(), Wake_up.class);
                                                i.addCategory(Intent.CATEGORY_HOME);
                                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(i);
                                            } else {
                                                Intent i = new Intent(getActivity(), Wake.class);
                                                i.addCategory(Intent.CATEGORY_HOME);
                                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(i);
                                            }
                                        }
                                    }
                                }
        );

        appointment.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               Boolean isFirstRuntest = getActivity().getSharedPreferences("PREFERENCE4", MODE_PRIVATE)
                                                       .getBoolean("isFirstRuntest4", true);
                                               if (isFirstRuntest) {
                                                   //show start activity
                                                   startActivity(new Intent(getActivity(), WelcomeAppointment.class)
                                                   );
                                                   getActivity().getSharedPreferences("PREFERENCE4", MODE_PRIVATE).edit()
                                                           .putBoolean("isFirstRuntest4", false).commit();
                                               } else {
                                                   String s = "Appointment";
                                                   String str = "select * from commontask where commontask_type='" + s + "' and commontask_status ='0' order by commontask_createddatetime desc  ";

                                                   c1 = db.rawQuery(str, null);
                                                   c1.moveToFirst();

                                                   if (c1.getCount() == 0) {
                                                       Intent i = new Intent(getActivity(), AddAppointment.class);
                                                       i.addCategory(Intent.CATEGORY_HOME);
                                                       i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                       startActivity(i);
                                                   } else {
                                                       Intent i = new Intent(getActivity(), Appointment.class);
                                                       i.addCategory(Intent.CATEGORY_HOME);
                                                       i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                       startActivity(i);
                                                   }
                                               }
                                           }
                                       }
        );

        medicine.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Boolean isFirstRuntest = getActivity().getSharedPreferences("PREFERENCE5", MODE_PRIVATE)
                                                    .getBoolean("isFirstRuntest5", true);
                                            if (isFirstRuntest) {
                                                //show start activity
                                                startActivity(new Intent(getActivity(), WelcomeMedic.class)
                                                );
                                                getActivity().getSharedPreferences("PREFERENCE5", MODE_PRIVATE).edit()
                                                        .putBoolean("isFirstRuntest5", false).commit();
                                            } else {
                                                String s = "Medicine";
                                                String str = "select * from commontask where commontask_type='" + s + "' and commontask_status ='0' order by commontask_createddatetime desc  ";

                                                c1 = db.rawQuery(str, null);
                                                c1.moveToFirst();

                                                if (c1.getCount() == 0) {
                                                    Intent i = new Intent(getActivity(), AddMedicines.class);
                                                    i.addCategory(Intent.CATEGORY_HOME);
                                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(i);

                                                } else {
                                                    Intent i = new Intent(getActivity(), Medicine.class);
                                                    i.addCategory(Intent.CATEGORY_HOME);
                                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(i);
                                                }
                                            }
                                        }
                                    }
        );

        bill.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Boolean isFirstRuntest = getActivity().getSharedPreferences("PREFERENCE6", MODE_PRIVATE)
                                                .getBoolean("isFirstRuntest6", true);
                                        if (isFirstRuntest) {
                                            //show start activity
                                            startActivity(new Intent(getActivity(), WelcomeBill.class)
                                            );
                                            getActivity().getSharedPreferences("PREFERENCE6", MODE_PRIVATE).edit()
                                                    .putBoolean("isFirstRuntest6", false).commit();
                                        } else {
                                            String s = "Bill";
                                            String str = "select * from commontask where commontask_type='" + s + "' and commontask_status ='0' order by commontask_createddatetime desc  ";

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
                                }
        );

        all.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       Intent i = new Intent(getActivity(), Todo.class);
                                       startActivity(i);
                                       // MaterialShowcaseView.resetSingleUse(getActivity(), SHOWCASE_ID);
                                   }
                               }
        );

        presentShowcaseView(1000);
        return rootView;
    }

    public void presentShowcaseView(int withDelay) {
        new MaterialShowcaseView.Builder(getActivity())
                .setTarget(all)
                .setTitleText("ToDo list")
                .setDismissTextColor(Color.parseColor("#ffffff"))
                .setContentTextColor(Color.parseColor("#feffff"))
                .setDismissStyle(Typeface.defaultFromStyle(tf.BOLD))
                .setDismissText("GOT IT")
                .setContentText("You can view your all reminder here ")
                .setDelay(withDelay) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse(SHOWCASE_ID) // provide a unique ID used to ensure it is only shown once
//                .useFadeAnimation() // remove comment if you want to use fade animations for Lollipop & up
                .show();
    }
}