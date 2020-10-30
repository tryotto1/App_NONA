package org.techtown.practice.ExtraTabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.techtown.practice.R;

import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.concurrent.ExecutionException;

public class Tab1_WriteActivity extends AppCompatActivity {

    // 입력 받을 수 있는 객체
    TextView tv_save, tv_ai_poem;
    EditText et_write, et_title;
    private ImageView iv_back;

    // 객체로부터 입력 받은 string 값
    String writing, title, writer;

    // 현재 내 이메일 값 - db 에 저장하기 위함
    String cur_email;

    // AI로부터 시를 받아주기 위함
    String ai_contet = "";
    String content;
    JSONArray ai_poem_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        // 내 이메일 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("cur_email",MODE_PRIVATE);
        cur_email = sharedPreferences.getString("cur_email", "여기서도 안 됐음");

        tv_save = findViewById(R.id.tv_save);
        et_title = findViewById(R.id.et_title);
        et_write = findViewById(R.id.et_write);
        iv_back = findViewById(R.id.iv_back);
        tv_ai_poem = findViewById(R.id.tv_ai_poem);
//        tv_topic.setText();
         // intent에서 받은걸로 설정하셈

        tv_ai_poem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content = et_write.getText().toString();

                // AI에게 시를 보내준다
//                try {
//                    new get_ai_poem().execute("http://192.168.123.1:8080/get_ai_poem/").get();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
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
                title = et_title.getText().toString();
                writing = et_write.getText().toString();

                Log.d("시 작성하기", "onClick: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>??");
//                try {
//                    new send2server().execute("http://192.168.123.1:8080/write_poem/").get();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                Intent intent = new Intent(getApplicationContext(), Tab_ShowWrittenActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("writing", writing);
                intent.putExtra("writer", writer);

                startActivity(intent);
                finish();
            }
        });
    }

    // AI에게 키워드를 보낸 뒤, 시를 가져온다
    public class get_ai_poem extends AsyncTask<String, String, String> {
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

                    String tmp = "{\"keyword\":\""+content+"\"}";
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
                    ai_poem_list = tmp_json.getJSONArray("ai_poem");

                    Log.d("json 어레이 ", "doInBackground: >>>>>>>>>" + ai_poem_list);

                    // jsonArray 에 있는 모든 내용물들을 리사이클러 뷰에 담아준다
                    for (int i = 0; i < ai_poem_list.length(); i++) {
                        try {
                            Log.d("여기 시 썼다 AI : ", "" + ai_poem_list.getString(i));

                            ai_contet += ai_poem_list.getString(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    et_write.setText(ai_contet);
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

    // 내가 쓴 시를 서버에 전달해준다
    public class send2server extends AsyncTask<String, String, String> {
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

                    String tmp = "{\"my_email\":\""+cur_email+"\", \"poem_title\":\""+ title + "\", \"poem\":\"" + writing + "\", \"writer\": \"" + writer + "\"}";
                    writer.write(tmp);
                    Log.d("\"JSONTask6 에러 찾기\"", "doInBackground: 또 왜>>>>>>>>>>>>>>>" + tmp);
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