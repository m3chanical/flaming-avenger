package com.tinkerduck.firstapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by m3chanical on 1/20/15.
 */
public class MySqliteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "NotesDB";

    private static final String TABLE_NOTES = "notes";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_BODY = "noteBody";
    private static final String KEY_TYPE = "noteType";

    private static final String[] COLUMNS = {KEY_ID, KEY_TITLE, KEY_BODY, KEY_TYPE};


    public MySqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //SQL Statement to create Notes table:
        String CREATE_NOTE_TABLE = "CREATE TABLE notes ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "notebody TEXT, " +
                "notetype TEXT )";
        db.execSQL(CREATE_NOTE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS notes");

        this.onCreate(db);
    }

    public void addNote(Notes note){
        Log.d("addNote", note.toString());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, note.getTitle());
        values.put(KEY_BODY, note.getNoteBody());
        values.put(KEY_TYPE, note.getNoteType());

        db.insert(TABLE_NOTES, null, values);

        db.close();
    }

    public Notes getNote(int id){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_NOTES,
                COLUMNS,
                " id = ?",
                new String[] { String.valueOf(id) },
                null,
                null,
                null,
                null);
        if (cursor != null){
            cursor.moveToFirst();
        }

        Notes note = new Notes();
        note.setId(Integer.parseInt(cursor.getString(0)));
        note.setTitle(cursor.getString(1));
        note.setBody(cursor.getString(2));
        note.setType(cursor.getString(3));

        Log.d("getNote("+id+")", note.toString());

        return note;
    }

    public List<Notes> getAllNotes(){
        List<Notes> notes = new LinkedList<Notes>();

        String query = "SELECT * FROM " + TABLE_NOTES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Notes note = null;
        if (cursor.moveToFirst()){
            do {
                note = new Notes();
                note.setId(Integer.parseInt(cursor.getString(0)));
                note.setTitle(cursor.getString(1));
                note.setBody(cursor.getString(2));
                note.setType(cursor.getString(3));
                notes.add(note);
            } while(cursor.moveToNext());
        }
        Log.d("getAllNotes()", notes.toString());

        return notes;
    }

    public int update(Notes note){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, note.getTitle());
        values.put(KEY_BODY, note.getNoteBody());
        values.put(KEY_TYPE, note.getNoteType());

        int i = db.update(TABLE_NOTES,
                            values,
                            KEY_ID+" = ?",
                            new String[] {String.valueOf(note.getId()) });
        db.close();
        return i;
    }

    public void delete(Notes note){

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NOTES, //table name
                KEY_ID+" = ?",  // selections
                new String[] { String.valueOf(note.getId()) }); //selections args

        db.close();

        Log.d("deleteBook", note.toString());
    }

}
