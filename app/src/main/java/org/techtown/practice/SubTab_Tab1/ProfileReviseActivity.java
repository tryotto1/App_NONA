package org.techtown.practice.SubTab_Tab1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.techtown.practice.R;

public class ProfileReviseActivity extends AppCompatActivity {
    // 사진 Uri 가져오기 위한 firebase
    StorageReference mStorageRef;
    StorageReference picture_Ref;

    // intent 관련 코드 번호 받기위해
    int BTN_IMAGE_CODE = 1001;

    // xml 연결할 것들
    ImageView img_profile;
    TextView usr_email;

    // 이메일, 아이디
    String my_email, my_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise_profile);

        // 사진을 저장하기 위한 레퍼런스 - 업로드
        mStorageRef = FirebaseStorage.getInstance().getReference();

        /* xml 연결 */
        img_profile = (ImageView)findViewById(R.id.img_profile);
        usr_email = (TextView)findViewById(R.id.usr_email);

        /* shared preference */
        // 현재 내 이메일 가져오기
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        my_email = pref.getString("email", "");

        int idx_domain = my_email.indexOf("@");
        my_id = my_email.substring(0, idx_domain);
        usr_email.setText(my_id);

        /* 프로필 사진 새로 업로드 하기 */
        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 갤러리로 넘어감
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, BTN_IMAGE_CODE);
            }
        });

        /* 프로필 사진 가져오기 */
        // 해당 인덱스에 대응되는 사진 Uri 값을 어답터에 넣어준다 - 다운로드
        picture_Ref = mStorageRef.child("img_profile/" + my_id);
        picture_Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // glide 사용해서 사진 설정하기
                Glide.with(ProfileReviseActivity.this).load(uri).into(img_profile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("no image >> ", "onFailure: 이미지가 업로드 안 됨");
            }
        });

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
                Glide.with(this).load(image).into(img_profile);

                /* 사진을 업로드하기 */
                // 사진을 가져오기 위한 레퍼런스 - 다운로드
                picture_Ref = mStorageRef.child("img_profile/" + my_id);
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