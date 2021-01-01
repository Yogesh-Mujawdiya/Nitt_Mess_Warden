package com.my.nitt_mess_warden.ui;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.nitt_mess_warden.Adapter.MessListAdapter;
import com.my.nitt_mess_warden.Class.Mess;
import com.my.nitt_mess_warden.HomeActivity;
import com.my.nitt_mess_warden.R;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class MessMenuFragment extends Fragment {

    RecyclerView recyclerView;
    MessListAdapter recyclerAdapter;
    List<Mess> messList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mess_menu, container, false);

        recyclerView = root.findViewById(R.id.recyclerViewMessList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setHasOptionsMenu(true);
        String UserId = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Data/Warden").child(UserId).child("MessId");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    messList = new ArrayList<Mess>();
                    for (DataSnapshot mess : dataSnapshot.getChildren()) {
                        getMessData(mess.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return root;
    }

    private void getMessData(final String MessId) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Data/Mess").child(MessId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot mess) {
                if (mess.exists()) {
                    Mess M = new Mess(
                            mess.getKey(),
                            mess.child("Name").getValue().toString(),
                            Integer.parseInt(mess.child("Total").getValue().toString()),
                            Integer.parseInt(mess.child("Allocate").getValue().toString()),
                            new Hashtable());
                    messList.add(M);
                }
                recyclerAdapter = new MessListAdapter(messList, getContext());
                recyclerView.setAdapter(recyclerAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.home, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView(((HomeActivity) getContext()).getSupportActionBar().getThemedContext());
        searchView.setQueryHint("Mess Name");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setActionView(searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerAdapter.getFilter().filter(newText);
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {

                                          }
                                      }
        );
        super.onCreateOptionsMenu(menu, inflater);
    }

}