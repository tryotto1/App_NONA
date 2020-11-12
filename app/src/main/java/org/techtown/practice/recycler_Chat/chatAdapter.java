package org.techtown.practice.recycler_Chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import org.techtown.practice.R;

import java.util.ArrayList;

public class chatAdapter extends RecyclerView.Adapter<chatAdapter.MyViewHolder> {
    // chat activity로부터 전달받을 내용들
    private ArrayList<chatData> mDataset;
    Context context;

    // firebase 유저 참조
    private FirebaseAuth mAuth;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView, email_icon;
        public MyViewHolder(View v) {
            super(v);

            textView = v.findViewById(R.id.tvChat);
            email_icon = v.findViewById(R.id.usr_email);
        }
    }

    @Override
    public int getItemViewType(int position) {
        SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        String my_email = pref.getString("email", " ");

        if(mDataset.get(position).email.equals(my_email)){
            return 1;
        }
        else{
            // 남이 쓴 것은 0을 반환
            return 0;
        }
    }

    // 이 adapter의 생성자
    public chatAdapter(ArrayList<chatData> myDataset, Context context) {
        // context를 전달받아야, shared preference를 쓸 수 있음
        this.context = context;

        // dataset을 받아서 뿌려준다
        mDataset = myDataset;
    }

    @Override
    public chatAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View v;

        // 내가 쓴거
        if(viewType == 1){
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

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textView.setText(mDataset.get(position).getMsg());

        // 내 이메일이 아닌 채팅만 뷰홀더를 따로 추가해준다
        SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        String my_email = pref.getString("email", " ");
        holder.email_icon.setText(mDataset.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}