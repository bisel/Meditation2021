package com.soulfriends.meditation.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.FriendEditBinding;
import com.soulfriends.meditation.databinding.InAppBinding;
import com.soulfriends.meditation.util.ActivityStack;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.viewmodel.FriendEditViewModel;
import com.soulfriends.meditation.viewmodel.FriendEditViewModelFactory;
import com.soulfriends.meditation.viewmodel.InAppViewModel;
import com.soulfriends.meditation.viewmodel.InAppViewModelFactory;

public class InAppActivity extends BaseActivity implements ResultListener {

    private InAppBinding binding;
    private InAppViewModel viewModel;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private InAppViewModelFactory inAppViewModelFactory;


    private int select_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_in_app);
        binding.setLifecycleOwner(this);

        // 디폴트 선택
        select_id = 0;

        if (inAppViewModelFactory == null) {
            inAppViewModelFactory = new InAppViewModelFactory(this, this);
        }
        viewModel = new ViewModelProvider(this.getViewModelStore(), inAppViewModelFactory).get(InAppViewModel.class);
        binding.setViewModel(viewModel);
    }

    @Override
    public void onSuccess(Integer id, String message) {

        switch (id) {
            case R.id.iv_close: {

                onBackPressed();

            }
            break;
            case R.id.iv_bt12: {

                // 12개월 구입

                UtilAPI.setImage(this, binding.ivBt1,R.drawable.membership_1_mon);   // 비선택
                UtilAPI.setImage(this, binding.ivBt12,R.drawable.membership_12_mon); // 선택

                select_id = 0;

                Toast.makeText(getApplicationContext(),"12개월 buy",Toast.LENGTH_SHORT).show();

            }
            break;
            case R.id.iv_bt1: {

                UtilAPI.setImage(this, binding.ivBt12,R.drawable.membership_1_mon);   // 비선택
                UtilAPI.setImage(this, binding.ivBt1,R.drawable.membership_12_mon);   // 선택

                select_id = 1;

                // 1개월 구입
                Toast.makeText(getApplicationContext(),"1개월 buy",Toast.LENGTH_SHORT).show();

            }
            break;
            case R.id.iv_startbg: {

                // 무제한 이용 시작하기

                if(select_id == 0)
                {
                    // 12개월 구입
                }
                else
                {
                    // 1개월 구입
                }

                Toast.makeText(getApplicationContext(),"무제한 이용",Toast.LENGTH_SHORT).show();

            }
            break;
        }
    }

    @Override
    public void onFailure(Integer id, String message) {

    }

    @Override
    public void onBackPressed() {

        ActivityStack.instance().OnBack(this);
    }
}