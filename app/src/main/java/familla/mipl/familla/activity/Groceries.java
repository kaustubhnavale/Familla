package familla.mipl.familla.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import familla.mipl.familla.R;

public class Groceries extends AppCompatActivity {
    Button Addmore;
    EditText name;
    String repeatstatus, selectdate, selecttime;
    DatabaseHandler handler;
    SQLiteDatabase db;
    StringBuffer sb = new StringBuffer();
    Cursor c;
    public static int[] chkState;
    Cursor c_user;
    ListView list1;
    private Toolbar mToolbar;
    SyncData syncnow;
    Cursor cursor;
    public static final String ActicityName = "Add groceries";
    CheckBox checkBox;
    SparseBooleanArray checked;
    TextView tvBody;
    StringBuffer s1 = new StringBuffer();
    ArrayList<String> gro_list = new ArrayList<String>();
    TextView textnext;
    ListView lv;
    RelativeLayout gro_parent;
    LinearLayout header, edit1;
    String selectedFromList;
    Typeface tf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groceries);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Select grocery item");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tf = Typeface.createFromAsset(getAssets(), "bariol.ttf");

        mToolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorgroceri)));

        // edit1 = (LinearLayout)findViewById(R.id.edit1);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorgroceri));
            //  window.setNavigationBarColor(this.getResources().getColor(R.color.colorbilldata));
        }

          /*  TextView tb=(TextView)findViewById(R.id.glist);
            TextView tb1=(TextView)findViewById(R.id.test);*/

        handler = new DatabaseHandler(getApplicationContext());
        db = handler.getReadableDatabase();

        String quer = "select * from groceries";
        c_user = db.rawQuery(quer, null);

          /*  if (c_user.getCount() == 0) {

               tb.setText("Create Your Grocery List :");
                tb1.setText("ADD List :");
                tb1.setVisibility(View.VISIBLE);
            }
            else
            {
                tb1.setText("ADD List :");
                tb1.setVisibility(View.VISIBLE);
                tb.setText("Select Your Grocery Items :");
            }*/
        c_user.moveToFirst();
        while (!c_user.isAfterLast()) {
            gro_list.add(c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_GROCERIESNAME)));
            c_user.moveToNext();
        }

        textnext = (TextView) findViewById(R.id.textnext);
        textnext.setTypeface(tf, Typeface.NORMAL);
        lv = (ListView) findViewById(R.id.grocery_list);

        Button btn = (Button) findViewById(R.id.addlist);

        btn.setTypeface(tf, Typeface.NORMAL);
        name = (EditText) findViewById(R.id.name);

            /*adapter = new TodoCursorAdapter(getApplicationContext(), c_user);

            lv.setAdapter(adapter);
            */

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_multiple_choice, gro_list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextSize(22);
                text.setTypeface(tf);
                return view;
            }
        };

        lv.setAdapter(arrayAdapter);

        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        checked = lv.getCheckedItemPositions();

        btn.setOnClickListener(new View.OnClickListener() {

                                   @Override
                                   public void onClick(View v) {

                                       if (name.getText().toString().trim().equals("")) {
                                           Toast.makeText(getApplicationContext(), "Please add item", Toast.LENGTH_LONG).show();
                                           Log.e("blank", "");
                                       } else {
                                           String input = name.getText().toString();
                                           ContentValues cv = new ContentValues();
                                           cv.put(DatabaseHandler.TAG_GROCERIESNAME, input);
                                           db.insert(DatabaseHandler.TABLE_Groceries, null, cv);
                                           gro_list.add(input);
                                              /* InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                               imm.hideSoftInputFromWindow(name.getWindowToken(),
                                                       InputMethodManager.RESULT_UNCHANGED_SHOWN);*/
                                           name.setText("");

                                           lv.setItemChecked((lv.getAdapter().getCount() - 1), true);
                                           lv.setSelection(lv.getAdapter().getCount() - 1);
                                       }
                                          /* for (int i = 0; i < lv.getAdapter().getCount(); i++) {

                                               if (checked.get(i))
                                               {
                                                   Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_LONG).show();
                                                   *//*s1.append(checkBox.getText());*//*
                                                   // Do something
                                               }
                                           }*/
                                   }
                               }
        );

        name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((actionId == EditorInfo.IME_ACTION_NEXT) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {
                    //Toast.makeText(getApplicationContext(),"Hi",Toast.LENGTH_SHORT).show();
                    if (name.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "Please add item", Toast.LENGTH_LONG).show();
                        Log.e("blank", "");
                    } else {
                        String input = name.getText().toString();
                        ContentValues cv = new ContentValues();
                        cv.put(DatabaseHandler.TAG_GROCERIESNAME, input);
                        db.insert(DatabaseHandler.TABLE_Groceries, null, cv);
                        gro_list.add(input);
                        name.setText("");
                        lv.setItemChecked((lv.getAdapter().getCount() - 1), true);
                        lv.setSelection(lv.getAdapter().getCount() - 1);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        textnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!name.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Click Add Button", Toast.LENGTH_LONG).show();
                    Log.e("blank", "");
                } else {
                    s1.delete(0, s1.length());
                    if (checked.size() == 0) {
                        Toast.makeText(getApplicationContext(), "Please select item from list", Toast.LENGTH_SHORT).show();
                    } else {

                        for (int i = 0; i < lv.getAdapter().getCount(); i++) {

                            if (checked.get(i) == true) {
                                Log.e("selected_item", lv.getItemAtPosition(i).toString());
                                //Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_LONG).show();
                                s1.append(lv.getItemAtPosition(i).toString());
                                s1.append(":");
                                // Do something
                            }
                        }
                        if (s1.length() == 0) {
                            Toast.makeText(getApplicationContext(), "Please select item from list", Toast.LENGTH_SHORT).show();
                        } else {
                            String selected_items = s1.toString();
                            Log.e("selected_items", selected_items);
                            Intent i = new Intent(Groceries.this, AddGroceries.class);
                            i.putExtra("selected_item", selected_items);
                            startActivity(i);
                        }
                    }
                }
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {

                // Toast.makeText(getApplicationContext(),"Hi",Toast.LENGTH_SHORT).show();
                selectedFromList = (lv.getItemAtPosition(pos).toString());
                /*new AlertDialog.Builder(Groceries.this)
                        .setTitle("Delete Grocery Product")
                        .setMessage("Are you sure you want to Delete Grocery Product?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                // continue with delete
                                //  db.delete(DatabaseHandler.TABLE_COMMONTASK,"1", null);

                                db.delete(DatabaseHandler.TABLE_Groceries, "groceries_item='" + selectedFromList + "'", null);

                                //db1.delete(Database.TABLE_CHAT,"1", null);
                                Intent intent = new Intent(Groceries.this, Groceries.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                                startActivity(intent);
                                finish();
                                dialog.dismiss();

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();*/

                new AlertDialog.Builder(Groceries.this)
                        .setTitle("Grocery Product List")
//                        .setMessage("Are you sure you want to Delete Grocery Product?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                // continue with delete
                                //  db.delete(DatabaseHandler.TABLE_COMMONTASK,"1", null);

                                db.delete(DatabaseHandler.TABLE_Groceries, "groceries_item='" + selectedFromList + "'", null);

                                //db1.delete(Database.TABLE_CHAT,"1", null);
                                Intent intent = new Intent(Groceries.this, Groceries.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                                startActivity(intent);
                                finish();
                                dialog.dismiss();

                            }
                        })
                        .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
