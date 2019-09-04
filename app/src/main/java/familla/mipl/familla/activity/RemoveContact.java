package familla.mipl.familla.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import familla.mipl.familla.R;

public class RemoveContact extends AppCompatActivity {
ListView lv;
Cursor c_user;
    ArrayList<String> contact_list=new ArrayList<String>();
    DatabaseHandler handler;
    private Toolbar mToolbar;
    SQLiteDatabase db;
    String selectedFromList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_contact);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(" R E M O V E  C O N T A C T ");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        lv=(ListView)findViewById(R.id.Contact);
        handler = new DatabaseHandler(getApplicationContext());
        db = handler.getReadableDatabase();

        String quer = "select * from contact";

        c_user = db.rawQuery(quer, null);
        c_user.moveToFirst();
        while (!c_user.isAfterLast())
        {
            contact_list.add(c_user.getString(c_user.getColumnIndex(DatabaseHandler.TAG_PHONENUMBER)));
            c_user.moveToNext();

        }
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android. R.layout.simple_list_item_1 ,contact_list);

        lv=(ListView) findViewById(R.id.Contact);

        lv.setAdapter(arrayAdapter);



        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                selectedFromList = (lv.getItemAtPosition(position).toString());
                new AlertDialog.Builder(RemoveContact.this)
                        .setTitle("Remove Contact")
                        .setMessage("Are you sure you want to Remove Contact?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete

                                // Toast.makeText(getApplicationContext(),selectedFromList,Toast.LENGTH_LONG).show();
                                ContentValues cv=new ContentValues();
                                db.delete(DatabaseHandler.TABLE_CONTACT,"contactno='"+selectedFromList+"'",null);
                                Intent intent = new Intent(RemoveContact.this, RemoveContact.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                                startActivity(intent);
                                finish();
                                dialog.dismiss();

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
                                  }
        );




    }
}
