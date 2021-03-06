package pl.torun.zsmeie.meteozsmeie.fragmenty;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import pl.torun.zsmeie.meteozsmeie.R;


public class WykresFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {


    private WebView mWykresWebView;
    private ProgressBar mProgressBar;
    protected String mWykresType;

    private TextView mDateFromTextView;
    private TextView mDataToTextView;
    private Spinner mMeasurementCountSpinner;

    private DatePickerDialog mFromDatePickerDialog;

    private SimpleDateFormat mDateFormatter;
    private SimpleDateFormat mDateFormatterMySql;
    private String mDateFrom;
    private String mMeasurementCount;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_wykres, container, false);

        Bundle args = getArguments();
        mWykresType = args.getString("wykresType");

        mDateFormatter = new SimpleDateFormat("dd MMMM yyyy");
        mDateFormatterMySql = new SimpleDateFormat("yyyy-MM-dd");

        mWykresWebView = (WebView) rootView.findViewById(R.id.wykres_vw);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mDateFromTextView = (TextView) rootView.findViewById(R.id.date_from_et);
        mDataToTextView = (TextView) rootView.findViewById(R.id.date_from_et);
        mMeasurementCountSpinner = (Spinner) rootView.findViewById(R.id.measurement_count_sp);

        mMeasurementCountSpinner.setSelection(1);

        mWykresWebView.getSettings().setJavaScriptEnabled(true);
        mWykresWebView.getSettings().setDefaultTextEncodingName("utf-8");

        mDateFromTextView.setOnClickListener(this);
        mMeasurementCountSpinner.setOnItemSelectedListener(this);


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -1);
        mDateFrom = mDateFormatterMySql.format(calendar.getTime());
        mDateFromTextView.setText(mDateFormatter.format(calendar.getTime()));

        setDateTimeField();
        //new PobierzWykres().execute();
        //new getChart().execute();
        return rootView;
    }


    private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.add(Calendar.MONTH, -1);
        mFromDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Log.e("MyApp", "from");
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mDateFromTextView.setText(mDateFormatter.format(newDate.getTime()));
                mDateFrom = mDateFormatterMySql.format(newDate.getTime());
                new getChart().execute();
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date_from_et:
                mFromDatePickerDialog.show();
                break;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                mMeasurementCount = "10";
                break;
            case 1:
                mMeasurementCount = "30";
                break;
            case 2:
                mMeasurementCount = "50";
                break;
            default:
                mMeasurementCount = "30";
        }
        new getChart().execute();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private class getChart extends AsyncTask<String, Long, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            mWykresWebView.loadUrl("about:blank");
            mWykresWebView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);

        }

        protected String doInBackground(String... urls) {
            try {

                //String url = "http://meteo.carokpl.usermd.net/ajax/wykresy";
                String url = "http://meteozsmeie.zz.vc/ajax/wykresy";

                Map<String, String> data = new HashMap<>();
                data.put("chartType", mWykresType);
                data.put("dateFrom", mDateFrom);
                data.put("measurementCount", mMeasurementCount);
                String result = HttpRequest.post(url)
                        .form(data)
                        .body();
                Log.e("MyApp", mWykresType + result);
                return result;
            } catch (HttpRequest.HttpRequestException exception) {
                Log.e("MyApp", exception.getMessage());
                return null;
            }
        }

        protected void onProgressUpdate(Long... progress) {
            Log.d("MyApp", "Downloaded bytes: " + progress[0]);
        }

        protected void onPostExecute(String result) {
            if (result != null)
                try {
                    String chartData = "";

                    JSONObject resultJson = new JSONObject(result);

                    JSONArray content = resultJson.getJSONArray("test");

                    if (resultJson.getBoolean("status") && content != null) {

                        for (int i = 0; i < content.length(); i++) {
                            JSONObject pomier = content.getJSONObject(i);
                            String data = pomier.getString("data");
                            String value = pomier.getString(mWykresType);
                            if (data != null && !TextUtils.isEmpty(data) && !TextUtils.equals(data, "null"))
                                chartData += "['" + data + "', " + value + "],";
                        }

                        String html = ReadFromFile("html_template.html", getActivity());
                        if (html != null) {
                            html = html.replace("{{padding_left}}", "25");
                            html = html.replace("{{chart_data}}", chartData);
                            mWykresWebView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
                        }
                    } else {

                    }


                } catch (JSONException e) {
                    Log.d("MyApp", "Download failed");
                }

            else
                Log.d("MyApp", "Download failed");

            mProgressBar.setVisibility(View.GONE);
            mWykresWebView.setVisibility(View.VISIBLE);
        }


    }

    private String ReadFromFile(String fileName, Context context) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets().open(fileName, Context.MODE_PRIVATE);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
        return returnString.toString();
    }


}
