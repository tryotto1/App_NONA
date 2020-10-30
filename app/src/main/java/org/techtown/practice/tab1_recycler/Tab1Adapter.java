package org.techtown.practice.tab1_recycler;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.practice.ExtraTabs.ChatActivity;
import org.techtown.practice.R;

import java.util.ArrayList;

public class Tab1Adapter extends RecyclerView.Adapter<Tab1Adapter.CustomViewHolder> {

    private ArrayList<Tab1Data> arrayList; // 서버에서 받는 정보

    public Tab1Adapter(ArrayList<Tab1Data> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public Tab1Adapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_poem, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final Tab1Adapter.CustomViewHolder holder, int position) {
        holder.tv_title.setText(arrayList.get(position).getTitle());
        holder.tv_content.setText(arrayList.get(position).getContent());
        holder.tv_writer.setText(arrayList.get(position).getWriter());
        holder.tv_date.setText(arrayList.get(position).getDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ChatActivity.class);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    public void remove(int position) {
        try {
            arrayList.remove(position);
            notifyItemRemoved(position); // 새로고침 해줌
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }



    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView tv_title, tv_content, tv_writer, tv_date;
        protected LinearLayout layout;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            this.tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            this.tv_writer = (TextView) itemView.findViewById(R.id.tv_writer);
            this.tv_date = (TextView) itemView.findViewById(R.id.tv_date);
        }
    }
}
