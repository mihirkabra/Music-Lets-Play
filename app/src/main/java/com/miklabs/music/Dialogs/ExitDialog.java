package com.miklabs.music.Dialogs;

import static com.miklabs.music.MusicPlayer.mediaPlayer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.miklabs.music.ForegroundService;
import com.miklabs.music.MainActivity;
import com.miklabs.music.R;

import java.util.Objects;

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

    public void showDialog() {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_simple);

        mainActivity = new MainActivity();

        text = dialog.findViewById(R.id.dialogSimple_text);
        text_NO = dialog.findViewById(R.id.dialogSimple_NO);

        image = dialog.findViewById(R.id.dialogSimple_image);

        button_YES = dialog.findViewById(R.id.dialogSimple_YES);

        text_NO.setOnClickListener(v -> {

            int p = MainActivity.db.getInt("fragmentNo", 0);
            Objects.requireNonNull(MainActivity.list.findViewHolderForAdapterPosition(p)).itemView.performClick();
            dialog.dismiss();

        });
        button_YES.setOnClickListener(v -> {
            stopService();
            mainActivity.exitApp();
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
