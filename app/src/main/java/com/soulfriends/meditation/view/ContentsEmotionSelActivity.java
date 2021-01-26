package com.soulfriends.meditation.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.ContentsEmotionSelBinding;
import com.soulfriends.meditation.databinding.FriendEditBinding;
import com.soulfriends.meditation.dlg.AlertLineTwoPopup;
import com.soulfriends.meditation.dlg.CopyrightDialog;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.viewmodel.ContentsEmotionSelViewModel;
import com.soulfriends.meditation.viewmodel.ContentsEmotionSelViewModelFactory;
import com.soulfriends.meditation.viewmodel.FriendEditViewModel;
import com.soulfriends.meditation.viewmodel.FriendEditViewModelFactory;

public class ContentsEmotionSelActivity extends BaseActivity implements ResultListener {

    private ContentsEmotionSelBinding binding;
    private ContentsEmotionSelViewModel viewModel;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private ContentsEmotionSelViewModelFactory contentsEmotionSelViewModelFactory;

    private int select_emoticon_id = -1;
    private View viewPrev = null;
    private boolean bCheck_emoticon = false;

    private int select_kind_id = -1;
    private View viewPrev_kind = null;
    private boolean bCheck_kind = false;

    private boolean bCheck_Result = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_contents_emotion_sel);
        binding.setLifecycleOwner(this);

        if (contentsEmotionSelViewModelFactory == null) {
            contentsEmotionSelViewModelFactory = new ContentsEmotionSelViewModelFactory(this, this);
        }
        viewModel = new ViewModelProvider(this.getViewModelStore(), contentsEmotionSelViewModelFactory).get(ContentsEmotionSelViewModel.class);
        binding.setViewModel(viewModel);
    }

    @Override
    public void onSuccess(Integer id, String message) {

        switch (id) {
            case R.id.ic_close: {

                // 닫기
                this.startActivity(new Intent(this, MyContentsActivity.class));
                this.overridePendingTransition(0, 0);
                finish();

            }
            break;
            case R.id.iv_registerbg: {

                // 콘텐츠 등록 버튼
                if(bCheck_Result) {

                    // 선택된 인덱스
                    // select_emoticon_id
                    // select_kind_id
                    
                    // 등록 시 저작권 정보 안내 팝업 노출 후 확인 시 업로드 진행 함
                    CopyrightDialog alertDlg = new CopyrightDialog(this, this);

                    alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    alertDlg.show();
                }

            }
            break;

            case R.id.iv_item1: {
                SelectKind(id,1);
            }
            break;
            case R.id.iv_item2: {
                SelectKind(id,1);
            }
            break;
            case R.id.iv_item3: {
                SelectKind(id,1);
            }
            break;
            case R.id.lay_1: {
                SelectEmoticon(id,1);
            }
            break;
            case R.id.lay_2: {
                SelectEmoticon(id,2);
            }
            break;
            case R.id.lay_3: {
                SelectEmoticon(id,3);
            }
            break;
            case R.id.lay_4: {
                SelectEmoticon(id,4);
            }
            break;
            case R.id.lay_5: {
                SelectEmoticon(id,5);
            }
            break;
            case R.id.lay_6: {
                SelectEmoticon(id,6);
            }
            break;
            case R.id.lay_7: {
                SelectEmoticon(id,7);
            }
            break;
            case R.id.lay_8: {
                SelectEmoticon(id,8);
            }
            break;
            case R.id.lay_9: {
                SelectEmoticon(id,9);
            }
            break;
            case R.id.lay_10: {
                SelectEmoticon(id,10);
            }
            break;
            case R.id.lay_11: {
                SelectEmoticon(id,11);
            }
            break;
            case R.id.lay_12: {
                SelectEmoticon(id,12);
            }
            break;
            case R.id.lay_13: {
                SelectEmoticon(id,13);
            }
            break;
            case R.id.lay_14: {
                SelectEmoticon(id,14);
            }
            break;
            case R.id.lay_15: {
                SelectEmoticon(id,15);
            }
            break;
            case R.id.lay_16: {
                SelectEmoticon(id,16);
            }
            break;
        }
    }

    @Override
    public void onFailure(Integer id, String message) {

    }

    private void SelectKind(int id, int index) {

        // 이전 버튼 비선택 처리
        if (select_kind_id != -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                viewPrev_kind.setBackground(ContextCompat.getDrawable(this, R.drawable.social_ctgr_btn));
            } else {
                viewPrev_kind.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.social_ctgr_btn));
            }
        }

        // 첫 이모티콘 선택시
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            viewModel.getView().setBackground(ContextCompat.getDrawable(this, R.drawable.social_ctgr_btn_selected));
        } else {
            viewModel.getView().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.social_ctgr_btn_selected));
        }

        viewPrev_kind = viewModel.getView();
        select_kind_id = index;

        bCheck_kind = true;

        CheckResult();

    }

    private void SelectEmoticon(int id, int index) {
        // 이전 버튼 비선택 처리
        if (select_emoticon_id != -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                viewPrev.setBackground(ContextCompat.getDrawable(this, R.drawable.feeling_bg));
            } else {
                viewPrev.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.feeling_bg));
            }
        }

        // 첫 이모티콘 선택시
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            viewModel.getView().setBackground(ContextCompat.getDrawable(this, R.drawable.feeling_bg_selected));
        } else {
            viewModel.getView().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.feeling_bg_selected));
        }

        viewPrev = viewModel.getView();
        select_emoticon_id = index;


        bCheck_emoticon = true;

        CheckResult();
    }

    private void CheckResult()
    {
        if(bCheck_kind && bCheck_emoticon)
        {
            //-------------------------------------
            // 결과 버튼 활성화
            //-------------------------------------
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                binding.ivRegisterbg.setBackground(ContextCompat.getDrawable(this, R.drawable.social_content_upload_able));
            } else {
                binding.ivRegisterbg.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.social_content_upload_able));
            }

            bCheck_Result = true;
        }
    }


    @Override
    public void onBackPressed() {

        // 닫기
        this.startActivity(new Intent(this, MyContentsActivity.class));
        this.overridePendingTransition(0, 0);
        finish();
    }

}