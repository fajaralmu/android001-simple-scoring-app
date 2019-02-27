package tasmi.rouf.com.tasmi;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Random;

import tasmi.rouf.com.model.Guru;
import tasmi.rouf.com.model.Kelas;
import tasmi.rouf.com.model.Siswa;
import tasmi.rouf.com.model.Soal;
import tasmi.rouf.com.model.Ujian;
import tasmi.rouf.com.server.SiswaServer;
import tasmi.rouf.com.util.Constant;
import tasmi.rouf.com.util.ServerAddress;

public class SiswaService extends Service {

    public final IBinder iBender = new LocalBinder();

    private String server_addr;
    private static ServerAddress sa;


    public class LocalBinder extends Binder {
        SiswaService getService(){
            return SiswaService.this;
        }
    }

    public SiswaService(){

    }

    public SiswaService(Context ctx){
        sa = new ServerAddress(ctx);
        this.server_addr = "http://"+sa.Read();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBender;
    }



    public String listSiswa(){
       return SiswaServer.executePost(server_addr+"/tasmi/index.php/siswa/semua","tampil=true");
    }

    public String siswaById(Siswa siswa){
        Integer id = siswa.getId();
        Log.i(Constant.tag,id.toString());
        String param = "byid=true";
        String respons = SiswaServer.executePost(server_addr+"/tasmi/index.php/siswa/siswabyid/"+id,param);
        respons = respons.replaceAll("\\p{C}", "");
        if(respons.equals("")){
            return "";
        }else{
            return respons;
        }
    }

    public Integer tambahSiswa(Siswa siswa){

        String param = "nama="+siswa.getNama()+"&kelas="+siswa.getKelas()+"&lahir="+siswa.getLahir();
        String jsonParam = siswa.toFullString();
        String respons = SiswaServer.executePost(server_addr+"/tasmi/index.php/siswa/tambah",jsonParam);
        respons = respons.replaceAll("\\p{C}", "");
        if(respons.equals("")){
            return 0;
        }else{
            return Integer.parseInt(respons);
        }
    }

    public Integer editSiswa(Siswa siswa){
        String param = "id="+siswa.getId()+"&nama="+siswa.getNama()+"&kelas="+siswa.getKelas()+"&lahir="+siswa.getLahir();
        String respons = SiswaServer.executePost(server_addr+"/tasmi/index.php/siswa/update",siswa.toFullString());
        respons = respons.replaceAll("\\p{C}", "");
        if(respons.equals("")){
            return 0;
        }else{
            return Integer.parseInt(respons);
        }
    }

    public Integer hapusSiswaFull(int id){
        String param = "id="+id;
        String respons = SiswaServer.executePost(server_addr+"/tasmi/index.php/siswa/hapus_siswa_full/"+id,param);
        respons = respons.replaceAll("\\p{C}", "");
        if(respons.equals("")){
            return 0;
        }else{
            return Integer.parseInt(respons);
        }
    }

    ///UJIAN///
    public String ujianByIdSiswa(Ujian ujian){
        Integer id = ujian.getIdsiswa();
        Log.i(Constant.tag,"ID:"+id);
        String param = "byidsiswa=true";
        String respons = SiswaServer.executePost(server_addr+"/tasmi/index.php/ujian/ujianbyidsiswa/"+id,param);
        respons = respons.replaceAll("\\p{C}", "");
        if(respons.equals("")){
            return "";
        }else{
            return respons;
        }
    }

    public Integer tambahUjian(Ujian u){
        String param = "id="+u.getId()+"&idsiswa="+u.getIdsiswa()+"&tajwid="+u.getTajwid()+
                "&idguru="+u.getIdguru()+"&hafalan="+u.getHafalan()+"&kehadiran="+u.getKehadiran()+"&keterangan="+u.getKeterangan()+"&total="+u.getTotal();
        String respons = SiswaServer.executePost(server_addr+"/tasmi/index.php/ujian/tambah",u.toFullString());
        respons = respons.replaceAll("\\p{C}", "");
        if(respons.equals("")){
            return 0;
        }else{
            return Integer.parseInt(respons);
        }
    }

