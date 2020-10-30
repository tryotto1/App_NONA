package org.techtown.practice.Tabs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.techtown.practice.R;

public class Frag2 extends Fragment {
    private View view;

    public static Frag2 newInstance() {
        Frag2 frag2 = new Frag2();
        return frag2;
    }

    // keyword 시를 채워준다
    TextView tv_title, tv_content, tv_writer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag2, container, false);

        // xml이랑 연결하기
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_content = (TextView) view.findViewById(R.id.tv_content);
        tv_writer = (TextView) view.findViewById(R.id.tv_writer);

        /* fragment가 전환될때마다 서버와 불필요하게 추가적인 통신을 하는걸 방지하기 위해
           shared preference를 활용함 */
        // 키워드 가져오기
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("keyword", getActivity().MODE_PRIVATE);
        String keyword = sharedPreferences.getString("keyword", "키워드");

        // 키워드 관련 시 가져오기
        SharedPreferences sharedPreferences2 = getActivity().getSharedPreferences("keyword_poem", getActivity().MODE_PRIVATE);
        String keyword_poem = sharedPreferences2.getString("keyword_poem", "키워드");

        // 해당 시인 가져오기
        SharedPreferences sharedPreferences3 = getActivity().getSharedPreferences("keyword_poet", getActivity().MODE_PRIVATE);
        String keyword_poet = sharedPreferences3.getString("keyword_poet", "키워드");
        Log.d("frag2 로그  :  ", "  "+keyword + "  "+ keyword_poem + "  "+keyword_poet);

        tv_title.setText(keyword);
        tv_content.setText(keyword_poem);
        tv_writer.setText(keyword_poet);

        return view;
    }
}
