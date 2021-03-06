package com.example.nutritiondiary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private RecyclerView childView;

    boolean authState = true;
    private RecyclerView.Adapter catAdapter;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseAuth mAuth;

    private FirebaseFirestore firebaseFirestore;

    List<ChildList> childLists = null;

    private long doubleBackToExitPressedOnce;

    String s;

    List<Integer> calories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        childView = (RecyclerView)findViewById(R.id.childlist);

        childView.setHasFixedSize(true);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);

        childLists = new ArrayList<>();

        childLists.clear();
        calories = new ArrayList<>();


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null){

                    startActivity(new Intent(MainActivity.this,SplashActivity.class));
                    authState = false;
                }
            }
        };


        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        int month = calendar.get(Calendar.MONTH);

        int day = calendar.get(Calendar.DAY_OF_MONTH);


        s = String.valueOf(year)+String.valueOf(month+1)+String.valueOf(day);


        Query firestoreQuery = firebaseFirestore.collection("parents").document(mAuth.getCurrentUser().getUid().toString()).collection("Childs").orderBy("name");

        firestoreQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(!queryDocumentSnapshots.isEmpty()){

                    for(DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){

                        if(documentChange.getType() == DocumentChange.Type.ADDED){


                            String Childid = documentChange.getDocument().getId();

                            ChildList childList = documentChange.getDocument().toObject(ChildList.class).withID(Childid,"",mAuth.getCurrentUser().getUid());

                            childLists.add(childList);

                            catAdapter.notifyDataSetChanged();

                        }

                    }

                }

            }
        });


        DividerItemDecoration divider = new DividerItemDecoration(childView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.custom_divider));
        childView.addItemDecoration(divider);


        childView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        catAdapter = new ChildListAdapter(childLists);
        catAdapter.notifyDataSetChanged();

        childView.setAdapter(catAdapter);



//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(HomeActivity.this,NewChildActivity.class));
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (doubleBackToExitPressedOnce + 2000 > System.currentTimeMillis()){

            finishAffinity();

            super.onBackPressed();
        }
        else{

            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {

                Toast.makeText(getBaseContext(),
                        "Press once again to exit!", Toast.LENGTH_SHORT)
                        .show();
            }


        }
        doubleBackToExitPressedOnce = System.currentTimeMillis();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            startActivity(new Intent(MainActivity.this,NewChildActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

            firebaseFirestore.collection("caregivers").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.getResult().exists()){

                        startActivity(new Intent(MainActivity.this,CareGiverParentsActivity.class));
                    }

                    else {


                        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        final View mView = getLayoutInflater().inflate(R.layout.caregiver_dialouge,null);

                        final ImageView join = (ImageView) mView.findViewById(R.id.joinascg);

                        final ImageView notnow = (ImageView) mView.findViewById(R.id.notnowascg);


                        alert.setView(mView);
                        final AlertDialog dialog = alert.create();

                        join.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                dialog.dismiss();
                                startActivity(new Intent(MainActivity.this,BeCareGiverActivity.class));

                            }
                        });


                        notnow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                dialog.dismiss();

                            }
                        });

                        dialog.show();


                    }

                }
            });


        } else if (id == R.id.nav_gallery) {

            startActivity(new Intent(MainActivity.this,AddFoodActivity.class));

        } else if (id == R.id.nav_slideshow) {



        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
