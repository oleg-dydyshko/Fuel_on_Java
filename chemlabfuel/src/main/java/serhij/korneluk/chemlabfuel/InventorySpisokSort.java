package serhij.korneluk.chemlabfuel;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Comparator;

public class InventorySpisokSort implements Comparator<InventorySpisok> {

    private final SharedPreferences fuel;

    InventorySpisokSort(Context context) {
        fuel = context.getSharedPreferences("fuel", Context.MODE_PRIVATE);
    }

    @Override
    public int compare(InventorySpisok o1, InventorySpisok o2) {
        int sort = fuel.getInt("sort", 0);
        if (sort == 1)
            return o1.data02.toLowerCase().compareTo(o2.data02.toLowerCase());
        if (sort == 2) {
            if (o1.data11 < o2.data11) {
                return -1;
            } else if (o1.data11 > o2.data11) {
                return 1;
            }
            return 0;
        }
        if (sort == 0) {
            String zero = "";
            if (o1.data01 < 10)
                zero = "0";
            String zeroO = "";
            if (o2.data01 < 10)
                zeroO = "0";
            return (zero + o1.data01 + ". " + o1.data02.toLowerCase()).compareTo(zeroO + o2.data01 + ". " + o2.data02.toLowerCase());
        }
        return 0;
    }
}
