package org.techtown.practice.SubTab_Tab1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.practice.R;
import org.techtown.practice.recycler_MyWritings.MyWritingsAdapter;
import org.techtown.practice.recycler_MyWritings.MyWritingsData;
import org.techtown.practice.recycler_Search.SearchAdapter;
import org.techtown.practice.recycler_Search.SearchData;
import org.techtown.practice.recycler_tab1.Tab1Data;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchActivity extends AppCompatActivity {
    /* 태그 값 */
    String str_tag;
    TextView str_search_tag;

    /* recyclerview에서 사용 */
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    private ArrayList<SearchData> arrayList;
    private SearchAdapter searchAdapter;

    /* Firebase */
    // 사진 Uri 가져오기 위한 firebase
    StorageReference mStorageRef;
    StorageReference picture_Ref;

    // firebase 데이터베이스를 가져온다
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        /* xml */
        str_search_tag = findViewById(R.id.str_search_tag);

        /* 태그 값을 전달받아온다 */
        Intent intent = getIntent();
        str_tag = intent.getStringExtra("tag_value");

        /* 태그 값을 text view 화면에 띄움*/
        str_search_tag.setText("#" + str_tag);

        /* firebase 데이터베이스 연결 */
        database = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        /*recycler view*/
        recyclerView = findViewById(R.id.sub_recycler);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();
        searchAdapter = new SearchAdapter(arrayList, this);
        recyclerView.setAdapter(searchAdapter);

        /* 태그 값을 가져온다 */
        // firebase에 올라온 글 내용을 실시간으로 업데이트 해주는 listener
        ChildEventListener childEventListener_tag = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                if(dataSnapshot.getValue()!=null) {
                    String tag_idx = dataSnapshot.getValue().toString();

                    database.getReference("writings").child(tag_idx).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final SearchData writing = dataSnapshot.getValue(SearchData.class);

                            // 게시물 받아온 걸 넣어준다
                            arrayList.add(writing);
                            searchAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

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
        database.getReference("index").child("tag").child(str_tag).addChildEventListener(childEventListener_tag);
    }
}