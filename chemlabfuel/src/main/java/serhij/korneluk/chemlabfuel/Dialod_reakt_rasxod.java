package serhij.korneluk.chemlabfuel;

import android.app.Dialog;
import android.os.Bundle;
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

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Dialod_reakt_rasxod extends DialogFragment {

    private int groupPosition = 0;
    private int childposition = 0;
    private TextView textView1e;
    private TextView textView2e;
    private EditText textView3e;
    private GregorianCalendar c;

    static Dialod_reakt_rasxod getInstance(int groupPosition, int childposition) {
        Dialod_reakt_rasxod opisanie = new Dialod_reakt_rasxod();
        Bundle bundle = new Bundle();
        bundle.putInt("groupposition", groupPosition);
        bundle.putInt("childposition", childposition);
        opisanie.setArguments(bundle);
        return opisanie;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            groupPosition = getArguments().getInt("groupposition", 1);
            childposition = getArguments().getInt("childposition", 1);
        }
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_reakt_rasxod, null);
        TextView editTextTitle = view.findViewById(R.id.textViewTitle);
        textView1e = view.findViewById(R.id.textView1e);
        c = (GregorianCalendar) Calendar.getInstance();
        set_data(1, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
        textView1e.setOnClickListener((v -> {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(getString(R.string.save), (dialog, which) -> {
            send();
        });
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
        if (!textView2e.getText().toString().trim().equals("") && !textView3e.getText().toString().equals("")) {
            String gurnal = CremLabFuel.ReaktiveSpisok.get(groupPosition).get(childposition).get(16);
            if (!gurnal.equals(""))
                gurnal = gurnal + "\n";
            gurnal = gurnal + textView1e.getText().toString() + " " + new BigDecimal(textView2e.getText().toString().trim()) + " " + textView3e.getText().toString() + "\n";
            String nomerProdukta = String.valueOf(groupPosition);
            String nomerPartii = String.valueOf(childposition);
            BigDecimal ras = new BigDecimal(textView2e.getText().toString().trim());
            BigDecimal rasxod = new BigDecimal(CremLabFuel.ReaktiveSpisok.get(groupPosition).get(childposition).get(9)).subtract(ras);
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("reagents").child(nomerProdukta).child(nomerPartii).child("data09").setValue(rasxod.doubleValue());
            mDatabase.child("reagents").child(nomerProdukta).child(nomerPartii).child("data11").setValue(gurnal);
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
}
