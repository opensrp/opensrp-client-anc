package org.smartregister.anc.library.viewholder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vijay.jsonwizard.views.CustomTextView;

import org.smartregister.anc.library.R;
import org.smartregister.anc.library.fragment.ProfileTasksFragment;
import org.smartregister.anc.library.listener.ContactTaskDisplayClickListener;

public class ContactTasksViewHolder extends RecyclerView.ViewHolder {
    public View parent;
    public RelativeLayout expansionPanelHeader;
    public ImageView accordionInfoIcon;
    public RelativeLayout expansionHeaderLayout;
    public ImageView statusImageView;
    public CustomTextView topBarTextView;
    public LinearLayout contentLayout;
    public LinearLayout accordionBottomNavigation;
    public LinearLayout contentView;
    public Button undoButton;
    public Button okButton;
    private ContactTaskDisplayClickListener contactTaskDisplayClickListener;

    public ContactTasksViewHolder(@NonNull View itemView, ProfileTasksFragment profileTasksFragment) {
        super(itemView);
        expansionHeaderLayout = itemView.findViewById(R.id.expansionHeader);
        accordionInfoIcon = itemView.findViewById(R.id.accordion_info_icon);
        expansionPanelHeader = itemView.findViewById(R.id.expansion_header_layout);
        statusImageView = itemView.findViewById(R.id.statusImageView);
        topBarTextView = itemView.findViewById(R.id.topBarTextView);
        contentLayout = itemView.findViewById(R.id.contentLayout);
        contentView = itemView.findViewById(R.id.contentView);
        accordionBottomNavigation = itemView.findViewById(R.id.accordion_bottom_navigation);
        undoButton = itemView.findViewById(R.id.undo_button);
        okButton = itemView.findViewById(R.id.ok_button);
        parent = itemView;

        contactTaskDisplayClickListener = new ContactTaskDisplayClickListener(profileTasksFragment);
        attachListeners();
    }

    /**
     * Attaches the click listener to the views on the view holder
     */
    private void attachListeners() {
        expansionHeaderLayout.setOnClickListener(contactTaskDisplayClickListener);
        accordionInfoIcon.setOnClickListener(contactTaskDisplayClickListener);
        expansionPanelHeader.setOnClickListener(contactTaskDisplayClickListener);
        statusImageView.setOnClickListener(contactTaskDisplayClickListener);
        topBarTextView.setOnClickListener(contactTaskDisplayClickListener);
        undoButton.setOnClickListener(contactTaskDisplayClickListener);
        okButton.setOnClickListener(contactTaskDisplayClickListener);
    }
}
