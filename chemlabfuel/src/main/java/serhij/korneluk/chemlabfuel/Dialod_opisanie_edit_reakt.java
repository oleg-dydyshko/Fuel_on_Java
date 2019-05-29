package serhij.korneluk.chemlabfuel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;

public class Dialod_opisanie_edit_reakt extends DialogFragment {

    private static boolean add;
    private TextView editTextTitle;
    private EditText editText2;
    private TextView editText3;
    private EditText editText5;
    private EditText editText6;
    private EditText editText7;
    private TextView editText8;
    private EditText editText9;
    private EditText editText10;
    private EditText editText12;
    private Spinner spinner11e;
    private EditText editText13;
    private EditText editText14;
    private EditText editText15;
    private Spinner spinner9;
    private String user = "";
    private String title = "";
    private int groupPosition = 0;
    private int childposition = 0;
    private int ed_izmerenia = 0;
    private String[] data = {"Килограмм", "Миллиграмм", "Литры", "Миллилитры"};
    private listUpdateListiner listiner;

    static Dialod_opisanie_edit_reakt getInstance(String user, int groupPosition, int childposition) {
        Dialod_opisanie_edit_reakt opisanie = new Dialod_opisanie_edit_reakt();
        Bundle bundle = new Bundle();
        bundle.putString("user", user);
        bundle.putInt("groupposition", groupPosition);
        bundle.putInt("childposition", childposition);
        opisanie.setArguments(bundle);
        add = false;
        return opisanie;
    }

    static Dialod_opisanie_edit_reakt getInstance(String user, String title, String minostatok, int ed_izmerenia) {
        Dialod_opisanie_edit_reakt opisanie = new Dialod_opisanie_edit_reakt();
        Bundle bundle = new Bundle();
        bundle.putString("user", user);
        bundle.putString("title", title);
        bundle.putString("minostatok", minostatok);
        bundle.putInt("ed_izmerenia", ed_izmerenia);
        opisanie.setArguments(bundle);
        add = true;
        return opisanie;
    }

