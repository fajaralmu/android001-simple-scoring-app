package tasmi.rouf.com.model;

public class Soal {
    private Integer id =0;
    private Integer idujian = 0;
    private Integer nilai = 0;

    public Soal(){

    }

    public Soal(Integer id, Integer idujian, Integer nilai) {
        this.id = id;
        this.idujian = idujian;
        this.nilai = nilai;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdujian() {
        return idujian;
    }

    public void setIdujian(Integer idujian) {
        this.idujian = idujian;
    }

    public Integer getNilai() {
        return nilai;
    }

    public void setNilai(Integer nilai) {
        this.nilai = nilai;
    }


    public String toFullString() {
        return "{" +
                "\"id\":" + id +
                ", \"idujian\":" + idujian +
                ", \"nilai\":" + nilai +
                '}';
    }
}
