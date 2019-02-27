package tasmi.rouf.com.tasmi;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tasmi.rouf.com.json.MyJSON;
import tasmi.rouf.com.model.Siswa;
import tasmi.rouf.com.model.Soal;
import tasmi.rouf.com.model.Ujian;
import tasmi.rouf.com.util.Constant;

public class UjianActivity extends Activity{//} AppCompatActivity {

    SiswaService siswaService;
    boolean iBound = false;

    private static Integer id_siswa = null;

    private Siswa siswa = new Siswa();
    private Ujian ujian = new Ujian();
    private List<Soal> soal = new ArrayList<Soal>();
    private Integer[] id_soal = {0,0,0,0,0,0,0};
    Boolean sudahUjian = false;

    private static TextView title, status;
    private static EditText totalNilai_txt;
    private static EditText[] kolom_soal= new EditText[10];

    //soal


    public UjianActivity(){
        siswa = new Siswa();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ujian);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Intent intent = new Intent(this, SiswaService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        id_siswa = getIntent().getExtras().getInt("id_siswa");
        Log.i(Constant.tag,"onCreate, id"+id_siswa);
        siswa.setId(id_siswa);

        title = findViewById(R.id.textView_title);
        status = findViewById(R.id.textView_status);
        kolom_soal[0] = findViewById(R.id.editText_q1);
        kolom_soal[1] = findViewById(R.id.editText_q2);
        kolom_soal[2] = findViewById(R.id.editText_q3);
        kolom_soal[3] = findViewById(R.id.editText_q4);
        kolom_soal[4] = findViewById(R.id.editText_q5);
        kolom_soal[5] = findViewById(R.id.editText_q6);
        kolom_soal[6] = findViewById(R.id.editText_tajwid);
        kolom_soal[7] = findViewById(R.id.editText_hafalan);
        kolom_soal[8] = findViewById(R.id.editText_hadir);
        kolom_soal[9] = findViewById(R.id.editText_keterangan);

        totalNilai_txt = findViewById(R.id.editText_total);

        Navigate.cekLogin(this);

    }

    private void cekUjian(){
        ujian.setIdsiswa(siswa.getId());
        Log.i(Constant.tag,"ID"+siswa.getId()+","+ujian.getIdsiswa());
        String resp_ujian = siswaService.ujianByIdSiswa(ujian);
        if(resp_ujian.equals("null")){

        }else{
            sudahUjian = true;
            List<String[]> obj = new ArrayList<String[]>();
            for (String x : MyJSON.extractObj(resp_ujian)) {
                String[] prop = MyJSON.propVal(x);
                obj.add(prop);
            }
            ujian = (Ujian) MyJSON.getObj(new Ujian(), obj);
            listSoal();
            for(int i=0;i<soal.size();i++){
                id_soal[i]=soal.get(i).getId();
                final String n_str = soal.get(i).getNilai().toString();
                kolom_soal[i].setText(n_str);
            }
            final String tajwid_str = ujian.getTajwid().toString();
            final String keterangan = ujian.getKeterangan();
            final String kehadiran = ujian.getKehadiran().toString();
            final String hafalan = ujian.getHafalan().toString();
            final String total = ujian.getTotal().toString();
            kolom_soal[6].setText(tajwid_str);
            kolom_soal[7].setText(hafalan);
            kolom_soal[8].setText(kehadiran);
            kolom_soal[9].setText(keterangan);
            Double  total_dbl = nilaiTotal(ujian.getHafalan(), ujian.getKehadiran(), soal);
            final String nilai_total_txt = total_dbl.toString();
          //  ujian.setTotal(total_dbl.intValue());
            totalNilai_txt.setText(nilai_total_txt);


        }

        status.setText("sudah ujian: "+sudahUjian.toString()+" "+ ujian.getId());
    }

    private void listSoal(){
        soal.clear();
        String respons = siswaService.soalByIdUjian(ujian.getId());

        System.out.println(respons);
        List<String> objs = MyJSON.getObjFromArray(respons);
        List<List<String[]>> list_obj = new ArrayList<>();
        String toDisplay="";
        for (String str_obj : objs) {
            List<String[]> obj = new ArrayList<String[]>();
            for (String x : MyJSON.extractObj(str_obj)) {

                String[] prop = MyJSON.propVal(x);
                obj.add(prop);
            }
            Soal s = (Soal) MyJSON.getObj(new Soal(), obj);

            list_obj.add(obj);
            toDisplay+=siswa.toString();
            Log.i(Constant.tag,"SOAL "+s);
            soal.add(s);
        }
    }

