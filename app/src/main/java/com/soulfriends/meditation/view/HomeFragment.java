package com.soulfriends.meditation.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.model.MeditationCategory;
import com.soulfriends.meditation.model.MeditationContents;
import com.soulfriends.meditation.model.MeditationShowCategorys;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.util.ItemClickListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.view.nested.ParentBottomItemViewModel;
import com.soulfriends.meditation.view.nested.ParentItemAdapter;
import com.soulfriends.meditation.view.nested.ParentItemViewModel;
import com.soulfriends.meditation.view.nested.ParentMiddleItemViewModel;
import com.soulfriends.meditation.view.nested.ParentTopItemViewModel;
import com.soulfriends.meditation.view.player.MeditationAudioManager;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements ItemClickListener {

    private UserProfile userProfile;

    private MeditationShowCategorys meditationShowCategorys;

    private ParentItemAdapter parentItemAdapter;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView ParentRecyclerViewItem = view.findViewById(R.id.parent_recyclerview);

        // 정보 요청
        userProfile = NetServiceManager.getinstance().getUserProfile();

        boolean mIsDoneTest = false;
        if (userProfile.emotiontype == 0) {
        } else {
            mIsDoneTest = true;
        }

        meditationShowCategorys = NetServiceManager.getinstance().reqMediationType(1, mIsDoneTest);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        parentItemAdapter = new ParentItemAdapter(ParentItemList(), container.getContext(), this, UtilAPI.s_psychology_state);
        UtilAPI.s_psychology_state = -1;

        ParentRecyclerViewItem.setAdapter(parentItemAdapter);
        ParentRecyclerViewItem.setLayoutManager(layoutManager);
        ParentRecyclerViewItem.setNestedScrollingEnabled(false);  // 12.02.괜찮은듯

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(parentItemAdapter != null)
        {
            parentItemAdapter.UpdateTopItem();
        }
    }

    private List<ParentItemViewModel> ParentItemList()
    {
        List list = new ArrayList<>();

        // 0
        {
            ParentTopItemViewModel parentTopItemViewModel = new ParentTopItemViewModel(userProfile.nickname, userProfile);
            list.add(parentTopItemViewModel);
        }

        // 1
        {
            ParentMiddleItemViewModel parentMiddleItemViewModel = new ParentMiddleItemViewModel(userProfile.nickname, userProfile);
            list.add(parentMiddleItemViewModel);
        }


        // 3
        {
            ParentBottomItemViewModel parentBottomItemViewModel = new ParentBottomItemViewModel();
            list.add(parentBottomItemViewModel);
        }

        // 2
        int num = meditationShowCategorys.showcategorys.size();
        for(int i = 0; i < num ; i++) {

            MeditationCategory entity = meditationShowCategorys.showcategorys.get(i);

            ParentItemViewModel parentItemViewModel = new ParentItemViewModel(entity);

            list.add(parentItemViewModel);
        }

        // 3
        {
            ParentBottomItemViewModel parentBottomItemViewModel = new ParentBottomItemViewModel();
            list.add(parentBottomItemViewModel);
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


                    UtilAPI.s_StrMainFragment = UtilAPI.FRAGMENT_HOME;
                    getActivity().finish();
                }
                else
                {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), PsychologyListActivity.class);
                    getActivity().startActivity(intent);


                    UtilAPI.s_StrMainFragment = UtilAPI.FRAGMENT_HOME;
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

                    UtilAPI.s_StrMainFragment = UtilAPI.FRAGMENT_HOME;
                    getActivity().finish();
                }
                else
                {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), PsychologyCharacterListActivity.class);
                    getActivity().startActivity(intent);


                    UtilAPI.s_StrMainFragment = UtilAPI.FRAGMENT_HOME;
                    getActivity().finish();
                }

            }
            else if(pos == 3) {

                // 심리검사 다시하기 버튼 클릭시
                Intent intent = new Intent();
                intent.setClass(getActivity(), PsychologyListActivity.class);
                getActivity().startActivity(intent);


                UtilAPI.s_StrMainFragment = UtilAPI.FRAGMENT_HOME;
                getActivity().finish();

            }
            else if(pos == 4) {
                // 성격검사 다시하기 버튼 클릭시
                Intent intent = new Intent();
                intent.setClass(getActivity(), PsychologyCharacterListActivity.class);
                getActivity().startActivity(intent);


                UtilAPI.s_StrMainFragment = UtilAPI.FRAGMENT_HOME;
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

            Intent intent = new Intent();
            intent.setClass(getActivity(), PlayerActivity.class);
            getActivity().startActivity(intent);

            getActivity().finish();
        }
    }
}