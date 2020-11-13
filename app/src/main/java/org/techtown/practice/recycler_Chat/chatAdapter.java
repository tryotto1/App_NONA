package org.techtown.practice.recycler_Chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.techtown.practice.R;
import org.techtown.practice.SubTab_Tab1.ProfileActivity;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatAdapter extends RecyclerView.Adapter<chatAdapter.MyViewHolder> {
    // chat activity로부터 전달받을 내용들
    private ArrayList<chatData> mDataset;
    Context context;
    String writer_id;

    // firebase 유저 참조
    private FirebaseAuth mAuth;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView, email_icon;
        public CircleImageView img_profile;
        public MyViewHolder(View v) {
            super(v);

            textView = v.findViewById(R.id.tvChat);
            email_icon = v.findViewById(R.id.usr_email);
            img_profile = v.findViewById(R.id.usr_icon);
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
    public chatAdapter(ArrayList<chatData> myDataset, Context context, String writer_id) {
        // context를 전달받아야, shared preference를 쓸 수 있음
        this.context = context;

        // dataset을 받아서 뿌려준다
        mDataset = myDataset;

        // 이 글 작성자를 알려준다
        this.writer_id = writer_id;
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
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        // 채팅 쓴 사람 아이디 구하기
        String msg_writer = mDataset.get(position).getEmail().toString();
        int idx = msg_writer.indexOf("@");
        String msg_id = msg_writer.substring(0, idx);

        if(this.writer_id.equals(msg_id)){
            // 채팅 이메일 - 방장으로 표시
            holder.email_icon.setText(mDataset.get(position).getEmail());
            holder.email_icon.setBackgroundColor(0x0FEF0F0F);
            Log.d(">>>", "onBindViewHolder: 내가 방장이다  writer id : " + writer_id +"  msgid: "+ msg_id);
        }else{
            // 채팅 이메일
            holder.email_icon.setText(mDataset.get(position).getEmail());
            Log.d(">>>", "onBindViewHolder: 방장 아님" + writer_id +"  msgid: "+ msg_id);
        }

        // 채팅 메세지
        holder.textView.setText(mDataset.get(position).getMsg());



        /* 채팅 프로필 사진 설정 */
        // shared preference - 이메일, 아이디 가져오기
        String my_email = mDataset.get(position).getEmail();

        int idx_domain = my_email.indexOf("@");
        String my_id = my_email.substring(0, idx_domain);

        // 사진 가져오기 - firebase
        StorageReference mStorageRef;
        StorageReference picture_Ref;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        picture_Ref = mStorageRef.child("img_profile/" + my_id);

        picture_Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // glide 사용해서 사진 설정하기
                Glide.with(context).load(uri).into(holder.img_profile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("no image >> ", "onFailure: 이미지가 업로드 안 됨");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}