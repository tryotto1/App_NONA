package org.techtown.practice.ExtraTabs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import org.techtown.practice.MainActivity;
import org.techtown.practice.R;

import java.util.Random;

public class SignupActivity extends AppCompatActivity {
    // firebase 회원가입용
    private FirebaseAuth mAuth;

    // 입력을 받도록 하기 위한 form
    private EditText user_email, user_password;
    private TextView tv_signup;
    private Button btn_email_verify, btn_pwd_check;
    ProgressBar progressBar;

    // 입력 받은 string 값을 담기 위한 변수
    String email, password;

    // 이메일 인증 번호
    Boolean flag_email = Boolean.FALSE;
    Boolean flag_pwd = Boolean.FALSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // firebase 회원가입용
        mAuth = FirebaseAuth.getInstance();

        // xml로부터 값들을 받아온다
        user_email = findViewById(R.id.user_email);
        user_password = findViewById(R.id.user_password);
        tv_signup = findViewById(R.id.tv_signup);
        btn_email_verify = findViewById(R.id.btn_email_verify);
        btn_pwd_check = findViewById(R.id.btn_pwd_check);
        progressBar = findViewById(R.id.progress);

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

        // 버튼을 클릭했을 시, 회원가입이 될 수 있도록 함
        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // progress bar 가 보이도록 한다
                progressBar.setVisibility(View.VISIBLE);

                if((flag_email==Boolean.TRUE)&&(flag_pwd==Boolean.TRUE)) {
                    email = user_email.getText().toString();
                    password = user_password.getText().toString();

                    // 중복 체크용 - 이메일
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // 이메일 인증용
                                        mAuth.getCurrentUser().sendEmailVerification()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        // progress bar 가 사라지도록 한다
                                                        progressBar.setVisibility(View.GONE);

                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(SignupActivity.this, "인증 메일이 발송되었습니다",
                                                                    Toast.LENGTH_LONG).show();
                                                            Toast.makeText(SignupActivity.this, "인증을 완료 후 로그인해주세요",
                                                                    Toast.LENGTH_LONG).show();

                                                            // 일단 로그인 화면으로 보낸다
                                                            finish();
                                                        }
                                                    }
                                                });
                                    } else {
                                        // progress bar 가 사라지도록 한다
                                        progressBar.setVisibility(View.GONE);

                                        Toast.makeText(SignupActivity.this, "이미 가입되어 있는 회원입니다",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(SignupActivity.this, "이메일과 비밀번호 체크를 완료해 주세요",
                            Toast.LENGTH_LONG).show();
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