    public void goToListUjianSiswa(View v){
        Intent i = new Intent(this, ListUjianSiswaActivity.class);
        startActivity(i);
    }

    private boolean setSiswa(){
        Log.i(Constant.tag,"ID SISWA:"+id_siswa);
        try {
            if (id_siswa.equals(null))
                return false;
            String s = siswaService.siswaById(siswa);
            Log.i(Constant.tag, s);
            if (s.equals(""))
                return false;
            List<String[]> obj = new ArrayList<String[]>();
            List<String> objs = MyJSON.extractObj(s);
            for (String x : objs) {
                String[] prop = MyJSON.propVal(x);
                obj.add(prop);
            }
            siswa = (Siswa) MyJSON.getObj(new Siswa(), obj);
            siswa.setId(id_siswa);
            title.setText(siswa.toString());
            return true;
        }catch(Exception e){
            Log.i(Constant.tag, e.toString());
            return false;
        }
    }

    public void resetField(View v){
        final String empty = "0";

        for(int i=0;i<kolom_soal.length;i++){
            kolom_soal[i].setText(empty);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateUjian(View v){
        soal.clear();
        for(int i=0;i<kolom_soal.length-4;i++){
            String nilai_text = kolom_soal[i].getText().toString();
            Integer nilai  = 0;
            if(!nilai_text.equals(""))
                nilai = Integer.parseInt(nilai_text);

            Log.i(Constant.tag,"nilai: "+ nilai);

            Soal s = new Soal(id_soal[i]==0?0:id_soal[i],ujian.getId(),nilai);
            soal.add(s);
        }

        final String nilai_tajwid_text = kolom_soal[6].getText().toString();
        final String nilai_hafalan = kolom_soal[7].getText().toString();
        final String nilai_hadir = kolom_soal[8].getText().toString();
        final String keterangan = kolom_soal[9].getText().toString();

        int nilai_tajwid_int = 0;
        int nilai_hafalan_int = 0;
        int nilai_hadir_int = 0;
        if(!nilai_tajwid_text.equals(""))
            nilai_tajwid_int = Integer.parseInt(nilai_tajwid_text);
        if(!nilai_hadir.equals(""))
            nilai_hadir_int = Integer.parseInt(nilai_hadir);
        if(!nilai_hafalan.equals(""))
            nilai_hafalan_int = Integer.parseInt(nilai_hafalan);
        ujian.setTajwid(nilai_tajwid_int);
        ujian.setHafalan(nilai_hafalan_int);
        ujian.setKehadiran(nilai_hadir_int);
        ujian.setKeterangan(keterangan);

        Double  total_dbl = nilaiTotal(nilai_hafalan_int, nilai_hadir_int, soal);
        final String nilai_total_txt = total_dbl.toString();
        ujian.setTotal(total_dbl.intValue());

        if(sudahUjian){
            Integer updateUjian = siswaService.editUjian(ujian);
            if(updateUjian.equals(1))
                for(Soal s:soal){
                    siswaService.editSoal(s);
                }
            Toast.makeText(this, "oke update ujian", Toast.LENGTH_SHORT).show();
        }else{
            Random r = new Random();
            int id_ujian_new = 298* r.nextInt(40000000)/200;
            id_ujian_new = Math.abs(id_ujian_new);
            ujian.setId(id_ujian_new);
            Integer addUjian = siswaService.tambahUjian(ujian);
            if(addUjian.equals(1)){
                Toast.makeText(this, "oke menambah ujian", Toast.LENGTH_SHORT).show();
                for(Soal s:soal){
                    s.setIdujian(id_ujian_new);
                    siswaService.tambahSoal(s);
                }
            }else{
                Toast.makeText(this, "gagal menambah ujian", Toast.LENGTH_SHORT).show();
            }
        }
        totalNilai_txt.setText(nilai_total_txt);
    }

    private Double nilaiTotal(double hafalan, double hadir, List<Soal> ls ){
        Double total = hadir+hafalan;
        Double totalSoal = 0d;
        for(Soal s:ls){
            totalSoal+=s.getNilai();
        }
        Log.i(Constant.tag,"totalSoal:"+ totalSoal + " hadir dan hafalan: "+total);
        total = total  + (totalSoal/6) * (0.6);

        return total;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(Constant.tag,"service connected");
            SiswaService.LocalBinder binder = (SiswaService.LocalBinder) service;
            siswaService = binder.getService();
            Log.i(Constant.tag, siswaService.toString());
            siswaService = new SiswaService(getApplicationContext());

            iBound = true;
            if(setSiswa())
                cekUjian();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iBound = false;
        }
    };
}
