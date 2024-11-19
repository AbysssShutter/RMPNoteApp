package com.ikit.rmpnoteapp.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ikit.rmpnoteapp.MainActivity;
import com.ikit.rmpnoteapp.R;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.Frame;
import com.yandex.runtime.image.ImageProvider;

import java.text.DateFormat;
import java.util.function.DoubleConsumer;

import io.realm.Realm;
import io.realm.RealmResults;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    Context context;
    RealmResults<Note> noteList;

    public NoteAdapter(Context context, RealmResults<Note> noteList) {
        this.context = context;
        this.noteList = noteList;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MapKitFactory.initialize(context);
        return new NoteViewHolder(LayoutInflater.from(context).inflate(R.layout.note_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.TitleView.setText(note.getTitle());
        holder.sdView.setText(note.getShortDescription());
        holder.fdView.setText(note.getFullDescription());
        holder.noteType.setText(note.getNoteType());
        String formattedTime = DateFormat.getDateInstance().format(note.timeWhenCreated);
        holder.timeCreatedView.setText(formattedTime);

        holder.revealBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                note.deleteFromRealm();
                realm.commitTransaction();
                return true;
            }
        });

        holder.revealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.sdView.getVisibility() == View.GONE) {
                    holder.sdView.setVisibility(View.VISIBLE);
                    holder.fdView.setVisibility(View.VISIBLE);
                } else {
                    holder.sdView.setVisibility(View.GONE);
                    holder.fdView.setVisibility(View.GONE);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(context, ChangeNote.class);
                intent.putExtra("id", note.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            }
        });

        holder.TitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (note.getLatitude() != 0) {
                    Intent intent = new Intent(context, MapWithPoint.class);
                    intent.putExtra("latitude", note.getLatitude());
                    intent.putExtra("longitude", note.getLongitude());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView TitleView;
        TextView sdView;
        TextView fdView;
        TextView noteType;
        TextView timeCreatedView;
        Button revealBtn;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            TitleView = itemView.findViewById(R.id.TitleView);
            sdView = itemView.findViewById(R.id.sdView);
            fdView = itemView.findViewById(R.id.fdView);
            noteType = itemView .findViewById(R.id.TypeView);
            timeCreatedView = itemView.findViewById(R.id.timeCreatedView);
            revealBtn = itemView.findViewById(R.id.revealButton);
        }
    }
}
