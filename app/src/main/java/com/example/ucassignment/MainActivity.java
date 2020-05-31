package com.example.ucassignment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    FloatingActionButton fab;
    RecyclerView todoitemsRecyclerView;
    RecyclerView.Adapter todoitemsRecyclerViewADAPTER;
    List<toDoItem> todoItemsList = new ArrayList<>(0);
    TextView InitialMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.fab);
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


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e(TAG, "Fab clicked");

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Add a TODO Item");
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

                                db.todoDao().insertall(new toDoItem(ToDoInput.getText().toString(),false));

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
    }
}