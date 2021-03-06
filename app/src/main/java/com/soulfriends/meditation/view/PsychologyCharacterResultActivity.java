package com.soulfriends.meditation.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.PsychologyCharacterResultBinding;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.parser.PersonResultData;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.viewmodel.PsychologyCharacterResultViewModel;
import com.soulfriends.meditation.viewmodel.PsychologyCharacterResultViewModelFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PsychologyCharacterResultActivity extends BaseActivity implements ResultListener {

    private PsychologyCharacterResultBinding binding;
    private PsychologyCharacterResultViewModel viewModel;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private PsychologyCharacterResultViewModelFactory psychologyCharacterResultViewModelFactory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_psychology_character_result);
        binding.setLifecycleOwner(this);

        if (psychologyCharacterResultViewModelFactory == null) {
            psychologyCharacterResultViewModelFactory = new PsychologyCharacterResultViewModelFactory(this, this);
        }
        viewModel = new ViewModelProvider(this.getViewModelStore(), psychologyCharacterResultViewModelFactory).get(PsychologyCharacterResultViewModel.class);
        binding.setViewModel(viewModel);

        // network
        UtilAPI.SetNetConnection_Activity(this);

        // 이모티콘 타입에 따라 결과 보이도록 한다.
        UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();
        int chartype = userProfile.chartype;

        ArrayList<PersonResultData> list = NetServiceManager.getinstance().getPersonResultDataList();

        PersonResultData resultData = list.get(chartype);//NetServiceManager.getinstance().getResultData(emotiontype);

        if(userProfile.nickname != null) {

            String strQuest = userProfile.nickname + getResources().getString(R.string.psychology_character_nickname);

            int end_nick = userProfile.nickname.length();

            if (end_nick > 0) {
                Spannable wordtoSpan = new SpannableString(strQuest);
                wordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(179, 179, 227)), 0, end_nick, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                wordtoSpan.setSpan(new ForegroundColorSpan(Color.WHITE), end_nick + 1, strQuest.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                binding.tvNick.setText(wordtoSpan);
            }
        }

        viewModel.setStrTitle(resultData.title);
        viewModel.setStrResult(resultData.desc);

        int res_id_1 = this.getResources().getIdentifier(resultData.img, "drawable", this.getPackageName());
        UtilAPI.setImageResource(binding.imageTile, res_id_1);
        //UtilAPI.setImageResource(binding.imageTile, res_id_1);

        // 심리 결과 시간을 저장을 한다.
        SimpleDateFormat format_date = new SimpleDateFormat ( "yyyy-MM-dd" );
        Date date_now = new Date(System.currentTimeMillis());
        String curdate = format_date.format(date_now);
        //PreferenceManager.setString(this, "psy_result_time", curdate);

        userProfile.finalchartestdate = curdate;

        // user profile send to sever
        NetServiceManager.getinstance().setOnRecvValProfileListener(new NetServiceManager.OnRecvValProfileListener() {
            @Override
            public void onRecvValProfile(boolean validate) {
                if (validate == true) {
                    int xx = 0;
                } else {
                    int yy = 0;
                }
            }
        });

        NetServiceManager.getinstance().sendValProfile(userProfile);

    }

    @Override
    public void onSuccess(Integer id, String message) {
        switch (id) {
            case R.id.iv_close: {
                // 닫기 버튼 - 사용하지 않음
            }
            break;
            case R.id.button_retry: {
                // 다시하기 -> 기존 성격을 돌려놓아야한다. 0이면 0으로 기존것을 알고 있어야 한다.

                // 2020.12.05 감정이 여기에 정상적으로 들어오는지 확인 필요
                NetServiceManager.getinstance().reqChartypeAllContents(true);

                Intent intent = new Intent(this, PsychologyCharacterListActivity.class);
                startActivity(intent);

                this.overridePendingTransition(0, 0);

                finish();
            }
            break;

            case R.id.button_recommand: {

                // 콘텐츠 추천하기
                // 2020.12.05 감정이 여기에 정상적으로 들어오는지 확인 필요
                NetServiceManager.getinstance().reqChartypeAllContents(true);

                UtilAPI.s_psychology_state = 1;
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
        NetServiceManager.getinstance().reqEmotionAllContents();
        NetServiceManager.getinstance().reqSocialEmotionAllContents();
        Intent intent = new Intent(this, PsychologyCharacterListActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
        finish();
    }
}