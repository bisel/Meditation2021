package com.soulfriends.meditation.view.nestedext;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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
import com.soulfriends.meditation.util.UtilAPI;

import java.util.ArrayList;

public class SliderPagerExtAdapter extends PagerAdapter {
    private LayoutInflater layoutInflater;
    Activity activity;
    ArrayList<String> list_array;

    private ItemClickListener listener;

    public SliderPagerExtAdapter(Activity activity, ArrayList<String> list_array, ItemClickListener listener) {
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

        View view = layoutInflater.inflate(R.layout.layout_slider_ext, container, false);


        TextView tv_nickname = view.findViewById(R.id.tv_nickname_state);

        ImageView iv_image = view.findViewById(R.id.img_child_item);

        TextView tv_text = view.findViewById(R.id.child_item_title);

        ConstraintLayout retry_layout = view.findViewById(R.id.retrylayout);

        ImageView iv_text_bar = view.findViewById(R.id.iv_text_bar);

        String strValue = list_array.get(position);

        ImageView retrybtnimg = view.findViewById(R.id.retrybtnimg);

        UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();


        if(strValue == "1")
        {
            //--------------------------------------------------------
            //
            // "1" 심리검사
            //
            //--------------------------------------------------------
            UtilAPI.setImageResource(iv_text_bar, R.drawable.main_test_textbg);

            // nickname feeling state
            if (userProfile.nickname != null) {

                String strQuest = "";
                // 심리 검사
                strQuest = userProfile.nickname + " " + activity.getResources().getString(R.string.feel_state_quest);

                int end_nick = userProfile.nickname.length();

                if (end_nick > 0) {
                    Spannable wordtoSpan = new SpannableString(strQuest);
                    wordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(179, 179, 227)), 0, end_nick, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    wordtoSpan.setSpan(new ForegroundColorSpan(Color.WHITE), end_nick + 1, strQuest.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_nickname.setText(wordtoSpan);
                }
            }


            // 심리검사
            // - 24시간 주기로 심리검사 초기화함
            // - 00시를 기준으로 함
            // - 하루 1회 심리검사 권유 문구 표기

            //String psy_reult_time = PreferenceManager.getString(context, "psy_result_time");

            String psy_reult_time = userProfile.finaltestdate;

            if (psy_reult_time != null) {
                if (psy_reult_time.length() > 0) {
                    // 1일 차이 여부
                    long day = UtilAPI.GetDay_Date(psy_reult_time);
                    if (day > 0) {
                        // 심리 검사 유도하도록 문구 나오도록 한다.
                        userProfile.emotiontype = 0; // 초기화 한다.

                        NetServiceManager.getinstance().setOnRecvValProfileListener(new NetServiceManager.OnRecvValProfileListener() {
                            @Override
                            public void onRecvValProfile(boolean validate) {
                                if (validate == true) {
                                    int xx = 0;
                                } else {
                                    int yy = 0;
                                }
                            }
                        });

                        NetServiceManager.getinstance().sendValProfile(userProfile);
                    }
                }
            }

            // nickname feeling state
            ResultData resultData = NetServiceManager.getinstance().getResultData(userProfile.emotiontype);
            tv_text.setText(resultData.state);

            int res_id_1 = activity.getResources().getIdentifier(resultData.emotionimg, "drawable", activity.getPackageName());
            //UtilAPI.setImage(context, bind.imgChildItem, res_id_1);
            UtilAPI.setImageResource(iv_image, res_id_1);

            if(NetServiceManager.getinstance().getUserProfile().emotiontype == 0){
                retry_layout.setVisibility(View.GONE);
            }else{
                retry_layout.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            //--------------------------------------------------------
            //
            // "2" 성격검사
            //
            //--------------------------------------------------------
            UtilAPI.setImageResource(iv_text_bar, R.drawable.main_test_chrctr_textbg);

            // nickname feeling state
            if (userProfile.nickname != null) {

                String strQuest = "";
                // 성격 검사
                strQuest = userProfile.nickname + " " + activity.getResources().getString(R.string.feel_state_char_quest);

                int end_nick = userProfile.nickname.length();

                if (end_nick > 0) {
                    Spannable wordtoSpan = new SpannableString(strQuest);
                    wordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(179, 179, 227)), 0, end_nick, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    wordtoSpan.setSpan(new ForegroundColorSpan(Color.WHITE), end_nick + 1, strQuest.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_nickname.setText(wordtoSpan);
                }
            }


            // 성격검사
            // - 24시간 주기로 심리검사 초기화함
            // - 00시를 기준으로 함
            // - 하루 1회 심리검사 권유 문구 표기

            String psy_reult_time = userProfile.finalchartestdate;

//            if (psy_reult_time != null) {
//                if (psy_reult_time.length() > 0) {
//                    // 1일 차이 여부
//                    long day = UtilAPI.GetDay_Date(psy_reult_time);
//                    if (day > 0) {
//                        // 성격 검사 유도하도록 문구 나오도록 한다.
//                       userProfile.chartype = 0; // 초기화 한다.
//
//                        NetServiceManager.getinstance().setOnRecvValProfileListener(new NetServiceManager.OnRecvValProfileListener() {
//                            @Override
//                            public void onRecvValProfile(boolean validate) {
//                                if (validate == true) {
//                                    int xx = 0;
//                                } else {
//                                    int yy = 0;
//                                }
//                            }
//                        });
//
//                        NetServiceManager.getinstance().sendValProfile(userProfile);
//                    }
//                }
//            }

            // nickname feeling state

            ArrayList<PersonResultData> list = NetServiceManager.getinstance().getPersonResultDataList();
            PersonResultData resultData = list.get(userProfile.chartype);
            tv_text.setText(resultData.title);

            int res_id_1 = activity.getResources().getIdentifier(resultData.img, "drawable", activity.getPackageName());
            //UtilAPI.setImage(context, bind.imgChildItem, res_id_1);
            UtilAPI.setImageResource(iv_image, res_id_1);

            if(NetServiceManager.getinstance().getUserProfile().chartype == 0){
                retry_layout.setVisibility(View.GONE);
            }else{
                retry_layout.setVisibility(View.VISIBLE);
            }
        }



        retrybtnimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(position==0)
                {
                    listener.onItemClick((Object) iv_image , 3);
                    //Toast.makeText(activity, "first Position", Toast.LENGTH_SHORT).show();
                }
                if(position==1)
                {
                    listener.onItemClick((Object) iv_image, 4);
                    //Toast.makeText(activity, "second Position", Toast.LENGTH_SHORT).show();
                }

            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(position==0)
                {
                    listener.onItemClick((Object) iv_image , 1);
                    //Toast.makeText(activity, "first Position", Toast.LENGTH_SHORT).show();
                }
                if(position==1)
                {
                    listener.onItemClick((Object) iv_image, 2);
                    //Toast.makeText(activity, "second Position", Toast.LENGTH_SHORT).show();
                }

            }
        });

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
