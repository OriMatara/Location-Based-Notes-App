package com.example.locationbasednotesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    private List<Note> dataSet;
    private Context context;

    public NoteAdapter(Context context,List<Note> dataSet, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.dataSet = dataSet;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
    }
    private OnNoteClickListener onNoteClickListener;

    public void setOnNoteClickListener(OnNoteClickListener listener) {
        this.onNoteClickListener = listener;
    }


    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView bodyTextView;
        TextView dateTextView;
        CardView cardView;

        public NoteViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            cardView=(CardView) itemView.findViewById(R.id.card_view);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            bodyTextView = (TextView) itemView.findViewById(R.id.bodyTextView);
            dateTextView = (TextView) itemView.findViewById(R.id.dateTextView);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null){
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClock(pos);
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.cards_layout, parent, false);

        NoteViewHolder myViewHolder=new NoteViewHolder(view, recyclerViewInterface);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder viewHolder, final int listposition) {
        TextView titleTextView=viewHolder.titleTextView;
        TextView bodyTextView=viewHolder.bodyTextView;
        TextView dateTextView=viewHolder.dateTextView;
        CardView cardView=viewHolder.cardView;

        titleTextView.setText(dataSet.get(listposition).getTitle());
        bodyTextView.setText(dataSet.get(listposition).getBody());
        dateTextView.setText(dataSet.get(listposition).getDate());

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}

