package org.techtown.practice.SubTab_Tab1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.techtown.practice.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;

public class WriteActivity extends AppCompatActivity {
    // 데이터베이스에서 가져온 ref 를 계속 사용해주기 위해 전역 선언을 한다
    DatabaseReference myRef_idx, myRef_write;
    StorageReference mStorageRef;
    StorageReference picture_Ref;

    // firebase 데이터베이스를 가져온다
    FirebaseDatabase database;

    // 인덱스 값 입력받기 위함
    int write_index;

    // 입력 받을 수 있는 객체
    TextView tv_save, tv_ai_poem;
    EditText et_write, et_title;
    private ImageView iv_back;
    private ImageView device_img;

    // 객체로부터 입력 받은 string 값
    String txt_content, title, writer;
    String email_id;

    // intent 관련 코드 번호 받기위해
    int BTN_IMAGE_CODE = 1001;
    int MY_PERMISSION_REQUEST_READ_CONTACTS = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        /* firebase 관련 디비 접근 */
        // firebase 데이터베이스 연결
        database = FirebaseDatabase.getInstance();

        // 내 이메일 가져오기
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        final String cur_email = pref.getString("email", "");

        int idx_domain = cur_email.indexOf("@");
        email_id = cur_email.substring(0, idx_domain);

        // 사진을 저장하기 위한 레퍼런스 - 업로드
        mStorageRef = FirebaseStorage.getInstance().getReference();

        /* xml 연결 */
        tv_save = findViewById(R.id.tv_save);
        et_title = findViewById(R.id.et_title);
        et_write = findViewById(R.id.et_write);
        iv_back = findViewById(R.id.iv_back);
        device_img = findViewById(R.id.device_img);

        /* 현재 글의 인덱스 값을 받아온다 */
        myRef_idx = database.getReference("index").child("current_writing_index");
        myRef_idx.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                write_index = Integer.parseInt(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /* 버튼 연결 */
        // 버튼 - 이미지
        device_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, BTN_IMAGE_CODE);
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        iv_back = findViewById(R.id.iv_back);
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 쓴 글을 받아온다
                title = et_title.getText().toString();
                txt_content = et_write.getText().toString();

                // 전달할 시간 저장
                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
                String datetime = dateFormat.format(c.getTime());

                // 전달할 내용들 저장
                Hashtable<String, String> writing
                        = new Hashtable<String, String>();
                writing.put("index", String.valueOf(write_index));
                writing.put("title", title);
                writing.put("txt_content", txt_content);
                writing.put("flag_buy", "no");
                writing.put("flag_borrow", "no");
                writing.put("flag_give_back", "no");
                writing.put("writer", email_id);

                /* 쓴 글을 데이터베이스에 기록해준다 */
                // 글 자체를 저장해준다
                myRef_write = database.getReference("writings").child(""+write_index);
                myRef_write.setValue(writing);

                // 인덱스 값을 저장해준다
                myRef_write = database.getReference("index").child("current_writing_index");
                myRef_write.setValue(write_index + 1);

                // 유저 테이블에 내가 쓴 글 인덱스를 기록해둔다
                myRef_write = database.getReference("user").child(email_id).child("my_write").child(datetime);
                myRef_write.setValue(write_index);

                // 내가 쓴 글을 확인한다
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("idx_writing", String.valueOf(write_index));
                editor.putString("writer", email_id);
                editor.putString("content_writing", txt_content);
                editor.putString("title_writing", title);
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), ShowWrittenActivity.class);
//                intent.putExtra("title_writing", title);
//                intent.putExtra("content_writing", txt_content);
//                intent.putExtra("writer", writer);
//                intent.putExtra("idx_writing", write_index + 1);

                startActivity(intent);
                finish();
            }
        });

        /* 권한 설정 관련 코드 */
        if(ContextCompat.checkSelfPermission(WriteActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(WriteActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){

            }else{
                ActivityCompat.requestPermissions(WriteActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSION_REQUEST_READ_CONTACTS);
            }
        }else{

        }
    }

    /* 단말기 접근 허가 관련 코드 */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(MY_PERMISSION_REQUEST_READ_CONTACTS == 1002){

        }
    }

    /* 이미지 업로드가 완료되면, firebase에 사진을 업로드 해준다 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /* 사진 업로드 버튼을 눌렀을 경우 */
        if(requestCode == BTN_IMAGE_CODE){
            if(data != null){
                Uri image = data.getData();

                // glide 사용해서 사진 가져오기
                Glide.with(this).load(image).into(device_img);

                /* 사진을 업로드하기 */
                // 사진을 가져오기 위한 레퍼런스 - 다운로드
                picture_Ref = mStorageRef.child("borrow_device/" + write_index);
                picture_Ref.putFile(image)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {

                            }
                        });
            }
        }
    }
}