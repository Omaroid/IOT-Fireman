package com.omaroid.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    public class FirebaseHelper {

        DatabaseReference db;
        Boolean saved;
        ArrayList<Chambre> chambres = new ArrayList<>();
        ListView mListView;
        Context c;

        /*
       let's receive a reference to our FirebaseDatabase
       */
        public FirebaseHelper(DatabaseReference db, Context context, ListView mListView) {
            this.db = db;
            this.c = context;
            this.mListView = mListView;
            this.retrieve();
        }

        /*
        let's now write how to save a single Teacher to FirebaseDatabase
         */
        public Boolean save(Chambre chambre) {
            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
            String user = currentFirebaseUser.getUid();
            if (chambre== null) {
                saved = false;
            } else {
                //otherwise try to push data to firebase database.
                try {
                    //push data to FirebaseDatabase. Table or Child called Teacher will be created.
                    db.child(user).child("rooms").push().setValue(chambre);
                    saved = true;

                } catch (DatabaseException e) {
                    e.printStackTrace();
                    saved = false;
                }
            }
            //tell them of status of save.
            return saved;
        }

        /*
        Retrieve and Return them clean data in an arraylist so that they just bind it to ListView.
         */
        public ArrayList<Chambre> retrieve() {
            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
            String user = currentFirebaseUser.getUid();
            DatabaseReference db=FirebaseDatabase.getInstance().getReference("users");
            db.child(user).child("rooms").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    chambres.clear();
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            //Now get Teacher Objects and populate our arraylist.
                            Chambre chambre = ds.getValue(Chambre.class);
                            chambre.setKey(ds.getKey());
                            chambres.add(chambre);
                        }
                        adapter = new CustomAdapter(c, chambres);
                        mListView.setAdapter(adapter);

                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                mListView.smoothScrollToPosition(chambres.size());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("mTAG", databaseError.getMessage());
                    Toast.makeText(c, "ERROR " + databaseError.getMessage(), Toast.LENGTH_LONG).show();

                }
            });

            return chambres;
        }

    }

    /**********************************CUSTOM ADAPTER START************************/
    class CustomAdapter extends BaseAdapter {
        Context c;
        ArrayList<Chambre> chambres;

        public CustomAdapter(Context c, ArrayList<Chambre> chambres) {
            this.c = c;
            this.chambres = chambres;
        }

        @Override
        public int getCount() {
            return chambres.size();
        }

        @Override
        public Object getItem(int position) {
            return chambres.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(c).inflate(R.layout.row_layout, parent, false);
            }

            TextView roomname = convertView.findViewById(R.id.roomname);
            final TextView roomaddr = convertView.findViewById(R.id.roomaddr);
            TextView roompin = convertView.findViewById(R.id.roompin);
            TextView roomtemperature = convertView.findViewById(R.id.roomtemperature);
            TextView roomhumidity = convertView.findViewById(R.id.roomhumidity);
            TextView roomupdate = convertView.findViewById(R.id.roomupdate);
            TextView roomfire = convertView.findViewById(R.id.roomfire);
            final TextView roomid = convertView.findViewById(R.id.roomid);
            Button appairagebtn = convertView.findViewById(R.id.appairagebtn);
            Button removeroombtn = convertView.findViewById(R.id.removeroombtn);

            final Chambre s = (Chambre) this.getItem(position);

            roomname.setText("Chambre : "+s.getName());
            roomaddr.setText("Adresse : "+s.getAddr());
            roomfire.setText("Feu : "+Integer.toString(s.getFire()));
            roomhumidity.setText("Humidité ambiante : "+Integer.toString(s.getHumidity())+"%");
            roompin.setText("Code : "+s.getPin());
            roomupdate.setText("MAJ : "+Integer.toString(s.getUpdate()));
            roomtemperature.setText("Temperature : "+Integer.toString(s.getTemperature())+"°C");
            roomid.setText(s.getKey().toString());

            appairagebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this, AppairageActivity.class);
                    Bundle b = new Bundle();
                    b.putString("addr", roomaddr.getText().toString().substring(10));
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });

            removeroombtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                                    String user = currentFirebaseUser.getUid();
                                    DatabaseReference db= FirebaseDatabase.getInstance().getReference("users").child(user).child("rooms");
                                    db.child(roomid.getText().toString()).removeValue();
                                    //refresh listview
                                    chambres.remove(position);
                                    adapter = new CustomAdapter(HomeActivity.this, chambres);
                                    mListView.setAdapter(adapter);
                                    mListView.smoothScrollToPosition(chambres.size());
                                    Toast.makeText(c, "Suppression de la chambre effectuée", Toast.LENGTH_SHORT).show();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    Toast.makeText(c, "Suppression de la chambre annulée", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(c);
                    builder.setMessage("Etes vous surs?").setPositiveButton("Oui", dialogClickListener)
                            .setNegativeButton("Non", dialogClickListener).show();

                }
            });

            //ONITECLICK
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(c, s.getName(), Toast.LENGTH_SHORT).show();
                }
            });
            return convertView;
        }
    }

    /**********************************MAIN ACTIVITY CONTINUATION************************/
    //instance fields
    DatabaseReference db;
    FirebaseHelper helper;
    CustomAdapter adapter;
    ListView mListView;
    EditText nameEditTxt, addrEditText, pinEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mListView = (ListView) findViewById(R.id.myListView);
        //initialize firebase database
        db = FirebaseDatabase.getInstance().getReference("users");
        helper = new FirebaseHelper(db, this, mListView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListView.smoothScrollToPosition(4);
                displayInputDialog();
            }
        });
    }

    //DISPLAY INPUT DIALOG
    private void displayInputDialog() {
        //create input dialog
        Dialog d = new Dialog(this);
        d.setTitle("Save To Firebase");
        d.setContentView(R.layout.input_dialog);

        //find widgets
        nameEditTxt = d.findViewById(R.id.nameroom);
        addrEditText = d.findViewById(R.id.addrroom);
        pinEditText = d.findViewById(R.id.pinroom);
        Button saveBtn = d.findViewById(R.id.saveBtn);

        //save button clicked
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get data from edittexts
                String name = nameEditTxt.getText().toString();
                String addr = addrEditText.getText().toString();
                String pin = pinEditText.getText().toString();

                //set data to POJO
                Chambre s = new Chambre();
                s.setName(name);
                s.setAddr(addr);
                s.setPin(pin);
                s.setHumidity(0);
                s.setFire(0);
                s.setTemperature(0);
                s.setUpdate(1);

                //perform simple validation
                if (name != null && name.length() > 0) {
                    //save data to firebase
                    if (helper.save(s)) {
                        //clear edittexts
                        nameEditTxt.setText("");
                        addrEditText.setText("");
                        pinEditText.setText("");

                        //refresh listview
                        ArrayList<Chambre> fetchedData = helper.retrieve();
                        adapter = new CustomAdapter(HomeActivity.this, fetchedData);
                        mListView.setAdapter(adapter);
                        mListView.smoothScrollToPosition(fetchedData.size());
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "Name Must Not Be Empty Please", Toast.LENGTH_SHORT).show();
                }
            }
        });

        d.show();
    }

}