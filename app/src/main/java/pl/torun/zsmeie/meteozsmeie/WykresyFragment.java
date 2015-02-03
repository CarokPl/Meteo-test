package pl.torun.zsmeie.meteozsmeie;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import pl.torun.zsmeie.meteozsmeie.adapter.PagerAdapter;
import pl.torun.zsmeie.meteozsmeie.fragmenty.WykresFragment;


public class WykresyFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private PagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private ImageView mPrevButton;
    private ImageView mNextButton;
    private TextView mChartNameEditText;


    public static WykresyFragment newInstance() {
        WykresyFragment fragment = new WykresyFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_wykresy, container, false);

        mViewPager = (ViewPager) rootView.findViewById(R.id.wykresy_vp);
        mPrevButton = (ImageView) rootView.findViewById(R.id.prev_btn);
        mNextButton = (ImageView) rootView.findViewById(R.id.next_btn);
        mChartNameEditText = (TextView) rootView.findViewById(R.id.chart_name_tv);

        mPagerAdapter = new PagerAdapter(getChildFragmentManager(), getActivity(), mViewPager, mChartNameEditText);
        mViewPager.setOffscreenPageLimit(3);
        setPagerAdapter();
        navigationButtonAction();

        return rootView;
    }

    private void setPagerAdapter() {

        Bundle args = new Bundle();
        args.putString("wykresType", "temperatura");
        mPagerAdapter.addTab(WykresFragment.class, args, "Temperatura");
        mChartNameEditText.setText("Temperatura");

        args = new Bundle();
        args.putString("wykresType", "cisnienie");
        mPagerAdapter.addTab(WykresFragment.class, args, "Ciśnienie");

        args = new Bundle();
        args.putString("wykresType", "wilgotnosc");
        mPagerAdapter.addTab(WykresFragment.class, args, "Wilgotność");
    }

    private void navigationButtonAction() {
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int currentItem = mViewPager.getCurrentItem();
                if (currentItem != 0)
                    mViewPager.setCurrentItem(currentItem - 1);
            }
        });
        mNextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int currentItem = mViewPager.getCurrentItem();
                if (currentItem != 2)
                    mViewPager.setCurrentItem(currentItem + 1);
            }
        });
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
