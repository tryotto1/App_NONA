package org.techtown.practice.Tabs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.practice.SubTab_Drawer.MyWritingsActivity;
import org.techtown.practice.SubTab_Tab1.ProfileActivity;
import org.techtown.practice.SubTab_Tab1.SearchActivity;
import org.techtown.practice.SubTab_Tab1.WriteActivity;
import org.techtown.practice.R;

import org.techtown.practice.SubTab_Tab1.WriteDonateActivity;
import org.techtown.practice.recycler_tab1.Tab1Adapter;
import org.techtown.practice.recycler_tab1.Tab1Data;
import org.techtown.practice.recycler_tab1_donate.Tab1DonateAdapter;
import org.techtown.practice.recycler_tab1_donate.Tab1DonateData;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Frag1 extends Fragment {
    private View view;
    private ImageView btn_write, btn_search, menu_drawer;
    private EditText search_txt;

    // 이메일, 아이디
    String my_email,my_id;

    // 프로필 사진
    CircleImageView img_profile;

    // 사진 Uri 가져오기 위한 firebase
    StorageReference mStorageRef;
    StorageReference picture_Ref;

    // firebase 데이터베이스를 가져온다
    FirebaseDatabase database;

    /* Tab Layout용 */
    private TabLayout tabLayout_recycle;
    int pos;

    /* recycler view */
    RecyclerView recyclerView, recyclerView2;
    LinearLayoutManager linearLayoutManager, linearLayoutManager2;
    private ArrayList<Tab1Data> arrayList;
    private ArrayList<Tab1DonateData> arrayList_donate;
    private Tab1Adapter tab1Adapter;
    private Tab1DonateAdapter tab1DonateAdapter;

    // ViewPagerAdapter를 위해서
    public static Frag1 newInstance() {
        Frag1 frag1 = new Frag1();
        return frag1;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /* xml 연결 */
        view = inflater.inflate(R.layout.frag1, container, false);
        btn_write = view.findViewById(R.id.btn_write);
        recyclerView = view.findViewById(R.id.frag1_recycle);
        recyclerView2 = view.findViewById(R.id.frag1_recycle2);
        img_profile = view.findViewById(R.id.img_profile);
        search_txt = view.findViewById(R.id.search_frag1);
        btn_search = view.findViewById(R.id.btn_search);

        // firebase 데이터베이스 연결
        database = FirebaseDatabase.getInstance();

        // 사진을 저장하기 위한 레퍼런스 - 업로드
        mStorageRef = FirebaseStorage.getInstance().getReference();

        /* 태그 검색용 연결 */
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 태그 검색 값을 가져오기
                String str_search = search_txt.getText().toString();

                // my write activity로 가기
                Intent intent = new Intent(Frag1.this.getContext(), SearchActivity.class);
                intent.putExtra("tag_value", str_search);
                startActivity(intent);
            }
        });

        /*recycler view*/
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();
        tab1Adapter = new Tab1Adapter(arrayList, getContext());
        recyclerView.setAdapter(tab1Adapter);

        /*recycler view 2 - 나눔 */
        linearLayoutManager2 = new LinearLayoutManager(getContext());
        recyclerView2.setLayoutManager(linearLayoutManager2);

        arrayList_donate = new ArrayList<>();
        tab1DonateAdapter = new Tab1DonateAdapter(arrayList_donate, getContext());
        recyclerView2.setAdapter(tab1DonateAdapter);


        /* button - 새로운 게시물 작성하기 */
        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pos == 0){
                    // 글 쓰는 activity로 넘겨준다
                    Intent intent = new Intent(getActivity(), WriteActivity.class);
                    startActivity(intent);
                }else{
                    // 글 쓰는 activity로 넘겨준다
                    Intent intent = new Intent(getActivity(), WriteDonateActivity.class);
                    startActivity(intent);
                }

            }
        });

        /* 프로필 사진 누를때 프로필로 화면 이동 */
        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getContext().getSharedPreferences("pref", getContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("str_list_chat", "");
                editor.commit();

                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        /* 탭 연결 설정 */
        tabLayout_recycle = (TabLayout) view.findViewById(R.id.tab_layout_two) ;
        tabLayout_recycle.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // TODO : process tab selection event.
                pos = tab.getPosition() ;

                switch (pos) {
                    case 0 :
                        view.findViewById(R.id.frag1_recycle).setVisibility(View.VISIBLE) ;
                        view.findViewById(R.id.frag1_recycle2).setVisibility(View.INVISIBLE) ;
                        break ;
                    case 1 :
                        view.findViewById(R.id.frag1_recycle).setVisibility(View.INVISIBLE) ;
                        view.findViewById(R.id.frag1_recycle2).setVisibility(View.VISIBLE) ;
                        break ;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // do nothing
            }
        }) ;

        /* 프로필 사진 올리기 */
        // 현재 내 이메일 가져오기
        SharedPreferences pref = getContext().getSharedPreferences("pref", getContext().MODE_PRIVATE);
        my_email = pref.getString("email", "");

        int idx_domain = my_email.indexOf("@");
        my_id = my_email.substring(0, idx_domain);

        // 해당 인덱스에 대응되는 사진 Uri 값을 어답터에 넣어준다 - 다운로드
        picture_Ref = mStorageRef.child("img_profile/" + my_id);
        picture_Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // glide 사용해서 사진 설정하기
                Glide.with(Frag1.this).load(uri).into(img_profile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        /* 대여 글 실시간 업데이트 */
        ChildEventListener childEventListener_frag1 = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                final Tab1Data writing = dataSnapshot.getValue(Tab1Data.class);

                // 쓴 글에 대한 인덱스 값을 가져온다
                String _idx = writing.getIndex();
                int idx = Integer.parseInt(_idx);

                // 해당 인덱스에 대응되는 사진 Uri 값을 어답터에 넣어준다 - 다운로드
                picture_Ref = mStorageRef.child("borrow_device/" + idx);
                picture_Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        writing.setUri(uri);

                        // 게시물 받아온 걸 넣어준다
                        arrayList.add(writing);
                        tab1Adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 게시물 받아온 걸 넣어준다
                        arrayList.add(writing);
                        tab1Adapter.notifyDataSetChanged();
                    }
                });
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
        database.getReference("writings").addChildEventListener(childEventListener_frag1);

        /* 나눔 글 실시간으로 업데이트 */
        ChildEventListener childEventListener_frag1_donate = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                final Tab1DonateData writing = dataSnapshot.getValue(Tab1DonateData.class);

                // 쓴 글에 대한 인덱스 값을 가져온다
                String _idx = writing.getIndex();
                int idx = Integer.parseInt(_idx);

                // 해당 인덱스에 대응되는 사진 Uri 값을 어답터에 넣어준다 - 다운로드
                picture_Ref = mStorageRef.child("borrow_device/" + idx);
                picture_Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        writing.setUri(uri);

                        // 게시물 받아온 걸 넣어준다
                        arrayList_donate.add(writing);
                        tab1DonateAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 게시물 받아온 걸 넣어준다
                        arrayList_donate.add(writing);
                        tab1DonateAdapter.notifyDataSetChanged();
                    }
                });
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
        database.getReference("writings_donate").addChildEventListener(childEventListener_frag1_donate);

        return view;
    }
}
