package com.soulfriends.meditation.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.NotiBinding;
import com.soulfriends.meditation.model.MeditationDetailAlarm;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.util.ItemClickListenerExt;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.view.friend.FriendEditAdapter;
import com.soulfriends.meditation.view.friend.FriendFindItemViewModel;
import com.soulfriends.meditation.view.noti.NotiAdapter;
import com.soulfriends.meditation.view.noti.NotiItemViewModel;
import com.soulfriends.meditation.viewmodel.NotiViewModel;
import com.soulfriends.meditation.viewmodel.NotiViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class NotiActivity extends BaseActivity implements ResultListener, ItemClickListenerExt {

    private NotiBinding binding;
    private NotiViewModel viewModel;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private NotiViewModelFactory notiViewModelFactory;


    private List list_noti = new ArrayList<>();

    private NotiAdapter notiAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_noti);
        binding.setLifecycleOwner(this);

        if (notiViewModelFactory == null) {
            notiViewModelFactory = new NotiViewModelFactory(this, this);
        }
        viewModel = new ViewModelProvider(this.getViewModelStore(), notiViewModelFactory).get(NotiViewModel.class);
        binding.setViewModel(viewModel);

        int noti_total = 10;
        if(noti_total == 0)
        {
            // 알림 없는 경우 해당
            binding.recyclerview.setVisibility(View.GONE);
            binding.layoutAlert.setVisibility(View.VISIBLE);
        }
        else {
            NetServiceManager.getinstance().setOnOpenAlarmListListener(new NetServiceManager.OnOpenAlarmListListener() {
                @Override
                public void onOpenAlarmList(boolean validate) {
                        // TRUE가 오면... 처리
                }
            });
            NetServiceManager.getinstance().openAlarmList();


            // 알림 있는 경우 해당

            binding.recyclerview.setVisibility(View.VISIBLE);
            binding.layoutAlert.setVisibility(View.GONE);
            
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            //rcv.setLayoutManager(layoutManager);

            list_noti = ItemList();
            notiAdapter = new NotiAdapter(list_noti, this, this);

            binding.recyclerview.setAdapter(notiAdapter);
            binding.recyclerview.setLayoutManager(layoutManager);
            binding.recyclerview.setNestedScrollingEnabled(false);
        }
    }

//-------------------------------------------------
//    알림 -> main_type  [0]
//
//    알림 항목 : -> sub_type
//    0 Type : 닉네임님이 친구 신청을 수락했습니다.
//    1 Type : 닉네임님이 친구 신청을 거절했습니다.
//    2 Type : 닉네임님이 감정 공유를 수락했습니다.
//    3 Type : 닉네임님이 감정 공유를 거절했습니다.

