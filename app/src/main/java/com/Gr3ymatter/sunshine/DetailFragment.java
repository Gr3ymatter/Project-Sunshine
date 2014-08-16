package com.Gr3ymatter.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.Gr3ymatter.sunshine.data.WeatherContract;

import org.apache.http.protocol.HTTP;

/**
     * A DetailFragment  containing a simple view.
     */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

        ShareActionProvider mShareActionProvider;
        private static final int DETAIL_LOADER = 0;
        public static final String DATE_KEY = "date";
        public static final String LOCATION_KEY = "location";
        private String mLocation;
        private String mDate;
        private String mForecastStr;

        TextView mDateView;
        TextView mForeCastView;
        TextView mHighView;
        TextView mLowView;
        TextView mHumidityView;
        TextView mWindView;
        TextView mPressureView;
        ImageView mIconView;
        TextView mDayView;


        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putString(LOCATION_KEY, mLocation);
        }

        final String[] columns = {
                WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_DATETEXT,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
                WeatherContract.WeatherEntry.COLUMN_PRESSURE,
                WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
                WeatherContract.WeatherEntry.COLUMN_DEGREES,
                WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
        };



        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {


            mDate = (String) getArguments().get(DATE_KEY);
            mLocation = Utility.getPreferredLocation(getActivity());
            Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(mLocation, mDate);


            return new CursorLoader(
                    getActivity(),
                    weatherUri,
                    columns,
                    null,
                    null,
                    null);

        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if(data.moveToFirst()){
                String description = data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC));
                String dateText = data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT));
                double high = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP));
                double low = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP));

                boolean isMetric = Utility.isMetric(getActivity());

                mDayView.setText(Utility.getDayName(getActivity(), dateText));
                mDateView.setText(Utility.getFormattedMonthDay(getActivity(), dateText));
                mForeCastView.setText(description);
                mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(data.getInt(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID))));

                mHighView.setText(Utility.formatTemperature(getActivity(), high, isMetric));
                mLowView.setText(Utility.formatTemperature(getActivity(),low, isMetric));

                float pressure = data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE));

                mPressureView.setText(String.format(getString(R.string.format_pressure), pressure));
                mWindView.setText(Utility.getFormattedWind(getActivity(), data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED)), data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES))));
                mHumidityView.setText(String.format(getString(R.string.format_humidity),data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY))));
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            getLoaderManager().restartLoader(DETAIL_LOADER,null, this);

        }

        public DetailFragment() {
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            if(null != savedInstanceState){
                mLocation = savedInstanceState.getString(LOCATION_KEY);
            }

            Bundle args = getArguments();

            if(args != null && args.containsKey(DATE_KEY))
                getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }

        @Override
        public void onResume() {
            super.onResume();
            if(null != mLocation && !mLocation.equals(Utility.getPreferredLocation(getActivity())))
                getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            setHasOptionsMenu(true);


            mDateView = (TextView)rootView.findViewById(R.id.detail_date_textview);
            mForeCastView = (TextView)rootView.findViewById(R.id.detail_item_forecast_textview);
            mHighView = (TextView)rootView.findViewById(R.id.detail_high_textview);
            mLowView = (TextView)rootView.findViewById(R.id.detail_low_textview);
            mHumidityView = (TextView)rootView.findViewById(R.id.detail_humidity_textview);
            mIconView = (ImageView)rootView.findViewById(R.id.detail_item_weather_image);
            mPressureView = (TextView)rootView.findViewById(R.id.detail_pressure_textview);
            mWindView = (TextView)rootView.findViewById(R.id.detail_wind_textview);
            mDayView = (TextView)rootView.findViewById(R.id.detail_day_textView);


            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.detailfragment_menu, menu);
            MenuItem item = menu.findItem(R.id.action_share);
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
            Intent shareIntent = new Intent();
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType(HTTP.PLAIN_TEXT_TYPE);
            shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr + " #Sunshine App");
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

