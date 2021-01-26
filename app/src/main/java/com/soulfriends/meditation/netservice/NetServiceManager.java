package com.soulfriends.meditation.netservice;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soulfriends.meditation.R;
import com.soulfriends.meditation.model.MediationShowContents;
import com.soulfriends.meditation.model.MeditationCategory;
import com.soulfriends.meditation.model.MeditationContents;
import com.soulfriends.meditation.model.MeditationContentsCharInfo;
import com.soulfriends.meditation.model.MeditationShowCategorys;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.parser.CategoryData;
import com.soulfriends.meditation.parser.CharData;
import com.soulfriends.meditation.parser.CharListData;
import com.soulfriends.meditation.parser.ColorData;
import com.soulfriends.meditation.parser.DetermineCharData;
import com.soulfriends.meditation.parser.EmotionListData;
import com.soulfriends.meditation.parser.PersonQuestionData;
import com.soulfriends.meditation.parser.PersonResultData;
import com.soulfriends.meditation.parser.QuestionData;
import com.soulfriends.meditation.parser.ResultData;
import com.soulfriends.meditation.parser.VoiceAnalysisData;
import com.soulfriends.meditation.parser.VoiceData;
import com.soulfriends.meditation.util.PreferenceManager;
import com.soulfriends.meditation.util.UtilAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.soulfriends.meditation.netservice.NetServiceUtility.mycontentsaudiodir;
import static com.soulfriends.meditation.netservice.NetServiceUtility.mycontentsthumnaildir;
import static com.soulfriends.meditation.netservice.NetServiceUtility.profieimgdir;
import static java.util.Arrays.asList;

public class NetServiceManager {
    private NetServiceManager() {
    }

    private static class instanceclass {
        private static final NetServiceManager instance = new NetServiceManager();
    }

    public static NetServiceManager getinstance(){
        return instanceclass.instance;
    }

