package com.soulfriends.meditation.dlg;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.soulfriends.meditation.R;

public class MenuPopup extends Dialog {

    private Activity activity;
    private Context context;

    public ImageView iv_modify;
    public ImageView iv_delete;

    public MenuPopup(@NonNull Context context, Activity activity) {
        super(context);

        //super(context, R.style.MyPopupMenu);
        this.context = context;
        this.activity = activity;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_menu);

        iv_modify = this.findViewById(R.id.iv_modify);

        iv_delete = this.findViewById(R.id.iv_delete);
    }
}
