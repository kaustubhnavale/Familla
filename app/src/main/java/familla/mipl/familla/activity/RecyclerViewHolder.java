package familla.mipl.familla.activity;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import familla.mipl.familla.R;

/**
 * Created by SONU on 25/09/15.
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    // View holder for gridview recycler view as we used in listview
    public TextView title;
    public TextView count;
    public TextView id;

    public RelativeLayout imageview;
    ItemClickListener itemClickListener;

    public RecyclerViewHolder(View view) {
        super(view);
        // Find all views ids

        this.title = (TextView) view
                .findViewById(R.id.title);
        this.id = (TextView) view
                .findViewById(R.id.id);
        this.count = (TextView) view
                .findViewById(R.id.count);
        this.imageview = (RelativeLayout) view.findViewById(R.id.image);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        this.itemClickListener.onItemClick(this.getLayoutPosition());
    }
}