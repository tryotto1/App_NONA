package org.techtown.practice.recycler_MyWritings_Chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.practice.R;
import org.techtown.practice.SubTab_Tab1.ChatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EventListener;
import java.util.Hashtable;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class MyWritingsChatAdapter extends RecyclerView.Adapter<MyWritingsChatAdapter.CustomViewHolder> {
    Context context;

    // 사진 Uri 가져오기 위한 firebase
    StorageReference mStorageRef;
    StorageReference picture_Ref;

    // shared preference 사용 용도
    String my_email, my_id;

    // firebase 데이터베이스를 가져온다
    FirebaseDatabase database;

    private ArrayList<MyWritingsChatData> arrayList; // 서버에서 받는 정보

    public MyWritingsChatAdapter(ArrayList<MyWritingsChatData> arrayList, Context context) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyWritingsChatAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_write_chat, parent, false);
        MyWritingsChatAdapter.CustomViewHolder holder = new MyWritingsChatAdapter.CustomViewHolder(view);

        // 사진을 저장하기 위한 레퍼런스 - 업로드
        mStorageRef = FirebaseStorage.getInstance().getReference();

        // firebase 데이터베이스 연결
        database = FirebaseDatabase.getInstance();

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyWritingsChatAdapter.CustomViewHolder holder, final int position) {
        holder.tv_id.setText(arrayList.get(position).getWriter());

        database.getReference("user").child(arrayList.get(position).getWriter()).child("my_info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String str = dataSnapshot.getValue().toString();

                int idx_name = str.indexOf("=");
                int idx_name2 = str.indexOf(",");
                String str_name = str.substring(idx_name+1, idx_name2);

                str = str.substring(idx_name2);
                idx_name = str.indexOf("=");
                String str_major = str.substring(idx_name+1, str.length()-1);

                holder.tv_major.setText(str_major);
                holder.tv_writer.setText(str_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // 해당 인덱스에 대응되는 사진 Uri 값을 어답터에 넣어준다 - 다운로드
        picture_Ref = mStorageRef.child("img_profile/" + arrayList.get(position).getWriter());
        picture_Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // glide 사용해서 사진 설정하기
                Glide.with(holder.itemView.getContext()).load(uri).into(holder.writer_img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        // 각 게시물을 클릭할 경우, 채팅을 시작한다
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 현재 내 이메일 가져오기
                SharedPreferences pref_tmp = context.getSharedPreferences("pref", MODE_PRIVATE);
                my_email = pref_tmp.getString("email", "");

                int idx_domain = my_email.indexOf("@");
                my_id = my_email.substring(0, idx_domain);

                // 몇 번째 게시물인지 chatActivity에 전달한다
                SharedPreferences pref = context.getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("idx_writing", arrayList.get(position).getIndex());
                editor.putString("chat_usr", arrayList.get(position).getWriter());
                editor.putString("writer", my_id);
                editor.commit();

                Log.d(">>write_chat_adapter", "onClick: (id)" + my_id + " (writer) " +arrayList.get(position).getWriter());

                // 채팅을 시작한다
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
        protected TextView tv_id, tv_writer, tv_major;
        protected CircleImageView writer_img;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_id = (TextView) itemView.findViewById(R.id.tv_id);
            this.writer_img = itemView.findViewById(R.id.writer_img_frag1);
            this.tv_writer = itemView.findViewById(R.id.writer_name_frag1);
            this.tv_major = itemView.findViewById(R.id.writer_major_frag1);
        }
    }
}
