package com.soulfriends.meditation.view.profile;

import android.content.Context;
import android.net.Uri;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soulfriends.meditation.databinding.ProfileItemBinding;
import com.soulfriends.meditation.util.ItemClickListener;
import com.soulfriends.meditation.util.UtilAPI;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter{

    private List list;
    private Context context;
    private ItemClickListener listener;

    private LayoutInflater inflater;

    private long mLastClickTime = 0;

    public ProfileAdapter(List list, Context context, ItemClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
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

        if(profileItemViewModel.meditationContents.thumbnail_uri == null)
        {
            UtilAPI.load_imageEX(this.context, profileItemViewModel.meditationContents.thumbnail, bind.imgChildItem, profileItemViewModel.meditationContents);
        }
        else
        {
            Uri uri = Uri.parse(profileItemViewModel.meditationContents.thumbnail_uri);
            UtilAPI.showImage(this.context, uri,  profileViewHolder.getProfileItemBinding().imgChildItem);
        }

        //Glide.with(this.context).load(childItemViewModel.entity.getValue().thumbnail).into(childViewHolder.getChildItemBinding().imgChildItem);

        // 배치 처리
        String releasedate = profileItemViewModel.meditationContents.releasedate;

        FrameLayout frame = bind.frameLayout;
        frame.setOnClickListener(v -> {

            if (SystemClock.elapsedRealtime() - mLastClickTime < UtilAPI.button_delay_time){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            listener.onItemClick((Object) profileItemViewModel.meditationContents, position);
        });
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
