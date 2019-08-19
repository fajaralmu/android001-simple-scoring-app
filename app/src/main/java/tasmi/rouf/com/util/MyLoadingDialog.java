package tasmi.rouf.com.util;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

public class MyLoadingDialog extends Dialog {
    public MyLoadingDialog(@NonNull Context context) {
        super(context);
    }

    public void show(){
        if(!this.isShowing()){
            super.show();
        }
    }

    public void dismiss(){
        if(this.isShowing())
            super.dismiss();
    }
}
