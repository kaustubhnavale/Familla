package familla.mipl.familla.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import familla.mipl.familla.R;

public class Clock extends Activity {

    // Creating 5 buttons, Each button for each theme.
    Button Theme1, Theme2, Theme3, Theme4, Theme5;

    //Creating TextView to display selected time.
    static TextView DisplayTime;

    //Creating calendar.
    Calendar calendar;

    static int Chour, Cminute;

    //Creating 5 TimePickerDialog, each one for different theme.
    static TimePickerDialog timepickerdialog1, timepickerdialog2, timepickerdialog3,
            timepickerdialog4,
            timepickerdialog5;

    DialogFragment dialogfragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        Theme1 = (Button) findViewById(R.id.button1);
        Theme2 = (Button) findViewById(R.id.button2);
        Theme3 = (Button) findViewById(R.id.button3);
        Theme4 = (Button) findViewById(R.id.button4);
        Theme5 = (Button) findViewById(R.id.button5);

        DisplayTime = (TextView) findViewById(R.id.textView1);

        calendar = Calendar.getInstance();
        Chour = calendar.get(Calendar.HOUR_OF_DAY);
        Cminute = calendar.get(Calendar.MINUTE);

        Theme1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                //Creating object of TimePickerTheme1class using DialogFragment.
                dialogfragment = new TimePickerTheme1class();
                dialogfragment.show(getFragmentManager(), "Time Picker with Theme 1");
            }
        });

        Theme2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                //Creating object of TimePickerTheme2class using DialogFragment.
                dialogfragment = new TimePickerTheme2class();
                dialogfragment.show(getFragmentManager(), "Time Picker with Theme 2");
            }
        });

        Theme3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                //Creating object of TimePickerTheme3class using DialogFragment.
                dialogfragment = new TimePickerTheme3class();
                dialogfragment.show(getFragmentManager(), "Time Picker with Theme 3");
            }
        });

        Theme4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                //Creating object of TimePickerTheme4class using DialogFragment.
                dialogfragment = new TimePickerTheme4class();
                dialogfragment.show(getFragmentManager(), "Time Picker with Theme 4");
            }
        });

        Theme5.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                //Creating object of TimePickerTheme5class using DialogFragment.
                dialogfragment = new TimePickerTheme5class();
                dialogfragment.show(getFragmentManager(), "Time Picker with Theme 5");
            }
        });
    }

    @SuppressLint("validFragment")
    public static class TimePickerTheme1class extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            timepickerdialog1 = new TimePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_DARK, this, Chour, Cminute, false);

            return timepickerdialog1;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            DisplayTime.setText(hourOfDay + ":" + minute);
        }
    }

    @SuppressLint("validFragment")
    public class TimePickerTheme2class extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            timepickerdialog1 = new TimePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, this, Chour, Cminute, false);

            return timepickerdialog1;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            DisplayTime.setText(hourOfDay + ":" + minute);
        }
    }

    @SuppressLint("validFragment")
    public class TimePickerTheme3class extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            timepickerdialog1 = new TimePickerDialog(getActivity(),
                    AlertDialog.THEME_HOLO_DARK, this, Chour, Cminute, false);

            return timepickerdialog1;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            DisplayTime.setText(hourOfDay + ":" + minute);
        }
    }

    @SuppressLint("validFragment")
    public class TimePickerTheme4class extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            timepickerdialog1 = new TimePickerDialog(getActivity(),
                    AlertDialog.THEME_HOLO_LIGHT, this, Chour, Cminute, false);

            return timepickerdialog1;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            DisplayTime.setText(hourOfDay + ":" + minute);
        }
    }

    @SuppressLint("validFragment")
    public class TimePickerTheme5class extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            timepickerdialog1 = new TimePickerDialog(getActivity(),
                    AlertDialog.THEME_TRADITIONAL, this, Chour, Cminute, false);

            return timepickerdialog1;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            DisplayTime.setText(hourOfDay + ":" + minute);
        }
    }
}