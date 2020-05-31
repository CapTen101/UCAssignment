package com.example.ucassignment;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface toDoDAO {

    @Query("SELECT * FROM todoitem")
    List<toDoItem> getalltodoitems();

    @Insert
    void insertall(toDoItem... toDoItems);

//    @Query("SELECT completed FROM toDoItem WHERE id=:id;")
//    boolean isCompleted(int id);

    @Query("DELETE FROM toDoItem WHERE todo_item=:item_to_delete;")
    void deleteitem(String item_to_delete);

    @Query("SELECT id FROM toDoItem WHERE todo_item=:item_to_search;")
    int getitemid(String item_to_search);
}
