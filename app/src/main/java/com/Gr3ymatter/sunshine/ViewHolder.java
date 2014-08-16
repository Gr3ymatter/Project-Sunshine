package com.Gr3ymatter.sunshine;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Afzal on 8/2/14.
 */
public class ViewHolder {

    public final TextView dateView;
    public final TextView forecastView;
    public final TextView highTempView;
    public final TextView lowTempView;
    public final ImageView iconView;


    public ViewHolder(View view){
        dateView = (TextView)view.findViewById(R.id.list_item_date_textview);
        forecastView = (TextView)view.findViewById(R.id.list_item_forecast_textview);
        highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
        lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        iconView = (ImageView)view.findViewById(R.id.list_item_weather_image);
    }


}
