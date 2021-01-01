package com.my.nitt_mess_warden.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.nitt_mess_warden.Adapter.StudentsLeaveRequestHistoryAdapter;
import com.my.nitt_mess_warden.Class.Leave;
import com.my.nitt_mess_warden.HomeActivity;
import com.my.nitt_mess_warden.R;

import java.util.ArrayList;
import java.util.List;

public class LeaveRequestFragment extends Fragment {
    FirebaseUser User;

    RecyclerView recyclerView;
    StudentsLeaveRequestHistoryAdapter recyclerAdapter;
    List<Leave> leaveList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_leave_request, container, false);

        User = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = root.findViewById(R.id.recyclerViewLeaveRequest);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setHasOptionsMenu(true);
        String UserId = User.getEmail().split("@")[0];
        DatabaseReference Database = FirebaseDatabase.getInstance().getReference("Data/Warden").child(UserId);
        Database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                if(data.exists()) {
                    for(DataSnapshot mess : data.child("MessId").getChildren())
                        getData(mess.getKey());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return root;
    }

    private void getData(String MessId){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Data/MessLeaveRequest").child(MessId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    leaveList = new ArrayList<>();
                    for (final DataSnapshot leaveRequest : dataSnapshot.getChildren()) {
                        getLeaveRequest(leaveRequest.getKey());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getLeaveRequest(String RequestId) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Data/LeaveRequest").child(RequestId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot leaveRequest) {
                if(leaveRequest.exists()) {
                        Leave leave = new Leave(
                                leaveRequest.getKey(),
                                leaveRequest.child("from").getValue().toString(),
                                leaveRequest.child("to").getValue().toString(),
                                leaveRequest.child("reason").getValue().toString(),
                                leaveRequest.child("rollNo").getValue().toString(),
                                leaveRequest.child("messId").getValue().toString(),
                                leaveRequest.child("status").getValue().toString()
                        );
                        leaveList.add(leave);
                    recyclerAdapter = new StudentsLeaveRequestHistoryAdapter(leaveList, getContext());
                    recyclerView.setAdapter(recyclerAdapter);
                }
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
        searchView.setQueryHint("Roll No");
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