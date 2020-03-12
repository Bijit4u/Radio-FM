package abc.def.mp3.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import abc.def.mp3.Home.Fragment.ListFragment;
import abc.def.mp3.R;
import abc.def.mp3.Utils.Tools;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Tools.setSystemBarColor(this, R.color.colorAccent);
        loadFragment(new ListFragment(),"Home");
        initComponent();



    }

    private BottomNavigationView navigation;
    private void initComponent() {
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        loadFragment(new ListFragment(), "home");
                        //item.setIcon(R.drawable.icon_20);
                        return true;
                    case R.id.rProg:
                        loadFragment(new ListFragment(), "rProg");
                        return true;
                    case R.id.about:
                        loadFragment(new ListFragment(), "about");
                        return true;
                    case R.id.contact:
                        loadFragment(new ListFragment(), "contact");
                        return true;
                }
                return false;
            }
        });
    }


    Fragment activeFragment=null;
    String activeFragment_="";
    public boolean loadFragment(Fragment fragment,String name) {
        try{
            Log.d("fragment___",fragment.toString());
            Log.d("active_fragment",activeFragment.toString());}catch (Exception e){}
        if(!activeFragment_.equals(name)){
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                activeFragment_=name;
                return true;
            }
        }
        return false;
    }
}
