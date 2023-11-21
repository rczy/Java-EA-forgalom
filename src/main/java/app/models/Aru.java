package app.models;

import javax.persistence.*;

@Entity
@Table(name = "ARU")
public class Aru {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aru_kod")
    public int kod;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "kat_kod")
    public Kategoria kategoria;

    @Column(name = "nev")
    public String nev;

    @Column(name = "egyseg")
    public String egyseg;

    @Column(name = "ar")
    public int ar;

    @OneToOne(mappedBy = "aru")
    public Eladas eladas;

    public Aru() {
    }

    public Aru(Kategoria kategoria, String nev, String egyseg, int ar, Eladas eladas) {
        this.kategoria = kategoria;
        this.nev = nev;
        this.egyseg = egyseg;
        this.ar = ar;
        this.eladas = eladas;
    }

    public int getKod() {
        return kod;
    }

    public void setKod(int kod) {
        this.kod = kod;
    }

    public Kategoria getKategoria() {
        return kategoria;
    }

    public void setKategoria(Kategoria kategoria) {
        this.kategoria = kategoria;
    }

    public String getNev() {
        return nev;
    }

    public void setNev(String nev) {
        this.nev = nev;
    }

    public String getEgyseg() {
        return egyseg;
    }

    public void setEgyseg(String egyseg) {
        this.egyseg = egyseg;
    }

    public int getAr() {
        return ar;
    }

    public void setAr(int ar) {
        this.ar = ar;
    }

    public Eladas getEladas() {
        return eladas;
    }

    public void setEladas(Eladas eladas) {
        this.eladas = eladas;
    }
}
