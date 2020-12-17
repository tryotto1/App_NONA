package org.techtown.practice.Login_Signin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.techtown.practice.R;

import java.util.Hashtable;

public class SignupActivity extends AppCompatActivity {
    // firebase 데이터베이스를 가져온다
    FirebaseDatabase database;

    // 데이터베이스에서 가져온 ref 를 계속 사용해주기 위해 전역 선언을 한다
    DatabaseReference myRef, myRef_usr;

    // firebase 회원가입용
    private FirebaseAuth mAuth;

    // 입력을 받도록 하기 위한 form
    private EditText user_email, user_password, user_name, user_major;
    private TextView tv_signup;
    private Button btn_email_verify, btn_pwd_check, btn_name_check, btn_major_check;

    // 입력 받은 string 값을 담기 위한 변수
    String email, password, name;

    // 이메일 인증 번호
    Boolean flag_email = Boolean.FALSE;
    Boolean flag_pwd = Boolean.FALSE;
    Boolean flag_major = Boolean.FALSE;
    Boolean flag_name = Boolean.FALSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // firebase 데이터베이스 연결
        database = FirebaseDatabase.getInstance();

        // firebase 회원가입용
        mAuth = FirebaseAuth.getInstance();

        // xml로부터 값들을 받아온다
        user_email = findViewById(R.id.user_email);
        user_password = findViewById(R.id.user_password);
        user_name = findViewById(R.id.use_name);
        user_major = findViewById(R.id.use_major);
        tv_signup = findViewById(R.id.tv_signup);
        btn_email_verify = findViewById(R.id.btn_email_verify);
        btn_pwd_check = findViewById(R.id.btn_pwd_check);
        btn_name_check = findViewById(R.id.btn_name_check);
        btn_major_check = findViewById(R.id.btn_major_check);

        // kupid 이메일인지 확인한다
        flag_email = Boolean.TRUE;
        btn_email_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = user_email.getText().toString();
                int idx_domain = email.indexOf("@");

                String email_domain = email.substring(idx_domain + 1);
                if(email_domain.equals("korea.ac.kr")){
                    Toast.makeText(SignupActivity.this, "올바른 이메일입니다", Toast.LENGTH_LONG).show();
                    flag_email = Boolean.TRUE;
                }else{
                    Toast.makeText(SignupActivity.this, "고려대학교 이메일만 사용할 수 있습니다", Toast.LENGTH_LONG).show();
                    flag_email = Boolean.FALSE;
                }
            }
        });

        // 버튼 클릭 시, 비밀번호 체크를 한다
        btn_pwd_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password = user_password.getText().toString();

                if(password.length() < 6){
                    Toast.makeText(SignupActivity.this, "6자리 이상의 비밀번호를 작성하세요!",
                            Toast.LENGTH_LONG).show();
                    flag_pwd = Boolean.FALSE;
                }else{
                    Toast.makeText(SignupActivity.this, "적당한 비밀번호입니다!",
                            Toast.LENGTH_LONG).show();
                    flag_pwd = Boolean.TRUE;
                }
            }
        });

        // 버튼 클릭 시, 이름을 확인한다
        btn_name_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_name.getText().toString().length()>0){
                    Toast.makeText(SignupActivity.this, "적당한 닉네임입니다!",
                            Toast.LENGTH_LONG).show();
                    flag_name = Boolean.TRUE;
                }else{
                    Toast.makeText(SignupActivity.this, "이름을 작성해주세요!",
                            Toast.LENGTH_LONG).show();
                    flag_name = Boolean.FALSE;
                }
            }
        });

        // 버튼 클릭 시, 학과을 확인한다
        btn_major_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_major.getText().toString().length()>0){
                    Toast.makeText(SignupActivity.this, "올바른 학과입니다!",
                            Toast.LENGTH_LONG).show();
                    flag_major = Boolean.TRUE;
                }else{
                    Toast.makeText(SignupActivity.this, "학과를 작성해주세요!",
                            Toast.LENGTH_LONG).show();
                    flag_major = Boolean.FALSE;
                }
            }
        });

        // 버튼을 클릭했을 시, 회원가입이 될 수 있도록 함
        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((flag_email==Boolean.TRUE)&&(flag_pwd==Boolean.TRUE)
                        &&(flag_name==Boolean.TRUE)&&(flag_major==Boolean.TRUE))==false) {
                    Toast.makeText(SignupActivity.this, "모든 정보를 체크해주세요!",
                            Toast.LENGTH_LONG).show();
                }else {
                    if ((flag_email == Boolean.TRUE) && (flag_pwd == Boolean.TRUE)) {
                        email = user_email.getText().toString();
                        password = user_password.getText().toString();
                        name = user_name.getText().toString();

                        // 중복 체크용 - 이메일
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            // 관련 정보를 firebase에 새로 데이터베이스를 만들어서 저장
                                            Hashtable<String, String> info
                                                    = new Hashtable<String, String>();
                                            info.put("name", name);
                                            info.put("my_confidence", "0");

                                            // user 이메일에서, 앞부분만 떼어 온다 (firebase 규칙때문)
                                            int idx_domain = email.indexOf("@");
                                            final String email_id = email.substring(0, idx_domain);

                                            // firebase 데이터베이스에 user 관련 정보를 저장해준다
                                            myRef = database.getReference("user").child(email_id);
                                            myRef.setValue(info);

                                            // 이메일 인증용
                                            mAuth.getCurrentUser().sendEmailVerification()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(SignupActivity.this, "인증 메일이 발송되었습니다",
                                                                        Toast.LENGTH_LONG).show();
                                                                Toast.makeText(SignupActivity.this, "인증을 완료 후 로그인해주세요",
                                                                        Toast.LENGTH_LONG).show();

                                                                // 유저 테이블에 전달할 내용들 저장
                                                                Hashtable<String, String> user_info
                                                                        = new Hashtable<String, String>();
                                                                user_info.put("my_name", user_name.getText().toString());
                                                                user_info.put("my_major", user_major.getText().toString());

                                                                /* 쓴 글을 데이터베이스에 기록해준다 */
                                                                // 글 자체를 저장해준다
                                                                myRef_usr = database.getReference("user").child(email_id).child("my_info");
                                                                myRef_usr.setValue(user_info);

                                                                // 일단 로그인 화면으로 보낸다
                                                                finish();
                                                            }
                                                        }
                                                    });
                                        } else {

                                            Toast.makeText(SignupActivity.this, "이미 가입되어 있는 회원입니다",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(SignupActivity.this, "이메일과 비밀번호 체크를 완료해 주세요",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    /* 중복 가입 방지를 위함 - 시작하자마자, 회원가입이 돼있는지 파악함 */
    @Override
    public void onStart() {
        super.onStart();
        // 해당 유저가 실제 회원인지 확인한다
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}
