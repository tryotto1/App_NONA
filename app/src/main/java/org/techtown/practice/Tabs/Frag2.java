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

        return null;
    }
}
