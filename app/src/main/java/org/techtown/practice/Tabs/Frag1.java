package org.techtown.practice.Tabs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.techtown.practice.ExtraTabs.ChatActivity;
import org.techtown.practice.ExtraTabs.Tab1_WriteActivity;
import org.techtown.practice.R;

import org.techtown.practice.chat_recycler.chatData;
import org.techtown.practice.tab1_recycler.Tab1Adapter;
import org.techtown.practice.tab1_recycler.Tab1Data;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Frag1 extends Fragment {
    private View view;
    private FloatingActionButton btn_write;

    //
    int tmp_idx = 0;

    // 데이터베이스에서 가져온 ref 를 계속 사용해주기 위해 전역 선언을 한다
    DatabaseReference _;

    // firebase 데이터베이스를 가져온다
    FirebaseDatabase database;

    /* recycler view */
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    private ArrayList<Tab1Data> arrayList;
    private Tab1Adapter tab1Adapter;

    // ViewPagerAdapter를 위해서
    public static Frag1 newInstance() {
        Frag1 frag1 = new Frag1();
        return frag1;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag1, container, false);
        btn_write = view.findViewById(R.id.btn_write);

        // firebase 데이터베이스 연결
        database = FirebaseDatabase.getInstance();

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
                Intent intent = new Intent(getActivity(), Tab1_WriteActivity.class);
                startActivity(intent);
            }
        });

        // firebase에 올라온 글 내용을 실시간으로 업데이트 해주는 listener
        ChildEventListener childEventListener_frag1 = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Tab1Data writing = dataSnapshot.getValue(Tab1Data.class);

                // 게시물 받아온 걸 넣어준다
                arrayList.add(writing);
                tab1Adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                Tab1Data writing = dataSnapshot.getValue(Tab1Data.class);
                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        database.getReference("writings").addChildEventListener(childEventListener_frag1);

        return view;
    }
}
