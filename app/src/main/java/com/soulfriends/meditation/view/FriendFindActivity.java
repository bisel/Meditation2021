package com.soulfriends.meditation.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.FriendEditBinding;
import com.soulfriends.meditation.databinding.FriendFindBinding;
import com.soulfriends.meditation.dlg.AlertLineOnePopup;
import com.soulfriends.meditation.model.MeditationContents;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.util.ItemClickListenerExt;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.view.friend.FriendEditAdapter;
import com.soulfriends.meditation.view.friend.FriendEditItemViewModel;
import com.soulfriends.meditation.view.friend.FriendEmotionAdapter;
import com.soulfriends.meditation.view.friend.FriendEmotionItemViewModel;
import com.soulfriends.meditation.view.friend.FriendFindAdapter;
import com.soulfriends.meditation.view.friend.FriendFindItemViewModel;
import com.soulfriends.meditation.viewmodel.FriendEditViewModel;
import com.soulfriends.meditation.viewmodel.FriendEditViewModelFactory;
import com.soulfriends.meditation.viewmodel.FriendFindViewModel;
import com.soulfriends.meditation.viewmodel.FriendFindViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class FriendFindActivity extends BaseActivity implements ResultListener, ItemClickListenerExt {

    private FriendFindBinding binding;
    private FriendFindViewModel viewModel;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private FriendFindViewModelFactory friendFindViewModelFactory;


    private FriendFindAdapter friendFindAdapter;

    private List list_friend = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = DataBindingUtil.setContentView(this, R.layout.activity_friend_find);
        binding.setLifecycleOwner(this);

        if (friendFindViewModelFactory == null) {
            friendFindViewModelFactory = new FriendFindViewModelFactory(this, this);
        }
        viewModel = new ViewModelProvider(this.getViewModelStore(), friendFindViewModelFactory).get(FriendFindViewModel.class);
        binding.setViewModel(viewModel);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        //rcv.setLayoutManager(layoutManager);

        // 검색 버튼 눌렀을때만 하기 때문에
        // 초기에 리스트를 보여줄 필요가 없다.
        //ItemList();


        

        friendFindAdapter = new FriendFindAdapter(list_friend, this, this);

        binding.recyclerview.setAdapter(friendFindAdapter);
        binding.recyclerview.setLayoutManager(layoutManager);
        binding.recyclerview.setNestedScrollingEnabled(false);  // 12.02.괜찮은듯


        // 초기 설정
        binding.layoutNofind.setVisibility(View.VISIBLE);
        binding.recyclerview.setVisibility(View.GONE);

        binding.editText.setOnEditorActionListener(new EditText.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    //Toast.makeText(getApplicationContext(),"success db",Toast.LENGTH_SHORT).show();

                    Find_Friend();

                    hideKeyBoard();
                    binding.editText.clearFocus();

                    return true;
                }
                return false;
            }
        });
    }

    private List<FriendFindItemViewModel> ItemList(ArrayList<UserProfile> list_user) {
        //List list = new ArrayList<>();

        for (int i = 0; i < list_user.size(); i++)
        {
            UserProfile userProfile = list_user.get(i);

            // 친구 추가 friend_state == 0
            // 친구  friend_state == 1
            // 친구 요청 중 friend_state == 2

            int friend_state = -1;
            // 친구 인지 아닌지 판별
            if(NetServiceManager.getinstance().findFriends(userProfile.uid) != null)
            {
                // 친구 임
                friend_state = 1;
            }
            else
            {
                // 친구 아닌 상태
                // 친구 추가
                friend_state = 0;
            }

            // 현재 친구 요청 리스트
            if(NetServiceManager.getinstance().findFriendsRequest(userProfile.uid))
            {
                // 친구 요청중에 있는지 확인
                friend_state = 2;
            }

            FriendFindItemViewModel friendFindItemViewModel = new FriendFindItemViewModel(this, i, userProfile, friend_state);

            list_friend.add(friendFindItemViewModel);

//            if(i % 4 == 0) {
//                FriendFindItemViewModel friendFindItemViewModel = new FriendFindItemViewModel(this, String.valueOf(i),UserProfile, 0);
//
//                list_friend.add(friendFindItemViewModel);
//            }
//            else if(i % 3 == 1) {
//                FriendFindItemViewModel friendFindItemViewModel = new FriendFindItemViewModel(this, String.valueOf(i),UserProfile, 1);
//
//                list_friend.add(friendFindItemViewModel);
//            }
//            else
//            {
//                FriendFindItemViewModel friendFindItemViewModel = new FriendFindItemViewModel(this, String.valueOf(i), UserProfile, 2);
//
//                list_friend.add(friendFindItemViewModel);
//            }
        }

        return list_friend;
    }

    private void hideKeyBoard() {

        InputMethodManager imm;
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText())
        {
            View view = this.getCurrentFocus();
            if(view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    //---------------------------------------------------------
    // 친구 -> 버튼 선택 호출
    //---------------------------------------------------------
    @Override
    public void onItemClick(View view, Object obj, int pos) {

        switch(view.getId())
        {
            case R.id.iv_addbt:
            {
                // 친구추가
                // "친구로 추가하시겠습니까?" 팝업 띄운다.
                // 예 -> "친구요청중"
                AlertLineOnePopup alertDlg = new AlertLineOnePopup(this, this, AlertLineOnePopup.Dlg_Type.friend_add);
                alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDlg.show();

                alertDlg.iv_ok.setOnClickListener(v -> {

                    UserProfile userProfile = (UserProfile)obj;

                    // 3. 친구 추가
                    // sendFriendRequest(보내는 사람 uid,  받는 사람 uid)
                    //        => 성공이 되면 "친구요청중"이라고 ux 변경

                    NetServiceManager.getinstance().setOnSendFriendRequestListener(new NetServiceManager.OnSendFriendRequestListener() {
                        @Override
                        public void onSendFriendRequest(boolean validate) {

                            if(validate)
                            {
                                FriendFindItemViewModel friendFindItemViewModel = (FriendFindItemViewModel)list_friend.get(pos);
                                friendFindItemViewModel.friend_state = 2;  // 친구 요청

                                // 리사이클 데이터 변경에따른 ui 업데이트
                                friendFindAdapter.notifyDataSetChanged();

                                //Toast.makeText(this,"친구 요청중",Toast.LENGTH_SHORT).show();

                                alertDlg.dismiss();
                            }
                            else
                            {

                            }
                        }
                    });

                    NetServiceManager.getinstance().sendFriendRequest(NetServiceManager.getinstance().getUserProfile().uid,  userProfile.uid);
                });
            }
            break;

            case R.id.iv_friendbt:
            {
                // 친구
                // "친구 삭제하시겠습니까?" 팝업 띄운다.
                // 예 -> 서버에 요청보내고 나서 처리 해야함. 삭제
                AlertLineOnePopup alertDlg = new AlertLineOnePopup(this, this, AlertLineOnePopup.Dlg_Type.friend_delete);
                alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDlg.show();

                alertDlg.iv_ok.setOnClickListener(v -> {

                    UserProfile userProfile = (UserProfile)obj;

                    NetServiceManager.getinstance().setOnRemoveFriendListener(new NetServiceManager.OnRemoveFriendListener() {
                        @Override
                        public void onRemoveFriend(boolean validate) {

                            if(validate)
                            {
                                // 삭제
                                // 리사이클 데이터 변경에따른 ui 업데이트
                                //list_friend.remove(pos);
                                //friendFindAdapter.notifyItemRemoved(pos);
                                //friendFindAdapter.notifyItemRangeChanged(pos, list_friend.size());

                                //Toast.makeText(this,"친구 요청중",Toast.LENGTH_SHORT).show();

                                FriendFindItemViewModel friendFindItemViewModel = (FriendFindItemViewModel)list_friend.get(pos);
                                friendFindItemViewModel.friend_state = 0;  // 친구추가로 변경

                                // 리사이클 데이터 변경에따른 ui 업데이트
                                friendFindAdapter.notifyDataSetChanged();

                                //Toast.makeText(this,"친구 요청중",Toast.LENGTH_SHORT).show();

                                alertDlg.dismiss();
                            }
                            else
                            {

                            }
                        }
                    });

                    NetServiceManager.getinstance().removeFriend(NetServiceManager.getinstance().getUserProfile().uid,  userProfile.uid);

                });
            }
            break;

            case R.id.iv_requestbt:
            {
                // 친구 요청중
                // "친구 요청을 취소하시겠습니까?" 팝업 띄운다.
                // 예 -> "친구 추가 +"
                AlertLineOnePopup alertDlg = new AlertLineOnePopup(this, this, AlertLineOnePopup.Dlg_Type.friend_cancel);
                alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDlg.show();

                alertDlg.iv_ok.setOnClickListener(v -> {

                    UserProfile userProfile = (UserProfile)obj;


                    NetServiceManager.getinstance().setOnCancelFriendRequestListener(new NetServiceManager.OnCancelFriendRequestListener() {
                        @Override
                        public void onCancelFriendRequest(boolean validate) {

                            if(validate)
                            {
                                FriendFindItemViewModel friendFindItemViewModel = (FriendFindItemViewModel)list_friend.get(pos);
                                friendFindItemViewModel.friend_state = 0;  // 친구추가로 변경

                                // 리사이클 데이터 변경에따른 ui 업데이트
                                friendFindAdapter.notifyDataSetChanged();

                                //Toast.makeText(this,"친구 요청중",Toast.LENGTH_SHORT).show();

                                alertDlg.dismiss();
                            }
                            else
                            {

                            }
                        }
                    });

                    NetServiceManager.getinstance().cancelFriendRequest(NetServiceManager.getinstance().getUserProfile().uid,  userProfile.uid);

                    alertDlg.dismiss();

                });
            }
            break;
        }
    }

    @Override
    public void onSuccess(Integer id, String message) {

        switch(id)
        {
            case R.id.ic_close:
            {
                // 닫기
                finish();

            }
            break;
            case R.id.iv_find:
            {
                String strInputWord = viewModel.inputword.getValue();

                if(strInputWord.length() > 0) {

                    // 찾기
                    Find_Friend();

                    Toast.makeText(this, "친구 검색 중", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // 친구가 없음 표시
                    binding.layoutNofind.setVisibility(View.VISIBLE);
                    binding.recyclerview.setVisibility(View.GONE);
                }

                binding.editText.clearFocus();

            }
            break;

        }
    }

    private void Find_Friend()
    {

        // 서버에 요청 메시지를 보낸다.
        // 돋보기 눌렸을때 이벤트
        // 1. recvFindUserList : 돋보기 누른 시점
        //      - mFindUserList
        NetServiceManager.getinstance().setOnRecvFindUserListListener(new NetServiceManager.OnRecvFindUserListListener() {
            @Override
            public void onRecvFindUserList(boolean validate) {
                if(validate)
                {
                    ArrayList<UserProfile> list = NetServiceManager.getinstance().mFindUserList;

                    int total_friend = list.size();
                    if(total_friend == 0) {

                        // 친구가 없다면
                        binding.layoutNofind.setVisibility(View.VISIBLE);
                        binding.recyclerview.setVisibility(View.GONE);

                        // clear
                        list_friend.clear();
                        friendFindAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                        // 친구가 있다면
                        binding.layoutNofind.setVisibility(View.GONE);
                        binding.recyclerview.setVisibility(View.VISIBLE);

                        // 클리어
                        list_friend.clear();

                        // 호출하도록 한다.
                        ItemList(list);

                        friendFindAdapter.notifyDataSetChanged();

                    }
                }
                else
                {

                    // 실패 한경우 
                    // 친구가 없음 표시 처리
                    // 친구가 없다면
                    binding.layoutNofind.setVisibility(View.VISIBLE);
                    binding.recyclerview.setVisibility(View.GONE);

                    // clear
                    list_friend.clear();
                    friendFindAdapter.notifyDataSetChanged();
                }
            }
        });

        NetServiceManager.getinstance().recvFindUserList(viewModel.inputword.getValue());
        
    }

    @Override
    public void onFailure(Integer id, String message) {

    }

    @Override
    public void onBackPressed() {

        // 닫기
        finish();
    }
}