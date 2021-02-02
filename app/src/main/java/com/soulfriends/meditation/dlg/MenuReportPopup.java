package com.soulfriends.meditation.dlg;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.soulfriends.meditation.R;

public class MenuReportPopup extends Dialog {
    private Activity activity;
    private Context context;

    public ImageView iv_report;

    public MenuReportPopup(@NonNull Context context, Activity activity) {
        super(context);

        //super(context, R.style.MyPopupMenu);
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_menu_report);

        iv_report = this.findViewById(R.id.iv_report);
    }
}
