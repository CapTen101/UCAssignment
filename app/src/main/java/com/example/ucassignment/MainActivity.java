package com.example.ucassignment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    FloatingActionButton plusFab, searchFab;
    RecyclerView todoitemsRecyclerView;
    RecyclerView.Adapter todoitemsRecyclerViewADAPTER;
    List<toDoItem> todoItemsList = new ArrayList<>(0);
    TextView InitialMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        plusFab = findViewById(R.id.plus_fab);
        searchFab = findViewById(R.id.search_fab);
        InitialMessage = findViewById(R.id.tap_to_add_text);
        todoitemsRecyclerView = findViewById(R.id.recycler_view);
        todoitemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Thread thread = new Thread() {
            @Override
            public void run() {
                AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "todoitemsdb").build();

                todoItemsList = db.todoDao().getalltodoitems();

                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {

                        if (todoItemsList.size() != 0) {
                            InitialMessage.setVisibility(View.GONE);
                            todoitemsRecyclerView.setVisibility(View.VISIBLE);
                            todoitemsRecyclerViewADAPTER = new MyRecyclerViewAdapter(todoItemsList, getApplicationContext());
                            todoitemsRecyclerView.setAdapter(todoitemsRecyclerViewADAPTER);
                        } else InitialMessage.setText("Tap + to add a ToDo item...");
                    }
                });
            }
        };
        thread.start();


        plusFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e(TAG, "Fab clicked");

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Add a ToDo Item");
                View viewInflated = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_box_layout, (ViewGroup) findViewById(android.R.id.content), false);

                final EditText ToDoInput = viewInflated.findViewById(R.id.input);
                builder.setView(viewInflated);

                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "todoitemsdb").build();

                                db.todoDao().insertall(new toDoItem(ToDoInput.getText().toString(), false));

                                todoItemsList = db.todoDao().getalltodoitems();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        InitialMessage.setVisibility(View.GONE);
                                        todoitemsRecyclerView.setVisibility(View.VISIBLE);
                                        todoitemsRecyclerViewADAPTER = new MyRecyclerViewAdapter(todoItemsList, getApplicationContext());
                                        todoitemsRecyclerView.setAdapter(todoitemsRecyclerViewADAPTER);
                                    }
                                });
                            }
                        };
                        thread.start();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });


        searchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Search your ToDO");
                View viewInflated = LayoutInflater.from(MainActivity.this).inflate(R.layout.search_item_layout, (ViewGroup) findViewById(android.R.id.content), false);

                final AutoCompleteTextView ToDoSearch = viewInflated.findViewById(R.id.search);
                builder.setView(viewInflated);

                final AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "todoitemsdb").build();

                Thread thread = new Thread() {
                    @Override
                    public void run() {

                        List<toDoItem> tempList = db.todoDao().getalltodoitems();

                        final List<String> searchText = new ArrayList<>();
                        for (toDoItem item : tempList) {
                            searchText.add(item.getToDoItem());
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayAdapter adapterSearch = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, searchText);
                                ToDoSearch.setAdapter(adapterSearch);
                            }
                        });
                    }
                };

                final int[] KEY = new int[1];

                ToDoSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final String todoitemtext = ToDoSearch.getText().toString();

                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                KEY[0] = db.todoDao().getitemid(todoitemtext);
                                Log.e(TAG, String.valueOf(KEY[0]));

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });
                            }
                        };
                        thread.start();

                    }
                });

                builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        todoitemsRecyclerView.smoothScrollToPosition(KEY[0]);
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                thread.start();
                builder.show();
            }
        });
    }
}