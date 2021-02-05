package com.soulfriends.meditation.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.FriendEditBinding;
import com.soulfriends.meditation.model.MeditationDetailFriend;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.util.ActivityStack;
import com.soulfriends.meditation.util.ItemClickListenerExt;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.view.friend.FriendEditAdapter;
import com.soulfriends.meditation.view.friend.FriendEditItemViewModel;
import com.soulfriends.meditation.view.friend.FriendFindAdapter;
import com.soulfriends.meditation.view.friend.FriendFindItemViewModel;
import com.soulfriends.meditation.viewmodel.FriendEditViewModel;
import com.soulfriends.meditation.viewmodel.FriendEditViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class FriendEditActivity extends BaseActivity implements ResultListener, ItemClickListenerExt {

    private FriendEditBinding binding;
    private FriendEditViewModel viewModel;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private FriendEditViewModelFactory friendEditViewModelFactory;

    private FriendEditAdapter friendEditAdapter;

    private List list_friend = new ArrayList<>();

    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_friend_edit);
        binding.setLifecycleOwner(this);

        if (friendEditViewModelFactory == null) {
            friendEditViewModelFactory = new FriendEditViewModelFactory(this, this);
        }
        viewModel = new ViewModelProvider(this.getViewModelStore(), friendEditViewModelFactory).get(FriendEditViewModel.class);
        binding.setViewModel(viewModel);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //rcv.setLayoutManager(layoutManager);

//        list_friend = ItemList();
//        friendEditAdapter = new FriendEditAdapter(list_friend, this, this);
//
//        binding.recyclerview.setAdapter(friendEditAdapter);
//        binding.recyclerview.setLayoutManager(layoutManager);
//        binding.recyclerview.setNestedScrollingEnabled(false);  // 12.02.괜찮은듯

        NetServiceManager.getinstance().setOnRecvFriendsListener(new NetServiceManager.OnRecvFriendsListener() {
            @Override
            public void onRecvFriends(boolean validate) {

                DoFriendList();
//                if (validate) {
//                    DoFriendList();
//                } else {
//
//                }
            }
        });


        NetServiceManager.getinstance().recvFriendsList();
    }

    private void DoFriendList() {


        list_friend.clear();

        ArrayList<MeditationDetailFriend> list_user = NetServiceManager.getinstance().mDetialFriendsList;

        int count = 0;
        for (int i = 0; i < list_user.size(); i++) {
            MeditationDetailFriend meditationDetailFriend = list_user.get(i);

            FriendEditItemViewModel friendEditItemViewModel = new FriendEditItemViewModel(this, count, meditationDetailFriend.mUserProfile);

            list_friend.add(friendEditItemViewModel);
        }

        if(friendEditAdapter == null) {
            friendEditAdapter = new FriendEditAdapter(list_friend, this, this);
        }
        else {
            friendEditAdapter.SetList(list_friend);
        }

        binding.recyclerview.setAdapter(friendEditAdapter);
        binding.recyclerview.setLayoutManager(layoutManager);
        binding.recyclerview.setNestedScrollingEnabled(false);  // 12.02.괜찮은듯
    }

//    private List<FriendEditItemViewModel> ItemList() {
//
//
//
//
//
////
////        List list = new ArrayList<>();
////
////        for (int i = 0; i < 50; i++)
////        {
////            FriendEditItemViewModel friendEditItemViewModel = new FriendEditItemViewModel(this, String.valueOf(i) ,String.valueOf(i));
////
////            list.add(friendEditItemViewModel);
////        }
//
//        return list;
//    }

    @Override
    public void onSuccess(Integer id, String message) {

        switch (id) {
            case R.id.ic_close: {
                onBackPressed();
            }
            break;
            case R.id.iv_completebg: {
                onBackPressed();
            }
            break;
        }
    }

    @Override
    public void onFailure(Integer id, String message) {

    }

    @Override
    public void onItemClick(View view, Object obj, int pos) {

        switch (view.getId()) {
            case R.id.iv_icon:
            case R.id.tv_nickname: {
                // 프로필과 닉네임 선택시 친구 프로필로 이동 처리
                ActivityStack.instance().Push(this, "");

                UtilAPI.s_userProfile_friend = (UserProfile) obj;


                ChangeActivity(ProfileFriendActivity.class);

            }
            break;
            case R.id.iv_delete: {

                // 삭제 버튼 선택하면
                // 서버에 보내고 성공 이벤트 받고 리스트에서 삭제하도록 한다.
                // 일단 테스트로 삭제 기능
                //int index_id = Integer.parseInt((String)obj);

                UserProfile userProfile = (UserProfile) obj;

                NetServiceManager.getinstance().setOnRemoveFriendListener(new NetServiceManager.OnRemoveFriendListener() {
                    @Override
                    public void onRemoveFriend(boolean validate) {

                        if (validate) {
                            // 삭제
                            // 리사이클 데이터 변경에따른 ui 업데이트
                            if(list_friend.size() > 0) {
                                list_friend.remove(pos);
                                friendEditAdapter.notifyItemRemoved(pos);
                                friendEditAdapter.notifyItemRangeChanged(pos, list_friend.size());
                            }

                        } else {

                        }
                    }
                });

                NetServiceManager.getinstance().removeFriend(NetServiceManager.getinstance().getUserProfile().uid, userProfile.uid);
//
//                list_friend.remove(pos);
//                friendEditAdapter.notifyItemRemoved(pos);
//                friendEditAdapter.notifyItemRangeChanged(pos, list_friend.size());

//                String msg = "id = ";
//                msg += String.valueOf(index_id);
//                msg += " , pos = ";
//                msg += String.valueOf(pos);
//                Toast.makeText(this, msg ,Toast.LENGTH_SHORT).show();

            }
            break;

        }
    }


    @Override
    public void onBackPressed() {

        ActivityStack.instance().OnBack(this);

    }
}