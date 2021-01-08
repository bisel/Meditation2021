package com.soulfriends.meditation.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.ContentsinfoBinding;
import com.soulfriends.meditation.model.MeditationContents;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.view.player.MeditationAudioManager;
import com.soulfriends.meditation.view.player.PlaybackStatus;
import com.soulfriends.meditation.viewmodel.ContentsinfoViewModel;
import com.soulfriends.meditation.viewmodel.ContentsinfoViewModelFactory;

import org.greenrobot.eventbus.Subscribe;

public class ContentsinfoActivity extends BaseActivity implements ResultListener {

    private ContentsinfoBinding binding;
    private ContentsinfoViewModel viewModel;

    private ViewModelStore viewModelStore = new ViewModelStore();
    private ContentsinfoViewModelFactory contentsinfoViewModelFactory;

    private MeditationContents meditationContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contentsinfo);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_contentsinfo);
        binding.setLifecycleOwner(this);

       // meditationContents = new MeditationContents();

//        meditationContents.audio = "스튜어트섬으로 떠나는 환상적인 별자리 여행";
//        meditationContents.narrtor = "이명진";
//        meditationContents.author = "Tamara Levitt text testtestrestwe";
//        meditationContents.favoritecnt = 3;

        meditationContents = NetServiceManager.getinstance().getCur_contents();

        if (contentsinfoViewModelFactory == null) {
            contentsinfoViewModelFactory = new ContentsinfoViewModelFactory(meditationContents,this, this);
        }
        viewModel = new ViewModelProvider(this.getViewModelStore(), contentsinfoViewModelFactory).get(ContentsinfoViewModel.class);
        binding.setViewModel(viewModel);


        // image
        if(!meditationContents.publisher.isEmpty())
        {
            int id_image = -1;
            if(meditationContents.publisher.contains(this.getResources().getString(R.string.publisher_soulfriends)))
            {
                id_image = R.drawable.b001;
            }
            if(meditationContents.publisher.contains(this.getResources().getString(R.string.publisher_deep)))
            {
                id_image = R.drawable.b002;
            }
            if(meditationContents.publisher.contains(this.getResources().getString(R.string.publisher_carensia)))
            {
                id_image = R.drawable.b003;
            }

            if(id_image != -1) {
                UtilAPI.setImage(this, binding.ivImage, id_image);
            }

            // 2020.12.04 회사 소개 글 추가.
            binding.ivContents2.setText(meditationContents.publisherintro);
        }

        UtilAPI.AddActivityInPlayer(this);
    }

    @Override
    protected void onStart() {

        super.onStart();

        //EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {

        super.onStop();

        //EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(String status) {
        switch (status) {
            case PlaybackStatus.STOPPED: {

                // 음악이 끝난 경우 발생되는 이벤트

                // 플레이 위치 초기화
                MeditationAudioManager.getinstance().idle_start();

                // MainActivity 이동
                ChangeActivity(MainActivity.class);

            }
            break;
            case PlaybackStatus.STOP_NOTI: {

                // 노티에서 정지 이벤트  발생된 경우
                MeditationAudioManager.getinstance().stop();
                MeditationAudioManager.getinstance().unbind();

                // MainActivity 이동
                ChangeActivity(MainActivity.class);
            }
            break;
        }
    }

    private void ChangeActivity(java.lang.Class<?> cls)
    {
        Intent intent = new Intent(this, cls);
        startActivity(intent);

        this.overridePendingTransition(0, 0);

        finish();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        UtilAPI.RemoveActivityInPlayer(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onSuccess(Integer id, String message) {
        switch (id) {
            case R.id.iv_close: {
                // 나가기 버튼

                Intent intent = new Intent(this, PlayerActivity.class);
                startActivity(intent);

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
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
        finish();
    }
}