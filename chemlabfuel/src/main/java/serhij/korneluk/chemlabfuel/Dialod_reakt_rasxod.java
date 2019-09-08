package serhij.korneluk.chemlabfuel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Dialod_reakt_rasxod extends DialogFragment {

    private int groupPosition = 0;
    private int childposition = 0;
    private int izmerenie = 0;
    private TextView textView1e;
    private EditText textView2e;
    private EditText textView3e;
    private EditText textView4e;
    private GregorianCalendar c;
    private String user = "";
    private String[] ed_izmerenia = {"килограммах", "миллиграммах", "литрах", "миллилитрах"};
    private String[] ed_izmerenia2 = {"килограмм(-а)", "миллиграмм(-а)", "литр(-а)", "миллилитр(-а)"};
    private String jurnal = "";
    private int position;
    private ArrayList<ArrayList<String>> jur;
    private updateJurnal listiner;

    static Dialod_reakt_rasxod getInstance(int groupPosition, int childposition, int izmerenie, String user) {
        Dialod_reakt_rasxod opisanie = new Dialod_reakt_rasxod();
        Bundle bundle = new Bundle();
        bundle.putInt("groupposition", groupPosition);
        bundle.putInt("childposition", childposition);
        bundle.putInt("izmerenie", izmerenie);
        bundle.putString("user", user);
        opisanie.setArguments(bundle);
        return opisanie;
    }

    static Dialod_reakt_rasxod getInstance(int groupPosition, int childposition, int izmerenie, String user, String jurnal, int position) {
        Dialod_reakt_rasxod opisanie = new Dialod_reakt_rasxod();
        Bundle bundle = new Bundle();
        bundle.putInt("groupposition", groupPosition);
        bundle.putInt("childposition", childposition);
        bundle.putInt("izmerenie", izmerenie);
        bundle.putString("user", user);
        bundle.putString("jurnal", jurnal);
        bundle.putInt("position", position);
        opisanie.setArguments(bundle);
        return opisanie;
    }

    interface updateJurnal {
        void updateJurnalRasxoda(int position, String t0, String t1, String t2, String t3, String t4, String t5);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            try {
                listiner = (updateJurnal) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString() + " must implement updateJurnal");
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            groupPosition = getArguments().getInt("groupposition", 1);
            childposition = getArguments().getInt("childposition", 1);
            izmerenie = getArguments().getInt("izmerenie", 0);
            user = getArguments().getString("user", "");
            jurnal = getArguments().getString("jurnal", "");
            position = getArguments().getInt("position", 0);
        }
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_reakt_rasxod, null);
        TextView editTextTitle = view.findViewById(R.id.textViewTitle);
        textView1e = view.findViewById(R.id.textView1e);
        TextView kolkast = view.findViewById(R.id.kolkast);
        kolkast.setText(kolkast.getText().toString() + " в " + ed_izmerenia[izmerenie]);
        c = (GregorianCalendar) Calendar.getInstance();
        set_data(1, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
        Button button1 = view.findViewById(R.id.button1);
        button1.setOnClickListener((v -> {
            String[] t1 = textView1e.getText().toString().split("-");
            c = new GregorianCalendar(Integer.parseInt(t1[0]), Integer.parseInt(t1[1]) - 1, Integer.parseInt(t1[2]));
            TextView textView1 = view.findViewById(R.id.textView1);
            Dialog_data data = Dialog_data.getInstance(c.getTimeInMillis(), 1, textView1.getText().toString());
            data.show(getFragmentManager(), "data");
        }));
        editTextTitle.setText(CremLabFuel.ReaktiveSpisok.get(groupPosition).get(childposition).get(13));
        textView3e = view.findViewById(R.id.textView3e);
        textView3e.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                send();
                return true;
            }
            return false;
        });
        textView2e = view.findViewById(R.id.textView2e);
        textView2e.addTextChangedListener(new MyTextWatcher(textView2e));
        textView4e = view.findViewById(R.id.textView4e);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ArrayList<String>>>() {
        }.getType();
        if (!jurnal.equals("")) {
            jur = gson.fromJson(jurnal, type);
            textView1e.setText(jur.get(position).get(0));
            textView2e.setText(jur.get(position).get(1));
            textView4e.setText(jur.get(position).get(3));
            textView3e.setText(jur.get(position).get(4));
        } else {
            jur = gson.fromJson(CremLabFuel.ReaktiveSpisok.get(groupPosition).get(childposition).get(16), type);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(getString(R.string.save), (dialog, which) -> send());
        builder.setNegativeButton(getString(R.string.cansel), (dialog, which) -> dialog.cancel());
        builder.setView(view);
        AlertDialog alert = builder.create();
        alert.setOnShowListener(dialog -> {
            Button btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE);
            btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            Button btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE);
            btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        });
        return alert;
    }

    private void send() {
        //String plotnost = "";
        //if (!textView4e.getText().toString().equals("")) {
        //plotnost = " Плотность:" + textView4e.getText().toString();
        //}
        if (!textView2e.getText().toString().trim().equals("") && !textView3e.getText().toString().equals("")) {
            //Gson gson = new Gson();
            //Type type = new TypeToken<ArrayList<ArrayList<String>>>() {
            //}.getType();
            //ArrayList<ArrayList<String>> jurnal = gson.fromJson(CremLabFuel.ReaktiveSpisok.get(groupPosition).get(childposition).get(16), type);
            //String gurnal = CremLabFuel.ReaktiveSpisok.get(groupPosition).get(childposition).get(16);
            //if (!gurnal.equals(""))
            //gurnal = gurnal + "\n";
            //gurnal = gurnal + textView1e.getText().toString() + " " + new BigDecimal(textView2e.getText().toString().trim().replace(",", ".")).toString().replace(".", ",") + ed_izmerenia[izmerenie] + plotnost + " " + textView3e.getText().toString() + "\n";
            if (textView4e.getText().toString().trim().equals(""))
                textView4e.setText("1");
            BigDecimal correkt;
            if (jurnal.equals("")) {
                correkt = new BigDecimal(CremLabFuel.ReaktiveSpisok.get(groupPosition).get(childposition).get(9));
                ArrayList<String> subJurnal = new ArrayList<>();
                subJurnal.add(textView1e.getText().toString());
                subJurnal.add(new BigDecimal(textView2e.getText().toString().trim().replace(",", ".")).toString().replace(".", ","));
                subJurnal.add(ed_izmerenia2[izmerenie]);
                subJurnal.add(new BigDecimal(textView4e.getText().toString().trim().replace(",", ".")).toString().replace(".", ","));
                subJurnal.add(textView3e.getText().toString());
                subJurnal.add(user);
                jur.add(subJurnal);
            } else {
                correkt = new BigDecimal(CremLabFuel.ReaktiveSpisok.get(groupPosition).get(childposition).get(9)).add(new BigDecimal(jur.get(position).get(1)));
                jur.get(position).set(0, textView1e.getText().toString());
                jur.get(position).set(1, new BigDecimal(textView2e.getText().toString().trim().replace(",", ".")).toString().replace(".", ","));
                jur.get(position).set(2, ed_izmerenia2[izmerenie]);
                jur.get(position).set(3, new BigDecimal(textView4e.getText().toString().trim().replace(",", ".")).toString().replace(".", ","));
                jur.get(position).set(4, textView3e.getText().toString());
                jur.get(position).set(5, user);
            }
            String nomerProdukta = String.valueOf(groupPosition);
            String nomerPartii = String.valueOf(childposition);
            BigDecimal ras = new BigDecimal(textView2e.getText().toString().trim().replace(",", "."));
            BigDecimal rasxod = correkt.subtract(ras);
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("reagents").child(nomerProdukta).child(nomerPartii).child("data09").setValue(rasxod.doubleValue());
            mDatabase.child("reagents").child(nomerProdukta).child(nomerPartii).child("data11").setValue(jur);
            listiner.updateJurnalRasxoda(position, jur.get(position).get(0), jur.get(position).get(1), jur.get(position).get(2), jur.get(position).get(3), jur.get(position).get(4), jur.get(position).get(5));
        } else {
            LinearLayout layout = new LinearLayout(getActivity());
            layout.setBackgroundResource(R.color.colorPrimary);
            TextView toast = new TextView(getActivity());
            toast.setTextColor(getResources().getColor(R.color.colorIcons));
            toast.setPadding(10, 10, 10, 10);
            toast.setText(R.string.error);
            toast.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            layout.addView(toast);
            Toast mes = new Toast(getActivity());
            mes.setDuration(Toast.LENGTH_LONG);
            mes.setView(layout);
            mes.show();
        }
        getDialog().cancel();
    }

    void set_data(int textview, int year, int month, int dayOfMonth) {
        String zero = "";
        String zero2 = "";
        if (month < 9) zero = "0";
        if (dayOfMonth < 10) zero2 = "0";
        if (textview == 1) {
            if (year == 0)
                textView1e.setText("");
            else
                textView1e.setText(getString(R.string.set_date, year, zero, month + 1, zero2, dayOfMonth));
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private int editPosition;
        private EditText textView;

        MyTextWatcher(EditText textView) {
            this.textView = textView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            editPosition = start + count;
        }

        @Override
        public void afterTextChanged(Editable s) {
            String edit = s.toString();
            edit = edit.replace(".", ",");
            textView.removeTextChangedListener(this);
            textView.setText(edit);
            textView.setSelection(editPosition);
            textView.addTextChangedListener(this);
        }
    }
}
