package tasmi.rouf.com.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

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
}
