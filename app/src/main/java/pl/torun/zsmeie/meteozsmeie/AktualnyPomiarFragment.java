package pl.torun.zsmeie.meteozsmeie;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;


public class AktualnyPomiarFragment extends android.support.v4.app.Fragment {


    private OnFragmentInteractionListener mListener;

    private TextView mTemperaturaTextView;
    private TextView mWilgotnoscTextView;
    private TextView mCisnienieTextView;
    private TextView mPredkoscWiatruTextView;
    private TextView mKierunekWiatruTextView;
    private TextView mDataTextView;
    private TextView mErrorTextView;
    ProgressBar mProgressBar;
    AsyncTask mPobieranie;

    public static AktualnyPomiarFragment newInstance() {
        AktualnyPomiarFragment fragment = new AktualnyPomiarFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_aktualny_pomiar, container, false);

        mTemperaturaTextView = (TextView) rootView.findViewById(R.id.temperatura);
        mWilgotnoscTextView = (TextView) rootView.findViewById(R.id.wilgotnosc);
        mCisnienieTextView = (TextView) rootView.findViewById(R.id.cisnienie);
        mPredkoscWiatruTextView = (TextView) rootView.findViewById(R.id.predkoscWiatru);
        mKierunekWiatruTextView = (TextView) rootView.findViewById(R.id.kierunekWiatru);
        mDataTextView = (TextView) rootView.findViewById(R.id.data);
        mErrorTextView = (TextView) rootView.findViewById(R.id.error_tv);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        mPobieranie = new PobierzAktualnyPomiar().execute();

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_example: {
                if (mPobieranie.getStatus() == AsyncTask.Status.FINISHED) {
                    mPobieranie = new PobierzAktualnyPomiar().execute();
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);

    }

    private class PobierzAktualnyPomiar extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... arg0) {
            String url = "http://meteo.carokpl.usermd.net/ajax/aktualnyPomiar";
            try {

                String result = HttpRequest.get(url).body();

                Log.d("MyApp", "Result> " + result);
                return result;
            } catch (HttpRequest.HttpRequestException e) {
               Log.e("MyApp", "Error" + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                try {
                    mErrorTextView.setVisibility(View.GONE);

                    JSONObject resultJson = new JSONObject(result);
                    Boolean status = resultJson.getBoolean("status");
                    if (status) {

                        JSONObject measurements = resultJson.getJSONObject("measurements");

                        mTemperaturaTextView.setText(measurements.getString("temperatura") + " °C");
                        mWilgotnoscTextView.setText(measurements.getString("wilgotnosc") + " %");
                        mCisnienieTextView.setText(measurements.getString("cisnienie") + " Hpa");
                        mPredkoscWiatruTextView.setText(measurements.getString("predkosc_wiatru") + " Km/h");
                        mKierunekWiatruTextView.setText(measurements.getString("kierunek_wiatru"));
                        mDataTextView.setText(measurements.getString("data") + " " + measurements.getString("godzina"));
                    }

                } catch (JSONException e) {
                    Log.e("MyApp", e.getMessage());
                    mErrorTextView.setVisibility(View.VISIBLE);
                }
            } else {
                mErrorTextView.setVisibility(View.VISIBLE);
                Log.e("MyApp", "Couldn't get any data from the url");
            }
            mProgressBar.setVisibility(View.GONE);
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
        mListener = null;
    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
