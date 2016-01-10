package com.example.android.navigationdrawerexample.model;

import android.provider.BaseColumns;

/**
 * Created by adven on 23.03.14.
 */
public class DBContract {

    /*To prevent someone from accidentally instantiating the contract class,
    give it an empty constructor.*/
    public DBContract() {
    }


    /* Inner class that defines the table contents */
    public static abstract class PhraseTable implements BaseColumns {
        public static final String TABLE_NAME = "phrases";
        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + PhraseTable.TABLE_NAME;
        //public static final String COLUMN_NAME_PHRASE = "phrase";
        //public static final String COLUMN_NAME_TRANSLATE = "translate";
        //public static final String COLUMN_NAME_STATUS = "status";
        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + PhraseTable.TABLE_NAME + " (" +
                        PhraseTable._ID + " INTEGER PRIMARY KEY," +
                        Columns.values()[0].getName() + TEXT_TYPE + COMMA_SEP +
                        Columns.values()[1].getName() + TEXT_TYPE + COMMA_SEP +
                        Columns.values()[2].getName() + TEXT_TYPE + COMMA_SEP +
                        " )";

        public enum Columns {
            PHRASE("phrase"),
            TRANSLATE("translate"),
            STATUS("status");
            String columnName;

            Columns(String columnName) {
                this.columnName = columnName;
            }

            public String getName() {
                return columnName;
            }

        }
    }
}
