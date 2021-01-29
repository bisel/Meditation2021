package com.soulfriends.meditation.view.noti;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soulfriends.meditation.databinding.FriendEditItemBinding;
import com.soulfriends.meditation.databinding.NotiItemBinding;
import com.soulfriends.meditation.util.ItemClickListenerExt;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.view.friend.FriendEditAdapter;
import com.soulfriends.meditation.view.friend.FriendEditItemViewModel;

import java.util.List;

public class NotiAdapter extends RecyclerView.Adapter{

    private List list;
    private Context context;

    private LayoutInflater inflater;

    private ItemClickListenerExt listener;

    private long mLastClickTime = 0;

    public NotiAdapter(List list, Context context, ItemClickListenerExt listener) {
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

        NotiItemBinding notiItemBinding = NotiItemBinding.inflate(inflater, viewGroup, false);
        return new NotiAdapter.NotiViewHolder(notiItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder ViewHolder, int position) {

        NotiAdapter.NotiViewHolder notiViewHolder = (NotiAdapter.NotiViewHolder) ViewHolder;

        NotiItemViewModel notiItemViewModel = (NotiItemViewModel) list.get(position);

        notiViewHolder.bind(notiItemViewModel);

        NotiItemBinding bind = notiViewHolder.getNotiItemBinding();

        notiItemViewModel.position = position;

        bind.layoutBt.setVisibility(View.GONE);

        if(notiItemViewModel.hAlarm.entity.alarmtype == 1)
        {
        }
        else
        {
            bind.layoutBt.setVisibility(View.VISIBLE);
        }

        // 프로필 이미지

        if(notiItemViewModel.hAlarm.otheruser.profileimg != null && notiItemViewModel.hAlarm.otheruser.profileimg.length() != 0) {
            UtilAPI.load_image(context, notiItemViewModel.hAlarm.otheruser.profileimg, bind.ivIcon);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class NotiViewHolder extends RecyclerView.ViewHolder {

        private NotiItemBinding notiItemBinding;

        public NotiViewHolder(NotiItemBinding notiItemBinding) {
            super(notiItemBinding.getRoot());
            this.notiItemBinding = notiItemBinding;
        }

        public void bind(NotiItemViewModel notiItemViewModel) {
            this.notiItemBinding.setViewModel(notiItemViewModel);
        }

        public NotiItemBinding getNotiItemBinding() {
            return notiItemBinding;
        }
    }
}
