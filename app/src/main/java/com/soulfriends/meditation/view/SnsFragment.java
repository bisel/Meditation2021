package com.soulfriends.meditation.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.model.MeditationCategory;
import com.soulfriends.meditation.model.MeditationContents;
import com.soulfriends.meditation.model.MeditationShowCategorys;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.util.ActivityStack;
import com.soulfriends.meditation.util.ItemClickListener;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.view.friend.FriendEmotionAdapter;
import com.soulfriends.meditation.view.friend.FriendEmotionItemViewModel;
import com.soulfriends.meditation.view.nested.ParentItemViewModel;
import com.soulfriends.meditation.view.nestedext.ParentBottomItemExtViewModel;
import com.soulfriends.meditation.view.nestedext.ParentItemExtAdapter;
import com.soulfriends.meditation.view.nestedext.ParentItemExtViewModel;
import com.soulfriends.meditation.view.nestedext.ParentMiddleItemExtViewModel;
import com.soulfriends.meditation.view.nestedext.ParentTopItemExtViewModel;
import com.soulfriends.meditation.view.player.MeditationAudioManager;

import java.util.ArrayList;
import java.util.List;


public class SnsFragment extends Fragment implements ItemClickListener, ResultListener {

    private UserProfile userProfile;

    private MeditationShowCategorys meditationShowCategorys;

    private RecyclerView contents_RecyclerViewItem;
    private RecyclerView friend_RecyclerViewItem;

    public SnsFragment() {
    }

    public static SnsFragment newInstance() {
        SnsFragment fragment = new SnsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_music, container, false);

        //------------------------------------------------
        // 콘텐츠
        //------------------------------------------------
        {
            contents_RecyclerViewItem = view.findViewById(R.id.recyclerview_contents);

            // 정보 요청
            userProfile = NetServiceManager.getinstance().getUserProfile();

            boolean mIsDoneTest = false;
            if (userProfile.emotiontype == 0) {
            } else {
                mIsDoneTest = true;
            }
            meditationShowCategorys = NetServiceManager.getinstance().reqMediationType(4, mIsDoneTest);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            ParentItemExtAdapter parentItemExtAdapter = new ParentItemExtAdapter(ParentItemList(), container.getContext(), this, UtilAPI.s_psychology_state);
            UtilAPI.s_psychology_state = -1;

            contents_RecyclerViewItem.setAdapter(parentItemExtAdapter);
            contents_RecyclerViewItem.setLayoutManager(layoutManager);
            contents_RecyclerViewItem.setNestedScrollingEnabled(false);  // 12.02.괜찮은듯

            contents_RecyclerViewItem.setVisibility(View.VISIBLE);
        }

        //------------------------------------------------
        // 친구
        //------------------------------------------------

        {
//            friend_RecyclerViewItem = view.findViewById(R.id.recyclerview_friend);
//            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//            //rcv.setLayoutManager(layoutManager);
//            FriendEmotionAdapter friendEmotionAdapter = new FriendEmotionAdapter(ItemList_Friend(), container.getContext(), this);
//
//            friend_RecyclerViewItem.setAdapter(friendEmotionAdapter);
//            friend_RecyclerViewItem.setLayoutManager(layoutManager);
//            friend_RecyclerViewItem.setNestedScrollingEnabled(false);  // 12.02.괜찮은듯
//
//
//            friend_RecyclerViewItem.setVisibility(View.GONE);
        }

        //------------------------------------------------
        // 콘텐츠 버튼 선택
        //------------------------------------------------

        ImageView ivContentsBt = view.findViewById(R.id.iv_contentsbt);
        ivContentsBt.setOnClickListener(v -> {

            this.contents_RecyclerViewItem.setVisibility(View.VISIBLE);

            this.friend_RecyclerViewItem.setVisibility(View.GONE);
        });

        //------------------------------------------------
        // 친구 버튼 선택
        //------------------------------------------------

        ImageView ivFriendBt = view.findViewById(R.id.iv_friendbt);
        ivFriendBt.setOnClickListener(v -> {

            this.contents_RecyclerViewItem.setVisibility(View.GONE);

            this.friend_RecyclerViewItem.setVisibility(View.VISIBLE);
        });

