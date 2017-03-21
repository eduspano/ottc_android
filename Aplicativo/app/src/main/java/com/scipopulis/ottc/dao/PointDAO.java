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
import android.util.Log;

import com.scipopulis.ottc.model.Point;

import java.util.ArrayList;
import java.util.List;

public class PointDAO extends SQLiteOpenHelper {
    private Context mContext;

    public PointDAO(Context cc) {
        super(cc, "Line", null, 1);
        this.mContext = cc;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String  sql = "CREATE TABLE Point (user_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, lat TEXT NOT NULL, lng TEXT NOT NULL, time TEXT NOT NULL, trip_id TEXT NOT NULL, flag TEXT NOT NULL, status TEXT NOT NULL, operator TEXT NOT NULL );";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if(i==2) {
            String sql = "DROP TABLE IF EXISTS Point";
            sqLiteDatabase.execSQL(sql);
            onCreate(sqLiteDatabase);
        }
    }

    public void insert(Point point) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues data = getContentValuesTrip(point);

        db.insert("Point", null, data);

    }

    @NonNull
    private ContentValues getContentValuesTrip(Point point) {
        ContentValues data = new ContentValues();
        data.put("lat", point.getLat());
        data.put("lng", point.getLng());
        data.put("time", point.getTime());
        data.put("trip_id", point.getTrip_id());
        data.put("flag", point.getFlag());
        data.put("status", point.getStatus());
        data.put("operator", point.getOperator());

        return data;
    }

    public List<Point> search(String key) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor c;

        if("all".equals(key)) {
            String sql = "SELECT * FROM Point ORDER BY time ASC;";
            c = db.rawQuery(sql, null);
        }else {
            String[] args = {key};
            c = db.rawQuery("SELECT * FROM Point WHERE trip_id = ? ORDER BY time ASC;", args);
        }

        List<Point> point = new ArrayList<Point>();
        while (c.moveToNext()) {
            Point trip = new Point();
            trip.setUser_id(c.getLong(c.getColumnIndex("user_id")));
            trip.setLat(c.getString(c.getColumnIndex("lat")));
            trip.setLng(c.getString(c.getColumnIndex("lng")));
            trip.setTime(c.getString(c.getColumnIndex("time")));
            trip.setTrip_id(c.getString(c.getColumnIndex("trip_id")));
            trip.setFlag(c.getString(c.getColumnIndex("flag")));
            trip.setStatus(c.getString(c.getColumnIndex("status")));
            trip.setOperator(c.getString(c.getColumnIndex("operator")));

            point.add(trip);
        }
        c.close();

        return point;
    }

    public List<Point> searchError(String chave) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor c;

        String[] args = {chave};
        c = db.rawQuery("SELECT * FROM Point WHERE flag = ? ORDER BY time ASC;", args);

        List<Point> line = new ArrayList<Point>();
        while (c.moveToNext()) {
            Point point = new Point();
            point.setUser_id(c.getLong(c.getColumnIndex("user_id")));
            point.setLat(c.getString(c.getColumnIndex("lat")));
            point.setLng(c.getString(c.getColumnIndex("lng")));
            point.setTime(c.getString(c.getColumnIndex("time")));
            point.setTrip_id(c.getString(c.getColumnIndex("trip_id")));
            point.setFlag(c.getString(c.getColumnIndex("flag")));
            point.setStatus(c.getString(c.getColumnIndex("status")));
            point.setOperator(c.getString(c.getColumnIndex("operator")));

            line.add(point);
        }
        c.close();

        return line;
    }


    public void delete(String trip_id){
        SQLiteDatabase db = getWritableDatabase();

        String[] params = {trip_id};
        db.delete("Point", "trip_id = ?", params);
    }

    public void update(Point point) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues data = getContentValuesTrip(point);

        String[] params = {point.getUser_id().toString()};
        db.update("Point",data,"id = ?",params);
    }

}
