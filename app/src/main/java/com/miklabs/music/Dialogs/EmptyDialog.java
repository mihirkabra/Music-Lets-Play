package com.miklabs.music.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.miklabs.music.R;

public class EmptyDialog extends Dialog {

    Activity activity;
    Dialog dialog;

    ImageView image;
    TextView text, text_NO;
    Button button_YES;
    String message;

    public EmptyDialog(@NonNull Activity activity, String message) {
        super(activity);
        this.activity = activity;
        this.message = message;
    }

    public void showDialog() {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_simple);
        text = (TextView) dialog.findViewById(R.id.dialogSimple_text);
        text_NO = (TextView) dialog.findViewById(R.id.dialogSimple_NO);
        text_NO.setText("Close");

        image = (ImageView) dialog.findViewById(R.id.dialogSimple_image);

        button_YES = (Button) dialog.findViewById(R.id.dialogSimple_YES);
        button_YES.setText("Create");

        text.setText(message);
        image.setVisibility(View.GONE);

        text_NO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });
        button_YES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                HomeAddPlaylistDialog d = new HomeAddPlaylistDialog(activity);
                d.showDialog();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }
}
