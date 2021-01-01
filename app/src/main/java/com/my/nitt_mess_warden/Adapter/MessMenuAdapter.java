package com.my.nitt_mess_warden.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.my.nitt_mess_warden.Class.Mess;
import com.my.nitt_mess_warden.Class.MessMenu;
import com.my.nitt_mess_warden.R;

import java.util.ArrayList;
import java.util.List;

public class MessMenuAdapter extends RecyclerView.Adapter<MessMenuAdapter.ViewHolder> implements Filterable {

    private static final String TAG = "RecyclerAdapter";
    List<MessMenu> messMenu, fullList;
    Context context;

    public MessMenuAdapter(List<MessMenu> messMenu, Context context) {

        this.messMenu = messMenu;
        fullList = messMenu;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.menu_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MessMenu menu = messMenu.get(position);
        holder.WeekDay.setText(menu.getWeekName());
        holder.Breakfast.setText(menu.getBreakfast());
        holder.Lunch.setText(menu.getLunch());
        holder.Snack.setText(menu.getSnack());
        holder.Dinner.setText(menu.getDinner());
    }

    @Override
    public int getItemCount() {
        return messMenu.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView WeekDay, Breakfast, Lunch, Dinner, Snack;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            WeekDay = itemView.findViewById(R.id.weekName);
            Breakfast = itemView.findViewById(R.id.Breakfast);
            Lunch = itemView.findViewById(R.id.Lunch);
            Snack = itemView.findViewById(R.id.Snack);
            Dinner = itemView.findViewById(R.id.Dinner);
            cardView = itemView.findViewById(R.id.cardView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
//            Toast.makeText(view.getContext(), messMenu.get(getAdapterPosition()), Toast.LENGTH_SHORT).show();
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
                ArrayList<MessMenu> fRecords = new ArrayList<>();

                for (MessMenu s : fullList) {
                    if (s.getWeekName().toUpperCase().trim().contains(constraint.toString().toUpperCase().trim())) {
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
            messMenu = (ArrayList<MessMenu>) results.values;
            notifyDataSetChanged();
        }
    }
}