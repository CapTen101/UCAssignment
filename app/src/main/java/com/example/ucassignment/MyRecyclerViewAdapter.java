package com.example.ucassignment;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.List;

class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<toDoItem> todoItemsList;
    Context c;

    MyRecyclerViewAdapter(List<toDoItem> todoItemsList, Context c) {
        this.todoItemsList = todoItemsList;
        this.c = c;
    }

    @NonNull
    @Override
    public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return (new ViewHolder(view));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyRecyclerViewAdapter.ViewHolder holder, final int position) {
        if (todoItemsList.get(0) == null) holder.todoItem.setText("No tasks pending, well done!");

        final Context finalContext = c;

        AppDatabase db = Room.databaseBuilder(finalContext, AppDatabase.class, "todoitemsdb").build();

        holder.todoItem.setText(todoItemsList.get(position).getToDoItem());
        holder.todoCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.todoItem.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.secondaryButtonsLayout.setVisibility(View.VISIBLE);
                holder.todoCheck.setVisibility(View.GONE);
            }
        });
        holder.undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.secondaryButtonsLayout.setVisibility(View.GONE);
                holder.todoCheck.setVisibility(View.VISIBLE);
                holder.todoCheck.setChecked(false);
                holder.todoItem.setPaintFlags(0);
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                todoItemsList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, todoItemsList.size());

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        AppDatabase db = Room.databaseBuilder(finalContext, AppDatabase.class, "todoitemsdb").build();
                        db.todoDao().deleteitem(holder.todoItem.getText().toString());
                    }
                };
                thread.start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return todoItemsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView recyclerViewCard;
        TextView todoItem;
        CheckBox todoCheck;
        RelativeLayout secondaryButtonsLayout;
        Button deleteButton, undoButton;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerViewCard = itemView.findViewById(R.id.recycler_view_card);
            todoItem = itemView.findViewById(R.id.todo_item_text);
            todoCheck = itemView.findViewById(R.id.todo_item_checkbox);
            secondaryButtonsLayout = itemView.findViewById(R.id.secondary_button_layout);
            deleteButton = itemView.findViewById(R.id.delete_button);
            undoButton = itemView.findViewById(R.id.undo_button);
        }
    }
}
