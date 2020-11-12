package org.techtown.practice.recycler_Follwers;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.practice.SubTab_Drawer.MyWritingsActivity;
import org.techtown.practice.R;

import java.util.ArrayList;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.CustomViewHolder> {

    private ArrayList<FollowersData> arrayList; // 서버에서 받는 정보

    public FollowersAdapter(ArrayList<FollowersData> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public FollowersAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_followers, parent, false);
        FollowersAdapter.CustomViewHolder holder = new FollowersAdapter.CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final FollowersAdapter.CustomViewHolder holder, int position) {
        holder.tv_writer.setText(arrayList.get(position).getWriter());
        holder.tv_email.setText(arrayList.get(position).getId());


//        holder.tv_content.setText(arrayList.get(position).getId());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                FollowersData clicked = arrayList.get(position);

                String writer = clicked.getId();

                Intent intent = new Intent(view.getContext(), MyWritingsActivity.class);
                intent.putExtra("writer", writer);
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
        protected TextView tv_title, tv_content, tv_writer, tv_date, tv_email;
        protected LinearLayout layout;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            this.tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            this.tv_writer = (TextView) itemView.findViewById(R.id.tv_writer);
            this.tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            this.tv_email = (TextView) itemView.findViewById(R.id.tv_email);
        }
    }
}
