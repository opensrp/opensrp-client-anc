package org.smartregister.anc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.smartregister.anc.R;
import org.smartregister.anc.domain.Characteristic;

import java.util.List;

/**
 * Created by ndegwamartin on 14/08/2018.
 */
public class PopulationCharacteristicsAdapter extends RecyclerView.Adapter<PopulationCharacteristicsAdapter.ViewHolder> {

    private List<Characteristic> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public PopulationCharacteristicsAdapter(Context context, List<Characteristic> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_population_characteristics_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Characteristic characteristic = mData.get(position);

        holder.labelTextView.setText(characteristic.getLabel());

        holder.valueTextView.setText(characteristic.getValue() ? "Yes" : "No");

        holder.infoLayout.setTag(characteristic.getKey());
        holder.infoLayout.setTag(R.id.CHARACTERISTIC_DESC, characteristic.getDescription());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView labelTextView;
        private TextView valueTextView;
        private LinearLayout infoLayout;

        ViewHolder(View itemView) {
            super(itemView);
            labelTextView = itemView.findViewById(R.id.label);
            valueTextView = itemView.findViewById(R.id.value);
            infoLayout = itemView.findViewById(R.id.info);
            infoLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
