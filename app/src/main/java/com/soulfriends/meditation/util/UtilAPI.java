package com.soulfriends.meditation.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.soulfriends.meditation.R;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.view.BaseActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.soulfriends.meditation.model.MeditationContents;
import com.soulfriends.meditation.view.ContentsEditActivity;
import com.soulfriends.meditation.view.ContentsEmotionSelActivity;
import com.soulfriends.meditation.view.ContentsMakeActivity;
import com.soulfriends.meditation.view.ContentsUploadActivity;
import com.soulfriends.meditation.view.ContentsinfoActivity;
import com.soulfriends.meditation.view.FriendEditActivity;
import com.soulfriends.meditation.view.FriendFindActivity;
import com.soulfriends.meditation.view.InAppActivity;
import com.soulfriends.meditation.view.IntroActivity;
import com.soulfriends.meditation.view.LoadingActivity;
import com.soulfriends.meditation.view.LoginActivity;
import com.soulfriends.meditation.view.MainActivity;
import com.soulfriends.meditation.view.MyContentsActivity;
import com.soulfriends.meditation.view.NetDialogActivity;
import com.soulfriends.meditation.view.NotiActivity;
import com.soulfriends.meditation.view.PlayerActivity;
import com.soulfriends.meditation.view.ProfileActivity;
import com.soulfriends.meditation.view.ProfileFriendActivity;
import com.soulfriends.meditation.view.PsychologyCharacterDetailActivity;
import com.soulfriends.meditation.view.PsychologyCharacterListActivity;
import com.soulfriends.meditation.view.PsychologyCharacterResultActivity;
import com.soulfriends.meditation.view.PsychologyCharacterTestActivity;
import com.soulfriends.meditation.view.PsychologyColorTestActivity;
import com.soulfriends.meditation.view.PsychologyDetailActivity;
import com.soulfriends.meditation.view.PsychologyFeelingTestActivity;
import com.soulfriends.meditation.view.PsychologyListActivity;
import com.soulfriends.meditation.view.PsychologyResultActivity;
import com.soulfriends.meditation.view.PsychologyVoiceTestActivity;
import com.soulfriends.meditation.view.SessioinActivity;
import com.soulfriends.meditation.view.SettingActivity;
import com.soulfriends.meditation.view.TimerActivity;
import com.soulfriends.meditation.view.UserinfoActivity;
import com.soulfriends.meditation.view.UserinfoExtActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


public class UtilAPI {

    public static long button_delay_time = 400;

    public static boolean s_bEvent_service = false;
    public static boolean s_bEvent_service_main = false;
    public static boolean s_bEvent_service_player = false;

    public static boolean s_bEvent_service_player_stop = false;
    public static boolean s_bEvent_service_player_timer_stop = false;

    // 백그라운드 이미지 아이디
    public static int s_id_backimamge_makecontents = -1;

    public static final String FRAGMENT_HOME = "HomeFragment";
    public static final String FRAGMENT_SLEEP = "SleepFragment";
    public static final String FRAGMENT_MEDITATION = "MeditationFragment";
    public static final String FRAGMENT_MUSIC = "MusicFragment";
    public static final String FRAGMENT_PROFILE = "ProfileFragment";

    public static String s_StrMainFragment = FRAGMENT_HOME;


    // 소셜 -> 콘테츠 와 친구 탭
    public static final String TAB_CONTENTS = "Tab_Contents";
    public static final String TAB_FRIEND = "Tab_Friend";
    public static String s_StrFragment_Profile_Tab = TAB_CONTENTS;




    public static boolean s_bShowTimerPopup = false;

    public static int s_psychology_state = -1;

    private static BaseActivity s_activityFocus; // 현재
    public static void SetActivityFocus(BaseActivity activity) {
        s_activityFocus = activity;
    }
    public static BaseActivity GetActivityFocus() {
        return s_activityFocus;
    }

    private static Activity s_activity; // IntroActivity 임.
    public static void SetActivity(Activity activity) {
        s_activity = activity;
    }
    public static Activity GetActivity() {
        return s_activity;
    }

