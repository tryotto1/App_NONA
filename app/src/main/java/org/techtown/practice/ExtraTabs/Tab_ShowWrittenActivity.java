package org.techtown.practice.ExtraTabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.techtown.practice.R;

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

public class Tab_ShowWrittenActivity extends AppCompatActivity {

    TextView tv_title, tv_content, tv_writer, tv_date;
    ImageView iv_heart, iv_back, iv_follow;
    Boolean is_like, is_follow;

    // 현재 내 이메일 값 - db 에 저장하기 위함
    String cur_email, cur_name;

    // 팔로우 하려는 유저 이메일 값
    String follow_email;

    // 좋아요 하려는 시 id
    int id_poem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_writing);

        // 내 이메일 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("cur_email",MODE_PRIVATE);
        cur_email = sharedPreferences.getString("cur_email", "여기서도 안 됐음");

        tv_title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        tv_writer = findViewById(R.id.tv_writer);
        tv_date = findViewById(R.id.tv_date);

        // 시, 제목 가져오기
        Bundle extras = getIntent().getExtras();
        String writings = extras.getString("writing");
        String title = extras.getString("title");
        String writer_name = extras.getString("writer");
        String date = extras.getString("date");
        follow_email = extras.getString("email");
        id_poem = extras.getInt("id_poem");

        Log.d("글 쓴 사람 필명", "" + writer_name + "  글쓴이 이메일 : " + follow_email);

        tv_content.setText(writings);
        tv_title.setText(title);
        tv_writer.setText(writer_name);
        tv_date.setText(date);

        // 누르면 좋아요 올라가기
        iv_heart = findViewById(R.id.iv_heart);
        is_like = false; // 서버에서 받아와야 함.
        setHeartImage(is_like);

        // 좋아요 추가해주기
        iv_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                is_like = (is_like? false: true);
                setHeartImage(is_like);

//                new add_likes().execute("http://192.168.123.1:8080/like_poem/");//AsyncTask 시작시
            }
        });

        is_follow = false;
        // 팔로우 추가하기
        iv_follow = findViewById(R.id.iv_follow);
        iv_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                is_follow = (is_follow? false: true);
                setFollowImage(is_follow);

//                new add_follow().execute("http://192.168.123.1:8080/new_follow/");//AsyncTask 시작시
            }
        });

        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setHeartImage(Boolean is_like) {
        if (is_like)
            iv_heart.setImageResource(R.drawable.heart_red);
        else
            iv_heart.setImageResource(R.drawable.heart);
    }

    private void setFollowImage(Boolean is_follow) {
        if (is_follow)
            iv_follow.setImageResource(R.drawable.followed);
        else
            iv_follow.setImageResource(R.drawable.follow);
    }

    // 좋아요를 추가해준다
    public class add_likes extends AsyncTask<String, String, String> {
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

                    String tmp = "{\"my_email\":\""+cur_email+"\", \"like_poem_id\": \""+ String.valueOf(id_poem)+"\"}";
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

    // 팔로우 추가해준다
    public class add_follow extends AsyncTask<String, String, String> {
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

                    String tmp = "{\"my_email\":\""+cur_email+"\", \"following_email\": \"" + follow_email + "\"}";
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