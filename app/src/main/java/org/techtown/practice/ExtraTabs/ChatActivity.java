package org.techtown.practice.ExtraTabs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.techtown.practice.MainActivity;
import org.techtown.practice.R;
import org.techtown.practice.chat_recycler.chatAdapter;
import org.techtown.practice.chat_recycler.chatData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;


public class ChatActivity extends AppCompatActivity {
    // firebase 데이터베이스를 가져온다
    FirebaseDatabase database;
    private FirebaseAuth mAuth;

    // 데이터베이스에서 가져온 ref 를 계속 사용해주기 위해 전역 선언을 한다
    DatabaseReference myRef;

    // 메세지 내용을 리사이클러 뷰에 올리기 위함
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    chatAdapter mAdapter;
    ArrayList<chatData> chatArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // xml 연결
        recyclerView = (RecyclerView) findViewById(R.id.chat_recycle);
        final EditText chat_msg = (EditText)findViewById(R.id.chat_msg);
        Button chat_btn =(Button)findViewById(R.id.btn_chat);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // firebase 데이터베이스 연결
        database = FirebaseDatabase.getInstance();

        // 리사이클러 뷰를 위함
        chatArray = new ArrayList<>();
        mAdapter = new chatAdapter(chatArray, getApplicationContext());
        recyclerView.setAdapter(mAdapter);

        // 현재 내 이메일 가져오기
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        final String my_email = pref.getString("email", "");

        // 버튼 설정 - 메세지 보내기
        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 보낼 내용 받아오기
                String str = chat_msg.getText().toString();

                // 전달할 시간 저장
                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
                String datetime = dateFormat.format(c.getTime());

                // 전달할 내용들 저장
                Hashtable<String, String> msg
                        = new Hashtable<String, String>();
                msg.put("email", my_email);
                msg.put("msg", str);

                // firebase 데이터베이스에서, "message" 관련된 ref 를 가져온다
                myRef = database.getReference("message").child(datetime);
                myRef.setValue(msg);
            }
        });

        // firebase에 올라온 채팅 내용을 실시간으로 업데이트 해주는 listener
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // A new comment has been added, add it to the displayed list
                chatData chat = dataSnapshot.getValue(chatData.class);

                Log.d("asd",""+chat.getMsg().toString());

                // chat array 추가
                chatArray.add(chat);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                chatData chat = dataSnapshot.getValue(chatData.class);
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
                Toast.makeText(ChatActivity.this, "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        database.getReference("message").addChildEventListener(childEventListener);
    }
}