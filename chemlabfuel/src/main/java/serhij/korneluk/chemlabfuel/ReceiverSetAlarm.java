package serhij.korneluk.chemlabfuel;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class ReceiverSetAlarm extends BroadcastReceiver {

    private final ArrayList<String> testData = new ArrayList<>();
    private final ArrayList<ReaktiveSpisok> reaktiveSpisok = new ArrayList<>();
    private long toDataAlarm = 45L;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences fuel = context.getSharedPreferences("fuel", Context.MODE_PRIVATE);
        switch (fuel.getInt("notification", 0)) {
            case 0:
                toDataAlarm = 45L;
                break;
            case 1:
                toDataAlarm = 30L;
                break;
            case 2:
                toDataAlarm = 15L;
                break;
            case 3:
                toDataAlarm = 10L;
                break;
            case 4:
                toDataAlarm = 5L;
                break;
            case 5:
                toDataAlarm = 0L;
                break;
        }
        Task(context);
    }

    private void Task(Context context) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (isNetworkAvailable(context)) {
                testData.clear();
                if (CremLabFuel.InventorySpisok == null) {
                    CremLabFuel.InventorySpisok = new ArrayList<>();
                    mDatabase.child("equipments").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                if (data.getValue() instanceof HashMap) {
                                    HashMap hashMap = (HashMap) data.getValue();
                                    if (hashMap.size() > 12) {
                                        Object editedAt = hashMap.get("editedAt");
                                        Object editedBy = hashMap.get("editedBy");
                                        if (hashMap.get("editedAt") == null)
                                            editedAt = 0L;
                                        if (hashMap.get("editedBy") == null)
                                            editedBy = "";
                                        CremLabFuel.InventorySpisok.add(new InventorySpisok((String) hashMap.get("createdBy"), (long) hashMap.get("data01"), (String) hashMap.get("data02"), (String) hashMap.get("data03"), (String) hashMap.get("data04"), (String) hashMap.get("data05"), (String) hashMap.get("data06"), (String) hashMap.get("data07"), (String) hashMap.get("data08"), (String) hashMap.get("data09"), (String) hashMap.get("data10"), (long) hashMap.get("data11"), (String) hashMap.get("data12"), data.getKey(), (long) editedAt, (String) editedBy));
                                    }
                                }
                            }
                            checkAlarm(context);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

                } else {
                    checkAlarm(context);
                }

                mDatabase.child("reagents").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        reaktiveSpisok.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            String id = data.getKey();
                            GregorianCalendar g = (GregorianCalendar) Calendar.getInstance();
                            long srokToDay = g.getTimeInMillis();
                            long data05b = 0;
                            int srok = 1;// Срок в норме
                            for (DataSnapshot data2 : data.getChildren()) {
                                if (data2.getValue() instanceof HashMap) {
                                    HashMap hashMap = (HashMap) data2.getValue();
                                    if (hashMap.size() >= 12) {
                                        String data05 = (String) data2.child("data05").getValue();
                                        String[] d = data05.split("-");
                                        if (d.length == 3)
                                            g.set(Integer.parseInt(d[0]), Integer.parseInt(d[1]) - 1, Integer.parseInt(d[2]));
                                        else
                                            g.set(Integer.parseInt(d[0]), Integer.parseInt(d[1]) - 1, 1);
                                        g.add(Calendar.MONTH, (int) (long) data2.child("data06").getValue());
                                        data05b = g.getTimeInMillis();
                                        if (srokToDay < g.getTimeInMillis()) {
                                            g.add(Calendar.DATE, (int) -toDataAlarm);
                                            if (srokToDay > g.getTimeInMillis()) {
                                                srok = 0; // Истекает срок
                                            } else {
                                                srok = 1; // Срок в норме
                                            }
                                        } else {
                                            srok = -1; // Срок истёк
                                        }
                                    }
                                }
                            }
                            reaktiveSpisok.add(new ReaktiveSpisok(data05b, Integer.parseInt(id), srok));
                        }
                        checkAlarmReaktive(context);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        }
    }

    private void checkAlarmReaktive(Context context) {
        GregorianCalendar c = (GregorianCalendar) Calendar.getInstance();
        long realtime = c.getTimeInMillis();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 8, 0, 0);
        long time = c.getTimeInMillis();
        for (ReaktiveSpisok reaktiveSpisok : reaktiveSpisok) {
            removeAlarm(context, reaktiveSpisok.id * 100);
            if (toDataAlarm != 0L) {
                switch (reaktiveSpisok.check) {
                    case 1:
                        c.setTimeInMillis(reaktiveSpisok.data);
                        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 8, 0, 0);
                        c.add(Calendar.DATE, (int) -toDataAlarm);
                        setAlarm(context, c, reaktiveSpisok.id * 100, true);
                        break;
                    case 0:
                        if (realtime > time) {
                            if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
                                c.add(Calendar.DATE, 3);
                            else
                                c.add(Calendar.DATE, 1);
                        }
                        setAlarm(context, c, reaktiveSpisok.id * 100, true);
                        break;
                }
            }
        }
    }

    private void checkAlarm(Context context) {
        long timer = toDataAlarm * 24L * 60L * 60L * 1000L;
        GregorianCalendar c = (GregorianCalendar) Calendar.getInstance();
        long realtime = c.getTimeInMillis();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 8, 0, 0);
        long time = c.getTimeInMillis();
        for (InventorySpisok inventarny_spisok_datum : CremLabFuel.InventorySpisok) {
            int data01 = (int) inventarny_spisok_datum.data01;
            removeAlarm(context, data01);
            if (toDataAlarm != 0L) {
                String data08 = inventarny_spisok_datum.data08;
                if (data08 != null && !data08.equals("")) {
                    String[] t1 = data08.split("-");
                    c.set(Integer.parseInt(t1[0]), Integer.parseInt(t1[1]) - 1, Integer.parseInt(t1[2]), 8, 0, 0);
                    long timeset = c.getTimeInMillis();
                    long timeres = timeset - time;
                    if (timeres > -timer && timeres < timer) {
                        c.setTimeInMillis(time);
                        if (realtime > time) {
                            if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
                                c.add(Calendar.DATE, 3);
                            else
                                c.add(Calendar.DATE, 1);
                        }
                        setAlarm(context, c, data01, false);
                    } else if (timeres > timer) {
                        GregorianCalendar calendar = new GregorianCalendar();
                        String data09 = inventarny_spisok_datum.data09;
                        String data10 = inventarny_spisok_datum.data10;
                        if (data09 != null && !data09.equals("")) {
                            String[] t2 = data09.split("-");
                            calendar.set(Integer.parseInt(t2[0]), Integer.parseInt(t2[1]) - 1, Integer.parseInt(t2[2]));
                            long t2l = calendar.getTimeInMillis();
                            if (data10 != null && !data10.equals("")) {
                                String[] t3 = data10.split("-");
                                calendar.set(Integer.parseInt(t3[0]), Integer.parseInt(t3[1]) - 1, Integer.parseInt(t3[2]));
                                long t3l = calendar.getTimeInMillis();
                                if (t2l < t3l) {
                                    c.add(Calendar.DATE, (int) -toDataAlarm);
                                    setAlarm(context, c, data01, false);
                                }
                            }
                        } else {
                            c.add(Calendar.DATE, (int) -toDataAlarm);
                            setAlarm(context, c, data01, false);
                        }
                    }
                }
            }
        }
    }

    private void removeAlarm(Context context, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, requestCode, new Intent(context, ReceiverNotification.class), 0);
        alarmManager.cancel(pIntent);
    }

    private void setAlarm(Context context, GregorianCalendar c, int requestCode, boolean reaktive) {
        boolean testAlarm = true;
        if (!reaktive) {
            String testDataLocal = c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DATE);
            for (int i = 0; i < testData.size(); i++) {
                if (testData.get(i).contains(testDataLocal)) {
                    testAlarm = false;
                    break;
                }
            }
        }
        if (testAlarm) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, ReceiverNotification.class);
            intent.putExtra("reaktive", reaktive);
            PendingIntent pIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pIntent);
            }
        }
        testData.add(c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DATE));
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) { // connected to the internet
            // connected to wifi or mobile provider's
            return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            // not connected to the internet
            return false;
        }
    }
}
