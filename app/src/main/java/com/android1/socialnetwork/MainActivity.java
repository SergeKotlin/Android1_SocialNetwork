package com.android1.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android1.socialnetwork.observer.Publisher;
import com.android1.socialnetwork.ui.SocialNetworkFragment;

public class MainActivity extends AppCompatActivity {

    private Navigation navigation;
    private Publisher publisher = new Publisher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigation = new Navigation(getSupportFragmentManager());
        initToolbar();
//        addFragment(SocialNetworkFragment.newInstance());
        getNavigation().addFragment(SocialNetworkFragment.newInstance(), false);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    /*private void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager(); //Получить менеджер фрагментов
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); // Открыть транзакцию
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null); // Кинуть в стэк обратного вызова
        fragmentTransaction.commit(); // Закрыть транзакцию
    }*/

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public Navigation getNavigation() {
        return navigation;
    }

    public Publisher getPublisher() {
        return publisher;
    }

}