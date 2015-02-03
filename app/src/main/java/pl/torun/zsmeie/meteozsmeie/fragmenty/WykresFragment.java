package pl.torun.zsmeie.meteozsmeie.fragmenty;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import pl.torun.zsmeie.meteozsmeie.R;


public class WykresFragment extends Fragment {


    private WebView mWykresWebView;
    private ProgressBar mProgressBar;
    private String mWykresType;


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

        mWykresWebView = (WebView) rootView.findViewById(R.id.wykres_vw);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        mWykresWebView.getSettings().setJavaScriptEnabled(true);
        mWykresWebView.getSettings().setDefaultTextEncodingName("utf-8");


        //new PobierzWykres().execute();
        new getChart().execute();
        return rootView;
    }


    private class getChart extends AsyncTask<String, Long, String> {

        protected String doInBackground(String... urls) {
            try {

                String url = "http://meteo.carokpl.usermd.net/ajax/wykresy";

                Map<String, String> data = new HashMap<>();
                data.put("wykres", mWykresType);
                String result = HttpRequest.post(url)
                        .form(data)
                        .body();
                Log.e("MyApp", result);
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
                    if (resultJson.getBoolean("status")) {
                        JSONArray content = resultJson.getJSONArray("content");
                        for (int i = 0; i < content.length(); i++) {
                            JSONObject pomier = content.getJSONObject(i);
                            String data = pomier.getString("data");
                            String value = pomier.getString(mWykresType);
                            chartData += "['" + data + "', " + value + "],";
                        }
                    }

                    String html = ReadFromFile("html_template.html", getActivity());
                    if (html != null) {
                        html = html.replace("{{site_url}}", "http://meteo.carokpl.usermd.net/");
                        html = html.replace("{{padding_left}}", "25");
                        html = html.replace("{{chart_data}}", chartData);
                        mWykresWebView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
                    }

                } catch (JSONException e) {
                    Log.d("MyApp", "Download failed");
                }

            else
                Log.d("MyApp", "Download failed");

            mProgressBar.setVisibility(View.GONE);
            mWykresWebView.setVisibility(View.VISIBLE);
        }

        protected void onPreExecute() {
            super.onPreExecute();
            mWykresWebView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);

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
