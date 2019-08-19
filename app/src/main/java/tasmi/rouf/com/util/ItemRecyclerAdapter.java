package tasmi.rouf.com.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import tasmi.rouf.com.model.Siswa;
import tasmi.rouf.com.tasmi.ListUjianSiswaActivity;
import tasmi.rouf.com.tasmi.R;

public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemHolder> {

    List<Siswa> models = new ArrayList<>();
    Context ctx;

    public ItemRecyclerAdapter(List<Siswa> list, Context ctx){
        models=list;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new ItemHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycler, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder,final int i) {
        itemHolder.itemName.setText((i+1)+"."+((Siswa) models.get(i)).getNama()+" jml soal: "+models.get(i).get_Ujian().get_ListSoal().size());
        itemHolder.btnGoUjian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListUjianSiswaActivity.goToUjian(models.get(i),ctx);
            }
        });
    }

    @Override
    public int getItemCount() {
        return models !=null?models.size():0;
    }
}
