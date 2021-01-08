package com.soulfriends.meditation.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.PasswordChangeBinding;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.viewmodel.PasswordChangeViewModel;

public class PasswordChangeActivity extends AppCompatActivity implements ResultListener {

    private PasswordChangeBinding binding;
    private PasswordChangeViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_userinfo);

        viewModel =  new PasswordChangeViewModel("aa", "bb", "cc");

        viewModel.setListener(this);

        binding.setLifecycleOwner(this);

        binding.setViewModel(viewModel);
    }

    @Override
    public void onSuccess(Integer id, String message) {
        switch (id) {
            case R.id.button_nickname: {

            }
            break;

            case R.id.button_ok: {
            }
            break;
        }
    }

    @Override
    public void onFailure(Integer id, String message) {

    }
}