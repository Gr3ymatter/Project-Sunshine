package com.Gr3ymatter.sunshine;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.Gr3ymatter.sunshine.data.WeatherContract;
import com.Gr3ymatter.sunshine.data.WeatherContract.LocationEntry;
import com.Gr3ymatter.sunshine.data.WeatherDbHelper;

import java.util.Map;
import java.util.Set;


/**
 * Created by Afzal on 7/22/14.
 */
public class TestProvider extends AndroidTestCase {

    final String LOG_TAG = TestProvider.class.getSimpleName();
    static public String TEST_LOCATION = "99736";
    static public String TEST_DATE = "3233923";

    public void testDeleteDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);

    }

    //Test to check if the getType function works.
    //It should match the types defined in the WeatherContract
    public void testGetType() {

        String type = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.CONTENT_URI);

        assertEquals(type, WeatherContract.WeatherEntry.CONTENT_TYPE);

        String testLocation = "95136";

        type = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.buildWeatherLocation(testLocation));

        assertEquals(type, WeatherContract.WeatherEntry.CONTENT_TYPE);

        String testDate = "34334";
        type = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(testLocation, testDate));

        assertEquals(WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE,type);

        type = mContext.getContentResolver().getType(LocationEntry.CONTENT_URI);

        assertEquals(type, LocationEntry.CONTENT_TYPE);

        type = mContext.getContentResolver().getType(LocationEntry.buildLocationUri(1L));

        assertEquals(type, LocationEntry.CONTENT_ITEM_TYPE);

    }



    private ContentValues createLocationValues(){
        String testName = "North Pole";
        String testLocationSetting = "95136";
        double testLatitude = 64.772;
        double testLongitude = -147.355;
        String postalCode = "95136";


        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_CITY_NAME, testName);
        values.put(LocationEntry.COLUMN_POSTAL_CODE, postalCode);
        values.put(LocationEntry.COLUMN_LOCATION_SETTING, TEST_LOCATION);
        values.put(LocationEntry.COLUMN_COORD_LAT, testLatitude);
        values.put(LocationEntry.COLUMN_COORD_LONG, testLongitude);

        return values;
    }


    private ContentValues createWeatherValues(long locationRowId){
        final String dateValue = "26th Oct 1986";
        final int weatherID = 602;
        final String shortDesc = "Hot and Humid";
        double minTemp = 23.0;
         double maxTemp = 60.4;
         double humidity = 233.55;
         double degrees = 60.3;
         double pressure = 222;
         double windSpeed = 33;


        ContentValues weatherValues = new ContentValues();
//        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationRowId);
//        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATETEXT, dateValue);
//        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, degrees);
//        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
//        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
//        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherID);
//        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
//        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, shortDesc);
//        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, maxTemp);
//        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, minTemp);

        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATETEXT, TEST_DATE);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, 75);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, 65); //Test fails if 65.0 is used.
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, 321);

        //Retrieval from the database results in an Int string if 65.0 is used.
        //They are not the same and so the test fails.


        return weatherValues;

    }


    public void testInsertReadProvider() {

        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long locationRowId;
        ContentValues testValues = createLocationValues();
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null,testValues);

        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New Row Id: " +locationRowId);

        Cursor locationCursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null);


        validateCursor(testValues, locationCursor);

        locationCursor = mContext.getContentResolver().query(
                LocationEntry.buildLocationUri(locationRowId),
                null,
                null,
                null,
                null
        );

        validateCursor(testValues, locationCursor);

        long weatherRowId;
        testValues = createWeatherValues(locationRowId);
        weatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, testValues);
        assertTrue(weatherRowId != -1);

        Cursor weatherCursor = mContext.getContentResolver().query(WeatherContract.WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        validateCursor(testValues, weatherCursor);

        weatherCursor.close();

        weatherCursor = mContext.getContentResolver().query(WeatherContract.WeatherEntry.buildWeatherLocation(TEST_LOCATION),
                null,
                null,
                null,
                null);

        validateCursor(testValues, weatherCursor);

        weatherCursor.close();

        weatherCursor = mContext.getContentResolver().query(WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(TEST_LOCATION, TEST_DATE),
                null,
                null,
                null,
                null);

        validateCursor(testValues, weatherCursor);

        weatherCursor.close();

        weatherCursor = mContext.getContentResolver().query(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(TEST_LOCATION, TEST_DATE),
                null,
                null,
                null,
                null);

        validateCursor(testValues, weatherCursor);

    }

    private void validateCursor(ContentValues expectedValues, Cursor valueCursor)
    {
        assertTrue(valueCursor.moveToFirst());

        Set <Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for(Map.Entry<String, Object> entry:valueSet)
        {
            String columnName = entry.getKey();
            int columnIndex = valueCursor.getColumnIndex(columnName);
            assertFalse(columnIndex == -1);
            String expectedValue = entry.getValue().toString();
            String cursorValue = valueCursor.getString(columnIndex);
            Log.d(LOG_TAG, "Expected Value = " + expectedValue + " and Cursor Value = " + cursorValue);


            assertEquals(expectedValue, cursorValue);

        }
        valueCursor.close();
    }







}
