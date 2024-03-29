package serhij.korneluk.chemlabfuel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    private final String[] data = {"за 45 дней", "за 30 дней", "за 15 дней", "за 10 дней", "за 5 дней", "Никогда"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences fuel = getSharedPreferences("fuel", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = fuel.edit();
        int fontsize = fuel.getInt("fontsize", 18);
        int notifi = fuel.getInt("notification", 0);
        setContentView(R.layout.settings_activity);
        Spinner spinner = findViewById(R.id.spinner9);
        ArrayAdapter<String> adapter = new ListAdapter(this);
        spinner.setAdapter(adapter);
        spinner.setSelection(notifi);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editor.putInt("notification", position);
                editor.apply();
                sendBroadcast(new Intent(SettingsActivity.this, ReceiverSetAlarm.class));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        SeekBar seekBar = findViewById(R.id.seekBar);
        TextView textsize = findViewById(R.id.textsize);
        textsize.setText(getString(R.string.text_size, fontsize));
        textsize.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
        seekBar.setMax(22 - 14);
        seekBar.setProgress((fontsize - 14) / 2);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = progress * 2;
                textsize.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14 + progress);
                textsize.setText(getString(R.string.text_size, 14 + progress));
                editor.putInt("fontsize", 14 + progress);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        setTollbarTheme();
    }

    private void setTollbarTheme() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView title_toolbar = findViewById(R.id.title_toolbar);
        title_toolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        title_toolbar.setText("Настройки");
    }

    @Override
    public void onBackPressed() {
        onSupportNavigateUp();
    }

    @Override
    public void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.alphain, R.anim.alphaout);
    }

    private class ListAdapter extends ArrayAdapter<String> {

        ListAdapter(Context context) {
            super(context, R.layout.simple_list_item2, data);
        }

        @NonNull
        @Override
        public View getView(int position, View mView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (mView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mView = vi.inflate(R.layout.simple_list_item2, parent, false);
                viewHolder = new ViewHolder();
                mView.setTag(viewHolder);
                viewHolder.text = mView.findViewById(R.id.label);
            } else {
                viewHolder = (ViewHolder) mView.getTag();
            }
            viewHolder.text.setText(data[position]);
            return mView;
        }
    }

    private static class ViewHolder {
        TextView text;
    }
}
