package com.soulfriends.meditation.view.friend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soulfriends.meditation.databinding.FriendEditItemBinding;
import com.soulfriends.meditation.netservice.NetServiceUtility;
import com.soulfriends.meditation.util.ItemClickListenerExt;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;

import java.util.List;

public class FriendEditAdapter extends RecyclerView.Adapter{

    private List list;
    private Context context;

    private LayoutInflater inflater;

    private ItemClickListenerExt listener;

    private long mLastClickTime = 0;

    public FriendEditAdapter(List list, Context context, ItemClickListenerExt listener) {
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

        FriendEditItemBinding friendEditItemBinding = FriendEditItemBinding.inflate(inflater, viewGroup, false);
        return new FriendEditViewHolder(friendEditItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder ViewHolder, int position) {

        FriendEditViewHolder friendEditViewHolder = (FriendEditViewHolder) ViewHolder;

        FriendEditItemViewModel friendEditItemViewModel = (FriendEditItemViewModel) list.get(position);
        friendEditViewHolder.bind(friendEditItemViewModel);

        friendEditItemViewModel.position = position;

        FriendEditItemBinding bind = friendEditViewHolder.getFriendEditItemBinding();


        // profile 이미지
        if(friendEditItemViewModel.userProfile.profileimg != null && friendEditItemViewModel.userProfile.profileimg.length() != 0) {
            UtilAPI.load_image_circle(context, NetServiceUtility.profieimgdir + friendEditItemViewModel.userProfile.profileimg, bind.ivIcon);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class FriendEditViewHolder extends RecyclerView.ViewHolder {

        private FriendEditItemBinding friendEditItemBinding;

        public FriendEditViewHolder(FriendEditItemBinding friendEditItemBinding) {
            super(friendEditItemBinding.getRoot());
            this.friendEditItemBinding = friendEditItemBinding;
        }

        public void bind(FriendEditItemViewModel friendEditItemViewModel) {
            this.friendEditItemBinding.setViewModel(friendEditItemViewModel);
        }

        public FriendEditItemBinding getFriendEditItemBinding() {
            return friendEditItemBinding;
        }
    }
}
