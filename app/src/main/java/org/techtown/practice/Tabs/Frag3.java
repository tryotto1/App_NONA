package org.techtown.practice.Tabs;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.techtown.practice.tab3_recycler.Tab3Adapter;
import org.techtown.practice.tab3_recycler.Tab3Data;
import org.techtown.practice.R;

//import org.json.JSONArray;
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

public class Frag3 extends Fragment {
    private View view;

    // tab3 recyclerview에서 사용
    private ArrayList<Tab3Data> arrayList;
    private Tab3Adapter tab3Adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    // 모든 시 를 json array 안에 담아온다
    JSONArray follow_poem_jsonArr;

    // 현재 내 이메일 값 - db 에 저장하기 위함
    String cur_email;

    public static Frag3 newInstance() {
        Frag3 fragWednesday = new Frag3();
        return fragWednesday;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag3, container, false);

        // 내 이메일 가져오기
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("cur_email",getActivity().MODE_PRIVATE);
        cur_email = sharedPreferences.getString("cur_email", "여기서도 안 됐음");

        //tab3 recyclerview
        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();

        tab3Adapter = new Tab3Adapter(arrayList);
        recyclerView.setAdapter(tab3Adapter);

        // 서버에 있는 모든 시 를 가져온다
//        new get_poem_every().execute("http://192.168.0.14:8000/poem_every/");

        return view;
    }

    // 시 를 array에 저장해준다
    public class get_poem_every extends AsyncTask<String, String, String> {
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

                    String tmp = "{\"my_email\":\""+cur_email+"\"}";
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

                    Log.d("받은 내용>>>>>>>>>>>>>>>", "doInBackground: " + buffer.toString());

                    JSONObject tmp_json = new JSONObject(buffer.toString());
                    follow_poem_jsonArr = tmp_json.getJSONArray("every_poem");

                    Log.d("json 어레이 ", "doInBackground: >>>>>>>>>" + follow_poem_jsonArr);

                    // jsonArray 에 있는 모든 내용물들을 리사이클러 뷰에 담아준다
                    for (int i = 0; i < follow_poem_jsonArr.length(); i++) {
                        try {
                            JSONObject poem = follow_poem_jsonArr.getJSONObject(i);

                            Log.d("시 하나하나 유저가 쓴 거", "doInBackground: >>>>>>>>>>>>" + poem);
                            Tab3Data tab3Data = new Tab3Data(poem.getString("poem_title"), poem.getString("poem"), poem.getString("poem_date") , poem.getString("user_name"), poem.getString("user_email"), poem.getInt("poem_id"));

                            Log.d("???", "doInBackground: " + poem.getString("poem_title"));
                            Log.d("???", "doInBackground: " + poem.getString("poem"));
                            Log.d("???", "doInBackground: " + poem.getString("poem_date"));
                            Log.d("???", "doInBackground: " + poem.getString("user_name"));
                            Log.d("???", "doInBackground: " + poem.getString("user_email"));

                            arrayList.add(tab3Data);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tab3Adapter.notifyDataSetChanged();
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
