package tasmi.rouf.com.model;

import java.util.ArrayList;
import java.util.List;

public class Ujian {

    private Integer id = 0, idsiswa = 0,idguru  = 0,tajwid = 0, hafalan =0, kehadiran=0, total =0;
    private String keterangan;

    private List<Soal> listSoal = new ArrayList<>();

    public Ujian(){

    }

    public List<Soal> get_ListSoal(){
        return this.listSoal;
    }

    public void set_ListSoal(List<Soal> l){
        listSoal = l;
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdsiswa() {
        return idsiswa;
    }

    public void setIdsiswa(Integer idsiswa) {
        this.idsiswa = idsiswa;
    }

    public Integer getIdguru() {
        return idguru;
    }

    public void setIdguru(Integer idguru) {
        this.idguru = idguru;
    }

    public Integer getTajwid() {
        return tajwid;
    }

    public void setTajwid(Integer tajwid) {
        this.tajwid = tajwid;
    }

    public Integer getHafalan() {
        return hafalan;
    }

    public void setHafalan(Integer hafalan) {
        this.hafalan = hafalan;
    }

    public Integer getKehadiran() {
        return kehadiran;
    }

    public void setKehadiran(Integer kehadiran) {
        this.kehadiran = kehadiran;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public void setTotal(Integer t) {
        this.total = t;
    }

    public Integer getTotal() {
        return total;
    }


    public String toFullString() {
        return "{\"id\":" + id +
                ", \"idsiswa\":" + idsiswa +
                ", \"idguru\":" + idguru +
                ", \"tajwid\":" + tajwid +
                ", \"hafalan\":" + hafalan +
                ", \"kehadiran\":" + kehadiran +
                ", \"total\":" + total +
                ", \"keterangan\":\"" + keterangan + "\"}";
    }
}
