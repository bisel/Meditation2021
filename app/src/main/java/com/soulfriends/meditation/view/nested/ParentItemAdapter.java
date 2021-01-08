package com.soulfriends.meditation.view.nested;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.soulfriends.meditation.databinding.ParentBottomItemBinding;
import com.soulfriends.meditation.databinding.ParentItemBinding;
import com.soulfriends.meditation.databinding.ParentMiddleItemBinding;
import com.soulfriends.meditation.databinding.ParentTopItemBinding;
import com.soulfriends.meditation.netservice.NetServiceViewPager;
import com.soulfriends.meditation.util.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class ParentItemAdapter extends RecyclerView.Adapter {

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List list;
    private Context context;
    private LayoutInflater inflater;

    private LayoutInflater inflater_top;

    private LayoutInflater inflater_middle;

    private LayoutInflater inflater_bottom;

    private ItemClickListener listener;

    private ParentTopViewHolder parentTopViewHolder = null;

    private ParentMiddleViewHolder parentMiddleViewHolder = null;

    private ParentBottomViewHolder parentBottomViewHolder = null;

    private ArrayList<String> slider_image_list;

    private int page = 0;

    public Handler mhandler;
    public Runnable mRunnable;

    private int page_state = 0;

    private int page_init = -1;

    private CountDownTimer countDownTimer;


    public ParentItemAdapter(List list, Context context, ItemClickListener listener, int page_init) {
        this.list = list;
        this.context = context;
        this.listener = listener;

        mhandler = new Handler();
        mRunnable = null;

        page_state = 0;

        this.page_init = page_init;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (this.getItemViewType(i) == 0) {
            if (inflater_top == null) {
                inflater_top = LayoutInflater.from(viewGroup.getContext());
            }

            ParentTopItemBinding parentTopItemBinding = ParentTopItemBinding.inflate(inflater_top, viewGroup, false);
            return new ParentTopViewHolder(parentTopItemBinding);

        } else if (this.getItemViewType(i) == 1) {

            if (inflater_middle == null) {
                inflater_middle = LayoutInflater.from(viewGroup.getContext());
            }

            ParentMiddleItemBinding parentMiddleItemBinding = ParentMiddleItemBinding.inflate(inflater_middle, viewGroup, false);
            return new ParentMiddleViewHolder(parentMiddleItemBinding);

        } else if(this.getItemViewType(i) == 2){

            if (inflater_bottom == null) {
                inflater_bottom = LayoutInflater.from(viewGroup.getContext());
            }


            ParentBottomItemBinding parentBottomItemBinding = ParentBottomItemBinding.inflate(inflater_bottom, viewGroup, false);
            return new ParentBottomViewHolder(parentBottomItemBinding);
        }
        else {
            // 3
            if (inflater == null) {
                inflater = LayoutInflater.from(viewGroup.getContext());
            }

            ParentItemBinding parentItemBinding = ParentItemBinding.inflate(inflater, viewGroup, false);
            return new ParentViewHolder(parentItemBinding);
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (list.get(position) instanceof ParentTopItemViewModel) {
            return 0;
        } else if (list.get(position) instanceof ParentMiddleItemViewModel) {
            return 1;
        }
        else if (list.get(position) instanceof ParentBottomItemViewModel) {
            return 2;
        }
        return 3;
    }

    public void UpdateTopItem() {
        if (parentTopViewHolder != null) {
            parentTopViewHolder.init(this.context);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder ViewHolder, int i) {

        if (this.getItemViewType(i) == 0) {

            parentTopViewHolder = (ParentTopViewHolder) ViewHolder;

            ParentTopItemViewModel parentTopItemViewModel = (ParentTopItemViewModel) list.get(i);
            parentTopViewHolder.bind(parentTopItemViewModel);

            parentTopViewHolder.init(this.context);

            // 일단 주석 dlsmdla
//            ImageView imageView = parentTopViewHolder.getParentTopItemBinding().imgChildItem;
//            imageView.setOnClickListener(v -> {
//
//                listener.onItemClick((Object) imageView, 0);
//            });

        } else if (this.getItemViewType(i) == 1) {

            parentMiddleViewHolder = (ParentMiddleViewHolder) ViewHolder;

            slider_image_list = new ArrayList<>();

            slider_image_list.add("1"); // 심리검사
            slider_image_list.add("2"); // 성격검사
            final SliderPagerAdapter sliderPagerAdapter = new SliderPagerAdapter((Activity) context, slider_image_list, listener);
            parentMiddleViewHolder.viewPager.setAdapter(sliderPagerAdapter);
            parentMiddleViewHolder.setSliderPageAdapter(sliderPagerAdapter);


            parentMiddleViewHolder.viewPager.SetParentItemAdapter(this);

            parentMiddleViewHolder.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {


                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    int xx = 0;
                    Log.d("Meditation", "onPageScrolled = " + position);
                    //if(state == 2)
                    {
                        startRun();
                    }
                }

                @Override
                public void onPageSelected(int position) {
                    addBottomDots(position, parentMiddleViewHolder.ll_dots);
                    page = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                    // state == 1 이면 터치가 된 상태가 된다.
                    // state == 0 이면 터치가 off

                   // Log.d("Meditation", "state = " + state);

                    if(state == 1 )
                    {
                        //터치가 된 상태가 된다.
                        //pauseRun();
                        page_state = 1;
                    }

//                    if(state == 0 && page_state == 1)
//                    {
//                        startRun();
//                    }

                    if(state == 2)
                    {
                        startRun();
                    }
                }
            });
            addBottomDots(0, parentMiddleViewHolder.ll_dots);

            startRun();
//            mhandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (sliderPagerAdapter.getCount() == page) {
//                        page = 0;
//                    } else {
//                        page++;
//                    }
//                    parentMiddleViewHolder.viewPager.setCurrentItem(page);
//                    h.postDelayed(this, 4000);
//                }
//            }, 4000);

            if(page_init > -1) {
                initPage(page_init);
            }

        }
        else if( this.getItemViewType(i) == 2) {

            parentBottomViewHolder = (ParentBottomViewHolder) ViewHolder;
            ParentBottomItemViewModel parentBottomItemViewModel = (ParentBottomItemViewModel) list.get(i);
            parentBottomViewHolder.bind(parentBottomItemViewModel);

            if(i < 4)
            {
                parentBottomViewHolder.bind.frameLayout1.setVisibility(View.GONE);
                parentBottomViewHolder.bind.frameLayout1.setLayoutParams(new LinearLayout.LayoutParams(200, 0));
            }
            else
            {
                parentBottomViewHolder.bind.frameLayout1.setVisibility(View.VISIBLE);

                final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, context.getResources().getDisplayMetrics());
                parentBottomViewHolder.bind.frameLayout1.setLayoutParams(new LinearLayout.LayoutParams(200, height));
            }

            parentBottomViewHolder.init(this.context);
        }
        else {
            ParentViewHolder parentViewHolder = (ParentViewHolder) ViewHolder;

            ParentItemViewModel parentItemViewModel = (ParentItemViewModel) list.get(i);
            parentViewHolder.bind(parentItemViewModel);

            ParentItemBinding parentItemBinding = parentViewHolder.getParentItemBinding();
            LinearLayoutManager layoutManager = new LinearLayoutManager(parentItemBinding.childRecyclerview.getContext(), LinearLayoutManager.HORIZONTAL, false);

            int size = parentItemViewModel.getSizeContest();
            layoutManager.setInitialPrefetchItemCount(size);

            //ChildItemAdapter childItemAdapter = new ChildItemAdapter(parentItemViewModel.getMeditationCategory().contests, this.context, listener);
            ChildItemAdapter childItemAdapter = new ChildItemAdapter(parentItemViewModel.getMeditationCategory(), this.context, listener);
            parentItemBinding.childRecyclerview.setLayoutManager(layoutManager);
            parentItemBinding.childRecyclerview.setAdapter(childItemAdapter);
            parentItemBinding.childRecyclerview.setRecycledViewPool(viewPool);

        }
    }

    private void initPage(int page)
    {
        parentMiddleViewHolder.viewPager.setCurrentItem(page);
        addBottomDots(page, parentMiddleViewHolder.ll_dots);
    }

    public void startRun()
    {
                        // 6초 테스트
        if(countDownTimer == null) {
            countDownTimer = new CountDownTimer(4 * 1000, 1000) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {

                    SliderPagerAdapter sliderPagerAdapter = parentMiddleViewHolder.getSliderPagerAdapter();

                    if(sliderPagerAdapter != null) {
                        if (sliderPagerAdapter.getCount() == page) {
                            page = 0;
                        } else {
                            page++;
                        }
                        parentMiddleViewHolder.viewPager.setCurrentItem(page);
                    }

                }
            }.start();
        }
        else
        {
            countDownTimer.start();
        }

