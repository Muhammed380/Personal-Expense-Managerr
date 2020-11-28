package com.muhammed.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    private DashboardFragment dashboardFragment;
    private  ExpenseFragment expenseFragment;
    private IncomeFragment incomeFragment;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Expense Manager");
        mAuth = FirebaseAuth.getInstance();

        setSupportActionBar(toolbar);
        bottomNavigationView=findViewById(R.id.bottomNavigationbar);
        frameLayout = findViewById(R.id.main_frame);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle  = new ActionBarDrawerToggle(
                this,drawerLayout, toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.naView);
        navigationView.setNavigationItemSelectedListener(this);

        dashboardFragment = new DashboardFragment();
        expenseFragment = new ExpenseFragment();
        incomeFragment = new IncomeFragment();
        setFragment(dashboardFragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.dashboard:
                        setFragment(dashboardFragment);
                        bottomNavigationView.setItemBackgroundResource(R.color.type);
                        return true;
                    case R.id.income:
                        setFragment(incomeFragment);
                        bottomNavigationView.setItemBackgroundResource(R.color.ıncomeNum);
                        return true;
                    case R.id.expense:
                        setFragment(expenseFragment);
                        bottomNavigationView.setItemBackgroundResource(R.color.expenseNum);

                        return true;
                    default:
                        return false;
                }


            }
        });




    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction  = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        }else {
            super.onBackPressed();

        }
    }
    public void displaySelectedListener (int itemId) {
        Fragment fragment = null;
        switch (itemId) {
            case R.id.dashboard:
                fragment = new DashboardFragment();
                break;
            case R.id.income:
                fragment = new IncomeFragment();

                break;
            case R.id.expense:
                fragment = new ExpenseFragment();
                break;
            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));

        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame,fragment);
            ft.commit();

        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displaySelectedListener(item.getItemId());
        return true;
    }
}