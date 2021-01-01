package com.my.nitt_mess_warden.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.my.nitt_mess_warden.Class.Mess;
import com.my.nitt_mess_warden.FeedbackActivity;
import com.my.nitt_mess_warden.MessRatingActivity;
import com.my.nitt_mess_warden.R;

import java.util.ArrayList;
import java.util.List;

public class MessFeedbackAdapter extends RecyclerView.Adapter<MessFeedbackAdapter.ViewHolder> implements Filterable {

    private static final String TAG = "RecyclerAdapter";
    List<Mess> messList, fullList;
    Context context;

    public MessFeedbackAdapter(List<Mess> messList, Context context) {

        this.messList = messList;
        fullList = messList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.mess, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Mess mess = messList.get(position);
        holder.textView.setText(mess.getName());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FeedbackActivity.class);
                intent.putExtra("Id", mess.getId());
                intent.putExtra("Name", mess.getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            cardView = itemView.findViewById(R.id.cardView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
//            Toast.makeText(view.getContext(), messList.get(getAdapterPosition()), Toast.LENGTH_SHORT).show();
        }
    }


    private Filter fRecords;

    //return the filter class object
    @Override
    public Filter getFilter() {
        if(fRecords == null) {
            fRecords=new RecordFilter();
        }
        return fRecords;
    }


    //filter class
    private class RecordFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            //Implement filter logic
            // if edittext is null return the actual list
            if (constraint == null || constraint.length() == 0) {
                //No need for filter
                results.values = fullList;
                results.count = fullList.size();

            } else {
                //Need Filter
                // it matches the text  entered in the edittext and set the data in adapter list
                ArrayList<Mess> fRecords = new ArrayList<>();

                for (Mess s : fullList) {
                    if (s.getName().toUpperCase().trim().contains(constraint.toString().toUpperCase().trim())) {
                        fRecords.add(s);
                    }
                }
                results.values = fRecords;
                results.count = fRecords.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,FilterResults results) {

            //it set the data from filter to adapter list and refresh the recyclerview adapter
            messList = (ArrayList<Mess>) results.values;
            notifyDataSetChanged();
        }
    }
}