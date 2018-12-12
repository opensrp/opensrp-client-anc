package org.smartregister.anc.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;

import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.views.CustomTextView;

import org.smartregister.anc.R;
import org.smartregister.anc.contract.AdvancedSearchContract;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListDetail;

    public ExpandableListAdapter(Context context, List<String> expandableListTitle,
                                 HashMap<String, List<String>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition)).get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedListText = (String) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.native_expandable_list_item, null);
        }

        populateChildView(expandedListText,convertView);

        return convertView;
    }

    private void populateChildView(String expandedListText, View convertView) {
        String label = "";
        String value = "";

        if (!TextUtils.isEmpty(expandedListText)) {
            String[] strings = expandedListText.split(":");
            label = strings[0];
            value = strings[1];
        }

        CustomTextView expandedListHeader = convertView.findViewById(R.id.item_header);
        if (!TextUtils.isEmpty(label)) {
            expandedListHeader.setText(label);
        }

        CustomTextView expandedListValue = convertView.findViewById(R.id.item_value);
        if (!TextUtils.isEmpty(value)) {
            expandedListValue.setText(value);
        }
    }


    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition)).size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.native_expandable_list_group, null);
        }
        CustomTextView listTitleTextView = convertView.findViewById(R.id.topBarTextView);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        showInfoIcon(convertView, parent);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    private void showInfoIcon(View convertView, ViewGroup parent) {
        String accordionInfoText = (String) parent.getTag(R.id.accordion_info_text);
        String accordionKey = (String) parent.getTag(com.vijay.jsonwizard.R.id.key);
        String accordionType = (String) parent.getTag(com.vijay.jsonwizard.R.id.type);
        String accordionInfoTitle = (String) parent.getTag(R.id.accordion_info_title);
        CommonListener commonListener = (CommonListener) parent.getTag(com.vijay.jsonwizard.R.id.specify_listener);

        if (accordionInfoText != null) {
            ImageView accordionInfoWidget = convertView.findViewById(R.id.accordion_info_icon);
            accordionInfoWidget.setVisibility(View.VISIBLE);
            accordionInfoWidget.setTag(com.vijay.jsonwizard.R.id.key, accordionKey);
            accordionInfoWidget.setTag(com.vijay.jsonwizard.R.id.type, accordionType);
            accordionInfoWidget.setTag(com.vijay.jsonwizard.R.id.label_dialog_info, accordionInfoText);
            accordionInfoWidget.setTag(com.vijay.jsonwizard.R.id.label_dialog_title, accordionInfoTitle);
            accordionInfoWidget.setOnClickListener(commonListener);
        }
    }

}
