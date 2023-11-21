package app.models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "KATEGORIA")
public class Kategoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kat_kod")
    public int kod;

    @Column(name = "kat_nev")
    public String nev;

    @OneToMany(mappedBy = "kategoria", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    public List<Aru> aruk;

    public Kategoria() {
    }

    public Kategoria(String nev, List<Aru> aruk) {
        this.nev = nev;
        this.aruk = aruk;
    }

    public int getKod() {
        return kod;
    }

    public void setKod(int kod) {
        this.kod = kod;
    }
}
