package com.example.admin.healthtrack.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.healthtrack.R;
import com.example.admin.healthtrack.models.Medicament;
import com.example.admin.healthtrack.models.Treatment;
import com.example.admin.healthtrack.views.MedicamentDialog;

import java.util.ArrayList;

public class TreatmentAdapter extends RecyclerView.Adapter<TreatmentAdapter.MyViewHolder> {
    private ArrayList<Treatment> treatments;
    private TreatmentAdapter.ItemClickListener mClickListener;

    public TreatmentAdapter(ArrayList<Treatment> treatments) {
        this.treatments = treatments;
    }

    @Override
    public TreatmentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_treatment, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TreatmentAdapter.MyViewHolder holder, int position) {
        final Treatment treatment = treatments.get(position);
        holder.textView_parentName.setText(treatment.getName());
        //
        int noOfChildTextViews = holder.linearLayout_childItems.getChildCount();
        int noOfChild = treatment.getMedicaments().size();
        if (noOfChild < noOfChildTextViews) {
            for (int index = noOfChild; index < noOfChildTextViews; index++) {
                TextView currentTextView = (TextView) holder.linearLayout_childItems.getChildAt(index);
                currentTextView.setVisibility(View.GONE);
            }
        }
        for (int textViewIndex = 0; textViewIndex < noOfChild; textViewIndex++) {
            final TextView currentTextView = (TextView) holder.linearLayout_childItems.getChildAt(textViewIndex);
            final Medicament med = treatment.getMedicaments().get(textViewIndex);
            currentTextView.setText(treatment.getMedicaments().get(textViewIndex).getName());
            currentTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) mClickListener.onItemClick(med);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return treatments.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context context;
        private TextView textView_parentName;
        private LinearLayout linearLayout_childItems;

        MyViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            textView_parentName = itemView.findViewById(R.id.tv_parentName);
            linearLayout_childItems = itemView.findViewById(R.id.ll_child_items);
            linearLayout_childItems.setVisibility(View.GONE);
            int intMaxNoOfChild = 0;
            for (int index = 0; index < treatments.size(); index++) {
                int intMaxSizeTemp = treatments.get(index).getMedicaments().size();
                if (intMaxSizeTemp > intMaxNoOfChild) intMaxNoOfChild = intMaxSizeTemp;
            }
            for (int indexView = 0; indexView < intMaxNoOfChild; indexView++) {
                TextView textView = new TextView(context);
                textView.setId(indexView);
                textView.setPadding(0, 20, 0, 20);
                textView.setGravity(Gravity.CENTER);
                textView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_sub_text));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textView.setOnClickListener(this);
                linearLayout_childItems.addView(textView, layoutParams);
            }
            textView_parentName.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.tv_parentName) {
                if (linearLayout_childItems.getVisibility() == View.VISIBLE) {
                    linearLayout_childItems.setVisibility(View.GONE);
                } else {
                    linearLayout_childItems.setVisibility(View.VISIBLE);
                }
            } else {
                TextView textViewClicked = (TextView) view;
                Toast.makeText(context, "" + textViewClicked.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    // allows clicks events to be caught
    public  void setClickListener(TreatmentAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick( Medicament med);
    }
}