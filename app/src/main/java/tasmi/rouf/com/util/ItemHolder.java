package tasmi.rouf.com.util;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import tasmi.rouf.com.tasmi.R;

public class ItemHolder extends RecyclerView.ViewHolder {

    TextView itemName;
    Button btnGoUjian;

    public ItemHolder(@NonNull View itemView) {
        super(itemView);
        itemName = itemView.findViewById(R.id.item_name);
        btnGoUjian = itemView.findViewById(R.id.btn_go_to_ujian);
    }

}
