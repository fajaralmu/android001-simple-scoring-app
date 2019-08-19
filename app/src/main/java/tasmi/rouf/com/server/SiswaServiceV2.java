package tasmi.rouf.com.server;

import android.content.Context;
import android.util.Log;


import tasmi.rouf.com.model.Guru;
import tasmi.rouf.com.model.Kelas;
import tasmi.rouf.com.model.Siswa;
import tasmi.rouf.com.model.Soal;
import tasmi.rouf.com.model.Ujian;
import tasmi.rouf.com.util.Constant;
import tasmi.rouf.com.util.ServerAddress;

public class SiswaServiceV2 {

    private String server_addr;
    private static ServerAddress sa;


    public SiswaServiceV2(Context ctx) {
        sa = new ServerAddress(ctx);
        this.server_addr = "http://" + sa.Read();
    }

    public String listSiswa(String sessionKey) {
        return SiswaServer.executePost(server_addr + "/tasmi/index.php/siswa/semua/"+sessionKey, "tampil=true");
    }

    public String siswaByKelasAndName(String nama, String kodekelas, String sessionKey, int mulai, int limit) {
        String param= "{ \"nama\":\""+nama+"\", \"kelas\":\""+kodekelas+"\", \"mulai\":"+mulai+",\"limit\":"+limit+" }";
        String respons = SiswaServer.executePost(server_addr + "/tasmi/index.php/siswa/filter/"+sessionKey, param);
        respons = respons.replaceAll("\\p{C}", "");
        Log.i(Constant.tag,respons);
        if (respons.equals("")) {
            return "";
        } else {
            return respons;
        }
    }

    public String siswaById(Siswa siswa) {
        Integer id = siswa.getId();
        String param = "byid=true";
        String respons = SiswaServer.executePost(server_addr + "/tasmi/index.php/siswa/siswabyid/" + id, param);
        respons = respons.replaceAll("\\p{C}", "");
        if (respons.equals("")) {
            return "";
        } else {
            return respons;
        }
    }

    public Integer tambahSiswa(Siswa siswa, String sessionKey) {
        String jsonParam = siswa.toFullString();
        String respons = SiswaServer.executePost(server_addr + "/tasmi/index.php/siswa/tambah/"+sessionKey, jsonParam);
        respons = respons.replaceAll("\\p{C}", "");
        if (respons.equals("")) {
            return 0;
        } else {
            return Integer.parseInt(respons);
        }
    }

    public Integer editSiswa(Siswa siswa, String sessionKey) {
        String respons = SiswaServer.executePost(server_addr + "/tasmi/index.php/siswa/update/"+sessionKey, siswa.toFullString());
        respons = respons.replaceAll("\\p{C}", "");
        if (respons.equals("")) {
            return 0;
        } else {
            return Integer.parseInt(respons);
        }
    }

    public Integer hapusSiswaFull(int id) {
        String param = "id=" + id;
        String respons = SiswaServer.executePost(server_addr + "/tasmi/index.php/siswa/hapus_siswa_full/" + id, param);
        respons = respons.replaceAll("\\p{C}", "");
        if (respons.equals("")) {
            return 0;
        } else {
            return Integer.parseInt(respons);
        }
    }

    ///UJIAN///
    public String ujianByIdSiswa(int id) {
        Log.i(Constant.tag, "ID:" + id);
        String param = "byidsiswa=true";
        String respons = SiswaServer.executePost(server_addr + "/tasmi/index.php/ujian/ujianbyidsiswa/" + id, param);
        respons = respons.replaceAll("\\p{C}", "");
        if (respons.equals("")) {
            return "";
        } else {
            return respons;
        }
    }

    public Integer tambahUjian(Ujian u,String sessionKey) {
        String respons = SiswaServer.executePost(server_addr + "/tasmi/index.php/ujian/tambah/"+sessionKey, u.toFullString());
        respons = respons.replaceAll("\\p{C}", "");
        if (respons.equals("")) {
            return 0;
        } else {
            return Integer.parseInt(respons);
        }
    }

    public Integer editUjian(Ujian u, String sessionKey) {
        String respons = SiswaServer.executePost(server_addr + "/tasmi/index.php/ujian/update/"+sessionKey, u.toFullString());
        respons = respons.replaceAll("\\p{C}", "");
        if (respons.equals("")) {
            return 0;
        } else {
            return Integer.parseInt(respons);
        }
    }

    ///SOAL///
    public String soalByIdUjian(int id) {
        String param = "byid=true";
        String respons = SiswaServer.executePost(server_addr + "/tasmi/index.php/soal/soalbyidujian/" + id, param);
        respons = respons.replaceAll("\\p{C}", "");
        if (respons.equals("")) {
            return "";
        } else {
            return respons;
        }
    }

    public Integer tambahSoal(Soal s) {
        String respons = SiswaServer.executePost(server_addr + "/tasmi/index.php/soal/tambah", s.toFullString());
        respons = respons.replaceAll("\\p{C}", "");
        if (respons.equals("")) {
            return 0;
        } else {
            return Integer.parseInt(respons);
        }
    }

    public Integer editSoal(Soal s) {
        String respons = SiswaServer.executePost(server_addr + "/tasmi/index.php/soal/update", s.toFullString());
        respons = respons.replaceAll("\\p{C}", "");
        if (respons.equals("")) {
            return 0;
        } else {
            return Integer.parseInt(respons);
        }
    }

    //GURU
    public String guruMasuk(Guru g) {
        String respons = SiswaServer.executePost(server_addr + "/tasmi/index.php/akun/masukMobile", g.toFullString());
        respons = respons.replaceAll("\\p{C}", "");
        return respons;

    }

    public Integer guruKeluar(String sessionKey) {
        String respons = SiswaServer.executePost(server_addr + "/tasmi/index.php/akun/keluarMobile/"+sessionKey, "oke=true");
        respons = respons.replaceAll("\\p{C}", "");
        if (respons.equals("")) {
            return 0;
        } else {
            return Integer.parseInt(respons);
        }
    }

    public Integer editAkunGuru(Guru g, String sessionKey) {
        String respons = SiswaServer.executePost(server_addr + "/tasmi/index.php/akun/update/"+sessionKey, g.toFullString());
        respons = respons.replaceAll("\\p{C}", "");
        if (respons.equals("")) {
            return 0;
        } else {
            return Integer.parseInt(respons);
        }
    }

    public String guruBySession(String session) {
        String respons = SiswaServer.executePost(server_addr + "/tasmi/index.php/akun/gurubysession/" + session, "oke=true");
        respons = respons.replaceAll("\\p{C}", "");
        if (respons.equals("")) {
            return "";
        } else {
            return respons;
        }
    }

    //KELAS//
    public String listKelas() {
        return SiswaServer.executePost(server_addr + "/tasmi/index.php/kelas/semua", "tampil=true");
    }

    public String kelasById(Kelas k) {
        Integer id = k.getId();
        Log.i(Constant.tag, id.toString());
        String param = "byid=true";
        String respons = SiswaServer.executePost(server_addr + "/tasmi/index.php/kelas/get/" + id, param);
        respons = respons.replaceAll("\\p{C}", "");
        if (respons.equals("")) {
            return "";
        } else {
            return respons;
        }
    }

    //OTHER//
    private Integer validateSession(String session) {
        String param = "byid=true";
        String respons = SiswaServer.executePost(server_addr + "/tasmi/index.php/akun/cekV2/" + session, "oke=true");
        respons = respons.replaceAll("\\p{C}", "");
        int intRespon = Integer.parseInt(respons);
        return intRespon;
    }

}
