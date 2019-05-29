package serhij.korneluk.chemlabfuel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CremLabFuel extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, Dialog_context_menu.Dialog_context_menu_Listener, Dialog_delite_confirm.Dialog_delite_confirm_listiner, Dialog_data.Dialog_data_listiner, Dialod_opisanie_edit_reakt.listUpdateListiner, ExpandableListView.OnChildClickListener {

    private ArrayList<String> inventarny_spisok = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private ListAdapterReakt arrayAdapter2;
    private ArrayList<ArrayList<String>> users = new ArrayList<>();
    private Dialod_opisanie_edit edit;
    private Dialod_opisanie_edit_reakt edit_reakt;
    Dialod_reakt_rasxod rasxod;
    private String userEdit;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    public static ArrayList<InventorySpisok> InventorySpisok = new ArrayList<>();
    public static LinkedHashMap<Integer, LinkedHashMap<Integer, LinkedHashMap<Integer, String>>> ReaktiveSpisok = new LinkedHashMap<>();
    private ArrayList<ReaktiveSpisok> spisokGroup = new ArrayList<>();
    private ArrayList<ArrayList<String>> spisokChild = new ArrayList<>();
    private TabHost tabHost;
    private ExpandableListView listView2;
    private String[] ed_izmerenia = {"кг.", "мг.", "л.", "мл."};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cremlabfuel);
        SharedPreferences fuel = getSharedPreferences("fuel", Context.MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.loading);
        ListView listView = findViewById(R.id.listView);
        arrayAdapter = new ListAdapter();
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        arrayAdapter2 = new ListAdapterReakt();
        listView2 = findViewById(R.id.listView2);
        listView2.setAdapter(arrayAdapter2);
        listView2.setOnChildClickListener(this);

        tabHost = findViewById(R.id.tabhost);
        tabHost.setup();
        TabHost.TabSpec tabSpec;
        tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setIndicator(getString(R.string.oborudovanie));
        tabSpec.setContent(R.id.tab1);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setIndicator(getString(R.string.reaktivy));
        tabSpec.setContent(R.id.tab2);
        tabHost.addTab(tabSpec);
        tabHost.setOnTabChangedListener(tabId -> {
            SharedPreferences.Editor editor = fuel.edit();
            if (tabId.contains("tag1"))
                editor.putBoolean("oborudovanie", true);
            else
                editor.putBoolean("oborudovanie", false);
            editor.apply();
            supportInvalidateOptionsMenu();
        });
        if (fuel.getBoolean("oborudovanie", true)) {
            tabHost.setCurrentTabByTag("tag1");
        } else {
            tabHost.setCurrentTabByTag("tag2");
        }
        setTollbarTheme();
        updateUI();
    }

    private void setTollbarTheme() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView title_toolbar = findViewById(R.id.title_toolbar);
        title_toolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        setSupportActionBar(toolbar);
        title_toolbar.setText(R.string.app_main);
    }

    @Override
    public void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.alphain, R.anim.alphaout);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.listView) {
            String fn = "";
            String ln = "";
            String fnG = "";
            String lnG = "";
            String zero = "";
            String zero2 = "";
            String editedString = "";
            if (!InventorySpisok.get(position).editedBy.equals("")) {
                long edited = InventorySpisok.get(position).editedAt;
                String editedBy = InventorySpisok.get(position).editedBy;
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).get(0).contains(editedBy)) {
                        fn = users.get(i).get(1);
                        ln = users.get(i).get(2);
                        break;
                    }
                }
                GregorianCalendar c = new GregorianCalendar();
                c.setTimeInMillis(edited);
                if (c.get(Calendar.DATE) < 10) zero = "0";
                if (c.get(Calendar.MONTH) < 9) zero2 = "0";
                editedString = " Изменено " + zero + c.get(Calendar.DATE) + "." + zero2 + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR) + " " + fn + " " + ln;
            }
            String createBy = InventorySpisok.get(position).createdBy;
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).get(0).contains(createBy)) {
                    fnG = users.get(i).get(1);
                    lnG = users.get(i).get(2);
                    break;
                }
            }
            String data02 = InventorySpisok.get(position).data02;
            String builder = "<strong>Марка, тип</strong><br>" + InventorySpisok.get(position).data03 + "<br><br>" +
                    "<strong>Заводской номер (инв. номер)</strong><br>" + InventorySpisok.get(position).data04 + "<br><br>" +
                    "<strong>Год выпуска (ввода в эксплуатацию)</strong><br>" + InventorySpisok.get(position).data05 + "<br><br>" +
                    "<strong>Периодичность метролог. аттестации, поверки, калибровки, мес.</strong><br>" + InventorySpisok.get(position).data06 + "<br><br>" +
                    "<strong>Дата последней аттестации, поверки, калибровки</strong><br>" + InventorySpisok.get(position).data07 + "<br><br>" +
                    "<strong>Дата следующей аттестации, поверки, калибровки</strong><br>" + InventorySpisok.get(position).data08 + "<br><br>" +
                    "<strong>Дата консервации</strong><br>" + InventorySpisok.get(position).data09 + "<br><br>" +
                    "<strong>Дата расконсервации</strong><br>" + InventorySpisok.get(position).data10 + "<br><br>" +
                    "<strong>Ответственный</strong><br>" + fnG + " " + lnG + "<br><br>" +
                    "<strong>Примечания</strong><br>" + InventorySpisok.get(position).data12 + editedString;
            Dialod_opisanie opisanie = Dialod_opisanie.getInstance(data02, builder);
            opisanie.show(getSupportFragmentManager(), "opisanie");
        }
    }

    private ArrayList<String> seash(int groupPosition, int childPosition) {
        ReaktiveSpisok GroupR = spisokGroup.get(groupPosition);
        String Group = GroupR.string;
        int t1 = spisokChild.get(groupPosition).get(childPosition).indexOf(".");
        String Child = spisokChild.get(groupPosition).get(childPosition).substring(0, t1);
        ArrayList<String> arrayList = new ArrayList<>();
        boolean end = false;
        for (Map.Entry<Integer, LinkedHashMap<Integer, LinkedHashMap<Integer, String>>> entry : CremLabFuel.ReaktiveSpisok.entrySet()) {
            LinkedHashMap<Integer, LinkedHashMap<Integer, String>> value = entry.getValue();
            for (Map.Entry<Integer, LinkedHashMap<Integer, String>> entry2 : value.entrySet()) {
                LinkedHashMap<Integer, String> value2 = entry2.getValue();
                arrayList.clear();
                for (Map.Entry<Integer, String> entry3 : value2.entrySet()) {
                    if (entry3.getKey() == 0)
                        arrayList.add(entry3.getValue());
                    if (entry3.getKey() == 1)
                        arrayList.add(entry3.getValue());
                    if (entry3.getKey() == 2)
                        arrayList.add(entry3.getValue());
                    if (entry3.getKey() == 3)
                        arrayList.add(entry3.getValue());
                    if (entry3.getKey() == 4)
                        arrayList.add(entry3.getValue());
                    if (entry3.getKey() == 5)
                        arrayList.add(entry3.getValue());
                    if (entry3.getKey() == 6)
                        arrayList.add(entry3.getValue());
                    if (entry3.getKey() == 7)
                        arrayList.add(entry3.getValue());
                    if (entry3.getKey() == 8)
                        arrayList.add(entry3.getValue());
                    if (entry3.getKey() == 9)
                        arrayList.add(entry3.getValue());
                    if (entry3.getKey() == 10)
                        arrayList.add(entry3.getValue());
                    if (entry3.getKey() == 11)
                        arrayList.add(entry3.getValue());
                    if (entry3.getKey() == 12)
                        arrayList.add(entry3.getValue());
                    if (entry3.getKey() == 13)
                        arrayList.add(entry3.getValue());
                    if (entry3.getKey() == 14)
                        arrayList.add(entry3.getValue());
                    if (entry3.getKey() == 15)
                        arrayList.add(entry3.getValue());
                    if (entry3.getKey() == 16)
                        arrayList.add(entry3.getValue());
                    if (entry3.getKey() == 17)
                        arrayList.add(entry3.getValue());
                }
                if (Group.contains(arrayList.get(13)) && Child.contains(arrayList.get(15))) {
                    arrayList.add(String.valueOf(entry.getKey()));
                    end = true;
                    break;
                }
            }
            if (end)
                break;
        }
        return arrayList;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        ArrayList<String> arrayList = seash(groupPosition, childPosition);
        String fn = "";
        String ln = "";
        String fnG = "";
        String lnG = "";
        String zero = "";
        String zero2 = "";
        String editedString = "";
        if (!arrayList.get(12).equals("")) {
            long edited = Long.parseLong(arrayList.get(11));
            String editedBy = arrayList.get(12);
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).get(0).contains(editedBy)) {
                    fn = users.get(i).get(1);
                    ln = users.get(i).get(2);
                    break;
                }
            }
            GregorianCalendar c = new GregorianCalendar();
            c.setTimeInMillis(edited);
            if (c.get(Calendar.DATE) < 10) zero = "0";
            if (c.get(Calendar.MONTH) < 9) zero2 = "0";
            editedString = zero + c.get(Calendar.DATE) + "." + zero2 + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR) + " " + fn + " " + ln;
        }
        String createBy = arrayList.get(0);
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).get(0).contains(createBy)) {
                fnG = users.get(i).get(1);
                lnG = users.get(i).get(2);
                break;
            }
        }
        String[] izmerenie = {"Килограмм", "Миллиграмм", "Литры", "Миллилитры"};
        String data02 = arrayList.get(13);
        String builder = "<strong>Партия</strong><br>" + arrayList.get(15) + "<br><br>" +
                "<strong>Дата получения</strong><br>" + arrayList.get(1) + "<br><br>" +
                "<strong>Поставщик</strong><br>" + arrayList.get(2) + "<br><br>" +
                "<strong>Притензии</strong><br>" + arrayList.get(3) + "<br><br>" +
                "<strong>Квалификация</strong><br>" + arrayList.get(4) + "<br><br>" +
                "<strong>Партия</strong><br>" + arrayList.get(17) + "<br><br>" +
                "<strong>Дата изготовления</strong><br>" + arrayList.get(5) + "<br><br>" +
                "<strong>Срок хранения</strong><br>" + arrayList.get(6) + "<br><br>" +
                "<strong>Условия хранения</strong><br>" + arrayList.get(7) + "<br><br>" +
                "<strong>Единица измерения</strong><br>" + izmerenie[Integer.parseInt(arrayList.get(8))] + "<br><br>" +
                "<strong>Количество на остатке</strong><br>" + arrayList.get(9).replace(".", ",") + "<br><br>" +
                "<strong>Минимальное количество</strong><br>" + arrayList.get(10).replace(".", ",") + "<br><br>" +
                "<strong>Ответственный</strong><br>" + fnG + " " + lnG + "<br><br>" +
                "<strong>Изменено</strong><br>" + editedString + "<br><br>" +
                "<strong>Журнал расхода</strong><br>" + arrayList.get(16).replace("\n", "<br>");
        Dialod_opisanie opisanie = Dialod_opisanie.getInstance(data02, builder);
        opisanie.show(getSupportFragmentManager(), "opisanie");
        return false;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Dialog_context_menu menu = Dialog_context_menu.getInstance(position, InventorySpisok.get(position).data02);
        menu.show(getSupportFragmentManager(), "menu");
        return true;
    }

    @Override
    public void UpdateList() {
        arrayAdapter2.notifyDataSetChanged();
    }

    @Override
    public void onDialogEditPosition(int position) {
        if (isNetworkAvailable()) {
            edit = Dialod_opisanie_edit.getInstance(userEdit, InventorySpisok.get(position).uid, InventorySpisok.get(position).data02, InventorySpisok.get(position).data03, InventorySpisok.get(position).data04, InventorySpisok.get(position).data05, InventorySpisok.get(position).data06, InventorySpisok.get(position).data07, InventorySpisok.get(position).data08, InventorySpisok.get(position).data09, InventorySpisok.get(position).data10, InventorySpisok.get(position).data12);
            edit.show(getSupportFragmentManager(), "edit");
        } else {
            Dialog_no_internet internet = new Dialog_no_internet();
            internet.show(getSupportFragmentManager(), "internet");
        }
    }

    @Override
    public void onDialogDeliteClick(int position) {
        if (isNetworkAvailable()) {
            Dialog_delite_confirm confirm = Dialog_delite_confirm.getInstance(InventorySpisok.get(position).data02, -1, position);
            confirm.show(getSupportFragmentManager(), "confirm");
        } else {
            Dialog_no_internet internet = new Dialog_no_internet();
            internet.show(getSupportFragmentManager(), "internet");
        }
    }

    @Override
    public void delite_data(int groupPosition, int position) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        if (groupPosition == -1) {
            mDatabase.child("equipments").child(InventorySpisok.get(position).uid).removeValue();
        } else {
            ArrayList<String> arrayList = seash(groupPosition, position);
            if (ReaktiveSpisok.get(Integer.parseInt(arrayList.get(18))).size() == 1)
                mDatabase.child("reagents").child(arrayList.get(14)).removeValue();
            else
                mDatabase.child("reagents").child(arrayList.get(14)).child(arrayList.get(15)).removeValue();
        }
    }

    @Override
    public void set_data(int textview, int year, int month, int dayOfMonth) {
        if (edit != null)
            edit.set_data(textview, year, month, dayOfMonth);
        if (edit_reakt != null)
            edit_reakt.set_data(textview, year, month, dayOfMonth);
        if (rasxod != null)
            rasxod.set_data(textview, year, month, dayOfMonth);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater infl = getMenuInflater();
        infl.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null)
            menu.findItem(R.id.exit).setVisible(false);
        else
            menu.findItem(R.id.exit).setVisible(true);
        SharedPreferences fuel = getSharedPreferences("fuel", Context.MODE_PRIVATE);
        int sort = fuel.getInt("sort", 0);
        if (sort == 0) {
            menu.findItem(R.id.sortdate).setChecked(false);
            menu.findItem(R.id.sorttime).setChecked(false);
        }
        if (sort == 1) {
            menu.findItem(R.id.sortdate).setChecked(true);
            menu.findItem(R.id.sorttime).setChecked(false);
        }
        if (sort == 2) {
            menu.findItem(R.id.sorttime).setChecked(true);
            menu.findItem(R.id.sortdate).setChecked(false);
        }
        if (fuel.getBoolean("oborudovanie", true)) {
            menu.findItem(R.id.add).setVisible(true);
            menu.findItem(R.id.add_reakt).setVisible(false);
        } else {
            menu.findItem(R.id.add_reakt).setVisible(true);
            menu.findItem(R.id.add).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add) {
            if (isNetworkAvailable()) {
                edit = Dialod_opisanie_edit.getInstance(userEdit, (long) InventorySpisok.size());
                edit.show(getSupportFragmentManager(), "edit");
                edit_reakt = null;
                rasxod = null;
            } else {
                Dialog_no_internet internet = new Dialog_no_internet();
                internet.show(getSupportFragmentManager(), "internet");
            }
        }
        if (id == R.id.add_reakt) {
            if (isNetworkAvailable()) {
                edit_reakt = Dialod_opisanie_edit_reakt.getInstance(userEdit, "", "", 0);
                edit_reakt.show(getSupportFragmentManager(), "edit");
                edit = null;
                rasxod = null;
            } else {
                Dialog_no_internet internet = new Dialog_no_internet();
                internet.show(getSupportFragmentManager(), "internet");
            }
        }
        if (id == R.id.exit) {
            mAuth.signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        if (id == R.id.sortdate) {
            SharedPreferences fuel = getSharedPreferences("fuel", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = fuel.edit();
            if (item.isChecked()) {
                editor.putInt("sort", 0);
                editor.apply();
                Collections.sort(InventorySpisok, new InventorySpisokSort(this));
                Collections.sort(spisokGroup, new ReaktiveSpisokSort(this));
            } else {
                editor.putInt("sort", 1);
                editor.apply();
                Collections.sort(InventorySpisok, new InventorySpisokSort(this));
                Collections.sort(spisokGroup, new ReaktiveSpisokSort(this));
            }
            inventarny_spisok.clear();
            for (InventorySpisok inventorySpisok : InventorySpisok) {
                inventarny_spisok.add(inventorySpisok.data01 + ". " + inventorySpisok.data02);
            }
            arrayAdapter.notifyDataSetChanged();
            arrayAdapter2.notifyDataSetChanged();
            supportInvalidateOptionsMenu();
        }
        if (id == R.id.sorttime) {
            SharedPreferences fuel = getSharedPreferences("fuel", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = fuel.edit();
            if (item.isChecked()) {
                editor.putInt("sort", 0);
                editor.apply();
                Collections.sort(InventorySpisok, new InventorySpisokSort(this));
                Collections.sort(spisokGroup, new ReaktiveSpisokSort(this));
            } else {
                editor.putInt("sort", 2);
                editor.apply();
                Collections.sort(InventorySpisok, new InventorySpisokSort(this));
                Collections.sort(spisokGroup, new ReaktiveSpisokSort(this));
            }
            inventarny_spisok.clear();
            for (InventorySpisok inventorySpisok : InventorySpisok) {
                inventarny_spisok.add(inventorySpisok.data01 + ". " + inventorySpisok.data02);
            }
            arrayAdapter.notifyDataSetChanged();
            arrayAdapter2.notifyDataSetChanged();
            supportInvalidateOptionsMenu();
        }
        if (id == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUI() {
        progressBar.setVisibility(View.VISIBLE);
        supportInvalidateOptionsMenu();
        if (isNetworkAvailable()) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("equipments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    InventorySpisok.clear();
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
                                InventorySpisok.add(new InventorySpisok((String) hashMap.get("createdBy"), (long) hashMap.get("data01"), (String) hashMap.get("data02"), (String) hashMap.get("data03"), (String) hashMap.get("data04"), (String) hashMap.get("data05"), (String) hashMap.get("data06"), (String) hashMap.get("data07"), (String) hashMap.get("data08"), (String) hashMap.get("data09"), (String) hashMap.get("data10"), (long) hashMap.get("data11"), (String) hashMap.get("data12"), data.getKey(), (long) editedAt, (String) editedBy));
                            }
                        }
                    }
                    Collections.sort(InventorySpisok, new InventorySpisokSort(CremLabFuel.this));
                    inventarny_spisok.clear();
                    for (InventorySpisok inventorySpisok : InventorySpisok) {
                        inventarny_spisok.add(inventorySpisok.data01 + ". " + inventorySpisok.data02);
                    }
                    arrayAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                    sendBroadcast(new Intent(CremLabFuel.this, ReceiverSetAlarm.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            mDatabase.child("reagents").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ReaktiveSpisok.clear();
                    spisokGroup.clear();
                    spisokChild.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        String name = (String) data.child("name").getValue();
                        if (name == null)
                            name = "";
                        String id = data.getKey();
                        BigDecimal ostatokSum = BigDecimal.ZERO;
                        BigDecimal minostatok = BigDecimal.ZERO;
                        LinkedHashMap<Integer, LinkedHashMap<Integer, String>> spisokN = new LinkedHashMap<>();
                        ArrayList<String> partia = new ArrayList<>();
                        GregorianCalendar g = (GregorianCalendar) Calendar.getInstance();
                        long srokToDay = g.getTimeInMillis();
                        long data05b = 0;
                        int data08 = 0;
                        for (DataSnapshot data2 : data.getChildren()) {
                            String srok = "";
                            int i = 0;
                            if (data2.getValue() instanceof HashMap) {
                                HashMap hashMap = (HashMap) data2.getValue();
                                if (hashMap.size() >= 12) {
                                    LinkedHashMap<Integer, String> spisoks = new LinkedHashMap<>();
                                    Object editedAt = data2.child("editedAt").getValue();
                                    Object editedBy = data2.child("editedBy").getValue();
                                    if (editedAt == null)
                                        editedAt = 0L;
                                    if (editedBy == null)
                                        editedBy = "";
                                    Object data11 = data2.child("data11").getValue();
                                    if (data11 == null)
                                        data11 = "";
                                    Object data12 = data2.child("data12").getValue();
                                    if (data12 == null)
                                        data12 = "";
                                    spisoks.put(i, (String) data2.child("createdBy").getValue());//0
                                    i++;
                                    spisoks.put(i, (String) data2.child("data01").getValue());//1
                                    i++;
                                    spisoks.put(i, (String) data2.child("data02").getValue());//2
                                    i++;
                                    spisoks.put(i, (String) data2.child("data03").getValue());//3
                                    i++;
                                    spisoks.put(i, (String) data2.child("data04").getValue());//4
                                    i++;
                                    spisoks.put(i, (String) data2.child("data05").getValue());//5
                                    i++;
                                    spisoks.put(i, String.valueOf(data2.child("data06").getValue()));//6
                                    i++;
                                    spisoks.put(i, (String) data2.child("data07").getValue());//7
                                    i++;
                                    spisoks.put(i, String.valueOf(data2.child("data08").getValue()));//8
                                    i++;
                                    spisoks.put(i, String.valueOf(data2.child("data09").getValue()));//9
                                    i++;
                                    spisoks.put(i, String.valueOf(data2.child("data10").getValue()));//10
                                    i++;
                                    spisoks.put(i, String.valueOf(editedAt));//11
                                    i++;
                                    spisoks.put(i, (String) editedBy);//12
                                    i++;
                                    spisoks.put(i, name);//13
                                    i++;
                                    spisoks.put(i, id);//14
                                    i++;
                                    spisoks.put(i, data2.getKey());//15
                                    i++;
                                    spisoks.put(i, (String) data11);//16
                                    i++;
                                    spisoks.put(i, String.valueOf(data12));//17
                                    spisokN.put(Integer.parseInt(data2.getKey()), spisoks);

                                    String data05 = (String) data2.child("data05").getValue();
                                    String[] d = data05.split("-");
                                    if (d.length == 3)
                                        g.set(Integer.parseInt(d[0]), Integer.parseInt(d[1]) - 1, Integer.parseInt(d[2]));
                                    else
                                        g.set(Integer.parseInt(d[0]), Integer.parseInt(d[1]) - 1, 1);
                                    data05b = g.getTimeInMillis();
                                    g.add(Calendar.MONTH, (int) (long) data2.child("data06").getValue());
                                    BigDecimal ostatok;
                                    if (data2.child("data09").getValue() instanceof Double)
                                        ostatok = BigDecimal.valueOf((double) data2.child("data09").getValue());
                                    else
                                        ostatok = BigDecimal.valueOf((double) (long) data2.child("data09").getValue());
                                    if (srokToDay < g.getTimeInMillis()) {
                                        ostatokSum = ostatokSum.add(ostatok);
                                        g.add(Calendar.DATE, -45);
                                        if (srokToDay > g.getTimeInMillis())
                                            srok = " <font color=#9a2828>Истекает срок</font>";
                                    } else {
                                        srok = " <font color=#9a2828>Срок истёк</font>";
                                    }
                                    if (data2.child("data10").getValue() instanceof Double)
                                        minostatok = BigDecimal.valueOf((double) data2.child("data10").getValue());
                                    else
                                        minostatok = BigDecimal.valueOf((double) (long) data2.child("data10").getValue());
                                    data08 = (int) (long) data2.child("data08").getValue();
                                    partia.add(data2.getKey() + "." + srok + " <!---->Остаток: " + ostatok.toString().replace(".", ",") + " " + ed_izmerenia[data08]);
                                }
                            }
                        }
                        spisokGroup.add(new ReaktiveSpisok(data05b, Integer.parseInt(id), name, ostatokSum, minostatok, data08));
                        spisokChild.add(partia);
                        ReaktiveSpisok.put(Integer.parseInt(id), spisokN);
                    }
                    if (getIntent().getExtras() != null) {
                        if (getIntent().getExtras().getBoolean("reaktive", false)) {
                            tabHost.setCurrentTabByTag("tag2");
                            for ( int i = 0; i < arrayAdapter2.getGroupCount(); i++ ) {
                                listView2.expandGroup(i);
                            }
                        }
                    }
                    Collections.sort(spisokGroup, new ReaktiveSpisokSort(CremLabFuel.this));
                    arrayAdapter2.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                    sendBroadcast(new Intent(CremLabFuel.this, ReceiverSetAlarm.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            mDatabase.child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        String key = data.getKey();
                        if (mAuth.getUid().contains(key)) {
                            userEdit = key;
                        }
                        for (DataSnapshot data2 : data.getChildren()) {
                            if (data2.getValue() instanceof HashMap) {
                                HashMap hashMap = (HashMap) data2.getValue();
                                String firstname = (String) hashMap.get("firstName");
                                String lastname = (String) hashMap.get("lastName");
                                ArrayList<String> user = new ArrayList<>();
                                user.add(key);
                                user.add(firstname);
                                user.add(lastname);
                                users.add(user);
                            }
                        }
                    }
                    Gson gson = new Gson();
                    SharedPreferences fuel = getSharedPreferences("fuel", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = fuel.edit();
                    editor.putString("users", gson.toJson(users));
                    editor.apply();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } else {
            SharedPreferences fuel = getSharedPreferences("fuel", Context.MODE_PRIVATE);
            String g = fuel.getString("fuel_data", "");
            if (!g.equals("")) {
                Gson gson = new Gson();
                Type type = new TypeToken<InventorySpisok[]>() {
                }.getType();
                InventorySpisok = gson.fromJson(g, type);
                for (InventorySpisok inventorySpisok : InventorySpisok) {
                    inventarny_spisok.add(inventorySpisok.data01 + ". " + inventorySpisok.data02);
                }
                String us = fuel.getString("users", "");
                Type type2 = new TypeToken<ArrayList<ArrayList<String>>>() {
                }.getType();
                users.addAll(gson.fromJson(us, type2));
                arrayAdapter.notifyDataSetChanged();
                Intent intent = new Intent(CremLabFuel.this, ReceiverSetAlarm.class);
                sendBroadcast(intent);
            } else {
                Dialog_no_internet internet = new Dialog_no_internet();
                internet.show(getSupportFragmentManager(), "internet");
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) { // connected to the internet
            // connected to wifi or mobile provider's
            return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            // not connected to the internet
            return false;
        }
    }

    private class ListAdapter extends ArrayAdapter<String> {

        private SharedPreferences fuel;

        ListAdapter() {
            super(CremLabFuel.this, R.layout.simple_list_item, inventarny_spisok);
            fuel = getSharedPreferences("fuel", Context.MODE_PRIVATE);
        }

        @NonNull
        @Override
        public View getView(int position, View mView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (mView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mView = vi.inflate(R.layout.simple_list_item, parent, false);
                viewHolder = new ViewHolder();
                mView.setTag(viewHolder);
                viewHolder.text = mView.findViewById(R.id.label);
                viewHolder.button_popup = mView.findViewById(R.id.button_popup);
            } else {
                viewHolder = (ViewHolder) mView.getTag();
            }
            viewHolder.button_popup.setOnClickListener((v -> showPopupMenu(viewHolder.button_popup, position)));
            GregorianCalendar g = (GregorianCalendar) Calendar.getInstance();
            long real = g.getTimeInMillis();
            String dataLong = "";
            String data8 = InventorySpisok.get(position).data08;
            if (data8 != null && !data8.equals("")) {
                String[] t1 = data8.split("-");
                GregorianCalendar calendar = new GregorianCalendar();
                String data09 = InventorySpisok.get(position).data09;
                String data10 = InventorySpisok.get(position).data10;
                if (data09 != null && !data09.equals("")) {
                    String[] t2 = data09.split("-");
                    calendar.set(Integer.parseInt(t2[0]), Integer.parseInt(t2[1]) - 1, Integer.parseInt(t2[2]));
                    long t2l = calendar.getTimeInMillis();
                    if (data10 != null && !data10.equals("")) {
                        String[] t3 = data10.split("-");
                        calendar.set(Integer.parseInt(t3[0]), Integer.parseInt(t3[1]) - 1, Integer.parseInt(t3[2]));
                        long t3l = calendar.getTimeInMillis();
                        if (t2l < t3l) {
                            g.set(Integer.parseInt(t1[0]), Integer.parseInt(t1[1]) - 1, Integer.parseInt(t1[2]));
                            if (g.getTimeInMillis() - real > 0L && g.getTimeInMillis() - real < 45L * 24L * 60L * 60L * 1000L) {
                                long dat = g.getTimeInMillis() - real;
                                g.setTimeInMillis(dat);
                                dataLong = "<br><font color=#9a2828>Осталось " + (g.get(Calendar.DAY_OF_YEAR) - 1) + " дней(-я)</font>";
                            } else if (g.getTimeInMillis() - real < 0L) {
                                dataLong = "<br><font color=#9a2828>Просрочено</font>";
                            }
                        }
                    }
                } else {
                    g.set(Integer.parseInt(t1[0]), Integer.parseInt(t1[1]) - 1, Integer.parseInt(t1[2]));
                    if (g.getTimeInMillis() - real > 0L && g.getTimeInMillis() - real < 45L * 24L * 60L * 60L * 1000L) {
                        long dat = g.getTimeInMillis() - real;
                        g.setTimeInMillis(dat);
                        dataLong = "<br><font color=#9a2828>Осталось " + (g.get(Calendar.DAY_OF_YEAR) - 1) + " дней(-я)</font>";
                    } else if (g.getTimeInMillis() - real < 0L) {
                        dataLong = "<br><font color=#9a2828>Просрочено</font>";
                    }
                }
            }
            viewHolder.text.setText(Html.fromHtml(inventarny_spisok.get(position) + dataLong));
            viewHolder.text.setTextSize(fuel.getInt("fontsize", 18));
            return mView;
        }

        private void showPopupMenu(View view, int position) {
            PopupMenu popup = new PopupMenu(CremLabFuel.this, view);
            MenuInflater infl = popup.getMenuInflater();
            infl.inflate(R.menu.popup, popup.getMenu());
            for (int i = 0; i < popup.getMenu().size(); i++) {
                MenuItem item = popup.getMenu().getItem(i);
                SpannableString spanString = new SpannableString(popup.getMenu().getItem(i).getTitle().toString());
                int end = spanString.length();
                spanString.setSpan(new AbsoluteSizeSpan(18, true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                item.setTitle(spanString);
            }
            popup.setOnMenuItemClickListener(menuItem -> {
                popup.dismiss();
                if (menuItem.getItemId() == R.id.menu_redoktor) {
                    onDialogEditPosition(position);
                    return true;
                }
                if (menuItem.getItemId() == R.id.menu_remove) {
                    onDialogDeliteClick(position);
                    return true;
                }
                return false;
            });
            popup.show();
        }
    }

    private class ListAdapterReakt extends BaseExpandableListAdapter {

        private SharedPreferences fuel;

        ListAdapterReakt() {
            fuel = getSharedPreferences("fuel", Context.MODE_PRIVATE);
        }

        @Override
        public int getGroupCount() {
            return spisokGroup.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return spisokChild.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return spisokGroup.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return spisokChild.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            ViewHolderGroup group;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.group_view, parent, false);
                group = new ViewHolderGroup();
                convertView.setTag(group);
                group.text = convertView.findViewById(R.id.textGroup);
            } else {
                group = (ViewHolderGroup) convertView.getTag();
            }
            group.text.setTextSize(fuel.getInt("fontsize", 18));
            String ostatok = " (Остаток: " + spisokGroup.get(groupPosition).ostatok.toString().replace(".", ",") + " " + ed_izmerenia[spisokGroup.get(groupPosition).ed_izmerenia] + ")";
            int compare = spisokGroup.get(groupPosition).ostatok.compareTo(spisokGroup.get(groupPosition).minostatok);
            if (spisokGroup.get(groupPosition).ostatok.equals(BigDecimal.ZERO))
                ostatok = " <font color=#9a2828>Срок истёк</font>";
            else if (compare <= 0)
                ostatok = " (<font color=#9a2828>Остаток: " + spisokGroup.get(groupPosition).ostatok.toString().replace(".", ",") + " " + ed_izmerenia[spisokGroup.get(groupPosition).ed_izmerenia] +  "</font>)";
            group.text.setText(Html.fromHtml(spisokGroup.get(groupPosition).id + ". " + spisokGroup.get(groupPosition).string + ostatok));
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.simple_list_item3, parent, false);
                viewHolder = new ViewHolder();
                convertView.setTag(viewHolder);
                viewHolder.text = convertView.findViewById(R.id.label);
                viewHolder.button_popup = convertView.findViewById(R.id.button_popup);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.button_popup.setOnClickListener((v -> showPopupMenu(viewHolder.button_popup, groupPosition, childPosition)));
            viewHolder.text.setText(Html.fromHtml(spisokChild.get(groupPosition).get(childPosition)));
            viewHolder.text.setTextSize(fuel.getInt("fontsize", 18));
            return convertView;
        }

        private void showPopupMenu(View view, int groupPosition, int childposition) {
            PopupMenu popup = new PopupMenu(CremLabFuel.this, view);
            MenuInflater infl = popup.getMenuInflater();
            infl.inflate(R.menu.popup_reaktive, popup.getMenu());
            for (int i = 0; i < popup.getMenu().size(); i++) {
                MenuItem item = popup.getMenu().getItem(i);
                SpannableString spanString = new SpannableString(popup.getMenu().getItem(i).getTitle().toString());
                int end = spanString.length();
                spanString.setSpan(new AbsoluteSizeSpan(18, true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                item.setTitle(spanString);
            }
            popup.setOnMenuItemClickListener(menuItem -> {
                popup.dismiss();
                if (menuItem.getItemId() == R.id.menu_add) {
                    edit_reakt = Dialod_opisanie_edit_reakt.getInstance(userEdit, spisokGroup.get(groupPosition).string, spisokGroup.get(groupPosition).minostatok.toString(), spisokGroup.get(groupPosition).ed_izmerenia);
                    edit_reakt.show(getSupportFragmentManager(), "edit");
                    edit = null;
                    rasxod = null;
                    return true;
                }
                if (menuItem.getItemId() == R.id.menu_minus) {
                    ArrayList<String> arrayList = seash(groupPosition, childposition);
                    rasxod = Dialod_reakt_rasxod.getInstance(Integer.parseInt(arrayList.get(14)), Integer.parseInt(arrayList.get(15)), Integer.parseInt(arrayList.get(8)));
                    rasxod.show(getSupportFragmentManager(), "rasxod");
                    edit = null;
                    edit_reakt = null;
                }
                if (menuItem.getItemId() == R.id.menu_redoktor) {
                    ArrayList<String> arrayList = seash(groupPosition, childposition);
                    edit_reakt = Dialod_opisanie_edit_reakt.getInstance(userEdit, Integer.parseInt(arrayList.get(14)), Integer.parseInt(arrayList.get(15)));
                    edit_reakt.show(getSupportFragmentManager(), "edit");
                    edit = null;
                    rasxod = null;
                    return true;
                }
                if (menuItem.getItemId() == R.id.menu_remove) {
                    String spisokGroupSt = spisokChild.get(groupPosition).get(childposition);
                    int t1 = spisokGroupSt.indexOf(" <");
                    if (t1 != -1)
                        spisokGroupSt = spisokGroupSt.substring(0, t1);
                    Dialog_delite_confirm confirm = Dialog_delite_confirm.getInstance(spisokGroup.get(groupPosition).string + " " + spisokGroupSt, groupPosition, childposition);
                    confirm.show(getSupportFragmentManager(), "confirm");
                    return true;
                }
                return false;
            });
            popup.show();
        }
    }

    private static class ViewHolderGroup {
        TextView text;
    }

    private static class ViewHolder {
        TextView text;
        ImageView button_popup;
    }
}
