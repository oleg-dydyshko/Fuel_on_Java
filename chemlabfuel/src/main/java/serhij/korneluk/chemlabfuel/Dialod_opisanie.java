package serhij.korneluk.chemlabfuel;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class Dialod_opisanie extends DialogFragment {

    static Dialod_opisanie getInstance(String title, String string) {
        Dialod_opisanie opisanie = new Dialod_opisanie();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("string", string);
        opisanie.setArguments(bundle);
        return opisanie;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        ScrollView scrollView = new ScrollView(getActivity());
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView textViewZaglavie = new TextView(getActivity());
        textViewZaglavie.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        textViewZaglavie.setPadding(10, 10, 10, 10);
        textViewZaglavie.setText(getArguments().getString("title"));
        textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textViewZaglavie.setTypeface(null, Typeface.BOLD);
        textViewZaglavie.setTextColor(getResources().getColor(R.color.colorIcons));
        linearLayout.addView(textViewZaglavie);
        TextView textView = new TextView(getActivity());
        textView.setPadding(10, 10, 10, 10);
        textView.setText(Html.fromHtml(getArguments().getString("string")));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary_text));
        linearLayout.addView(scrollView);
        scrollView.addView(textView);
        builder.setPositiveButton(getString(R.string.good), (dialog, which) -> dialog.cancel());
        builder.setView(linearLayout);
        AlertDialog alert = builder.create();
        alert.setOnShowListener(dialog -> {
            Button btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE);
            btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        });
        return alert;
    }
}
