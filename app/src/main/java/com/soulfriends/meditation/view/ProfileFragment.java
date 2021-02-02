package com.soulfriends.meditation.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.ProfileBinding;
import com.soulfriends.meditation.dlg.AlertLineOnePopup;
import com.soulfriends.meditation.dlg.AlertLineTwoPopup;
import com.soulfriends.meditation.model.MeditationCategory;
import com.soulfriends.meditation.model.MeditationContents;
import com.soulfriends.meditation.model.MeditationDetailFriend;
import com.soulfriends.meditation.model.MeditationShowCategorys;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.parser.PersonResultData;
import com.soulfriends.meditation.parser.ResultData;
import com.soulfriends.meditation.util.ActivityStack;
import com.soulfriends.meditation.util.ItemClickListener;
import com.soulfriends.meditation.util.ItemClickListenerExt;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.view.friend.FriendEditAdapter;
import com.soulfriends.meditation.view.friend.FriendEditItemViewModel;
import com.soulfriends.meditation.view.friend.FriendEmotionAdapter;
import com.soulfriends.meditation.view.friend.FriendEmotionItemViewModel;
import com.soulfriends.meditation.view.friend.FriendFindItemViewModel;
import com.soulfriends.meditation.view.nested.ParentItemViewModel;
import com.soulfriends.meditation.view.nestedext.ParentBottomItemExtViewModel;
import com.soulfriends.meditation.view.nestedext.ParentItemExtAdapter;
import com.soulfriends.meditation.view.nestedext.ParentItemExtViewModel;
import com.soulfriends.meditation.view.nestedext.ParentMiddleItemExtViewModel;
import com.soulfriends.meditation.view.nestedext.ParentTopItemExtViewModel;
import com.soulfriends.meditation.view.player.MeditationAudioManager;
import com.soulfriends.meditation.viewmodel.ProfileViewModel;
import com.soulfriends.meditation.viewmodel.ProfileViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment implements ItemClickListener, ItemClickListenerExt {

    private UserProfile userProfile;

    private MeditationShowCategorys meditationShowCategorys;

    private RecyclerView contents_RecyclerViewItem;
    private RecyclerView friend_RecyclerViewItem;

    private FriendEmotionAdapter friendEmotionAdapter;

    private List list_friend = new ArrayList<>();

    private int count_friend_recv = 0;

    private View layout_friend_bts;
    private View layout_friend_no;



    private LinearLayoutManager layoutManager_friend;

    private ViewGroup container;

    private int bFocusTab = 0;

    public ProfileFragment() {
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

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        this.container = container;
        //------------------------------------------------
        // 기타
        //------------------------------------------------
        layout_friend_bts = view.findViewById(R.id.layout_friend_bt);

        layout_friend_no = view.findViewById(R.id.layout_friend_no);

        layout_friend_bts.setVisibility(View.GONE);




        //------------------------------------------------
        // 소셜 정보 요청
        //------------------------------------------------
        NetServiceManager.getinstance().setOnSocialRecvContentsListener(new NetServiceManager.OnSocialRecvContentsListener() {
            @Override
            public void onSocialRecvContents(boolean validate) {
                if (validate == true) {

                    NetServiceManager.getinstance().reqSocialEmotionAllContents();

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
                        meditationShowCategorys = NetServiceManager.getinstance().reqMediationType(5, mIsDoneTest);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        ParentItemExtAdapter parentItemExtAdapter = new ParentItemExtAdapter(ParentItemList(), container.getContext(), ProfileFragment.this, UtilAPI.s_psychology_state);
                        UtilAPI.s_psychology_state = -1;

                        contents_RecyclerViewItem.setAdapter(parentItemExtAdapter);
                        contents_RecyclerViewItem.setLayoutManager(layoutManager);
                        contents_RecyclerViewItem.setNestedScrollingEnabled(false);  // 12.02.괜찮은듯

                        contents_RecyclerViewItem.setVisibility(View.VISIBLE);

                        //------------------------------------------------
                        // 친구
                        //------------------------------------------------
                        {
                            friend_RecyclerViewItem = view.findViewById(R.id.recyclerview_friend);
                            layoutManager_friend = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                        }

                        ImageView ivContentsBt = view.findViewById(R.id.iv_contentsbt);
                        ImageView ivFriendBt = view.findViewById(R.id.iv_friendbt);
                        
                        //------------------------------------------------
                        // 콘텐츠 탭 버튼 선택
                        //------------------------------------------------

                        if(UtilAPI.s_StrFragment_Profile_Tab == UtilAPI.TAB_CONTENTS)
                        {
                            ProfileFragment.this.contents_RecyclerViewItem.setVisibility(View.VISIBLE);

                            ProfileFragment.this.friend_RecyclerViewItem.setVisibility(View.GONE);

                            layout_friend_bts.setVisibility(View.GONE);
                            layout_friend_no.setVisibility(View.GONE);

                            UtilAPI.setImage(getContext(), ivContentsBt, R.drawable.social_mnbg);
                            UtilAPI.setImage(getContext(), ivFriendBt, R.drawable.social_mnbg_selected);
                        }
                        else
                        {
                            ProfileFragment.this.contents_RecyclerViewItem.setVisibility(View.GONE);

                            layout_friend_bts.setVisibility(View.VISIBLE);


                            UtilAPI.setImage(getContext(), ivContentsBt, R.drawable.social_mnbg_selected);
                            UtilAPI.setImage(getContext(), ivFriendBt, R.drawable.social_mnbg);

                            // 2. 현재 친구 리스트 : 친구 인지 아닌지 판별 , 감정친구인지
                            //  -> recvFriendsRequestList(normal) : 어느 특정 시작 시점
                            // 5. 감정 친구 요청중
                            //  -> recvFriendsRequestList("emotion") : 감정친구 요청 리스트 받는 함수

                            //--------------------------
                            // 1. 친구 노멀 리스트 요청
                            //--------------------------

                            NetServiceManager.getinstance().setOnRecvFriendsRequestListener(new NetServiceManager.OnRecvFriendsRequestListener() {
                                @Override
                                public void onRecvFriendsRequest(boolean validate) {
                                    DoEmotionFriendRequest();
                                }
                            });

                            NetServiceManager.getinstance().recvFriendsRequestList("normal");
                        }
                    }
                }
            }
        });

        NetServiceManager.getinstance().recvSocialContentsExt();

        // 버튼 구성
        Build_Button(view);

        return view;
    }

    public void Build_Button(View view)
    {
        ImageView ivContentsBt = view.findViewById(R.id.iv_contentsbt);
        ImageView ivFriendBt = view.findViewById(R.id.iv_friendbt);

        //------------------------------------------------
        // 콘텐츠 탭  버튼 선택
        //------------------------------------------------
        ivContentsBt.setOnClickListener(v -> {

            bFocusTab = 0;

            this.contents_RecyclerViewItem.setVisibility(View.VISIBLE);

            this.friend_RecyclerViewItem.setVisibility(View.GONE);

            layout_friend_bts.setVisibility(View.GONE);
            layout_friend_no.setVisibility(View.GONE);

            UtilAPI.setImage(getContext(), ivContentsBt, R.drawable.social_mnbg);
            UtilAPI.setImage(getContext(), ivFriendBt, R.drawable.social_mnbg_selected);

        });

        //------------------------------------------------
        // 친구 탭  버튼 선택
        //------------------------------------------------

        ivFriendBt.setOnClickListener(v -> {

            bFocusTab = 1;

            this.contents_RecyclerViewItem.setVisibility(View.GONE);

            layout_friend_bts.setVisibility(View.VISIBLE);


            UtilAPI.setImage(getContext(), ivContentsBt, R.drawable.social_mnbg_selected);
            UtilAPI.setImage(getContext(), ivFriendBt, R.drawable.social_mnbg);

            // 2. 현재 친구 리스트 : 친구 인지 아닌지 판별 , 감정친구인지
            //  -> recvFriendsRequestList(normal) : 어느 특정 시작 시점
            // 5. 감정 친구 요청중
            //  -> recvFriendsRequestList("emotion") : 감정친구 요청 리스트 받는 함수

            //--------------------------
            // 1. 친구 노멀 리스트 요청
            //--------------------------

            NetServiceManager.getinstance().setOnRecvFriendsRequestListener(new NetServiceManager.OnRecvFriendsRequestListener() {
                @Override
                public void onRecvFriendsRequest(boolean validate) {
                    DoEmotionFriendRequest();
                }
            });

            NetServiceManager.getinstance().recvFriendsRequestList("normal");
        });


        //------------------------------------------------
        // My 버튼 선택
        //------------------------------------------------
        ImageView iv_myBt = view.findViewById(R.id.iv_mybt);
        iv_myBt.setOnClickListener(v -> {

            // 내가 제작한 콘텐츠 액티비티 이동처리

            ActivityStack.instance().Push(getActivity(), ""); // 메인액티비티여야 된다.

            getActivity().startActivity(new Intent(getActivity(), MyContentsActivity.class));
            getActivity().overridePendingTransition(0, 0);

//            Intent intent = new Intent();
//            intent.setClass(getActivity(), MyContentsActivity.class);
//
//            getActivity().overridePendingTransition(0, 0);
//            getActivity().startActivity(intent);

            UtilAPI.s_StrMainFragment = UtilAPI.FRAGMENT_PROFILE;
            getActivity().finish();

        });

        //------------------------------------------------
        // + 버튼 선택
        //------------------------------------------------
        ImageView iv_addBt = view.findViewById(R.id.iv_addbt);
        iv_addBt.setOnClickListener(v -> {

            AlertLineTwoPopup alertDlg = new AlertLineTwoPopup(getActivity(), getActivity(), AlertLineTwoPopup.Dlg_Type.contents_existing_delete);

            alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDlg.show();
        });

        //------------------------------------------------
        // 친구 검색 버튼 선택
        //------------------------------------------------
        View ic_findbt = view.findViewById(R.id.ic_findbt);
        ic_findbt.setOnClickListener(v -> {

            ActivityStack.instance().Push(getActivity(), ""); // 메인액티비티여야 된다.

            getActivity().startActivity(new Intent(getActivity(), FriendFindActivity.class));
            getActivity().overridePendingTransition(0, 0);

            UtilAPI.s_StrMainFragment = UtilAPI.FRAGMENT_PROFILE;
            UtilAPI.s_StrFragment_Profile_Tab = UtilAPI.TAB_FRIEND;
            getActivity().finish();
        });

        //------------------------------------------------
        // 친구 편집 버튼 선택
        //------------------------------------------------
        View ic_editbt = view.findViewById(R.id.iv_editbt);
        ic_editbt.setOnClickListener(v -> {

            ActivityStack.instance().Push(getActivity(), ""); // 메인액티비티여야 된다.

            getActivity().startActivity(new Intent(getActivity(), FriendEditActivity.class));
            getActivity().overridePendingTransition(0, 0);

            UtilAPI.s_StrMainFragment = UtilAPI.FRAGMENT_PROFILE;
            UtilAPI.s_StrFragment_Profile_Tab = UtilAPI.TAB_FRIEND;
            getActivity().finish();
        });


        //------------------------------------------------
        // 친구 없는 경우
        // 찾기 버튼
        //------------------------------------------------
        View iv_find_friend_bt = view.findViewById(R.id.iv_find_friend_bt);
        iv_find_friend_bt.setOnClickListener(v -> {

            ActivityStack.instance().Push(getActivity(), ""); // 메인액티비티여야 된다.

            getActivity().startActivity(new Intent(getActivity(), FriendFindActivity.class));
            getActivity().overridePendingTransition(0, 0);

            UtilAPI.s_StrMainFragment = UtilAPI.FRAGMENT_PROFILE;
            UtilAPI.s_StrFragment_Profile_Tab = UtilAPI.TAB_FRIEND;
            getActivity().finish();
        });
    }

    public void DoEmotionFriendRequest()
    {
        NetServiceManager.getinstance().setOnRecvEmotionFriendsRequestListener(new NetServiceManager.OnRecvEmotionFriendsRequestListener() {
            @Override
            public void onRecvEmotionFriendsRequest(boolean validate) {
                DoRequest_FriendList();
            }
        });


        NetServiceManager.getinstance().recvFriendsRequestList("emotion");
    }

    public void DoRequest_FriendList()
    {
        NetServiceManager.getinstance().setOnRecvFriendsListener(new NetServiceManager.OnRecvFriendsListener() {
            @Override
            public void onRecvFriends(boolean validate) {

                DoFriendList();
//                if(validate)
//                {
//                    DoFriendList();
//                }
//                else
//                {
//
//                }
            }
        });


        // 친구 리스트 요청 할때 마다 NetServiceManager.getinstance().mDetialFriendsList 카운트가 증가되어 값을 준다.
        // dlsmdla 2021_0202 버그 !!!
        NetServiceManager.getinstance().recvFriendsList();
    }

    private void DoFriendList()
    {
        // 콘텐츠 탭일 경우는 리턴하도록 처리해야만 된다.
        if(bFocusTab == 0) return;

        list_friend.clear();

        ArrayList<MeditationDetailFriend> list_user = NetServiceManager.getinstance().mDetialFriendsList;

        if(list_user.size() > 0) {

            int count = 0;
            for (int i = 0; i < list_user.size(); i++) {
                MeditationDetailFriend meditationDetailFriend = list_user.get(i);

                String res = NetServiceManager.getinstance().findFriends(meditationDetailFriend.mUserProfile.uid);

                int emotion_type = -1;
                if (res.equals("normal")) {
                    // 감정공유 +
                    emotion_type = 2;
                } else if (res.equals("emotion")) {
                    // 감정상태
                    emotion_type = 0;
                } else {
                    // "감정 공유 요청 중"

                    if (NetServiceManager.getinstance().findEmotionFriendsRequest(meditationDetailFriend.mUserProfile.uid)) {
                        emotion_type = 1;
                    }
                }

                if (emotion_type > -1) {
                    FriendEmotionItemViewModel friendEmotionItemViewModel = new FriendEmotionItemViewModel(this, count, meditationDetailFriend.mUserProfile, emotion_type);
                    count++;
                    list_friend.add(friendEmotionItemViewModel);
                }
            }

            if(friendEmotionAdapter == null) {
                friendEmotionAdapter = new FriendEmotionAdapter(list_friend, container.getContext(), this);
            }

            friendEmotionAdapter.SetList(list_friend);
            friend_RecyclerViewItem.setAdapter(friendEmotionAdapter);
            friend_RecyclerViewItem.setLayoutManager(layoutManager_friend);
            friend_RecyclerViewItem.setNestedScrollingEnabled(false);
        }
        else
        {
            friend_RecyclerViewItem.setVisibility(View.GONE);
            layout_friend_bts.setVisibility(View.GONE);

            layout_friend_no.setVisibility(View.VISIBLE);
        }
    }


