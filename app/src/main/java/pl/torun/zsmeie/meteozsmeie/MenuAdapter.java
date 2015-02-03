package pl.torun.zsmeie.meteozsmeie;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class MenuAdapter extends ArrayAdapter<MenuAdapter.MenuItem> {


    private final LayoutInflater mInflater;
    private final int mResourceId;

    static final class MenuItem {
        private final String name;
        private final Drawable icon;

        MenuItem(String _name, Drawable _icon) {
            name = _name;
            icon = _icon;
        }
    }

    public MenuAdapter(Context context, int resource) {
        super(context, resource);
        mInflater = LayoutInflater.from(context);
        mResourceId = resource;
    }

    public void addMenuItem(String name, Drawable icon) {
        MenuItem item = new MenuItem(name, icon);
        add(item);
        notifyDataSetChanged();
    }

   /* public void setParametr(HashMap<String, String> objects) {
        clear();
        for (int i=0; i < objects.size(); i++){
            add(objects.g);
        }


*//*
        for (JSONObject object : objects) {
            add(object);
        }*//*

        if (isEmpty()) {
            notifyDataSetInvalidated();
        } else {
            notifyDataSetChanged();
        }
    }*/

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (view == null) {

            view = mInflater.inflate(R.layout.fragment_navigation_drawer_list, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.mNameTextView = (TextView) view.findViewById(R.id.sectionName_tv);
            viewHolder.mIconImageView = (ImageView) view.findViewById(R.id.sectionIcon_iv);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        MenuItem menuItem = getItem(position);

        viewHolder.mNameTextView.setText(menuItem.name);
        viewHolder.mIconImageView.setImageDrawable(menuItem.icon);


        return view;
    }

    private static class ViewHolder {
        TextView mNameTextView;
        ImageView mIconImageView;
    }


}
