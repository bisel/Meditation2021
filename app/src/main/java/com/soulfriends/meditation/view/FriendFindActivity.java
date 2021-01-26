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

                    hideKeyBoard();
                    binding.editText.clearFocus();

                    return true;
                }
                return false;
            }
        });
    }

    private List<FriendFindItemViewModel> ItemList() {
        //List list = new ArrayList<>();

        for (int i = 0; i < 50; i++)
        {
            if(i % 4 == 0) {
                FriendFindItemViewModel friendFindItemViewModel = new FriendFindItemViewModel(this, String.valueOf(i), 0);

                list_friend.add(friendFindItemViewModel);
            }
            else if(i % 3 == 1) {
                FriendFindItemViewModel friendFindItemViewModel = new FriendFindItemViewModel(this, String.valueOf(i),1);

                list_friend.add(friendFindItemViewModel);
            }
            else
            {
                FriendFindItemViewModel friendFindItemViewModel = new FriendFindItemViewModel(this, String.valueOf(i), 2);

                list_friend.add(friendFindItemViewModel);
            }
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

                    int index_id =  Integer.parseInt((String)obj);

                    FriendFindItemViewModel friendFindItemViewModel = (FriendFindItemViewModel)list_friend.get(index_id);
                    friendFindItemViewModel.friend_state = 2;  // 감정공유 요청

                    // 리사이클 데이터 변경에따른 ui 업데이트
                    friendFindAdapter.notifyDataSetChanged();

                    Toast.makeText(this,"친구 요청중",Toast.LENGTH_SHORT).show();

                    alertDlg.dismiss();
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

                    int index_id =  Integer.parseInt((String)obj);

                    FriendFindItemViewModel friendFindItemViewModel = (FriendFindItemViewModel)list_friend.get(index_id);
                    friendFindItemViewModel.friend_state = 2;  // 삭제가 되어야 함.

                    // 리사이클 데이터 변경에따른 ui 업데이트
                    friendFindAdapter.notifyDataSetChanged();

                    Toast.makeText(this,"삭제처리",Toast.LENGTH_SHORT).show();

                    alertDlg.dismiss();
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

                    int index_id =  Integer.parseInt((String)obj);

                    FriendFindItemViewModel friendFindItemViewModel = (FriendFindItemViewModel)list_friend.get(index_id);
                    friendFindItemViewModel.friend_state = 0;  // 감정공유 요청 중

                    // 리사이클 데이터 변경에따른 ui 업데이트
                    friendFindAdapter.notifyDataSetChanged();

                    Toast.makeText(this,"친구 추가+",Toast.LENGTH_SHORT).show();

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

                // 찾기
                Find_Friend();

                Toast.makeText(this,"친구 검색 중",Toast.LENGTH_SHORT).show();

            }
            break;

        }
    }

    private void Find_Friend()
    {

        // 서버에 요청 메시지를 보낸다.
        
        // 응답이 온 경우 
        
        // 친구가 없다면
        //layout_nofind

        int total_friend = 0;
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
            ItemList();
        }
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