package com.example.sid24rane.blockcanteen.Dashboard;


import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid24rane.blockcanteen.Dashboard.AllTransactions.AllTransactionsFragment;
import com.example.sid24rane.blockcanteen.Dashboard.Home.HomeFragment;
import com.example.sid24rane.blockcanteen.Dashboard.Profile.ProfileFragment;
import com.example.sid24rane.blockcanteen.R;
import com.example.sid24rane.blockcanteen.utilities.ConnectionLiveData;
import com.example.sid24rane.blockcanteen.utilities.ConnectionModel;

public class DashboardActivity extends AppCompatActivity {

    public static final int MobileData = 2;
    public static final int WifiData = 1;
    private LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private boolean doubleBackToExitPressedOnce = false;
    private boolean first = true;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        checkConnection();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragmentTransaction.replace(R.id.container, new HomeFragment()).commit();

    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container,new HomeFragment()).commit();
                    return true;
                case R.id.navigation_dashboard:
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container,new ProfileFragment()).commit();
                    return true;
                case R.id.navigation_notifications:
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container,new AllTransactionsFragment()).commit();
                    return true;
            }
            return false;
        }
    };

    private void checkConnection(){

        /* Live data object and setting an oberser on it */
        ConnectionLiveData connectionLiveData = new ConnectionLiveData(getApplicationContext());
        connectionLiveData.observe(this, new Observer<ConnectionModel>() {
            @Override
            public void onChanged(@Nullable ConnectionModel connection) {
                /* every time connection state changes, we'll be notified and can perform action accordingly */
                if (connection.getIsConnected()) {
                    switch (connection.getType()) {
                        case WifiData:
                        case MobileData:
                            if (!first){
                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "Internet Connected!", Snackbar.LENGTH_LONG);
                                View view = snackbar.getView();
                                CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)view.getLayoutParams();
                                params.gravity = Gravity.TOP;
                                view.setLayoutParams(params);
                                snackbar.show();
                            }
                            break;
                    }
                } else {
                    first = false;
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setActionTextColor(Color.RED);
                    View view = snackbar.getView();
                    CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)view.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    view.setLayoutParams(params);
                    TextView textView = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        first = true;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;

        Toast.makeText(this, "Press BACK button again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    /* required to make activity life cycle owner */
    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycleRegistry;
    }

     /*   @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }
*/
}
