package com.example.flytrapiot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Clase auxiliar que extiende SQLiteOpenHelper.
 * Crea la base de datos 'flytrap_db' y la tabla 'trampas', y expone el CRUD completo.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "flytrap_db";
    public static final int DB_VERSION = 1;

    public static final String TABLA = "trampas";
    public static final String COL_ID = "id";
    public static final String COL_SECTOR = "codigo_sector";
    public static final String COL_TIPO = "tipo_trampa";
    public static final String COL_NIVEL = "nivel_captura";
    public static final String COL_OBS = "observaciones";

    // SQL provisto en la evaluación
    private static final String CREATE_TABLE =
            "CREATE TABLE trampas (" +
            " id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " codigo_sector TEXT NOT NULL," +
            " tipo_trampa TEXT NOT NULL," +
            " nivel_captura INTEGER NOT NULL," +
            " observaciones TEXT )";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLA);
        onCreate(db);
    }

    // ---------- CREATE ----------
    public long insertarTrampa(String sector, String tipo, int nivel, String obs) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SECTOR, sector);
        values.put(COL_TIPO, tipo);
        values.put(COL_NIVEL, nivel);
        values.put(COL_OBS, obs);
        long id = db.insert(TABLA, null, values);
        db.close();
        return id;
    }

    // ---------- READ (todas) ----------
    public ArrayList<Trampa> obtenerTrampas() {
        ArrayList<Trampa> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLA + " ORDER BY " + COL_ID + " DESC", null);
        if (c.moveToFirst()) {
            do {
                lista.add(new Trampa(
                        c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                        c.getString(c.getColumnIndexOrThrow(COL_SECTOR)),
                        c.getString(c.getColumnIndexOrThrow(COL_TIPO)),
                        c.getInt(c.getColumnIndexOrThrow(COL_NIVEL)),
                        c.getString(c.getColumnIndexOrThrow(COL_OBS))
                ));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return lista;
    }

    // ---------- READ (una por ID) ----------
    public Trampa obtenerTrampaPorId(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLA + " WHERE " + COL_ID + " = ?",
                new String[]{String.valueOf(id)});
        Trampa t = null;
        if (c.moveToFirst()) {
            t = new Trampa(
                    c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                    c.getString(c.getColumnIndexOrThrow(COL_SECTOR)),
                    c.getString(c.getColumnIndexOrThrow(COL_TIPO)),
                    c.getInt(c.getColumnIndexOrThrow(COL_NIVEL)),
                    c.getString(c.getColumnIndexOrThrow(COL_OBS))
            );
        }
        c.close();
        db.close();
        return t;
    }

    // ---------- UPDATE ----------
    public int actualizarTrampa(int id, String sector, String tipo, int nivel, String obs) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SECTOR, sector);
        values.put(COL_TIPO, tipo);
        values.put(COL_NIVEL, nivel);
        values.put(COL_OBS, obs);
        int filas = db.update(TABLA, values, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return filas;
    }

    // ---------- DELETE ----------
    public int eliminarTrampa(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int filas = db.delete(TABLA, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return filas;
    }
}