//        pauseRun();
//        mhandler.postDelayed(mRunnable = new Runnable() {
//            @Override
//            public void run() {
//
//                SliderPagerAdapter sliderPagerAdapter = parentMiddleViewHolder.getSliderPagerAdapter();
//
//                if(sliderPagerAdapter != null) {
//                    if (sliderPagerAdapter.getCount() == page) {
//                        page = 0;
//                    } else {
//                        page++;
//                    }
//                    parentMiddleViewHolder.viewPager.setCurrentItem(page);
//                    //mhandler.postDelayed(this, 4000);
//                }
//            }
//        }, 4000);
    }

    public void pauseRun()
    {
        countDownTimer.cancel();

//        if(mhandler != null) {
//            mhandler.removeCallbacks(mRunnable);
//
//            mhandler.removeCallbacksAndMessages(null);
//        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    //showing dots on screen
    private void addBottomDots(int currentPage, LinearLayout ll_dots) {
        TextView[] dots = new TextView[slider_image_list.size()];
        ll_dots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(context);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(Color.parseColor("#b9c4db"));
            ll_dots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(Color.parseColor("#3a5da7"));
    }

    static class ParentTopViewHolder extends RecyclerView.ViewHolder {

        private ParentTopItemBinding bind;
        private ParentTopItemViewModel viewModel;

        public ParentTopViewHolder(ParentTopItemBinding parentTopItemBinding) {
            super(parentTopItemBinding.getRoot());
            this.bind = parentTopItemBinding;
        }

        public void bind(ParentTopItemViewModel parentTopItemViewModel) {
            this.bind.setViewModel(parentTopItemViewModel);
            this.viewModel = parentTopItemViewModel;
        }

        public void init(Context context) {

        }

        public ParentTopItemBinding getParentTopItemBinding() {
            return bind;
        }
    }

    static class ParentViewHolder extends RecyclerView.ViewHolder {

        private ParentItemBinding parentItemBinding;

        public ParentViewHolder(ParentItemBinding parentItemBinding) {
            super(parentItemBinding.getRoot());
            this.parentItemBinding = parentItemBinding;
        }

        public void bind(ParentItemViewModel parentItemViewModel) {
            this.parentItemBinding.setViewModel(parentItemViewModel);
        }

        public ParentItemBinding getParentItemBinding() {
            return parentItemBinding;
        }
    }

    static class ParentMiddleViewHolder extends RecyclerView.ViewHolder {

        final public LinearLayout ll_dots;
        final public NetServiceViewPager viewPager;
        private SliderPagerAdapter sliderPagerAdapter;

        private ParentMiddleItemBinding bind;

        public ParentMiddleViewHolder(ParentMiddleItemBinding parentMiddleItemBinding) {
            super(parentMiddleItemBinding.getRoot());
            this.bind = parentMiddleItemBinding;

            ll_dots = this.bind.llDots;
            viewPager = this.bind.vpSlider;
        }

        public void setSliderPageAdapter(SliderPagerAdapter sliderPagerAdapter)
        {
            this.sliderPagerAdapter = sliderPagerAdapter;
        }

        public SliderPagerAdapter getSliderPagerAdapter()
        {
            return sliderPagerAdapter;
        }

        public void bind(ParentMiddleItemViewModel parentMiddleItemViewModel) {
            this.bind.setViewModel(parentMiddleItemViewModel);
        }

        public ParentMiddleItemBinding getParentMiddleItemBinding() {
            return bind;
        }
    }

    static class ParentBottomViewHolder extends RecyclerView.ViewHolder {

        private ParentBottomItemBinding bind;
        private ParentBottomItemViewModel viewModel;

        public ParentBottomViewHolder(ParentBottomItemBinding parentBottomItemBinding) {
            super(parentBottomItemBinding.getRoot());
            this.bind = parentBottomItemBinding;
        }

        public void bind(ParentBottomItemViewModel parentBottomItemViewModel) {
            this.bind.setViewModel(parentBottomItemViewModel);
            this.viewModel = parentBottomItemViewModel;
        }

        public void init(Context context) {

        }

        public ParentBottomItemBinding getParentBottomItemBinding() {
            return bind;
        }
    }
}