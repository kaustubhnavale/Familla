package familla.mipl.familla.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import familla.mipl.familla.R;


public class RecyclerView_Adapter extends
        RecyclerView.Adapter<RecyclerViewHolder> {// Recyclerview will extend to
    // recyclerview adapter
    private ArrayList<Data_Model> arrayList;
    private Context context;
    Typeface tf;
    RecyclerViewHolder mainHolder;

    public RecyclerView_Adapter(Context context, ArrayList<Data_Model> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        final Data_Model model = arrayList.get(position);

        mainHolder = holder;// holder

        //  Bitmap image = BitmapFactory.decodeResource(context.getResources(),model.getImage());
        // This will convert drawbale image into
        // bitmap

        // setting title
        mainHolder.id.setText(model.getId());

        mainHolder.title.setText(model.getTitle().toUpperCase());
        tf = Typeface.createFromAsset(context.getAssets(), "bariol.ttf");
        mainHolder.title.setTypeface(tf, Typeface.BOLD);
        if (model.getTitle().equals("Add Member")) {
            mainHolder.count.setVisibility(View.GONE);
        } else {
            mainHolder.count.setText(model.getCount());
        }
        mainHolder.imageview.setBackgroundResource(model.getImage());

        mainHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {

                // Toast.makeText(context, String.valueOf(pos),Toast.LENGTH_SHORT).show();
                //Toast.makeText(context, model.getTitle(),Toast.LENGTH_SHORT).show();
                String id = model.getId();

                if (model.getTitle().equals("Add Member")) {
                    Intent intent3 = new Intent(context, AddMember.class);
                    context.startActivity(intent3);
                } else {
                    Intent intent3 = new Intent(context, MemberDetails.class);
                    intent3.putExtra("member_id", id);
                    context.startActivity(intent3);
                }
            }
        });
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
                R.layout.item_row, viewGroup, false);
        RecyclerViewHolder listHolder = new RecyclerViewHolder(mainGroup);
        return listHolder;
    }
}