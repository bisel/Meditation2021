package com.soulfriends.meditation.view;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import android.content.Intent;
import android.os.Bundle;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.PsychologyCharacterListBinding;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.viewmodel.PsychologyCharacterListViewModel;
import com.soulfriends.meditation.viewmodel.PsychologyCharacterListViewModelFactory;

public class PsychologyCharacterListActivity extends BaseActivity implements ResultListener {

    private PsychologyCharacterListBinding binding;
    private PsychologyCharacterListViewModel viewModel;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private PsychologyCharacterListViewModelFactory psychologyCharacterListViewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_psychology_character_list);
        binding.setLifecycleOwner(this);

        if (psychologyCharacterListViewModelFactory == null) {
            psychologyCharacterListViewModelFactory = new PsychologyCharacterListViewModelFactory(this, this);
        }
        viewModel = new ViewModelProvider(this.getViewModelStore(), psychologyCharacterListViewModelFactory).get(PsychologyCharacterListViewModel.class);
        binding.setViewModel(viewModel);

        // network
        UtilAPI.SetNetConnection_Activity(this);

        // service onevent PlaybackStatus.STOPPED_END 체크
        UtilAPI.s_bEvent_service_main = false;
        UtilAPI.s_bEvent_service_player = false;
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

            case R.id.iv_character_button: {
                // 성격 검사

                Intent intent = new Intent(this, PsychologyCharacterTestActivity.class);
                startActivity(intent);
                finish(); // 2020.12.08
            }
            break;
        }
    }

    @Override
    public void onFailure(Integer id, String message) {

    }

    @Override // 2020.12.20 , Close 버튼과 동일
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
        finish();
    }
}