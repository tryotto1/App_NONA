package org.techtown.practice.SubTab_Tab1;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.practice.R;
import org.techtown.practice.recycler_MyWritings.MyWritingsAdapter;
import org.techtown.practice.recycler_MyWritings.MyWritingsData;
import org.techtown.practice.recycler_MyWritings_Chat.MyWritingsChatAdapter;
import org.techtown.practice.recycler_MyWritings_Chat.MyWritingsChatData;
import org.techtown.practice.recycler_tab1.Tab1Data;

import java.util.ArrayList;

public class MyWritingsChatActivity extends AppCompatActivity {
    private View view;
    private TextView tv_userid, tv_title;
    private String title;

    /* recyclerview에서 사용 */
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    private ArrayList<MyWritingsChatData> arrayList;
    private MyWritingsChatAdapter myWritingsChatAdapter;

    // image view
    private ImageView iv_back;

    /* Firebase */
    // 사진 Uri 가져오기 위한 firebase
    StorageReference mStorageRef;
    StorageReference picture_Ref;

    // firebase 데이터베이스를 가져온다
    FirebaseDatabase database;

    // my writing 목록을 가져오기 위함
    String str_list_write;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writings_chat);

        // firebase 데이터베이스 연결
        database = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        /*recycler view*/
        recyclerView = findViewById(R.id.sub_recycler);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();
        myWritingsChatAdapter = new MyWritingsChatAdapter(arrayList, this);
        recyclerView.setAdapter(myWritingsChatAdapter);

        /* 뒤로가기 버튼 */
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /* 인덱스 값 가져오기 */
        final String idx_writing = getSharedPreferences("pref", MODE_PRIVATE).getString("idx_writing", "");

        /* 채팅 하고 있는 모든 사람들을 리사이클러뷰에 넣어준다 */
        ChildEventListener childEventListener_write_chat = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // 채팅하고 있는 유저 아이디 가져옴
                MyWritingsChatData myWritingsChatData = new MyWritingsChatData();
                myWritingsChatData.setWriter(dataSnapshot.getKey());
                myWritingsChatData.setIndex(idx_writing);

                Log.d("idx_writing", "onChildAdded: idx_writing  " + idx_writing);

                arrayList.add(myWritingsChatData);
                myWritingsChatAdapter.notifyDataSetChanged();
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
        database.getReference("message").child(idx_writing).addChildEventListener(childEventListener_write_chat);
    }
}