        return view;
    }

    private List<FriendEmotionItemViewModel> ItemList_Friend() {
        List list = new ArrayList<>();
//
//        for (int i = 0; i < 50; i++)
//        {
//            if(i % 4 == 0) {
//                FriendEmotionItemViewModel friendEmotionItemViewModel = new FriendEmotionItemViewModel(this, 0);
//
//                list.add(friendEmotionItemViewModel);
//            }
//            else if(i % 3 == 1) {
//                FriendEmotionItemViewModel friendEmotionItemViewModel = new FriendEmotionItemViewModel(this, 1);
//
//                list.add(friendEmotionItemViewModel);
//            }
//            else
//            {
//                FriendEmotionItemViewModel friendFindItemViewModel = new FriendEmotionItemViewModel(this, 2);
//
//                list.add(friendFindItemViewModel);
//            }
//        }

        return list;
    }

    private List<ParentItemViewModel> ParentItemList()
    {
        List list = new ArrayList<>();

        // 0
        {
            ParentTopItemExtViewModel parentTopItemExtViewModel = new ParentTopItemExtViewModel(userProfile.nickname, userProfile);
            list.add(parentTopItemExtViewModel);
        }

        // 1
        {
            ParentMiddleItemExtViewModel parentMiddleItemExtViewModel = new ParentMiddleItemExtViewModel(userProfile.nickname, userProfile);
            list.add(parentMiddleItemExtViewModel);
        }

        // 3
        {
            ParentBottomItemExtViewModel parentBottomItemExtViewModel = new ParentBottomItemExtViewModel();
            list.add(parentBottomItemExtViewModel);
        }

        int num = meditationShowCategorys.showcategorys.size();
        for(int i = 0; i < num ; i++) {

            MeditationCategory entity = meditationShowCategorys.showcategorys.get(i);

            ParentItemExtViewModel parentItemExtViewModel = new ParentItemExtViewModel(entity);

            list.add(parentItemExtViewModel);
        }

        // 3
        {
            ParentBottomItemExtViewModel parentBottomItemExtViewModel = new ParentBottomItemExtViewModel();
            list.add(parentBottomItemExtViewModel);
        }

        return list;
    }

    @Override
    public void onItemClick(Object obj, int pos)
    {
        if (obj instanceof ImageView) {
            // Top Item 선택 한 경우
            //Toast.makeText(this.getContext(), "Top Item select", Toast.LENGTH_SHORT).show();

            if(pos == 1)
            {
                // 심리 검사 결과 화면

                UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();

                if(userProfile.emotiontype > 0) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), PsychologyDetailActivity.class);
                    getActivity().startActivity(intent);


                    UtilAPI.s_StrMainFragment = UtilAPI.FRAGMENT_MUSIC;
                    getActivity().finish();
                }
                else
                {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), PsychologyListActivity.class);
                    getActivity().startActivity(intent);


                    UtilAPI.s_StrMainFragment = UtilAPI.FRAGMENT_MUSIC;
                    getActivity().finish();
                }
            }
            else if(pos == 2) {

                // 성격 검사 결과 화면
                UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();

                if(userProfile.chartype> 0) {

                    Intent intent = new Intent();
                    intent.setClass(getActivity(), PsychologyCharacterDetailActivity.class);
                    getActivity().startActivity(intent);

                    UtilAPI.s_StrMainFragment = UtilAPI.FRAGMENT_MUSIC;
                    getActivity().finish();
                }
                else
                {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), PsychologyCharacterListActivity.class);
                    getActivity().startActivity(intent);


                    UtilAPI.s_StrMainFragment = UtilAPI.FRAGMENT_MUSIC;
                    getActivity().finish();
                }

            }
            else if(pos == 3) {

                // 심리검사 다시하기 버튼 클릭시
                Intent intent = new Intent();
                intent.setClass(getActivity(), PsychologyListActivity.class);
                getActivity().startActivity(intent);


                UtilAPI.s_StrMainFragment = UtilAPI.FRAGMENT_MUSIC;
                getActivity().finish();

            }
            else if(pos == 4) {
                // 성격검사 다시하기 버튼 클릭시
                Intent intent = new Intent();
                intent.setClass(getActivity(), PsychologyCharacterListActivity.class);
                getActivity().startActivity(intent);


                UtilAPI.s_StrMainFragment = UtilAPI.FRAGMENT_MUSIC;
                getActivity().finish();

            }
        }
        else {

            MeditationContents meditationContents = (MeditationContents) obj;

            //UtilAPI.Start_PlayerCheck(meditationContents.uid);

            // 다른 경우
            if(NetServiceManager.getinstance().getCur_contents() != null ) {
                if (NetServiceManager.getinstance().getCur_contents().uid != meditationContents.uid) {

                    MeditationAudioManager.with(getActivity()).stop();

                    if(MeditationAudioManager.with(getActivity()).getServiceBound()) {
                        MeditationAudioManager.with(getActivity()).unbind();
                    }
                }
            }

            NetServiceManager.getinstance().setCur_contents(meditationContents);

            //String str = meditationContents.uid;
            //Toast.makeText(this.getContext(), str, Toast.LENGTH_SHORT).show();

            ActivityStack.instance().Push(getActivity(), ""); // 메인액티비티여야 된다.

            Intent intent = new Intent();
            intent.setClass(getActivity(), PlayerActivity.class);
            getActivity().startActivity(intent);

            getActivity().finish();
        }
    }

    @Override
    public void onSuccess(Integer id, String message) {

    }

    @Override
    public void onFailure(Integer id, String message) {

    }
}