    public Integer editUjian(Ujian u){
        String param = "id="+u.getId()+"&idsiswa="+u.getIdsiswa()+"&tajwid="+u.getTajwid()+
                "&idguru="+u.getIdguru()+"&hafalan="+u.getHafalan()+"&kehadiran="+u.getKehadiran()+"&keterangan="+u.getKeterangan()+"&total="+u.getTotal();
        String respons = SiswaServer.executePost(server_addr+"/tasmi/index.php/ujian/update",u.toFullString());
        respons = respons.replaceAll("\\p{C}", "");
        if(respons.equals("")){
            return 0;
        }else{
            return Integer.parseInt(respons);
        }
    }

    ///SOAL///
    public String soalByIdUjian(int id){
        String param = "byid=true";
        String respons = SiswaServer.executePost(server_addr+"/tasmi/index.php/soal/soalbyidujian/"+id,param);
        respons = respons.replaceAll("\\p{C}", "");
        if(respons.equals("")){
            return "";
        }else{
            return respons;
        }
    }

    public Integer tambahSoal(Soal s){
        String param = "idujian="+s.getIdujian()+"&nilai="+s.getNilai();
        String respons = SiswaServer.executePost(server_addr+"/tasmi/index.php/soal/tambah",s.toFullString());
        respons = respons.replaceAll("\\p{C}", "");
        if(respons.equals("")){
            return 0;
        }else{
            return Integer.parseInt(respons);
        }
    }

    public Integer editSoal(Soal s){
        String param = "id="+s.getId()+"&idujian="+s.getIdujian()+"&nilai="+s.getNilai();
        String respons = SiswaServer.executePost(server_addr+"/tasmi/index.php/soal/update",s.toFullString());
        respons = respons.replaceAll("\\p{C}", "");
        if(respons.equals("")){
            return 0;
        }else{
            return Integer.parseInt(respons);
        }
    }

    //GURU
    public Integer guruMasuk(Guru g){
        String param = "namapengguna="+g.getNamapengguna()+"&katasandi="+g.getKatasandi();
        String respons = SiswaServer.executePost(server_addr+"/tasmi/index.php/akun/masuk",g.toFullString());
    //    Log.i(Constant.tag, "param: "+param+", RESP "+server_addr+" "+ respons);
        respons = respons.replaceAll("\\p{C}", "");
        if(respons.equals("") || respons.equals(null) || respons.equals("null")){
            return 0;
        }else{
            return Integer.parseInt(respons);
        }

    }

    public Integer editAkunGuru(Guru g){
        String param = "id="+g.getId()+"&nama="+g.getNama()+"&namapengguna="+g.getNamapengguna()+"&katasandi="+g.getKatasandi();
        String respons = SiswaServer.executePost(server_addr+"/tasmi/index.php/akun/update",g.toFullString());
        respons = respons.replaceAll("\\p{C}", "");
        if(respons.equals("")){
            return 0;
        }else{
            return Integer.parseInt(respons);
        }
    }

    public String guruByUsername(Guru g){
        String username = g.getNamapengguna();

        String param = "byusername=true";
        String respons = SiswaServer.executePost(server_addr+"/tasmi/index.php/akun/gurubyusername/"+username,param);
        respons = respons.replaceAll("\\p{C}", "");
        if(respons.equals("")){
            return "";
        }else{
            return respons;
        }
    }

    //KELAS//
    public String listKelas(){
        return SiswaServer.executePost(server_addr+"/tasmi/index.php/kelas/semua","tampil=true");
    }

    public String kelasById(Kelas k){
        Integer id = k.getId();
        Log.i(Constant.tag,id.toString());
        String param = "byid=true";
        String respons = SiswaServer.executePost(server_addr+"/tasmi/index.php/kelas/get/"+id,param);
        respons = respons.replaceAll("\\p{C}", "");
        if(respons.equals("")){
            return "";
        }else{
            return respons;
        }
    }

}
