package com.example.music.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.music.ForegroundService;
import com.example.music.MainActivity;
import com.example.music.R;

import static com.example.music.MusicPlayer.mediaPlayer;

public class ExitDialog extends Dialog {

    Activity activity;
    Dialog dialog;

    ImageView image;
    TextView text, text_NO;
    Button button_YES;

    MainActivity mainActivity;

    public ExitDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public void showDialog()
    {
        dialog= new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_simple);

        mainActivity = new MainActivity();

        text = (TextView) dialog.findViewById(R.id.dialogSimple_text);
        text_NO = (TextView)dialog.findViewById(R.id.dialogSimple_NO);

        image = (ImageView) dialog.findViewById(R.id.dialogSimple_image);

        button_YES = (Button) dialog.findViewById(R.id.dialogSimple_YES);

        text_NO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int p = MainActivity.db.getInt("fragmentNo", 0);
                MainActivity.list.findViewHolderForAdapterPosition(p).itemView.performClick();
                dialog.dismiss();

            }
        });
        button_YES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService();
                mainActivity.exitApp();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }
    public void stopService() {
        Intent serviceIntent = new Intent(activity, ForegroundService.class);
        activity.stopService(serviceIntent);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
