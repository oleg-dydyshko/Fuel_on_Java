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
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, Dialog_context_menu.Dialog_context_menu_Listener, Dialog_delite_confirm.Dialog_delite_confirm_listiner, Dialog_data.Dialog_data_listiner {

    private FirebaseAuth mAuth;
    private EditText useremail;
    private EditText userpass;
    private Button login;
    private String email;
    private String password;
    private ListView listView;
    private ProgressBar progressBar;
    private ArrayList<String> inventarny_spisok = new ArrayList<>();
    private ArrayList<HashMap> inventarny_spisok_data = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<ArrayList<String>> users = new ArrayList<>();
    private ArrayList<ArrayList<Long>> notifications = new ArrayList<>();
    private TextView textView;
    private Dialod_opisanie_edit edit;
    private String userEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        useremail = findViewById(R.id.username);
        userpass = findViewById(R.id.password);
        login = findViewById(R.id.login);
        listView = findViewById(R.id.listView);
        progressBar = findViewById(R.id.loading);
        arrayAdapter = new ListAdapter(this);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        login.setOnClickListener(v -> {
            // Скрываем клавиатуру
            InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm1 != null) {
                imm1.hideSoftInputFromWindow(useremail.getWindowToken(), 0);
            }
            email = useremail.getText().toString();
            password = userpass.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user1 = mAuth.getCurrentUser();
                    updateUI(user1);
                } else {
                    LinearLayout layout = new LinearLayout(this);
                    layout.setBackgroundResource(R.color.colorPrimary);
                    TextView toast = new TextView(this);
                    toast.setTextColor(getResources().getColor(R.color.colorIcons));
                    toast.setPadding(10, 10, 10, 10);
                    toast.setText("Неверный логин или пароль");
                    toast.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    layout.addView(toast);
                    Toast mes = new Toast(this);
                    mes.setDuration(Toast.LENGTH_LONG);
                    mes.setView(layout);
                    mes.show();
                    updateUI(null);
                }
            });
        });
        textView = findViewById(R.id.link);
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='https://github.com/oleg-dydyshko/Fuel/blob/master/README.md'>Политика конфиденциальности</a>";
        textView.setText(Html.fromHtml(text));
        setTollbarTheme();
    }

    private void setTollbarTheme() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView title_toolbar = findViewById(R.id.title_toolbar);
        title_toolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        setSupportActionBar(toolbar);
        title_toolbar.setText(R.string.app_main);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if useremail is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String fn = "";
        String ln = "";
        String fnG = "";
        String lnG = "";
        String zero = "";
        String zero2 = "";
        String editedString = "";
        if (inventarny_spisok_data.get(position).get("editedAt") != null && inventarny_spisok_data.get(position).get("editedBy") != null) {
            long edited;
            if (inventarny_spisok_data.get(position).get("editedAt") instanceof Double)
                edited = (long) (double) inventarny_spisok_data.get(position).get("editedAt");
            else
                edited = (long) inventarny_spisok_data.get(position).get("editedAt");
            String editedBy = (String) inventarny_spisok_data.get(position).get("editedBy");
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
        String createBy = (String) inventarny_spisok_data.get(position).get("createdBy");
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).get(0).contains(createBy)) {
                fnG = users.get(i).get(1);
                lnG = users.get(i).get(2);
                break;
            }
        }
        String data02 = (String) inventarny_spisok_data.get(position).get("data02");
        String builder = "<strong>Марка, тип</strong><br>" + inventarny_spisok_data.get(position).get("data03") + "<br><br>" +
                "<strong>Заводской номер (инв. номер)</strong><br>" + inventarny_spisok_data.get(position).get("data04") + "<br><br>" +
                "<strong>Год выпуска (ввода в эксплуатацию)</strong><br>" + inventarny_spisok_data.get(position).get("data05") + "<br><br>" +
                "<strong>Периодичность метролог. аттестации, поверки, калибровки, мес.</strong><br>" + inventarny_spisok_data.get(position).get("data06") + "<br><br>" +
                "<strong>Дата последней аттестации, поверки, калибровки</strong><br>" + inventarny_spisok_data.get(position).get("data07") + "<br><br>" +
                "<strong>Дата следующей аттестации, поверки, калибровки</strong><br>" + inventarny_spisok_data.get(position).get("data08") + "<br><br>" +
                "<strong>Дата консервации</strong><br>" + inventarny_spisok_data.get(position).get("data09") + "<br><br>" +
                "<strong>Дата расконсервации</strong><br>" + inventarny_spisok_data.get(position).get("data10") + "<br><br>" +
                "<strong>Ответственный</strong><br>" + fnG + " " + lnG + "<br><br>" +
                "<strong>Примечания</strong><br>" + inventarny_spisok_data.get(position).get("data12") + editedString;
        Dialod_opisanie opisanie = Dialod_opisanie.getInstance(data02, builder);
        opisanie.show(getSupportFragmentManager(), "opisanie");
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Dialog_context_menu menu = Dialog_context_menu.getInstance(position, (String) inventarny_spisok_data.get(position).get("data02"));
        menu.show(getSupportFragmentManager(), "menu");
        return true;
    }

    @Override
    public void onDialogEditPosition(int position) {
        edit = Dialod_opisanie_edit.getInstance(userEdit, (String) inventarny_spisok_data.get(position).get("uid"), (String) inventarny_spisok_data.get(position).get("data02"), (String) inventarny_spisok_data.get(position).get("data03"), (String) inventarny_spisok_data.get(position).get("data04"), (String) inventarny_spisok_data.get(position).get("data05"), (String) inventarny_spisok_data.get(position).get("data06"), (String) inventarny_spisok_data.get(position).get("data07"), (String) inventarny_spisok_data.get(position).get("data08"), (String) inventarny_spisok_data.get(position).get("data09"), (String) inventarny_spisok_data.get(position).get("data10"), (String) inventarny_spisok_data.get(position).get("data12"));
        edit.show(getSupportFragmentManager(), "edit");
    }

    @Override
    public void onDialogDeliteClick(int position) {
        Dialog_delite_confirm confirm = Dialog_delite_confirm.getInstance((String) inventarny_spisok_data.get(position).get("data02"), position);
        confirm.show(getSupportFragmentManager(), "confirm");
    }

    @Override
    public void delite_data(int position) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("equipments").child((String) inventarny_spisok_data.get(position).get("uid")).removeValue();
    }

    @Override
    public void set_data(int textview, int year, int month, int dayOfMonth) {
        edit.set_data(textview, year, month, dayOfMonth);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add) {
            edit = Dialod_opisanie_edit.getInstance(userEdit, (long) inventarny_spisok_data.size());
            edit.show(getSupportFragmentManager(), "edit");
        }
        if (id == R.id.exit) {
            mAuth.signOut();
            listView.setVisibility(View.GONE);
            useremail.setVisibility(View.VISIBLE);
            userpass.setVisibility(View.VISIBLE);
            login.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUI(FirebaseUser user) {
        progressBar.setVisibility(View.VISIBLE);
        supportInvalidateOptionsMenu();
        if (user != null) {
            if (isNetworkAvailable()) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("equipments").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        SharedPreferences fuel = getSharedPreferences("fuel", Context.MODE_PRIVATE);
                        Type type2 = new TypeToken<ArrayList<ArrayList<Long>>>() {
                        }.getType();
                        Gson gson = new Gson();
                        String notifi = fuel.getString("notifi", "");
                        ArrayList<ArrayList<Long>> temp;
                        if (!notifi.equals("")) {
                            temp = new ArrayList<>(gson.fromJson(notifi, type2));
                        } else {
                            temp = new ArrayList<>();
                        }
                        int i = 0;
                        inventarny_spisok.clear();
                        inventarny_spisok_data.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            if (data.getValue() instanceof HashMap) {
                                HashMap hashMap = (HashMap) data.getValue();
                                inventarny_spisok_data.add(hashMap);
                                if (hashMap.size() > 12) {
                                    String uid = (String) hashMap.get("uid");
                                    if (uid == null)
                                        hashMap.put("uid", data.getKey());
                                    long data11 = 0;
                                    if (hashMap.get("data11") != null)
                                        data11 = (long) hashMap.get("data11");
                                    inventarny_spisok.add(hashMap.get("data01") + ". " + hashMap.get("data02"));
                                    ArrayList<Long> notif = new ArrayList<>();
                                    notif.add((long) hashMap.get("data01"));
                                    notif.add(data11);
                                    if (temp.size() < i) {
                                        if (temp.size() == 0)
                                            notif.add(1L);
                                        else if (temp.get(i).get(1) != data11)
                                            notif.add(1L);
                                        else
                                            notif.add(0L);
                                    } else {
                                        notif.add(1L);
                                    }
                                    notifications.add(notif);
                                    i++;
                                }
                            }
                        }
                        listView.setVisibility(View.VISIBLE);
                        useremail.setVisibility(View.GONE);
                        userpass.setVisibility(View.GONE);
                        login.setVisibility(View.GONE);
                        textView.setVisibility(View.GONE);
                        arrayAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        SharedPreferences.Editor editor = fuel.edit();
                        editor.putString("fuel_data", gson.toJson(inventarny_spisok_data));
                        editor.putString("notifi", gson.toJson(notifications));
                        editor.apply();
                        Intent intent = new Intent(MainActivity.this, ReceiverSetAlarm.class);
                        sendBroadcast(intent);
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
                    Type type = new TypeToken<ArrayList<HashMap>>() {
                    }.getType();
                    inventarny_spisok_data.addAll(gson.fromJson(g, type));
                    for (int i = 0; i < inventarny_spisok_data.size(); i++) {
                        HashMap hashMap = inventarny_spisok_data.get(i);
                        int data01 = (int) (double) hashMap.get("data01");
                        inventarny_spisok.add(data01 + ". " + hashMap.get("data02"));
                    }
                    String us = fuel.getString("users", "");
                    Type type2 = new TypeToken<ArrayList<ArrayList<String>>>() {
                    }.getType();
                    users.addAll(gson.fromJson(us, type2));
                    arrayAdapter.notifyDataSetChanged();
                    Intent intent = new Intent(MainActivity.this, ReceiverSetAlarm.class);
                    sendBroadcast(intent);
                } else {
                    Dialog_no_internet internet = new Dialog_no_internet();
                    internet.show(getSupportFragmentManager(), "internet");
                }
                listView.setVisibility(View.VISIBLE);
                useremail.setVisibility(View.GONE);
                userpass.setVisibility(View.GONE);
                login.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        } else {
            listView.setVisibility(View.GONE);
            useremail.setVisibility(View.VISIBLE);
            userpass.setVisibility(View.VISIBLE);
            login.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
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

        ListAdapter(Context context) {
            super(context, R.layout.simple_list_item, inventarny_spisok);
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
            viewHolder.button_popup.setOnClickListener((v -> showPopupMenu(viewHolder.button_popup, position, inventarny_spisok.get(position))));
            long data;
            if (inventarny_spisok_data.get(position).get("data11") instanceof Double) {
                data = (long) (double) inventarny_spisok_data.get(position).get("data11");
            } else {
                if (inventarny_spisok_data.get(position).get("data11") != null)
                    data = (long) inventarny_spisok_data.get(position).get("data11");
                else data = 0;
            }
            GregorianCalendar g = new GregorianCalendar();
            g.setTimeInMillis(data);
            String dataLong = "";
            GregorianCalendar real = (GregorianCalendar) Calendar.getInstance();
            if (g.getTimeInMillis() - real.getTimeInMillis() > 0L && g.getTimeInMillis() - real.getTimeInMillis() < 45L * 24L * 60L * 60L * 1000L) {
                dataLong = "<br><font color=#9a2828>Осталось " + (g.get(Calendar.DATE) - real.get(Calendar.DATE)) + " дней(-я)</font>";
            } else if (g.getTimeInMillis() - real.getTimeInMillis() < 0L) {
                dataLong = "<br><font color=#9a2828>Просрочено</font>";
            }
            viewHolder.text.setText(Html.fromHtml(inventarny_spisok.get(position) + dataLong));
            return mView;
        }

        private void showPopupMenu(View view, int position, String name) {
            PopupMenu popup = new PopupMenu(MainActivity.this, view);
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

    private static class ViewHolder {
        TextView text;
        ImageView button_popup;
    }
}