//-------------------------------------------------
//    수락/거절 리스트   [1]
//
//    0 Type : 닉네임 님이 감정공유를 요청했습니다.
//    1 Type : 닉네임 님이 친구 신청을 했습니다.


    private List<NotiItemViewModel> ItemList() {

        List list = new ArrayList<>();
        ArrayList<MeditationDetailAlarm> list_alarm = NetServiceManager.getinstance().mDetailAlarmDataList;

        for (int i = 0; i < list_alarm.size(); i++)
        {
            MeditationDetailAlarm alarm = list_alarm.get(i);

            NotiItemViewModel notiViewModel = new NotiItemViewModel(this,this, String.valueOf(i), alarm);

            list.add(notiViewModel);

        }

//        List list = new ArrayList<>();
//
//        for (int i = 0; i < 30; i++)
//        {
//            if(i % 6 == 0)
//            {
//                NotiItemViewModel notiViewModel = new NotiItemViewModel(this,this, String.valueOf(i), String.valueOf(i), 0, 0);
//
//                list.add(notiViewModel);
//            }
//            else if(i % 6 == 1){
//                NotiItemViewModel notiViewModel = new NotiItemViewModel(this, this, String.valueOf(i), String.valueOf(i), 0, 1);
//
//                list.add(notiViewModel);
//            }
//            else if(i % 6 == 2){
//                NotiItemViewModel notiViewModel = new NotiItemViewModel(this,this, String.valueOf(i), String.valueOf(i), 0, 2);
//
//                list.add(notiViewModel);
//            }
//            else if(i % 6== 3){
//                NotiItemViewModel notiViewModel = new NotiItemViewModel(this,this, String.valueOf(i), String.valueOf(i), 0, 3);
//
//                list.add(notiViewModel);
//            }
//            else if(i % 6 == 4){
//                NotiItemViewModel notiViewModel = new NotiItemViewModel(this,this, String.valueOf(i), String.valueOf(i), 1, 0);
//
//                list.add(notiViewModel);
//            }
//            else {
//                NotiItemViewModel notiViewModel = new NotiItemViewModel(this,this, String.valueOf(i), String.valueOf(i), 1, 1);
//
//                list.add(notiViewModel);
//            }
//        }

        return list;
    }

    @Override
    public void onSuccess(Integer id, String message) {

        switch (id) {
            case R.id.ic_close: {
                // 닫기
                this.overridePendingTransition(0, 0);
                finish();
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
            case R.id.iv_reject: {
                //  거절

                MeditationDetailAlarm hAlarm = (MeditationDetailAlarm)obj;

                if(hAlarm.entity.alarmtype == 2)
                {
                    if(hAlarm.entity.alarmsubtype == 1)
                    {
                        //  1 . 친구 신청 거절 (상대방이 나에게 신청)
                        NetServiceManager.getinstance().setOnRejectFriendRequestListener(new NetServiceManager.OnRejectFriendRequestListener() {
                            @Override
                            public void onRejectFriendRequest(boolean validate) {

                                if(validate)
                                {
                                    // 리스트에서 삭제
                                    list_noti.remove(pos);
                                    notiAdapter.notifyItemRemoved(pos);
                                    notiAdapter.notifyItemRangeChanged(pos, list_noti.size());

                                    //Toast.makeText(this, "수락" ,Toast.LENGTH_SHORT).show();
                                }
                                else
                                {

                                }
                            }
                        });

                        NetServiceManager.getinstance().rejectFriendRequest(NetServiceManager.getinstance().getUserProfile().uid,  hAlarm.otheruser.uid);

                    }
                    else
                    {
                        //  2.  감정 공유 거절 (상대방이 나에게 신청)
                        NetServiceManager.getinstance().setOnRejectFriendRequestListener(new NetServiceManager.OnRejectFriendRequestListener() {
                            @Override
                            public void onRejectFriendRequest(boolean validate) {

                                if(validate)
                                {
                                    // 리스트에서 삭제
                                    list_noti.remove(pos);
                                    notiAdapter.notifyItemRemoved(pos);
                                    notiAdapter.notifyItemRangeChanged(pos, list_noti.size());

                                    //Toast.makeText(this, "수락" ,Toast.LENGTH_SHORT).show();
                                }
                                else
                                {

                                }
                            }
                        });

                        NetServiceManager.getinstance().rejectEmotionFriendRequest(NetServiceManager.getinstance().getUserProfile().uid,  hAlarm.otheruser.uid);

                    }
                }

                list_noti.remove(pos);
                notiAdapter.notifyItemRemoved(pos);
                notiAdapter.notifyItemRangeChanged(pos, list_noti.size());

                Toast.makeText(this, "거절" ,Toast.LENGTH_SHORT).show();
            }
            break;
            case R.id.iv_ok: {

                //  수락

                MeditationDetailAlarm hAlarm = (MeditationDetailAlarm)obj;

                if(hAlarm.entity.alarmtype == 2)
                {
                    if(hAlarm.entity.alarmsubtype == 1)
                    {
                        //  1 . 친구 신청 신청 (상대방이 나에게 신청)

                        NetServiceManager.getinstance().setOnAcceptFriendRequestListener(new NetServiceManager.OnAcceptFriendRequestListener() {
                            @Override
                            public void onAcceptFriendRequest(boolean validate) {

                                if(validate)
                                {
                                    // 리스트에서 삭제
                                    list_noti.remove(pos);
                                    notiAdapter.notifyItemRemoved(pos);
                                    notiAdapter.notifyItemRangeChanged(pos, list_noti.size());

                                    //Toast.makeText(this, "수락" ,Toast.LENGTH_SHORT).show();
                                }
                                else
                                {

                                }
                            }
                        });

                        NetServiceManager.getinstance().AcceptFriend(NetServiceManager.getinstance().getUserProfile().uid,  hAlarm.otheruser.uid);
                    }
                    else
                    {
                        //  2.  감정 공유 신청 (상대방이 나에게 신청)

                        NetServiceManager.getinstance().setOnAcceptFriendRequestListener(new NetServiceManager.OnAcceptFriendRequestListener() {
                            @Override
                            public void onAcceptFriendRequest(boolean validate) {

                                if(validate)
                                {
                                    // 리스트에서 삭제
                                    list_noti.remove(pos);
                                    notiAdapter.notifyItemRemoved(pos);
                                    notiAdapter.notifyItemRangeChanged(pos, list_noti.size());

                                    //Toast.makeText(this, "수락" ,Toast.LENGTH_SHORT).show();
                                }
                                else
                                {

                                }
                            }
                        });

                        NetServiceManager.getinstance().AcceptEmotionFriend(NetServiceManager.getinstance().getUserProfile().uid,  hAlarm.otheruser.uid);

                    }
                }


            }
            break;
        }
    }

    @Override
    public void onBackPressed() {

        // 닫기
        this.overridePendingTransition(0, 0);
        finish();
    }
}