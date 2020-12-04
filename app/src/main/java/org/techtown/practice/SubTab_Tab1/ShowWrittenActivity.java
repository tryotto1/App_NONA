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
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowWrittenActivity extends AppCompatActivity {
    // xml 연결용
    ImageView iv_back, img_item, btn_one_write_chat, btn_one_write_buy, btn_one_write_dib;
    TextView one_write_title, one_write_content, written_writer_name, date_writing;
    TextView txt_date_exchange, txt_place_exchange;
    CircleImageView writer_img;

    // 사진 Uri 가져오기 위한 firebase
    StorageReference mStorageRef;
    StorageReference picture_Ref;

    // firebase 데이터베이스를 가져온다
    FirebaseDatabase database;

    // 글 내용
    String title, content, writer, idx_writing, date;
    String date_exchange, place_exchange;
    String my_email, my_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_writing);

        // 현재 내 이메일 가져오기
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        my_email = pref.getString("email", "");

        int idx_domain = my_email.indexOf("@");
        my_id = my_email.substring(0, idx_domain);

        // firebase 데이터베이스 연결
        database = FirebaseDatabase.getInstance();

        // 사진을 저장하기 위한 레퍼런스 - 업로드
        mStorageRef = FirebaseStorage.getInstance().getReference();

        /* xml 연결 */
        iv_back = findViewById(R.id.iv_back);
        one_write_content = findViewById(R.id.one_write_content);
        one_write_title = findViewById(R.id.one_write_title);
        img_item = findViewById(R.id.one_write_img);
        btn_one_write_chat = findViewById(R.id.btn_one_write_chat);
        btn_one_write_dib = findViewById(R.id.btn_one_write_dib);
        written_writer_name = findViewById(R.id.written_writer_name);
        date_writing = findViewById(R.id.written_writer_time);
        writer_img = findViewById(R.id.written_writer_img);
        txt_date_exchange = findViewById(R.id.txt_date_exchange);
        txt_place_exchange = findViewById(R.id.txt_place_exchange);

        /*shared preference 값들 다 가져온다*/
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        title = pref.getString("title_writing", "");
        content = pref.getString("content_writing", "");
        writer = pref.getString("writer", "");
        idx_writing = pref.getString("idx_writing", "");
        date = pref.getString("date_writing", "");
        date_exchange = pref.getString("date_exchange", "");
        place_exchange = pref.getString("place_exchange", "");

        /* 쓴 사람 프로필 사진 가져오기 */
        // 해당 인덱스에 대응되는 사진 Uri 값을 어답터에 넣어준다 - 다운로드
        picture_Ref = mStorageRef.child("img_profile/" + writer);
        picture_Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // glide 사용해서 사진 설정하기
                Glide.with(ShowWrittenActivity.this).load(uri).into(writer_img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        /* 버튼 설정하기 */
        // 뒤로가기 버튼
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /* 채팅 보내기 */
        btn_one_write_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 내가 쓴 글이 아닐 경우, 바로 채팅 시작
                if(my_id.equals(writer)==false) {
                    // 몇 번째 게시물인지 chatActivity에 전달한다
                    SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("idx_writing", idx_writing);
                    editor.putString("chat_usr", my_id);
                    editor.putString("writer", writer);
                    editor.commit();

                    Log.d("...>>", "onClick: (idx)" + idx_writing + " (id) " + my_id + " (writer) " + writer);

                    // firebase 데이터베이스에 user 관련 정보를 저장해준다
                    database.getReference("user").child(my_id).child("my_chat").child(idx_writing).setValue(idx_writing);

                    // 토스트
                    Toast.makeText(ShowWrittenActivity.this, "쪽지 목록에 추가 했습니다", Toast.LENGTH_SHORT).show();

                    // 채팅을 시작한다
                    Intent intent = new Intent(view.getContext(), ChatActivity.class);
                    view.getContext().startActivity(intent);
                }else{
                    Log.d("22", "onClick: 여긴가?");
                    // 내가 쓴 글일 경우, 나와 쪽지 주고받는 모든 유저들 리스트 가져옴
                    Intent intent = new Intent(view.getContext(), MyWritingsChatActivity.class);
                    view.getContext().startActivity(intent);
                }
            }
        });

        /* xml 값 채워주기 */
        one_write_title.setText(title);
        one_write_content.setText(content);
        written_writer_name.setText(writer);
        date_writing.setText(date);
        txt_date_exchange.setText(date_exchange);
        txt_place_exchange.setText(place_exchange);

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

        /* 찜 선택시 */
        btn_one_write_dib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                // 전달할 시간 저장
//                Calendar c = Calendar.getInstance();
//                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
//                String datetime = dateFormat.format(c.getTime());

                // 관련 정보를 firebase에 새로 데이터베이스를 만들어서 저장
                Hashtable<String, String> dib
                        = new Hashtable<String, String>();
                dib.put("dib", idx_writing);

                // firebase 데이터베이스에 user 관련 정보를 저장해준다
                database.getReference("user").child(my_id).child("my_dib").child(idx_writing).setValue(dib);

                // 토스트
                Toast.makeText(ShowWrittenActivity.this, "찜 했습니다", Toast.LENGTH_SHORT).show();
            }
        });

    }
}