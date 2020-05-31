package com.example.ucassignment;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class toDoItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "todo_item")
    private String toDoItem;

    @ColumnInfo(name = "completed")
    private boolean completed;

    public toDoItem(String toDoItem, boolean completed) {
        this.toDoItem = toDoItem;
        this.completed = completed;
    }

    public String getToDoItem() {
        return toDoItem;
    }

    public void setToDoItem(String toDoItem) {
        this.toDoItem = toDoItem;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
