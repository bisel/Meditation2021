package com.soulfriends.meditation.util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.soulfriends.meditation.view.FriendEditActivity;
import com.soulfriends.meditation.view.MainActivity;
import com.soulfriends.meditation.view.MyContentsActivity;
import com.soulfriends.meditation.view.PlayerActivity;
import com.soulfriends.meditation.view.ProfileActivity;
import com.soulfriends.meditation.view.UserinfoActivity;


public class ActivityStack {

    private Context context;

    private static ActivityStack instance = null;

    public static ActivityStack instance(){
        return instance;
    }

    private ActivityStack(Context context) {
        this.context = context;
    }

    public static ActivityStack with(Context context) {

        if (instance == null)
            instance = new ActivityStack(context);

        return instance;
    }

    private ArrayStack arrayStack = new ArrayStack(1000);

    public class Node{
        public String activity_name;
        public String data;
    }

    public void Push(Activity activity, String data)
    {
        Node node = new Node();
        node.activity_name = activity.getClass().getSimpleName();
        node.data = data;

        arrayStack.push(node);
    }

    public Node Pop()
    {
        return (Node)arrayStack.pop();
    }

    public void Clear()
    {
        arrayStack.Clear();
    }

    public String Peek()
    {
        Node node = (Node)arrayStack.peek();
        if(node == null) return null;
        return node.activity_name;
    }

    public void OnBack(Activity activity)
    {
        if(arrayStack.empty())
        {
            // 메인액티비티
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.overridePendingTransition(0, 0);
            activity.finish();
            return;
        }
        Node node = Pop();

        switch(node.activity_name)
        {
            case "PlayerActivity":
            {
                activity.startActivity(new Intent(activity, PlayerActivity.class));
                activity.overridePendingTransition(0, 0);
                activity.finish();
            }
            break;
            case "MainActivity":
            {
                activity.startActivity(new Intent(activity, MainActivity.class));
                activity.overridePendingTransition(0, 0);
                activity.finish();
            }
            break;
            case "ProfileActivity":
            {
                activity.startActivity(new Intent(activity, ProfileActivity.class));
                activity.overridePendingTransition(0, 0);
                activity.finish();
            }
            break;
            case "FriendEditActivity":
            {
                activity.startActivity(new Intent(activity, FriendEditActivity.class));
                activity.overridePendingTransition(0, 0);
                activity.finish();
            }
            break;
            case "MyContentsActivity":
            {
                activity.startActivity(new Intent(activity, MyContentsActivity.class));
                activity.overridePendingTransition(0, 0);
                activity.finish();
            }
            break;
        }
    }

}
