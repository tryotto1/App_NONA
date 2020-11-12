package org.techtown.practice.SubTab_Drawer;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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

import org.techtown.practice.recycler_Dib.DibAdapter;
import org.techtown.practice.recycler_Dib.DibData;
import org.techtown.practice.recycler_MyWritings.MyWritingsAdapter;
import org.techtown.practice.recycler_MyWritings.MyWritingsData;
import org.techtown.practice.R;
import org.techtown.practice.recycler_tab1.Tab1Data;

import java.util.ArrayList;

public class MyWritingsActivity extends AppCompatActivity {
    private View view;
    private TextView tv_userid, tv_title;
    private String title;

    /* recyclerview에서 사용 */
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    private ArrayList<MyWritingsData> arrayList;
    private MyWritingsAdapter myWritingsAdapter;

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
        setContentView(R.layout.activity_writings_my);

        // firebase 데이터베이스 연결
        database = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        // 현재 내 Writing 목록 가져오기
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        str_list_write = pref.getString("str_list_write", "");

        /*recycler view*/
        recyclerView = findViewById(R.id.sub_recycler);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();
        myWritingsAdapter = new MyWritingsAdapter(arrayList, this);
        recyclerView.setAdapter(myWritingsAdapter);

        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // firebase에 올라온 글 내용을 실시간으로 업데이트 해주는 listener
        ChildEventListener childEventListener_write = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                final MyWritingsData writing = dataSnapshot.getValue(MyWritingsData.class);

                String[] arr_my_write = str_list_write.split(",");
                for (String one_my_write : arr_my_write) {
                    // 쓴 글에 대한 인덱스 값을 가져온다
                    String idx = writing.getIndex();

                    // 내가 찜한 글이 아니면 넘어간다
                    if(!idx.equals(one_my_write))
                        continue;

                    // 해당 인덱스에 대응되는 사진 Uri 값을 어답터에 넣어준다 - 다운로드
                    picture_Ref = mStorageRef.child("borrow_device/" + idx);
                    picture_Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            writing.setUri(uri);

                            // 게시물 받아온 걸 넣어준다
                            arrayList.add(writing);
                            myWritingsAdapter.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // 게시물 받아온 걸 넣어준다
                            arrayList.add(writing);
                            myWritingsAdapter.notifyDataSetChanged();
                        }
                    });
                }
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
        database.getReference("writings").addChildEventListener(childEventListener_write);
    }
}
