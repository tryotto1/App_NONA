package org.techtown.practice.ExtraTabs;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.practice.MyWritings_recycler.MyWritingsAdapter;
import org.techtown.practice.MyWritings_recycler.MyWritingsData;
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
import java.util.ArrayList;

public class sub_MyWritingsActivity extends AppCompatActivity {
    private View view;
    private TextView tv_userid, tv_title;
    private String title;

    // tab3 recyclerview에서 사용
    private ArrayList<MyWritingsData> arrayList;
    private MyWritingsAdapter myWritingsAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ImageView iv_back;

    // 내가 쓴 모든 시들을 받아주기 위함
    JSONArray my_all_poem_jsonArr;

    // 현재 내 이메일 값 - db 에 저장하기 위함
    String writer_email;
    String cur_name;

    public static sub_MyWritingsActivity newInstance() {
        sub_MyWritingsActivity fragWednesday = new sub_MyWritingsActivity();
        return fragWednesday;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writings);

        // 쓴 사람 이메일 가져오기
        Bundle extras = getIntent().getExtras();
        writer_email = extras.getString("writer");
//        title = extras.getString("title");

        // 쓴 사람(=나) 필명 가져오기
        SharedPreferences sharedPreferences_name = getSharedPreferences("cur_name",MODE_PRIVATE);
        cur_name = sharedPreferences_name.getString("cur_name", "여기서도 안 됐음");

        tv_title = findViewById(R.id.tv_title);
        tv_userid = findViewById(R.id.tv_userid);
        tv_userid.setText(cur_name);
//        tv_title.setText(title);

        Log.d("플래그 값 되는거니? : ", writer_email);
        if(writer_email.equals("flag_my")){
            // 내 이메일 가져오기
            SharedPreferences sharedPreferences = getSharedPreferences("cur_email",MODE_PRIVATE);
            writer_email = sharedPreferences.getString("cur_email", "여기서도 안 됐음");
        }

        //tab3 recyclerview
        recyclerView = (RecyclerView)findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();

        myWritingsAdapter = new MyWritingsAdapter(arrayList);
        recyclerView.setAdapter(myWritingsAdapter);

        // 내가 쓴 시 리스트 가져오기
//        new get_my_poems().execute("http://192.168.123.1:8080/poem_my_all_list/");

        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // 내 모든 시 들을 가져온다
    public class get_my_poems extends AsyncTask<String, String, String> {
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

                    String tmp = "{\"my_email\":\""+writer_email+"\"}";
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
                    my_all_poem_jsonArr = tmp_json.getJSONArray("my_every_poem");

                    Log.d("json 어레이 ", "doInBackground: >>>>>>>>>" + my_all_poem_jsonArr);

                    // jsonArray 에 있는 모든 내용물들을 리사이클러 뷰에 담아준다
                    for (int i = 0; i < my_all_poem_jsonArr.length(); i++) {
                        try {
                            JSONObject poem = my_all_poem_jsonArr.getJSONObject(i);

                            Log.d("시 하나하나 직접 쓴거임", "doInBackground: >>>>>>>>>>>>" + poem);

                            MyWritingsData tab3Data = new MyWritingsData(poem.getString("poem_title"), poem.getString("poem"), poem.getString("poem_tag"), "2020년 08월 01일, 밤", cur_name, "");

                            Log.d("???", "doInBackground: " + poem.getString("poem_title"));
                            Log.d("???", "doInBackground: " + poem.getString("poem"));
                            Log.d("???", "doInBackground: " + poem.getString("poem_tag"));

                            arrayList.add(tab3Data);

                            sub_MyWritingsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myWritingsAdapter.notifyDataSetChanged();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