    // 2020.12.30
    public boolean isAppOnForeground() {
        Context context = mCurApplicationContext;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
    // member variables
    private final String genre0 = "홈";
    private final String genre1 = "명상";
    private final String genre2 = "수면";
    private final String genre3 = "음악";
    private final String favoriteCategoryId = "50";
    private final String top10CategoryId = "66";
    private final String recentPlayCategoryId = "68";
    private final int maxFavoriteContentsNum = 30;
    private final int maxRecentPlayListNum = 30;
    private final int maxTop10ContentsNum = 10;
    private boolean bSaveRecording = false;
    private String url ="http://211.193.41.115:8880/ASPMultiAnalysis";
    private String saveFileName = "/record11.wav";
    private boolean isRecording = false;
    private AudioRecord mAudioRecord = null;
    private AudioTrack mAudioTrack = null;
    private ByteArrayList fArrayList = null;
    private FileOutputStream fos = null;
    private String contentsInfoString = "meditationext_mind"; //
    private String socialContentsInfoString = "social_meditationext_mind"; //
    private String contentsCharInfoString = "meditationchartag_mind";//

    public ArrayList<MeditationContents> mContentsList = new ArrayList<MeditationContents>();
    public ArrayList<MeditationContents> mSocialContentsList = new ArrayList<MeditationContents>();
    public ArrayList<MeditationContentsCharInfo> mContentsCharinfoList = new ArrayList<MeditationContentsCharInfo>();

    private DatabaseReference mfbDBRef;
    private UserProfile mUserProfile = new UserProfile();

    private boolean testFileDBMode = false;
    private Resources mAppRes = null;
    private Context mCurApplicationContext = null;

    private final int mNewContentsDelayDay = 1;
    public int GetNewContentsDelayDay(){
        return mNewContentsDelayDay;
    }

    // 현재 선택된 MeditationContents
    private MeditationContents cur_contents;
    public MeditationContents getCur_contents() {
        return cur_contents;
    }

    public void setCur_contents(MeditationContents cur_contents) {
        this.cur_contents = cur_contents;
    }

    // 현재 타이머 중인 MeditationContents
    private MeditationContents cur_contents_timer;
    public MeditationContents getCur_contents_timer() {
        return cur_contents_timer;
    }

    public void setCur_contents_timer(MeditationContents cur_contents) {
        this.cur_contents_timer = cur_contents;
    }

    // 2020.12.05 start
    MeditationCategory   mEmotionMeditationCategory = null;
    MeditationCategory   mEmotionSleepCategory = null;
    MeditationCategory   mEmotionMusicCategory = null;
    // 2020.12.05 end

    // 2020.12.22
    MeditationContents   mChartypeMeditationContent = null;
    MeditationContents   mChartypeSleepContent = null;
    MeditationContents   mChartypeMusicContent = null;
    MeditationCategory   mChartypeHomeCategory =  new MeditationCategory();;
    MeditationCategory   mChartypeMeditationCategory = new MeditationCategory();;
    MeditationCategory   mChartypeSleepategory =  new MeditationCategory();;
    MeditationCategory   mChartypeMusicCategory =  new MeditationCategory();;


    // 심리 검사 데이터
    private ArrayList<ResultData> resultData_list;
    public ResultData getResultData(int id) {
        return resultData_list.get(id);
    }

    // Color DataList
    private ArrayList<ColorData> mColorDataList;
    public ArrayList<ColorData> getColorDataList() {return mColorDataList;}

    // Question DataList
    private ArrayList<QuestionData> mQuestionDataList;
    public ArrayList<QuestionData> getQuestionDataList() {return mQuestionDataList;}

    // Category DataList
    private ArrayList<CategoryData> mCategoryDataList;
    public ArrayList<CategoryData> getCategoryDataList() {return mCategoryDataList;}

    // emotion meditation dataList
    private ArrayList<EmotionListData> mEmotionListMeditationDataList;
    public ArrayList<EmotionListData> getEmotionListMeditationDataList() {return mEmotionListMeditationDataList;}

    // emotion sleep DataList
    private ArrayList<EmotionListData> mEmotionListSleepDataList;
    public ArrayList<EmotionListData> getEmotionListSleepDataList() {return mEmotionListSleepDataList;}

    // emotion music DataList
    private ArrayList<EmotionListData> mEmotionListMusicDataList;
    public ArrayList<EmotionListData> getEmotionListMusicDataList() {return mEmotionListMusicDataList;}

    // 성격 검사 DataList -> 성격 검사 질문 데이터  2020.12.21
    private ArrayList<PersonQuestionData> mPersonQuestionDataList;
    public ArrayList<PersonQuestionData> getPersonQuestionDataList() {return mPersonQuestionDataList;}

    // 목소리 분석할때 읽는 지문 데이터 -> 2020.12.21
    private ArrayList<VoiceData> mVoiceDataList;
    public ArrayList<VoiceData> getVoiceDataList() {return mVoiceDataList;}

    // 성격 검사 결과 정보 & 성격대비 데이터 이미지 (프로필) 정보도 여기서 miniimg로 얻어서 처리 -> 2020.12.21ㅊ
    private ArrayList<PersonResultData> mPersonResultDataList;
    public ArrayList<PersonResultData> getPersonResultDataList() {return mPersonResultDataList;}

    // 성격 데이터
    private ArrayList<CharData> mCharDataList;
    public ArrayList<CharData> getCharDataList() {return mCharDataList;}

    // 성격 조합 데이터
    private ArrayList<DetermineCharData> mDetermineCharDataList;
    public ArrayList<DetermineCharData> getDetermineCharDataList() {return mDetermineCharDataList;}

    // 성격 명상 데이터
    private ArrayList<CharListData> mCharListMeditationDataList;
    public ArrayList<CharListData> getCharListMeditationDataList() {return mCharListMeditationDataList;}

    // 성격 수면 데이터
    private ArrayList<CharListData> mCharListSleepDataList;
    public ArrayList<CharListData> getCharListSleepDataList() {return mCharListSleepDataList;}

    // 성격 음악 데이터
    private ArrayList<CharListData> mCharListMusicDataList;
    public ArrayList<CharListData> getCharListMusicDataList() {return mCharListMusicDataList;}

    // 목소리 분석 데이터
    private ArrayList<VoiceAnalysisData> mVoiceAnalysisDataList;
    public ArrayList<VoiceAnalysisData> getVoiceAnalysisDataList() {return mVoiceAnalysisDataList;}


    // 초기화 관련 변수 초기화
    public void init(Resources AppRes,Context applicationContext) {
        bSaveRecording = false;
        mfbDBRef = FirebaseDatabase.getInstance().getReference();

        // 컬러데이터, 컬러테스트질문, 카데고리데이터
        mColorDataList = xmlColorDataParser(R.xml.colordata_result,AppRes);
        mQuestionDataList = xmlQuestionDataParser(R.xml.questiondata_result,AppRes);
        mCategoryDataList = xmlCategoryDataParser(R.xml.categorydata_result,AppRes);

        // 감정리스트_명상,수면,음악 데이터
        mEmotionListMeditationDataList = xmlEmotionListDataParser(R.xml.emotionlist_meditation_result,AppRes);
        mEmotionListSleepDataList = xmlEmotionListDataParser(R.xml.emotionlist_sleep_result,AppRes);
        mEmotionListMusicDataList = xmlEmotionListDataParser(R.xml.emotionlist_music_result,AppRes);

        // 성격 검사 리스트 추가 데이터들
        mPersonQuestionDataList = xmlPersonQuestionDataParser(R.xml.person_questiondata_result,AppRes);
        mVoiceDataList = xmlVoiceDataParser(R.xml.voice_data_result,AppRes);
        mPersonResultDataList = xmlPersonResultDataParser(R.xml.person_resultdata_result,AppRes);

        mCharDataList = xmlCharDataParser(R.xml.char_data_result,AppRes);

        mDetermineCharDataList = xmlDetermineCharDataParser(R.xml.determine_char_data_result,AppRes);
        mCharListMeditationDataList = xmlCharListDataParser(R.xml.char_meditation_data_result,AppRes);
        mCharListSleepDataList = xmlCharListDataParser(R.xml.char_sleep_data_result,AppRes);
        mCharListMusicDataList = xmlCharListDataParser(R.xml.char_music_data_result,AppRes);

        mVoiceAnalysisDataList = xmlVoiceAnalysisDataParser(R.xml.voiceanalysis_data_result,AppRes);

        mCurApplicationContext = applicationContext;
        mAppRes = AppRes;

        cur_contents = null;
        cur_contents_timer = null;
    }

    public Context getContext() { return mCurApplicationContext;}
    public UserProfile getUserProfile() {
        return mUserProfile;
    }

    //=====================================================
    // nickname duplicated 처리
    //=====================================================
    private OnRecvValNickNameListener mOnRecvValNickNameLister = null;
    public interface OnRecvValNickNameListener {
        void onRecvValNickName(boolean validate);
    }
    public void setOnRecvValNickNameListener(OnRecvValNickNameListener listenfunc){
        mOnRecvValNickNameLister = listenfunc;
    }

    // 중복이 안되어 있으면 해당 NickName은 허용 가능하므로 true, 이미 있으면 false를 반환
    public void sendValNickName(String nickName){
        if(this.mOnRecvValNickNameLister != null){
            mfbDBRef.child("nick").child(nickName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        mOnRecvValNickNameLister.onRecvValNickName(false);
                    }
                    else{
                        mOnRecvValNickNameLister.onRecvValNickName(true);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Error처리도 해야 한다.
                }
            });
        }
    }

    // 2020.12.21
    public void allClear()
    {
       mContentsList.clear();
       mContentsCharinfoList.clear();
       mSocialContentsList.clear();
    }

    //========================================================
    // profile 정보 던져서 서버에 저장
    //========================================================
    private OnRecvValProfileListener mRecvValProfileListener = null;
    public interface OnRecvValProfileListener {
        void onRecvValProfile(boolean validate);
    }
    public void setOnRecvValProfileListener(OnRecvValProfileListener listenfunc){
        mRecvValProfileListener = listenfunc;
    }
    public void sendValProfile(UserProfile profile){
        if(this.mRecvValProfileListener != null){
            mfbDBRef.child("users").child(profile.uid).setValue(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mfbDBRef.child("nick").child(profile.nickname).setValue(profile.uid);
                    mRecvValProfileListener.onRecvValProfile(true);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mRecvValProfileListener.onRecvValProfile(false);
                }
            });
        }
    }

    // 2021.01.21
    // 기존 prevProfile에 새롭게 입력하려는 것
    // 수정일 경우 nickname, intro, profileImageURI 중에서 수정안하는 것은 null로 준다.
    public void sendValNewProfileExt(UserProfile profile, String nickName, String intro, String profileImageURI){
        // 1. 기존 NickName있으면 삭제
        if(nickName != null){
            DatabaseReference prevRef = mfbDBRef.child("nick").child(profile.nickname);
            if(prevRef != null){
                prevRef.removeValue();
                profile.nickname = nickName;
            }
        }

        // 2. intro 확인
        if(intro != null){
            profile.profileIntro = intro;
        }

        // 3. profileImageURI 확인
        File upfile = new File(profileImageURI);
        if(upfile != null){
            SimpleDateFormat format_date = new SimpleDateFormat ( "yyyyMMdd" );
            Date date_now = new Date(System.currentTimeMillis());
            String curdate = format_date.format(date_now);

            Uri profileUri = Uri.fromFile(upfile);
            String curimgName = profile.uid + "_" + curdate + ".jpg";

            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(profieimgdir).child(profileUri.getLastPathSegment());
            UploadTask task = storageRef.putFile(profileUri);

            task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = task.getResult().getStorage().getDownloadUrl();
                    if(!uriTask.isSuccessful()){
                        try {
                            throw uriTask.getException();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mRecvValProfileListener.onRecvValProfile(false);
                        }
                    }

                    Uri downloadUri=uriTask.getResult();
                    String download_url = downloadUri.toString();
                    profile.profileimg = curimgName;

                    // 성공한 후에 보내야 한다.
                    sendValProfile(profile);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mCurApplicationContext,"fail upload",Toast.LENGTH_SHORT).show();
                    mRecvValProfileListener.onRecvValProfile(false);
                }
            });
        }else{
            sendValProfile(profile);
        }
    }

    //==============================================================================
    // 유저 프로파일 정보 받기
    //      true : 유저정보 정상적으로 받고, mUserProfile에 저장,
    //      false, 0: 유저 정보가 없음, false, 1000 : error나옴.
    //==============================================================================
    private OnRecvProfileListener mRecvProfileListener = null;
    public interface OnRecvProfileListener {
        void onRecvProfile(boolean validate,int errorcode);
    }
    public void setOnRecvProfileListener(OnRecvProfileListener listenfunc){
        mRecvProfileListener = listenfunc;
    }
    public void recvUserProfile(String uid){
        mfbDBRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    getinstance().mUserProfile = (UserProfile)snapshot.getValue(UserProfile.class);
                    mRecvProfileListener.onRecvProfile(true,0);
                }
                else{
                    // 없는 경우 데이터가 올라가지 않음, false이지만 errcode 0
                    mRecvProfileListener.onRecvProfile(false,0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error처리도 해야 한다.
                //mRecvContentsListener.onRecvContents(false);
                mRecvProfileListener.onRecvProfile(false,1000);
            }
        });
    }

    //==============================================================================
    // 해당 콘텐츠 정보를 서버에게 받아야 한다. (로그인할때에만 받아야 한다. 하루 기준)
    // 해당 정보를 다 받을떄까지 콜백처리 해야 함.
    //==============================================================================
    private OnRecvContentsListener mRecvContentsListener = null;
    public interface OnRecvContentsListener {
        void onRecvContents(boolean validate);
    }
    public void setOnRecvContentsListener(OnRecvContentsListener listenfunc){
        mRecvContentsListener = listenfunc;
    }

    public void recvContentsExt(){
        mfbDBRef.child(contentsInfoString).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot meditationSnapshot: snapshot.getChildren()) {
                        MeditationContents contentsdata = meditationSnapshot.getValue(MeditationContents.class);

                        // audio
                        contentsdata.audio =   NetServiceUtility.audiofiledir + contentsdata.audio + NetServiceUtility.audioextenstion;

                        // thumnail
                        contentsdata.thumbnail = NetServiceUtility.thumnaildir + contentsdata.thumbnail  + NetServiceUtility.imgextenstion;

                        // bg image
                        contentsdata.bgimg =   NetServiceUtility.bgimgdir + contentsdata.bgimg + NetServiceUtility.imgextenstion;

                        // showtype결정
                        if(contentsdata.artist.equals("0")){
                            if(!contentsdata.author.equals("0")){
                                contentsdata.showtype = 1;
                            }else{
                                contentsdata.showtype = 0;
                            }
                        }else{
                            contentsdata.showtype = 2;
                        }

                        mContentsList.add(contentsdata);
                    }
                    mRecvContentsListener.onRecvContents(true);
                }
                else{
                    mRecvContentsListener.onRecvContents(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mRecvContentsListener.onRecvContents(false);
            }
        });
    }


    //==============================================================================
    // 해당 콘텐츠 정보를 서버에게 받아야 한다. (로그인할때에만 받아야 한다. 하루 기준)
    // 해당 정보를 다 받을떄까지 콜백처리 해야 함.
    // 2021.01.24
    //==============================================================================
    private OnSocialRecvContentsListener mSocialRecvContentsListener = null;
    public interface OnSocialRecvContentsListener {
        void onSocialRecvContents(boolean validate);
    }
    public void setOnSocialRecvContentsListener(OnSocialRecvContentsListener listenfunc){
        mSocialRecvContentsListener = listenfunc;
    }

    public void recvSocialContentsExt(){
        mfbDBRef.child(socialContentsInfoString).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot meditationSnapshot: snapshot.getChildren()) {
                        MeditationContents contentsdata = meditationSnapshot.getValue(MeditationContents.class);

                        // audio
                        contentsdata.audio =   NetServiceUtility.audiofiledir + contentsdata.audio + NetServiceUtility.audioextenstion;

                        // thumnail
                        contentsdata.thumbnail = NetServiceUtility.mycontentsthumnaildir + contentsdata.thumbnail  + NetServiceUtility.imgextenstion;

                        // bg image -> bg는 이미지
                        // contentsdata.bgimg =   NetServiceUtility.bgimgdir + contentsdata.bgimg + NetServiceUtility.imgextenstion;

                        // showtype결정
                        if(contentsdata.artist.equals("0")){
                            if(!contentsdata.author.equals("0")){
                                contentsdata.showtype = 1;
                            }else{
                                contentsdata.showtype = 0;
                            }
                        }else{
                            contentsdata.showtype = 2;
                        }

                        mSocialContentsList.add(contentsdata);
                    }
                    mSocialRecvContentsListener.onSocialRecvContents(true);
                }
                else{
                    mSocialRecvContentsListener.onSocialRecvContents(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mSocialRecvContentsListener.onSocialRecvContents(false);
            }
        });
    }

    //==============================================================================
    // 해당 콘텐츠 성격 정보를 서버에게 받아야 한다. (로그인할때에만 받아야 한다. 하루 기준)
    // 해당 정보를 다 받을떄까지 콜백처리 해야 함.
    //==============================================================================
    private OnRecvContentsCharInfoListener mRecvContentsCharInfoListener = null;
    public interface OnRecvContentsCharInfoListener {
        void onRecvContentsCharInfo(boolean validate);
    }
    public void setOnRecvContentsCharInfoListener(OnRecvContentsCharInfoListener listenfunc){
        mRecvContentsCharInfoListener = listenfunc;
    }

    public void recvContentsCharInfo(){
        mfbDBRef.child(contentsCharInfoString).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot meditationSnapshot: snapshot.getChildren()) {
                        MeditationContentsCharInfo contentsdata = meditationSnapshot.getValue(MeditationContentsCharInfo.class);
                        mContentsCharinfoList.add(contentsdata);
                    }
                    mRecvContentsCharInfoListener.onRecvContentsCharInfo(true);
                }
                else{
                    mRecvContentsCharInfoListener.onRecvContentsCharInfo(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mRecvContentsCharInfoListener.onRecvContentsCharInfo(false);
            }
        });
    }


    //=========================================================================================
    // 감정 테스트 여부를 파악해서 홈에 관련된 카데고리 정보를 반환한다.
    // 감정 테스트를 했으면 UserProfile 구조체의 emotionid 정보를 통해 선택한 감정을 확인한다.
    //=========================================================================================
    public MeditationShowCategorys returnHomeShowCategorys(boolean mIsDoneTest){
        MeditationShowCategorys newShowCategorys = new MeditationShowCategorys();
        newShowCategorys.showcategorys = new ArrayList<MeditationCategory>();

         //  해당 감정의 id를 이용해서 매칭 태그 string list를 얻고 이것을 통해서 콘텐츠의
         //  healingtag에 같은 것이 하나라도 있으면 선택이 되는 것임.
         if(mIsDoneTest){
             for(int i = 0; i < 3; i++){
                 String genre = "";
                 if(i == 0) {
                     genre = genre1;
                 }else if(i == 1){
                     genre = genre2;
                 }else{
                     genre = genre3;
                 }

                 MeditationCategory emotionCategory =  getLocalEmotionTestCategory(genre);
                 if(emotionCategory != null){
                     newShowCategorys.showcategorys.add(emotionCategory);
                 }
             }
         }

         //  성격 검사 콘텐츠
         MeditationCategory charTypeCategory =  getLocalCharTypeCategory(0);
         if(charTypeCategory != null){
            newShowCategorys.showcategorys.add(charTypeCategory);
         }

         //  즐겨찾기한 콘텐츠
         MeditationCategory favoriteCategory = getFavoriteCategory(0);
         if(favoriteCategory != null){
             newShowCategorys.showcategorys.add(favoriteCategory);
         }

         // 3. 인기리스트
         MeditationCategory top10Category = getTop10Category(0);
         if(top10Category != null){
            newShowCategorys.showcategorys.add(top10Category);
         }

         // 최근 재생한 리스트
        MeditationCategory recentPlayCategory = getRecentPlayCategory(0);
        if(recentPlayCategory != null){
            newShowCategorys.showcategorys.add(recentPlayCategory);
        }



         // 4. 고정리스트 콘텐츠들
         List<String> inputContentsCategorysIds = asList("63","67","55","56","57","58","59","60","61","62","64","65");
         procContentsCategorys(inputContentsCategorysIds,newShowCategorys);

         return newShowCategorys;
    }

    // 명상 카테고리를 얻는다.
    public MeditationShowCategorys returnMeditationShowCategorys(boolean mIsDoneTest) {
        MeditationShowCategorys newShowCategorys = new MeditationShowCategorys();
        newShowCategorys.showcategorys = new ArrayList<MeditationCategory>();

        if(mIsDoneTest){
            MeditationCategory emotionMeditationCategory =  getLocalEmotionTestCategory(genre1);
            if(emotionMeditationCategory != null){
                newShowCategorys.showcategorys.add(emotionMeditationCategory);
            }
        }

        //  성격 검사 콘텐츠
        MeditationCategory charTypeCategory =  getLocalCharTypeCategory(1);
        if(charTypeCategory != null){
            newShowCategorys.showcategorys.add(charTypeCategory);
        }

        //    즐겨찾기한 콘텐츠
        MeditationCategory favoriteCategory = getFavoriteCategory(1);
        if(favoriteCategory != null){
            newShowCategorys.showcategorys.add(favoriteCategory);
        }

        // 인기리스트
        MeditationCategory top10Category = getTop10Category(1);
        if(top10Category != null){
            newShowCategorys.showcategorys.add(top10Category);
        }

        // 최근 재생한 리스트
        MeditationCategory recentPlayCategory = getRecentPlayCategory(1);
        if(recentPlayCategory != null){
            newShowCategorys.showcategorys.add(recentPlayCategory);
        }

        List<String> inputContentsCategorysIds = asList("55","56","57","58","60");
        procContentsCategorys(inputContentsCategorysIds,newShowCategorys);

        return newShowCategorys;
    }


    // 수면  카테고리를 얻는다.
    public MeditationShowCategorys returnSleepShowCategorys(boolean mIsDoneTest) {
        MeditationShowCategorys newShowCategorys = new MeditationShowCategorys();
        newShowCategorys.showcategorys = new ArrayList<MeditationCategory>();

        if(mIsDoneTest){
            MeditationCategory emotionSleepCategory =  getLocalEmotionTestCategory(genre2);
            if(emotionSleepCategory != null){
                newShowCategorys.showcategorys.add(emotionSleepCategory);
            }
        }

        //  성격 검사 콘텐츠
        MeditationCategory charTypeCategory =  getLocalCharTypeCategory(2);
        if(charTypeCategory != null){
            newShowCategorys.showcategorys.add(charTypeCategory);
        }

        // 즐겨찾기한 콘텐츠
        // userprofile의 favorites.contents가 하나라도 있으면 만든다.
        MeditationCategory favoriteCategory = getFavoriteCategory(2);
        if(favoriteCategory != null){
            newShowCategorys.showcategorys.add(favoriteCategory);
        }

        // 인기리스트
        MeditationCategory top10Category = getTop10Category(2);
        if(top10Category != null){
            newShowCategorys.showcategorys.add(top10Category);
        }

        // 최근 재생한 리스트
        MeditationCategory recentPlayCategory = getRecentPlayCategory(2);
        if(recentPlayCategory != null){
            newShowCategorys.showcategorys.add(recentPlayCategory);
        }

        List<String> inputContentsCategorysIds = asList("63","67","62","64");
        procContentsCategorys(inputContentsCategorysIds,newShowCategorys);
        return newShowCategorys;
    }

    // 음악  카테고리를 얻는다.
    public MeditationShowCategorys returnMusicShowCategorys(boolean mIsDoneTest) {
        MeditationShowCategorys newShowCategorys = new MeditationShowCategorys();
        newShowCategorys.showcategorys = new ArrayList<MeditationCategory>();

        if(mIsDoneTest){
            MeditationCategory emotionMusicCategory =  getLocalEmotionTestCategory(genre3);
            if(emotionMusicCategory != null){
                newShowCategorys.showcategorys.add(emotionMusicCategory);
            }
        }

        //  성격 검사 콘텐츠
        MeditationCategory charTypeCategory =  getLocalCharTypeCategory(3);
        if(charTypeCategory != null){
            newShowCategorys.showcategorys.add(charTypeCategory);
        }

        //    즐겨찾기한 콘텐츠
        //    userprofile의 favorites.contents가 하나라도 있으면 만든다.
        MeditationCategory favoriteCategory = getFavoriteCategory(3);
        if(favoriteCategory != null){
            newShowCategorys.showcategorys.add(favoriteCategory);
        }

        MeditationCategory top10Category = getTop10Category(3);
        if(top10Category != null){
            newShowCategorys.showcategorys.add(top10Category);
        }

        // 최근 재생한 리스트
        MeditationCategory recentPlayCategory = getRecentPlayCategory(3);
        if(recentPlayCategory != null){
            newShowCategorys.showcategorys.add(recentPlayCategory);
        }

        List<String> inputContentsCategorysIds = asList("59","61","65");
        procContentsCategorys(inputContentsCategorysIds,newShowCategorys);
        return newShowCategorys;
    }

    private String getCurdateString(){
        SimpleDateFormat format_date = new SimpleDateFormat ( "yyyy-MM-dd" );
        Date date_now = new Date(System.currentTimeMillis());
        String curdate = format_date.format(date_now);
        return curdate;
    }


    // 성격테스트후에 성격에 따른 성격테스트 결과에 대한 카테고리를 알려준다.]
    private MeditationContents getCharTestCategoryContents(String genre,boolean forceAction)
    {
        String curcharid = Integer.toString(mUserProfile.chartype);

        int dataNum = 0; // mEmotionListMeditationDataList.size();
        Map<String,MeditationContentsCharInfo> contentsCharList = new LinkedHashMap<>();

        // 카테고리 정보를 얻어야 한다.
        CategoryData curCategoryData = null;
        ArrayList<CharListData> curCharListDataList = null;

        // 주어진 장르와 같은 DataList를 얻는다.
        if(genre.equals(genre1)){
            curCategoryData = getCategoryDataCharExt(curcharid, genre1);
            curCharListDataList = mCharListMeditationDataList;
        }else if(genre.equals(genre2)){
            curCategoryData = getCategoryDataCharExt(curcharid, genre2);
            curCharListDataList = mCharListSleepDataList;
        }else if(genre.equals(genre3)){
            curCategoryData = getCategoryDataCharExt(curcharid, genre3);
            curCharListDataList = mCharListMusicDataList;
        }else{
            return null;
        }

        dataNum = curCharListDataList.size();
        MeditationCategory mCurCategory = null;

        // 1. 성격 태그를 먼저 찾는다.
        List<String> chartaglist = null;
        for(int i = 0; i < dataNum; i++) {
            CharListData mData = curCharListDataList.get(i);
            if (curcharid.equals(mData.charid)) {
                chartaglist = asList(mData.chartag.split(","));
                break;
            }
        }

        // 2. 찾은 감정태그를 이용해서 콘텐츠의 성격 태그들과 비교한다.
        if(chartaglist == null)
            return null;

        int contentsNum = mContentsCharinfoList.size();
        int subDataNum = chartaglist.size();

        for(int k = 0; k < contentsNum; k++){
            MeditationContentsCharInfo curContents = mContentsCharinfoList.get(k);
            List<String> contentschartaglist = asList(curContents.chartag.split(","));
            int chartagNum = contentschartaglist.size();

            boolean bFindContents = false;

            // char 하나라도 같으면 넣으면 된다.
            for(int j = 0; j < subDataNum; j++) {
                String tagString = chartaglist.get(j);

                for (int idx = 0; idx < chartagNum; idx++) {
                    String curCharTag = contentschartaglist.get(idx);
                    if (tagString.equals(curCharTag) && curContents.genre.equals(genre)) {   // genre비교 안되어 있었음.
                        // 해당 chartag가 Map에 있는 지 조사
                        if (!contentsCharList.containsKey(curContents.uid)) {
                            contentsCharList.put(curContents.uid, curContents);
                            bFindContents = true;
                            break;
                        }
                    }
                }

                if(bFindContents == true)
                    break;
            }
        }

        // 완료된후에 Map에 들어가 있는 원소들을 모두 돌면서 contentsid를 얻고
        if(contentsCharList.size()> 0){
            // 해당 장르의 저장된 날짜와 ContentID를 얻어야 한다.

            String prevCharContentId = "";
            String prevCharTestTime = "";

            if(genre.equals(genre1)) {
                prevCharContentId = PreferenceManager.getString(mCurApplicationContext, "char_meditation_contentid");
            }else if(genre.equals(genre2)){
                prevCharContentId = PreferenceManager.getString(mCurApplicationContext, "char_sleep_contentid");
            }else if(genre.equals(genre3)){
                prevCharContentId = PreferenceManager.getString(mCurApplicationContext, "char_music_contentid");
            }else{
                return null;
            }

            if(genre.equals(genre1)) {
                prevCharTestTime = PreferenceManager.getString(mCurApplicationContext, "char_meditation_date");
            }else if(genre.equals(genre2)){
                prevCharTestTime = PreferenceManager.getString(mCurApplicationContext, "char_sleep_date");
            }else if(genre.equals(genre3)){
                prevCharTestTime = PreferenceManager.getString(mCurApplicationContext, "char_music_date");
            }else{
                return null;
            }

            if(prevCharContentId == null || prevCharContentId == null){
                return null;
            }else if(prevCharContentId.isEmpty() ||  prevCharTestTime.isEmpty()){
                prevCharContentId = "0";
            }

            boolean isCalcCharContents = false;

            if(forceAction){
                isCalcCharContents = true;
            }else {
                // 시간 체크 해야 한다.
                long day = UtilAPI.GetDay_Date(prevCharTestTime);
                if (day > 0) {
                    isCalcCharContents = true;
                }
            }

            // forceAction
            if(isCalcCharContents){
                // 해당 id보다 큰것을 찾는다. 만약에 없으면 처음으로 돌아가야 한다.
                boolean findContents = false;
                MeditationContentsCharInfo entityInfo = null;
                MeditationContentsCharInfo fristEntityInfo = null;

                int entryNum = 0;

                // 처음초기값을 넣었으므로, 없으면 그냥 저장된다.
                for (Map.Entry<String, MeditationContentsCharInfo> entry : contentsCharList.entrySet()) {
                    if(entryNum == 0){
                        entityInfo = entry.getValue();
                    }

                    if( Integer.parseInt(prevCharContentId) < Integer.parseInt(entry.getKey())){
                        entityInfo = entry.getValue();
                        findContents = true;
                        break;
                    }
                    entryNum++;
                }

                String curDateString = getCurdateString();

                if(genre.equals(genre1)) {
                    PreferenceManager.setString(mCurApplicationContext, "char_meditation_contentid",entityInfo.uid);
                    PreferenceManager.setString(mCurApplicationContext, "char_meditation_date",curDateString);
                }else if(genre.equals(genre2)){
                    PreferenceManager.setString(mCurApplicationContext, "char_sleep_contentid",entityInfo.uid);
                    PreferenceManager.setString(mCurApplicationContext, "char_sleep_date",curDateString);
                }else if(genre.equals(genre3)){
                    PreferenceManager.setString(mCurApplicationContext, "char_music_contentid",entityInfo.uid);
                    PreferenceManager.setString(mCurApplicationContext, "char_music_date",curDateString);
                }else{
                    return null;
                }

                return getMeditationContents(entityInfo.uid);
            }else{
                // 기존 정보를 이용해서 보낸다.
                return getMeditationContents(prevCharContentId);
            }
        }

        return null;
    }


    // 감정테스트후에 감정에 따른 감정테스트 결과에 대한 카데고리를 알려준다.
    private MeditationCategory getEmotionTestCategory(String genre)
    {
        // 명상
        String curemotionid = Integer.toString(mUserProfile.emotiontype);

        int dataNum = 0; // mEmotionListMeditationDataList.size();
        Map<String,MeditationContents> contentsList = new HashMap<>();

        // 카테고리 정보를 얻어야 한다.
        CategoryData curCategoryData = null;
        ArrayList<EmotionListData> curEmotionListDataList = null;

        // 주어진 장르와 같은 DataList를 얻는다.
        if(genre.equals(genre1)) {
            curCategoryData = getCategoryDataExt(curemotionid, genre1);
            curEmotionListDataList = mEmotionListMeditationDataList;
        }else if(genre.equals(genre2)){
            curCategoryData = getCategoryDataExt(curemotionid, genre2);
            curEmotionListDataList = mEmotionListSleepDataList;
        }else if(genre.equals(genre3)){
            curCategoryData = getCategoryDataExt(curemotionid, genre3);
            curEmotionListDataList = mEmotionListMusicDataList;
        }else{
            return null;
        }

        dataNum = curEmotionListDataList.size();
        MeditationCategory mCurCategory = null;

        // 1. 감정 태그를 먼저 찾는다.
        List<String> healingtaglist = null;
        for(int i = 0; i < dataNum; i++) {
            EmotionListData mData = curEmotionListDataList.get(i);
            if (curemotionid.equals(mData.emotionid)) {
                healingtaglist = asList(mData.healingtag.split(","));
                break;
            }
        }

        // 2. 찾은 감정태그를 이용해서 콘텐츠의 태그들과 비교한다.
        if(healingtaglist == null)
            return null;

        int contentsNum = mContentsList.size();
        int subDataNum = healingtaglist.size();

        for(int k = 0; k < contentsNum; k++){
            MeditationContents curContents = mContentsList.get(k);
            List<String> contentshealingtaglist = asList(curContents.healingtag.split(","));
            int healingtagNum = contentshealingtaglist.size();

            boolean bFindContents = false;

            // healingtag 하나라도 같으면 넣으면 된다.
            for(int j = 0; j < subDataNum; j++) {
                String tagString = healingtaglist.get(j);

                for (int idx = 0; idx < healingtagNum; idx++) {
                    String curHealingTag = contentshealingtaglist.get(idx);
                    if (tagString.equals(curHealingTag) && curContents.genre.equals(genre)) {   // genre비교 안되어 있었음.
                        // 해당 HealingTag가 Map에 있는 지 조사
                        if (!contentsList.containsKey(curContents.uid)) {
                            contentsList.put(curContents.uid, curContents);
                            bFindContents = true;
                            break;
                        }
                    }
                }

                if(bFindContents == true)
                    break;
            }
        }

        // 완료된후에 Map에 들어가 있는 원소들을 모두 돌면서 contentsid를 얻고
        if(contentsList.size()> 0){
            mCurCategory = new MeditationCategory();
            mCurCategory.name = curCategoryData.name;
            mCurCategory.contests = new ArrayList<MediationShowContents>();
        }

        //======================================================================
        // Map을 랜덤하게 일정수를 넣도록 하는것이 좋다. 우선은 Max치만 넣게 한다.
        //======================================================================
        int curNum = 0;
        for (Map.Entry<String, MeditationContents> entry : contentsList.entrySet()) {
            if(curNum < curCategoryData.maxnum) {
                String curkey = entry.getKey();  // contents id
                MediationShowContents newEntity = new MediationShowContents();
                newEntity.entity = entry.getValue();
                mCurCategory.contests.add(newEntity);
                curNum++;
            }
        }

        // 2020.12.05
        if(mCurCategory != null &&  mCurCategory.contests.size() > 0){
            Collections.shuffle(mCurCategory.contests);
        }

        return mCurCategory;
    }

    // selectType 0 : all 1: 명상, 2: 수면 3 : 음악
    // 종류별로 Top10콘텐츠를 알려준다.
    private void SortContentsFavoritecnt(boolean AscendingOrder ){
        Collections.sort(mContentsList, new Comparator() {
            @Override
            public int compare(Object t1, Object t2) {
                MeditationContents o1 = (MeditationContents)t1;
                MeditationContents o2 = (MeditationContents)t2;
                // 내림차순.
                if(AscendingOrder){
                    if(o1.favoritecnt  > o2.favoritecnt) {
                        return 1;
                    }
                    else if(o1.favoritecnt < o2.favoritecnt) {
                        return -1;
                    }
                }else{
                    if(o1.favoritecnt  > o2.favoritecnt) {
                        return -1;
                    }
                    else if(o1.favoritecnt < o2.favoritecnt) {
                        return 1;
                    }
                }
                return 0;
            }
        });
    }

    private void SortContentsUID(boolean AscendingOrder ){
        Collections.sort(mContentsList, new Comparator() {
            @Override
            public int compare(Object t1, Object t2) {
                MeditationContents o1 = (MeditationContents)t1;
                MeditationContents o2 = (MeditationContents)t2;
                // 내림차순.
                if(AscendingOrder){
                    if( Integer.parseInt(o1.uid)  > Integer.parseInt(o2.uid)) {
                        return 1;
                    }
                    else if(Integer.parseInt(o1.uid) < Integer.parseInt(o2.uid)) {
                        return -1;
                    }
                }else{
                    if( Integer.parseInt(o1.uid)  > Integer.parseInt(o2.uid)) {
                        return -1;
                    }
                    else if(Integer.parseInt(o1.uid) < Integer.parseInt(o2.uid)) {
                        return 1;
                    }
                }
                return 0;
            }
        });
    }


    // 최근 재생 콘텐츠 리스트
    private MeditationCategory getRecentPlayCategory(int seletType)
    {
        int recentplaylistNum = mUserProfile.recentplaylist.size();
        if(recentplaylistNum == 0)
            return null;

        CategoryData entityData = getCategoryData(recentPlayCategoryId);
        if(entityData == null)
            return null;

        MeditationCategory recentPlayCategory =  new MeditationCategory();
        recentPlayCategory.name = entityData.name;
        recentPlayCategory.contests = new ArrayList<MediationShowContents>();

        int contentsNum = 0;
        int contentsSize = mContentsList.size();
        MeditationContents entity = null;

        for(int i = 0; i < recentplaylistNum; i++){
            entity = getMeditationContents(mUserProfile.recentplaylist.get(i));

            if(seletType == 1){
                if(!entity.genre.equals(genre1)){
                    continue;
                }
            }
            else if(seletType == 2){
                if(!entity.genre.equals(genre2)){
                    continue;
                }
            }
            else if(seletType == 3){
                if(!entity.genre.equals(genre3)){
                    continue;
                }
            }

            if(mContentsList.get(i).favoritecnt > 0 && contentsNum < maxRecentPlayListNum){
                MediationShowContents newEntity = new MediationShowContents();
                newEntity.entity = entity;
                recentPlayCategory.contests.add(newEntity);
                contentsNum++;
            }
        }

        if(contentsNum > 0)
            return recentPlayCategory;

        return null;
    }

    // top10 카테고리
    private MeditationCategory getTop10Category(int seletType)
    {
        CategoryData entityData = getCategoryData(top10CategoryId);

        SortContentsFavoritecnt(false);  // 내림차순 정렬

        if(mContentsList.get(0).favoritecnt == 0 || entityData == null)
            return null;

        //Log.d("MeditationCategory","MeditationCategory size : "+ mContentsList.size());

        // 내림차순한 Content의 favoritecnt가 처음 원소가 0이면 top10은 의미가 없고 값도 null을 주어야 한다.
        MeditationCategory top10Category =  new MeditationCategory();
        top10Category.name = entityData.name;
        top10Category.contests = new ArrayList<MediationShowContents>();
        top10Category.subtype = 1;

        // favoritecnt가 0 이상인것만 올려야 한다. 그리고 최대 30개
        int contentsNum = 0;
        int contentsSize = mContentsList.size();
        MeditationContents entity = null;

        for(int i = 0; i < contentsSize; i++){
            entity = mContentsList.get(i);

            if(seletType == 1){
                if(!entity.genre.equals(genre1)){
                    continue;
                }
            }
            else if(seletType == 2){
                if(!entity.genre.equals(genre2)){
                    continue;
                }
            }
            else if(seletType == 3){
                if(!entity.genre.equals(genre3)){
                    continue;
                }
            }

            if(mContentsList.get(i).favoritecnt > 0 && contentsNum < maxTop10ContentsNum){
                MediationShowContents newEntity = new MediationShowContents();
                newEntity.entity = entity;
                top10Category.contests.add(newEntity);
                contentsNum++;
            }
        }

        // uid별로 정렬이 되어 원래대로 되어야 한다.
        SortContentsUID(true);

        if(contentsNum > 0)
            return top10Category;

        return null;
    }

    // selectType 0 : all 1: 명상, 2: 수면 3 : 음악
    // 종류별로 즐겨찾기한 콘텐츠들을 알려준다.
    private MeditationCategory getFavoriteCategory(int seletType)
    {
        int favoritesContentsNum = mUserProfile.favoriteslist.size();
        if(favoritesContentsNum > 0){
            CategoryData entityData = getCategoryData(favoriteCategoryId);

            if(entityData != null){
                MeditationCategory favoriteCategory = new MeditationCategory();
                favoriteCategory.name = entityData.name;
                favoriteCategory.contests = new ArrayList<MediationShowContents>();

                // 유저프로파일에 있는 favorites 콘텐츠를 통해서 카데고리 구성
                boolean checkedEntity = false;
                int contentsNum = 0;

                for (Map.Entry<String, Boolean> entry : mUserProfile.favoriteslist.entrySet()) {
                    String curkey = entry.getKey();  // contents id
                    checkedEntity = false;
                    MeditationContents entity = getMeditationContents(curkey);

                    if(seletType == 1){
                        if(entity.genre.equals(genre1))  checkedEntity = true;
                    }else if(seletType == 2){
                        if(entity.genre.equals(genre2))  checkedEntity = true;
                    }else if(seletType == 3){
                        if(entity.genre.equals(genre3))  checkedEntity = true;
                    }else{
                        checkedEntity = true;
                    }

                    if(entity != null && checkedEntity == true){
                        if(contentsNum < maxFavoriteContentsNum){
                            MediationShowContents newEntity = new MediationShowContents();
                            newEntity.entity = entity;
                            favoriteCategory.contests.add(newEntity);
                            contentsNum++;
                        }
                    }
                }

                if(favoriteCategory.contests.size() > 0)
                    return favoriteCategory;
            }
        }
        return null;
    }

    // 주어진 Categoryid 집합들에 맞는 콘텐츠들을 모아서 한꺼번에 처리한다.
    private void procContentsCategorys(List<String> categoryids,MeditationShowCategorys newShowCategorys)
    {
        int dataNum = categoryids.size();
        for(int i = 0; i < dataNum; i++){
            MeditationCategory contentsCategory = getContentsCategory(categoryids.get(i));
            if(contentsCategory != null){
                newShowCategorys.showcategorys.add(contentsCategory);
            }
        }
    }

    // 주어진 단일 Categoryid에 맞는 콘텐츠들을 모은다.
    private MeditationCategory getContentsCategory(String categoryid)
    {
        int dataNum = mContentsList.size();
        int curNum = 0;
        CategoryData entityData = getCategoryData(categoryid);

        if(entityData != null && dataNum > 0) {
            MeditationCategory contentsCategory = new MeditationCategory();
            contentsCategory.name = entityData.name;
            contentsCategory.contests = new ArrayList<MediationShowContents>();

            for(int i = 0; i < dataNum; i++){
                MeditationContents contents = mContentsList.get(i);
                if(contents.categoryid.equals(entityData.id)){
                    if(curNum < entityData.maxnum){
                        MediationShowContents newEntity = new MediationShowContents();
                        newEntity.entity = contents;
                        contentsCategory.contests.add(newEntity);
                        curNum++;
                    }
                }
            }

            if(contentsCategory.contests.size() == 0){
                contentsCategory.contests = null;
                contentsCategory = null;
                return null;
            }

            return contentsCategory;
        }
        return null;
    }

    // 2020.12.22
    // 새로운 정보를 얻을때 사용한다.
    public void reqChartypeAllContents(boolean forceAction){

        mChartypeMeditationContent = getCharTestCategoryContents(genre1,forceAction);
        mChartypeSleepContent = getCharTestCategoryContents(genre2,forceAction);
        mChartypeMusicContent = getCharTestCategoryContents(genre3,forceAction);

        String curcharid = Integer.toString(mUserProfile.chartype);

        CategoryData curCategoryData = null;
        curCategoryData = getCategoryDataCharExt(curcharid, genre0);

        mChartypeHomeCategory.allClear();
        mChartypeMeditationCategory.allClear();
        mChartypeSleepategory.allClear();
        mChartypeMusicCategory.allClear();

        if(mUserProfile.chartype == 0)
            return;

        mChartypeHomeCategory.name = curCategoryData.name;
        mChartypeHomeCategory.contests = new ArrayList<MediationShowContents>();

        curCategoryData = getCategoryDataCharExt(curcharid, genre1);
        mChartypeMeditationCategory.name = curCategoryData.name;
        mChartypeMeditationCategory.contests = new ArrayList<MediationShowContents>();

        curCategoryData = getCategoryDataCharExt(curcharid, genre2);
        mChartypeSleepategory.name = curCategoryData.name;
        mChartypeSleepategory.contests = new ArrayList<MediationShowContents>();

        curCategoryData = getCategoryDataCharExt(curcharid, genre3);
        mChartypeMusicCategory.name = curCategoryData.name;
        mChartypeMusicCategory.contests = new ArrayList<MediationShowContents>();

        MediationShowContents newEntity = new MediationShowContents();
        newEntity.entity = mChartypeMeditationContent;
        mChartypeHomeCategory.contests.add(newEntity);
        mChartypeMeditationCategory.contests.add(newEntity);

        newEntity = new MediationShowContents();
        newEntity.entity = mChartypeSleepContent;
        mChartypeHomeCategory.contests.add(newEntity);
        mChartypeSleepategory.contests.add(newEntity);

        newEntity = new MediationShowContents();
        newEntity.entity = mChartypeMusicContent;
        mChartypeHomeCategory.contests.add(newEntity);
        mChartypeMusicCategory.contests.add(newEntity);
    }

    private MeditationCategory getLocalCharTypeCategory(int selType)
    {
        if(mUserProfile.chartype == 0)
            return null;

        switch (selType){
            case 0:
                return mChartypeHomeCategory;
            case 1:
                return mChartypeMeditationCategory;

            case 2:
                return mChartypeSleepategory;

            case 3:
                return mChartypeMusicCategory;
        }
        return null;
    }


    // 2020.12.05 start
    // 새로운 정보를 얻을때에 사용한다.
    public void reqEmotionAllContents(){
        mEmotionMeditationCategory = getEmotionTestCategory(genre1);
        mEmotionSleepCategory = getEmotionTestCategory(genre2);
        mEmotionMusicCategory = getEmotionTestCategory(genre3);
    }

    private MeditationCategory getLocalEmotionTestCategory(String genre)
    {
        if(genre.equals(genre1)){
            return mEmotionMeditationCategory;
        }else if(genre.equals(genre2)){
            return mEmotionSleepCategory;
        }else if(genre.equals(genre3)){
            return mEmotionMusicCategory;
        }

        return null;
    }
    // 2020.12.05 end

    // 주어진 타입에 따라서 관련 MeditationShowCategorys을 알려준다.
    // 1: home  2: 명상  3: 수면, 4: 음악 5: 테스트용
    public MeditationShowCategorys reqMediationType(int type, boolean mIsDoneTest){
        switch (type){
            case 1 :  // home
                return returnHomeShowCategorys(mIsDoneTest);
            case 2:   // 명상
                return returnMeditationShowCategorys(mIsDoneTest);
            case 3:   // 수면
                return returnSleepShowCategorys(mIsDoneTest);
            case 4:   // 음악
                return returnMusicShowCategorys(mIsDoneTest);
            case 5:   // Test 용
                // home에 대한 정보를 만들어서 처리한다.
                MeditationShowCategorys newShowCategorys = new MeditationShowCategorys();
                newShowCategorys.showcategorys = new ArrayList<MeditationCategory>();

                MeditationCategory newCategory = new MeditationCategory();
                newCategory.name = "오늘을 찾는 명상";
                newCategory.contests = new ArrayList<MediationShowContents>();

                int sizeNum = NetServiceManager.getinstance().mContentsList.size();
                for(int i = 0; i <  sizeNum ; i++){
                    if(NetServiceManager.getinstance().mContentsList.get(i).genre.equals("명상")){
                        MediationShowContents entity = new MediationShowContents();
                        entity.entity = NetServiceManager.getinstance().mContentsList.get(i);
                        newCategory.contests.add(entity);
                    }
                }

                newShowCategorys.showcategorys.add(newCategory);

                newCategory = new MeditationCategory();
                newCategory.name = "오늘을 찾는 수면";
                newCategory.contests = new ArrayList<MediationShowContents>();

                sizeNum = NetServiceManager.getinstance().mContentsList.size();
                for(int i = 0; i <  sizeNum ; i++){
                    if(NetServiceManager.getinstance().mContentsList.get(i).genre.equals("수면")){
                        MediationShowContents entity = new MediationShowContents();
                        entity.entity = NetServiceManager.getinstance().mContentsList.get(i);
                        newCategory.contests.add(entity);
                    }
                }

                newShowCategorys.showcategorys.add(newCategory);

                newCategory = new MeditationCategory();
                newCategory.name = "오늘을 찾는 음악";
                newCategory.contests = new ArrayList<MediationShowContents>();

                sizeNum = NetServiceManager.getinstance().mContentsList.size();
                for(int i = 0; i <  sizeNum ; i++){
                    if(NetServiceManager.getinstance().mContentsList.get(i).genre.equals("음악")){
                        MediationShowContents entity = new MediationShowContents();
                        entity.entity = NetServiceManager.getinstance().mContentsList.get(i);
                        newCategory.contests.add(entity);
                    }
                }

                newShowCategorys.showcategorys.add(newCategory);
                return newShowCategorys;
            default:
                break;
        }
        return null;
    }

    //==============================================================================================
    //  해당 콘텐츠의 좋아요, 싫어요 결정. reactiionCode 0: Default 1 : 좋아요, 2: 싫어요
    // ex) NetServiceManager.getinstance().sendFavoriteEvent("dkefddfeassss","10001",1);
    //==============================================================================================
    public void sendFavoriteEvent(String uid, String contentid, int reactionCode){
        FirebaseDatabase.getInstance().getReference(contentsInfoString).child(contentid)
                .runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        MeditationContents infoData =  currentData.getValue(MeditationContents.class);

                        if (infoData == null) {
                            return Transaction.success(currentData);
                        }

                        // 2020.12.11
                        Integer curstate = infoData.states.get(uid);

                        if(infoData.states.containsKey(uid)){
                            // 기존의 값을 입력값을 기준으로 해서 상태 업데이트
                            int prevReaction = curstate.intValue();

                            // remove는 될수가 없다. Add만 있다. 따라서 비교해서 수정만 하면 된다.
                            // 우선 이전것을 - 한다. 그리고  현재 것을 +한다.
                            if(prevReaction == 1){
                                infoData.favoritecnt -= 1;
                            }else if(prevReaction == 2){
                                infoData.hatecnt -= 1;
                            }

                            if(reactionCode == 1){
                                infoData.favoritecnt += 1;
                            }else if(reactionCode == 2){
                                infoData.hatecnt += 1;
                            }

                            if(infoData.favoritecnt < 0 ) infoData.favoritecnt = 0;
                            if(infoData.hatecnt < 0 ) infoData.hatecnt = 0;

                            infoData.states.put(uid,reactionCode);
                        }else {
                            if(reactionCode == 1){
                                infoData.favoritecnt += 1;
                                infoData.states.put(uid,reactionCode);
                            }else if(reactionCode == 2){
                                infoData.hatecnt += 1;
                                infoData.states.put(uid,reactionCode);
                            }else{
                                // error
                                return null;
                            }
                        }

                        if(infoData.favoritecnt < 0 ) infoData.favoritecnt = 0;
                        if(infoData.hatecnt < 0 ) infoData.hatecnt = 0;

                        currentData.setValue(infoData);
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                    }
                });
    }

    // 해당 콘텐츠의 해당 유저의 favorite여부 0: Default 1 : 좋아요, 2: 싫어요
    public int reqContentsFavoriteEvent(String uid, String contentid)
    {
        int itemsize= mContentsList.size();

        for(int i = 0; i < itemsize;i++) {
            MeditationContents contents = mContentsList.get(i);
            if(contents.uid.equals(contentid)){
                Integer value =  contents.states.get(uid);
                if(value == null){
                    return 0;
                }
                return value.intValue();
            }
        }

        return 0;
    }

    // 즐겨찾기 여부 : false - 즐겨찾기 안되어 있음. true - 즐겨찾기 되어있음
    public Boolean reqFavoriteContents(String contentid)
    {
        if(mUserProfile.favoriteslist.containsKey(contentid))
            return true;

        return false;
    }

    //  즐겨찾기 설정 함수 isfavorite : true(즐겨찾기 ON),false(즐겨찾기 off)
    public void sendFavoriteContents(String contentid,Boolean isfavorite){
        boolean isHaveContents = false;

        if(mUserProfile.favoriteslist.containsKey(contentid)){
            isHaveContents = true;
        }

        if(isfavorite == true){
            // 기존에 값이 있는지 확인 필요
            if(!isHaveContents){
                // 값을 넣어 주어야 함.
                mUserProfile.favoriteslist.put(contentid,true);
                // 서버에 값을 넣어주어야 한다.
                mfbDBRef.child("users").child(mUserProfile.uid).setValue(mUserProfile);
            }
        }else{
            // 있는 놈만 지운다.
            if(isHaveContents) {
                mUserProfile.favoriteslist.remove(contentid);
            }
            mfbDBRef.child("users").child(mUserProfile.uid).setValue(mUserProfile);
        }
    }

    // 성격검사 , selectid 1~16 : 감정
    public void checkFeelTest(int selectid)
    {
        mUserProfile.emotiontype = selectid;
        mfbDBRef.child("users").child(mUserProfile.uid).setValue(mUserProfile);
    }

    // 컬러 검사. 선택한 colorList 1~4선택된 6개로 온다.
    // 해당 내용을 바탕으로 해서 감정 평가 해서 mUserProfile.emotiontype 에 넣어준다.

    public Map<String,Integer> colorTestResult = new HashMap<>();

    // emotionid 값을 string으로 준다. 만약에 0이 오면 문제가 있는 것임.
    public String checkColorTest(List<Integer> answerList)
    {
        colorTestResult.clear();

        int questionNum = answerList.size();

        for(int i = 0; i < questionNum; i++){
            int curValue = answerList.get(i).intValue();

            QuestionData curQuestion = mQuestionDataList.get(i);

            int curSelColorId = getCurAnswerId(curQuestion,curValue);

            ColorData curData = getColorData(curSelColorId);

            // 선택한 colorData의 감정과 score를 저장
            if(curData != null){
                // 이미 해당 감정 데이터가 있는 경우에는 기존값을 얻어와서 더해 준다.
                int curScore = curData.score;

                if(colorTestResult.containsKey(curData.emotionid)){
                    int preScore = colorTestResult.get(curData.emotionid).intValue();
                    curScore += preScore;
                }
                colorTestResult.put(curData.emotionid,curScore);
            }else{
                Log.d("checkColorTest","error curData!");
                continue;
            }
        }

        // Value가 가장 높은 Key값을 알아내면 그것이 해당 감정 id이다.
        Map<String,Integer> result = sortMapByValue(colorTestResult,false);
        String findemotionid =  "0";
        for (Map.Entry<String, Integer> entry : result.entrySet()) {
            Log.d("checkColorTest","Key : "+ entry.getKey() + " Value : "+entry.getValue());
            String curkey = entry.getKey();
            int curValue = entry.getValue().intValue();
            findemotionid = entry.getKey();
        }

        mUserProfile.emotiontype = Integer.parseInt(findemotionid);
        // 최종 ID
        return findemotionid;
    }

    // 맵에 대한 오른차순 정렬
    public static LinkedHashMap<String, Integer> sortMapByValue(Map<String, Integer> map,boolean Descending) {
        List<Map.Entry<String, Integer>> entries = new LinkedList<>(map.entrySet());
        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();

        if(Descending == true){  // 내림차순
            Collections.sort(entries, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        }else{  // 오름차순
            Collections.sort(entries, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));
        }

        for (Map.Entry<String, Integer> entry : entries) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    // selidx 1~4
    public int getCurAnswerId(QuestionData curQuestion, int curValue)
    {
        int curSelColorId  = 0;

        if(curValue == 1){
            curSelColorId = curQuestion.answer1_id;
        }else if(curValue == 2){
            curSelColorId = curQuestion.answer2_id;
        }else if(curValue == 3){
            curSelColorId = curQuestion.answer3_id;
        }else if(curValue == 4){
            curSelColorId = curQuestion.answer4_id;
        }else{
            Log.d("checkColorTest","error checkColorTest!");
        }

        return curSelColorId;
    }

    public ColorData getColorData(int curSelColorId){
        // 해당 curSelColorId을 통해서 해당 Color를 찾아서 그 Color의 emotionid와 score를 저장해야한다.
        int colorDataNum = mColorDataList.size();
        for(int j = 0 ; j < colorDataNum; j++){
            ColorData entity = mColorDataList.get(j);
            if( entity.id == curSelColorId){
                return entity;
            }
        }

        return null;
    }

    // function 8. 카데고리 Data알려줌.
    public CategoryData getCategoryData(String curSelCategoryId){
        // 해당 curSelColorId을 통해서 해당 Color를 찾아서 그 Color의 emotionid와 score를 저장해야한다.
        int DataNum = mCategoryDataList.size();
        for(int j = 0 ; j < DataNum; j++){
            CategoryData entity = mCategoryDataList.get(j);
            if( entity.id.equals(curSelCategoryId)){
                return entity;
            }
        }
        return null;
    }

    // CategoryData를 charid와 genre를 통해서 Data를 알려줌.
    public CategoryData getCategoryDataCharExt(String charid, String genre){
        // 해당 curSelColorId을 통해서 해당 Color를 찾아서 그 Color의 emotionid와 score를 저장해야한다.
        int DataNum = mCategoryDataList.size();
        for(int j = 0 ; j < DataNum; j++){
            CategoryData entity = mCategoryDataList.get(j);
            if( entity.charid.equals(charid) && entity.genre.equals(genre)){
                return entity;
            }
        }
        return null;
    }

    // CategoryData를 emotionid와 genre를 통해서 Data를 알려줌.
    public CategoryData getCategoryDataExt(String emotionid, String genre){
        // 해당 curSelColorId을 통해서 해당 Color를 찾아서 그 Color의 emotionid와 score를 저장해야한다.
        int DataNum = mCategoryDataList.size();
        for(int j = 0 ; j < DataNum; j++){
            CategoryData entity = mCategoryDataList.get(j);
            if( entity.emotionid.equals(emotionid) && entity.genre.equals(genre)){
                return entity;
            }
        }
        return null;
    }

    // func contents를 얻기 위해서 사용. 그런데 문제는 부모일경우의 처리 후에 필요.
    public MeditationContents getMeditationContents(String contentid){
        int dataNum = mContentsList.size();
        for(int i = 0; i < dataNum; i++){
            if(mContentsList.get(i).uid.equals(contentid))
                return mContentsList.get(i);
        }
        return null;
    }

    // func contents를 얻기 위해서 사용. 그런데 문제는 부모일경우의 처리 후에 필요.  2021.01.22
    public MeditationContents getSocialContents(String contentid){
        int dataNum = mSocialContentsList.size();
        for(int i = 0; i < dataNum; i++){
            if(mSocialContentsList.get(i).uid.equals(contentid))
                return mSocialContentsList.get(i);
        }
        return null;
    }

    private XmlResourceParser parser;

    public ArrayList<VoiceAnalysisData> xmlVoiceAnalysisDataParser(int res_id,Resources res) {
        ArrayList<VoiceAnalysisData> arrayList = new ArrayList<VoiceAnalysisData>();
        parser = res.getXml(res_id);
        try {
            int eventType = parser.getEventType();
            VoiceAnalysisData data = null;

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String startTag = parser.getName();
                        if(startTag.equals("voiceanalysisdata")) {
                            data = new VoiceAnalysisData();
                        }
                        if(startTag.equals("no")) {
                            data.no = parser.nextText();
                        }
                        if(startTag.equals("voice1st")) {
                            data.voice1st = parser.nextText();
                        }
                        if(startTag.equals("voice2st")) {
                            data.voice2st = parser.nextText();
                        }
                        if(startTag.equals("emotionid")) {
                            data.emotionid = parser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String endTag = parser.getName();
                        if(endTag.equals("voiceanalysisdata")) {
                            arrayList.add(data);
                        }
                        break;
                }
                eventType = parser.next();
            }
        }catch(XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arrayList;
    }


    public ArrayList<CharListData> xmlCharListDataParser(int res_id,Resources res) {
        ArrayList<CharListData> arrayList = new ArrayList<CharListData>();
        parser = res.getXml(res_id);
        try {
            int eventType = parser.getEventType();
            CharListData data = null;

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String startTag = parser.getName();
                        if(startTag.equals("charlistdatadata")) {
                            data = new CharListData();
                        }
                        if(startTag.equals("character")) {
                            data.character = parser.nextText();
                        }
                        if(startTag.equals("charid")) {
                            data.charid = parser.nextText();
                        }
                        if(startTag.equals("genre")) {
                            data.genre = parser.nextText();
                        }
                        if(startTag.equals("chartag")) {
                            data.chartag = parser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String endTag = parser.getName();
                        if(endTag.equals("charlistdatadata")) {
                            arrayList.add(data);
                        }
                        break;
                }
                eventType = parser.next();
            }
        }catch(XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    public ArrayList<DetermineCharData> xmlDetermineCharDataParser(int res_id,Resources res) {
        ArrayList<DetermineCharData> arrayList = new ArrayList<DetermineCharData>();
        parser = res.getXml(res_id);
        try {
            int eventType = parser.getEventType();
            DetermineCharData data = null;

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String startTag = parser.getName();
                        if(startTag.equals("determinechardata")) {
                            data = new DetermineCharData();
                        }
                        if(startTag.equals("combinecharstring")) {
                            data.combinecharstring = parser.nextText();
                        }
                        if(startTag.equals("id")) {
                            data.id = parser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String endTag = parser.getName();
                        if(endTag.equals("determinechardata")) {
                            arrayList.add(data);
                        }
                        break;
                }
                eventType = parser.next();
            }
        }catch(XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    public ArrayList<CharData> xmlCharDataParser(int res_id,Resources res) {
        ArrayList<CharData> arrayList = new ArrayList<CharData>();
        parser = res.getXml(res_id);
        try {
            int eventType = parser.getEventType();
            CharData data = null;

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String startTag = parser.getName();
                        if(startTag.equals("chardata")) {
                            data = new CharData();
                        }
                        if(startTag.equals("id")) {
                            data.id = parser.nextText();
                        }
                        if(startTag.equals("charstring")) {
                            data.charstring = parser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String endTag = parser.getName();
                        if(endTag.equals("chardata")) {
                            arrayList.add(data);
                        }
                        break;
                }
                eventType = parser.next();
            }
        }catch(XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    public ArrayList<PersonResultData> xmlPersonResultDataParser(int res_id,Resources res) {
        ArrayList<PersonResultData> arrayList = new ArrayList<PersonResultData>();
        parser = res.getXml(res_id);
        try {
            int eventType = parser.getEventType();
            PersonResultData data = null;

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String startTag = parser.getName();
                        if(startTag.equals("resultdata")) {
                            data = new PersonResultData();
                        }
                        if(startTag.equals("id")) {
                            data.id = parser.nextText();
                        }
                        if(startTag.equals("animal")) {
                            data.animal = parser.nextText();
                        }
                        if(startTag.equals("miniimg")) {
                            data.miniimg = parser.nextText();
                        }
                        if(startTag.equals("img")) {
                            data.img = parser.nextText();
                        }
                        if(startTag.equals("title")) {
                            data.title = parser.nextText();
                        }
                        if(startTag.equals("desc")) {
                            data.desc = parser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String endTag = parser.getName();
                        if(endTag.equals("resultdata")) {
                            arrayList.add(data);
                        }
                        break;
                }
                eventType = parser.next();
            }
        }catch(XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    public ArrayList<VoiceData> xmlVoiceDataParser(int res_id,Resources res) {
        ArrayList<VoiceData> arrayList = new ArrayList<VoiceData>();
        parser = res.getXml(res_id);
        try {
            int eventType = parser.getEventType();
            VoiceData data = null;

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String startTag = parser.getName();
                        if(startTag.equals("voicedata")) {
                            data = new VoiceData();
                        }
                        if(startTag.equals("id")) {
                            data.id = parser.nextText();
                        }
                        if(startTag.equals("desc")) {
                            data.desc = parser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String endTag = parser.getName();
                        if(endTag.equals("voicedata")) {
                            arrayList.add(data);
                        }
                        break;
                }
                eventType = parser.next();
            }
        }catch(XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    public ArrayList<PersonQuestionData> xmlPersonQuestionDataParser(int res_id,Resources res) {
        ArrayList<PersonQuestionData> arrayList = new ArrayList<PersonQuestionData>();
        parser = res.getXml(res_id);
        try {
            int eventType = parser.getEventType();
            PersonQuestionData data = null;

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String startTag = parser.getName();
                        if(startTag.equals("personquestiondata")) {
                            data = new PersonQuestionData();
                        }
                        if(startTag.equals("id")) {
                            data.id = parser.nextText();
                        }
                        if(startTag.equals("page")) {
                            data.page = Integer.parseInt(parser.nextText());
                        }
                        if(startTag.equals("question")) {
                            data.question = parser.nextText();
                        }
                        if(startTag.equals("questionfile")) {
                            data.questionfile = parser.nextText();
                        }
                        if(startTag.equals("answer1_id")) {
                            data.answer1_id =  Integer.parseInt(parser.nextText());
                        }
                        if(startTag.equals("answer1_text")) {
                            data.answer1_text = parser.nextText();
                        }
                        if(startTag.equals("answer2_id")) {
                            data.answer2_id = Integer.parseInt(parser.nextText());
                        }
                        if(startTag.equals("answer2_text")) {
                            data.answer2_text = parser.nextText();
                        }
                        if(startTag.equals("answer3_id")) {
                            data.answer3_id = Integer.parseInt(parser.nextText());
                        }
                        if(startTag.equals("answer3_text")) {
                            data.answer3_text = parser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String endTag = parser.getName();
                        if(endTag.equals("personquestiondata")) {
                            arrayList.add(data);
                        }
                        break;
                }
                eventType = parser.next();
            }
        }catch(XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    // emotionlist data 3개를 parsing하는 함수
    public ArrayList<EmotionListData> xmlEmotionListDataParser(int res_id,Resources res) {
        ArrayList<EmotionListData> arrayList = new ArrayList<EmotionListData>();
        parser = res.getXml(res_id);
        try {
            int eventType = parser.getEventType();
            EmotionListData data = null;

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String startTag = parser.getName();
                        if(startTag.equals("emotiondata")) {
                            data = new EmotionListData();
                        }
                        if(startTag.equals("id")) {
                            data.id = parser.nextText();
                        }
                        if(startTag.equals("voiceemotion")) {
                            data.voiceemotion = parser.nextText();
                        }
                        if(startTag.equals("softemotion")) {
                            data.softemotion = parser.nextText();
                        }
                        if(startTag.equals("emotionid")) {
                            data.emotionid = parser.nextText();
                        }
                        if(startTag.equals("genre")) {
                            data.genre = parser.nextText();
                        }
                        if(startTag.equals("healingtag")) {
                            data.healingtag = parser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String endTag = parser.getName();
                        if(endTag.equals("emotiondata")) {
                            arrayList.add(data);
                        }
                        break;
                }
                eventType = parser.next();
            }
        }catch(XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    public ArrayList<CategoryData> xmlCategoryDataParser(int res_id,Resources res){
        ArrayList<CategoryData> arrayList = new ArrayList<CategoryData>();
        parser = res.getXml(res_id);
        try {
            int eventType = parser.getEventType();
            CategoryData data = null;

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String startTag = parser.getName();
                        if(startTag.equals("categorydata")) {
                            data = new CategoryData();
                        }
                        if(startTag.equals("id")) {
                            data.id = parser.nextText();
                        }
                        if(startTag.equals("name")) {
                            data.name = parser.nextText();
                        }
                        if(startTag.equals("priority")) {
                            data.priority = parser.nextText();
                        }
                        if(startTag.equals("maxnum")) {
                            data.maxnum = Integer.parseInt(parser.nextText());
                        }
                        if(startTag.equals("emotionid")) {
                            data.emotionid = parser.nextText();
                        }
                        if(startTag.equals("charid")) {
                            data.charid = parser.nextText();
                        }
                        if(startTag.equals("genre")) {
                            data.genre = parser.nextText();
                        }
                        if(startTag.equals("fix")) {
                            data.fix = parser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String endTag = parser.getName();
                        if(endTag.equals("categorydata")) {
                            arrayList.add(data);
                        }
                        break;
                }
                eventType = parser.next();
            }
        }catch(XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    public ArrayList<ColorData> xmlColorDataParser(int res_id,Resources res){
        ArrayList<ColorData> arrayList = new ArrayList<ColorData>();
        parser = res.getXml(res_id);
        try {
            int eventType = parser.getEventType();
            ColorData data = null;

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String startTag = parser.getName();
                        if(startTag.equals("colordata")) {
                            data = new ColorData();
                        }
                        if(startTag.equals("id")) {
                            data.id = Integer.parseInt(parser.nextText());
                        }
                        if(startTag.equals("colorsphere")) {
                            data.colorsphere = parser.nextText();
                        }
                        if(startTag.equals("emotion")) {
                            data.emotion = parser.nextText();
                        }
                        if(startTag.equals("emotionid")) {
                            data.emotionid = parser.nextText();
                        }
                        if(startTag.equals("score")) {
                            data.score = Integer.parseInt(parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String endTag = parser.getName();
                        if(endTag.equals("colordata")) {
                            arrayList.add(data);
                        }
                        break;
                }
                eventType = parser.next();
            }
        }catch(XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arrayList;
    }


    //private ArrayList<QuestionData> mQuestionDataList;
    private ArrayList<QuestionData> xmlQuestionDataParser(int res_id,Resources res)  {
        ArrayList<QuestionData> arrayList = new ArrayList<QuestionData>();
        parser = res.getXml(res_id);
        try {
            int eventType = parser.getEventType();
            QuestionData data = null;

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String startTag = parser.getName();
                        if(startTag.equals("colorquestiondata")) {
                            data = new QuestionData();
                        }
                        if(startTag.equals("id")) {
                            data.id = Integer.parseInt(parser.nextText());
                        }
                        if(startTag.equals("page")) {
                            data.page = Integer.parseInt(parser.nextText());
                        }
                        if(startTag.equals("question")) {
                            data.question = parser.nextText();
                        }
                        if(startTag.equals("answer1_id")) {
                            data.answer1_id = Integer.parseInt(parser.nextText());
                        }
                        if(startTag.equals("answer1_text")) {
                            data.answer1_text = parser.nextText();
                        }
                        if(startTag.equals("answer1_file")) {
                            data.answer1_file = parser.nextText();
                        }
                        if(startTag.equals("answer2_id")) {
                            data.answer2_id = Integer.parseInt(parser.nextText());
                        }
                        if(startTag.equals("answer2_text")) {
                            data.answer2_text = parser.nextText();
                        }
                        if(startTag.equals("answer2_file")) {
                            data.answer2_file = parser.nextText();
                        }
                        if(startTag.equals("answer3_id")) {
                            data.answer3_id = Integer.parseInt(parser.nextText());
                        }
                        if(startTag.equals("answer3_text")) {
                            data.answer3_text = parser.nextText();
                        }
                        if(startTag.equals("answer3_file")) {
                            data.answer3_file = parser.nextText();
                        }
                        if(startTag.equals("answer4_id")) {
                            data.answer4_id = Integer.parseInt(parser.nextText());
                        }
                        if(startTag.equals("answer4_text")) {
                            data.answer4_text = parser.nextText();
                        }
                        if(startTag.equals("answer4_file")) {
                            data.answer4_file = parser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String endTag = parser.getName();
                        if(endTag.equals("colorquestiondata")) {
                            arrayList.add(data);
                        }
                        break;
                }
                eventType = parser.next();
            }

        }catch(XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    public ArrayList<ResultData> xmlParser(int res_id, Resources res)  {

        ArrayList<ResultData> arrayList = new ArrayList<ResultData>();
        parser = res.getXml(res_id);

        try {
            int eventType = parser.getEventType();
            ResultData data = null;

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String startTag = parser.getName();
                        if(startTag.equals("resultdata")) {
                            data = new ResultData();
                        }
                        if(startTag.equals("id")) {
                            data.id = Integer.parseInt(parser.nextText());
                        }
                        if(startTag.equals("emotion")) {
                            data.emotion = parser.nextText();
                        }
                        if(startTag.equals("emotionimg")) {
                            data.emotionimg = parser.nextText();
                        }
                        if(startTag.equals("state")) {
                            data.state = parser.nextText();
                        }
                        if(startTag.equals("desc")) {
                            data.desc = parser.nextText();
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        String endTag = parser.getName();
                        if(endTag.equals("resultdata")) {
                            arrayList.add(data);
                        }
                        break;
                }
                eventType = parser.next();
            }

        }catch(XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        resultData_list = arrayList;

        return arrayList;
    }


    // 유저프로필 : 세션 카운트 1증가, 플레이 시간 업데이트
    public void Update_UserProfile_Play(int playtime, int count)
    {
        if(mUserProfile == null) return;

        mUserProfile.playtime += (playtime * count);
        mUserProfile.sessionnum += count;

        //String strText =  String.format("player time = %d초, session = %d", playtime, mUserProfile.sessionnum);
        //Toast.makeText(UtilAPI.GetActivity(),strText,Toast.LENGTH_LONG).show();

        // 2020.12.21 : 최근 리스트 추가.
        if(!mUserProfile.recentplaylist.contains(cur_contents.uid)){
            if(mUserProfile.recentplaylist.size() > maxRecentPlayListNum) {
                mUserProfile.recentplaylist.remove(0);
            }
            mUserProfile.recentplaylist.add(cur_contents.uid);
        }
    }

    // 2020.12.24 플레이어 타임과 세션 증가
    public void SendUserProFile_Play(int playtime, int count)
    {
        if(mUserProfile == null) return;

        Update_UserProfile_Play(playtime, count);

        setOnRecvValProfileListener(new OnRecvValProfileListener() {
            @Override
            public void onRecvValProfile(boolean validate) {
                if (validate == true) {
                } else {
                }
            }
        });

        sendValProfile(mUserProfile);
    }

    // local contents list update
    public void sendFavoriteLocalEvent(String uid, String contentid, int reactionCode) {
        MeditationContents localContents = this.getMeditationContents(contentid);
        if(localContents != null){
            if(localContents.states.containsKey(uid)){
                // 기존값이 있느데 같을 경우
                // 기존값이 있는데 다를 경우
                Integer prev = localContents.states.get(uid);
                if(prev.intValue() != reactionCode){
                    if(reactionCode == 1){
                        // 기존코드가 reactionCode 2
                        localContents.favoritecnt++;
                        localContents.hatecnt--;
                        localContents.states.put(uid,reactionCode);

                    }else if(reactionCode == 2){
                        // 기존코드가 reactionCode 1
                        localContents.favoritecnt--;
                        localContents.hatecnt++;
                        localContents.states.put(uid,reactionCode);
                    }
                }
            }else{
                // 기존값이 없을 경우
                if(reactionCode == 1){
                    localContents.favoritecnt++;
                    localContents.states.put(uid,reactionCode);
                }else if(reactionCode == 2){
                    // 기존코드가 reactionCode 1
                    localContents.hatecnt++;
                    localContents.states.put(uid,reactionCode);
                }

            }
        }
    }


    // 2020.12.21
    // 해당 감정이 긍정인지, 부정인지 판별
    public boolean isPositiveEmotion(String emotion){
        if(emotion.equals("Joy") || emotion.equals("Passion") || emotion.equals("Energy") || emotion.equals("Logical")){
            return true;
        }

        return false;
    }

    
    private final String JoyKRString = "기쁨";
    private final String PassionKRString = "열정";
    private final String EnergyKRString = "활력";
    private final String LogicalKRString = "논리적인";
    private final String StressKRString = "스트레스";
    private final String UpsetKRString = "당황";
    private final String EmbKRString = "불쾌";
    private final String AngerKRString = "화남";

    // 한글로 변환
    public String convertVoiceEmotionKRString(String voiceEmotion){
        String resultKRString = "";

        if(voiceEmotion.equals("Joy")){
            resultKRString = JoyKRString;
        }else if(voiceEmotion.equals("Passion")){
            resultKRString = PassionKRString;
        }else if(voiceEmotion.equals("Energy")){
            resultKRString = EnergyKRString;
        }else if(voiceEmotion.equals("Logical")){
            resultKRString = LogicalKRString;
        }else if(voiceEmotion.equals("Stress")){
            resultKRString = StressKRString;
        }else if(voiceEmotion.equals("Upset")){
            resultKRString = UpsetKRString;
        }else if(voiceEmotion.equals("Emb")){
            resultKRString = EmbKRString;
        }else if(voiceEmotion.equals("Anger")){
            resultKRString = AngerKRString;
        }else{
            Log.d("KRString","error convertVoiceEmotionKRString");
        }

        return resultKRString;
    }
    
    // 긍정 보이스 감정 찾기
    public String getVoicePostiveEmotion(String EmotionString){
        String defaultEmotionId = "0";
        int dataNum = mVoiceAnalysisDataList.size();
        VoiceAnalysisData entity = null;
        for(int i = 0; i < dataNum; i++){
            entity = mVoiceAnalysisDataList.get(i);
            if(entity.voice1st.equals(EmotionString)){
                return entity.emotionid;
            }
        }

        return defaultEmotionId;
    }

    // 부정 보이스 감정 찾기
    public String getVoiceNegativeEmotion(String EmotionString1, String EmotionString2){
        String defaultEmotionId = "0";
        int dataNum = mVoiceAnalysisDataList.size();
        VoiceAnalysisData entity = null;
        for(int i = 0; i < dataNum; i++){
            entity = mVoiceAnalysisDataList.get(i);
            if(entity.voice1st.equals(EmotionString1) && entity.voice2st.equals(EmotionString2)){
                return entity.emotionid;
            }
        }

        return defaultEmotionId;
    }


    // 보이스 검사 결과 처리해서 감정 ID 알려줌.
    public String checkVoiceEmotionTest(String resultval)
    {
        String voiceEmotionId = "0";

        JSONObject jsonObject2 = null;
        try {
            jsonObject2 = new JSONObject(resultval);
            JSONArray movieArray = jsonObject2.getJSONArray("TestData");
            JSONObject movieObject = movieArray.getJSONObject(0);
            String reString = movieObject.getString("Question");
            JSONArray moviesubArray2 = movieObject.getJSONArray("SEQ");
            int kValue = moviesubArray2.length();
            ArrayList<JSONObject> arrayJson = new ArrayList<JSONObject>();
            ArrayList<String> arrayJsonString = new ArrayList<String>();

            // Map을 하나 만들어서 6개의 감정에 대해서 차례대로 값을 더해주자.
            // Map<String,int>
            Map<String,Integer> CalcCharMap = new HashMap<String, Integer>();
            List<String> charEmotionDesign = Arrays.asList("Joy","Passion","Energy","Logical","Stress","Upset","Emb","Anger");
            List<String> charEmotion = Arrays.asList("Energy","Passion","Stress","Joy","Logical","Anger","Emb","Upset");

            if(moviesubArray2.length() != 0){
                for(int i=0; i < kValue; i++)
                {
                    String tempJson = moviesubArray2.getString(i);
                    String[] array = tempJson.split(";");

                    if(i == 0)  continue;;   // 0번쨰 줄은 해당 type 이름
                    //Energy;Passion;Stress;Joy;Logical;Anger;Emb.;Upset  index 3 ~ 10;
                    // strt index 3, end index 10까지
                    for(int k = 3; k <= 10; k++ ){
                        Integer eValue = CalcCharMap.get(charEmotion.get(k-3));
                        if(eValue != null){
                            eValue += Integer.parseInt(array[k]);
                            CalcCharMap.put(charEmotion.get(k-3),eValue);
                        }else{
                            CalcCharMap.put(charEmotion.get(k-3),Integer.parseInt(array[k]));
                        }
                    }
                    arrayJsonString.add(tempJson);
                }
            }else{
                // SEQ의 값이 아무것도 없을 경우의 예외처리 실패를 해야할지.... 실패므로 emotion0으로 보내자.
                Log.d("checkVoiceEmotionTest","not checkVoiceEmotionTest");
                return voiceEmotionId;
            }

            // 해당 Mapd에서 긍정에 대한 점수
            int charEmotionDesignNum = charEmotionDesign.size();
            int positiveEmotionScore = 0;
            int negativeEmotionScore = 0;
            int curEmotionScore = 0;

            Map<String,Integer> CalcCharPositiveMap = new HashMap<String, Integer>();
            Map<String,Integer> CalcCharNegativeMap = new HashMap<String, Integer>();

            String VoiceEmotionKRString = "";
            // 긍정과 부정 점수를 얻는다.
            for(int i = 0; i< charEmotionDesignNum; i++){
                curEmotionScore = CalcCharMap.get(charEmotionDesign.get(i));

                // 한글 보이스 감정으로 바꾼다.
                VoiceEmotionKRString = convertVoiceEmotionKRString(charEmotionDesign.get(i));

                if( i < 4){
                    CalcCharPositiveMap.put(VoiceEmotionKRString,curEmotionScore);
                    positiveEmotionScore += curEmotionScore;
                }else{
                    CalcCharNegativeMap.put(VoiceEmotionKRString,curEmotionScore);
                    negativeEmotionScore += curEmotionScore;
                }
            }

            // 긍정리스트 총합이 70%이상이어야 긍정이다.
            float determinePositiveRatio = 0.7f;
            boolean emotionPositive = false;
            float totoalEmotionScore = (float)(positiveEmotionScore + negativeEmotionScore);
            float positiveEmotionRatio = (float)positiveEmotionScore / totoalEmotionScore;
            if(positiveEmotionRatio > determinePositiveRatio){
                emotionPositive = true;
            }

            if(emotionPositive){
                // 긍정일때에는 1순위만 key값을 얻는다.
                // CalcCharPositiveMap을 정렬한다.
                Map<String,Integer> resultMap = sortMapByValue(CalcCharPositiveMap,true);

                List<String> keyList = new ArrayList<>(resultMap.keySet());
                List<Integer> valueList = new ArrayList<>(resultMap.values());

                if(keyList.size() == 0){
                    Log.d("checkVoiceEmotionTest","error Positive checkVoiceEmotionTest");
                }

                voiceEmotionId = getVoicePostiveEmotion(keyList.get(0));

            }else{
                // 부정일때에는 1순위 2순위 값을 같이 얻는다.
                Map<String,Integer> resultMap = sortMapByValue(CalcCharNegativeMap,true);

                List<String> keyList = new ArrayList<>(resultMap.keySet());
                List<Integer> valueList = new ArrayList<>(resultMap.values());

                if(keyList.size() == 0){
                    Log.d("checkVoiceEmotionTest","error Negative checkVoiceEmotionTest");
                }

                voiceEmotionId = getVoiceNegativeEmotion(keyList.get(0),keyList.get(1));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mUserProfile.emotiontype = Integer.parseInt(voiceEmotionId);

        return voiceEmotionId;
    }

    // 해당 answer에 맞는 chardata return
    public CharData getCharData(int curSelAnswerId){
        int charDataNum = mCharDataList.size();
        for(int j = 0 ; j < charDataNum; j++){
            CharData entity = mCharDataList.get(j);
            if( Integer.parseInt(entity.id) == curSelAnswerId){
                return entity;
            }
        }

        return null;
    }


    public String getPersonQuestionCurAnswerId(PersonQuestionData curQuestion, int curValue)
    {
        int curSelAnswerId  = 0;

        if(curValue == 1){
            curSelAnswerId = curQuestion.answer1_id;
        }else if(curValue == 2){
            curSelAnswerId = curQuestion.answer2_id;
        }else if(curValue == 3){
            curSelAnswerId = curQuestion.answer3_id;
        }else{
            Log.d("checkCharTest","error getPersonQuestionCurAnswerId!");
        }

        return Integer.toString(curSelAnswerId);
    }


    // 성격 검사 결과에 대한 id를 string으로 반환한다.
    public Map<String,Integer> charTestResult1st = new HashMap<>();
    public Map<String,Integer> charTestResult2st = new HashMap<>();
    public String checkCharTest(List<Integer> answerList){
        String personalityid = "0";

        // 1~8에 선택한 답변에 해당하는 id의 개수를 더한다.
        int questionNum = answerList.size();

        for(int i = 0; i < questionNum; i++){
            int curValue = answerList.get(i).intValue();
            PersonQuestionData curPersonQuestion = mPersonQuestionDataList.get(i);
            String curSelCharId = getPersonQuestionCurAnswerId(curPersonQuestion,curValue);
            if(!curSelCharId.equals("0")){
                int curScore = 1;
                if(i < 4){
                    if(charTestResult1st.containsKey(curSelCharId)){
                        int preScore = charTestResult1st.get(curSelCharId).intValue();
                        curScore += preScore;
                    }
                    charTestResult1st.put(curSelCharId,curScore);
                }else{
                    if(charTestResult2st.containsKey(curSelCharId)){
                        int preScore = charTestResult2st.get(curSelCharId).intValue();
                        curScore += preScore;
                    }
                    charTestResult2st.put(curSelCharId,curScore);
                }
            }
            else{
                Log.d("curSelCharId","error curData!");
                continue;
            }
        }

        // 1~4번 답, 5~8답에 대해서 가장 많은 글자 return
        Map<String,Integer> result1st = sortMapByValue(charTestResult1st,false);
        Map<String,Integer> result2st = sortMapByValue(charTestResult2st,false);

        String findcharstring1st =  "0";
        String findcharstring2st =  "0";
        String findfinalcharstring =  "0";
        String findfinalcharid =  "0";

        for (Map.Entry<String, Integer> entry : result1st.entrySet()) {
            findcharstring1st = entry.getKey();
        }

        for (Map.Entry<String, Integer> entry : result2st.entrySet()) {
            findcharstring2st = entry.getKey();
        }

        // 해당 답변 ID문자를 문자열로 바꾸어야 하다.
        CharData StringData = getCharData(Integer.parseInt(findcharstring1st));
        findcharstring1st = StringData.charstring;

        StringData= getCharData(Integer.parseInt(findcharstring2st));
        findcharstring2st = StringData.charstring;

        // 2개의 string을 하나로 합친다.
        findfinalcharstring = findcharstring1st + findcharstring2st;

        // 해당 최종 글자를 통해서 최종 성격 ID를 얻는다.
        int determinEntityNum = mDetermineCharDataList.size();
        for(int i = 0; i < determinEntityNum; i++){
            DetermineCharData entity = mDetermineCharDataList.get(i);
            if(entity.combinecharstring.equals(findfinalcharstring)){
                findfinalcharid = entity.id;
                break;
            }
        }

        personalityid = findfinalcharid;

        if(Integer.parseInt(personalityid) > 0) {
            mUserProfile.chartype = Integer.parseInt(personalityid);
        }

        return personalityid;
    }

    // 목소리 분석 function ->  2020.12.21
    int recordingState = 0;   // 0, init, 1 : start recored  2: stop record, 3: done record
    boolean playVoiceAnalysis = false;
    String resultval = "";
    Thread mVoiceAnalysisThraed = null;
    Thread mRecordThread = null;

    private int mAudioSource = MediaRecorder.AudioSource.MIC;
    private int mSampleRate = 8000;
    private int mChannelCount = AudioFormat.CHANNEL_IN_MONO;
    private int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int mBufferSize = AudioTrack.getMinBufferSize(mSampleRate, AudioFormat.CHANNEL_OUT_MONO, mAudioFormat);
    public String mFilePath = null;

    public void saveVoiceRecord() {
    {
           if(!isRecording && !bSaveRecording) {
               return;
           }

            int mAudioLen = fArrayList.size();
            int HEADER_SIZE = 44;
            byte[] header = new byte[HEADER_SIZE];
            int totalDataLen = mAudioLen + 36;
            long byteRate =  mSampleRate * 1 * 2; // Sample Rate * Channels * *bitDepth/8) -
            header[0] = 'R';  // RIFF/WAVE header
            header[1] = 'I';
            header[2] = 'F';
            header[3] = 'F';
            header[4] = (byte) (totalDataLen & 0xff);
            header[5] = (byte) ((totalDataLen >> 8) & 0xff);
            header[6] = (byte) ((totalDataLen >> 16) & 0xff);
            header[7] = (byte) ((totalDataLen >> 24) & 0xff);
            header[8] = 'W';
            header[9] = 'A';
            header[10] = 'V';
            header[11] = 'E';
            header[12] = 'f';  // 'fmt ' chunk
            header[13] = 'm';
            header[14] = 't';
            header[15] = ' ';
            header[16] = (byte)16;  // 4 bytes: size of 'fmt ' chunk
            header[17] = 0;
            header[18] = 0;
            header[19] = 0;
            header[20] = (byte)1;  // format = 1 (PCM방식)
            header[21] = 0;
            header[22] = (byte)1; // Mono
            header[23] = 0;
            header[24] = (byte) (mSampleRate & 0xff);
            header[25] = (byte) ((mSampleRate >> 8) & 0xff);
            header[26] = (byte) ((mSampleRate >> 16) & 0xff);
            header[27] = (byte) ((mSampleRate >> 24) & 0xff);
            header[28] = (byte) (byteRate & 0xff);
            header[29] = (byte) ((byteRate >> 8) & 0xff);
            header[30] = (byte) ((byteRate >> 16) & 0xff);
            header[31] = (byte) ((byteRate >> 24) & 0xff);
            header[32] = (byte) 2;  // block align : channel * (bitDepth / 8)
            header[33] = 0;
            header[34] = (byte)16;  // bits per sample
            header[35] = 0;
            header[36] = 'd';
            header[37] = 'a';
            header[38] = 't';
            header[39] = 'a';
            header[40] = (byte)(mAudioLen & 0xff);
            header[41] = (byte)((mAudioLen >> 8) & 0xff);
            header[42] = (byte)((mAudioLen >> 16) & 0xff);
            header[43] = (byte)((mAudioLen >> 24) & 0xff);
            fArrayList.addPreAll(header);
            //byte[] cbuffer = fArrayList.getContentsOrigin();
            //fos.write(cbuffer, 0, mAudioLen+44);
            //fos.close();

            if(this.mRecvDoneVoiceTestListener != null) {
                // 녹음된 음원파일을 저장한다.
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mRecvDoneVoiceTestListener.onRecvDoneVoiceTest(true);
                        } catch (Exception e) {
                            mRecvDoneVoiceTestListener.onRecvDoneVoiceTest(false);
                            e.printStackTrace();
                            throw e;
                        }
                    }
                }, 10);
            }
        }
    }


    public void callRecordThread() {

        mRecordThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] readData = new byte[mBufferSize];
                    //mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + saveFileName;
                    //fos = new FileOutputStream(mFilePath);
                    fArrayList = new ByteArrayList();

                    while (isRecording && !Thread.currentThread().isInterrupted()) {
                        mAudioRecord.read(readData, 0, mBufferSize);
                        fArrayList.addAll(readData);
                    }

                    mAudioRecord.stop();
                    mAudioRecord.release();
                    mAudioRecord = null;

                    saveVoiceRecord();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void callVoiceAnalysisThraed() {
        mVoiceAnalysisThraed = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (playVoiceAnalysis && !Thread.currentThread().isInterrupted()) {
                        //String subWaveFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + saveFileName;
                        //File f2 = new File(subWaveFilePath); // The location where you want your WAV file
                        //byte[] rawData = new byte[(int) f2.length()];
                        byte[] rawData = fArrayList.getContentsOrigin();

                        DataInputStream input = null;
                        String encoded;
                        URL urlCon = null;
                        urlCon = new URL(url);

                        InputStream is = null;
                        String result = "";

                        HttpURLConnection httpCon = null;

                        httpCon = (HttpURLConnection) urlCon.openConnection();

                        String json = "";
                        String base64WavString = "";

                        //input = new DataInputStream(new FileInputStream(subWaveFilePath));
                        //input.read(rawData);
                        base64WavString = android.util.Base64.encodeToString(rawData, android.util.Base64.NO_WRAP);

                        Log.d("meditation", "result base64WavString : " + base64WavString);

                        //if (input != null) {
                        //    try {
                        //        input.close();
                        //    } catch (IOException e) {
                        //        e.printStackTrace();
                        //    }
                        //}

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.accumulate("customId", "1000000000");
                        jsonObject.accumulate("AgeGroup", "30");
                        jsonObject.accumulate("Gender", "M");
                        jsonObject.accumulate("userId", "User1");
                        jsonObject.accumulate("Seq", "20201217121314001");

                        JSONObject jsonsubObject = new JSONObject();
                        jsonsubObject.accumulate("Topic", "Introduction");
                        jsonsubObject.accumulate("Question", "Q1");
                        jsonsubObject.accumulate("Reference", "Personality");
                        jsonsubObject.accumulate("URL", base64WavString);

                        JSONArray jsonArrayObject = new JSONArray();
                        jsonArrayObject.put(jsonsubObject);
                        jsonObject.accumulate("TestData", jsonArrayObject);

                        json = jsonObject.toString();

                        httpCon.setRequestProperty("Accept", "application/json");
                        httpCon.setRequestProperty("Content-type", "application/json");

                        // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
                        // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
                        httpCon.setDoOutput(true);
                        httpCon.setDoInput(true);
                        httpCon.setConnectTimeout(10000);

                        OutputStream os = null;
                        os = httpCon.getOutputStream();
                        os.write(json.getBytes("euc-kr"));
                        os.flush();

                        // 받는데이터 처리
                        is = httpCon.getInputStream();
                        if (is != null) {
                            StringBuffer sb = new StringBuffer();
                            byte[] b = new byte[4096];
                            for (int n; (n = is.read(b)) != -1; ) {
                                sb.append(new String(b, 0, n));
                            }
                            resultval = sb.toString();
                            Log.d("meditation", "result msg : " + resultval);

                            try {
                                JSONObject jsonObject2 = new JSONObject(resultval);
                                JSONArray movieArray = jsonObject2.getJSONArray("TestData");
                                JSONObject movieObject = movieArray.getJSONObject(0);
                                String reString = movieObject.getString("Question");
                                JSONArray moviesubArray2 = movieObject.getJSONArray("SEQ");
                                int kValue = moviesubArray2.length();
                                ArrayList<JSONObject> arrayJson = new ArrayList<JSONObject>();
                                ArrayList<String> arrayJsonString = new ArrayList<String>();

                                // Map을 하나 만들어서 6개의 감정에 대해서 차례대로 값을 더해주자.
                                // Map<String,int>
                                Map<String, Integer> CalcCharMap = new HashMap<String, Integer>();
                                List<String> charEmotion = Arrays.asList("Energy", "Passion", "Stress", "Joy", "Logical", "Anger", "Emb", "Upset");

                                for (int i = 0; i < kValue; i++) {
                                    String tempJson = moviesubArray2.getString(i);
                                    String[] array = tempJson.split(";");

                                    if (i == 0) continue;
                                    ;   // 0번쨰 줄은 해당 type 이름
                                    //Energy;Passion;Stress;Joy;Logical;Anger;Emb.;Upset  index 3 ~ 10;
                                    // strt index 3, end index 10까지
                                    for (int k = 3; k <= 10; k++) {
                                        Integer eValue = CalcCharMap.get(charEmotion.get(k - 3));
                                        if (eValue != null) {
                                            eValue += Integer.parseInt(array[k]);
                                            CalcCharMap.put(charEmotion.get(k - 3), eValue);
                                        } else {
                                            CalcCharMap.put(charEmotion.get(k - 3), Integer.parseInt(array[k]));
                                        }
                                    }
                                    arrayJsonString.add(tempJson);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            result = "Did not work!";
                        }

                        httpCon.disconnect();
                        ProcessVoiceAnalysis();
                        playVoiceAnalysis = false;
                        fArrayList = null;
                        Thread.sleep(1);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    ProcessVoiceAnalysis();
                    playVoiceAnalysis = false;
                    fArrayList = null;
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    ProcessVoiceAnalysis();
                    playVoiceAnalysis = false;
                    fArrayList = null;
                } finally {
                }
            }
        });




    }




    // 목소리 저장 시작
    public void StartVoiceTest(){
        recordingState = 1;
        // 녹음 시작 thread

        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, AudioFormat.CHANNEL_OUT_MONO, mAudioFormat, mBufferSize, AudioTrack.MODE_STREAM);

        if(mAudioRecord == null) {
            mAudioRecord =  new AudioRecord(mAudioSource, mSampleRate, mChannelCount, mAudioFormat, mBufferSize);
        }

        isRecording = true;
        bSaveRecording = false;
        mAudioRecord.startRecording();

        // 배경음이나. 음악을 모두 꺼야 한다. -> 2020.12.27

        callRecordThread();
        mRecordThread.start();

        HttpURLConnection httpCon = null;




    }

    // 목소리 저장 완료
    private OnRecvDoneVoiceTestListener mRecvDoneVoiceTestListener = null;
    public interface OnRecvDoneVoiceTestListener {
        void onRecvDoneVoiceTest(boolean validate);
    }
    public void setOnRecvDoneVoiceTestListener(OnRecvDoneVoiceTestListener listenfunc){
        mRecvDoneVoiceTestListener = listenfunc;
    }

    public void DoneVoiceTest(){
        isRecording = false;
        bSaveRecording = true;
        /*
        if(this.mRecvDoneVoiceTestListener != null) {
            // 녹음된 음원파일을 저장한다.
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        mRecvDoneVoiceTestListener.onRecvDoneVoiceTest(true);
                    } catch (Exception e) {
                        mRecvDoneVoiceTestListener.onRecvDoneVoiceTest(false);
                        e.printStackTrace();
                        throw e;
                    }
                }
            }, 4000);
        }
        */
    }

    // 목소리 저장 중간 종료
    public void StopVoiceTest(){
        // recording 도중에 오는곳, 완료후 오는곳
        isRecording = false;
    }

    // 목소리 분석 시작
    private OnRecvDoneVoiceAnalysisListener mRecvDoneVoiceAnalysisListener = null;
    public interface OnRecvDoneVoiceAnalysisListener {
        void onRecvDoneVoiceAnalysis(boolean validate);
    }
    public void setRecvDoneVoiceAnalysisListener(OnRecvDoneVoiceAnalysisListener listenfunc){
        mRecvDoneVoiceAnalysisListener = listenfunc;
    }
    public void StartVoiceAnalysis(){
        // Callback 완료되거나 오류 발생 처리 하고.
        playVoiceAnalysis = true;
        resultval = "";                 // Voice result String
        callVoiceAnalysisThraed();
        mVoiceAnalysisThraed.start();
    }

    public void ProcessVoiceAnalysis(){
        if(this.mRecvDoneVoiceAnalysisListener != null) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 목소리 분석 시간 및 성격 결정. 우선 테스트로
                        //mUserProfile.emotiontype = 1;
                        if(!checkVoiceEmotionTest(resultval).equals("0")){
                            // 녹음된 음원 파일을 서버에 던지고 받으면 그때 Callback함수 호출
                            mRecvDoneVoiceAnalysisListener.onRecvDoneVoiceAnalysis(true);
                        }else{
                            mRecvDoneVoiceAnalysisListener.onRecvDoneVoiceAnalysis(false);
                        }
                    } catch (Exception e) {
                        mRecvDoneVoiceAnalysisListener.onRecvDoneVoiceAnalysis(false);
                        e.printStackTrace();
                        throw e;
                    }
                }
            }, 10);

            /*
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 목소리 분석 시간 및 성격 결정. 우선 테스트로
                        mUserProfile.emotiontype = 1;

                        // 녹음된 음원 파일을 서버에 던지고 받으면 그때 Callback함수 호출
                        mRecvDoneVoiceAnalysisListener.onRecvDoneVoiceAnalysis(true);

                    } catch (Exception e) {
                        mRecvDoneVoiceAnalysisListener.onRecvDoneVoiceAnalysis(false);
                        e.printStackTrace();
                        throw e;
                    }
                }
            }, 7000);
             */
        }
    }

    public void StopVoiceAnalysis()
    {
        // 중간에 멈쳐야 할 경우 처리
        playVoiceAnalysis = false;
        mVoiceAnalysisThraed.interrupt();
    }


    //==========================================================================================================
    //  유저 콘텐츠 등록 : 음원파일명, 녹음,파일인지 여부, 생성날짜, 힐링Tag, 저자, 기존 콘텐츠 정보 이용. 콘텐츠 UID
    //  1. 파일을 먼저 Firebasestorage에 올리고 그 다음에 콘텐츠를 올려야 한다.
    //==========================================================================================================

    //   현재 알림데서 보았는지 안보았는지에 대한 처리를 해야 한다.

    //=============================================================================================
    // 콘텐츠 등록
    //=============================================================================================

    //=============================================================================================
    // 친구 검색 , 일반친구 신청, 감정친구 신청,  현재 요청중이라는 것이 나와야 하는데 이것은 어떻게???
    // 1. 특정 사람 검색  : 사람의 프로필 이미지도 보여주어야 한다. 따라서 Profile에 이미지 URL이 있어야 한다.
    // 2. 친구 List보여주기 : 친구 프로필 이미지도 보여주어야 한다.
    // 3. 일반 친구 신청
    // 4. 감정 친구 신청
    // 5. 친구 해제
    // 6. 친구 요청중이라는 것
    // 7. 친구 삭제
    // 8. 친구 요청 수락과 거절
    //=============================================================================================

    // 1.사람 검색
    //   사람의 NickName을 통해서 확인한다. 그리고 Profile정보도 확인해야 한다.
    //
    //   현재 감정상태는 UserProfile에 있어야 한다.
    //   감정고유친구인지, 일반친구인지 확인 , 아닌상태인지.
    //
    //   자신의 친구요청, 감정공유 요청 중 정보를 바탕으로 정보.
    //
    //   - 파이어베이스에서 해당 Name을 가진 사람들 리스트 검색한다. Profile정보를 받아온다.
    //   - 자신의 Name은 제거,
    //
    //   1) 자신이 친구요청한 리스트, 친구감정공유 리스트를 얻는다.
    //   2) 나의 친구 리스트 얻어야 한다.(UserProfile)

    //   1. Thumnail 이미지 올리기, 콘텐츠 정보 올리기 img만 그냥 올리자 콘텐츠는 jpg로 하자.
    //      콘텐츠 정보를 올려야 한다.




    // 요청 리스트를 얻는다. -> 요청리스트를 친구요청과 감정고유 요청을 둘로 나눈다. 아니면 하나로 처리
    // uid는 아는데 친구정보를 얻어야 한느데.....
    // https://cionman.tistory.com/72



    //==============================================================================
    // 요구사항 1 : 친구의 UID를 통해서 친구 UserProfile 정보를 얻는다. : 함수 필요.
    //==============================================================================
    // 1. 친구리스트를 먼저 언어야 한다. uid만 만들어 온다.
    // 자신의 정보를 이용해서 다른 정보들을 모두 가져온다. join
    //
    // 2. 친구 요청, 감정 친구 요청 리스틀 얻어야 한다.
    //
    // 3. 핵심은 해당 List의 실시간 여부처리
    //
    // 참고사항 : https://m.blog.naver.com/PostView.nhn?blogId=kkrdiamond77&logNo=221305647401&categoryNo=68&proxyReferer=https:%2F%2Fwww.google.com%2F
    //==============================================================================
    public void getFriendList() {
    }

    //public UserProfile OtherUserProfile = new UserProfile();

    private OnRecvOtherProfileListener mRecvOtherProfileListener = null;
    public interface OnRecvOtherProfileListener {
        void onRecvOtherProfileListener(boolean validate,UserProfile otherUser);
    }
    public void setOnRecvOtherProfileListener(OnRecvOtherProfileListener listenfunc){
        mRecvOtherProfileListener = listenfunc;
    }

    public void getOtherUserProfile(String uid) {
        mfbDBRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserProfile OtherUserProfile = (UserProfile)snapshot.getValue(UserProfile.class);
                    mRecvOtherProfileListener.onRecvOtherProfileListener(true,OtherUserProfile);
                }
                else{
                    // 없는 경우 데이터가 올라가지 않음, false이지만 errcode 0
                    mRecvOtherProfileListener.onRecvOtherProfileListener(false,null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error처리도 해야 한다.
                //mRecvContentsListener.onRecvContents(false);
                mRecvOtherProfileListener.onRecvOtherProfileListener(false,null);
            }
        });
    }

    //==============================================================================
    // 요구사항 2 : 친구 리스트 보여줄떄 자체 모델 하나 만들기
    //             1 : 감정진구 2: 감정요청중 3: 일반친구       -> 친구리스트
    //             11 : 친구추가 12 : 친구  13 : 친구요청중    -> 검색힌 사람 리스트
    //
    //             아래 6개 인터페이스 input UID
    //             1) 친구 추가 요청
    //             2) 친구 추가 취소
    //             3) 친구 삭제
    //             3) 감정 공유 요청
    //             4) 감정 공유 요청을 취소
    //             5) 감정 공유 친구 삭제
    //==============================================================================

    //======================================================================================
    // 요구사항 3
    //
    //  * 콘텐츠 생성
    //  1. 녹음 시작
    //    Thread mMyContentsRecordThread;
    MediaRecorder mMycContentsRecorder = null;
    String mMyContentsPath = null;
    boolean isMyContentsRecording = false;

