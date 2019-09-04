package familla.mipl.familla.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import familla.mipl.familla.R;

public class Groceri_List_dialog extends AppCompatActivity {
    private Toolbar mToolbar;
    String task_id;
    ArrayList<String> member_list = new ArrayList<String>();
    DatabaseHandler handler;
    SQLiteDatabase db, dbwrite;
    Cursor c, c_user2;
    ListView grolist;
    List<String> myList;
    String[] arr;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groceri_list_dialog);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle(" G R O C E R I E S  L I S T ");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorgroceri)));

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorgroceri));
            //  window.setNavigationBarColor(this.getResources().getColor(R.color.colorbilldata));
        }
        Typeface tf = Typeface.createFromAsset(getAssets(), "bariol.ttf");

        handler = new DatabaseHandler(getApplicationContext());

        db = handler.getReadableDatabase();
        Button mark = (Button) findViewById(R.id.mark);

        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();

        task_id = "";
        name = "";
        if (extras != null) {

            task_id = intent.getExtras().getString("Taskid").toString();

            String quer = "select * from commontask where _id= " + task_id + " ";
            Cursor c_user = db.rawQuery(quer, null);
            c_user.moveToFirst();

            String ab = (c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_COMMONTASKNAME)));

            grolist = (ListView) findViewById(R.id.grocery_list_item);
            myList = new ArrayList<String>(Arrays.asList(ab.split(":")));
            arr = myList.toArray(new String[myList.size()]);

            groceri_list_adapter customAdapter1 = new groceri_list_adapter(getApplicationContext(), arr);
            grolist.setAdapter(customAdapter1);
        }

        mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues cv = new ContentValues();
                cv.put(DatabaseHandler.TAG_COMMONTASKSTATUS, 1);
                db.update(DatabaseHandler.TABLE_COMMONTASK, cv, "_id ='" + task_id + "' ", null);
                Toast.makeText(getApplicationContext(), "Task is mark completed", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
    }
}