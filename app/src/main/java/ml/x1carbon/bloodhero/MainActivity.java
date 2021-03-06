package ml.x1carbon.bloodhero;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ListView resultview;
    TextView usernameDesign;
    List<AddEntry> artists;
    DatabaseReference databaseArtists;
    Button btnNotification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);//per momentin nuk eshte aktiv
        //setSupportActionBar(toolbar);
        databaseArtists = FirebaseDatabase.getInstance().getReference("messages");
        artists = new ArrayList<>();
        databaseArtists.keepSynced(true);
        resultview = (ListView) findViewById(R.id.listView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),AddNew.class);
                startActivity(i);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Per Nitification




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseArtists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
                artists.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    AddEntry artist = postSnapshot.getValue(AddEntry.class);
                    //adding artist to the list
                    artists.add(artist);
                }

                //creating adapter
                BloodModel artistAdapter = new BloodModel(MainActivity.this, artists);
                //attaching adapter to the listview
                resultview.setAdapter(artistAdapter);
                resultview.post(new Runnable() {
                    @Override
                    public void run() {
                        resultview.setSelection(resultview.getCount() + 1);
                    }
                });
                // resultview.setStackFromBottom(true);
                // resultview.smoothScrollToPosition(artistAdapter.getCount()-1);
                resultview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        //getting the selected artist
                        AddEntry artist = artists.get(i);

                        //creating an intent
                        Intent intent = new Intent(getApplicationContext(), ViewEntries.class);

                        //putting artist name and id to intent
                        intent.putExtra("NAME", artist.getmAName());
                        intent.putExtra("PHONE", artist.getmBPhone());
                        intent.putExtra("CITY", artist.getmCCity());
                        intent.putExtra("BLOOD", artist.getmDBlood());
                        intent.putExtra("DESC", artist.getmEDescription());

                        //starting the activity with intent
                        startActivity(intent);
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        TestAsync objTaskAsync=new TestAsync();
        objTaskAsync.execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_HOME:
                Toast.makeText(getApplicationContext(), "Home button presed", Toast.LENGTH_LONG).show();
                break;
            case KeyEvent.KEYCODE_BACK:
                Toast.makeText(getApplicationContext(), "Back button presed", Toast.LENGTH_LONG).show();
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                break;

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.profile_amd:
                Intent profile=new Intent(MainActivity.this, Dashboard.class);
                startActivity(profile);
                break;

            case R.id.dashboard_amd:
                Intent home=new Intent(MainActivity.this, Dashboard.class);
                startActivity(home);
                break;
            case R.id.ap:
                Intent apIntent=new Intent(MainActivity.this,Tags.class);
                apIntent.putExtra("tag","A+");
                break;
            case R.id.an:
                Intent anIntent=new Intent(MainActivity.this,Tags.class);
                anIntent.putExtra("tag","A-");
                startActivity(anIntent);
                break;
            case R.id.bp:
                Intent bpIntent=new Intent(MainActivity.this,Tags.class);
                bpIntent.putExtra("tag","B+");
                startActivity(bpIntent);
                break;
            case R.id.bn:
                Intent bnIntent=new Intent(MainActivity.this,Tags.class);
                bnIntent.putExtra("tag","B-");
                startActivity(bnIntent);
                break;
            case R.id.op:
                Intent opIntent=new Intent(MainActivity.this,Tags.class);
                opIntent.putExtra("tag","O+");
                startActivity(opIntent);
                break;
            case R.id.on:
                Intent onIntent=new Intent(MainActivity.this,Tags.class);
                onIntent.putExtra("tag","O-");
                startActivity(onIntent);
                break;
            case R.id.abp:
                Intent abIntent=new Intent(MainActivity.this,Tags.class);
                abIntent.putExtra("tag","AB+");
                startActivity(abIntent);
                break;
            case R.id.abn:
                Intent abnIntent=new Intent(MainActivity.this,Tags.class);
                abnIntent.putExtra("tag","AB-");
                startActivity(abnIntent);
                break;
            case R.id.notifications_amd:
                Toast.makeText(getApplicationContext(),"Ska ende Notfication", Toast.LENGTH_LONG).show();
//                Intent notify=new Intent(MainActivity.this,Notifications.class);
//                startActivity(notify);
                break;
            case R.id.log_out_amd:
                AlertDialog.Builder alertDialoBuilder=new AlertDialog.Builder(this);
                alertDialoBuilder.setTitle("Logout:");
                alertDialoBuilder.setMessage("Are you sure you want to log out?");
                alertDialoBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db=(new Databaza(MainActivity.this)).getWritableDatabase();
                        ContentValues cv3=new ContentValues();
                        cv3.put("Aktiv",0);
                        db.update("Perdoruesit3",cv3,"Aktiv="+1,null);
                        Intent logOutIntent=new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(logOutIntent);
                    }
                });
                alertDialoBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialoBuilder.show();

                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    public class TestAsync extends AsyncTask<Void, Void, Void>
    {


        @Override
        protected Void doInBackground(Void... voids) {
           SystemClock.sleep(3000);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i("Test","test");
        }

        @Override
        protected void onProgressUpdate(Void... values) {

            super.onProgressUpdate(values);
        }
    }

    public void setDestroy()
    {
        onDestroy();
    }

    public void setStop()
    {
        onStop();
    }


}
