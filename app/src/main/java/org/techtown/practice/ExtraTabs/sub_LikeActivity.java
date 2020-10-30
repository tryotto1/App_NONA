package org.techtown.practice.ExtraTabs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.techtown.practice.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.techtown.practice.Like_recycler.LikeAdapter;
import org.techtown.practice.Like_recycler.LikeData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class sub_LikeActivity extends AppCompatActivity {
    private View view;
    private TextView tv_userid, tv_title;
    private String title;

    // tab3 recyclerview에서 사용
    private ArrayList<LikeData> arrayList;
    private LikeAdapter LikeAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ImageView iv_back;

    // 내가 쓴 모든 시들을 받아주기 위함
    JSONArray poem_id_jsonArr;

    // 현재 내 이메일 값 - db 에 저장하기 위함
    String writer_email;
    String cur_name, cur_email;

    // 시 리스트 보내기 위함
    String poem_id_arr;
    String tmp_id;

    // 시 리스트 arraylist
    ArrayList<String> id_list = new ArrayList<String>();

    public static sub_LikeActivity newInstance() {
        sub_LikeActivity fragWednesday = new sub_LikeActivity();
        return fragWednesday;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writings);

        // 내 이메일 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("cur_email", MODE_PRIVATE);
        cur_email = sharedPreferences.getString("cur_email", "여기서도 안 됐음");

        // 쓴 사람(=나) 필명 가져오기
        SharedPreferences sharedPreferences_name = getSharedPreferences("cur_name",MODE_PRIVATE);
        cur_name = sharedPreferences_name.getString("cur_name", "여기서도 안 됐음");

        tv_title = findViewById(R.id.tv_title);
        tv_userid = findViewById(R.id.tv_userid);
        tv_userid.setText(cur_name);

        //tab3 recyclerview
        recyclerView = (RecyclerView)findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();

        LikeAdapter = new LikeAdapter(arrayList);
        recyclerView.setAdapter(LikeAdapter);

        Log.d("asd", "11111111");

        // 내가 쓴 시 리스트 가져오기
//        try {
//            new like_poem_get().execute("http://192.168.123.1:8080/like_poem_get/").get();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        Log.d("asd", "22222222");

        // 쓴 시의 id에 대응해서, 시를 직접 가져오기
        for(int i=0;i<id_list.size();i++){
//            try {
//                tmp_id = id_list.get(i);
//                Log.d("여기 인덱스 : ", "" + tmp_id);
//                new poem_get_one().execute("http://192.168.123.1:8080/poem_get_one/").get();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }

        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // 좋아요 한 글들 모두 가져오기
    public class like_poem_get extends AsyncTask<String, String, String> {
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

                    String tmp = "{\"my_email\":\""+cur_email+"\"}";
                    writer.write(tmp);
                    Log.d("잘 되니-좋아요? tmp 값", "doInBackground: >>>>>>>>>>>>>>>" + tmp);
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
                    JSONArray poem_id_arr = tmp_json.getJSONArray("poem_id_list");

                    for(int i=0;i<poem_id_arr.length();i++){
                        Log.d("json 어레이 ", "doInBackground: >>>>>>>>>" + poem_id_arr.getString(i));

                        // id 목록을 리스트에 담는다
                        id_list.add(poem_id_arr.getString(i));
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


    // 시 하나 가져오기
    public class poem_get_one extends AsyncTask<String, String, String> {
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

                    String tmp = "{\"poem_id\":\""+tmp_id+"\"}";
                    writer.write(tmp);
                    Log.d("잘 되니-시 하나 가져오기? tmp 값", "doInBackground: >>>>>>>>>>>>>>>" + tmp);
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
                    String tmp_poem_title = tmp_json.getString("poem_title");
                    String tmp_poem =  tmp_json.getString("poem");
                    String tmp_my_email = tmp_json.getString("my_email");

                    LikeData likeData = new LikeData(tmp_poem_title, tmp_poem, "", "", tmp_my_email,"");
                    //String title, String content, String tag, String date, String writer, String id
                    arrayList.add(likeData);

                    sub_LikeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LikeAdapter.notifyDataSetChanged();
                        }
                    });

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
