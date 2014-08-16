package com.Gr3ymatter.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.Gr3ymatter.sunshine.data.WeatherContract;

import java.util.Date;

/**
 * Created by Afzal on 7/17/14.
 */

public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String mLocation;
    // Each Loader has an ID. This allows us to have multiple loaders and differentiate
    // between them.
    private static final int FORECAST_LOADER = 0;

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATETEXT,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_SHORT_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;
    public static final int COL_ICON_ID = 6;


    SharedPreferences prefs;
    ForecastAdapter adapter;
    String  WEATHER_DETAIL = "Weather_Detail";
    String FILENAME = "pref_general";
    String location;


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String date);
    }

    public ForecastFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        location = prefs.getString(getString(R.string.location_key), getString(R.string.default_location));

        (menu.findItem(R.id.action_pref_location)).setTitle(getString(R.string.action_pref_location) +location);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.action_refresh:
                updateWeather();
                return true;
            case R.id.action_settings:
                Intent i = new Intent(getActivity(), SettingsActivity.class);
                startActivity(i);
                Toast.makeText(getActivity(),"Settings Launched", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_pref_location:
                prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                location = prefs.getString(getString(R.string.location_key), getString(R.string.default_location));

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = Uri.parse("geo:0,0?q=" + location);
                intent.setData(uri);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null)
                    startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }


    }

    private void updateWeather(){
        FetchWeatherTask task = new FetchWeatherTask(getActivity());

        //The getSharedPreference returns a preference object but the object is not updated
        //When my settings change. i.e i always get the default location key, irregardless
        //of whether i change it settings. If i use Preference Manager, then it works
        // and the preferences get updated.. Am i missing something?

//        prefs = this.getActivity().getSharedPreferences(FILENAME,0);
//
//        String location = prefs.getString(getString(R.string.location_key), getString(R.string.default_location));
//        Log.d("DEBUG", location);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String units = prefs.getString(getString(R.string.location_units_key), "metric");
        location = prefs.getString(getString(R.string.location_key), getString(R.string.default_location));
        Toast.makeText(getActivity(), location, Toast.LENGTH_SHORT).show();
        task.execute(location,units);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);


        adapter = new ForecastAdapter(getActivity(),null, 0);

        ListView listView = (ListView)rootView.findViewById(R.id.listview_forecast);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ForecastAdapter sAdapter = (ForecastAdapter)parent.getAdapter();
                Cursor cursor = sAdapter.getCursor();


                if(null != cursor && cursor.moveToPosition(position)){
                    String dateExtra = cursor.getString(COL_WEATHER_DATE);
                    ((Callback)getActivity()).onItemSelected(dateExtra);
                }
            }
        });

        return rootView;
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, get the String representation for today,
        // and filter the query to return weather only for dates after or including today.
        // Only return data after today.
        updateWeather();
        String startDate = WeatherContract.getDbDateString(new Date());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";

        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                mLocation, startDate);

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        if(mLocation == null || !mLocation.equals(Utility.getPreferredLocation(getActivity()))){
            getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);

    }
}