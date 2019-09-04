package familla.mipl.familla.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import familla.mipl.familla.R;

/**
 * Created by android on 11/22/2016.
 */

public class Custom_list_adapter extends BaseAdapter {

    Context context;
    Typeface tf ;
    String[] event;
    LayoutInflater inflter;

    public Custom_list_adapter(Context applicationContext, String[] event) {
        this.context = applicationContext;

        this.event = event;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return  event.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_list, null);
        TextView names = (TextView) view.findViewById(R.id.taskname);
        tf= Typeface.createFromAsset(context.getAssets(),"bariol.ttf");
        names.setTypeface(tf, Typeface.BOLD);
        names.setText(event[i].toUpperCase());
        return view;
    }
}