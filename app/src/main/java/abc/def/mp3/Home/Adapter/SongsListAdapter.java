package abc.def.mp3.Home.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import abc.def.mp3.Home.Modal.SongsDetails;
import abc.def.mp3.R;

public class SongsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SongsDetails> items = new ArrayList<>();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, SongsDetails obj, int position,ImageView ivProfile,TextView name);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public SongsListAdapter(Context context, List<SongsDetails> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView fileName;
        LinearLayout clickMe;
        ImageView ivProfile;


        public OriginalViewHolder(View v) {
            super(v);
            fileName = (TextView) v.findViewById(R.id.name);
            clickMe = (LinearLayout) v.findViewById(R.id.clickMe);
            ivProfile =  v.findViewById(R.id.ivProfile);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lists, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;


            SongsDetails p = items.get(position);

            view.fileName.setText(p.getName());
            view.clickMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, items.get(position), position,view.ivProfile,view.fileName);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}