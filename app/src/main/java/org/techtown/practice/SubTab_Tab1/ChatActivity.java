package org.techtown.practice.SubTab_Tab1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.techtown.practice.R;
import org.techtown.practice.recycler_Chat.chatAdapter;
import org.techtown.practice.recycler_Chat.chatData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;


public class ChatActivity extends AppCompatActivity {
    // firebase 데이터베이스를 가져온다
    FirebaseDatabase database;

    // 데이터베이스에서 가져온 ref 를 계속 사용해주기 위해 전역 선언을 한다
    DatabaseReference myRef;

    // 메세지 내용을 리사이클러 뷰에 올리기 위함
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    chatAdapter mAdapter;
    ArrayList<chatData> chatArray;

    // 이메일, 아이디
    String my_email;
    String my_id;

    // 이미지 뷰
    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // xml 연결
        recyclerView = (RecyclerView) findViewById(R.id.chat_recycle);
        final EditText chat_msg = (EditText)findViewById(R.id.chat_msg);
        ImageView chat_btn =findViewById(R.id.btn_chat);

        /* 버튼 클릭 연결 */
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 현재 내 이메일 가져오기
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        my_email = pref.getString("email", "");

        int idx_domain = my_email.indexOf("@");
        my_id = my_email.substring(0, idx_domain);

        // 게시물 번호 가져오기
        final String idx_writing = pref.getString("idx_writing", " ");

        // 작성자 아이디 가져오기
        final String writer = pref.getString("writer", " ");

        // 내 게시물에서 대화하고 있다면, 타인의 아이디 가져옴
        final String chat_usr = pref.getString("chat_usr", "no");

        Log.d(">>?>>", "onCreate: (writer)" + writer + " (chat_usr) " + chat_usr + " (my_id) " + my_id);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // firebase 데이터베이스 연결
        database = FirebaseDatabase.getInstance();

        // 리사이클러 뷰를 위함
        chatArray = new ArrayList<>();
        mAdapter = new chatAdapter(chatArray, getApplicationContext(), writer);
        recyclerView.setAdapter(mAdapter);

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
                dateFormat = new SimpleDateFormat("aa h시 m분");
                String date = dateFormat.format(c.getTime());

                // 전달할 내용들 저장
                Hashtable<String, String> msg
                        = new Hashtable<String, String>();
                msg.put("email", my_email);
                msg.put("msg", str);
                msg.put("date", date);

                // 내가 쓴 글에서, 다른 사람과 채팅시
                if(chat_usr.equals(my_id)==false&& writer.equals(my_id)==true) {
                    Log.d("11", "onCreate: 여기1>>");
                    myRef = database.getReference("message").child(idx_writing).child(chat_usr).child(datetime);
                    myRef.setValue(msg);
                }
                // 다른 사람이 쓴 글에서, 내가 채팅시
                else if(chat_usr.equals(my_id)==true && writer.equals(my_id)==false){
                    Log.d("22", "onCreate: 여기2>>");
                // 내 게시물에서 다른 사람이랑 대화중
                    myRef = database.getReference("message").child(idx_writing).child(my_id).child(datetime);
                    myRef.setValue(msg);
                }
                // 채팅 입력창 초기화
                chat_msg.getText().clear();
            }
        });

        // firebase에 올라온 채팅 내용을 실시간으로 업데이트 해주는 listener
        ChildEventListener childEventListener_chat = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // A new comment has been added, add it to the displayed list
                chatData chat = dataSnapshot.getValue(chatData.class);
                Log.d("???", "onChildAdded: ???");
                // chat array 추가
                chatArray.add(chat);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }
        };

        // 다른 사람 게시물에서 내가 채팅한것 가져오기
        if(chat_usr.equals(my_id)==true && writer.equals(my_id)==false){
            Log.d("11", "onCreate: 여기1  " + idx_writing + "??");
            database.getReference("message").child(idx_writing).child(my_id).addChildEventListener(childEventListener_chat);
        }
        // 내가 쓴 게시물에서, 다른 사람과 채팅한거 가져오기
        else if(chat_usr.equals(my_id)==false && writer.equals(my_id)==true){
            Log.d("22", "onCreate: 여기2");
        // 내 게시물에서 다른 사람이랑 대화중
            database.getReference("message").child(idx_writing).child(chat_usr).addChildEventListener(childEventListener_chat);
        }
    }
}