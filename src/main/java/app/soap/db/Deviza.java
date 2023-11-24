package app.soap.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "DEVIZA")
public class Deviza {
    @Id
    @Column(name = "kod")
    public String kod;

    public Deviza() {
    }

    public Deviza(String kod) {
        this.kod = kod;
    }

    public String getKod() {
        return kod;
    }

    public void setKod(String kod) {
        this.kod = kod;
    }
}
