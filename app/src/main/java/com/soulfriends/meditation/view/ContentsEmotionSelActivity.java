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
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.parser.EmotionListData;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.viewmodel.ContentsEmotionSelViewModel;
import com.soulfriends.meditation.viewmodel.ContentsEmotionSelViewModelFactory;
import com.soulfriends.meditation.viewmodel.FriendEditViewModel;
import com.soulfriends.meditation.viewmodel.FriendEditViewModelFactory;

import java.util.ArrayList;
import java.util.List;

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

    // value
    private String titleName = "";
    private String thumnailImgName = "";
    private int playtime = 0;
    private int IsSndFile = 0;
    private String SndFileName = "";
    private String releasedate = "";
    private String backgrroundImgName = "";


    private String str_genre_id = "select_genre_id";
    private String str_emotion_id = "select_emotion_id";

    private String activity_class;
    
    private int contents_mode = 0; // 0 이면 make, 1이면 수정

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

        // 정보 받기
        Intent intent = getIntent();

        // 액티비티
        activity_class = intent.getStringExtra("activity_class");
        titleName = intent.getStringExtra("titleName");
        thumnailImgName = intent.getStringExtra("thumnailImgName");
        playtime = intent.getIntExtra("playtime", 0);
        IsSndFile = intent.getIntExtra("IsSndFile", 0);
        SndFileName = intent.getStringExtra("SndFileName");
        releasedate = intent.getStringExtra("releasedate");
        backgrroundImgName = intent.getStringExtra("backgrroundImgName");

        str_genre_id = intent.getStringExtra("genre");
        str_emotion_id = intent.getStringExtra("emotion");

        if(str_genre_id == null || str_genre_id.length() == 0)
        {
            // 없는 경우
            str_genre_id = "";
            // 콘텐츠 새로 만든 경우에 해당됨

            viewModel.setContents_state(this.getResources().getString(R.string.contents_emotionsel_register));
        }
        else
        {
            // 있는 경우
            // 콘텐츠 수정된 경우 해당

            bCheck_kind = true;

            viewModel.setContents_state(this.getResources().getString(R.string.contents_emotionsel_edit));

            if(str_genre_id.equals("명상"))
            {
                First_SelectKind(1);
            }
            else if(str_genre_id.equals("수면"))
            {
                First_SelectKind(2);
            }
            else if(str_genre_id.equals("음악"))
            {
                First_SelectKind(3);
            }
        }

        if(str_emotion_id == null || str_emotion_id.length() == 0)
        {
            // 없는 경우

            str_emotion_id = "";
            // 콘텐츠 새로 만든 경우에 해당됨


        }
        else
        {
            // 있는 경우
            // 콘텐츠 수정된 경우 해당

            contents_mode = 1;

            bCheck_emoticon = true;

            CheckResult();


            ArrayList<EmotionListData> list_emotion = null;
            if(select_kind_id == 1) {
                list_emotion = NetServiceManager.getinstance().getEmotionListMeditationDataList();
            }
            else if(select_kind_id == 2) {
                list_emotion = NetServiceManager.getinstance().getEmotionListSleepDataList();
            }
            else if(select_kind_id == 3) {
                 list_emotion = NetServiceManager.getinstance().getEmotionListMusicDataList();
            }

            if(list_emotion != null) {
                int find_emotion = 0;
                for (int i = 0, len = list_emotion.size(); i < len; i++) {
                    if (list_emotion.get(i).softemotion.equals(str_emotion_id)) {
                        find_emotion = i;
                        break;
                    }
                }
                First_SelectEmoticon(find_emotion);
            }
        }
    }

    @Override
    public void onSuccess(Integer id, String message) {

        switch (id) {
            case R.id.ic_close: {

                // 닫기
                //this.startActivity(new Intent(this, MyContentsActivity.class));
                //this.overridePendingTransition(0, 0);

                UtilAPI.RemoveActivity_Temp(this);
                finish();

            }
            break;
            case R.id.iv_registerbg: {

                // 콘텐츠 등록 버튼
                if(bCheck_Result) {

                    // 선택된 인덱스

                    // 장르
                    String str_genre = "";
                    String str_emotion = "";

                    if(select_kind_id == 1)
                    {
                        str_genre = NetServiceManager.getinstance().genre1; //"명상";

                        ArrayList<EmotionListData> list_emotion = NetServiceManager.getinstance().getEmotionListMeditationDataList();
                        str_emotion = list_emotion.get(select_emoticon_id - 1).softemotion;
                    }
                    else if(select_kind_id == 2)
                    {
                        str_genre = NetServiceManager.getinstance().genre2; //"수면";

                        ArrayList<EmotionListData> list_emotion = NetServiceManager.getinstance().getEmotionListSleepDataList();
                        str_emotion = list_emotion.get(select_emoticon_id - 1).softemotion;
                    }
                    else if(select_kind_id == 3)
                    {
                        str_genre = NetServiceManager.getinstance().genre3; //"음악";

                        ArrayList<EmotionListData> list_emotion = NetServiceManager.getinstance().getEmotionListMusicDataList();
                        str_emotion = list_emotion.get(select_emoticon_id - 1).softemotion;
                    }

                    String final_genre = str_genre;
                    String finalStr_emotion = str_emotion;

                    // 등록 시 저작권 정보 안내 팝업 노출 후 확인 시 업로드 진행 함
                    CopyrightDialog alertDlg = new CopyrightDialog(this, this);

                    alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    alertDlg.show();


                    alertDlg.iv_ok.setOnClickListener(v -> {

                        // 업로드 이동
                        Intent intent = new Intent(this, ContentsUploadActivity.class);

                        // 전달
                        // 액티비티
                        intent.putExtra("activity_class", activity_class);
                        intent.putExtra("titleName", titleName);
                        intent.putExtra("thumnailImgName", thumnailImgName);
                        intent.putExtra("playtime", playtime);
                        intent.putExtra("IsSndFile", IsSndFile);
                        intent.putExtra("SndFileName", SndFileName);
                        intent.putExtra("releasedate", releasedate);
                        intent.putExtra("backgrroundImgName", backgrroundImgName);

                        intent.putExtra("contents_mode", contents_mode); // 0 이면 make, 1이면 수정

                        // 장르
                        String res_genre = null;
                        if(!str_genre_id.equals(final_genre))
                        {
                            res_genre = final_genre;
                        }

                        // 명상
                        String res_emotion = null;
                        if(!str_emotion_id.equals(finalStr_emotion))
                        {
                            //res_emotion = str_emotion;
                            res_emotion = Integer.toString(select_emoticon_id);  // 2021.02.01로 처리하자.  1~16 index
                        }


                        intent.putExtra("genre", res_genre); // 장르
                        intent.putExtra("emotion", res_emotion);

                        this.startActivity(intent);
                        this.overridePendingTransition(0, 0);
                        //finish();

                        UtilAPI.AddActivity_Temp(this);

                        alertDlg.dismiss();
                    });
                }

            }
            break;

            case R.id.iv_item1: {
                SelectKind(1);
            }
            break;
            case R.id.iv_item2: {
                SelectKind(2);
            }
            break;
            case R.id.iv_item3: {
                SelectKind(3);
            }
            break;
            case R.id.lay_1: {
                SelectEmoticon(1);
            }
            break;
            case R.id.lay_2: {
                SelectEmoticon(2);
            }
            break;
            case R.id.lay_3: {
                SelectEmoticon(3);
            }
            break;
            case R.id.lay_4: {
                SelectEmoticon(4);
            }
            break;
            case R.id.lay_5: {
                SelectEmoticon(5);
            }
            break;
            case R.id.lay_6: {
                SelectEmoticon(6);
            }
            break;
            case R.id.lay_7: {
                SelectEmoticon(7);
            }
            break;
            case R.id.lay_8: {
                SelectEmoticon(8);
            }
            break;
            case R.id.lay_9: {
                SelectEmoticon(9);
            }
            break;
            case R.id.lay_10: {
                SelectEmoticon(10);
            }
            break;
            case R.id.lay_11: {
                SelectEmoticon(11);
            }
            break;
            case R.id.lay_12: {
                SelectEmoticon(12);
            }
            break;
            case R.id.lay_13: {
                SelectEmoticon(13);
            }
            break;
            case R.id.lay_14: {
                SelectEmoticon(14);
            }
            break;
            case R.id.lay_15: {
                SelectEmoticon(15);
            }
            break;
            case R.id.lay_16: {
                SelectEmoticon(16);
            }
            break;
        }
    }

    @Override
    public void onFailure(Integer id, String message) {

    }

    private void SelectKind(int index) {

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

    private void SelectEmoticon(int index) {
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

    private void First_SelectKind(int index)
    {
        ArrayList list = new ArrayList();

        list.add(binding.ivItem1);
        list.add(binding.ivItem2);
        list.add(binding.ivItem3);

        View view = (View)list.get(index - 1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(ContextCompat.getDrawable(this, R.drawable.social_ctgr_btn_selected));
        } else {
            view.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.social_ctgr_btn_selected));
        }


        viewPrev_kind = view;
        select_kind_id = index;
    }

    private void First_SelectEmoticon(int index)
    {
        ArrayList list = new ArrayList();

        list.add(binding.lay1);
        list.add(binding.lay2);
        list.add(binding.lay3);
        list.add(binding.lay4);
        list.add(binding.lay5);
        list.add(binding.lay6);
        list.add(binding.lay7);
        list.add(binding.lay8);
        list.add(binding.lay9);
        list.add(binding.lay10);
        list.add(binding.lay11);
        list.add(binding.lay12);
        list.add(binding.lay13);
        list.add(binding.lay14);
        list.add(binding.lay15);
        list.add(binding.lay16);

        View view = (View)list.get(index);

        // 첫 이모티콘 선택시
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(ContextCompat.getDrawable(this, R.drawable.feeling_bg_selected));
        } else {
            view.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.feeling_bg_selected));
        }

        viewPrev = view;
        select_emoticon_id = index + 1;
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
        //this.startActivity(new Intent(this, MyContentsActivity.class));
        //this.overridePendingTransition(0, 0);

        UtilAPI.RemoveActivity_Temp(this);
        finish();
    }

}