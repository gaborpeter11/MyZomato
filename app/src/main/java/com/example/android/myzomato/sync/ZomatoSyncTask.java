/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.myzomato.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.example.android.myzomato.Utils.NetworkUtils;
import com.example.android.myzomato.Utils.RestaurantJsonParser;
import com.example.android.myzomato.data.RestaurantTableContents;

import java.net.URL;

public class ZomatoSyncTask {


    /**
     * Performs the network request for updated weather, parses the JSON from that request, and
     * inserts the new weather information into our ContentProvider. Will notify the user that new
     * weather has been loaded if the user hasn't been notified of the weather within the last day
     * AND they haven't disabled notifications in the preferences screen.
     *
     * @param context Used to access utility methods and the ContentResolver
     */
    synchronized public static void syncRestaurant(Context context) {

        String jsonRestaurantResponse;
        URL receptRequestUrl = NetworkUtils.buildUrl("https://developers.zomato.com/api/v2.1/search?entity_id=219&entity_type=city");
        URL receptRequestUrl2 = NetworkUtils.buildUrl("https://developers.zomato.com/api/v2.1/search?entity_id=219&entity_type=city&start=20");

        try {
            jsonRestaurantResponse = NetworkUtils
                    .getResponseFromHttpUrl(receptRequestUrl);

            String jsonRestaurantResponse2 = NetworkUtils
                    .getResponseFromHttpUrl(receptRequestUrl2);

            ContentValues[] vals = RestaurantJsonParser.getRestaurantDataFromJson(jsonRestaurantResponse);
            int jop = vals.length;
            ContentValues[] vals2 = RestaurantJsonParser.getRestaurantDataFromJson(jsonRestaurantResponse2);
            jop = vals.length;
            ContentValues[] array1and2 = concatArrays(vals, vals2);
            jop = array1and2.length;

            if (vals != null && vals.length != 0) {
                ContentResolver sunshineContentResolver = context.getContentResolver();

                sunshineContentResolver.delete(
                        RestaurantTableContents.RestaurantEntry.CONTENT_URI,
                        null,
                        null);

                sunshineContentResolver.bulkInsert(
                        RestaurantTableContents.RestaurantEntry.CONTENT_URI,
                        array1and2);
            }



            System.out.println(jsonRestaurantResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    public static ContentValues[] concatArrays(ContentValues[] one, ContentValues[] two){
        ContentValues[] array1and2 = new ContentValues[one.length + two.length];
        System.arraycopy(one, 0, array1and2, 0, one.length);
        System.arraycopy(two, 0, array1and2, one.length, two.length);
        return array1and2;

    }



}