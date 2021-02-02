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
import com.soulfriends.meditation.dlg.AlertLineOneOkPopup;
import com.soulfriends.meditation.dlg.AlertLineOnePopup;
import com.soulfriends.meditation.dlg.MenuPopup;
import com.soulfriends.meditation.dlg.PsychologyDlg;
import com.soulfriends.meditation.model.MediationShowContents;
import com.soulfriends.meditation.model.MeditationCategory;
import com.soulfriends.meditation.model.MeditationContents;
import com.soulfriends.meditation.model.MeditationShowCategorys;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.util.ActivityStack;
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

        int total_count = NetServiceManager.getinstance().maxMyContentsNum;
        int make_count = NetServiceManager.getinstance().getUserProfile().mycontentslist.size();

        strTitle += " ";
        strTitle += String.valueOf(make_count);
        strTitle += "/";
        strTitle += String.valueOf(total_count);

        viewModel.setTitle(strTitle);

        int xx = 0;
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
                onBackPressed();

//                // 소셜메뉴 콘텐츠 탭으로 이동,hardwareback도 동일함
//                Intent intent = new Intent(this, MainActivity.class);
//                startActivity(intent);
//                this.overridePendingTransition(0, 0);
//                finish();

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

                //Toast.makeText(getApplicationContext(), "img_child_item", Toast.LENGTH_SHORT).show();
            }
            break;
            case R.id.iv_modify: {

                MeditationContents meditationContents = (MeditationContents) obj;


                //----------------------------------------------------------------
                // MenuPopup dialog 방식
                //----------------------------------------------------------------
                MenuPopup menuPopup = new MenuPopup(MyContentsActivity.this, MyContentsActivity.this);
                menuPopup.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                menuPopup.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                menuPopup.setCancelable(true);

                int[] outLocation = new int[2];
                view.getLocationInWindow(outLocation);
                WindowManager.LayoutParams params = menuPopup.getWindow().getAttributes();
                params.gravity = Gravity.TOP | Gravity.LEFT;
                params.x = outLocation[0];
                params.y = outLocation[1];
                menuPopup.getWindow().setAttributes(params);

                menuPopup.show();
                menuPopup.iv_modify.setOnClickListener(v -> {

                    // 콘텐츠 수정 액티비티로 이동
                    UtilAPI.s_MeditationContents_temp = meditationContents;
                    ActivityStack.instance().Push(MyContentsActivity.this, ""); // 메인액티비티여야 된다.
                    ChangeActivity(ContentsEditActivity.class);
                    menuPopup.dismiss();
                });

                menuPopup.iv_delete.setOnClickListener(v -> {
                    // 콘텐츠 삭제
                    OnEvent_Delete_Contents(meditationContents);
                    menuPopup.dismiss();
                });


            }
            break;

            case R.id.img_child_item_add: {
                //Toast.makeText(getApplicationContext(), "콘텐츠 추가", Toast.LENGTH_SHORT).show();

                // 터치 시 콘텐츠 제작 화면으로 이동

                ActivityStack.instance().Push(MyContentsActivity.this, ""); // 메인액티비티여야 된다.

                ChangeActivity(ContentsMakeActivity.class);

//                Intent intent = new Intent(this, ContentsMakeActivity.class);
//                startActivity(intent);
//                this.overridePendingTransition(0, 0);
//
//                finish();

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

                    NetServiceManager.getinstance().delLocalSocialContents(meditationContents.uid);

                    NetServiceManager.getinstance().delUserProfileMyContents(meditationContents.uid);

                    Update_Recyclerview();

                    //Toast.makeText(getApplicationContext(),"삭제 성공",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    // 삭제 실패

                    //Toast.makeText(getApplicationContext(),"삭제 실패",Toast.LENGTH_SHORT).show();
                }
            }
        });

        NetServiceManager.getinstance().delMyContents(meditationContents);
    }

    @Override
    public void onBackPressed() {

        ActivityStack.instance().OnBack(this);
//        // 소셜메뉴 콘텐츠 탭으로 이동,hardwareback도 동일함
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//        this.overridePendingTransition(0, 0);
//        finish();
    }
}