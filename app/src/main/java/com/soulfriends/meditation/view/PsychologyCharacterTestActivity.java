package com.soulfriends.meditation.view;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.PsychologyCharacterTestBinding;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.parser.PersonQuestionData;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.viewmodel.PsychologyCharacterTestViewModel;
import com.soulfriends.meditation.viewmodel.PsychologyCharacterTestViewModelFactory;

import java.util.ArrayList;

public class PsychologyCharacterTestActivity extends BaseActivity implements ResultListener {

    private PsychologyCharacterTestBinding binding;
    private PsychologyCharacterTestViewModel viewModel;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private PsychologyCharacterTestViewModelFactory psychologyCharacterTestViewModelFactory;

    ArrayList<PersonQuestionData> questionData_list;
    ArrayList<Integer> list_page_selectid;

    private int curPageIndex = 0;

    private boolean bCheckResult = false;
    private int select_item_id = -1;
    private Button viewPrev = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_psychology_character_test);
        binding.setLifecycleOwner(this);

        if (psychologyCharacterTestViewModelFactory == null) {
            psychologyCharacterTestViewModelFactory = new PsychologyCharacterTestViewModelFactory(this, this);
        }
        viewModel = new ViewModelProvider(this.getViewModelStore(), psychologyCharacterTestViewModelFactory).get(PsychologyCharacterTestViewModel.class);
        binding.setViewModel(viewModel);

        // network
        UtilAPI.SetNetConnection_Activity(this);

        list_page_selectid = new ArrayList<Integer>();
        questionData_list = NetServiceManager.getinstance().getPersonQuestionDataList();
        curPageIndex = 0;

        for(int i = 0; i < questionData_list.size(); i++) {
            list_page_selectid.add(-1);
        }

        MakePage(0);

    }

    @Override
    public void onSuccess(Integer id, String message) {

        switch (id) {
            case R.id.ic_close: {
                // 닫기 버튼

                if(prev_page())
                {

                }
                else
                {
                    Intent intent = new Intent(this, PsychologyCharacterListActivity.class);
                    startActivity(intent);

                    finish();
                }

            }
            break;
            case R.id.bt_result: {
                // 다음 버튼
                if (bCheckResult) {

                    if(questionData_list.size() == curPageIndex)
                        return;

                    // 현재 저장
                    list_page_selectid.set(curPageIndex, select_item_id);

                    // 다음
                    if(next_page())
                    {

                    }
                    else
                    {
                        NetServiceManager.getinstance().checkCharTest(list_page_selectid);
                        finish();
                        Intent intent = new Intent(this, PsychologyCharacterResultActivity.class);
                        startActivity(intent);
                    }
                }
            }
            break;
            case R.id.bt_answer1: {
                // 1번 선택
                SeleteItem(id, 1);
            }
            break;
            case R.id.bt_answer2: {
                // 2번 선택
                SeleteItem(id, 2);
            }
            break;
            case R.id.bt_answer3: {
                // 3번 선택
                SeleteItem(id, 3);
            }
            break;
        }
    }

    @Override
    public void onFailure(Integer id, String message) {

    }

    private void setDot(int page_index)
    {
        // de select
        UtilAPI.setImage(this, binding.ivCicle1, R.drawable.test_chrctr_navigation);
        UtilAPI.setImage(this, binding.ivCicle2, R.drawable.test_chrctr_navigation);
        UtilAPI.setImage(this, binding.ivCicle3, R.drawable.test_chrctr_navigation);
        UtilAPI.setImage(this, binding.ivCicle4, R.drawable.test_chrctr_navigation);

        UtilAPI.setImage(this, binding.ivCicle5, R.drawable.test_chrctr_navigation);
        UtilAPI.setImage(this, binding.ivCicle6, R.drawable.test_chrctr_navigation);
        UtilAPI.setImage(this, binding.ivCicle7, R.drawable.test_chrctr_navigation);
        UtilAPI.setImage(this, binding.ivCicle8, R.drawable.test_chrctr_navigation);

        switch(page_index)
        {
            case 0:
            {
                UtilAPI.setImage(this, binding.ivCicle1, R.drawable.test_chrctr_selected);
            }
            break;
            case 1:
            {
                UtilAPI.setImage(this, binding.ivCicle2, R.drawable.test_chrctr_selected);
            }
            break;
            case 2:
            {
                UtilAPI.setImage(this, binding.ivCicle3, R.drawable.test_chrctr_selected);
            }
            break;
            case 3:
            {
                UtilAPI.setImage(this, binding.ivCicle4, R.drawable.test_chrctr_selected);
            }
            break;
            case 4:
            {
                UtilAPI.setImage(this, binding.ivCicle5, R.drawable.test_chrctr_selected);
            }
            break;
            case 5:
            {
                UtilAPI.setImage(this, binding.ivCicle6, R.drawable.test_chrctr_selected);
            }
            break;
            case 6:
            {
                UtilAPI.setImage(this, binding.ivCicle7, R.drawable.test_chrctr_selected);
            }
            break;
            case 7:
            {
                UtilAPI.setImage(this, binding.ivCicle8, R.drawable.test_chrctr_selected);
            }
            break;

        }
    }

    private void MakePage(int page_index)
    {
        PersonQuestionData data = questionData_list.get(page_index);

        setDot(page_index);

        viewModel.setTitle_data(data.question);

        // 다음  / 결과 보기

        if(page_index < questionData_list.size() - 1) {

            String strButtonText = getResources().getString(R.string.psychology_character_next);
            viewModel.setStrButtontext(strButtonText);
        }
        else {
            String strButtonText = getResources().getString(R.string.psychology_character_resultview);
            viewModel.setStrButtontext(strButtonText);
        }

        viewModel.setText_1_data(data.answer1_text);
        viewModel.setText_2_data(data.answer2_text);
        viewModel.setText_3_data(data.answer3_text);

        int res_id_1 = this.getResources().getIdentifier(data.questionfile, "drawable", this.getPackageName());
        setImage(binding.ivImage, res_id_1);


        // 이전 버튼 비선택 처리
        if (select_item_id != -1) {

            UtilAPI.setButtonBackground(this, viewPrev, R.drawable.test_chrctr_answerbg);
        }

        // 이미 선택된 것이 있다면
        int _select_item_id = list_page_selectid.get(page_index);
        if(_select_item_id > 0)
        {
            // 기존에 선택된 아이템이 있다.
            Button view_layout = null;

            switch (_select_item_id) {
                case 1: {
                    view_layout = binding.btAnswer1;
                }
                break;
                case 2: {
                    view_layout = binding.btAnswer2;
                }
                break;
                case 3: {
                    view_layout = binding.btAnswer3;
                }
                break;
            }

            if(view_layout != null)
            {
                viewPrev = view_layout;

                // 선택 표시
                UtilAPI.setButtonBackground(this, view_layout, R.drawable.test_chrctr_answerbg_selected);

                // 결과 버튼 활성화
                UtilAPI.setButtonBackground(this, binding.btResult, R.drawable.btn_colcr_able);

                bCheckResult = true;
            }
        }
        else
        {
            if (select_item_id != -1) {

                // 결과 버튼 비활성화
                UtilAPI.setButtonBackground(this, binding.btResult, R.drawable.btn_colcr_disable);

                bCheckResult = false;
            }
        }
    }

    private void setImage(ImageView view, int res_id)
    {
        view.setImageResource(res_id);
    }

    private boolean prev_page()
    {
        curPageIndex--;
        if(curPageIndex < 0)
        {
            // 화면 전환

            return false;
        }

        MakePage(curPageIndex);
        select_item_id = list_page_selectid.get(curPageIndex);
        return true;
    }

    private boolean next_page()
    {
        curPageIndex++;
        if(curPageIndex > questionData_list.size() - 1)
        {
            // 결과

            return false;
        }

        MakePage(curPageIndex);
        select_item_id = list_page_selectid.get(curPageIndex);

        return true;
        //SeleteItem(int id, int index)
    }


    private void SeleteItem(int id, int index)
    {
        // 이전 버튼 비선택 처리
        if (select_item_id != -1) {

            UtilAPI.setButtonBackground(this, viewPrev, R.drawable.test_chrctr_answerbg);
        }

        // 첫 이모티콘 선택시

        viewPrev = viewModel.getView();

        UtilAPI.setButtonBackground(this, viewPrev, R.drawable.test_chrctr_answerbg_selected);


        select_item_id = index;

        // 결과 버튼 활성화

        UtilAPI.setButtonBackground(this, binding.btResult, R.drawable.btn_colcr_able);

        bCheckResult = true;

    }

    @Override // 2020.12.20 , Close 버튼과 동일
    public void onBackPressed() {
        if(prev_page()){

        }
        else{
            Intent intent = new Intent(this, PsychologyCharacterListActivity.class);
            startActivity(intent);
            finish();
        }
    }
}