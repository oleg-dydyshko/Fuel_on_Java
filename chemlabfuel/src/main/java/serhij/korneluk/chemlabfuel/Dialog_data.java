package serhij.korneluk.chemlabfuel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Dialog_data extends DialogFragment {

    private Dialog_data_listiner listiner;

    static Dialog_data getInstance(long data, int textview, String title) {
        Dialog_data opisanie = new Dialog_data();
        Bundle bundle = new Bundle();
        bundle.putLong("data", data);
        bundle.putInt("textview", textview);
        bundle.putString("title", title);
        opisanie.setArguments(bundle);
        return opisanie;
    }

    interface Dialog_data_listiner {
        void set_data(int textview, int year, int month, int dayOfMonth);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            try {
                listiner = (Dialog_data_listiner) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString() + " must implement Dialog_data_listiner");
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_data, null);
        TextView today = view.findViewById(R.id.today);
        TextView textView1 = view.findViewById(R.id.textView1);
        TextView textView2 = view.findViewById(R.id.textView2);
        TextView title = view.findViewById(R.id.title);
        title.setText(getArguments().getString("title"));
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        calendarView.setDate(getArguments().getLong("data"));
        today.setOnClickListener((v -> {
            GregorianCalendar c = (GregorianCalendar) Calendar.getInstance();
            calendarView.setDate(c.getTimeInMillis());
        }));
        textView1.setOnClickListener((v -> {
            GregorianCalendar c = new GregorianCalendar();
            c.setTimeInMillis(calendarView.getDate());
            c.add(Calendar.YEAR, -1);
            calendarView.setDate(c.getTimeInMillis());
        }));
        textView2.setOnClickListener((v -> {
            GregorianCalendar c = new GregorianCalendar();
            c.setTimeInMillis(calendarView.getDate());
            c.add(Calendar.YEAR, 1);
            calendarView.setDate(c.getTimeInMillis());
        }));
        int textview = getArguments().getInt("textview");
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            listiner.set_data(textview, year, month, dayOfMonth);
            getDialog().cancel();
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        if (textview == 7 || textview == 9 || textview == 10) {
            builder.setNeutralButton("Удалить дату", (dialog, which) -> listiner.set_data(getArguments().getInt("textview"), 0, 0, 0));
            builder.setPositiveButton("Отмена", (dialog, which) -> dialog.cancel());
        }
        if (textview == 3 || textview == 1) {
            builder.setPositiveButton("Отмена", (dialog, which) -> dialog.cancel());
        }
        if (textview == 8) {
            builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
            builder.setPositiveButton("Установить месяц", (dialog, which) -> {
                GregorianCalendar c = new GregorianCalendar();
                c.setTimeInMillis(calendarView.getDate());
                listiner.set_data(textview, c.get(Calendar.YEAR), c.get(Calendar.MONTH), -1);
            });
        }
        AlertDialog alert = builder.create();
        alert.setOnShowListener(dialog -> {
            Button btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE);
            btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            Button btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE);
            btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            Button btnNeutral = alert.getButton(Dialog.BUTTON_NEUTRAL);
            btnNeutral.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        });
        return alert;
    }
}
