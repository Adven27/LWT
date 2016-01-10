package com.example.android.navigationdrawerexample.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.example.android.navigationdrawerexample.model.Phrase;
import com.example.android.navigationdrawerexample.model.WordStatus;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.android.navigationdrawerexample.model.DBContract.PhraseTable.Columns;
import static com.example.android.navigationdrawerexample.model.DBContract.PhraseTable.TABLE_NAME;

/**
 * Created by adven on 23.03.14.
 */
public class DBAssetHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "lwt.db";
    private static final int DATABASE_VERSION = 13;
    private static final String TAG = "DBAssetHelper";
    private static Map<String, Phrase> cashedPhrases = new HashMap<String, Phrase>();

    public DBAssetHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

/*
        you can use an alternate constructor to specify a database location
        (such as a folder on the sd card)
        you must ensure that this folder is available and you have permission
        to write to it
        super(context, DATABASE_NAME, context.getExternalFilesDir(null).getAbsolutePath(), null, DATABASE_VERSION);
*/

        // we have supplied no upgrade path from version 1 to 2
        setForcedUpgrade(2);
    }

    public Phrase getPhrase(String phrase) {
        return getPhrase(phrase, true);
    }

    public Phrase getPhrase(String phrase, boolean returnNewIfEmpty) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {Columns.TRANSLATE.getName(), Columns.STATUS.getName()};
        String[] selectionArgs = {phrase};

        qb.setTables(TABLE_NAME);
        Cursor c = qb.query(db, sqlSelect, Columns.PHRASE.getName() + " = ?", selectionArgs, null, null, null);

        if (c.moveToFirst()) {
            //TODO: get rid of Magic Numbers
            return new Phrase(phrase, c.getString(0), WordStatus.getByName(c.getString(1)));
        }

        return returnNewIfEmpty ? new Phrase(phrase, "", WordStatus.UNKNOWN) : null;
    }

    public List<Phrase> getPhrasesLike(String phrase, boolean returnNewIfEmpty) {
        List<Phrase> resultLst = new ArrayList<Phrase>();

        for (String key : cashedPhrases.keySet()) {
            if (key.contains(phrase)) {
                resultLst.add(cashedPhrases.get(key));
            }
        }

        Log.i("ppp", phrase + " " + String.valueOf(cashedPhrases.size()) + " " + String.valueOf(resultLst.size()));

        if (resultLst.isEmpty()) {
            SQLiteDatabase db = getReadableDatabase();
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

            String[] sqlSelect = {Columns.PHRASE.getName(), Columns.TRANSLATE.getName(), Columns.STATUS.getName()};
            String[] selectionArgs = {phrase + "%"};

            qb.setTables(TABLE_NAME);
            Cursor c = qb.query(db, sqlSelect, Columns.PHRASE.getName() + " LIKE ?", selectionArgs, null, null, null);


            if (c.moveToFirst()) {
                do {
                    Phrase newPhrase = new Phrase(c.getString(0), c.getString(1), WordStatus.getByName(c.getString(2)));
                    resultLst.add(newPhrase);
                    cashedPhrases.put(newPhrase.getPhrase(), newPhrase);
                } while (c.moveToNext());


            } else if (returnNewIfEmpty) { // nothing was found in DB, return "unknown" phrase
                Phrase newPhrase = new Phrase(phrase, "", WordStatus.UNKNOWN);
                resultLst.add(newPhrase);
                cashedPhrases.put(newPhrase.getPhrase(), newPhrase);
            }
        }

        return resultLst;
    }

    public WordStatus getPhraseStatus(String phrase) {
        WordStatus result = WordStatus.UNKNOWN;
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {Columns.STATUS.getName()};
        String[] selectionArgs = {phrase};

        qb.setTables(TABLE_NAME);
        Cursor c = qb.query(db, sqlSelect, Columns.PHRASE.getName() + " = ?", selectionArgs, null, null, null);

        if (c.moveToFirst()) {
            result = WordStatus.getByName(c.getString(0));
        }

        Log.i(TAG, "DB OPERATION. getPhraseStatus = " + result.getCode());
        return result;
    }

    public long insertPhrase(Phrase phrase) {
        Log.i("DB", "insertPhrase. Try to insert PHRASE '" + phrase.getPhrase() + "'");
        SQLiteDatabase db = getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Columns.PHRASE.getName(), phrase.getPhrase());
        values.put(Columns.TRANSLATE.getName(), phrase.getTranslate());
        values.put(Columns.STATUS.getName(), phrase.getStatus().getCode());

        long rowId = db.insert(TABLE_NAME, null, values);
        cashedPhrases.put(phrase.getPhrase(), phrase);
        Log.i("DB", "insertPhrase. PHRASE '" + phrase.getPhrase() + "' INSERTED. ID = " + rowId);
        return rowId;
    }

    public long updatePhrase(Phrase phrase) {
        SQLiteDatabase db = getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Columns.PHRASE.getName(), phrase.getPhrase());
        values.put(Columns.TRANSLATE.getName(), phrase.getTranslate());
        values.put(Columns.STATUS.getName(), phrase.getStatus().getCode());

        String[] selectionArgs = {phrase.getPhrase()};
        cashedPhrases.put(phrase.getPhrase(), phrase);
        return db.update(TABLE_NAME, values, Columns.PHRASE.getName() + " = ?", selectionArgs);
    }

    public int getUpgradeVersion() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"MAX (version)"};
        String sqlTables = "upgrades";

        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, null, null,
                null, null, null);

        int v = 0;
        c.moveToFirst();
        if (!c.isAfterLast()) {
            v = c.getInt(0);
        }
        c.close();
        return v;
    }

    public int clearBase() {
        SQLiteDatabase db = getWritableDatabase();
        cashedPhrases.clear();
        return db.delete(TABLE_NAME, null, null);
    }

    public void deletePhrase(Phrase phrase) {
        SQLiteDatabase db = getWritableDatabase();
        String[] selectionArgs = {phrase.getPhrase()};
        db.delete(TABLE_NAME, Columns.PHRASE.getName() + " = ?", selectionArgs);
        cashedPhrases.remove(phrase.getPhrase());
    }
}
