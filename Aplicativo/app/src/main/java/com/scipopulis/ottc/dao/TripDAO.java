/**

 Projeto OTTC - Operadora de Tecnologia de Transporte Compartilhado
 Copyright (C) <2017> Scipopulis Desenvolvimento e Análise de Dados Ltda

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.

 Authors: Roberto Speicys Cardoso
 Date: 2017-03-20
 */

package com.scipopulis.ottc.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.scipopulis.ottc.model.Trip;

import java.util.ArrayList;
import java.util.List;

public class TripDAO extends SQLiteOpenHelper {
    private Context mContext;

    public TripDAO(Context cc) {
        super(cc, "Ride", null, 1);
        this.mContext=cc;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String  sql = "CREATE TABLE Trip (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, operator TEXT NOT NULL, start TEXT NOT NULL, end TEXT NOT NULL, start_time TEXT NOT NULL, end_time TEXT NOT NULL, tripid TEXT NOT NULL );";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if(i==2) {
            String sql = "DROP TABLE IF EXISTS Trip";
            sqLiteDatabase.execSQL(sql);
            onCreate(sqLiteDatabase);
        }
    }

    public void insert(Trip trip) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues data = getContentValuesTrip(trip);

        db.insert("Trip", null, data);
    }

    @NonNull
    private ContentValues getContentValuesTrip(Trip trip) {
        ContentValues data = new ContentValues();
        data.put("operator", trip.getOperator());
        data.put("start", trip.getStart());
        data.put("end", trip.getEnd());
        data.put("start_time", trip.getStart_time());
        data.put("end_time", trip.getEnd_time());
        data.put("tripid", trip.getTripid());
        return data;
    }

    public List<Trip> search(String key) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor c;

        if("all".equals(key)) {
            String sql = "SELECT * FROM Trip ORDER BY start_time DESC;";
            c = db.rawQuery(sql, null);
        }else {
            String[] args = {key};
            c = db.rawQuery("SELECT * FROM Trip WHERE tripid = ? ;", args);
        }

        List<Trip> trips = new ArrayList<Trip>();
        while (c.moveToNext()) {
            Trip trip = new Trip();
            trip.setId(c.getLong(c.getColumnIndex("id")));
            trip.setStart(c.getString(c.getColumnIndex("start")));
            trip.setEnd(c.getString(c.getColumnIndex("end")));
            trip.setStart_time(c.getString(c.getColumnIndex("start_time")));
            trip.setEnd_time(c.getString(c.getColumnIndex("end_time")));
            trip.setTripid(c.getString(c.getColumnIndex("tripid")));
            trip.setOperator(c.getString(c.getColumnIndex("operator")));

            trips.add(trip);
        }
        c.close();

        return trips;
    }

    public void delete(String tripid){
        SQLiteDatabase db = getWritableDatabase();

        String[] params = {tripid};
        db.delete("Trip", "tripid = ?", params);
    }

    public void update(Trip trip) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues data = getContentValuesTrip(trip);

        String[] params = {trip.getId().toString()};
        db.update("Trip",data,"id = ?",params);
    }

}

