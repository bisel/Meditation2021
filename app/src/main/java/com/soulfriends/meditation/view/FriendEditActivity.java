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
import com.soulfriends.meditation.databinding.FriendEditBinding;
import com.soulfriends.meditation.util.ItemClickListenerExt;
import com.soulfriends.meditation.util.ResultListener;
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        //rcv.setLayoutManager(layoutManager);

        list_friend = ItemList();
        friendEditAdapter = new FriendEditAdapter(list_friend, this, this);

        binding.recyclerview.setAdapter(friendEditAdapter);
        binding.recyclerview.setLayoutManager(layoutManager);
        binding.recyclerview.setNestedScrollingEnabled(false);  // 12.02.괜찮은듯
    }

    private List<FriendEditItemViewModel> ItemList() {
        List list = new ArrayList<>();

        for (int i = 0; i < 50; i++)
        {
            FriendEditItemViewModel friendEditItemViewModel = new FriendEditItemViewModel(this, String.valueOf(i) ,String.valueOf(i));

            list.add(friendEditItemViewModel);
        }

        return list;
    }

    @Override
    public void onSuccess(Integer id, String message) {

        switch (id) {
            case R.id.iv_close: {

            }
        }
    }

    @Override
    public void onFailure(Integer id, String message) {

    }

    @Override
    public void onItemClick(View view, Object obj, int pos) {

        switch(view.getId())
        {
            case R.id.iv_delete:
            {

                // 삭제 버튼 선택하면
                // 서버에 보내고 성공 이벤트 받고 리스트에서 삭제하도록 한다.
                // 일단 테스트로 삭제 기능
                int index_id = Integer.parseInt((String)obj);

                list_friend.remove(pos);
                friendEditAdapter.notifyItemRemoved(pos);
                friendEditAdapter.notifyItemRangeChanged(pos, list_friend.size());

                String msg = "id = ";
                msg += String.valueOf(index_id);
                msg += " , pos = ";
                msg += String.valueOf(pos);
                Toast.makeText(this, msg ,Toast.LENGTH_SHORT).show();

            }
            break;

        }
    }
}