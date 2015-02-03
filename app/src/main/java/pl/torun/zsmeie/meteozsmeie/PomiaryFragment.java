package pl.torun.zsmeie.meteozsmeie;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PomiaryFragment extends android.support.v4.app.Fragment {

    private String mDay;
    private String mMonth;
    private String mYear;
    private String mHour;
    private String mMinute;

    private ProgressBar mProgressBar;
    private ListView mMeasurementsListView;
    private TextView mErrorTextView;

    PomiaryAdapter mAdapter;

    getMeasurements mPobieranie;

    private OnFragmentInteractionListener mListener;
    private int REQUEST_SEARCH = 432;


    public static PomiaryFragment newInstance() {
        PomiaryFragment fragment = new PomiaryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SEARCH && resultCode == Activity.RESULT_OK) {
            mDay = data.getStringExtra("day");
            mMonth = data.getStringExtra("month");
            mYear = data.getStringExtra("year");
            mHour = data.getStringExtra("hour");
            mMinute = data.getStringExtra("minute");

            mPobieranie.cancel(true);
            mPobieranie = new getMeasurements();
            mPobieranie.execute();

            Log.e("MyApp", "mon: " + mMonth + " day: " + mDay + " year: " + mYear + " hour: " + mHour + " minute: " + mMinute);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.pomiary, menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View viewRoot = inflater.inflate(R.layout.fragment_pomiary, container, false);

        mMeasurementsListView = (ListView) viewRoot.findViewById(R.id.pomiary_lv);
        mProgressBar = (ProgressBar) viewRoot.findViewById(R.id.progressBar);
        mErrorTextView = (TextView) viewRoot.findViewById(R.id.error_tv);

        mPobieranie = new getMeasurements();
        mPobieranie.execute();


        mAdapter = new PomiaryAdapter(getActivity(), R.layout.adapter_pomiary);
        mMeasurementsListView.setAdapter(mAdapter);

        return viewRoot;
    }

    //awdadw


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_example: {
                if (mPobieranie.getStatus() == AsyncTask.Status.FINISHED) {
                    mPobieranie = new getMeasurements();
                    mPobieranie.execute();
                } else {
                    Log.e("MyApp", "No refresh");
                }
                return true;
            }
            case R.id.action_bar_search:

                Intent intent = new Intent(getActivity().getBaseContext(), SearchActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("day", mDay);
                bundle.putString("month", mMonth);
                bundle.putString("year", mYear);
                bundle.putString("hour", mHour);
                bundle.putString("minute", mMinute);
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_SEARCH);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class getMeasurements extends AsyncTask<Void, Void, String> {


        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            mMeasurementsListView.setVisibility(View.GONE);
            Log.e("MyApp", "refresh");
        }

        @Override
        protected String doInBackground(Void... v) {

            String url = "http://meteo.carokpl.usermd.net/ajax/pomiary";
            try {

                Map<String, String> postData = new HashMap<>();

                postData.put("day", mDay);
                postData.put("month", mMonth);
                postData.put("year", mYear);
                postData.put("hour", mHour);
                postData.put("minute", mMinute);

                String result = HttpRequest.post(url)
                        .form(postData)
                        .body();

                Log.d("MyApp", "Result> " + result);

                return result;
            } catch (HttpRequest.HttpRequestException e) {
                Log.e("MyApp", "Error" + e.getMessage());
                return null;
            }

        }

        protected void onPostExecute(String result) {

            if(result != null) {
                try {

                    JSONArray jsonArray = new JSONArray(result);
                    ArrayList<JSONObject> list = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        list.add(jsonArray.getJSONObject(i));
                    }

                    mAdapter.setParametr(list);

                } catch (JSONException e) {
                    mErrorTextView.setVisibility(View.VISIBLE);
                    Log.e("MyApp", "Error" + e.getMessage());
                }
            }
            else{
                mErrorTextView.setVisibility(View.VISIBLE);
                Log.e("MyApp", "Couldn't get any data from the url");
            }
            mProgressBar.setVisibility(View.GONE);
            mMeasurementsListView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPobieranie.cancel(true);
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}
