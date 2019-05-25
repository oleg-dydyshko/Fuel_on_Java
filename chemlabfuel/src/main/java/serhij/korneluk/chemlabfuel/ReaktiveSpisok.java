package serhij.korneluk.chemlabfuel;

import java.math.BigDecimal;

class ReaktiveSpisok {

    final long data;
    final int id;
    final String string;
    final BigDecimal ostatok;
    final BigDecimal minostatok;

    ReaktiveSpisok(long data, int id, String string, BigDecimal ostatok, BigDecimal minostatok) {
        this.data = data;
        this.id = id;
        this.string = string;
        this.ostatok = ostatok;
        this.minostatok =minostatok;
    }
}
