package org.techtown.practice.Tabs;

import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.techtown.practice.ExtraTabs.Tab1_WriteActivity;
import org.techtown.practice.R;

import org.techtown.practice.tab1_recycler.Tab1Adapter;
import org.techtown.practice.tab1_recycler.Tab1Data;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Frag1 extends Fragment {
    private View view;
    private FloatingActionButton btn_write;

    /* recycler view */
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    private ArrayList<Tab1Data> arrayList;
    private Tab1Adapter tab1Adapter;

    // ViewPagerAdapter를 위해서
    public static Frag1 newInstance() {
        Frag1 frag1 = new Frag1();
        return frag1;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag1, container, false);
        btn_write = view.findViewById(R.id.btn_write);

        /*recycler view*/
        recyclerView = view.findViewById(R.id.frag1_recycle);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();
        tab1Adapter = new Tab1Adapter(arrayList);
        recyclerView.setAdapter(tab1Adapter);

        // 임시로 일단 여러 게시물들을 만들어 둔다
        Tab1Data tmp = new Tab1Data("title","content","date","writer", "email", 1);
        arrayList.add(tmp);
        arrayList.add(tmp);
        arrayList.add(tmp);
        tab1Adapter.notifyDataSetChanged();

        /* button - 새로운 게시물 작성하기 */
        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 글 쓰는 activity로 넘겨준다
                Intent intent = new Intent(getActivity(), Tab1_WriteActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
