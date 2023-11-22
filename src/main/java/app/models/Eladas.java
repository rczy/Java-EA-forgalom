package app.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ELADAS")
public class Eladas implements Serializable {
    @Id
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "aru_kod")
    public Aru aru;

    @Column(name = "mennyiseg")
    public int mennyiseg;

    public Eladas() {
    }

    public Eladas(Aru aru, int mennyiseg) {
        this.aru = aru;
        this.mennyiseg = mennyiseg;
    }

    public Aru getAru() {
        return aru;
    }

    public void setAru(Aru aru) {
        this.aru = aru;
    }

    public int getMennyiseg() {
        return mennyiseg;
    }

    public void setMennyiseg(int mennyiseg) {
        this.mennyiseg = mennyiseg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Eladas eladas)) return false;
        return mennyiseg == eladas.mennyiseg && Objects.equals(aru, eladas.aru);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aru, mennyiseg);
    }
}
