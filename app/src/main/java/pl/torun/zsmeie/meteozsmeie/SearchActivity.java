package pl.torun.zsmeie.meteozsmeie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


public class SearchActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {


    private String mMonth;
    private String mYear;


    private Button mSearchButton;
    private EditText mDayEditText;
    private Spinner mMonthSpinner;
    private Spinner mYearSpinner;
    private EditText mHourEditText;
    private EditText mMinuteEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar _toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(_toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDayEditText = (EditText) findViewById(R.id.day_et);
        mMonthSpinner = (Spinner) findViewById(R.id.month_sp);
        mYearSpinner = (Spinner) findViewById(R.id.year_sp);
        mHourEditText = (EditText) findViewById(R.id.hour_et);
        mMinuteEditText = (EditText) findViewById(R.id.minute_et);
        mSearchButton = (Button) findViewById(R.id.search_btn);

        getExtrasData();

        mSearchButton.setOnClickListener(this);
        mMonthSpinner.setOnItemSelectedListener(this);
        mYearSpinner.setOnItemSelectedListener(this);
    }


    private void getExtrasData(){
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            mMonth = extras.getString("month");
            if (mMonth != null)
                mMonthSpinner.setSelection(Integer.parseInt(mMonth));

            mYear = extras.getString("year");
            if (mYear != null)
                mYearSpinner.setSelection(Integer.parseInt(mYear) - 2009);

            mDayEditText.setText(extras.getString("day"));
            mHourEditText.setText( extras.getString("hour"));
            mMinuteEditText.setText(extras.getString("minute"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_seatch, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        switch (id) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_btn:
                Intent intent = new Intent();
                intent.putExtra("day", mDayEditText.getText().toString());
                intent.putExtra("month", mMonth);
                intent.putExtra("year", mYear);
                intent.putExtra("hour", mHourEditText.getText().toString());
                intent.putExtra("minute", mMinuteEditText.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {
            case R.id.month_sp: {
                mMonth = String.valueOf(position);
                break;
            }
            case R.id.year_sp: {
                if (position == 0)
                    mYear = String.valueOf(0);
                else
                    mYear = parent.getItemAtPosition(position).toString();
                break;
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
