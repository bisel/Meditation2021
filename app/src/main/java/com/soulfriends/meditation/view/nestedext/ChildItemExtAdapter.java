package com.soulfriends.meditation.view.nestedext;

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
import com.soulfriends.meditation.databinding.ChildItemExtBinding;
import com.soulfriends.meditation.model.MediationShowContents;
import com.soulfriends.meditation.model.MeditationCategory;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.util.ItemClickListener;
import com.soulfriends.meditation.util.UtilAPI;

import java.util.ArrayList;
import java.util.List;

public class ChildItemExtAdapter extends RecyclerView.Adapter{
    private List<ChildItemExtViewModel> list;
    private Context context;
    private LayoutInflater inflater;

    private ItemClickListener listener;

    private long mLastClickTime = 0;


    public ChildItemExtAdapter(MeditationCategory category, Context context, ItemClickListener listener) {

        this.listener = listener;
        if(this.list == null) {

            this.list = new ArrayList<ChildItemExtViewModel>();

            // test
            //category.subtype = 2;

            int count = 1;
            for (MediationShowContents data : category.contests) {

                if(data.entity == null)
                {
                    int xxxx = 0;
                }
                ChildItemExtViewModel childItemExtViewModel = new ChildItemExtViewModel(data.entity, category.subtype, count);
                this.list.add(childItemExtViewModel);

                count++;
            }
        }

        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (inflater == null) {
            inflater = LayoutInflater.from(viewGroup.getContext());
        }

        ChildItemExtBinding childItemExtBinding = ChildItemExtBinding.inflate(inflater, viewGroup, false);

        return new ChildItemExtAdapter.ChildExtViewHolder(childItemExtBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder ViewHolder, int position) {

        ChildItemExtAdapter.ChildExtViewHolder childExtViewHolder = (ChildItemExtAdapter.ChildExtViewHolder) ViewHolder;

        ChildItemExtViewModel childItemExtViewModel = list.get(position);
        childExtViewHolder.bind(list.get(position));

        ChildItemExtBinding bind = childExtViewHolder.getChildItemExtBinding();

        // 0 : default 1 : 숫자 1~10까지  2 : 플레이버튼

        bind.ivPlayedIcon.setVisibility(View.GONE);
        bind.ivTopCount.setVisibility(View.GONE);

        if(childItemExtViewModel.GetSubType() == 1)
        {
            int count = childItemExtViewModel.GetCount();

            if(count < 11) {

                bind.ivTopCount.setVisibility(View.VISIBLE);
                switch (count) {
                    case 1: {
                        bind.ivTopCount.setImageResource(R.drawable.main_top_10_01);
                    }
                    break;
                    case 2: {
                        bind.ivTopCount.setImageResource(R.drawable.main_top_10_02);
                    }
                    break;
                    case 3: {
                        bind.ivTopCount.setImageResource(R.drawable.main_top_10_03);
                    }
                    break;
                    case 4: {
                        bind.ivTopCount.setImageResource(R.drawable.main_top_10_04);
                    }
                    break;
                    case 5: {
                        bind.ivTopCount.setImageResource(R.drawable.main_top_10_05);
                    }
                    break;
                    case 6: {
                        bind.ivTopCount.setImageResource(R.drawable.main_top_10_06);
                    }
                    break;
                    case 7: {
                        bind.ivTopCount.setImageResource(R.drawable.main_top_10_07);
                    }
                    break;
                    case 8: {
                        bind.ivTopCount.setImageResource(R.drawable.main_top_10_08);
                    }
                    break;
                    case 9: {
                        bind.ivTopCount.setImageResource(R.drawable.main_top_10_09);
                    }
                    break;
                    case 10: {
                        bind.ivTopCount.setImageResource(R.drawable.main_top_10_10);
                    }
                    break;
                }
            }
        }
        else if(childItemExtViewModel.GetSubType() == 2)
        {
            bind.ivPlayedIcon.setVisibility(View.VISIBLE);
        }

        //String str = childItemViewModel.entity.getValue().thumbnail;

        //if(childItemViewModel.meditationContents.thumbnail_uri == null)

        if(childItemExtViewModel.meditationContents.thumbnail == null) {
            UtilAPI.setImage(this.context, bind.imgChildItem, R.drawable.basic_img);
        }else {
            if (childItemExtViewModel.meditationContents.thumbnail_uri == null) {
                UtilAPI.load_imageEX(this.context, childItemExtViewModel.entity.getValue().thumbnail, bind.imgChildItem, childItemExtViewModel.meditationContents);
            } else {
                Uri uri = Uri.parse(childItemExtViewModel.meditationContents.thumbnail_uri);
                UtilAPI.showImage(this.context, uri, childExtViewHolder.getChildItemExtBinding().imgChildItem);
            }
        }

        //Glide.with(this.context).load(childItemViewModel.entity.getValue().thumbnail).into(childViewHolder.getChildItemBinding().imgChildItem);

        // 배치 처리
        String releasedate = childItemExtViewModel.entity.getValue().releasedate;

        long day = UtilAPI.GetDay_Date(releasedate);

        if(day > NetServiceManager.getinstance().GetNewContentsDelayDay())
        {
            bind.ivBadge.setVisibility(View.GONE);
        }
        else
        {
            // new
            bind.ivBadge.setVisibility(View.VISIBLE);
        }

        // 콘텐츠 상태
        // 콘텐츠 인덱스를 받아서 콘텐츠 상태 표시를 해야 한다.
        int contents_state = childItemExtViewModel.contentskind;
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


        FrameLayout frame = bind.frameLayout;
        frame.setOnClickListener(v -> {

            if (SystemClock.elapsedRealtime() - mLastClickTime < UtilAPI.button_delay_time){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            listener.onItemClick((Object) childItemExtViewModel.entity.getValue(), position);
        });
    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    static class ChildExtViewHolder extends RecyclerView.ViewHolder {

        private ChildItemExtBinding childItemExtBinding;

        public ChildExtViewHolder(ChildItemExtBinding childItemExtBinding) {
            super(childItemExtBinding.getRoot());
            this.childItemExtBinding = childItemExtBinding;
        }

        public void bind(ChildItemExtViewModel childItemExtViewModel) {
            this.childItemExtBinding.setViewModel(childItemExtViewModel);
        }

        public ChildItemExtBinding getChildItemExtBinding() {
            return childItemExtBinding;
        }
    }
}
