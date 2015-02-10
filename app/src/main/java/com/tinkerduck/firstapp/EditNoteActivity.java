package com.tinkerduck.firstapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by m3chanical on 1/24/15.
 */
public class EditNoteActivity extends Activity {

    MySqliteHelper db = new MySqliteHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        final List<String> pizzaList = Arrays.asList(getResources().getStringArray(R.array.pizza_array));

        final EditText titleEditText = (EditText)findViewById(R.id.titleEditText);
        final EditText typeEditText = (EditText)findViewById(R.id.typeEditText);
        final EditText bodyEditText = (EditText)findViewById(R.id.bodyEditText);


        Serializable extra = getIntent().getSerializableExtra(MainActivity.EXTRA_NOTE);
        if (extra != null) {
            Notes note = (Notes)extra;
            titleEditText.setText(note.getTitle());
            typeEditText.setText(note.getNoteType());
            bodyEditText.setText(note.getNoteBody());
        }
        int selection = getIntent().getIntExtra(MainActivity.EXTRA_SELECTION, -1);
        if(selection != -1) {
            titleEditText.setText(String.valueOf(selection));
            bodyEditText.setText(pizzaList.get(selection));
        }


    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        boolean editMode = getIntent().getExtras().getBoolean(MainActivity.editMode);

        switch(item.getItemId()){
            case R.id.action_discard:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            case R.id.action_save_note:
                if(!editMode) { // If note in edit mode (this is a new note), then add the note to the db:
                    Notes note = new Notes(((EditText) findViewById(R.id.titleEditText)).getText().toString(),
                            ((EditText) findViewById(R.id.bodyEditText)).getText().toString(),
                            ((EditText) findViewById(R.id.typeEditText)).getText().toString());
                    db.addNote(note);
                    setResult(RESULT_OK);
                    finish();
                } else { // If IN edit mode, update the note:
                    Notes note = (Notes)getIntent().getSerializableExtra(MainActivity.EXTRA_NOTE);
                    note.setTitle(((EditText) findViewById(R.id.titleEditText)).getText().toString());
                    note.setBody(((EditText) findViewById(R.id.bodyEditText)).getText().toString());
                    note.setType(((EditText) findViewById(R.id.typeEditText)).getText().toString());
                    db.update(note);
                    setResult(RESULT_OK);
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
