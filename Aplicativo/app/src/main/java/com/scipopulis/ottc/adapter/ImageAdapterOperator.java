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

package com.scipopulis.ottc.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.scipopulis.ottc.MainActivity;
import com.scipopulis.ottc.api.object.Company;
import com.squareup.picasso.Picasso;
import java.io.File;


public class ImageAdapterOperator extends BaseAdapter {

    private final String[] opers;
    private Context mContext;

    public ImageAdapterOperator(Context cc) {
        this.mContext = cc;

        SharedPreferences prefs = cc.getSharedPreferences("key_op", cc.MODE_PRIVATE);
        String ss = prefs.getString("operator", null);
        opers = ss.split(", ");
    }

    @Override
    public int getCount() {
        return opers.length;
    }

    @Override
    public Object getItem(int i) {
        return opers[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        int totalHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, mContext.getResources().getDisplayMetrics());

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(totalHeight, totalHeight));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        String root = mContext.getFilesDir().toString();
        File f = new File(root + "/operator/"+opers[position]+".png");
        Picasso.with(mContext).load(f).fit().centerInside().into(imageView);

        final int kk = position;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Company cc = new Company();
                cc.setCompany(opers[kk]);
                MainActivity.bus.post(cc);
            }
        });


        return imageView;
    }

}
