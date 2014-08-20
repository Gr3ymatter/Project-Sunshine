package com.Gr3ymatter.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;


public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback{

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;

            ForecastFragment fragment  = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            fragment.adapter.useTodayLayout(false);

            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment())
                        .commit();
            }
        } else {
            ForecastFragment fragment  = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            fragment.adapter.useTodayLayout(true);
            mTwoPane = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onItemSelected(String date) {

        if(mTwoPane){

         Bundle args = new Bundle();
         args.putString(DetailFragment.DATE_KEY, date);
         DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(R.id.weather_detail_container, fragment).commit();


        }else{

            Intent dateIntent = new Intent(this, DetailActivity.class);
            dateIntent.putExtra(DetailFragment.DATE_KEY, date);
            startActivity(dateIntent);

        }

    }
}