package tn.bchat.aramex.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import de.hdodenhof.circleimageview.CircleImageView;
import tn.bchat.aramex.Adapters.TablayoutAdapter;
import tn.bchat.aramex.Fragments.CollisFragment;
import tn.bchat.aramex.Fragments.FindAramexFragment;
import tn.bchat.aramex.Fragments.MapFragment;
import tn.bchat.aramex.Fragments.TarifFragment;
import tn.bchat.aramex.Fragments.TrackMapFragment;
import tn.bchat.aramex.Models.User;
import tn.bchat.aramex.R;
import tn.bchat.aramex.Utils.ConnectionReceiver;
import tn.bchat.aramex.Utils.SharedPrefManager;

public class DashboardActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    NavigationView navigationView;
    TabLayout tabLayout;
    ViewPager viewPager;
    ImageView menuImage;
    //CircleImageView profileIv;
    TextView emailTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        if (!isConnected(this)) {
            showInternetDialog();
        }

        menuImage = findViewById(R.id.menuIv);
        //profileIv = findViewById(R.id.profileIv);
        //emailTv = findViewById(R.id.emailTv);

        initDrawerLayout();

        //initTablyout();

        //if the user is not logged in
        //starting the login activity
      /*  if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        //getting the current user
        User user = SharedPrefManager.getInstance(this).getUser();
        //setting the values to the textviews
        emailTv.setText(user.getEmail());
        */

    }

    //drawer layout
    private void initDrawerLayout(){
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolBar);

        setSupportActionBar(toolbar);
        //toolbar.setTitle("");
        //toolbar.setNavigationIcon(R.drawable.ic_food_menu);
        //toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_food_menu));

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //set default fragment
        loadFragment(new CollisFragment());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Fragment fragment = null;
                switch (id){
                    case R.id.nav_home:
                        fragment = new CollisFragment();
                        loadFragment(fragment);
                        break;
                    case R.id.nav_tarif:
                        fragment = new TarifFragment();
                        loadFragment(fragment);
                        break;
                    case R.id.nav_map:
                        fragment = new TrackMapFragment();
                        //fragment = new FindAramexFragment();
                        loadFragment(fragment);
                        break;
                    case R.id.nav_logout:
                        logout();
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        // create a FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // replace the FrameLayout with new Fragment and save the changes
        fragmentTransaction.replace(R.id.frame, fragment).commit();

        drawerLayout.closeDrawer(GravityCompat.START);
        fragmentTransaction.addToBackStack(null);
    }
    private void logout() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure to exit the application ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                //SharedPrefManager.getInstance(getApplicationContext()).logout();
                Intent loginPage = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginPage);
            }
        });
        builder.setNegativeButton("No", null);
        builder.create().show();

    }

    //tablayout
    private void initTablyout(){
        tabLayout = findViewById(R.id.food_tab);
        viewPager = findViewById(R.id.food_viewpager);

        tabLayout.addTab(tabLayout.newTab().setText("Collis"));     //0
        tabLayout.addTab(tabLayout.newTab().setText("Tarifs"));    //1
        tabLayout.addTab(tabLayout.newTab().setText("Track"));    //2
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabTextColors(Color.parseColor("#020000"), Color.parseColor("#11CFC5"));
        tabLayout.setSelectedTabIndicator(0);

        final TablayoutAdapter adapter = new TablayoutAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void onProfileClick(View View){
        startActivity(new Intent(this,ProfileActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);

    }
    //connection internet
    private void showInternetDialog() {

        Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        View view = LayoutInflater.from(this).inflate(R.layout.no_internet_dialog, findViewById(R.id.no_internet_layout));
        view.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected(DashboardActivity.this)) {
                    showInternetDialog();
                } else {
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                    finish();
                }
            }
        });

        dialog.setContentView(view);
        dialog.show();

    }

    private boolean isConnected(DashboardActivity dashboardActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) dashboardActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        //hide
        //menu.findItem(R.id.action_search).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

  /*  @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    } */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(this.getClass().getName(), "back button pressed");
            // your code to handle back button event.
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}