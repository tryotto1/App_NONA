package org.techtown.practice.Tabs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.practice.SubTab_Tab1.WriteActivity;
import org.techtown.practice.R;

import org.techtown.practice.recycler_tab1.Tab1Adapter;
import org.techtown.practice.recycler_tab1.Tab1Data;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Frag2 extends Fragment {
    private View view;
    private FloatingActionButton btn_write;

    // 이메일, 아이디
    String my_email,my_id;

    // 프로필 사진
    CircleImageView img_profile;

    // 사진 Uri 가져오기 위한 firebase
    StorageReference mStorageRef;
    StorageReference picture_Ref;

    // firebase 데이터베이스를 가져온다
    FirebaseDatabase database;

    /* recycler view */
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    private ArrayList<Tab1Data> arrayList;
    private Tab1Adapter tab1Adapter;

    // ViewPagerAdapter를 위해서
    public static Frag2 newInstance() {
        Frag2 frag2 = new Frag2();
        return frag2;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag2, container, false);
        btn_write = view.findViewById(R.id.btn_write);

        // firebase 데이터베이스 연결
        database = FirebaseDatabase.getInstance();

        // 사진을 저장하기 위한 레퍼런스 - 업로드
        mStorageRef = FirebaseStorage.getInstance().getReference();

        /*recycler view*/
        recyclerView = view.findViewById(R.id.frag1_recycle);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();
        tab1Adapter = new Tab1Adapter(arrayList, getContext());
        recyclerView.setAdapter(tab1Adapter);

        /* button - 새로운 게시물 작성하기 */
        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 글 쓰는 activity로 넘겨준다
                Intent intent = new Intent(getActivity(), WriteActivity.class);
                startActivity(intent);
            }
        });

        /* 프로필 사진 올리기 */
        // xml 연결
        img_profile = view.findViewById(R.id.img_profile);

        // 현재 내 이메일 가져오기
        SharedPreferences pref = getContext().getSharedPreferences("pref", getContext().MODE_PRIVATE);
        my_email = pref.getString("email", "");

        int idx_domain = my_email.indexOf("@");
        my_id = my_email.substring(0, idx_domain);

        // 해당 인덱스에 대응되는 사진 Uri 값을 어답터에 넣어준다 - 다운로드
        picture_Ref = mStorageRef.child("img_profile/" + my_id);
        picture_Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // glide 사용해서 사진 설정하기
                Glide.with(Frag2.this).load(uri).into(img_profile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("no image >> ", "onFailure: 이미지가 업로드 안 됨");
            }
        });

        // firebase에 올라온 글 내용을 실시간으로 업데이트 해주는 listener
        ChildEventListener childEventListener_frag1 = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                final Tab1Data writing = dataSnapshot.getValue(Tab1Data.class);

                // 쓴 글에 대한 인덱스 값을 가져온다
                String _idx = writing.getIndex();
                int idx = Integer.parseInt(_idx);

                // 해당 인덱스에 대응되는 사진 Uri 값을 어답터에 넣어준다 - 다운로드
                picture_Ref = mStorageRef.child("borrow_device/" + idx);
                picture_Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        writing.setUri(uri);

                        // 게시물 받아온 걸 넣어준다
                        arrayList.add(writing);
                        tab1Adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 게시물 받아온 걸 넣어준다
                        arrayList.add(writing);
                        tab1Adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Tab1Data writing = dataSnapshot.getValue(Tab1Data.class);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String commentKey = dataSnapshot.getKey();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        database.getReference("writings").addChildEventListener(childEventListener_frag1);

        return view;
    }
}
