package com.aabusabra.evrekademo.helper;

import androidx.annotation.NonNull;

import com.aabusabra.evrekademo.model.Container;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseCRUDHelper {

    DatabaseReference db;
    List<Container> allContainers = new ArrayList<>();
    Boolean saved=null;



    public FirebaseCRUDHelper(DatabaseReference db) {
        this.db = db;
    }


    //Read
    public List<Container> getData()
    {

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fetchData(dataSnapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return allContainers;
    }


    private void fetchData(DataSnapshot dataSnapshot)
    {
        allContainers.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {

            Container newContainer = ds.getValue(Container.class);

            allContainers.add(newContainer);
        }
    }



    public Boolean save(Container container)
    {
        if(container!=null)
        {
            saved=false;
        }else
        {
            try
            {
                db.child("Container").push().setValue(container);
                saved=true;
            }catch (DatabaseException e)
            {
                e.printStackTrace();
                saved=false;
            }
        }
        return saved;
    }


    public void add(Container container, String id)
    {
        try
        {
            db.child(id).setValue(container);

//            db.child("Container").push().setValue(container);
        }catch (DatabaseException e)
        {
            e.printStackTrace();
        }

    }

}
