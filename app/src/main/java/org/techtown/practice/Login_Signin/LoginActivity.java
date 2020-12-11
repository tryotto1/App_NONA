package org.techtown.practice.Login_Signin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private TextView tv_signup, lost_pwd;
    private Button btn_login;

    // 로그인용 firebase
    private FirebaseAuth mAuth;

    // 로그인 하기 위해 받은 string 값
    String email, password;
    String my_email, my_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 현재 내 이메일 가져오기
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        my_email = pref.getString("email", "");

        // 저장된 로그 기록이 있을 경우, 바로 메인 함수로 넘어간다
        if(my_email.equals("") == false){
            Toast.makeText(LoginActivity.this, "안녕하세요!",
                    Toast.LENGTH_LONG).show();

            // 메인 화면으로 보낸다
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(intent);
            finish();
        }

        // 로그인용 파이어베이스 인스턴스 가져오기
        mAuth = FirebaseAuth.getInstance();

        // xml 연결
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        tv_signup = findViewById(R.id.tv_signup);
        lost_pwd = findViewById(R.id.lost_pwd);

        // 로그인 버튼
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = et_email.getText().toString();
                password = et_password.getText().toString();

                // 로그인 시도를 한다
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

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

        // 비밀번호 분실 버튼
        lost_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.sendPasswordResetEmail(et_email.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this, "해당 이메일 주소로 메일을 보냈습니다",
                                            Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(LoginActivity.this, "해당 이메일 주소는 가입되지 않은 주소입니다",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }
}
