package serhij.korneluk.chemlabfuel;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Dialod_opisanie_edit extends DialogFragment {

    private String data8 = "";
    private String uid = "";
    private boolean add = true;
    private long size = -1;
    private TextView editText7;
    private TextView editText9;
    private TextView editText10;
    private EditText editText2;
    private EditText editText3;
    private EditText editText4;
    private EditText editText5;
    private EditText editText6;
    private EditText editText12;
    private String user = "";
    private String data9_Konservacia = "";
    private String data10_RazKonservacia = "";
    private String data7_OldCheck = "";
    private String data6_PeriodCheck = "";

    static Dialod_opisanie_edit getInstance(String user, String uid, String data2, String data3, String data4, String data5, String data6, String data7, String data8, String data9, String data10, String data12) {
        Dialod_opisanie_edit opisanie = new Dialod_opisanie_edit();
        Bundle bundle = new Bundle();
        bundle.putString("user", user);
        bundle.putString("uid", uid);
        bundle.putString("data2", data2);
        bundle.putString("data3", data3);
        bundle.putString("data4", data4);
        bundle.putString("data5", data5);
        bundle.putString("data6", data6);
        bundle.putString("data7", data7);
        bundle.putString("data8", data8);
        bundle.putString("data9", data9);
        bundle.putString("data10", data10);
        bundle.putString("data12", data12);
        opisanie.setArguments(bundle);
        return opisanie;
    }

    static Dialod_opisanie_edit getInstance(String user, long size) {
        Dialod_opisanie_edit opisanie = new Dialod_opisanie_edit();
        Bundle bundle = new Bundle();
        bundle.putString("user", user);
        bundle.putLong("size", size);
        opisanie.setArguments(bundle);
        return opisanie;
    }

    void set_data(int textview, int year, int month, int dayOfMonth) {
        String zero = "";
        String zero2 = "";
        if (month < 9) zero = "0";
        if (dayOfMonth < 10) zero2 = "0";
        switch (textview) {
            case 7:
                if (year == 0)
                    editText7.setText("");
                else
                    editText7.setText(getString(R.string.set_date, year, zero, month + 1, zero2, dayOfMonth));
                break;
            case 9:
                if (year == 0)
                    editText9.setText("");
                else
                    editText9.setText(getString(R.string.set_date, year, zero, month + 1, zero2, dayOfMonth));
                break;
            case 10:
                if (year == 0)
                    editText10.setText("");
                else
                    editText10.setText(getString(R.string.set_date, year, zero, month + 1, zero2, dayOfMonth));
                break;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_opisanie_edit, null);
        TextView editTextTitle = view.findViewById(R.id.textViewTitle);

        editText2 = view.findViewById(R.id.textView2e);
        editText3 = view.findViewById(R.id.textView3e);
        editText4 = view.findViewById(R.id.textView4e);
        editText5 = view.findViewById(R.id.textView5e);
        editText6 = view.findViewById(R.id.textView6e);
        editText7 = view.findViewById(R.id.textView7e);
        GregorianCalendar g = (GregorianCalendar) Calendar.getInstance();
        String zero = "";
        if (g.get(Calendar.DATE) < 10) zero = "0";
        String zero2 = "";
        if (g.get(Calendar.MONTH) < 9) zero2 = "0";
        editText7.setHint(g.get(Calendar.YEAR) + "-" + zero2 + (g.get(Calendar.MONTH) + 1) + "-" + zero + g.get(Calendar.DATE));
        editText9 = view.findViewById(R.id.textView9e);
        editText10 = view.findViewById(R.id.textView10e);
        editText12 = view.findViewById(R.id.textView12e);
        editText12.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                send();
                return true;
            }
            return false;
        });
        Button button7 = view.findViewById(R.id.button7);
        button7.setOnClickListener((v -> {
            GregorianCalendar c;
            if (editText7.getText().toString().equals("")) {
                c = (GregorianCalendar) Calendar.getInstance();
            } else {
                String[] t1 = editText7.getText().toString().split("-");
                c = new GregorianCalendar(Integer.parseInt(t1[0]), Integer.parseInt(t1[1]) - 1, Integer.parseInt(t1[2]));
            }
            TextView textView7 = view.findViewById(R.id.textView7);
            Dialog_data data = Dialog_data.getInstance(c.getTimeInMillis(), 7, textView7.getText().toString());
            data.show(getFragmentManager(), "data");
        }));
        Button button9 = view.findViewById(R.id.button9);
        button9.setOnClickListener((v -> {
            GregorianCalendar c;
            if (editText9.getText().toString().equals("")) {
                c = (GregorianCalendar) Calendar.getInstance();
            } else {
                String[] t1 = editText9.getText().toString().split("-");
                c = new GregorianCalendar(Integer.parseInt(t1[0]), Integer.parseInt(t1[1]) - 1, Integer.parseInt(t1[2]));
            }
            TextView textView9 = view.findViewById(R.id.textView9);
            Dialog_data data = Dialog_data.getInstance(c.getTimeInMillis(), 9, textView9.getText().toString());
            data.show(getFragmentManager(), "data");
        }));
        Button button10 = view.findViewById(R.id.button10);
        button10.setOnClickListener((v -> {
            GregorianCalendar c;
            if (editText10.getText().toString().equals("")) {
                c = (GregorianCalendar) Calendar.getInstance();
            } else {
                String[] t1 = editText10.getText().toString().split("-");
                c = new GregorianCalendar(Integer.parseInt(t1[0]), Integer.parseInt(t1[1]) - 1, Integer.parseInt(t1[2]));
            }
            TextView textView10 = view.findViewById(R.id.textView10);
            Dialog_data data = Dialog_data.getInstance(c.getTimeInMillis(), 10, textView10.getText().toString());
            data.show(getFragmentManager(), "data");
        }));

        String data2 = "";
        String data3 = "";
        String data4 = "";
        String data5 = "";
        String data12 = "";
        if (getArguments() != null) {
            user = getArguments().getString("user", "");
            size = getArguments().getLong("size", -1);
            uid = getArguments().getString("uid", "");
            data9_Konservacia = getArguments().getString("data9", "");
            data10_RazKonservacia = getArguments().getString("data10", "");
            data7_OldCheck = getArguments().getString("data7", "");
            data6_PeriodCheck = getArguments().getString("data6", "");
            data2 = getArguments().getString("data2", "");
            data3 = getArguments().getString("data3", "");
            data4 = getArguments().getString("data4", "");
            data5 = getArguments().getString("data5", "");
            data12 = getArguments().getString("data12", "");
        }
        if (size == -1)
            add = false;

        if (add)
            editTextTitle.setText(R.string.add);
        else
            editTextTitle.setText(data2);
        editText2.setText(data2);
        editText3.setText(data3);
        editText4.setText(data4);
        editText5.setText(data5);
        editText6.setText(data6_PeriodCheck);
        editText7.setText(data7_OldCheck);
        editText9.setText(data9_Konservacia);
        editText10.setText(data10_RazKonservacia);
        editText12.setText(data12);

        editText2.setSelection(editText2.getText().length());
        editText3.setSelection(editText3.getText().length());
        editText4.setSelection(editText4.getText().length());
        editText5.setSelection(editText5.getText().length());
        editText6.setSelection(editText6.getText().length());
        editText12.setSelection(editText12.getText().length());

        // Показываем клавиатуру
        //InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        builder.setView(view);

        builder.setPositiveButton(getString(R.string.save), (dialog, which) -> send());
        builder.setNegativeButton(getString(R.string.cansel), (dialog, which) -> dialog.cancel());
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
        data9_Konservacia = editText9.getText().toString().trim();
        data10_RazKonservacia = editText10.getText().toString().trim();
        data7_OldCheck = editText7.getText().toString().trim();
        data6_PeriodCheck = editText6.getText().toString().trim();
        if (!(!data7_OldCheck.equals("") && data7_OldCheck.contains("-"))) {
            GregorianCalendar g = (GregorianCalendar) Calendar.getInstance();
            String zero = "";
            if (g.get(Calendar.DATE) < 10) zero = "0";
            String zero2 = "";
            if (g.get(Calendar.MONTH) < 9) zero2 = "0";
            data7_OldCheck = g.get(Calendar.YEAR) + "-" + zero2 + (g.get(Calendar.MONTH) + 1) + "-" + zero + g.get(Calendar.DATE);
        }
        GregorianCalendar c = (GregorianCalendar) Calendar.getInstance();
        c.add(Calendar.YEAR, 20);
        long data11 = c.getTimeInMillis();
        if (data6_PeriodCheck != null && !data6_PeriodCheck.equals("")) {
            if (data9_Konservacia != null && !data9_Konservacia.equals("")) {
                String[] tk = data9_Konservacia.split("-");
                c.set(Integer.parseInt(tk[0]), (Integer.parseInt(tk[1]) - 1), Integer.parseInt(tk[2]));
                long start = c.getTimeInMillis();
                if (data10_RazKonservacia != null && !data10_RazKonservacia.equals("")) {
                    String[] tr = data10_RazKonservacia.split("-");
                    c.set(Integer.parseInt(tr[0]), (Integer.parseInt(tr[1]) - 1), Integer.parseInt(tr[2]));
                    long end = c.getTimeInMillis();
                    if (start > end) {
                        c.setTimeInMillis(start);
                        c.add(Calendar.YEAR, 20);
                        data11 = c.getTimeInMillis();
                        String zero = "";
                        if (c.get(Calendar.DATE) < 10) zero = "0";
                        String zero2 = "";
                        if (c.get(Calendar.MONTH) < 9) zero2 = "0";
                        String[] t1 = data7_OldCheck.split("-");
                        c.set(Integer.parseInt(t1[0]), (Integer.parseInt(t1[1]) - 1), Integer.parseInt(t1[2]));
                        c.add(Calendar.MONTH, Integer.parseInt(data6_PeriodCheck));
                        data8 = c.get(Calendar.YEAR) + "-" + zero2 + (c.get(Calendar.MONTH) + 1) + "-" + zero + c.get(Calendar.DATE);
                    } else {
                        String[] t1 = data7_OldCheck.split("-");
                        c.set(Integer.parseInt(t1[0]), (Integer.parseInt(t1[1]) - 1), Integer.parseInt(t1[2]));
                        c.add(Calendar.MONTH, Integer.parseInt(data6_PeriodCheck));
                        String zero = "";
                        if (c.get(Calendar.DATE) < 10) zero = "0";
                        String zero2 = "";
                        if (c.get(Calendar.MONTH) < 9) zero2 = "0";
                        data8 = c.get(Calendar.YEAR) + "-" + zero2 + (c.get(Calendar.MONTH) + 1) + "-" + zero + c.get(Calendar.DATE);
                        data11 = c.getTimeInMillis();
                    }
                } else {
                    c.setTimeInMillis(start);
                    c.add(Calendar.YEAR, 20);
                    data11 = c.getTimeInMillis();
                    String[] t1 = data7_OldCheck.split("-");
                    c.set(Integer.parseInt(t1[0]), (Integer.parseInt(t1[1]) - 1), Integer.parseInt(t1[2]));
                    c.add(Calendar.MONTH, Integer.parseInt(data6_PeriodCheck));
                    String zero = "";
                    if (c.get(Calendar.DATE) < 10) zero = "0";
                    String zero2 = "";
                    if (c.get(Calendar.MONTH) < 9) zero2 = "0";
                    data8 = c.get(Calendar.YEAR) + "-" + zero2 + (c.get(Calendar.MONTH) + 1) + "-" + zero + c.get(Calendar.DATE);
                }
            } else {
                String[] t1 = data7_OldCheck.split("-");
                c.set(Integer.parseInt(t1[0]), (Integer.parseInt(t1[1]) - 1), Integer.parseInt(t1[2]));
                c.add(Calendar.MONTH, Integer.parseInt(data6_PeriodCheck));
                String zero = "";
                if (c.get(Calendar.DATE) < 10) zero = "0";
                String zero2 = "";
                if (c.get(Calendar.MONTH) < 9) zero2 = "0";
                data8 = c.get(Calendar.YEAR) + "-" + zero2 + (c.get(Calendar.MONTH) + 1) + "-" + zero + c.get(Calendar.DATE);
                data11 = c.getTimeInMillis();
            }
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        GregorianCalendar g = (GregorianCalendar) Calendar.getInstance();
        if (add) {
            uid = mDatabase.child("equipments").push().getKey();
            mDatabase.child("equipments").child(uid).child("createdAt").setValue(g.getTimeInMillis());
            mDatabase.child("equipments").child(uid).child("createdBy").setValue(user);
            mDatabase.child("equipments").child(uid).child("data01").setValue(size + 1);
        }
        mDatabase.child("equipments").child(uid).child("data02").setValue(editText2.getText().toString().trim());
        mDatabase.child("equipments").child(uid).child("data03").setValue(editText3.getText().toString().trim());
        mDatabase.child("equipments").child(uid).child("data04").setValue(editText4.getText().toString().trim());
        mDatabase.child("equipments").child(uid).child("data05").setValue(editText5.getText().toString().trim());
        mDatabase.child("equipments").child(uid).child("data06").setValue(data6_PeriodCheck);
        mDatabase.child("equipments").child(uid).child("data07").setValue(data7_OldCheck);
        mDatabase.child("equipments").child(uid).child("data08").setValue(data8);
        mDatabase.child("equipments").child(uid).child("data09").setValue(data9_Konservacia);
        mDatabase.child("equipments").child(uid).child("data10").setValue(data10_RazKonservacia);
        mDatabase.child("equipments").child(uid).child("data11").setValue(data11);
        mDatabase.child("equipments").child(uid).child("data12").setValue(editText12.getText().toString().trim());
        if (!add) {
            mDatabase.child("equipments").child(uid).child("editedAt").setValue(g.getTimeInMillis());
            mDatabase.child("equipments").child(uid).child("editedBy").setValue(user);
        }
        getActivity().sendBroadcast(new Intent(getActivity(), ReceiverSetAlarm.class));
        getDialog().cancel();
    }
}