    private static ArrayList<Activity>  s_activityInPlayerList = new ArrayList<>(); // player activity 속한것들
    public static void AddActivityInPlayer(Activity activity) {
        if(!s_activityInPlayerList.contains(activity))
        {
            s_activityInPlayerList.add(activity);
        }
    }

    public static void RemoveActivityInPlayer(Activity activity) {
        if(s_activityInPlayerList.size() > 0) {
            s_activityInPlayerList.remove(activity);
        }
    }

    public static ArrayList<Activity> GetActivityInPlayerList() {
        return s_activityInPlayerList;
    }

    public static void ClearActivityInPlayerList() {
        s_activityInPlayerList.clear();
    }

    //
    public static MeditationContents s_MeditationContents_temp;

    //

    private static ArrayList<Activity>  s_activity_TempList = new ArrayList<>();
    public static void AddActivity_Temp(Activity activity) {
        if(!s_activity_TempList.contains(activity))
        {
            s_activity_TempList.add(activity);
        }
    }

    public static void RemoveActivity_Temp(Activity activity) {
        if(s_activity_TempList.size() > 0) {
            s_activity_TempList.remove(activity);
        }
    }

    public static void AllFinishActivity_Temp()
    {
        int size = s_activity_TempList.size();
        for(int i = 0; i < s_activity_TempList.size(); i++)
        {
            s_activity_TempList.get(i).finish();
        }
    }


    public static UserProfile s_userProfile_friend;

    public static void ClearActivity_Temp() {
        s_activity_TempList.clear();
    }


    public enum PlayerMode
    {
        base,
        my,
        friend,
    }

    public static PlayerMode s_playerMode = PlayerMode.base;

