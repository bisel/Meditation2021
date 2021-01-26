package com.soulfriends.meditation.view.mycontents;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.MyContentsItemBinding;
import com.soulfriends.meditation.util.ItemClickListener;
import com.soulfriends.meditation.util.ItemClickListenerExt;
import com.soulfriends.meditation.util.UtilAPI;

import java.util.List;

public class MyContentsAdapter extends RecyclerView.Adapter{

    private List list;
    private Context context;
    private ItemClickListenerExt listener;

    private LayoutInflater inflater;

    private long mLastClickTime = 0;

    public MyContentsAdapter(List list, Context context, ItemClickListenerExt listener) {
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

        MyContentsItemBinding myContentsItemBinding = MyContentsItemBinding.inflate(inflater, viewGroup, false);
        return new MyContentsViewHolder(myContentsItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder ViewHolder, int position) {

        MyContentsViewHolder myContentsViewHolder = (MyContentsViewHolder) ViewHolder;

        MyContentsItemViewModel myContentsItemViewModel = (MyContentsItemViewModel) list.get(position);
        myContentsViewHolder.bind(myContentsItemViewModel);

        MyContentsItemBinding bind = myContentsViewHolder.getMyContentsItemBinding();

        if(myContentsItemViewModel.meditationContents == null)
        {
            // add
            bind.layoutBase.setVisibility(View.GONE);
            bind.layoutAdd.setVisibility(View.VISIBLE);
        }
        else
        {
            // 기본
            bind.layoutBase.setVisibility(View.VISIBLE);
            bind.layoutAdd.setVisibility(View.GONE);

            if(myContentsItemViewModel.meditationContents.thumbnail_uri == null)
            {
                UtilAPI.load_imageEX(this.context, myContentsItemViewModel.meditationContents.thumbnail, bind.imgChildItem, myContentsItemViewModel.meditationContents);
            }
            else
            {
                Uri uri = Uri.parse(myContentsItemViewModel.meditationContents.thumbnail_uri);
                UtilAPI.showImage(this.context, uri, myContentsViewHolder.getMyContentsItemBinding().imgChildItem);
            }

            // 콘텐츠 상태
            // 콘텐츠 인덱스를 받아서 콘텐츠 상태 표시를 해야 한다.
            int contents_state = 1;
            switch(contents_state)
            {
                case 1:
                {
                    // 명상
                    UtilAPI.setImage(context,bind.ivState, R.drawable.ctgr_med);
                }
                break;
                case 2:
                {
                    // 수면 명상
                    UtilAPI.setImage(context,bind.ivState, R.drawable.ctgr_sleep_md);
                }
                break;
                case 3:
                {
                    // 북수면
                    UtilAPI.setImage(context,bind.ivState, R.drawable.ctgr_bsleep);
                }
                break;
                case 4:
                {
                    // 음악
                    UtilAPI.setImage(context,bind.ivState, R.drawable.ctgr_music);
                }
                break;
                case 5:
                {
                    // 수면음악
                    UtilAPI.setImage(context,bind.ivState, R.drawable.ctgr_sleep_ms);
                }
                break;
                case 6:
                {
                    // 자연소리
                    UtilAPI.setImage(context,bind.ivState, R.drawable.ctgr_nature);
                }
                break;
            }
        }
       //

        //Glide.with(this.context).load(childItemViewModel.entity.getValue().thumbnail).into(childViewHolder.getChildItemBinding().imgChildItem);

        // 배치 처리
        //String releasedate = myContentsItemViewModel.meditationContents.releasedate;

//        FrameLayout frame = bind.frameLayout;
//        frame.setOnClickListener(v -> {
//
//            if (SystemClock.elapsedRealtime() - mLastClickTime < UtilAPI.button_delay_time){
//                return;
//            }
//            mLastClickTime = SystemClock.elapsedRealtime();
//
//            listener.onItemClick((Object) myContentsItemViewModel.meditationContents, position);
//        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    static class MyContentsViewHolder extends RecyclerView.ViewHolder {

        private MyContentsItemBinding myContentsItemBinding;

        public MyContentsViewHolder(MyContentsItemBinding myContentsItemBinding) {
            super(myContentsItemBinding.getRoot());
            this.myContentsItemBinding = myContentsItemBinding;
        }

        public void bind(MyContentsItemViewModel myContentsItemViewModel) {
            this.myContentsItemBinding.setViewModel(myContentsItemViewModel);
        }

        public MyContentsItemBinding getMyContentsItemBinding() {
            return myContentsItemBinding;
        }
    }
}
