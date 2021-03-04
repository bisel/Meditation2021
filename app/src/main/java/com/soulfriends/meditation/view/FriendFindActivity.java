package com.soulfriends.meditation.view;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.FriendFindBinding;
import com.soulfriends.meditation.dlg.AlertAlreadyPopup;
import com.soulfriends.meditation.dlg.AlertLineOneOkPopup;
import com.soulfriends.meditation.dlg.AlertLineOnePopup;
import com.soulfriends.meditation.dlg.ErrorCodePopup;
import com.soulfriends.meditation.model.MeditationFriend;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.util.ActivityStack;
import com.soulfriends.meditation.util.ItemClickListenerExt;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.view.friend.FriendFindAdapter;
import com.soulfriends.meditation.view.friend.FriendFindItemViewModel;
import com.soulfriends.meditation.view.player.MeditationAudioManager;
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

        // network
        UtilAPI.SetNetConnection_Activity(this);

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

            // 현재 상대방이 나에게 친구 요청 중 -> 2021.02.09
            if(NetServiceManager.getinstance().findFriendsRecvRequest(userProfile.uid))
            {
                friend_state = 3;
            }

            // Profile UID와  OtherPlayer UID를 기준으로 확인 해야 한다.

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
            case R.id.iv_icon:
            case R.id.tv_nickname:
            {
                // 친구 프로필로 이동
                ActivityStack.instance().Push(this, "");

                UtilAPI.s_userProfile_friend = (UserProfile) obj;

                    ChangeActivity(ProfileFriendActivity.class);

            }
            break;
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
                        public void onSendFriendRequest(boolean validate,int errorCode) {

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
                                alertDlg.dismiss();

                                if(errorCode == 400){
                                    // 서버 로직상으로 이미 상대방이 자신에게 친구 요청한 상태 - 2021.02.23 -> 요청 응답

                                    ErrorCodePopup alertDlg_error = new ErrorCodePopup(FriendFindActivity.this, FriendFindActivity.this);
                                    alertDlg_error.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                    alertDlg_error.show();

                                    FriendFindItemViewModel friendFindItemViewModel = (FriendFindItemViewModel)list_friend.get(pos);
                                    String str_msg = friendFindItemViewModel.userProfile.nickname + FriendFindActivity.this.getResources().getString(R.string.dialog_error_code_400);
                                    alertDlg_error.textView.setText(str_msg);

                                    alertDlg_error.iv_ok.setOnClickListener(v -> {

                                        friendFindItemViewModel.friend_state = 3;  // 요청 응답

                                        // 리사이클 데이터 변경에따른 ui 업데이트
                                        friendFindAdapter.notifyDataSetChanged();


                                        alertDlg_error.dismiss();
                                    });


                                }else{
                                    // FireBase의 서버 오류
                                    ErrorCodePopup alertDlg_error = new ErrorCodePopup(FriendFindActivity.this, FriendFindActivity.this);
                                    alertDlg_error.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                    alertDlg_error.show();

                                    alertDlg_error.textView.setText(FriendFindActivity.this.getResources().getString(R.string.dialog_error_firebase));

                                    alertDlg_error.iv_ok.setOnClickListener(v -> {

                                        alertDlg_error.dismiss();
                                    });
                                }
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
                        public void onRemoveFriend(boolean validate,int errocode) {

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
                                alertDlg.dismiss();

                                // 1. 상대방이 이미 친구를 삭제 한 경우, 2021.02.23 , errcode 403
                                if(errocode == 403){

                                    ErrorCodePopup alertDlg_error = new ErrorCodePopup(FriendFindActivity.this, FriendFindActivity.this);
                                    alertDlg_error.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                    alertDlg_error.show();

                                    FriendFindItemViewModel friendFindItemViewModel = (FriendFindItemViewModel)list_friend.get(pos);
                                    String str_msg = friendFindItemViewModel.userProfile.nickname + FriendFindActivity.this.getResources().getString(R.string.dialog_error_code_403);
                                    alertDlg_error.textView.setText(str_msg);

                                    alertDlg_error.iv_ok.setOnClickListener(v -> {

                                        friendFindItemViewModel.friend_state = 0;  // 친구추가로 변경

                                        // 리사이클 데이터 변경에따른 ui 업데이트
                                        friendFindAdapter.notifyDataSetChanged();

                                        alertDlg_error.dismiss();
                                    });
                                }
                                else{
                                    ErrorCodePopup alertDlg_error = new ErrorCodePopup(FriendFindActivity.this, FriendFindActivity.this);
                                    alertDlg_error.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                    alertDlg_error.show();

                                    alertDlg_error.textView.setText(FriendFindActivity.this.getResources().getString(R.string.dialog_error_firebase));

                                    alertDlg_error.iv_ok.setOnClickListener(v -> {

                                        alertDlg_error.dismiss();
                                    });
                                }
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

                                //alertDlg.dismiss();
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

            case R.id.iv_request_answerbt:
            {

                // 여기에 분기해야 한다.
                UserProfile userProfile = (UserProfile)obj;

                NetServiceManager.getinstance().setOnCheckSendFriendRequest(new NetServiceManager.OnCheckSendFriendRequest() {
                    @Override
                    public void onCheckSendFriendRequest(boolean validate,int errocode) {

                        if(validate)
                        {
                            // 친구요청 응답
                            // 팝업을 띄운다.
                            // 거절, 수락 처리


                            AlertAlreadyPopup alertDlg = new AlertAlreadyPopup(FriendFindActivity.this, FriendFindActivity.this, AlertAlreadyPopup.Dlg_Type.friend, userProfile.nickname);
                            alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            alertDlg.show();

                            // 수락
                            alertDlg.iv_ok.setOnClickListener(v -> {
                                //  1 . 친구 신청 신청 (상대방이 나에게 신청)

                                NetServiceManager.getinstance().setOnAcceptFriendRequestListener(new NetServiceManager.OnAcceptFriendRequestListener() {
                                    @Override
                                    public void onAcceptFriendRequest(boolean validate, MeditationFriend friendinfo, int errorcode) {

                                        if (validate) {
                                            //Toast.makeText(this, "수락" ,Toast.LENGTH_SHORT).show();
                                            NetServiceManager.getinstance().addForceLocalFriends(userProfile, friendinfo);
                                            //상태 변경
                                            // "친구" 로 변경
                                            if (list_friend.size() > pos) {
                                                FriendFindItemViewModel friendFindItemViewModel = (FriendFindItemViewModel) list_friend.get(pos);
                                                friendFindItemViewModel.friend_state = 1;

                                                // 리사이클 데이터 변경에따른 ui 업데이트
                                                friendFindAdapter.notifyDataSetChanged();
                                            }
                                        } else {
                                            if (errorcode == 500) {

                                                // 해당 요청이 상대방이 취소해서 해당 메시지가 유효하지 않는 경우 : 500
                                                ErrorCodePopup alertDlg_error = new ErrorCodePopup(FriendFindActivity.this, FriendFindActivity.this);
                                                alertDlg_error.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                                alertDlg_error.show();

                                                alertDlg_error.textView.setText(FriendFindActivity.this.getResources().getString(R.string.dialog_error_code_500));

                                                alertDlg_error.iv_ok.setOnClickListener(v -> {

                                                    FriendFindItemViewModel friendFindItemViewModel = (FriendFindItemViewModel) list_friend.get(pos);
                                                    friendFindItemViewModel.friend_state = 0;  // 친구추가로 변경

                                                    // 리사이클 데이터 변경에따른 ui 업데이트
                                                    friendFindAdapter.notifyDataSetChanged();
                                                    alertDlg_error.dismiss();
                                                });
                                            }
                                        }
                                    }
                                });

                                NetServiceManager.getinstance().AcceptFriend(NetServiceManager.getinstance().getUserProfile().uid, userProfile.uid);

                                alertDlg.dismiss();

                            });

                            // 거절

                            alertDlg.iv_no.setOnClickListener(v -> {
                                //  1 . 친구 신청 신청 (상대방이 나에게 신청)
                                NetServiceManager.getinstance().setOnRejectFriendRequestListener(new NetServiceManager.OnRejectFriendRequestListener() {
                                    @Override
                                    public void onRejectFriendRequest(boolean validate, int errorcode) {

                                        if (validate) {
                                            // "친구 추가"
                                            if (list_friend.size() > pos) {
                                                FriendFindItemViewModel friendFindItemViewModel = (FriendFindItemViewModel) list_friend.get(pos);
                                                friendFindItemViewModel.friend_state = 0;

                                                // 리사이클 데이터 변경에따른 ui 업데이트
                                                friendFindAdapter.notifyDataSetChanged();
                                            }
                                        } else {
                                            if (errorcode == 500) {

                                                // 해당 요청이 상대방이 취소해서 해당 메시지가 유효하지 않는 경우 : 500
                                                ErrorCodePopup alertDlg_error = new ErrorCodePopup(FriendFindActivity.this, FriendFindActivity.this);
                                                alertDlg_error.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                                alertDlg_error.show();

                                                alertDlg_error.textView.setText(FriendFindActivity.this.getResources().getString(R.string.dialog_error_code_500));

                                                alertDlg_error.iv_ok.setOnClickListener(v -> {

                                                    // "친구 추가"
                                                    if (list_friend.size() > pos) {
                                                        FriendFindItemViewModel friendFindItemViewModel = (FriendFindItemViewModel) list_friend.get(pos);
                                                        friendFindItemViewModel.friend_state = 0;

                                                        // 리사이클 데이터 변경에따른 ui 업데이트
                                                        friendFindAdapter.notifyDataSetChanged();
                                                    }

                                                    alertDlg_error.dismiss();
                                                });
                                            }
                                        }
                                    }
                                });

                                NetServiceManager.getinstance().rejectFriendRequest(NetServiceManager.getinstance().getUserProfile().uid, userProfile.uid);

                                alertDlg.dismiss();

                            });
                        }
                        else
                        {
                            if (errocode == -1 || errocode == 0)
                            {
                                // 해당 요청이 상대방이 취소해서 해당 메시지가 유효하지 않는 경우 : 500
                                ErrorCodePopup alertDlg_error = new ErrorCodePopup(FriendFindActivity.this, FriendFindActivity.this);
                                alertDlg_error.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                alertDlg_error.show();

                                alertDlg_error.textView.setText(FriendFindActivity.this.getResources().getString(R.string.dialog_error_code_500));

                                alertDlg_error.iv_ok.setOnClickListener(v -> {

                                    FriendFindItemViewModel friendFindItemViewModel = (FriendFindItemViewModel) list_friend.get(pos);
                                    friendFindItemViewModel.friend_state = 0;  // 친구추가로 변경

                                    // 리사이클 데이터 변경에따른 ui 업데이트
                                    friendFindAdapter.notifyDataSetChanged();

                                    alertDlg_error.dismiss();
                                });
                            }
                        }

                    }
                });


                NetServiceManager.getinstance().checkSendFriendRequest(NetServiceManager.getinstance().getUserProfile().uid,  userProfile.uid);
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
                onBackPressed();

            }
            break;
            case R.id.iv_find:
            {
                String strInputWord = viewModel.inputword.getValue();

                if(strInputWord != null && strInputWord.length() > 0) {

                    // 찾기
                    Find_Friend();

                    //Toast.makeText(this, "친구 검색 중", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // 친구가 없음 표시
                    binding.layoutNofind.setVisibility(View.VISIBLE);
                    binding.recyclerview.setVisibility(View.GONE);
                }

                hideKeyBoard();
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

        String text = viewModel.inputword.getValue();
        if(text != null && text.length() > 0) {
            NetServiceManager.getinstance().recvFindUserList(viewModel.inputword.getValue());
        }
        
    }

    @Override
    public void onFailure(Integer id, String message) {

    }

    @Override
    public void onBackPressed() {

        ActivityStack.instance().OnBack(this);
    }
}