package com.soulfriends.meditation.view.background;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.parser.PersonResultData;
import com.soulfriends.meditation.parser.ResultData;
import com.soulfriends.meditation.util.ItemClickListener;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;

import java.util.ArrayList;

public class BackGroundPagerAdapter extends PagerAdapter {

    private LayoutInflater layoutInflater;
    Activity activity;
    ArrayList<Integer> list_array;

    private ResultListener listener;

    public BackGroundPagerAdapter(Activity activity, ArrayList<Integer> list_array, ResultListener listener) {
        this.activity = activity;
        this.list_array = list_array;
        this.listener = listener;
    }


    @Override
    public int getCount() {
        return list_array.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.layout_slider_background, container, false);

        ImageView imageView = view.findViewById(R.id.iv_bg);

        //imageView.setImageResource(R.drawable.bg_1);
        imageView.setImageResource(list_array.get(position));

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