//    public void callMyContentsRecordThread() {
//        mMyContentsRecordThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    mMycContentsRecorder.start();
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }


    private void initMyContentsRecord()
    {
        mMycContentsRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mMycContentsRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMycContentsRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMycContentsRecorder.setAudioEncodingBitRate(16*44100);
        mMycContentsRecorder.setAudioSamplingRate(44100);

        SimpleDateFormat format_date = new SimpleDateFormat ( "yyyyMMdd" );
        Date date_now = new Date(System.currentTimeMillis());
        String curdate = format_date.format(date_now);
        String curimgName = mUserProfile.uid + "_" + curdate + ".aac";

        mMyContentsPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+curimgName;

        Log.d("TAG", "file path is " + mMyContentsPath);
        mMycContentsRecorder.setOutputFile(mMyContentsPath);
        try {
            mMycContentsRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 녹음 시작
    public void startMyContentsRecord()
    {
        initMyContentsRecord();
        mMycContentsRecorder.start();
        isMyContentsRecording = true;
    }

    //  2. 녹음 완료
    public void doneMyContentsRecord()
    {
        mMycContentsRecorder.stop();
        mMycContentsRecorder.release();
        mMycContentsRecorder = null;
        isMyContentsRecording = false;
    }

    //  3. 녹음 취소 후 반드시 delMyContentsRecordFile을 통해서 파일 삭제 해야 한다.
    public void cancelMyContensRecord()
    {
        mMycContentsRecorder.stop();
        mMycContentsRecorder.release();
        mMycContentsRecorder = null;
        isMyContentsRecording = false;
    }

    // 4.  반드시 파일은 지워주어야 한다.
    public boolean delMyContentsRecordFile()
    {
        File delFile = new File(mMyContentsPath);
        if(delFile.exists()){
            delFile.delete();
            mMyContentsPath = "";
            return true;
        }

        mMyContentsPath = "";
        return false;
    }


    //  5. 콘텐츠 올리기 function : sendValMeditationContents
    private OnRecvValMeditationContentsListener mOnRecvValMeditationContentsListener  = null;
    public interface OnRecvValMeditationContentsListener {
        void onRecvValMeditationContentsListener (boolean validate, MeditationContents successContents);
    }
    public void setOnRecvValMeditationContentsListener (OnRecvValMeditationContentsListener listenfunc){
        mOnRecvValMeditationContentsListener = listenfunc;
    }

    private boolean doneUploadContentsThumnailImg = false;
    private boolean doneUploadContentsSnd = false;

    private void notifyDoneUpload(MeditationContents infoData,int successtype, Map<String, Object> updateMap,boolean newContents){
        if(successtype == 1){
            doneUploadContentsThumnailImg = true;
        }else {
            doneUploadContentsSnd = true;
        }

        // 완료되었을때의 처리
        if(doneUploadContentsThumnailImg && doneUploadContentsSnd){
            if(newContents){
                mfbDBRef.child(socialContentsInfoString).child(infoData.uid).updateChildren(updateMap)
                        .addOnCompleteListener(task ->
                                Log.d("TAG", "update infoData.uid : " + task.isSuccessful())
                );
            }else{
                mfbDBRef.child(socialContentsInfoString).child(infoData.uid).setValue(infoData);
            }

            mOnRecvValMeditationContentsListener.onRecvValMeditationContentsListener(true, infoData);

            doneUploadContentsThumnailImg = false;
            doneUploadContentsSnd = false;
        }
    }

    //  중복이 안되어 있으면 해당 NickName은 허용 가능하므로 true, 이미 있으면 false를 반환, 저자는 userpofile의 nickname
    //  backgrroundImgName 파일 확장자까지 넣어주세요. fullpath
    //  해당 내용이 null이면 관련 처리 안하고 업데이트
    //
    //  thumnailImgName 은 fullpath가 들어와야 File을 접근해서 처리 가능
    //  SndFileName 도 fullppath가 들어와야 File을 접근해서 처리 가능
    //
    //  없는 것은 -1, null로 처리
    //
    //  성공하면 Profile에 자신이 만든 콘텐츠를 업데이트 해 주어야 한다.  UID는 시간을 반드시 처리해서 넣어주어야 한다.
    //  성공이 유저의 정보에서 playerlist도 업데이트해야 한다.
    //
    //  emotion에 따라서 healing Tag을 업데이트 해야 한다.
    public void sendValSocialMeditationContents(MeditationContents socialcontents, String titleName, String thumnailImgName,String playtime, int IsSndFile,String SndFileName, String releasedate, String backgrroundImgName,String genre,String emotion){
        if(this.mOnRecvValMeditationContentsListener != null){
            SimpleDateFormat format_date = new SimpleDateFormat ( "yyyyMMdd" );
            Date date_now = new Date(System.currentTimeMillis());
            String curdate = format_date.format(date_now);

            SimpleDateFormat format_detail_date =new SimpleDateFormat("yyyyMMddHHmmss");
            Date date_detail_now = new Date(System.currentTimeMillis());
            String curdetialdate = format_date.format(date_detail_now);

            boolean newContents = false;
            doneUploadContentsThumnailImg = false;
            doneUploadContentsSnd = false;
            MeditationContents infoData = null;

            // updateMap
            Map<String, Object> updateMap = new HashMap<>();

            if(socialcontents != null){
                infoData = socialcontents;  // UID가 문제
            }else{
                infoData = new MeditationContents();
                infoData.ismycontents = 1;
                infoData.author = mUserProfile.nickname;
                infoData.uid = curdetialdate+mUserProfile.uid;  // uid를 키를 해야 한다.
                socialcontents.authoruid = mUserProfile.uid;
                newContents = true;
            }

            // title 처리 
            if(titleName != null){
                infoData.title = titleName;
                updateMap.put("title", titleName);
            }
            
            // backgrroundImgName 처리
            if(backgrroundImgName !=null){
                infoData.bgimg = backgrroundImgName;
                updateMap.put("bgimg", backgrroundImgName);
            }

            // 저장된 사운드 파일인지 확인
            if(IsSndFile != -1){
                infoData.isRecordSndFile = IsSndFile;
                updateMap.put("isRecordSndFile", IsSndFile);
            }

            // 장르 처리
            if(genre != null){
                infoData.genre = genre;
                updateMap.put("genre", genre);
            }

            if(emotion != null){
                infoData.emotion = emotion;
                updateMap.put("emotion", emotion);

               //=======================================================
               // 해당 감정에 따른 healing Tag를 바꿔야 한다.
               //=======================================================
            }

            // 현재 올린 Date를 기준으로 갱신한다.
            if(releasedate != null) {
                infoData.releasedate = releasedate;
                updateMap.put("releasedate", releasedate);
            }

            if(playtime != null){
                infoData.playtime = playtime;
                updateMap.put("playtime", playtime);
            }


            // thumnail 처리
            if(thumnailImgName != null){
                MeditationContents finalInfoData = infoData;
                boolean finalNewContents = newContents;

                // 1. 기존 Thumnail을 지워야 한다.
                StorageReference delStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(mycontentsthumnaildir).child(infoData.thumbnail);

                // Delete the file
                delStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        File upfile = new File(thumnailImgName);
                        Uri thumbnailImgUri = Uri.fromFile(upfile);
                        String curimgName = mUserProfile.uid + "_" + curdate + "_" + thumbnailImgUri.getLastPathSegment(); // uploadimageName

                        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(mycontentsthumnaildir).child(curimgName);
                        UploadTask task = storageRef.putFile(thumbnailImgUri);

                        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                finalInfoData.thumbnail = curimgName;
                                notifyDoneUpload(finalInfoData,1,updateMap, finalNewContents);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mOnRecvValMeditationContentsListener.onRecvValMeditationContentsListener(false,null);
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                                        double progress = (100 * snapshot.getBytesTransferred()) /  snapshot.getTotalByteCount();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                        mOnRecvValMeditationContentsListener.onRecvValMeditationContentsListener(false, null);
                    }
                });
            }else{
                // 이미 있기 때문에 success를 보낸다.
                notifyDoneUpload(infoData,1,updateMap,newContents);
            }


            if(SndFileName != null){
                // 기존의 사운드 파일 삭제
                StorageReference delStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(mycontentsaudiodir).child(infoData.audio);
                MeditationContents finalInfoData1 = infoData;
                boolean finalNewContents1 = newContents;

                // Delete the file
                delStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        File upSoundfile = new File(SndFileName);
                        Uri SoundUri = Uri.fromFile(upSoundfile);
                        String curSndName = mUserProfile.uid + "_" + curdate + "_" + SoundUri.getLastPathSegment();

                        StorageReference sndStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(mycontentsaudiodir).child(curSndName);
                        UploadTask sndTask = sndStorageRef.putFile(SoundUri);
                        sndTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                finalInfoData1.audio = curSndName;
                                notifyDoneUpload(finalInfoData1,2,updateMap, finalNewContents1);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mOnRecvValMeditationContentsListener.onRecvValMeditationContentsListener(false,null);
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                                        double progress = (100 * snapshot.getBytesTransferred()) /  snapshot.getTotalByteCount();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                        mOnRecvValMeditationContentsListener.onRecvValMeditationContentsListener(false,null);
                    }
                });
            }else{
                notifyDoneUpload(infoData,2,updateMap, newContents);
            }
        }
    }

    //===================================================================================================
    // 요구사항 4
    // 1. 내가 만든 콘텐츠 삭제하고 UserProfile을 받아서 처리 (ContentsUID만 input에서 처리)
    //    내가 재생한 콘텐츠 지워진 콘텐츠가 있을수 도 있다. 이것에 대한 처리 지워진것은 소셜 콘텐츠에 대해서 예외처리 할 수 밖에 없다.
    //    UserProfile의 자기 Player에서도 지워야 한다.
    //===================================================================================================
    private OnDelMyContentsListener mDelMyContentsListener = null;
    public interface OnDelMyContentsListener {
        void onDelMyContentsListener(boolean validate);
    }
    public void setOnDelMyContentsListener(OnDelMyContentsListener listenfunc){
        mDelMyContentsListener = listenfunc;
    }

    public void delMyContents(MeditationContents delMyContents){

        // 1. 기존 Thumnail을 지워야 한다.
        StorageReference delStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(mycontentsthumnaildir).child(delMyContents.thumbnail);

        // Delete the file
        delStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });

        // 2. 기존 Sound File을 지운다.
        StorageReference delAudioStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(mycontentsaudiodir).child(delMyContents.audio);

        // Delete the file
        delAudioStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });

        // 3. 기존 Contents를 지운다. -> 내가 재생한 콘텐츠 지워진 콘텐츠가 있을수 도 있다. 이것에 대한 처리 지워진것은 소셜 콘텐츠에 대해서 예외처리 할 수 밖에 없다.
        mfbDBRef.child(socialContentsInfoString).child(delMyContents.uid).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDelMyContentsListener.onDelMyContentsListener(true);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mDelMyContentsListener.onDelMyContentsListener(false);
            }
        });;
    }

    // local social Contents도 지워야 한다.
    public void delLocalMyContents(String uid){
        mSocialContentsList.remove(getSocialContents(uid));
    }

    // playerlist의 social Contents도 지워야 한다. 그리고 UserProfile을 업데이트 해야 한다.
    public void delUserProfileMyContents(String uid){
        mUserProfile.mycontentslist.remove(uid);
        sendValProfile(mUserProfile);
    }

    //===================================================================================================
    // 요구사항 5
    // 2. 소셜 콘텐츠 신고하기 인터페이스 추가. (ContentsUID받아서 처리 : isReported)
    //===================================================================================================
    public void reportedSocialContents(MeditationContents reportedSocialContents){
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("isReported", 1);
        mfbDBRef.child(socialContentsInfoString).child(reportedSocialContents.uid).updateChildren(updateMap)
                .addOnCompleteListener(task -> Log.d("TAG", "update reportedSocialContents : " + task.isSuccessful()));

    }

    //===================================================================================================
    // 요구사항 6 . 알림
    // type : 1 : 간단 알람  2 : 수락,거절 알람
    //
    //  1.  친구 신청 수락
    //  2.  친구 신청 거절 (상대방이 거절)
    //  3.  감정 공유 수락
    //  4.  감정 공유 거절
    //
    //  1 . 친구 신청 시청
    //  2.  감정 공유 신청
    //===================================================================================================








    //=========================================================================================================
    // 요구사항 7. 인앱 : 참고 https://it-highjune.tistory.com/4
    //  1 12개월 시작, 1개월 인앱 요구 function
    //=========================================================================================================
    public void purchaseBilling(String productuid){

    }

    //  2.현재 유저가 유료 사용자인지 구글에 검증해야 함. 그리고 판단 function (로그인)
    public boolean checkBilling(){
        return true;
    }
}


