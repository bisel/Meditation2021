package com.soulfriends.meditation.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.MyContentsBinding;
import com.soulfriends.meditation.databinding.ProfileBinding;
import com.soulfriends.meditation.dlg.AlertLineOnePopup;
import com.soulfriends.meditation.model.MediationShowContents;
import com.soulfriends.meditation.model.MeditationCategory;
import com.soulfriends.meditation.model.MeditationContents;
import com.soulfriends.meditation.model.MeditationShowCategorys;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.netservice.NetServiceUtility;
import com.soulfriends.meditation.parser.PersonResultData;
import com.soulfriends.meditation.parser.ResultData;
import com.soulfriends.meditation.util.ActivityStack;
import com.soulfriends.meditation.util.ItemClickListener;
import com.soulfriends.meditation.util.ItemClickListenerExt;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.view.mycontents.MyContentsAdapter;
import com.soulfriends.meditation.view.mycontents.MyContentsItemViewModel;
import com.soulfriends.meditation.view.player.MeditationAudioManager;
import com.soulfriends.meditation.view.profile.ProfileAdapter;
import com.soulfriends.meditation.view.profile.ProfileItemViewModel;
import com.soulfriends.meditation.viewmodel.MyContentsViewModel;
import com.soulfriends.meditation.viewmodel.MyContentsViewModelFactory;
import com.soulfriends.meditation.viewmodel.ProfileViewModel;
import com.soulfriends.meditation.viewmodel.ProfileViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends PhotoBaseActivity implements ResultListener, ItemClickListenerExt {


    private ProfileBinding binding;
    private ProfileViewModel viewModel;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private ProfileViewModelFactory profileViewModelFactory;

    private MeditationShowCategorys meditationShowCategorys; // test


    private UserProfile userProfile;

    private ProfileAdapter profileAdapter_make;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        binding.setLifecycleOwner(this);

        userProfile = NetServiceManager.getinstance().getUserProfile();

        if (profileViewModelFactory == null) {
            profileViewModelFactory = new ProfileViewModelFactory(userProfile, this, this);
        }
        viewModel = new ViewModelProvider(this.getViewModelStore(), profileViewModelFactory).get(ProfileViewModel.class);
        binding.setViewModel(viewModel);


        meditationShowCategorys = NetServiceManager.getinstance().reqMediationType(1, false);

        // 프로필 사진

        if(userProfile.profileimg_uri !=null && userProfile.profileimg_uri.length() > 0) {

            String image_uri = userProfile.profileimg_uri;

            UtilAPI.load_image_circle(this, image_uri, binding.ivPicture);
        }
        
        //----------------------------------------------
        // 최근 체험 콘텐츠
        //----------------------------------------------
        {
            // 콘텐츠가 없으면 보이지 않도록 처리 해야함!!

            ArrayList<String> recentplaylist = userProfile.recentplaylist;
            if(recentplaylist.size() > 0) {

                //GridLayoutManager gridLayoutManager_new = new GridLayoutManager(this, 2);
                LinearLayoutManager layoutManager_new = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                //LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                ProfileAdapter profileAdapter_new = new ProfileAdapter(New_ContentsItemList(), this, this);

                binding.recyclerviewNew.setAdapter(profileAdapter_new);
                binding.recyclerviewNew.setLayoutManager(layoutManager_new);
                binding.recyclerviewNew.setNestedScrollingEnabled(false);

                binding.layoutContentsNew.setVisibility(View.VISIBLE);
            }
            else
            {
                binding.layoutContentsNew.setVisibility(View.GONE);
            }
        }

        //----------------------------------------------
        // 제작한 콘텐츠
        //----------------------------------------------
        {
            // 콘텐츠가 없으면 보이지 않도록 처리 해야함!!

            ArrayList<String> contentslist = userProfile.mycontentslist;
            if(contentslist.size() > 0) {

                //GridLayoutManager gridLayoutManager_make = new GridLayoutManager(this, 2);
                LinearLayoutManager layoutManager_make = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                //LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                profileAdapter_make = new ProfileAdapter(Make_ContentsItemList(), this, this);

                binding.recyclerviewMake.setAdapter(profileAdapter_make);
                binding.recyclerviewMake.setLayoutManager(layoutManager_make);
                binding.recyclerviewMake.setNestedScrollingEnabled(false);

                binding.layoutContentsMake.setVisibility(View.VISIBLE);
            }
            else
            {
                binding.layoutContentsMake.setVisibility(View.GONE);
            }
        }
    }

    private List<MyContentsItemViewModel> New_ContentsItemList() {
        List list = new ArrayList<>();

        ArrayList<String> recentplaylist = userProfile.recentplaylist;

        // 내가 만든 콘텐츠 개수
        for (int i = 0; i < recentplaylist.size(); i++) {

            //MediationShowContents data = entity.contests.get(i);
            MeditationContents data = NetServiceManager.getinstance().getSocialContents(recentplaylist.get(i));

            ProfileItemViewModel profileItemViewModel = new ProfileItemViewModel(data, 1, this);
            list.add(profileItemViewModel);
        }

        return list;
    }

    private List<MyContentsItemViewModel> Make_ContentsItemList() {
        List list = new ArrayList<>();

        ArrayList<String> contentslist = userProfile.mycontentslist;

        // 내가 만든 콘텐츠 개수
        for (int i = 0; i < contentslist.size(); i++) {

            //MediationShowContents data = entity.contests.get(i);
            MeditationContents data = NetServiceManager.getinstance().getSocialContents(contentslist.get(i));

            ProfileItemViewModel profileItemViewModel = new ProfileItemViewModel(data, 1, this);
            list.add(profileItemViewModel);
        }

        return list;
    }

    @Override
    public void onStart() {
        super.onStart();

        viewModel.Update();

        if (userProfile.emotiontype > 0) {

            binding.layFeel.setVisibility(View.VISIBLE);
            binding.tvStateQuest.setVisibility(View.VISIBLE);

            // 닉네임 질의
            if (userProfile.nickname != null) {
                String strQuest = userProfile.nickname + getResources().getString(R.string.feel_state_quest); // 2020.12.11

                int end_nick = userProfile.nickname.length();

                if (end_nick > 0) {
                    Spannable wordtoSpan = new SpannableString(strQuest);
                    wordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(179, 179, 227)), 0, end_nick, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    wordtoSpan.setSpan(new ForegroundColorSpan(Color.WHITE), end_nick + 1, strQuest.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    binding.tvStateQuest.setText(wordtoSpan);
                }
            }

            // 이모티콘
            ResultData resultData = NetServiceManager.getinstance().getResultData(userProfile.emotiontype);

            if (resultData.id > 0) {
                String str_id = "";
                if (resultData.id < 10) {
                    str_id = "0" + String.valueOf(resultData.id);
                } else {
                    str_id = String.valueOf(resultData.id);
                }
                String strEmoti = "emoti_" + str_id;
                int res_id_1 = this.getResources().getIdentifier(strEmoti, "drawable", this.getPackageName());
                UtilAPI.setImage(this, binding.ivIcon, res_id_1);
            }
        } else {
            binding.layFeel.setVisibility(View.GONE);
            binding.tvStateQuest.setVisibility(View.GONE);
        }

        // 성격상태 처리
        if (userProfile.chartype > 0) {

            binding.layCharacter.setVisibility(View.VISIBLE);
            binding.tvStateCharQuest.setVisibility(View.VISIBLE);

            // 닉네임 질의
            if (userProfile.nickname != null) {
                String strQuest = userProfile.nickname + getResources().getString(R.string.feel_state_char_quest); // 2020.12.11

                int end_nick = userProfile.nickname.length();

                if (end_nick > 0) {
                    Spannable wordtoSpan = new SpannableString(strQuest);
                    wordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(179, 179, 227)), 0, end_nick, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    wordtoSpan.setSpan(new ForegroundColorSpan(Color.WHITE), end_nick + 1, strQuest.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    binding.tvStateCharQuest.setText(wordtoSpan);
                }
            }

            // 이모티콘
            ArrayList<PersonResultData> list = NetServiceManager.getinstance().getPersonResultDataList();
            PersonResultData resultData = list.get(userProfile.chartype);

            int id_resultData = Integer.parseInt(resultData.id);
            if (id_resultData > 0) {
                String str_id = "";
                if (id_resultData < 10) {
                    str_id = String.valueOf(id_resultData);
                } else {
                    str_id = String.valueOf(id_resultData);
                }
                String strPersonality = "mini_personality_" + str_id;
                int res_id_1 = this.getResources().getIdentifier(strPersonality, "drawable", this.getPackageName());
                UtilAPI.setImage(this, binding.ivCharIcon, res_id_1);
            }
        } else {
            binding.layCharacter.setVisibility(View.GONE);
            binding.tvStateCharQuest.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSuccess(Integer id, String message) {

        switch (id) {
            case R.id.ic_close:
            {
                onBackPressed();
            }
            break;
            case R.id.lay_feel: {

                // 심리 결과 화면으로 이동 처리

                Intent intent = new Intent();
                intent.setClass(this, PsychologyDetailActivity.class);
                this.startActivity(intent);

                UtilAPI.s_StrMainFragment = UtilAPI.FRAGMENT_PROFILE;
                this.finish();
            }
            break;
            case R.id.lay_character: {

                // 성격 결과 화면으로 이동 처리

                Intent intent = new Intent();
                intent.setClass(this, PsychologyCharacterDetailActivity.class);
                this.startActivity(intent);

                UtilAPI.s_StrMainFragment = UtilAPI.FRAGMENT_PROFILE;
                this.finish();
            }
            break;

            case R.id.layout_photo:
            {
                // 포토 사진 선택 한 경우
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
                bottomSheetDialog.setContentView(R.layout.layout_photo_sheetdialog);
                bottomSheetDialog.show();

                View view_gallery = bottomSheetDialog.findViewById(R.id.layer_gallery);

                view_gallery.setOnClickListener(v -> {

                    getAlbum();

                    bottomSheetDialog.dismiss();

                });

                View view_photo = bottomSheetDialog.findViewById(R.id.layer_photo);

                view_photo.setOnClickListener(v -> {

                    captureCamera();

                    bottomSheetDialog.dismiss();

                });

            }
            break;
            case R.id.layout_nickname:
            {
                // 유저 정보 액티비티로 이동
                Intent intent = new Intent();
                intent.setClass(this, UserinfoExtActivity.class);
                this.startActivity(intent);

            }
            break;
            case R.id.iv_Introduction_bg:
            {
                // 자기소개글 선택 한 경우
                Intent intent = new Intent();
                intent.setClass(this, UserinfoExtActivity.class);
                this.startActivity(intent);
            }
            break;
        }
    }

    @Override
    public void onFailure(Integer id, String message) {

    }

    @Override
    public void OnSuccess_ImageCrop()
    {
        // 썹네일 성공시
        // 이미지 show

        binding.ivPicture.setImageURI(albumURI);

    }
    @Override
    public void onItemClick(View view, Object obj, int pos) {

        switch (view.getId()) {

            case R.id.img_child_item: {

                MeditationContents meditationContents = (MeditationContents) obj;

                //UtilAPI.Start_PlayerCheck(meditationContents.uid);

                // 다른 경우
                if(NetServiceManager.getinstance().getCur_contents() != null ) {
                    if (NetServiceManager.getinstance().getCur_contents().uid != meditationContents.uid) {
                        MeditationAudioManager.getinstance().stop();
                        if(MeditationAudioManager.getinstance().getServiceBound()) {
                            MeditationAudioManager.getinstance().unbind();
                        }
                    }
                }

                //  0 : 기본 제공  1 : 소셜 콘텐츠
                if(meditationContents.ismycontents == 0)
                {
                    UtilAPI.s_playerMode = UtilAPI.PlayerMode.base;
                }
                else
                {
                    UtilAPI.s_playerMode = UtilAPI.PlayerMode.friend;
                }

                NetServiceManager.getinstance().setCur_contents(meditationContents);


                ActivityStack.instance().Push(this, "");

                Intent intent = new Intent();
                intent.setClass(this, PlayerActivity.class);
                this.startActivity(intent);




                this.finish();

                //Toast.makeText(getApplicationContext(), "img_child_item", Toast.LENGTH_SHORT).show();
            }
            break;
            case R.id.iv_modify: {

                MeditationContents meditationContents = (MeditationContents) obj;

                Toast.makeText(getApplicationContext(), "iv_modify", Toast.LENGTH_SHORT).show();

                // 팝업 메뉴
                Context c = ProfileActivity.this;

                c.setTheme(R.style.PopupMenu);
                //PopupMenu popupMenu = new PopupMenu(c,view);
                PopupMenu popupMenu = new PopupMenu(c, view, Gravity.CENTER, 0, R.style.PopupMenuMoreCentralized);
                getMenuInflater().inflate(R.menu.popup_myplayer, popupMenu.getMenu());

                Menu menu = popupMenu.getMenu();
                {
                    MenuItem item = menu.findItem(R.id.action_menu1);
                    SpannableString s = new SpannableString(ProfileActivity.this.getResources().getString(R.string.popup_menu_modify_noun));
                    s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
                    s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
                    item.setTitle(s);

                    MenuItem item1 = menu.findItem(R.id.action_menu2);
                    SpannableString s1 = new SpannableString(ProfileActivity.this.getResources().getString(R.string.popup_menu_delete_noun));
                    s1.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s1.length(), 0);
                    s1.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s1.length(), 0);
                    item1.setTitle(s1);
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.action_menu1) {
                            Toast.makeText(ProfileActivity.this, "수정 클릭", Toast.LENGTH_SHORT).show();

                            UtilAPI.s_MeditationContents_temp = meditationContents;

                            // 콘텐츠 수정 액티비티로 이동
                            Intent intent = new Intent(ProfileActivity.this, ContentsEditActivity.class);

                            intent.putExtra("activity_class", "ProfileActivity");

                            startActivity(intent);
                            ProfileActivity.this.overridePendingTransition(0, 0);

                            finish();


                        } else {
                            // 팝업
                            // 친구추가
                            // "콘텐츠를 정말 삭제하시겠습니까? 팝업 띄운다.
                            AlertLineOnePopup alertDlg = new AlertLineOnePopup(ProfileActivity.this, ProfileActivity.this, AlertLineOnePopup.Dlg_Type.friend_add);
                            alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            alertDlg.show();

                            alertDlg.iv_ok.setOnClickListener(v -> {

                                int index_id =  Integer.parseInt((String)obj);

                                OnEvent_Delete_Contents(meditationContents);
                                Toast.makeText(ProfileActivity.this,"삭제",Toast.LENGTH_SHORT).show();

                                alertDlg.dismiss();
                            });
                            //Toast.makeText(MyContentsActivity.this, "삭제 클릭", Toast.LENGTH_SHORT).show();
                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
            break;
        }

    }

    private void Update_Recyclerview()
    {
        profileAdapter_make.SetList(Make_ContentsItemList());
        // 리사이클 데이터 변경에따른 ui 업데이트
        profileAdapter_make.notifyDataSetChanged();
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

        ActivityStack.instance().OnBack(this);
    }

}