package serhij.korneluk.chemlabfuel;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Comparator;

public class ReaktiveSpisokSort implements Comparator<ReaktiveSpisok> {

    private SharedPreferences fuel;

    ReaktiveSpisokSort(Context context) {
        fuel = context.getSharedPreferences("fuel", Context.MODE_PRIVATE);
    }

    @Override
    public int compare(ReaktiveSpisok o1, ReaktiveSpisok o2) {
        int sort = fuel.getInt("sort", 0);
        if (sort == 1)
            return o1.string.toLowerCase().compareTo(o2.string.toLowerCase());
        if (sort == 2) {
            if (o1.data < o2.data) {
                return -1;
            } else if (o1.data > o2.data) {
                return 1;
            }
            return 0;
        }
        if (sort == 0) {
            String zero = "";
            if (o1.id < 10)
                zero = "0";
            String zeroO = "";
            if (o2.id < 10)
                zeroO = "0";
            return (zero + o1.id + ". " + o1.string.toLowerCase()).compareTo(zeroO + o2.id + ". " + o2.string.toLowerCase());
        }
        return 0;
    }
}
