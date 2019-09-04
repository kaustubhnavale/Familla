package familla.mipl.familla.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import familla.mipl.familla.R;

public class ShowContact extends AppCompatActivity {

        Cursor c_user;
        TextView contactsDisplay;
        Button pickContacts;


        final int CONTACT_PICK_REQUEST = 1000;
    ListView lv;
    ArrayList<String> contact_list=new ArrayList<String>();
    DatabaseHandler handler;
    SQLiteDatabase db;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_show_contact);
            Typeface tf = Typeface.createFromAsset(getAssets(),"bariol.ttf");
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

            lv=(ListView) findViewById(R.id.contactlist);

            lv.setAdapter(arrayAdapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    String selectedFromList = (lv.getItemAtPosition(position).toString());
                   // Toast.makeText(getApplicationContext(),selectedFromList,Toast.LENGTH_LONG).show();

                    String[] result = selectedFromList.split(":");
                    for (String s : result) {

                        s=result[1];

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+s));
                        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(callIntent);

                }

                }
            });

            pickContacts = (Button) findViewById(R.id.btn_pick_contacts);
            pickContacts.setTypeface(tf, Typeface.BOLD);
            pickContacts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentContactPick = new Intent(ShowContact.this, EmergancyContact.class);
                    ShowContact.this.startActivityForResult(intentContactPick, CONTACT_PICK_REQUEST);


                }
            });

        }


        @Override
        public void onActivityResult(int requestCode,int resultCode,Intent data){
            super.onActivityResult(requestCode, resultCode, data);

            if(requestCode == CONTACT_PICK_REQUEST && resultCode == RESULT_OK){

                ArrayList<Contact> selectedContacts = data.getParcelableArrayListExtra("SelectedContacts");

                String display="";


                for(int i=0;i<selectedContacts.size();i++){

                    display += (i+1)+". "+selectedContacts.get(i).toString()+"\n";
                    ContentValues cv= new ContentValues();
                    cv.put(DatabaseHandler.TAG_PHONENUMBER,selectedContacts.get(i).toString());
                    db.insert(DatabaseHandler.TABLE_CONTACT,null,cv);

                    contact_list.add(selectedContacts.get(i).toString());


                }


            }

        }


    }
