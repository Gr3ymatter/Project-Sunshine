package com.Gr3ymatter.sunshine.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Afzal on 7/22/14.
 */
public class WeatherContract {


    //Content Authority is usually the package name of the app. It is the
    //core identifying authority used in front of the content:// scheme
    //and identifies the app.
    public static final String CONTENT_AUTHORITY = "com.Gr3ymatter.sunshine.data";

    //Create a base Content Uri. A Uri is a content uri if it has the content scheme.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //A Complete content Uri has path information as well. A path indicates the table
    //or dataset to query.
    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";


    public static final class WeatherEntry implements BaseColumns{

        /*
        For each table entry of our database. We create a Content Uri using the
        path variables.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();


        //Column Names
        public static final String TABLE_NAME = "weather";

        public static final String COLUMN_LOC_KEY = "location_id";
        public static final String COLUMN_DATETEXT = "date";
        public static final String COLUMN_WEATHER_ID = "weather_id";
        public static final String COLUMN_SHORT_DESC = "short_desc";

        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_WIND_SPEED = "wind";
        public static final String COLUMN_DEGREES = "degrees";


         /*
        Mime Type For content providers. Dir returns lists and item returns a single
        item.
         */

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        /*
         Helper methods to abstract the URi encoding information from the outside.
         */

        //Returns WeatherUri for a certian ID. Allows Item weather access
        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        //Returns Weather URi that provides weather information for a particular location.
        //It Can be a list
        public static Uri buildWeatherLocation(String locationSetting) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        //Returns Weather Uri that provides weather information for a particular date and location
        public static Uri buildWeatherLocationWithStartDate(
                String locationSetting, String startDate) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COLUMN_DATETEXT, startDate).build();
        }

        //Returns Weather Uri that provides weather information for a particulat date. Its an Item.
        public static Uri buildWeatherLocationWithDate(String locationSetting, String date) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).appendPath(date).build();
        }

        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getDateFromUri(Uri uri) {

               if(uri.getPathSegments().size() > 2)
                    return uri.getPathSegments().get(2);
               else
                   return null;
        }

        public static String getStartDateFromUri(Uri uri) {
            return uri.getQueryParameter(COLUMN_DATETEXT);
        }


    }

    public static final class LocationEntry implements BaseColumns{
               /*
        For each table entry of our database. We create a Content Uri using the
        path variables.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();


        public static final String TABLE_NAME = "location";

        public static final String COLUMN_CITY_NAME = "city";

        public static final String COLUMN_POSTAL_CODE = "post_code";

        public static final String COLUMN_LOCATION_SETTING = "location_setting";

        public static final String COLUMN_COORD_LAT = "coord_lat";

        public static final String COLUMN_COORD_LONG = "coord_long";


        /*
        Mime Type For content providers. Dir returns lists and item returns a single
        item.
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;


        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }




}
