package familla.mipl.familla.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import familla.mipl.familla.R;

public class EventSpinner extends BaseAdapter {
    Context context;

    String[] event;
    LayoutInflater inflter;

    public EventSpinner(Context applicationContext, String[] event) {
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
        view = inflter.inflate(R.layout.event_spinner, null);
        TextView names = (TextView) view.findViewById(R.id.textView);

        names.setText(event[i]);
        return view;
    }
}