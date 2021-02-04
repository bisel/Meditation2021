package com.soulfriends.meditation.view.profile;

import android.content.Context;
import android.net.Uri;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.ProfileItemBinding;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.util.ItemClickListener;
import com.soulfriends.meditation.util.ItemClickListenerExt;
import com.soulfriends.meditation.util.UtilAPI;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter{

    private List list;
    private Context context;
    private ItemClickListenerExt listener;

    private LayoutInflater inflater;

    private long mLastClickTime = 0;

    public ProfileAdapter(List list, Context context, ItemClickListenerExt listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    public void SetList(List list)
    {
        this.list = list;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (inflater == null) {
            inflater = LayoutInflater.from(viewGroup.getContext());
        }

        ProfileItemBinding profileItemBinding = ProfileItemBinding.inflate(inflater, viewGroup, false);
        return new ProfileAdapter.ProfileViewHolder(profileItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder ViewHolder, int position) {

        ProfileAdapter.ProfileViewHolder profileViewHolder = (ProfileAdapter.ProfileViewHolder) ViewHolder;

        ProfileItemViewModel profileItemViewModel = (ProfileItemViewModel) list.get(position);
        profileViewHolder.bind(profileItemViewModel);

        ProfileItemBinding bind = profileViewHolder.getProfileItemBinding();

        UtilAPI.load_imageEX(this.context, profileItemViewModel.meditationContents.thumbnail, bind.imgChildItem, profileItemViewModel.meditationContents);

        //  0 : 기본 제공  1 : 소셜 콘텐츠

        bind.ivModify.setVisibility(profileItemViewModel.bShow_ivModify);

//        if(profileItemViewModel.meditationContents.ismycontents == 0)
//        {
//            // 0 : 기본 제공
//
//            bind.ivModify.setVisibility(View.GONE);
//
//
//        }
//        else
//        {
//            // 1 : 소셜 콘텐츠
//            // 2021.02.02
//            if(profileItemViewModel.meditationContents.authoruid.equals(NetServiceManager.getinstance().getUserProfile().uid)){
//                UtilAPI.s_playerMode = UtilAPI.PlayerMode.my;
//
//                bind.ivModify.setVisibility(View.VISIBLE);
//
//            }else{
//                UtilAPI.s_playerMode = UtilAPI.PlayerMode.friend;
//
//                bind.ivModify.setVisibility(View.GONE);
//            }
//        }

//        if(profileItemViewModel.meditationContents.thumbnail_uri == null)
//        {
//            UtilAPI.load_imageEX(this.context, profileItemViewModel.meditationContents.thumbnail, bind.imgChildItem, profileItemViewModel.meditationContents);
//        }
//        else
//        {
//            Uri uri = Uri.parse(profileItemViewModel.meditationContents.thumbnail_uri);
//            UtilAPI.showImage(this.context, uri,  profileViewHolder.getProfileItemBinding().imgChildItem);
//        }

        //Glide.with(this.context).load(childItemViewModel.entity.getValue().thumbnail).into(childViewHolder.getChildItemBinding().imgChildItem);

        // 배치 처리
        String releasedate = profileItemViewModel.meditationContents.releasedate;


        // 콘텐츠 상태
        // 콘텐츠 인덱스를 받아서 콘텐츠 상태 표시를 해야 한다.


        int contents_state = profileItemViewModel.contentskind;
        if(contents_state == 0)
        {
            bind.ivState.setVisibility(View.GONE);
        }
        else {

            bind.ivState.setVisibility(View.VISIBLE);

            switch (contents_state) {
                case 1: {
                    // 명상
                    UtilAPI.setImage(context, bind.ivState, R.drawable.ctgr_med);
                }
                break;
                case 2: {
                    // 수면 명상
                    UtilAPI.setImage(context, bind.ivState, R.drawable.ctgr_sleep_md);
                }
                break;
                case 3: {
                    // 북수면
                    UtilAPI.setImage(context, bind.ivState, R.drawable.ctgr_bsleep);
                }
                break;
                case 4: {
                    // 음악
                    UtilAPI.setImage(context, bind.ivState, R.drawable.ctgr_music);
                }
                break;
                case 5: {
                    // 수면음악
                    UtilAPI.setImage(context, bind.ivState, R.drawable.ctgr_sleep_ms);
                }
                break;
                case 6: {
                    // 자연소리
                    UtilAPI.setImage(context, bind.ivState, R.drawable.ctgr_nature);
                }
                break;
            }
        }
//        FrameLayout frame = bind.frameLayout;
//        frame.setOnClickListener(v -> {
//
//            if (SystemClock.elapsedRealtime() - mLastClickTime < UtilAPI.button_delay_time){
//                return;
//            }
//            mLastClickTime = SystemClock.elapsedRealtime();
//
//            listener.onItemClick((Object) profileItemViewModel.meditationContents, position);
//        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    static class ProfileViewHolder extends RecyclerView.ViewHolder {

        private ProfileItemBinding profileItemBinding;

        public ProfileViewHolder(ProfileItemBinding profileItemBinding) {
            super(profileItemBinding.getRoot());
            this.profileItemBinding = profileItemBinding;
        }

        public void bind(ProfileItemViewModel profileItemViewModel) {
            this.profileItemBinding.setViewModel(profileItemViewModel);
        }

        public ProfileItemBinding getProfileItemBinding() {
            return profileItemBinding;
        }
    }
}
