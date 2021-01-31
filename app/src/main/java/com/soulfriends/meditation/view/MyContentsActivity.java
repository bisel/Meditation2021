package com.soulfriends.meditation.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.MyContentsBinding;
import com.soulfriends.meditation.dlg.AlertLineOnePopup;
import com.soulfriends.meditation.dlg.PsychologyDlg;
import com.soulfriends.meditation.model.MediationShowContents;
import com.soulfriends.meditation.model.MeditationCategory;
import com.soulfriends.meditation.model.MeditationContents;
import com.soulfriends.meditation.model.MeditationShowCategorys;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.util.ItemClickListener;
import com.soulfriends.meditation.util.ItemClickListenerExt;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.view.friend.FriendFindItemViewModel;
import com.soulfriends.meditation.view.mycontents.MyContentsAdapter;
import com.soulfriends.meditation.view.mycontents.MyContentsItemViewModel;
import com.soulfriends.meditation.viewmodel.MyContentsViewModel;
import com.soulfriends.meditation.viewmodel.MyContentsViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

public class MyContentsActivity extends BaseActivity implements ResultListener, ItemClickListenerExt {

    private MyContentsBinding binding;
    private MyContentsViewModel viewModel;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private MyContentsViewModelFactory myContentsViewModelFactory;

    private MeditationShowCategorys meditationShowCategorys; // test

    private List list_contents = new ArrayList<>();
    private MyContentsAdapter myContentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_contents);
        binding.setLifecycleOwner(this);

        if (myContentsViewModelFactory == null) {
            myContentsViewModelFactory = new MyContentsViewModelFactory(this, this);
        }
        viewModel = new ViewModelProvider(this.getViewModelStore(), myContentsViewModelFactory).get(MyContentsViewModel.class);
        binding.setViewModel(viewModel);


        meditationShowCategorys = NetServiceManager.getinstance().reqMediationType(1, false);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2, GridLayoutManager.VERTICAL, false);

        MyContentsItemList();
        myContentsAdapter = new MyContentsAdapter(list_contents, this, this);

        binding.recyclerview.setAdapter(myContentsAdapter);
        binding.recyclerview.setLayoutManager(gridLayoutManager);
        binding.recyclerview.setNestedScrollingEnabled(false);  // 12.02.괜찮은듯


        // 타이틀
        String strTitle = getResources().getString(R.string.mycontents_title);

        int total_count = 10;
        int make_count = 5;

        strTitle += " ";
        strTitle += String.valueOf(make_count);
        strTitle += "/";
        strTitle += String.valueOf(total_count);

        viewModel.setTitle(strTitle);
    }

    private void Update_Recyclerview()
    {
        MyContentsItemList();

        myContentsAdapter.SetList(list_contents);
        // 리사이클 데이터 변경에따른 ui 업데이트
        myContentsAdapter.notifyDataSetChanged();
    }

    private List<MyContentsItemViewModel> MyContentsItemList()
    {

        list_contents.clear();

        // 정보 요청
        UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();

        ArrayList<String> contentslist = userProfile.mycontentslist;

        // 내가 만든 갯수
        int total_count = 10;

        int my_make_count = contentslist.size();

        // 내가 만든 콘텐츠 개수
        for (int i = 0; i < my_make_count; i++) {

            //MediationShowContents data = entity.contests.get(i);
            MeditationContents data = NetServiceManager.getinstance().getSocialContents(contentslist.get(i));

            MyContentsItemViewModel myContentsItemViewModel = new MyContentsItemViewModel(data, 1, this);
            list_contents.add(myContentsItemViewModel);
        }

        // 추가할 개수
        int add_count = total_count - my_make_count;
        for (int i = 0; i < add_count; i++) {

            MyContentsItemViewModel myContentsItemViewModel = new MyContentsItemViewModel(null, 2, this);
            list_contents.add(myContentsItemViewModel);

        }

        return list_contents;
    }


    @Override
    public void onSuccess(Integer id, String message) {

        switch (id) {
            case R.id.ic_close: {

                // 닫기 버튼

                // 소셜메뉴 콘텐츠 탭으로 이동,hardwareback도 동일함
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                this.overridePendingTransition(0, 0);
                finish();

            }
            break;
        }
    }

    @Override
    public void onFailure(Integer id, String message) {

    }

    @Override
    public void onItemClick(View view, Object obj, int pos) {

        switch (view.getId()) {

            case R.id.img_child_item: {

                MeditationContents meditationContents = (MeditationContents) obj;

                Toast.makeText(getApplicationContext(), "img_child_item", Toast.LENGTH_SHORT).show();
            }
            break;
            case R.id.iv_modify: {

                MeditationContents meditationContents = (MeditationContents) obj;

                Toast.makeText(getApplicationContext(), "iv_modify", Toast.LENGTH_SHORT).show();

                // 팝업 메뉴
                Context c = MyContentsActivity.this;

                c.setTheme(R.style.PopupMenu);
                //PopupMenu popupMenu = new PopupMenu(c,view);
                PopupMenu popupMenu = new PopupMenu(c, view, Gravity.CENTER, 0, R.style.PopupMenuMoreCentralized);
                getMenuInflater().inflate(R.menu.popup_myplayer, popupMenu.getMenu());

                Menu menu = popupMenu.getMenu();
                {
                    MenuItem item = menu.findItem(R.id.action_menu1);
                    SpannableString s = new SpannableString(MyContentsActivity.this.getResources().getString(R.string.popup_menu_modify_noun));
                    s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
                    s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
                    item.setTitle(s);

                    MenuItem item1 = menu.findItem(R.id.action_menu2);
                    SpannableString s1 = new SpannableString(MyContentsActivity.this.getResources().getString(R.string.popup_menu_delete_noun));
                    s1.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s1.length(), 0);
                    s1.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s1.length(), 0);
                    item1.setTitle(s1);
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.action_menu1) {
                            Toast.makeText(MyContentsActivity.this, "수정 클릭", Toast.LENGTH_SHORT).show();

                            UtilAPI.s_MeditationContents_temp = meditationContents;

                            // 콘텐츠 수정 액티비티로 이동
                            Intent intent = new Intent(MyContentsActivity.this, ContentsEditActivity.class);
                            startActivity(intent);
                            MyContentsActivity.this.overridePendingTransition(0, 0);

                            finish();


                        } else {
                            // 팝업
                            // "콘텐츠를 정말 삭제하시겠습니까? 팝업 띄운다.
                            AlertLineOnePopup alertDlg = new AlertLineOnePopup(MyContentsActivity.this, MyContentsActivity.this, AlertLineOnePopup.Dlg_Type.contents_delete);
                            alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            alertDlg.show();

                            alertDlg.iv_ok.setOnClickListener(v -> {

                                OnEvent_Delete_Contents(meditationContents);
                                Toast.makeText(MyContentsActivity.this,"삭제",Toast.LENGTH_SHORT).show();

                                alertDlg.dismiss();
                            });
                            //Toast.makeText(MyContentsActivity.this, "삭제 클릭", Toast.LENGTH_SHORT).show();
                        }

                        return false;
                    }
                });
                popupMenu.show();