//    private List<FriendEmotionItemViewModel> ItemList_Friend() {
//
//        ArrayList<String> mEmotionFriendsRequestList = NetServiceManager.getinstance().mEmotionFriendsRequestList;
//
//        for (int i = 0; i < 50; i++)
//        {
//            if(i % 4 == 0) {
//                FriendEmotionItemViewModel friendEmotionItemViewModel = new FriendEmotionItemViewModel(this,  String.valueOf(i), 0);
//
//                list_friend.add(friendEmotionItemViewModel);
//            }
//            else if(i % 3 == 1) {
//                FriendEmotionItemViewModel friendEmotionItemViewModel = new FriendEmotionItemViewModel(this, String.valueOf(i),1);
//
//                list_friend.add(friendEmotionItemViewModel);
//            }
//            else
//            {
//                FriendEmotionItemViewModel friendFindItemViewModel = new FriendEmotionItemViewModel(this, String.valueOf(i),2);
//
//                list_friend.add(friendFindItemViewModel);
//            }
//        }
//
//        return list_friend;
//    }

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


                    UtilAPI.s_StrMainFragment = UtilAPI.FRAGMENT_PROFILE;
                    UtilAPI.s_StrFragment_Profile_Tab = UtilAPI.TAB_CONTENTS;
                    getActivity().finish();
                }
                else
                {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), PsychologyListActivity.class);
                    getActivity().startActivity(intent);


                    UtilAPI.s_StrMainFragment = UtilAPI.FRAGMENT_PROFILE;
                    UtilAPI.s_StrFragment_Profile_Tab = UtilAPI.TAB_CONTENTS;
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

                    UtilAPI.s_StrMainFragment = UtilAPI.FRAGMENT_PROFILE;
                    UtilAPI.s_StrFragment_Profile_Tab = UtilAPI.TAB_CONTENTS;
                    getActivity().finish();
                }
                else
                {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), PsychologyCharacterListActivity.class);
                    getActivity().startActivity(intent);


                    UtilAPI.s_StrMainFragment = UtilAPI.FRAGMENT_PROFILE;
                    UtilAPI.s_StrFragment_Profile_Tab = UtilAPI.TAB_CONTENTS;
                    getActivity().finish();
                }

            }
            else if(pos == 3) {

                // 심리검사 다시하기 버튼 클릭시
                Intent intent = new Intent();
                intent.setClass(getActivity(), PsychologyListActivity.class);
                getActivity().startActivity(intent);


                UtilAPI.s_StrMainFragment = UtilAPI.FRAGMENT_PROFILE;
                UtilAPI.s_StrFragment_Profile_Tab = UtilAPI.TAB_CONTENTS;
                getActivity().finish();

            }
            else if(pos == 4) {
                // 성격검사 다시하기 버튼 클릭시
                Intent intent = new Intent();
                intent.setClass(getActivity(), PsychologyCharacterListActivity.class);
                getActivity().startActivity(intent);


                UtilAPI.s_StrMainFragment = UtilAPI.FRAGMENT_PROFILE;
                UtilAPI.s_StrFragment_Profile_Tab = UtilAPI.TAB_CONTENTS;
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

    //---------------------------------------------------------
    // 친구 -> x , + 감정 버튼 선택했을때 호출되는 함수
    //---------------------------------------------------------
    @Override
    public void onItemClick(View view, Object obj, int pos) {

        switch(view.getId())
        {
            case R.id.tv_nickname:
            case R.id.iv_icon:
            {
                // 프로필과 닉네임 선택시 친구 프로필로 이동 처리
                ActivityStack.instance().Push(getActivity(), ""); // 메인액티비티여야 된다.

                UtilAPI.s_userProfile_friend = (UserProfile) obj;

                Intent intent = new Intent();
                intent.setClass(getActivity(), ProfileFriendActivity.class);
                getActivity().startActivity(intent);

                UtilAPI.s_StrMainFragment = UtilAPI.FRAGMENT_PROFILE;
                UtilAPI.s_StrFragment_Profile_Tab = UtilAPI.TAB_FRIEND;
                getActivity().finish();
            }
            break;
            case R.id.ic_close:
            {
                // 감정상태
                // "감정 공유를 취소하시겠습니까" 팝업 띄운다.
                // 예 -> "감정공유 요청"
                // 기획서에 색상 하늘색
                // 감정상태 x

                UserProfile userProfile = (UserProfile)obj;

                AlertLineOnePopup alertDlg = new AlertLineOnePopup(getActivity(), getActivity(), AlertLineOnePopup.Dlg_Type.emotion_cancel);
                alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDlg.show();

                alertDlg.iv_ok.setOnClickListener(v -> {


                    NetServiceManager.getinstance().setOnRemoveFriendListener(new NetServiceManager.OnRemoveFriendListener() {
                        @Override
                        public void onRemoveFriend(boolean validate) {

                            if(validate)
                            {
                                FriendEmotionItemViewModel friendEmotionItemViewModel = (FriendEmotionItemViewModel)list_friend.get(pos);
                                friendEmotionItemViewModel.emotion_state = 2;  // 감정공유 +

                                // 리사이클 데이터 변경에따른 ui 업데이트
                                friendEmotionAdapter.notifyDataSetChanged();

                                //Toast.makeText(getActivity(),"감정 공유를 취소",Toast.LENGTH_SHORT).show();

                                alertDlg.dismiss();
                            }
                            else
                            {

                            }
                        }
                    });


                    NetServiceManager.getinstance().removeEmotionFriend(NetServiceManager.getinstance().getUserProfile().uid,  userProfile.uid);

                });
            }
            break;

            case R.id.ic_close_ing:
            {
                // 감정 공유 요청중
                // "감정공유 요청을 취소하시겠습니까" 팝업 띄운다.
                // 예 -> "감정공유 요청"
                // 기획서에 색상 짙은 파란색
                // 감정 공유 요청 중 x

                UserProfile userProfile = (UserProfile)obj;

                AlertLineOnePopup alertDlg = new AlertLineOnePopup(getActivity(), getActivity(), AlertLineOnePopup.Dlg_Type.emotion_request_cancel);
                alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDlg.show();

                alertDlg.iv_ok.setOnClickListener(v -> {


                    NetServiceManager.getinstance().setOnCancelFriendRequestListener(new NetServiceManager.OnCancelFriendRequestListener() {
                        @Override
                        public void onCancelFriendRequest(boolean validate) {

                            if(validate)
                            {
                                FriendEmotionItemViewModel friendEmotionItemViewModel = (FriendEmotionItemViewModel)list_friend.get(pos);
                                friendEmotionItemViewModel.emotion_state = 2;  // 감정공유 요청

                                // 리사이클 데이터 변경에따른 ui 업데이트
                                friendEmotionAdapter.notifyDataSetChanged();

                                //Toast.makeText(getActivity(),"감정공유 요청을 취소",Toast.LENGTH_SHORT).show();

                                alertDlg.dismiss();
                            }
                            else
                            {

                            }
                        }
                    });


                    NetServiceManager.getinstance().cancelEmotionFriendRequest(NetServiceManager.getinstance().getUserProfile().uid,  userProfile.uid);


                });
            }
            break;

            case R.id.ic_close_req:
            {
                // 감정 공유 요청
                // "감정 공유를 요청하시겠습니까" 팝업 띄운다.
                // 예 -> "감정공유 요청 중"
                // 기획서에 색상 녹색 
                // 감정 공유 + 해당

                UserProfile userProfile = (UserProfile)obj;

                AlertLineOnePopup alertDlg = new AlertLineOnePopup(getActivity(), getActivity(), AlertLineOnePopup.Dlg_Type.emotion_request);
                alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDlg.show();

                alertDlg.iv_ok.setOnClickListener(v -> {


                    NetServiceManager.getinstance().setOnSendFriendRequestListener(new NetServiceManager.OnSendFriendRequestListener() {
                        @Override
                        public void onSendFriendRequest(boolean validate) {

                            if(validate)
                            {
                                FriendEmotionItemViewModel friendEmotionItemViewModel = (FriendEmotionItemViewModel)list_friend.get(pos);
                                friendEmotionItemViewModel.emotion_state = 1;  // 감정공유 요청 중

                                // 리사이클 데이터 변경에따른 ui 업데이트
                                friendEmotionAdapter.notifyDataSetChanged();

                                //Toast.makeText(getActivity(),"감정 공유를 요청",Toast.LENGTH_SHORT).show();

                                alertDlg.dismiss();
                            }
                            else
                            {

                            }
                        }
                    });

                    NetServiceManager.getinstance().sendEmotionFriendRequest(NetServiceManager.getinstance().getUserProfile().uid,  userProfile.uid);

                });
            }
            break;
        }
    }
}
