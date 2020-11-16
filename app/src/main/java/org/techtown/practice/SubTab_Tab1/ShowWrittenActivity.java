package org.techtown.practice.SubTab_Tab1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.practice.R;
import org.techtown.practice.recycler_tab1.Tab1Data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ShowWrittenActivity extends AppCompatActivity {
    // xml 연결용
    ImageView iv_back, img_item, btn_one_write_chat, btn_one_write_buy, btn_one_write_dib;
    TextView one_write_title, one_write_content, one_write_writer;

    // 사진 Uri 가져오기 위한 firebase
    StorageReference mStorageRef;
    StorageReference picture_Ref;

    // firebase 데이터베이스를 가져온다
    FirebaseDatabase database;

    // 글 내용
    String title, content, writer, idx_writing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_writing);

        // firebase 데이터베이스 연결
        database = FirebaseDatabase.getInstance();

        // 사진을 저장하기 위한 레퍼런스 - 업로드
        mStorageRef = FirebaseStorage.getInstance().getReference();

        /* xml 연결 */
        iv_back = findViewById(R.id.iv_back);
        one_write_content = findViewById(R.id.one_write_content);
        one_write_title = findViewById(R.id.one_write_title);
        img_item = findViewById(R.id.one_write_img);
        btn_one_write_buy = findViewById(R.id.btn_one_write_buy);
        btn_one_write_chat = findViewById(R.id.btn_one_write_chat);
        btn_one_write_dib = findViewById(R.id.btn_one_write_dib);

        /*shared preference 값들 다 가져온다*/
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        title = pref.getString("title_writing", "");
        content = pref.getString("content_writing", "");
        writer = pref.getString("writer", "");
        idx_writing = pref.getString("idx_writing", "");

        /* 버튼 설정하기 */
        // 뒤로가기 버튼
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_one_write_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 채팅을 시작한다
                Intent intent = new Intent(view.getContext(), ChatActivity.class);
                view.getContext().startActivity(intent);
            }
        });

        /* xml 값 채워주기 */
        one_write_title.setText(title);
        one_write_content.setText(content);

        // 해당 인덱스에 대응되는 사진 Uri 값을 사진첩에 넣어준다 - 다운로드
        picture_Ref = mStorageRef.child("borrow_device/" + idx_writing);
        picture_Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // glide 사용해서 사진 가져오기
                Glide.with(ShowWrittenActivity.this).load(uri).into(img_item);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
}