    interface listUpdateListiner {
        void UpdateList();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            try {
                listiner = (listUpdateListiner) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString() + " must implement listUpdateListiner");
            }
        }
    }

    void set_data(int textview, int year, int month, int dayOfMonth) {
        String zero = "";
        String zero2 = "";
        if (month < 9) zero = "0";
        if (dayOfMonth < 10) zero2 = "0";
        switch (textview) {
            case 3:
                if (year == 0)
                    editText3.setText("");
                else
                    editText3.setText(getString(R.string.set_date, year, zero, month + 1, zero2, dayOfMonth));
                break;
            case 8:
                if (year == 0)
                    editText8.setText("");
                else
                    if (dayOfMonth == -1)
                        editText8.setText(getString(R.string.set_date2, year, zero, month + 1));
                    else
                        editText8.setText(getString(R.string.set_date, year, zero, month + 1, zero2, dayOfMonth));
                break;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] god = {"Год", "Месяц"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_opisanie_edit_reakt, null);
        editTextTitle = view.findViewById(R.id.textViewTitle);

        editText2 = view.findViewById(R.id.textView2e);
        editText2.addTextChangedListener(new MyTextWatcher(editText2));
        editText3 = view.findViewById(R.id.textView3e);
        editText5 = view.findViewById(R.id.textView5e);
        editText6 = view.findViewById(R.id.textView6e);
        editText7 = view.findViewById(R.id.textView7e);
        editText8 = view.findViewById(R.id.textView8e);
        editText9 = view.findViewById(R.id.textView9e);
        editText10 = view.findViewById(R.id.textView10e);
        spinner11e = view.findViewById(R.id.spinner11e);
        spinner9 = view.findViewById(R.id.spinner9);
        spinner9.setAdapter(new ListAdapter(getActivity(), god));
        spinner11e.setAdapter(new ListAdapter(getActivity(), data));
        spinner11e.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        editText12 = view.findViewById(R.id.textView12e);
        editText12.addTextChangedListener(new MyTextWatcher(editText12));
        editText13 = view.findViewById(R.id.textView13e);
        editText13.addTextChangedListener(new MyTextWatcher(editText13));
        editText15 = view.findViewById(R.id.textView15e);
        editText14 = view.findViewById(R.id.textView14e);
        Button button3 = view.findViewById(R.id.button3);
        button3.setOnClickListener((v -> {
            GregorianCalendar c;
            if (editText3.getText().toString().equals("")) {
                c = (GregorianCalendar) Calendar.getInstance();
            } else {
                String[] t1 = editText3.getText().toString().split("-");
                c = new GregorianCalendar(Integer.parseInt(t1[0]), Integer.parseInt(t1[1]) - 1, Integer.parseInt(t1[2]));
            }
            TextView textView3 = view.findViewById(R.id.textView3);
            Dialog_data data = Dialog_data.getInstance(c.getTimeInMillis(), 3, textView3.getText().toString());
            data.show(getFragmentManager(), "data");
        }));
        Button button8 = view.findViewById(R.id.button8);
        button8.setOnClickListener((v -> {
            GregorianCalendar c;
            if (editText8.getText().toString().equals("")) {
                c = (GregorianCalendar) Calendar.getInstance();
            } else {
                String[] t1 = editText8.getText().toString().split("-");
                if (t1.length == 3)
                    c = new GregorianCalendar(Integer.parseInt(t1[0]), Integer.parseInt(t1[1]) - 1, Integer.parseInt(t1[2]));
                else
                    c = new GregorianCalendar(Integer.parseInt(t1[0]), Integer.parseInt(t1[1]) - 1, 1);
            }
            TextView textView8 = view.findViewById(R.id.textView8);
            Dialog_data data = Dialog_data.getInstance(c.getTimeInMillis(), 8, textView8.getText().toString());
            data.show(getFragmentManager(), "data");
        }));
        String minostatok = "";
        if (getArguments() != null) {
            user = getArguments().getString("user", "");
            title = getArguments().getString("title", "");
            groupPosition = getArguments().getInt("groupposition", 1);
            childposition = getArguments().getInt("childposition", 1);
            minostatok = getArguments().getString("minostatok", "");
            ed_izmerenia = getArguments().getInt("ed_izmerenia", 0);
        }
        if (add) {
            editTextTitle.setText(R.string.add);
            editText2.setText(title);
            editText13.setText(minostatok);
            editText13.setImeOptions(EditorInfo.IME_ACTION_GO);
            editText13.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                send();
                return true;
            }
            return false;
        });
            spinner11e.setSelection(ed_izmerenia);
            TextView textView15 = view.findViewById(R.id.textView15);
            textView15.setVisibility(View.GONE);
            editText15.setVisibility(View.GONE);
        } else {
            spinner9.setVisibility(View.GONE);
            editTextTitle.setText(CremLabFuel.ReaktiveSpisok.get(groupPosition).get(childposition).get(13));
            editText2.setText(CremLabFuel.ReaktiveSpisok.get(groupPosition).get(childposition).get(13));
            editText3.setText(CremLabFuel.ReaktiveSpisok.get(groupPosition).get(childposition).get(1));
            editText5.setText(CremLabFuel.ReaktiveSpisok.get(groupPosition).get(childposition).get(2));
            editText6.setText(CremLabFuel.ReaktiveSpisok.get(groupPosition).get(childposition).get(3));
            editText7.setText(CremLabFuel.ReaktiveSpisok.get(groupPosition).get(childposition).get(4));
            editText8.setText(CremLabFuel.ReaktiveSpisok.get(groupPosition).get(childposition).get(5));
            editText9.setText(String.valueOf(CremLabFuel.ReaktiveSpisok.get(groupPosition).get(childposition).get(6)));
            editText10.setText(CremLabFuel.ReaktiveSpisok.get(groupPosition).get(childposition).get(7));
            spinner11e.setSelection(Integer.parseInt(CremLabFuel.ReaktiveSpisok.get(groupPosition).get(childposition).get(8)));
            editText12.setText(String.valueOf(CremLabFuel.ReaktiveSpisok.get(groupPosition).get(childposition).get(9)));
            editText13.setText(String.valueOf(CremLabFuel.ReaktiveSpisok.get(groupPosition).get(childposition).get(10)));
            editText14.setText(String.valueOf(CremLabFuel.ReaktiveSpisok.get(groupPosition).get(childposition).get(17)));
            editText15.setText(String.valueOf(CremLabFuel.ReaktiveSpisok.get(groupPosition).get(childposition).get(16)));
        }

        editText2.setSelection(editText2.getText().length());
        editText5.setSelection(editText5.getText().length());
        editText6.setSelection(editText6.getText().length());
        editText7.setSelection(editText7.getText().length());
        editText9.setSelection(editText9.getText().length());
        editText10.setSelection(editText10.getText().length());
        editText12.setSelection(editText12.getText().length());
        editText13.setSelection(editText13.getText().length());
        editText14.setSelection(editText14.getText().length());
        // Показываем клавиатуру
        //InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        builder.setView(view);

        builder.setPositiveButton(getString(R.string.save), (dialog, which) -> {
            send();
        });
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
        if (editText6.getText().toString().trim().equals("")) {
            editText6.setText(R.string.no);
        }
        if (editText9.getText().toString().trim().equals("")) {
            editText9.setText("1");
        }
        if (editText10.getText().toString().trim().equals("")) {
            editText10.setText(R.string.obychnye);
        }
        if (!editText12.getText().toString().trim().equals("") && !editText12.getText().toString().trim().equals("") && !editText8.getText().toString().trim().equals("")) {

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            GregorianCalendar g = (GregorianCalendar) Calendar.getInstance();

            String nomerProdukta = String.valueOf(groupPosition);
            String nomerPartii = String.valueOf(childposition);
            long text9 = Long.valueOf(editText9.getText().toString().trim());
            if (add && spinner9.getSelectedItemPosition() == 0) {
                text9 = text9 * 12;
            }
            if (add) {
                if (CremLabFuel.ReaktiveSpisok.size() != 0) {
                    for (Map.Entry<Integer, LinkedHashMap<Integer, LinkedHashMap<Integer, String>>> entry : CremLabFuel.ReaktiveSpisok.entrySet()) {
                        LinkedHashMap<Integer, LinkedHashMap<Integer, String>> value = entry.getValue();
                        for (Map.Entry<Integer, LinkedHashMap<Integer, String>> entry2 : value.entrySet()) {
                            LinkedHashMap<Integer, String> value2 = entry2.getValue();
                            String name = "no";
                            for (Map.Entry<Integer, String> entry3 : value2.entrySet()) {
                                if (entry3.getKey() == 13) {
                                    name = entry3.getValue();
                                }
                                if (entry3.getKey() == 14) {
                                    if (title.equals("")) {
                                        groupPosition = Integer.parseInt(entry3.getValue()) + 1;
                                        nomerProdukta = String.valueOf(groupPosition);
                                    } else if (editText2.getText().toString().trim().contains(name)) {
                                        groupPosition = Integer.parseInt(entry3.getValue());
                                        nomerProdukta = String.valueOf(groupPosition);
                                    }
                                }
                                if (entry3.getKey() == 15) {
                                    if (editText2.getText().toString().trim().contains(name)) {
                                        childposition = Integer.parseInt(entry3.getValue()) + 1;
                                        nomerPartii = String.valueOf(childposition);
                                    }
                                }
                            }
                        }

                    }
                }
                mDatabase.child("reagents").child(nomerProdukta).child(nomerPartii).child("createdAt").setValue(g.getTimeInMillis());
                mDatabase.child("reagents").child(nomerProdukta).child(nomerPartii).child("createdBy").setValue(user);
            }
            mDatabase.child("reagents").child(nomerProdukta).child("name").setValue(editText2.getText().toString().trim());
            mDatabase.child("reagents").child(nomerProdukta).child(nomerPartii).child("data01").setValue(editText3.getText().toString().trim());
            mDatabase.child("reagents").child(nomerProdukta).child(nomerPartii).child("data02").setValue(editText5.getText().toString().trim());
            mDatabase.child("reagents").child(nomerProdukta).child(nomerPartii).child("data03").setValue(editText6.getText().toString().trim());
            mDatabase.child("reagents").child(nomerProdukta).child(nomerPartii).child("data04").setValue(editText7.getText().toString().trim());
            mDatabase.child("reagents").child(nomerProdukta).child(nomerPartii).child("data05").setValue(editText8.getText().toString().trim());
            mDatabase.child("reagents").child(nomerProdukta).child(nomerPartii).child("data06").setValue(text9);
            mDatabase.child("reagents").child(nomerProdukta).child(nomerPartii).child("data07").setValue(editText10.getText().toString().trim());
            mDatabase.child("reagents").child(nomerProdukta).child(nomerPartii).child("data08").setValue((long) spinner11e.getSelectedItemPosition());
            mDatabase.child("reagents").child(nomerProdukta).child(nomerPartii).child("data09").setValue(Double.valueOf(editText12.getText().toString().trim().replace(",", ".")));
            mDatabase.child("reagents").child(nomerProdukta).child(nomerPartii).child("data10").setValue(Double.valueOf(editText13.getText().toString().trim().replace(",", ".")));
            mDatabase.child("reagents").child(nomerProdukta).child(nomerPartii).child("data11").setValue(editText15.getText().toString().trim());
            mDatabase.child("reagents").child(nomerProdukta).child(nomerPartii).child("data12").setValue(editText14.getText().toString().trim());
            if (!add) {
                mDatabase.child("reagents").child(nomerProdukta).child(nomerPartii).child("editedAt").setValue(g.getTimeInMillis());
                mDatabase.child("reagents").child(nomerProdukta).child(nomerPartii).child("editedBy").setValue(user);
            }
            getActivity().sendBroadcast(new Intent(getActivity(), ReceiverSetAlarm.class));
            listiner.UpdateList();
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
            if (textView.getId() == R.id.textView2e && !edit.equals("")) {
                editTextTitle.setText(edit);
            } else {
                edit = edit.replace(".", ",");
                textView.removeTextChangedListener(this);
                textView.setText(edit);
                textView.setSelection(editPosition);
                textView.addTextChangedListener(this);
            }
        }
    }

    private class ListAdapter extends ArrayAdapter<String> {

        private String[] dataA;

        ListAdapter(Context context, String[] data) {
            super(context, R.layout.simple_list_item2, data);
            dataA = data;
        }

        @NonNull
        @Override
        public View getView(int position, View mView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (mView == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mView = vi.inflate(R.layout.simple_list_item2, parent, false);
                viewHolder = new ViewHolder();
                mView.setTag(viewHolder);
                viewHolder.text = mView.findViewById(R.id.label);
            } else {
                viewHolder = (ViewHolder) mView.getTag();
            }
            viewHolder.text.setText(dataA[position]);
            return mView;
        }
    }

    private static class ViewHolder {
        TextView text;
    }
}
