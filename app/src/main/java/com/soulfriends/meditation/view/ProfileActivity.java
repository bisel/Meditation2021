package com.soulfriends.meditation.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.MyContentsBinding;
import com.soulfriends.meditation.databinding.ProfileBinding;
import com.soulfriends.meditation.model.MediationShowContents;
import com.soulfriends.meditation.model.MeditationCategory;
import com.soulfriends.meditation.model.MeditationShowCategorys;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.parser.PersonResultData;
import com.soulfriends.meditation.parser.ResultData;
import com.soulfriends.meditation.util.ItemClickListener;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.view.mycontents.MyContentsAdapter;
import com.soulfriends.meditation.view.mycontents.MyContentsItemViewModel;
import com.soulfriends.meditation.view.profile.ProfileAdapter;
import com.soulfriends.meditation.view.profile.ProfileItemViewModel;
import com.soulfriends.meditation.viewmodel.MyContentsViewModel;
import com.soulfriends.meditation.viewmodel.MyContentsViewModelFactory;
import com.soulfriends.meditation.viewmodel.ProfileViewModel;
import com.soulfriends.meditation.viewmodel.ProfileViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements ResultListener, ItemClickListener {


    private ProfileBinding binding;
    private ProfileViewModel viewModel;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private ProfileViewModelFactory profileViewModelFactory;

    private MeditationShowCategorys meditationShowCategorys; // test


    private UserProfile userProfile;

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


        //----------------------------------------------
        // 최근 체험 콘텐츠
        //----------------------------------------------
        {
            // 콘텐츠가 없으면 보이지 않도록 처리 해야함!!

            //GridLayoutManager gridLayoutManager_new = new GridLayoutManager(this, 2);
            LinearLayoutManager layoutManager_new = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            //LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            ProfileAdapter profileAdapter_new = new ProfileAdapter(New_ContentsItemList(), this, this);

            binding.recyclerviewNew.setAdapter(profileAdapter_new);
            binding.recyclerviewNew.setLayoutManager(layoutManager_new);
            binding.recyclerviewNew.setNestedScrollingEnabled(false);  // 12.02.괜찮은듯
        }

        //----------------------------------------------
        // 제작한 콘텐츠
        //----------------------------------------------
        {
            // 콘텐츠가 없으면 보이지 않도록 처리 해야함!!

            //GridLayoutManager gridLayoutManager_make = new GridLayoutManager(this, 2);
            LinearLayoutManager layoutManager_make = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            //LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            ProfileAdapter profileAdapter_make = new ProfileAdapter(Make_ContentsItemList(), this, this);

            binding.recyclerviewMake.setAdapter(profileAdapter_make);
            binding.recyclerviewMake.setLayoutManager(layoutManager_make);
            binding.recyclerviewMake.setNestedScrollingEnabled(false);  // 12.02.괜찮은듯
        }
    }

    private List<MyContentsItemViewModel> New_ContentsItemList() {
        List list = new ArrayList<>();
        MeditationCategory entity = meditationShowCategorys.showcategorys.get(10);

        for (MediationShowContents data : entity.contests) {

            ProfileItemViewModel profileItemViewModel = new ProfileItemViewModel(data.entity, 1, this);
            list.add(profileItemViewModel);
        }
        return list;
    }

    private List<MyContentsItemViewModel> Make_ContentsItemList() {
        List list = new ArrayList<>();
        MeditationCategory entity = meditationShowCategorys.showcategorys.get(10);

        for (MediationShowContents data : entity.contests) {

            ProfileItemViewModel profileItemViewModel = new ProfileItemViewModel(data.entity, 1, this);
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
        }
    }

    @Override
    public void onFailure(Integer id, String message) {

    }

    @Override
    public void onItemClick(Object obj, int pos) {

    }
}