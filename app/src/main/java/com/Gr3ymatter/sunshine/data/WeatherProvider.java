package com.Gr3ymatter.sunshine.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Afzal on 7/26/14.
 */
public class WeatherProvider extends ContentProvider {

    /*
    Content Providers makes us think of viewdata in the form of Uri's.
    Views can display different data based on the active Uri.
    The Data Layer gets abstracted in a way that other apps dont need to know about
    the data Layer. You can also switch out the datalayer without much effort.

    Each Uri type is tied to an integer constant

    NOTE: Make sure to add the provider in the ManifestFile.
     */
    private static final int WEATHER = 100;
    private static final int WEATHER_WITH_LOCATION = 101;
    private static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    private static final int LOCATION = 300;
    private static final int LOCATION_ID = 301;

    /* Use Uri matcher to match the Uri with the integer constants */

    private static UriMatcher mURiMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mURiMatcher.addURI(WeatherContract.CONTENT_AUTHORITY,WeatherContract.PATH_WEATHER, WEATHER);
        mURiMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER + "/*" , WEATHER_WITH_LOCATION);
        mURiMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER + "/*/*",WEATHER_WITH_LOCATION_AND_DATE);
        mURiMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_LOCATION , LOCATION);
        mURiMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_LOCATION + "/#" , LOCATION_ID);
    }


    WeatherDbHelper mDbHelper;

    /*
    SQLiteQueryBuilder is used to do an INNER JOIN between the two tables in the database
    This is done by the function setTables().
    Hence it is "weather INNER JOIN location ON weather.location_id = location._ID
    Everytime Location is accessed from the location table, all the corresponding rows for the same
     location id in the weather table are also provided

     NOTE: This inner join is used when querying for the weather using location and location with date
     */
    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;

    static {
        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
        sWeatherByLocationSettingQueryBuilder.setTables(
                WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
                        WeatherContract.LocationEntry.TABLE_NAME +
                        " ON " + WeatherContract.WeatherEntry.TABLE_NAME +
                        "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY +
                        " = " + WeatherContract.LocationEntry.TABLE_NAME +
                        "." + WeatherContract.LocationEntry._ID);

    }

    /* Strings used in selection parameter. They are similar to the WHERE conditions used in SQL
       These strings define the constraints. As an example, sLocationSettingWithStartDateSelection
        applies the constraint of getting the location for dates after a particular start date
        */
    private static final String sLocationSettingSelection = WeatherContract.LocationEntry.TABLE_NAME+"."
            + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? ";

    private static final String sLocationSettingWithStartDateSelection = WeatherContract.LocationEntry.TABLE_NAME +
            "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? " + " AND " +
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry.COLUMN_DATETEXT +
            " >= ? ";

    private static final String sLocationSettingWithDate = WeatherContract.LocationEntry.TABLE_NAME +
            "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? " + " AND " +
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry.COLUMN_DATETEXT +
            " = ? ";



    private Cursor getWeatherByLocationSetting(Uri uri, String[] projection, String sortOrder){
        String LocationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        String startDate = WeatherContract.WeatherEntry.getDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if(startDate == null){
            selection = sLocationSettingSelection;
            selectionArgs = new String[]{LocationSetting};
        } else {
            selection = sLocationSettingWithStartDateSelection;
            selectionArgs = new String[]{LocationSetting, startDate};
        }

        return sWeatherByLocationSettingQueryBuilder.query(
                mDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );


    }

    private Cursor getWeatherByLocationSettingWithDate(Uri uri, String[] projection, String sortOrder){
        String LocationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        String startDate = WeatherContract.WeatherEntry.getDateFromUri(uri);

        String[] selectionArgs = new String[] {LocationSetting, startDate};
        String selection = sLocationSettingWithDate;


        return sWeatherByLocationSettingQueryBuilder.query(
                mDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }



    @Override
    public boolean onCreate() {
        mDbHelper = new WeatherDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;

        switch(mURiMatcher.match(uri))
        {
            // "weather/*"
            case WEATHER_WITH_LOCATION:
                retCursor = getWeatherByLocationSetting(uri, projection, sortOrder);
                break;
            // "weather"
            case WEATHER:
                retCursor = mDbHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            // "weather/*/*"
            case WEATHER_WITH_LOCATION_AND_DATE:
                retCursor = getWeatherByLocationSettingWithDate(uri, projection, sortOrder);
                break;
            // "location"
            case LOCATION:
                retCursor = mDbHelper.getReadableDatabase().query(
                        WeatherContract.LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            // "location/#"
            case LOCATION_ID:
                String row = String.valueOf(ContentUris.parseId(uri));
                retCursor = mDbHelper.getReadableDatabase().query(
                        WeatherContract.LocationEntry.TABLE_NAME,
                        projection,
                        WeatherContract.LocationEntry._ID + " = '" + ContentUris.parseId(uri) +"'",
//                        row,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);

        }

        retCursor.setNotificationUri(getContext().getContentResolver(),uri);

        return retCursor;
    }

    @Override
    public String getType(Uri uri) {

       final int match =  mURiMatcher.match(uri);

        switch(match)
        {
            case WEATHER_WITH_LOCATION_AND_DATE:
                return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
            case WEATHER_WITH_LOCATION:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case WEATHER:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case LOCATION:
                return WeatherContract.LocationEntry.CONTENT_TYPE;
            case LOCATION_ID:
                return WeatherContract.LocationEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {




        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
