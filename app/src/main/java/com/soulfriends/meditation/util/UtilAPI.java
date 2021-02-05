package com.soulfriends.meditation.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.ConnectivityManager;
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
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.view.BaseActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.soulfriends.meditation.model.MeditationContents;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
                showImage(context, uri, view);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                Log.d("Meditation","load_imageEX onFailure : " + str_uri);
                int xx = 0;
            }
        });
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
        Glide.with(view.getContext()).load(uri).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(view);
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




    public static void Init()
    {
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
