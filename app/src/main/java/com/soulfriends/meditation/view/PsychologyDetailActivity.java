package com.soulfriends.meditation.view;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.PsychologyDetailBinding;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.parser.ResultData;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.viewmodel.PsychologyDetailViewModel;
import com.soulfriends.meditation.viewmodel.PsychologyDetailViewModelFactory;

import java.util.ArrayList;

public class PsychologyDetailActivity extends BaseActivity implements ResultListener {

    private PsychologyDetailBinding binding;
    private PsychologyDetailViewModel viewModel;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private PsychologyDetailViewModelFactory psychologyDetailViewModelFactory;

    ArrayList<ResultData> resultData_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_psychology_detail);
        binding.setLifecycleOwner(this);

        if (psychologyDetailViewModelFactory == null) {
            psychologyDetailViewModelFactory = new PsychologyDetailViewModelFactory(this, this);
        }
        viewModel = new ViewModelProvider(this.getViewModelStore(), psychologyDetailViewModelFactory).get(PsychologyDetailViewModel.class);
        binding.setViewModel(viewModel);

        // network
        UtilAPI.SetNetConnection_Activity(this);

        // 이모티콘 타입에 따라 결과 보이도록 한다.
        UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();
        //int emotiontype = 1;//NetServiceManager.getinstance().getUserProfile().emotiontype;
        int emotiontype = userProfile.emotiontype;
        ResultData resultData = NetServiceManager.getinstance().getResultData(emotiontype);

        if(userProfile.nickname != null) {

            String strQuest = userProfile.nickname + getResources().getString(R.string.psychology_nickname);

            int end_nick = userProfile.nickname.length();

            if (end_nick > 0) {
                Spannable wordtoSpan = new SpannableString(strQuest);
                wordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(179, 179, 227)), 0, end_nick, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                wordtoSpan.setSpan(new ForegroundColorSpan(Color.WHITE), end_nick + 1, strQuest.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                binding.tvNick.setText(wordtoSpan);
            }
        }

        viewModel.setStrTitle(resultData.state);
        viewModel.setStrResult(resultData.desc);

        int res_id_1 = this.getResources().getIdentifier(resultData.emotionimg, "drawable", this.getPackageName());

        UtilAPI.setImageResource(binding.imageTile, res_id_1);
    }

    @Override
    public void onSuccess(Integer id, String message) {
        switch (id) {
            case R.id.ic_close: {
                // 닫기 버튼

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

    @Override // 2020.12.20 , Close 막기
    public void onBackPressed() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
        finish();
    }
}