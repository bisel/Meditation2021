package com.soulfriends.meditation.view;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import android.content.Intent;
import android.os.Bundle;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.BackGroundBinding;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.view.background.BackGroundPagerAdapter;
import com.soulfriends.meditation.viewmodel.BackGroundViewModel;
import com.soulfriends.meditation.viewmodel.BackGroundViewModelFactory;

import java.util.ArrayList;

public class BackGroundActivity extends BaseActivity implements ResultListener {

    private BackGroundBinding binding;
    private BackGroundViewModel viewModel;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private BackGroundViewModelFactory backGroundViewModelFactory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_back_ground);
        binding.setLifecycleOwner(this);

        if (backGroundViewModelFactory == null) {
            backGroundViewModelFactory = new BackGroundViewModelFactory(this, this);
        }
        viewModel = new ViewModelProvider(this.getViewModelStore(), backGroundViewModelFactory).get(BackGroundViewModel.class);
        binding.setViewModel(viewModel);

        ArrayList<Integer> list = new ArrayList<>();

        // 추후 음악 bg 넣도록 한다.
        list.add(R.drawable.bsleep_wall);
        list.add(R.drawable.med_wall);
        list.add(R.drawable.music_wall);
        list.add(R.drawable.nature_wall);
        list.add(R.drawable.sleep_md_wall);
        list.add(R.drawable.sleep_ms_wall);

        BackGroundPagerAdapter adapter = new BackGroundPagerAdapter(this, list, this);
        binding.vpSlider.setAdapter(adapter);


        Intent intent = getIntent();
        if(intent.getExtras() != null) {
            String title_text = intent.getExtras().getString("title_text");
            if(title_text != null) {
                if (title_text.length() > 0) {

                    viewModel.setTitle(title_text);
                } else {
                    viewModel.setTitle(this.getResources().getString(R.string.background_input));
                }
            }
            else
            {
                viewModel.setTitle(this.getResources().getString(R.string.background_input));
            }
        }
        else
        {
            viewModel.setTitle(this.getResources().getString(R.string.background_input));
        }
    }

    @Override
    public void onSuccess(Integer id, String message) {

        switch (id) {
            case R.id.ic_close: {

                // 닫기
                finish();

            }
            break;
            case R.id.iv_prev: {

                // 이전
                binding.vpSlider.setCurrentItem(binding.vpSlider.getCurrentItem()-1, true);

            }
            break;
            case R.id.iv_next: {

                // 다음
                binding.vpSlider.setCurrentItem(binding.vpSlider.getCurrentItem()+1, true);

            }
            break;
            case R.id.iv_cancel: {

                // 취소
                finish();

            }
            break;

            case R.id.iv_select: {

                // 선택
                UtilAPI.s_id_background_image = binding.vpSlider.getCurrentItem();

                finish();

            }
            break;
        }

    }

    @Override
    public void onFailure(Integer id, String message) {

    }

    @Override
    public void onBackPressed() {

        // 닫기
        finish();
    }
}