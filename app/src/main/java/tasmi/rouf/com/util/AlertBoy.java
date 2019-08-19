package tasmi.rouf.com.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Window;

import tasmi.rouf.com.tasmi.R;

public class AlertBoy {
    public AlertBoy(){
    }

    public static void YesAlert(Context ctx, String title, String content){
        AlertDialog.Builder a_builder = new AlertDialog.Builder(ctx);
        a_builder.setMessage(content)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                ;
        AlertDialog alert  = a_builder.create();
        alert.setTitle(title);
        alert.show();
    }

    public static MyLoadingDialog loadingMulai(Context ctx) {
        MyLoadingDialog dialog_loading_final = new MyLoadingDialog(ctx);
        dialog_loading_final.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_loading_final.setContentView(R.layout.loading_layout);
        dialog_loading_final.setCancelable(false);
        return dialog_loading_final;

    }

}
