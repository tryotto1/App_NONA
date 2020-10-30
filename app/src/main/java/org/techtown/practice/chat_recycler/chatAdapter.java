package org.techtown.practice.chat_recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.techtown.practice.R;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class chatAdapter extends RecyclerView.Adapter<chatAdapter.MyViewHolder> {
    private ArrayList<chatData> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public MyViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.tvChat);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mDataset.get(position).name.equals("ksy"))
            return 1;
        else
            return 0;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public chatAdapter(ArrayList<chatData> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public chatAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View v;
        // ksy 가 쓴거
        if(viewType==1){
            // create a new view
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_right_view, parent, false);
        }else{
            // create a new view
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_left_view, parent, false);
        }

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.setText(mDataset.get(position).getMsg());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

