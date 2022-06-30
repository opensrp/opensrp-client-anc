package org.smartregister.anc.library.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.anc.library.R;
import org.smartregister.anc.library.model.LibraryContent;
import org.smartregister.anc.library.viewholder.LibraryContentViewHolder;

import java.util.List;

public class LibraryContentAdapter extends RecyclerView.Adapter<LibraryContentViewHolder> {
    private final List<LibraryContent> libraryContentList;
    private final LayoutInflater inflater;
    private Activity activity;
    private final Context context;

    public LibraryContentAdapter(List<LibraryContent> libraryContentList, Context context) {
        this.libraryContentList = libraryContentList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public LibraryContentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.library_items_row, viewGroup, false);
        return new LibraryContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryContentViewHolder libraryContentViewHolder, int position) {
        if (libraryContentList != null && libraryContentList.size() > 0) {
            LibraryContent libraryContent = libraryContentList.get(position);
            if (libraryContent != null) {
                String contentHeader = libraryContent.getContentHeader();
                libraryContentViewHolder.contentHeader.setText(contentHeader);
            }
        }
    }

    @Override
    public int getItemCount() {
        return libraryContentList.size();
    }
}
