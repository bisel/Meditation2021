package com.soulfriends.meditation.view.noti;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soulfriends.meditation.databinding.FriendEditItemBinding;
import com.soulfriends.meditation.databinding.NotiItemBinding;
import com.soulfriends.meditation.util.ItemClickListenerExt;
import com.soulfriends.meditation.util.ResultListener;
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

        if(notiItemViewModel.main_type == 0)
        {
        }
        else
        {
            bind.layoutBt.setVisibility(View.VISIBLE);
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
