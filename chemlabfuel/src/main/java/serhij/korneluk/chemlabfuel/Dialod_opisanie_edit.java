package serhij.korneluk.chemlabfuel;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Dialod_opisanie_edit extends DialogFragment {

    static Dialod_opisanie_edit getInstance(String uid, String data2, String data3, String data4, String data5, String data6, String data7, String data8, String data9, String data10) {
        Dialod_opisanie_edit opisanie = new Dialod_opisanie_edit();
        Bundle bundle = new Bundle();
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
        opisanie.setArguments(bundle);
        return opisanie;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_opisanie_edit, null);
        TextView editTextTitle = view.findViewById(R.id.textViewTitle);
        EditText editText1 = view.findViewById(R.id.textView1e);
        EditText editText2 = view.findViewById(R.id.textView2e);
        EditText editText3 = view.findViewById(R.id.textView3e);
        EditText editText4 = view.findViewById(R.id.textView4e);
        EditText editText5 = view.findViewById(R.id.textView5e);
        EditText editText6 = view.findViewById(R.id.textView6e);
        EditText editText7 = view.findViewById(R.id.textView7e);
        EditText editText8 = view.findViewById(R.id.textView8e);
        EditText editText9 = view.findViewById(R.id.textView9e);
        String uid = getArguments().getString("uid", "");
        editTextTitle.setText(getArguments().getString("data2"));
        editText1.setText(getArguments().getString("data2"));
        editText2.setText(getArguments().getString("data3"));
        editText3.setText(getArguments().getString("data4"));
        editText4.setText(getArguments().getString("data5"));
        editText5.setText(getArguments().getString("data6"));
        editText6.setText(getArguments().getString("data7"));
        editText7.setText(getArguments().getString("data8"));
        editText8.setText(getArguments().getString("data9"));
        editText9.setText(getArguments().getString("data10"));
        editText1.setSelection(editText1.getText().length());
        editText2.setSelection(editText2.getText().length());
        editText3.setSelection(editText3.getText().length());
        editText4.setSelection(editText4.getText().length());
        editText5.setSelection(editText5.getText().length());
        editText6.setSelection(editText6.getText().length());
        editText7.setSelection(editText7.getText().length());
        editText8.setSelection(editText8.getText().length());
        editText9.setSelection(editText9.getText().length());

        // Показываем клавиатуру
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        builder.setView(view);

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("equipments").child(uid).child("data02").setValue(editText1.getText().toString());
            mDatabase.child("equipments").child(uid).child("data03").setValue(editText2.getText().toString());
            mDatabase.child("equipments").child(uid).child("data04").setValue(editText3.getText().toString());
            mDatabase.child("equipments").child(uid).child("data05").setValue(editText4.getText().toString());
            mDatabase.child("equipments").child(uid).child("data06").setValue(editText5.getText().toString());
            mDatabase.child("equipments").child(uid).child("data07").setValue(editText6.getText().toString());
            mDatabase.child("equipments").child(uid).child("data08").setValue(editText7.getText().toString());
            mDatabase.child("equipments").child(uid).child("data09").setValue(editText8.getText().toString());
            mDatabase.child("equipments").child(uid).child("data10").setValue(editText9.getText().toString());
            dialog.cancel();
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.setOnShowListener(dialog -> {
            Button btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE);
            btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            Button btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE);
            btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        });
        return alert;
    }
}
