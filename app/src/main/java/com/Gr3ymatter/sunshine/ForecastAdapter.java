package com.Gr3ymatter.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Afzal on 8/2/14.
 */
public class ForecastAdapter extends android.support.v4.widget.CursorAdapter {

    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY = 1;
    Context mContext;
    private int viewType;

    boolean mUseTodayLayout;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        View view;

        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == VIEW_TYPE_TODAY){
            view =  inflater.inflate(R.layout.list_item_forecast_today, parent, false);
        } else
            view =  inflater.inflate(R.layout.list_item_forecast, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void useTodayLayout(boolean use){
        mUseTodayLayout = use;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(ForecastFragment.COL_ICON_ID);
        // Use placeholder image for now
        if(getItemViewType(cursor.getPosition()) == VIEW_TYPE_TODAY)
           viewHolder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
        else
            viewHolder.iconView.setImageResource(Utility.getIconResourceForWeatherCondition(weatherId));

        // Read date from cursor
        String dateString = cursor.getString(ForecastFragment.COL_WEATHER_DATE);
        // Find TextView and set formatted date on it
        viewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateString));

        // Read weather forecast from cursor
        String description = cursor.getString(ForecastFragment.COL_WEATHER_SHORT_DESC);
        // Find TextView and set weather forecast on it
        viewHolder.forecastView.setText(description);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        float high = cursor.getFloat(ForecastFragment.COL_WEATHER_MAX_TEMP);
        viewHolder.highTempView.setText(Utility.formatTemperature(mContext, high, isMetric));


        // Read low temperature from cursor
        float low = cursor.getFloat(ForecastFragment.COL_WEATHER_MIN_TEMP);
        viewHolder.lowTempView.setText(Utility.formatTemperature(mContext, low, isMetric));
    }
}