//                int[] location = new int[2];
//                view.getLocationOnScreen(location);
//                int x = location[0];
//                int y = location[1];
//
//                AlertLineOnePopup alertDlg = new AlertLineOnePopup(this, this, AlertLineOnePopup.Dlg_Type.friend_add);
//                alertDlg.show();
//
//                WindowManager.LayoutParams params = alertDlg.getWindow().getAttributes();
//                params.x = x;
//                params.y = x;
//                alertDlg.getWindow().setAttributes(params);


            }
            break;

            case R.id.img_child_item_add: {
                Toast.makeText(getApplicationContext(), "콘텐츠 추가", Toast.LENGTH_SHORT).show();

                // 터치 시 콘텐츠 제작 화면으로 이동

                Intent intent = new Intent(this, ContentsMakeActivity.class);
                startActivity(intent);
                this.overridePendingTransition(0, 0);

                finish();

            }
            break;
        }

    }

    public void OnEvent_Delete_Contents(MeditationContents meditationContents)
    {
        NetServiceManager.getinstance().setOnDelMyContentsListener(new NetServiceManager.OnDelMyContentsListener() {
            @Override
            public void onDelMyContentsListener(boolean validate) {

                if(validate)
                {

                    // 삭제 성공

                    NetServiceManager.getinstance().delLocalMyContents(meditationContents.uid);

                    NetServiceManager.getinstance().delUserProfileMyContents(meditationContents.uid);

                    Update_Recyclerview();

                    Toast.makeText(getApplicationContext(),"삭제 성공",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    // 삭제 실패

                    Toast.makeText(getApplicationContext(),"삭제 실패",Toast.LENGTH_SHORT).show();
                }
            }
        });

        NetServiceManager.getinstance().delMyContents(meditationContents);
    }

    @Override
    public void onBackPressed() {

        // 소셜메뉴 콘텐츠 탭으로 이동,hardwareback도 동일함
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
        finish();
    }
}