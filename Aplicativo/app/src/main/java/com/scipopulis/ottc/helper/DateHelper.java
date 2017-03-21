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
package com.scipopulis.ottc.helper;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {
    private static final String  TAG  = DateHelper.class.getSimpleName ();

    public final static String DEFAULT = "MM/dd/yyyy hh:mm:ss a Z";
    public final static String ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ";
    public final static String ISO8601_NOMS = "yyyy-MM-dd'T'HH:mm:ssZ";
    public final static String RFC822 = "EEE, dd MMM yyyy HH:mm:ss Z";
    public final static String SIMPLE = "MM/dd/yyyy hh:mm:ss a";

    public static String format ( String format, Date date ) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format ( date );
    }

    public static String format ( Date date ) {
        return format ( DEFAULT, date );
    }

    public static String formatISO8601 ( Date date ) {
        return format ( ISO8601, date );
    }

    public static String formatISO8601NoMilliseconds ( Date date ) {
        return format ( ISO8601_NOMS, date );
    }

    public static String formatRFC822 ( Date date ) {
        return format ( RFC822, date );
    }

    public static Date parse ( String date ) throws ParseException {
        return parse ( DEFAULT,date );
    }

    public static Date parse ( String format, String date ) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse ( date );
    }

    public static Date parseISO8601 ( String date ) throws ParseException {
        return parse ( ISO8601,date );
    }

    public static Date parseISO8601NoMilliseconds ( String date ) throws ParseException {
        return parse ( ISO8601_NOMS,date );
    }

    public static Date parseRFC822 ( String date ) throws ParseException {
        return parse ( RFC822,date );
    }


    public static Date Now () {
        return new Date();
    }
}
