package pl.torun.zsmeie.meteozsmeie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PomiaryAdapter extends ArrayAdapter<JSONObject> {


    private final LayoutInflater mInflater;
    private final int mResourceId;

    public PomiaryAdapter(Context context, int resource) {
        super(context, resource);
        mInflater = LayoutInflater.from(context);
        mResourceId = resource;
    }

    public void setParametr(List<JSONObject> objects) {
        clear();

        for (JSONObject object : objects) {
            add(object);
        }

        if (isEmpty()) {
            notifyDataSetInvalidated();
        } else {
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (view == null) {

            view = mInflater.inflate(R.layout.adapter_pomiary, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.mDataTextView = (TextView) view.findViewById(R.id.data_tv);
            viewHolder.mGodzinaTextView = (TextView) view.findViewById(R.id.godzina_tv);
            viewHolder.mTemperaturaTextView = (TextView) view.findViewById(R.id.temperatura_tv);
            viewHolder.mCisnienieTextView = (TextView) view.findViewById(R.id.cisnienie_tv);
            viewHolder.mKierunekWiatruTextView = (TextView) view.findViewById(R.id.kierunekWiatru_tv);
            viewHolder.mPredkoscWiatruTextView = (TextView) view.findViewById(R.id.predkoscWiatru_tv);
            viewHolder.mWilgotnoscTextView = (TextView) view.findViewById(R.id.wilgotnosc_tv);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        JSONObject object = getItem(position);

        try {
            viewHolder.mDataTextView.setText(object.getString("data") + "r.");
            viewHolder.mGodzinaTextView.setText(object.getString("godzina"));
            viewHolder.mTemperaturaTextView.setText(object.getString("temperatura") + " °C");
            viewHolder.mCisnienieTextView.setText(object.getString("cisnienie") + " Hpa");
            viewHolder.mKierunekWiatruTextView.setText(object.getString("kierunek_wiatru"));
            viewHolder.mPredkoscWiatruTextView.setText(object.getString("predkosc_wiatru") + " km/h");
            viewHolder.mWilgotnoscTextView.setText(object.getString("wilgotnosc") + " %");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    private static class ViewHolder {
        TextView mDataTextView;
        TextView mGodzinaTextView;
        TextView mTemperaturaTextView;
        TextView mCisnienieTextView;
        TextView mKierunekWiatruTextView;
        TextView mPredkoscWiatruTextView;
        TextView mWilgotnoscTextView;
    }

}