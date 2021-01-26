package com.soulfriends.meditation.view.nestedext;

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
import com.soulfriends.meditation.databinding.ParentBottomItemExtBinding;
import com.soulfriends.meditation.databinding.ParentItemBinding;
import com.soulfriends.meditation.databinding.ParentItemExtBinding;
import com.soulfriends.meditation.databinding.ParentMiddleItemBinding;
import com.soulfriends.meditation.databinding.ParentMiddleItemExtBinding;
import com.soulfriends.meditation.databinding.ParentTopItemBinding;
import com.soulfriends.meditation.databinding.ParentTopItemExtBinding;
import com.soulfriends.meditation.netservice.NetServiceViewPager;
import com.soulfriends.meditation.util.ItemClickListener;
import com.soulfriends.meditation.view.nested.ChildItemAdapter;
import com.soulfriends.meditation.view.nested.ParentBottomItemViewModel;
import com.soulfriends.meditation.view.nested.ParentItemAdapter;
import com.soulfriends.meditation.view.nested.ParentItemViewModel;
import com.soulfriends.meditation.view.nested.ParentMiddleItemViewModel;
import com.soulfriends.meditation.view.nested.ParentTopItemViewModel;
import com.soulfriends.meditation.view.nested.SliderPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ParentItemExtAdapter extends RecyclerView.Adapter{
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List list;
    private Context context;
    private LayoutInflater inflater;

    private LayoutInflater inflater_top;

    private LayoutInflater inflater_middle;

    private LayoutInflater inflater_bottom;

    private ItemClickListener listener;

    private ParentItemExtAdapter.ParentTopExtViewHolder parentTopExtViewHolder = null;

    private ParentItemExtAdapter.ParentMiddleExtViewHolder parentMiddleExtViewHolder = null;

    private ParentItemExtAdapter.ParentBottomExtViewHolder parentBottomExtViewHolder = null;

    private ArrayList<String> slider_image_list;

    private int page = 0;

    public Handler mhandler;
    public Runnable mRunnable;

    private int page_state = 0;

    private int page_init = -1;

    private CountDownTimer countDownTimer;


    public ParentItemExtAdapter(List list, Context context, ItemClickListener listener, int page_init) {
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

            ParentTopItemExtBinding parentTopItemExtBinding = ParentTopItemExtBinding.inflate(inflater_top, viewGroup, false);
            return new ParentItemExtAdapter.ParentTopExtViewHolder(parentTopItemExtBinding);

        } else if (this.getItemViewType(i) == 1) {

            if (inflater_middle == null) {
                inflater_middle = LayoutInflater.from(viewGroup.getContext());
            }

            ParentMiddleItemExtBinding parentMiddleItemExtBinding = ParentMiddleItemExtBinding.inflate(inflater_middle, viewGroup, false);
            return new ParentItemExtAdapter.ParentMiddleExtViewHolder(parentMiddleItemExtBinding);

        } else if(this.getItemViewType(i) == 2){

            if (inflater_bottom == null) {
                inflater_bottom = LayoutInflater.from(viewGroup.getContext());
            }


            ParentBottomItemExtBinding parentBottomItemExtBinding = ParentBottomItemExtBinding.inflate(inflater_bottom, viewGroup, false);
            return new ParentItemExtAdapter.ParentBottomExtViewHolder(parentBottomItemExtBinding);
        }
        else {
            // 3
            if (inflater == null) {
                inflater = LayoutInflater.from(viewGroup.getContext());
            }

            ParentItemExtBinding parentItemExtBinding = ParentItemExtBinding.inflate(inflater, viewGroup, false);
            return new ParentItemExtAdapter.ParentExtViewHolder(parentItemExtBinding);
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (list.get(position) instanceof ParentTopItemExtViewModel) {
            return 0;
        } else if (list.get(position) instanceof ParentMiddleItemExtViewModel) {
            return 1;
        }
        else if (list.get(position) instanceof ParentBottomItemExtViewModel) {
            return 2;
        }
        return 3;
    }

    public void UpdateTopItem() {
        if (parentTopExtViewHolder != null) {
            parentTopExtViewHolder.init(this.context);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder ViewHolder, int i) {

        if (this.getItemViewType(i) == 0) {

            parentTopExtViewHolder = (ParentItemExtAdapter.ParentTopExtViewHolder) ViewHolder;

            ParentTopItemExtViewModel parentTopItemExtViewModel = (ParentTopItemExtViewModel) list.get(i);
            parentTopExtViewHolder.bind(parentTopItemExtViewModel);

            parentTopExtViewHolder.init(this.context);

            // 일단 주석 dlsmdla
//            ImageView imageView = parentTopViewHolder.getParentTopItemBinding().imgChildItem;
//            imageView.setOnClickListener(v -> {
//
//                listener.onItemClick((Object) imageView, 0);
//            });

        } else if (this.getItemViewType(i) == 1) {

            parentMiddleExtViewHolder = (ParentItemExtAdapter.ParentMiddleExtViewHolder) ViewHolder;

            slider_image_list = new ArrayList<>();

            slider_image_list.add("1"); // 심리검사
            slider_image_list.add("2"); // 성격검사
            final SliderPagerExtAdapter sliderPagerExtAdapter = new SliderPagerExtAdapter((Activity) context, slider_image_list, listener);
            parentMiddleExtViewHolder.viewPager.setAdapter(sliderPagerExtAdapter);
            parentMiddleExtViewHolder.setSliderPageExtAdapter(sliderPagerExtAdapter);


            parentMiddleExtViewHolder.viewPager.SetParentItemExtAdapter(this);

            parentMiddleExtViewHolder.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {


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
                    addBottomDots(position, parentMiddleExtViewHolder.ll_dots);
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
            addBottomDots(0, parentMiddleExtViewHolder.ll_dots);

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

            parentBottomExtViewHolder = (ParentItemExtAdapter.ParentBottomExtViewHolder) ViewHolder;
            ParentBottomItemExtViewModel parentBottomItemExtViewModel = (ParentBottomItemExtViewModel) list.get(i);
            parentBottomExtViewHolder.bind(parentBottomItemExtViewModel);

            if(i < 4)
            {
                parentBottomExtViewHolder.bind.frameLayout1.setVisibility(View.GONE);
                parentBottomExtViewHolder.bind.frameLayout1.setLayoutParams(new LinearLayout.LayoutParams(200, 0));
            }
            else
            {
                parentBottomExtViewHolder.bind.frameLayout1.setVisibility(View.VISIBLE);

                final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, context.getResources().getDisplayMetrics());
                parentBottomExtViewHolder.bind.frameLayout1.setLayoutParams(new LinearLayout.LayoutParams(200, height));
            }

            parentBottomExtViewHolder.init(this.context);
        }
        else {
            ParentItemExtAdapter.ParentExtViewHolder parentExtViewHolder = (ParentItemExtAdapter.ParentExtViewHolder) ViewHolder;

            ParentItemExtViewModel parentItemExtViewModel = (ParentItemExtViewModel) list.get(i);
            parentExtViewHolder.bind(parentItemExtViewModel);

            ParentItemExtBinding parentItemExtBinding = parentExtViewHolder.getParentItemExtBinding();
            LinearLayoutManager layoutManager = new LinearLayoutManager(parentItemExtBinding.childRecyclerview.getContext(), LinearLayoutManager.HORIZONTAL, false);

            int size = parentItemExtViewModel.getSizeContest();
            layoutManager.setInitialPrefetchItemCount(size);

            //ChildItemAdapter childItemAdapter = new ChildItemAdapter(parentItemViewModel.getMeditationCategory().contests, this.context, listener);
            ChildItemExtAdapter childItemExtAdapter = new ChildItemExtAdapter(parentItemExtViewModel.getMeditationCategory(), this.context, listener);
            parentItemExtBinding.childRecyclerview.setLayoutManager(layoutManager);
            parentItemExtBinding.childRecyclerview.setAdapter(childItemExtAdapter);
            parentItemExtBinding.childRecyclerview.setRecycledViewPool(viewPool);

        }
    }

    private void initPage(int page)
    {
        parentMiddleExtViewHolder.viewPager.setCurrentItem(page);
        addBottomDots(page, parentMiddleExtViewHolder.ll_dots);
    }

    public void startRun()
    {
        // 6초 테스트
        if(countDownTimer == null) {
            countDownTimer = new CountDownTimer(4 * 1000, 1000) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {

                    SliderPagerExtAdapter sliderPagerExtAdapter = parentMiddleExtViewHolder.getSliderPagerExtAdapter();

                    if(sliderPagerExtAdapter != null) {
                        if (sliderPagerExtAdapter.getCount() == page) {
                            page = 0;
                        } else {
                            page++;
                        }
                        parentMiddleExtViewHolder.viewPager.setCurrentItem(page);
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

    static class ParentTopExtViewHolder extends RecyclerView.ViewHolder {

        private ParentTopItemExtBinding bind;
        private ParentTopItemExtViewModel viewModel;

        public ParentTopExtViewHolder(ParentTopItemExtBinding parentTopItemExtBinding) {
            super(parentTopItemExtBinding.getRoot());
            this.bind = parentTopItemExtBinding;
        }

        public void bind(ParentTopItemExtViewModel parentTopItemExtViewModel) {
            this.bind.setViewModel(parentTopItemExtViewModel);
            this.viewModel = parentTopItemExtViewModel;
        }

        public void init(Context context) {

        }

        public ParentTopItemExtBinding getParentTopItemExtBinding() {
            return bind;
        }
    }

    static class ParentExtViewHolder extends RecyclerView.ViewHolder {

        private ParentItemExtBinding parentItemExtBinding;

        public ParentExtViewHolder(ParentItemExtBinding parentItemExtBinding) {
            super(parentItemExtBinding.getRoot());
            this.parentItemExtBinding = parentItemExtBinding;
        }

        public void bind(ParentItemExtViewModel parentItemExtViewModel) {
            this.parentItemExtBinding.setViewModel(parentItemExtViewModel);
        }

        public ParentItemExtBinding getParentItemExtBinding() {
            return parentItemExtBinding;
        }
    }

    static class ParentMiddleExtViewHolder extends RecyclerView.ViewHolder {

        final public LinearLayout ll_dots;
        final public NetServiceExtViewPager viewPager;
        private SliderPagerExtAdapter sliderPagerExtAdapter;

        private ParentMiddleItemExtBinding bind;

        public ParentMiddleExtViewHolder(ParentMiddleItemExtBinding parentMiddleItemExtBinding) {
            super(parentMiddleItemExtBinding.getRoot());
            this.bind = parentMiddleItemExtBinding;

            ll_dots = this.bind.llDots;
            viewPager = this.bind.vpSlider;
        }

        public void setSliderPageExtAdapter(SliderPagerExtAdapter sliderPagerExtAdapter)
        {
            this.sliderPagerExtAdapter = sliderPagerExtAdapter;
        }

        public SliderPagerExtAdapter getSliderPagerExtAdapter()
        {
            return sliderPagerExtAdapter;
        }

        public void bind(ParentMiddleItemExtViewModel parentMiddleItemExtViewModel) {
            this.bind.setViewModel(parentMiddleItemExtViewModel);
        }

        public ParentMiddleItemExtBinding getParentMiddleItemExtBinding() {
            return bind;
        }
    }

    static class ParentBottomExtViewHolder extends RecyclerView.ViewHolder {

        private ParentBottomItemExtBinding bind;
        private ParentBottomItemExtViewModel viewModel;

        public ParentBottomExtViewHolder(ParentBottomItemExtBinding parentBottomItemExtBinding) {
            super(parentBottomItemExtBinding.getRoot());
            this.bind = parentBottomItemExtBinding;
        }

        public void bind(ParentBottomItemExtViewModel parentBottomItemExtViewModel) {
            this.bind.setViewModel(parentBottomItemExtViewModel);
            this.viewModel = parentBottomItemExtViewModel;
        }

        public void init(Context context) {

        }

        public ParentBottomItemExtBinding getParentBottomItemExtBinding() {
            return bind;
        }
    }
}
