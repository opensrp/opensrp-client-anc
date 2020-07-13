package org.smartregister.anc.library.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.anc.library.R;
import org.smartregister.domain.ServerSetting;

import java.util.List;

/**
 * Created by ndegwamartin on 14/08/2018.
 */
public class CharacteristicsAdapter extends RecyclerView.Adapter<CharacteristicsAdapter.ViewHolder> {

    private List<ServerSetting> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public CharacteristicsAdapter(Context context, List<ServerSetting> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_characteristics_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ServerSetting characteristic = mData.get(position);String label = characteristic.getLabel() != null ? characteristic.getLabel() : "";
        holder.labelTextView.setText(label);
        holder.valueTextView.setText(characteristic.getValue() ? "Yes" : "No");
        holder.info.setTag(characteristic.getKey());
        holder.info.setTag(R.id.CHARACTERISTIC_DESC, characteristic.getDescription());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView labelTextView;
        private TextView valueTextView;
        private View info;

        ViewHolder(View itemView) {
            super(itemView);
            labelTextView = itemView.findViewById(R.id.label);
            valueTextView = itemView.findViewById(R.id.value);
            info = itemView.findViewById(R.id.info);
            info.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}
