package serhij.korneluk.chemlabfuel;

import java.math.BigDecimal;

class ReaktiveSpisok {

    final long data;
    final int id;
    final String string;
    final BigDecimal ostatok;
    final BigDecimal minostatok;
    final int ed_izmerenia;
    final int check;

    ReaktiveSpisok(long data, int id, String string, BigDecimal ostatok, BigDecimal minostatok, int ed_izmerenia) {
        this.data = data;
        this.id = id;
        this.string = string;
        this.ostatok = ostatok;
        this.minostatok = minostatok;
        this.ed_izmerenia = ed_izmerenia;
        this.check = 1;
    }

    ReaktiveSpisok(long data, int id, int check) {
        this.data = data;
        this.id = id;
        this.string = "";
        this.ostatok = null;
        this.minostatok = null;
        this.ed_izmerenia = 0;
        this.check = check;
    }
}
