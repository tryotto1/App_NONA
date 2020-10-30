package org.techtown.practice.ExtraTabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.techtown.practice.MainActivity;
import org.techtown.practice.R;

/* 통신용 */
import org.json.JSONObject;
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

public class SignupActivity extends AppCompatActivity {

    // 입력을 받도록 하기 위한 form
    private EditText et_id, et_password, et_name;
    private TextView tv_signup;

    // 입력 받은 string 값을 담기 위한 변수
    String id, password, name;
    String my_writer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        et_id = findViewById(R.id.et_id);
        et_password = findViewById(R.id.et_password);
        et_name = findViewById(R.id.et_name);
        tv_signup = findViewById(R.id.et_name);

        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = et_id.getText().toString();
                password = et_password.getText().toString();
                name = et_name.getText().toString();

                // 회원가입 요청 보내기
//                new signin_form().execute("http://192.168.123.1:8080/signin/");

                Boolean success = true;
                // 서버 통신 -> 아이디가 존재하는지 확인
                if (success) {
                    // 회원가입 한 이메일을 임시로 저장하기 위함
                    SharedPreferences sharedPreferences = getSharedPreferences("cur_email", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("cur_email", id);
                    editor.commit();
                    Log.d("1111", "되라 제발");
                    // 필명 가져오기
//                    new get_name().execute("http://192.168.123.1:8080/get_name/");
                    Log.d("22222", "되라 제발");
                    Toast.makeText(view.getContext(), "성공적으로 계정을 생성하였습니다..", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(view.getContext(), "존재하지 않는 계정입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // 전달받은 이메일을 이용해서, 필명을 가져온다
    public class get_name extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]);

                    // 연결을 해준다
                    con = (HttpURLConnection) url.openConnection();

                    // 연결 설정해주기
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Cache-Control", "no-cache");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "text/html");
                    con.setDoOutput(true);
                    con.connect();

                    // 서버로 보낼 스트림 -> 이걸 이용한 버퍼 생성
                    OutputStream outStream = con.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));

                    String tmp = "{\"my_email\":\""+id+"\"}";
                    writer.write(tmp);
                    Log.d("잘 되니3333? tmp 값", "doInBackground: >>>>>>>>>>>>>>>" + tmp);
                    writer.flush();
                    writer.close();

                    // 서버로부터 데이터 받음 -> 이걸 이용한 버퍼 생성
                    InputStream stream = con.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));

                    // 결과로 리턴 될 버퍼
                    StringBuffer buffer = new StringBuffer();

                    // 버퍼 리더로부터 문자열을 받은걸 결과 버퍼에 담는다
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    JSONObject tmp_json = new JSONObject(buffer.toString());

                    Log.d("json 어레이 ", "doInBackground: >>>>>>>>>" + tmp_json.getString("my_name"));

                    my_writer = tmp_json.getString("my_name");

                    // 로그인 된 필명을 임시로 저장하기 위함
                    SharedPreferences sharedPreferences_name = getSharedPreferences("cur_name",MODE_PRIVATE);
                    SharedPreferences.Editor editor_name = sharedPreferences_name.edit();
                    editor_name.putString("cur_name", my_writer);
                    Log.d("됨!", "" + my_writer);
                    editor_name.commit();

                    // 결과 버퍼 내용을 문자열로 바꿔서 리턴한다
                    return buffer.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally
                {
                    if (con != null) {
                        con.disconnect();
                    }
                    try
                    {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    // 회원 가입을 위한 서버 연동
    public class signin_form extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]);

                    // 연결을 해준다
                    con = (HttpURLConnection) url.openConnection();

                    // 연결 설정해주기
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Cache-Control", "no-cache");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "text/html");
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    con.connect();

                    // 서버로 보낼 스트림 -> 이걸 이용한 버퍼 생성
                    OutputStream outStream = con.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));

                    String tmp = "{\"my_email\":\""+id+"\", \"my_pwd\": \"" + password + "\", \"my_name\" : \""+name+"\"}";
                    writer.write(tmp);

                    Log.d("잘 되니? tmp 값", "doInBackground: >>>>>>>>>>>>>>>" + tmp);

                    writer.flush();
                    writer.close();

                    // 서버로부터 데이터 받음 -> 이걸 이용한 버퍼 생성
                    InputStream stream = con.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));

                    // 결과로 리턴 될 버퍼
                    StringBuffer buffer = new StringBuffer();

                    // 버퍼 리더로부터 문자열을 받은걸 결과 버퍼에 담는다
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    JSONObject tmp_json = new JSONObject(buffer.toString());
                    String signin_flag = tmp_json.getString("flag");

                    if(signin_flag.equals("true")){
                        Log.d("회원가입? ", "doInBackground: >>>>>> 회원가입 성공!");

                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                        SignupActivity.this.startActivity(intent);
                        finish();
                    }else{
                        SignupActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(SignupActivity.this,  "이미 존재하는 이메일입니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.d("회원가입? ", "doInBackground: >>>>>> 회원가입 실패 ㅠㅠ");
                    }

                    // 결과 버퍼 내용을 문자열로 바꿔서 리턴한다
                    return buffer.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally
                {
                    if (con != null) {
                        con.disconnect();
                    }
                    try
                    {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}
