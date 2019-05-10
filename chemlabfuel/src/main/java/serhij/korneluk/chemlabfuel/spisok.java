package serhij.korneluk.chemlabfuel;

import androidx.annotation.NonNull;

public class spisok implements Comparable {

    private final int data;
    private final String opisanie;
    private final String opisanieData;

    spisok(int data, String opisanie, String opisanieData) {
        this.data = data;
        this.opisanie = opisanie;
        this.opisanieData = opisanieData;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        spisok tmp = (spisok) o;
        if (this.data < tmp.data) {
            return -1;
        } else if (this.data > tmp.data) {
            return 1;
        }
        return 0;
    }
}
