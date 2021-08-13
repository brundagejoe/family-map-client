 package com.example.familymapclientstudent.UserInterface;

 import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

 import com.example.familymapclientstudent.Proxies.DataCache;
 import com.example.familymapclientstudent.R;
 import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

 import java.util.Objects;


 public class MainActivity extends AppCompatActivity implements LoginFragment.Listener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Iconify.with(new FontAwesomeModule());

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.loginFrame);

        if (fragment == null) {
            fragment = new LoginFragment();
            ((LoginFragment) fragment).registerListener(this);
            fragmentManager.beginTransaction().add(R.id.loginFrame, fragment).commit();
        }
    }

     @Override
     public void notifyDone() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = new MapFragment();

        String userID = DataCache.getInstance().getUser().getPersonID();
        String userBirthID = Objects.requireNonNull(DataCache.getInstance()
                .getEventsByPersonID()
                .get(userID))
                .first()
                .getEventID();

        Bundle bundle = new Bundle();
        bundle.putString(MapFragment.EVENT_ID_KEY, userBirthID);
        bundle.putBoolean(MapFragment.OPTIONS_MENU_KEY, true);
        fragment.setArguments(bundle);

        fragmentManager.beginTransaction().replace(R.id.loginFrame, fragment).commit();


     }
 }