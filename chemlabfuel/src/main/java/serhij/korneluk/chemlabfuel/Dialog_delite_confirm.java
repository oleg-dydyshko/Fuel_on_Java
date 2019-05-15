package serhij.korneluk.chemlabfuel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class Dialog_delite_confirm extends DialogFragment {

    private Dialog_delite_confirm_listiner listiner;

    static Dialog_delite_confirm getInstance(String title, int position) {
        Dialog_delite_confirm opisanie = new Dialog_delite_confirm();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putInt("position", position);
        opisanie.setArguments(bundle);
        return opisanie;
    }

    interface Dialog_delite_confirm_listiner {
        void delite_data(int position);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            try {
                listiner = (Dialog_delite_confirm_listiner) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString() + " must implement Dialog_delite_confirm_listiner");
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView textViewZaglavie = new TextView(getActivity());
        textViewZaglavie.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        textViewZaglavie.setPadding(10, 10, 10, 10);
        textViewZaglavie.setText(R.string.remove);
        textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textViewZaglavie.setTypeface(null, Typeface.BOLD);
        textViewZaglavie.setTextColor(getResources().getColor(R.color.colorIcons));
        linearLayout.addView(textViewZaglavie);
        TextView textView = new TextView(getActivity());
        textView.setPadding(10, 10, 10, 10);
        textView.setText(getString(R.string.remove_conform, getArguments().getString("title")));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary_text));
        linearLayout.addView(textView);
        ad.setView(linearLayout);
        ad.setPositiveButton(getString(R.string.delite), (dialog, which) -> listiner.delite_data(getArguments().getInt("position")));
        ad.setNegativeButton(getString(R.string.cansel), (dialog, which) -> dialog.cancel());
        AlertDialog alert = ad.create();
        alert.setOnShowListener(dialog -> {
            Button btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE);
            btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            Button btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE);
            btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        });
        return alert;
    }
}
