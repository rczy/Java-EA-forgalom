package app.soap.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ARFOLYAM")
public class Arfolyam implements Serializable {
    @Id
    @Column(name = "datum")
    public String datum;
    @Id
    @Column(name = "deviza")
    public String deviza;
    @Column(name = "arfolyam")
    public Double arfolyam;

    public Arfolyam() {
    }

    public Arfolyam(String datum, String deviza, Double arfolyam) {
        this.datum = datum;
        this.deviza = deviza;
        this.arfolyam = arfolyam;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getDeviza() {
        return deviza;
    }

    public void setDeviza(String deviza) {
        this.deviza = deviza;
    }

    public Double getArfolyam() {
        return arfolyam;
    }

    public void setArfolyam(Double arfolyam) {
        this.arfolyam = arfolyam;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Arfolyam arfolyam1)) return false;
        return Objects.equals(datum, arfolyam1.datum) && Objects.equals(deviza, arfolyam1.deviza) && Objects.equals(arfolyam, arfolyam1.arfolyam);
    }

    @Override
    public int hashCode() {
        return Objects.hash(datum, deviza, arfolyam);
    }
}
