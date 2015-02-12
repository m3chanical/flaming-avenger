package com.tinkerduck.firstapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class MainActivity extends FragmentActivity {
    public static final String EXTRA_MESSAGE = "com.tinkerduck.firstapp.MESSAGE";
    public static final String EXTRA_FD = "com.tinkerduck.firstapp.FD";
    public static final String EXTRA_NOTE = "com.tinkerduck.firstapp.NOTE";
    public static final String EXTRA_SELECTION = "com.tinkerduck.firstapp.SELECTION";
    public static final String editMode = "com.tinkerduck.firstapp.EDIT";
    public static final String filename = "myFile";
    private SimpleDateFormat mFormatter = new SimpleDateFormat("MMMM dd yyyy hh:mm aa");

    private MySqliteHelper db;
    private ListView notesListView;
    private List<Notes> notesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new MySqliteHelper(this);

        notesListView = (ListView)findViewById(R.id.notesListView);
        notesList = db.getAllNotes();
        registerForContextMenu(notesListView);
        populateList();

        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                intent.putExtra(EXTRA_NOTE, notesList.get(position));
                intent.putExtra(editMode, true);
                startActivityForResult(intent, 1);
            }
        });
     }

    private void populateList() {
        List<String> values = new ArrayList<String>();

        for (Notes note : notesList) {
            values.add(note.getTitle());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);

        notesListView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED){
            return;
        }
        if(resultCode == RESULT_OK) {
            populateList();
            finish();
            startActivity(getIntent());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case(R.id.action_search):
                SlideDateTimeListener listener = new SlideDateTimeListener() {
                    @Override
                    public void onDateTimeSet(Date date) {
                        Toast.makeText(MainActivity.this,
                                mFormatter.format(date), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onDateTimeCancel(){
                        Toast.makeText(MainActivity.this,
                                "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                };
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();

                return true;
            case(R.id.action_settings):
                //openSettings();
                return true;
            case(R.id.action_add_note):
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final List<String> pizzaList = Arrays.asList(getResources().getStringArray(R.array.pizza_array));

                builder.setTitle("Choose Your Favorite Pizza")
                        .setItems(R.array.pizza_array, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                Log.d("The Pizza You Chose:", pizzaList.get(which));
                                Intent editNoteIntent = new Intent(getApplicationContext(), EditNoteActivity.class);
                                editNoteIntent.putExtra(editMode, false);
                                editNoteIntent.putExtra(EXTRA_SELECTION, which);
                                startActivityForResult(editNoteIntent, 1);

                            }
                        });
                builder.create().show();
                //Intent editNoteIntent = new Intent(this, EditNoteActivity.class);
                //editNoteIntent.putExtra(editMode, false);
                //startActivityForResult(editNoteIntent, 1);
                return true;
            case(R.id.action_test_data):
                db.addNote(new Notes("Sandwich", "Sandwiches are Tasty", "gibberish"));
                db.addNote(new Notes("Pizza", "Yum yum", "gibberish"));
                db.addNote(new Notes("Shit I care about:", "", "gibberish"));
                finish();
                startActivity(getIntent());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notes_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()){
            case R.id.note_delete:
                db.delete(notesList.get(info.position));
                finish();
                startActivity(getIntent());

                Context context = getApplicationContext();
                CharSequence text = "Note Deleted!";
                int duration = Toast.LENGTH_SHORT;
                Toast.makeText(context, text, duration).show();

                return true;

            case R.id.note_edit:
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                intent.putExtra(EXTRA_NOTE, notesList.get(info.position));
                startActivityForResult(intent, 1);
                return true;

            default:
                return super.onContextItemSelected(item);

        }
    }

    public void sendMessage(View view){
        EditText editText = (EditText) findViewById(R.id.editMessage);
        String message = editText.getText().toString();
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        try {
            FileOutputStream outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(message.getBytes());
            intent.putExtra(EXTRA_FD, String.valueOf(outputStream.getFD()));
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }
}
