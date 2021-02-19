package com.soulfriends.meditation.view;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.PsychologyCharacterDetailBinding;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.parser.PersonResultData;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.viewmodel.PsychologyCharacterDetailViewModel;
import com.soulfriends.meditation.viewmodel.PsychologyCharacterDetailViewModelFactory;

import java.util.ArrayList;

public class PsychologyCharacterDetailActivity extends BaseActivity implements ResultListener {

    private PsychologyCharacterDetailBinding binding;
    private PsychologyCharacterDetailViewModel viewModel;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private PsychologyCharacterDetailViewModelFactory psychologyCharacterDetailViewModelFactory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_psychology_character_detail);
        binding.setLifecycleOwner(this);

        if (psychologyCharacterDetailViewModelFactory == null) {
            psychologyCharacterDetailViewModelFactory = new PsychologyCharacterDetailViewModelFactory(this, this);
        }
        viewModel = new ViewModelProvider(this.getViewModelStore(), psychologyCharacterDetailViewModelFactory).get(PsychologyCharacterDetailViewModel.class);
        binding.setViewModel(viewModel);

        // network
        UtilAPI.SetNetConnection_Activity(this);

        // 이모티콘 타입에 따라 결과 보이도록 한다.
        UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();
        int chartype = userProfile.chartype;

        ArrayList<PersonResultData> list = NetServiceManager.getinstance().getPersonResultDataList();

        PersonResultData resultData = list.get(chartype);//NetServiceManager.getinstance().getResultData(emotiontype);

        if(userProfile.nickname != null) {

            String strQuest = userProfile.nickname + getResources().getString(R.string.psychology_character_nickname);

            int end_nick = userProfile.nickname.length();

            if (end_nick > 0) {
                Spannable wordtoSpan = new SpannableString(strQuest);
                wordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(179, 179, 227)), 0, end_nick, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                wordtoSpan.setSpan(new ForegroundColorSpan(Color.WHITE), end_nick + 1, strQuest.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                binding.tvNick.setText(wordtoSpan);
            }
        }

        viewModel.setStrTitle(resultData.title);
        viewModel.setStrResult(resultData.desc);

//        String desc = "한국 소설의 기원은 패관 문학이다. 패관 문학은 지금으로 치면 통속문학이었고 성리학이 뿌리 깊었던 조선에선 그냥 공부나 가정일할 시간에 쓸데없이 시간을 낭비하는데 쓰게 만든다해서 좋은 대우를 못 받았다. 종종 허무맹랑한 소설들이 해악을 끼친다고 하면서 사회적 문제거리로까지 여겼고, 실제 소설 내용도 진지하게 다루는 작품보다는 흥미본위 내용을 다룬 소설이 많았다. 지금으로 따지면 별 의미없이 재미로 읽는 인터넷 소설, 무협 소설, 라이트 노벨, 판타지 소설 같은 개념이었다. 조선후기에 저술된 소설 가운데 작자가 미상인 경우가 많은 것도 이러한 인식 때문인 것으로 추정된다.\n" +
//                "\n" +
//                "하지만 당대에는 문맹률이 어마어마하게 높았고, 특히 한문의 경우 실질적으로 사대부 계층이나 제대로 사용할 수 있었다. 소설은 문자와 뗄레야 뗄 수 없는 관계이고, 한문 소설부터 시작되었기 때문에 근간이 사대부 계층인 것은 당연한 일이다. 식자층이어야 향유할 수 있기에, 받는 취급에 비해 고등한 축에 속하는 유흥거리이기도 하였다. 이후 '언패(언문 소설)'가 등장하면서 커트라인이 낮아지긴 하였으나, 언문도 배우지 못하는 평민들도 많았고, 책값이 일반인들에게 매우 부담이 가는 수준이었기 때문에 소설책도 아무나 사서 읽지 못해서 시장판이나 길거리에서 전기수들이 낭독하는 소설을 듣기도 했다. 애당초 사대부 계층에서 시작된 것이니 사대부들도 당연히 봤었고, 사대부 여인들이 시간을 때우기 위해서 소설책을 빌리는 일도 흔했다. 그리고 소설책을 대놓고 천시한 경우도 있지만 그럼에도 소설을 긍정적으로 본 고위층들도 적지는 않아서 영조는 소설책을 대놓고 즐겨보았고, 일부 사대부들도 손수 소설책을 창작하기도 했으며, 개중에서 용돈벌이용으로 소설을 창작한 경우도 종종 보이기도 한다. 대표적인 예로 김시습과 김만중, 박지원이 있으며 세도정치의 문을 연 것으로 유명한 김조순도 소설책을 즐겨봐서 정조에게 혼이 났던 일화도 있고, 소설책을 손수 창작하기도 했다.";
//        viewModel.setStrResult(desc);

        int res_id_1 = this.getResources().getIdentifier(resultData.img, "drawable", this.getPackageName());
        UtilAPI.setImageResource(binding.imageTile, res_id_1);
    }

    @Override
    public void onSuccess(Integer id, String message) {
        switch (id) {
            case R.id.ic_close: {


                // 닫기 버튼

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                this.overridePendingTransition(0, 0);
                finish();
            }
            break;
        }
    }

    @Override
    public void onFailure(Integer id, String message) {

    }

    @Override // 2020.12.20 , Close 막기
    public void onBackPressed() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
        finish();
    }
}