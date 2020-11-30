package org.techtown.practice.SubTab_Drawer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.practice.R;

import org.techtown.practice.recycler_Dib.DibAdapter;
import org.techtown.practice.recycler_Dib.DibData;
import org.techtown.practice.recycler_tab1.Tab1Data;

import java.util.ArrayList;

public class DibActivity extends AppCompatActivity {
    /* Firebase */
    // 사진 Uri 가져오기 위한 firebase
    StorageReference mStorageRef;
    StorageReference picture_Ref;

    // firebase 데이터베이스를 가져온다
    FirebaseDatabase database;

    /* recyclerview에서 사용 */
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    private ArrayList<DibData> arrayList;
    private DibAdapter dibAdapter;

    // Dib 목록을 만들기 위함
    String str_my_dib;

    // 뒤로 가기 버튼용
    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writings);

        // firebase 데이터베이스 연결
        database = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        /*recycler view*/
        recyclerView = findViewById(R.id.sub_recycler);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();
        dibAdapter = new DibAdapter(arrayList, this);
        recyclerView.setAdapter(dibAdapter);

        // 현재 내 Dib 목록 가져오기
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        str_my_dib = pref.getString("str_list_dib", "");

        /* 버튼 클릭 연결 */
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // firebase에 올라온 글 내용을 실시간으로 업데이트 해주는 listener
        ChildEventListener childEventListener_Dib = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                final DibData writing = dataSnapshot.getValue(DibData.class);

                Log.d("list of dib", "onChildAdded: " + str_my_dib);

                String[] arr_my_dib = str_my_dib.split(",");
                for (String one_my_dib : arr_my_dib) {
                    // 쓴 글에 대한 인덱스 값을 가져온다
                    String idx = writing.getIndex();

                    Log.d("my all written idx", "onChildAdded: " + idx);
                    Log.d("my dib idx", "onChildAdded: " + one_my_dib);

                    // 내가 찜한 글이 아니면 넘어간다
                    if(!idx.equals(one_my_dib))
                        continue;

                    // 해당 인덱스에 대응되는 사진 Uri 값을 어답터에 넣어준다 - 다운로드
                    picture_Ref = mStorageRef.child("borrow_device/" + idx);
                    picture_Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            writing.setUri(uri);

                            // 게시물 받아온 걸 넣어준다
                            arrayList.add(writing);
                            dibAdapter.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // 게시물 받아온 걸 넣어준다
                            arrayList.add(writing);
                            dibAdapter.notifyDataSetChanged();
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
        database.getReference("writings").addChildEventListener(childEventListener_Dib);
    }
}