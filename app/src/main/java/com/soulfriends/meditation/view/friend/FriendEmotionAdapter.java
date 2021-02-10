package com.soulfriends.meditation.view.friend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soulfriends.meditation.databinding.FriendEmotionItemBinding;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.netservice.NetServiceUtility;
import com.soulfriends.meditation.parser.ResultData;
import com.soulfriends.meditation.util.ItemClickListenerExt;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;

import java.util.List;

public class FriendEmotionAdapter extends RecyclerView.Adapter{

    private List list;
    private Context context;

    private LayoutInflater inflater;

    private ItemClickListenerExt listener;

    private long mLastClickTime = 0;

    public FriendEmotionAdapter(List list, Context context, ItemClickListenerExt listener) {
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

        FriendEmotionItemBinding friendEmotionItemBinding = FriendEmotionItemBinding.inflate(inflater, viewGroup, false);
        return new FriendEmotionAdapter.FriendEmotionViewHolder(friendEmotionItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder ViewHolder, int position) {

        FriendEmotionAdapter.FriendEmotionViewHolder friendEmotionViewHolder = (FriendEmotionAdapter.FriendEmotionViewHolder) ViewHolder;

        FriendEmotionItemViewModel friendEmotionItemViewModel = (FriendEmotionItemViewModel) list.get(position);
        friendEmotionViewHolder.bind(friendEmotionItemViewModel);

        FriendEmotionItemBinding bind = friendEmotionViewHolder.getFriendEmotionItemBinding();

        if(friendEmotionItemViewModel.emotion_state == 0)
        {
            // 감정상태
            bind.layoutBasebt.setVisibility(View.VISIBLE);
            bind.layoutRequestingbt.setVisibility(View.GONE);
            bind.layoutRequestbt.setVisibility(View.GONE);
            bind.layoutRequestAnswerbt.setVisibility(View.GONE);
        }
        else if(friendEmotionItemViewModel.emotion_state == 1)
        {
            // 감정공유 요청 중
            bind.layoutBasebt.setVisibility(View.GONE);
            bind.layoutRequestingbt.setVisibility(View.VISIBLE);
            bind.layoutRequestbt.setVisibility(View.GONE);
            bind.layoutRequestAnswerbt.setVisibility(View.GONE);
        }
        else if(friendEmotionItemViewModel.emotion_state == 3)
        {
            // 감정공유 응답
            bind.layoutBasebt.setVisibility(View.GONE);
            bind.layoutRequestingbt.setVisibility(View.GONE);
            bind.layoutRequestbt.setVisibility(View.GONE);
            bind.layoutRequestAnswerbt.setVisibility(View.VISIBLE);
        }
        else
        {
            // 감정공유 요청
            bind.layoutBasebt.setVisibility(View.GONE);
            bind.layoutRequestingbt.setVisibility(View.GONE);
            bind.layoutRequestbt.setVisibility(View.VISIBLE);
            bind.layoutRequestAnswerbt.setVisibility(View.GONE);
        }

        // profile 이미지
        if(friendEmotionItemViewModel.userProfile.profileimg != null && friendEmotionItemViewModel.userProfile.profileimg.length() != 0) {
            UtilAPI.load_image_circle(context, NetServiceUtility.profieimgdir + friendEmotionItemViewModel.userProfile.profileimg, bind.ivIcon);
        }

        // 이모티콘
        ResultData resultData = NetServiceManager.getinstance().getResultData(friendEmotionItemViewModel.userProfile.emotiontype);

        if (resultData.id > 0) {
            String str_id = "";
            if (resultData.id < 10) {
                str_id = "0" + String.valueOf(resultData.id);
            } else {
                str_id = String.valueOf(resultData.id);
            }
            String strEmoti = "emoti_" + str_id;
            int res_id_1 = context.getResources().getIdentifier(strEmoti, "drawable", context.getPackageName());
            UtilAPI.setImage(context, bind.ivEmoticon, res_id_1); //iv_emoticon
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class FriendEmotionViewHolder extends RecyclerView.ViewHolder {

        private FriendEmotionItemBinding friendEmotionItemBinding;

        public FriendEmotionViewHolder(FriendEmotionItemBinding friendEmotionItemBinding) {
            super(friendEmotionItemBinding.getRoot());
            this.friendEmotionItemBinding = friendEmotionItemBinding;
        }

        public void bind(FriendEmotionItemViewModel friendEmotionItemViewModel) {
            this.friendEmotionItemBinding.setViewModel(friendEmotionItemViewModel);
        }

        public FriendEmotionItemBinding getFriendEmotionItemBinding() {
            return friendEmotionItemBinding;
        }
    }
}