    public static void SetFullScreen(android.view.Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController controller = window.getInsetsController();

            if (controller != null)
                controller.hide(WindowInsets.Type.statusBars());
        } else {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        }
    }

    public static void load_imageEX(Context context, String str_uri, ImageView view, MeditationContents meditationContents)
    {
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(str_uri);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

            @Override
            public void onSuccess(Uri uri) {

                meditationContents.thumbnail_uri = uri.toString();
                showImageEX(context, uri, view);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                Log.d("Meditation","load_imageEX onFailure : " + str_uri);
                int xx = 0;
            }
        });
    }

    public static void showImageEX(Context context, Uri uri, ImageView view) {
        //statesList.put(uri.toString(),uri.toString());
        Activity activity = (Activity) context;
        if (activity.isFinishing())
            return;
        Glide.with(view.getContext()).load(uri).
                placeholder(R.drawable.default_card_02).
                transition(withCrossFade()).
                diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(view);
    }

    public static void load_image(Context context, String str_uri, ImageView view)
    {
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(str_uri);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                showImage(context, uri, view);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }

    //static Map<String, String> statesList = new HashMap<>();

    public static void showImage(Context context, Uri uri, ImageView view) {
        //statesList.put(uri.toString(),uri.toString());
        Activity activity = (Activity) context;
        if (activity.isFinishing())
            return;
        Glide.with(view.getContext()).load(uri).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(view);
    }


    public static void load_image_circle(Context context, String str_uri, de.hdodenhof.circleimageview.CircleImageView view)
    {
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(str_uri);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                int xxx = 0;
                showImage_circle(context, uri, view);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                int xx = 0;
            }
        });
    }

    //static Map<String, String> statesList = new HashMap<>();

    public static void showImage_circle(Context context, Uri uri, de.hdodenhof.circleimageview.CircleImageView view)
    {
        //statesList.put(uri.toString(),uri.toString());
        Activity activity = (Activity) context;
        if (activity.isFinishing())
            return;
        Glide.with(view.getContext()).load(uri).
                placeholder(R.drawable.register_profile_img).
                transition(withCrossFade()).
                diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(view);
    }

    public static void setImage(Context context, ImageView view, int res_id)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(ContextCompat.getDrawable(context, res_id));
        } else {
            view.setBackgroundDrawable(ContextCompat.getDrawable(context, res_id));
        }
    }

    public static void setImageResource(ImageView view, int res_id)
    {
        view.setImageResource(res_id);
    }

    public static void setButtonBackground(Context context, Button button, int res_id)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            button.setBackground(ContextCompat.getDrawable(context, res_id));
        } else {
            button.setBackgroundDrawable(ContextCompat.getDrawable(context, res_id));
        }
    }

    public static long GetDay_Date(String date)
    {
        SimpleDateFormat format_date = new SimpleDateFormat ( "yyyy-MM-dd" );

        long milliseconds_release = 0;
        try {
            Date ReleaseDate = format_date.parse(date);
            milliseconds_release = ReleaseDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date date_now = new Date(System.currentTimeMillis());
        long milliseconds_cur = date_now.getTime();
        long gap_time = milliseconds_cur - milliseconds_release;

        long day = gap_time / (60 * 60 * 24 * 1000);

        return day;
    }

    public static void setMarginBottom(Context context, View v, int bottom) {

        int dp_bottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bottom, context.getResources().getDisplayMetrics());

        ViewGroup.MarginLayoutParams params =
                (ViewGroup.MarginLayoutParams)v.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin,
                params.rightMargin, dp_bottom);
    }

    public static Activity scanForActivity(Context context) {
        if (context == null)
            return null;
        else if (context instanceof Activity)
            return (Activity) context;
        else if (context instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) context).getBaseContext());
        return null;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }


    public static int isConnected(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni != null && ni.isConnected())
        {
            if(ni.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                // 3g, 4g 인 경우
                return 1;
            }
            else if(ni.getType() == ConnectivityManager.TYPE_WIFI)
            {
                // wifi 인 경우
                return 2;
            }
        }
        // 연결 안됨
        return 0;
    }


    //-----------------------------------------------------------------------------
    //
    // 인터넷 연결 상태
    //
    //-----------------------------------------------------------------------------
    public static boolean s_bShowNetConnection = false;
    public static String s_ActivityName_NetConnection = "";
    private static Activity s_activity_NetConnection;

    public static void SetNetConnection_Activity(Activity activity)
    {
        s_activity_NetConnection = activity;

        s_ActivityName_NetConnection = activity.getClass().getSimpleName();
    }

    public static void showNetConnectionDlg() {

        if (!s_bShowNetConnection) {
            Intent intent = new Intent(GetActivity(), NetDialogActivity.class);
            GetActivity().startActivity(intent);
            s_bShowNetConnection = true;
        }
    }

    public static void startActivity_NetConnection( Activity activity)
    {
        // 이전 화면 finish
        if(s_activity_NetConnection != null)
        {
            if(s_ActivityName_NetConnection.equals("IntroActivity"))
            {
            }
            else {
                s_activity_NetConnection.finish();
            }
        }

        // new 화면
        //Activity activity = GetActivity();
        if(s_ActivityName_NetConnection.length() > 0) {
            switch (s_ActivityName_NetConnection) {
                case "ContentsEditActivity": {
                    activity.startActivity(new Intent(activity, ContentsEditActivity.class));
                }
                break;
                case "ContentsEmotionSelActivity": {
                    activity.startActivity(new Intent(activity, ContentsEmotionSelActivity.class));
                }
                break;
                case "ContentsinfoActivity": {
                    activity.startActivity(new Intent(activity, ContentsinfoActivity.class));
                }
                break;
                case "ContentsMakeActivity": {
                    activity.startActivity(new Intent(activity, ContentsMakeActivity.class));
                }
                break;
                case "ContentsUploadActivity": {
                    activity.startActivity(new Intent(activity, ContentsUploadActivity.class));
                }
                break;
                case "FriendEditActivity": {
                    activity.startActivity(new Intent(activity, FriendEditActivity.class));
                }
                break;
                case "FriendFindActivity": {
                    activity.startActivity(new Intent(activity, FriendFindActivity.class));
                }
                break;
                case "InAppActivity": {
                    activity.startActivity(new Intent(activity, InAppActivity.class));
                }
                break;
                case "IntroActivity": {

                    ((IntroActivity)s_activity_NetConnection).Refresh();
                    //activity.startActivity(new Intent(activity, IntroActivity.class));
                }
                break;
                case "LoadingActivity": {
                    activity.startActivity(new Intent(activity, LoadingActivity.class));
                }
                break;
                case "LoginActivity": {
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                }
                break;
                case "MainActivity": {
                    activity.startActivity(new Intent(activity, MainActivity.class));
                }
                break;
                case "MyContentsActivity": {
                    activity.startActivity(new Intent(activity, MyContentsActivity.class));
                }
                break;
                case "NotiActivity": {
                    activity.startActivity(new Intent(activity, NotiActivity.class));
                }
                break;
                case "PlayerActivity": {
                    activity.startActivity(new Intent(activity, PlayerActivity.class));
                }
                break;
                case "ProfileActivity": {
                    activity.startActivity(new Intent(activity, ProfileActivity.class));
                }
                break;
                case "ProfileFriendActivity": {
                    activity.startActivity(new Intent(activity, ProfileFriendActivity.class));
                }
                break;
                case "PsychologyCharacterDetailActivity": {
                    activity.startActivity(new Intent(activity, PsychologyCharacterDetailActivity.class));
                }
                break;
                case "PsychologyCharacterResultActivity": {
                    activity.startActivity(new Intent(activity, PsychologyCharacterResultActivity.class));
                }
                break;
                case "PsychologyCharacterTestActivity": {
                    activity.startActivity(new Intent(activity, PsychologyCharacterTestActivity.class));
                }
                break;
                case "PsychologyColorTestActivity": {
                    activity.startActivity(new Intent(activity, PsychologyColorTestActivity.class));
                }
                break;
                case "PsychologyDetailActivity": {
                    activity.startActivity(new Intent(activity, PsychologyDetailActivity.class));
                }
                break;
                case "PsychologyFeelingTestActivity": {
                    activity.startActivity(new Intent(activity, PsychologyFeelingTestActivity.class));
                }
                break;
                case "PsychologyListActivity": {
                    activity.startActivity(new Intent(activity, PsychologyListActivity.class));
                }
                break;
                case "PsychologyVoiceTestActivity": {
                    activity.startActivity(new Intent(activity, PsychologyVoiceTestActivity.class));
                }
                break;
                case "SessioinActivity": {
                    activity.startActivity(new Intent(activity, SessioinActivity.class));
                }
                break;
                case "SettingActivity": {
                    activity.startActivity(new Intent(activity, SettingActivity.class));
                }
                break;
                case "TimerActivity": {
                    activity.startActivity(new Intent(activity, TimerActivity.class));
                }
                break;
                case "UserinfoActivity": {
                    activity.startActivity(new Intent(activity, UserinfoActivity.class));
                }
                break;
                case "UserinfoExtActivity": {
                    activity.startActivity(new Intent(activity, UserinfoExtActivity.class));
                }
                break;
            }
        }
    }

    public static void Init()
    {
        s_bShowNetConnection = false;
        s_ActivityName_NetConnection = "";
        s_activity_NetConnection = null;


        s_bEvent_service = false;
        s_bEvent_service_main = false;
        s_bEvent_service_player = false;

        s_bEvent_service_player_stop = false;
        s_bEvent_service_player_timer_stop = false;

        //s_player_timer_count = 0;

        s_StrMainFragment = FRAGMENT_HOME;

        s_StrFragment_Profile_Tab = TAB_CONTENTS;

        s_activity = null;


        ClearActivity_Temp();

        s_userProfile_friend = null;

        s_MeditationContents_temp = null;

        ClearActivityInPlayerList();

        s_activityFocus = null;

        s_bShowTimerPopup = false;

        s_psychology_state = -1;

        s_id_backimamge_makecontents = -1;

        s_playerMode = PlayerMode.base;
    }
}
