package com.ikit.rmpnoteapp.ui.notes;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ikit.rmpnoteapp.MainActivity;
import com.ikit.rmpnoteapp.R;
import com.ikit.rmpnoteapp.ui.Note;
import com.ikit.rmpnoteapp.ui.NoteAdapter;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class NotesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public NotesViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}