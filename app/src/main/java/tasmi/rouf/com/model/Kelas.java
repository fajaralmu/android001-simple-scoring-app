package tasmi.rouf.com.model;

import java.util.List;

public class Kelas {
    private Integer id;
    private String namakelas, keterangan;

    public Kelas(Integer id, String kelas, String keterangan) {
        this.id = id;
        this.namakelas = kelas;
        this.keterangan = keterangan;
    }

    public Kelas(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNamakelas() {
        return namakelas;
    }

    public void setNamakelas(String namakelas) {
        this.namakelas = namakelas;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    @Override
    public String toString(){
        return this.namakelas;
    }

    public static Kelas get_NamakelasFromList(Integer id, List<Kelas> lk){
        Kelas kelas = new Kelas(000, "undefined", "undefined");

        for(Kelas k:lk){
            if(id.equals(k.getId()))
                kelas = k;
        }
        return kelas;
    }
}
