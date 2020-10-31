package org.techtown.practice.ExtraTabs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.techtown.practice.R;

public class Tab1_WriteActivity extends AppCompatActivity {
    // 데이터베이스에서 가져온 ref 를 계속 사용해주기 위해 전역 선언을 한다
    DatabaseReference myRef_idx, myRef_write;

    // firebase 데이터베이스를 가져온다
    FirebaseDatabase database;

    // 인덱스 값 입력받기 위함
    int write_index;

    // 입력 받을 수 있는 객체
    TextView tv_save, tv_ai_poem;
    EditText et_write, et_title;
    private ImageView iv_back;

    // 객체로부터 입력 받은 string 값
    String writing, title, writer;
    Tab_WriteData writeData;

    // 현재 내 이메일 값 - db 에 저장하기 위함
    String cur_email;

    // AI로부터 시를 받아주기 위함
    String ai_contet = "";
    String content;
    JSONArray ai_poem_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        // firebase 데이터베이스 연결
        database = FirebaseDatabase.getInstance();

        // 내 이메일 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("prev",MODE_PRIVATE);
        cur_email = sharedPreferences.getString("email", "null");

        // xml 연결
        tv_save = findViewById(R.id.tv_save);
        et_title = findViewById(R.id.et_title);
        et_write = findViewById(R.id.et_write);
        iv_back = findViewById(R.id.iv_back);
        tv_ai_poem = findViewById(R.id.tv_ai_poem);

        /* 버튼 연결 */
        tv_ai_poem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content = et_write.getText().toString();
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 현재 인덱스 값을 받아온다
        myRef_idx = database.getReference("index").child("current_writing_index");
        myRef_idx.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                write_index = Integer.parseInt(dataSnapshot.getValue().toString());
                Log.d(">>>>>>index 구하기111111", "onDataChange: " + dataSnapshot.getValue().toString());
                Log.d(">>>>>>index 구하기222222", "onDataChange: " + write_index);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        iv_back = findViewById(R.id.iv_back);
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 쓴 글을 받아온다
                title = et_title.getText().toString();
                writing = et_write.getText().toString();

                /* 쓴 글을 데이터베이스에 기록해준다 */

                Log.d(">>>>>>>>", "onClick: "+title+"  "+writing);

                writeData = new Tab_WriteData();
                writeData.setTitle(title);
                writeData.setTxt_content(writing);

                // 글 자체를 저장해준다
                Log.d(">>>>>>>>> 실제 저장시 인덱스 ", "onClick: " + write_index);
                myRef_write = database.getReference("writings").child(String.valueOf(write_index + 1));
                myRef_write.setValue(writeData);

                // 인덱스 값을 저장해준다
                myRef_write = database.getReference("index").child("current_writing_index");
                myRef_write.setValue(write_index + 1);

                // 내가 쓴 글을 확인한다
                Intent intent = new Intent(getApplicationContext(), Tab_ShowWrittenActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("writing", writing);
                intent.putExtra("writer", writer);

                startActivity(intent);
                finish();
            }
        });

    }
}