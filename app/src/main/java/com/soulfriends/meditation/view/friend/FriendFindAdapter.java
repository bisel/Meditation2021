package com.soulfriends.meditation.view.friend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soulfriends.meditation.databinding.FriendFindItemBinding;
import com.soulfriends.meditation.util.ItemClickListenerExt;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;

import java.util.List;

public class FriendFindAdapter extends RecyclerView.Adapter{
    private List list;
    private Context context;

    private LayoutInflater inflater;

    private ItemClickListenerExt listener;

    private long mLastClickTime = 0;

    public FriendFindAdapter(List list, Context context, ItemClickListenerExt listener) {
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

        FriendFindItemBinding friendFindItemBinding = FriendFindItemBinding.inflate(inflater, viewGroup, false);
        return new FriendFindAdapter.FriendFindViewHolder(friendFindItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder ViewHolder, int position) {

        FriendFindAdapter.FriendFindViewHolder friendFindViewHolder = (FriendFindAdapter.FriendFindViewHolder) ViewHolder;

        FriendFindItemViewModel friendFindItemViewModel = (FriendFindItemViewModel) list.get(position);
        friendFindViewHolder.bind(friendFindItemViewModel);

        FriendFindItemBinding bind = friendFindViewHolder.getFriendFindItemBinding();

        if(friendFindItemViewModel.friend_state == 0)
        {
            // 친구 추가
            bind.layoutAddbt.setVisibility(View.VISIBLE);
            bind.layoutBt.setVisibility(View.GONE);
            bind.layoutRequestbt.setVisibility(View.GONE);
        }
        else if(friendFindItemViewModel.friend_state == 1)
        {
            // 친구
            bind.layoutAddbt.setVisibility(View.GONE);
            bind.layoutBt.setVisibility(View.VISIBLE);
            bind.layoutRequestbt.setVisibility(View.GONE);
        }
        else
        {
            // 친구 요청 중
            bind.layoutAddbt.setVisibility(View.GONE);
            bind.layoutBt.setVisibility(View.GONE);
            bind.layoutRequestbt.setVisibility(View.VISIBLE);
        }

        // profile 이미지
        if(friendFindItemViewModel.userProfile.profileimg != null && friendFindItemViewModel.userProfile.profileimg.length() != 0) {
            UtilAPI.load_image(context, friendFindItemViewModel.userProfile.profileimg, bind.ivIcon);
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class FriendFindViewHolder extends RecyclerView.ViewHolder {

        private FriendFindItemBinding friendFindItemBinding;

        public FriendFindViewHolder(FriendFindItemBinding friendFindItemBinding) {
            super(friendFindItemBinding.getRoot());
            this.friendFindItemBinding = friendFindItemBinding;
        }

        public void bind(FriendFindItemViewModel friendFindItemViewModel) {
            this.friendFindItemBinding.setViewModel(friendFindItemViewModel);
        }

        public FriendFindItemBinding getFriendFindItemBinding() {
            return friendFindItemBinding;
        }
    }
}
