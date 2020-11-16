package org.techtown.practice.recycler_tab1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.techtown.practice.SubTab_Tab1.ChatActivity;
import org.techtown.practice.R;
import org.techtown.practice.SubTab_Tab1.ShowWrittenActivity;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class Tab1Adapter extends RecyclerView.Adapter<Tab1Adapter.CustomViewHolder> {
    // shared preference를 쓰기 위한 설정
    Context context;

    private ArrayList<Tab1Data> arrayList; // 서버에서 받는 정보

    public Tab1Adapter(ArrayList<Tab1Data> arrayList, Context context) {
        // context를 전달받아야, shared preference를 쓸 수 있음
        this.context = context;
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
    public void onBindViewHolder(@NonNull final Tab1Adapter.CustomViewHolder holder, final int position) {
        holder.tv_title.setText(arrayList.get(position).getTitle());

        // 각 게시물을 클릭할 경우, 채팅을 시작한다
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 몇 번째 게시물인지 chatActivity에 전달한다
                SharedPreferences pref = context.getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("idx_writing", arrayList.get(position).getIndex());
                editor.putString("writer", arrayList.get(position).getWriter());
                editor.putString("content_writing", arrayList.get(position).getContent());
                editor.putString("title_writing", arrayList.get(position).getTitle());
                editor.commit();

                // 게시물을 자세히 확인한다
                Intent intent = new Intent(view.getContext(), ShowWrittenActivity.class);
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

        protected TextView tv_title, tv_content, borrow_possible, borrow_impossible;
        protected LinearLayout layout;
        protected ImageView device_img;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_title = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }
}