//
                                AlertDialog.Builder alert = new AlertDialog.Builder(Groceries.this);
                                alert.setMessage("Edit product name.");
                                final EditText mobileNumber = new EditText(Groceries.this);
                                mobileNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                                mobileNumber.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                                mobileNumber.setText(selectedFromList);
                                alert.setView(mobileNumber);

                                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        String value = mobileNumber.getText().toString();
                                        Log.d("Mobile number== ", value);

                                        if (!mobileNumber.getText().toString().equals("")) {

                                            try {
                                                /*ContentValues cv_logout = new ContentValues();
                                                cv_logout.put(DatabaseHandler.TAG_GROCERIESNAME, value);
                                                long change = db.update("groceries", cv_logout, "groceries_item =" + selectedFromList, null);*/

                                                String strSQL = "UPDATE groceries SET groceries_item = '" + value + "' WHERE groceries_item = '"+ selectedFromList + "'";
                                                db.execSQL(strSQL);

                                                Intent intent = new Intent(Groceries.this, Groceries.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                                                startActivity(intent);
                                                finish();
                                                dialog.dismiss();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        } else {

                                        }
                                    }
                                });

                                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
                                    }
                                });
                                alert.show();
                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;
            }
        });
    }

    /* public class TodoCursorAdapter extends CursorAdapter {
         public TodoCursorAdapter(Context context, Cursor cursor) {
             super(context, cursor, 0);
         }

         // The newView method is used to inflate a new view and return it,
         // you don't bind any data to the view at this point.
         @Override
         public View newView(Context context, Cursor cursor, ViewGroup parent) {
             return LayoutInflater.from(context).inflate(R.layout.groceries_list, parent, false);
         }

         // The bindView method is used to bind all data to a given view
         // such as setting the text on a TextView.
         @Override
         public void bindView(View view, Context context, Cursor cursor) {
             // Find fields to populate in inflated template
             tvBody = (TextView) view.findViewById(R.id.taskname);
             checkBox = (CheckBox) view.findViewById(R.id.checkbox);

             // Extract properties from cursor
             String body = (cursor.getString(cursor.getColumnIndex(DatabaseHandler.TAG_GROCERIESNAME)));

             // Populate fields with extracted properties
             tvBody.setText(body);
         }

         public void onBackPressed() {

             Intent i = new Intent(Groceries.this, MainActivity.class);
             i.addCategory(Intent.CATEGORY_HOME);
             i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             startActivity(i);
         }
     }
*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.action_home) {
            Intent i = new Intent(Groceries.this, MainActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_home);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        gro_list.clear();
        Intent i = new Intent(Groceries.this, Show_groceries.class);
        i.putExtra("Activity", ActicityName);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        super.onBackPressed();
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }
}