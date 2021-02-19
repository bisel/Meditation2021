package com.soulfriends.meditation.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.PsychologyFeelingTestBinding;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.viewmodel.PsychologyFeelingTestViewModel;
import com.soulfriends.meditation.viewmodel.PsychologyFeelingTestViewModelFactory;

public class PsychologyFeelingTestActivity extends BaseActivity implements ResultListener {

    private PsychologyFeelingTestBinding binding;
    private PsychologyFeelingTestViewModel viewModel;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private PsychologyFeelingTestViewModelFactory psychologyFeelingTestViewModelFactory;

    private boolean bCheckResult = false;
    private int select_emoticon_id = -1;
    private View viewPrev = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_psychology_feeling_test);
        binding.setLifecycleOwner(this);

        if (psychologyFeelingTestViewModelFactory == null) {
            psychologyFeelingTestViewModelFactory = new PsychologyFeelingTestViewModelFactory(this, this);
        }
        viewModel = new ViewModelProvider(this.getViewModelStore(), psychologyFeelingTestViewModelFactory).get(PsychologyFeelingTestViewModel.class);
        binding.setViewModel(viewModel);
        //initRecyclerView();

        // network
        UtilAPI.SetNetConnection_Activity(this);

        //
        UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();
		if(userProfile.nickname != null ) {
	        String strQuest = userProfile.nickname + getResources().getString(R.string.psychology_feel_nickname);

	        int end_nick = userProfile.nickname.length();

			 if(end_nick > 0) {
       			Spannable wordtoSpan = new SpannableString(strQuest);
		        wordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(179, 179, 227)), 0, end_nick, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		        wordtoSpan.setSpan(new ForegroundColorSpan(Color.WHITE), end_nick + 1, strQuest.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		        binding.tvNick.setText(wordtoSpan);
	        }
	    }

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onSuccess(Integer id, String message) {
        switch (id) {
            case R.id.ic_close: {
                // 닫기 버튼
                Intent intent = new Intent(this, PsychologyListActivity.class);
                startActivity(intent);

                finish();
            }
            break;
            case R.id.bt_result: {
                // 결과 보기
                if(bCheckResult) {

                    NetServiceManager.getinstance().checkFeelTest(select_emoticon_id);

                    Intent intent = new Intent(this, PsychologyResultActivity.class);
                    startActivity(intent);

                    finish();
                }
            }
            break;
            case R.id.lay_1: {
                SeleteEmoticon(id,1);
            }
            break;
            case R.id.lay_2: {
                SeleteEmoticon(id,2);
            }
            break;
            case R.id.lay_3: {
                SeleteEmoticon(id,3);
            }
            break;
            case R.id.lay_4: {
                SeleteEmoticon(id,4);
            }
            break;
            case R.id.lay_5: {
                SeleteEmoticon(id,5);
            }
            break;
            case R.id.lay_6: {
                SeleteEmoticon(id,6);
            }
            break;
            case R.id.lay_7: {
                SeleteEmoticon(id,7);
            }
            break;
            case R.id.lay_8: {
                SeleteEmoticon(id,8);
            }
            break;
            case R.id.lay_9: {
                SeleteEmoticon(id,9);
            }
            break;
            case R.id.lay_10: {
                SeleteEmoticon(id,10);
            }
            break;
            case R.id.lay_11: {
                SeleteEmoticon(id,11);
            }
            break;
            case R.id.lay_12: {
                SeleteEmoticon(id,12);
            }
            break;
            case R.id.lay_13: {
                SeleteEmoticon(id,13);
            }
            break;
            case R.id.lay_14: {
                SeleteEmoticon(id,14);
            }
            break;
            case R.id.lay_15: {
                SeleteEmoticon(id,15);
            }
            break;
            case R.id.lay_16: {
                SeleteEmoticon(id,16);
            }
            break;
        }
    }

    @Override
    public void onFailure(Integer id, String message) {

    }

    private void SeleteEmoticon(int id, int index) {
        // 이전 버튼 비선택 처리
        if (select_emoticon_id != -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                viewPrev.setBackground(ContextCompat.getDrawable(this, R.drawable.feeling_bg));
            } else {
                viewPrev.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.feeling_bg));
            }
        }

        // 첫 이모티콘 선택시
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            viewModel.getView().setBackground(ContextCompat.getDrawable(this, R.drawable.feeling_bg_selected));
        } else {
            viewModel.getView().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.feeling_bg_selected));
        }

        viewPrev = viewModel.getView();
        select_emoticon_id = index;

        // 결과 버튼 활성화
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            binding.btResult.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_feel_able));
        } else {
            binding.btResult.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.btn_feel_able));
        }

        bCheckResult = true;
    }

    @Override // 2020.12.20 , Close 버튼과 동일
    public void onBackPressed() {
        Intent intent = new Intent(this, PsychologyListActivity.class);
        startActivity(intent);
        finish();
    }
}