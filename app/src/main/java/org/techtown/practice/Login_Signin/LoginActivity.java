package org.techtown.practice.Login_Signin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.techtown.practice.MainActivity;
import org.techtown.practice.R;

public class LoginActivity extends AppCompatActivity {
    private EditText et_email, et_password;
    private TextView tv_login, tv_signup;
    ProgressBar progressBar;

    // 로그인용 firebase
    private FirebaseAuth mAuth;

    // 로그인 하기 위해 받은 string 값
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 모든 shared preference 저장값들을 삭제한다 - 부정 로그인 방지
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("email");
        editor.commit();

        // 로그인용 파이어베이스 인스턴스 가져오기
        mAuth = FirebaseAuth.getInstance();

        // xml 연결
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        tv_login = findViewById(R.id.tv_login);
        tv_signup = findViewById(R.id.tv_signup);
        progressBar = findViewById(R.id.progress);

        // 로그인 버튼
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = et_email.getText().toString();
                password = et_password.getText().toString();

                // progress bar 가 보이도록 한다
                progressBar.setVisibility(View.VISIBLE);

                // 로그인 시도를 한다
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // progress bar 가 사라지게 한다
                                progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {

                                    // 학교 이메일 인증이 완료됐을 경우만 로그인 가능
                                    if (mAuth.getCurrentUser().isEmailVerified()) {
                                        Toast.makeText(LoginActivity.this, "안녕하세요!",
                                                Toast.LENGTH_LONG).show();

                                        // 로그인 된 이메일을 임시 저장한다
                                        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putString("email", mAuth.getCurrentUser().getEmail());
                                        editor.commit();

                                        // 인증이 완료되었으므로, 메인 화면으로 보낸다
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        LoginActivity.this.startActivity(intent);
                                        finish();
                                    } else {
                                        // 완료가 덜 됐으므로 입장 불가능
                                        Toast.makeText(LoginActivity.this, "인증을 완료하여 주세요",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                                else {
                                    // 비밀번호가 틀렸으므로, 입장 불가능하다
                                    Toast.makeText(LoginActivity.this, "이메일 또는 비밀번호를 확인하세요",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        // 회원가입 버튼
        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SignupActivity.class);
                view.getContext().startActivity(intent);
            }
        });
    }
}
