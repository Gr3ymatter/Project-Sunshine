package com.Gr3ymatter.sunshine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Afzal on 7/18/14.
 */
public class WeatheDataParser {


    /**
     * Given a string of the form returned by the api call:
     * http://api.openweathermap.org/com.Gr3ymatter.sunshine.data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7
     * retrieve the maximum temperature for the day indicated by dayIndex
     * (Note: 0-indexed, so 0 would refer to the first day).
     */
    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex)
            throws JSONException {

            // TODO: add parsing code here
            JSONArray weatherOfDays = (new JSONObject(weatherJsonStr)).getJSONArray("list");
            if(dayIndex >= weatherOfDays.length())
                return -1;
            JSONObject desiredDay = weatherOfDays.getJSONObject(dayIndex);
            return desiredDay.getJSONObject("temp").getDouble("max");


    }

}