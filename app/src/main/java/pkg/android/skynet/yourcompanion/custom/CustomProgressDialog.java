package pkg.android.skynet.yourcompanion.custom;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import pkg.android.skynet.yourcompanion.R;

/**
 * Created by as on 5/6/2016.
 */
public class CustomProgressDialog {

    private Context context;
    private ProgressDialog dialog;
    //private Dialog dialog;

    public CustomProgressDialog(Context context){
        try {
            dialog = new ProgressDialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            /*dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_progress_gif);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#bfffffff")));*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show(){
        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismiss(){
        try {
            dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
