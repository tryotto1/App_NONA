package org.techtown.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import org.techtown.practice.SubTab_Drawer.FollowingActivity;
import org.techtown.practice.SubTab_Drawer.DibActivity;
import org.techtown.practice.Login_Signin.LoginActivity;
import org.techtown.practice.SubTab_Drawer.MyWritingsActivity;
import org.techtown.practice.SubTab_Tab1.ProfileActivity;
import org.techtown.practice.Tabs.ViewPagerAdapter;
import org.techtown.practice.recycler_Dib.DibData;
import org.techtown.practice.recycler_tab1.Tab1Data;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //splash에 사용
    private static int SPLASH_TIME = 1;

    // 3 fragement - viewpager에 사용
    private FragmentPagerAdapter fragmentPagerAdapter;

    // drawer를 받아줄 수 있도록 하는 레이아웃 - 이 어플에선 activity_main 으로 선언됨
    private DrawerLayout drawerLayout;

    // 왼쪽 drawer에 사용
    private View drawerView;

    // 현재 내 이메일을 받아오기 위함
    String my_email, my_id;

    /* Firebase */
    // 사진 Uri 가져오기 위한 firebase
    StorageReference mStorageRef;
    StorageReference picture_Ref;

    // firebase 데이터베이스를 가져온다
    FirebaseDatabase database;

    // Dib List를 전달하기 위한 리스트
    ArrayList<String> List_Dib;
    String str_list_dib = "", str_list_write = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // firebase 데이터베이스 연결
        database = FirebaseDatabase.getInstance();

        // Dib List 초기화
        List_Dib = new ArrayList<>();

        // 왼쪽 drawer 를 위한 코드
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerView = (View)findViewById(R.id.drawer);

        // 현재 내 이메일 가져오기
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        my_email = pref.getString("email", "");

        int idx_domain = my_email.indexOf("@");
        my_id = my_email.substring(0, idx_domain);


        // 왼쪽 drawer menu 여는 코드
        ImageButton btn_open = (ImageButton)findViewById(R.id.btn_open);
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        /* 왼쪽 bar의 각 버튼을 클릭할때마다, 대응되는 activity로 이동한다 */
        LinearLayout my_writing = (LinearLayout) findViewById(R.id.my_writing);
        LinearLayout my_like = (LinearLayout) findViewById(R.id.my_like);
        LinearLayout my_profile = (LinearLayout) findViewById(R.id.my_profile);
        LinearLayout exit = (LinearLayout) findViewById(R.id.exit);

        // 내가 쓴 글 목록
        my_writing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 모든 shared preference 저장값들을 삭제한다 - 부정 로그인 방지
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("str_list_write", str_list_write);
                editor.commit();

                // my write activity로 가기
                Intent intent = new Intent(getApplicationContext(), MyWritingsActivity.class);
                startActivity(intent);
            }
        });

        // 내가 대여한 목록
        my_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 모든 shared preference 저장값들을 삭제한다 - 부정 로그인 방지
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("str_list_dib", str_list_dib);
                editor.commit();

                // my dib activity로 가기
                Intent intent = new Intent(getApplicationContext(), DibActivity.class);
                startActivity(intent);
            }
        });

        // 내 프로필
        my_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        // 로그아웃
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);

                finish();
            }
        });

        // firebase에 올라온 글 내용을 실시간으로 업데이트 해주는 listener
        ChildEventListener childEventListener_Dib_List = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                String dib_idx = dataSnapshot.getValue().toString();

                str_list_dib += (dib_idx+",");
                Log.d("dib_idx", "onChildAdded: "+str_list_dib);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Tab1Data writing = dataSnapshot.getValue(Tab1Data.class);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String commentKey = dataSnapshot.getKey();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        database.getReference("user").child(my_id).child("my_dib").addChildEventListener(childEventListener_Dib_List);

        // firebase에 올라온 글 내용을 실시간으로 업데이트 해주는 listener
        ChildEventListener childEventListener_Write_List = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                String write_idx = dataSnapshot.getValue().toString();

                str_list_write += (write_idx+",");
                Log.d("dib_idx", "onChildAdded: " + str_list_write);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Tab1Data writing = dataSnapshot.getValue(Tab1Data.class);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String commentKey = dataSnapshot.getKey();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        database.getReference("user").child(my_id).child("my_write").addChildEventListener(childEventListener_Write_List);

        /* 왼쪽 drawer에 대한 설정을 해줌 */
        drawerLayout.setDrawerListener(listener);
        drawerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        /* navigation bar 아랫부분 - viewpager, tab layout 세팅 */
        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        fragmentPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }



    /* 왼쪽 drawer를 열고 닫을 수 있도록 하는 listener
        - 버튼 클릭 이외에도 자연스럽게 열 수 있도록 하기 위함*/
    DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
        @Override
        // slide 했을때 호출됨.
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

        }
        // 열었을때 호출됨.
        @Override
        public void onDrawerOpened(@NonNull View drawerView) {

        }
        // 닫혔을 때 호출됨.
        @Override
        public void onDrawerClosed(@NonNull View drawerView) {

        }
        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };
}
