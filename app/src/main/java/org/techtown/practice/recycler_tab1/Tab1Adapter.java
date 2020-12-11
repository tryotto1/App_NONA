package org.techtown.practice.recycler_tab1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.practice.SubTab_Tab1.ChatActivity;
import org.techtown.practice.R;
import org.techtown.practice.SubTab_Tab1.ShowWrittenActivity;
import org.techtown.practice.Tabs.Frag1;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class Tab1Adapter extends RecyclerView.Adapter<Tab1Adapter.CustomViewHolder> {
    // shared preference를 쓰기 위한 설정
    Context context;

    // 사진 Uri 가져오기 위한 firebase
    StorageReference mStorageRef;
    StorageReference picture_Ref;

    // shared preference 사용 용도
    String my_email, my_id;

    // firebase 데이터베이스를 가져온다
    FirebaseDatabase database;

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

        // 사진을 저장하기 위한 레퍼런스 - 업로드
        mStorageRef = FirebaseStorage.getInstance().getReference();

        // firebase 데이터베이스 연결
        database = FirebaseDatabase.getInstance();

        // 현재 내 이메일 가져오기
        SharedPreferences pref = context.getSharedPreferences("pref", MODE_PRIVATE);
        my_email = pref.getString("email", "");

        int idx_domain = my_email.indexOf("@");
        my_id = my_email.substring(0, idx_domain);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final Tab1Adapter.CustomViewHolder holder, final int position) {
        holder.tv_title.setText(arrayList.get(position).getTitle());
        holder.tv_writer.setText(arrayList.get(position).getWriter());
        holder.tv_date.setText(arrayList.get(position).getDate());
        holder.tv_content.setText(arrayList.get(position).getContent());
        Glide.with(holder.itemView.getContext()).load(arrayList.get(position).getUri()).into(holder.device_img);

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

        /* 찜 선택 반응 */
        holder.frag1_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 관련 정보를 firebase에 새로 데이터베이스를 만들어서 저장
                Hashtable<String, String> dib
                        = new Hashtable<String, String>();
                dib.put("dib", arrayList.get(position).getIndex());

                // firebase 데이터베이스에 user 관련 정보를 저장해준다
                database.getReference("user").child(my_id).child("my_dib").child(arrayList.get(position).getIndex()).setValue(dib);

                // 토스트
                Toast.makeText(context, "찜 했습니다", Toast.LENGTH_SHORT).show();

                // 하트 색깔을 바꿔준다
                holder.frag1_like.setImageResource(R.drawable.heart_red);
            }
        });

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
                editor.putString("date_writing", arrayList.get(position).getDate());
                editor.putString("date_exchange", arrayList.get(position).getWrite_date_exchange());
                editor.putString("place_exchange", arrayList.get(position).getWrite_place_exchange());
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

        protected TextView tv_title, tv_writer, tv_date, tv_content;
        protected LinearLayout layout;
        protected ImageView device_img;
        protected CircleImageView writer_img;
        protected ImageFilterView frag1_like;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_content = itemView.findViewById(R.id.item_content);
            this.tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            this.device_img = itemView.findViewById(R.id.frag1_img_item);
            this.writer_img = itemView.findViewById(R.id.writer_img_frag1);
            this.tv_writer = itemView.findViewById(R.id.writer_name_frag1);
            this.tv_date = itemView.findViewById(R.id.writer_date_frag1);
            this.frag1_like = itemView.findViewById(R.id.frag1_like);
        }
    }
}
