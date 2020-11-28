package org.techtown.practice.SubTab_Tab1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import org.techtown.practice.Login_Signin.LoginActivity;
import org.techtown.practice.R;
import org.techtown.practice.SubTab_Drawer.DibActivity;
import org.techtown.practice.SubTab_Drawer.MyWritingsActivity;

public class ProfileActivity extends AppCompatActivity {
    // 이메일, 아이디
    String my_email, my_id;

    // 판매목록, 구매목록, 신뢰도
    String str_list_write, str_list_dib, str_confidence;

    // xml 연결할 것들
    ImageView img_profile;
    TextView usr_email, usr_confidnce;
    Button btn_write, btn_dib, btn_chat, btn_re_profile;
    ImageView btn_logout;

    // 사진 Uri 가져오기 위한 firebase
    StorageReference mStorageRef;
    StorageReference picture_Ref;

    // intent 관련 코드 번호 받기위해
    int BTN_IMAGE_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 사진을 저장하기 위한 레퍼런스 - 업로드
        mStorageRef = FirebaseStorage.getInstance().getReference();

        /* xml 연결 */
        img_profile = (ImageView)findViewById(R.id.img_profile);
        usr_email = (TextView)findViewById(R.id.usr_email);
        btn_chat = (Button)findViewById(R.id.btn_profile_chat);
        btn_dib = (Button)findViewById(R.id.btn_profile_dib);
        btn_write = (Button)findViewById(R.id.btn_profile_my_writing);
        btn_logout = (ImageView)findViewById(R.id.icon_logout);
        btn_re_profile = (Button)findViewById(R.id.btn_re_profile);

        /* shared preference */
        // 현재 내 이메일 가져오기
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        my_email = pref.getString("email", "");

        int idx_domain = my_email.indexOf("@");
        my_id = my_email.substring(0, idx_domain);
        usr_email.setText(my_id);

        // 현재 내 판매목록 string 값 가져오기
        str_list_write = pref.getString("str_list_write", "");
        String[] arr_my_write = str_list_write.split(",");
        int num_write = arr_my_write.length;

        // 현재 내 구매목록 string 값 가져오기
        str_list_dib = pref.getString("str_list_dib", "");
        String[] arr_my_dib = str_list_dib.split(",");
        int num_dib = arr_my_dib.length;

        /* firebase에서 신뢰도 값 가져오기 */
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("user").child(my_id).child("my_confidence");

        /* 버튼 설정해주기 */
        // 프로필 수정
        btn_re_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // revise profile activity로 가기
                Intent intent = new Intent(getApplicationContext(), ProfileReviseActivity.class);
                startActivity(intent);
            }
        });

        // 로그아웃
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // login activity로 가기
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        // 내가 쓴 글
        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 모든 shared preference 저장값들을 삭제한다 - 부정 로그인 방지
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("str_list_write", str_list_write);
                editor.commit();

                // my write activity로 가기
                Intent intent = new Intent(getApplicationContext(), MyWritingsActivity.class);
                startActivity(intent);
            }
        });

        // 내가 대여한 글
        btn_dib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 모든 shared preference 저장값들을 삭제한다 - 부정 로그인 방지
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("str_list_dib", str_list_dib);
                editor.commit();

                // my dib activity로 가기
                Intent intent = new Intent(getApplicationContext(), DibActivity.class);
                startActivity(intent);
            }
        });

        // 내가 채팅하고 있는 글
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 아직 구현 못함
            }
        });

        /* 프로필 사진 올리기 */
        // 해당 인덱스에 대응되는 사진 Uri 값을 어답터에 넣어준다 - 다운로드
        picture_Ref = mStorageRef.child("img_profile/" + my_id);
        picture_Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // glide 사용해서 사진 설정하기
                Glide.with(ProfileActivity.this).load(uri).into(img_profile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("no image >> ", "onFailure: 이미지가 업로드 안 됨");
            }
        });

        // 신뢰도 값을 가져오기 위함
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null)
                    str_confidence = dataSnapshot.getValue().toString();
                else
                    str_confidence = "0";
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
}