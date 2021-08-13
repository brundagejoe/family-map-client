package com.example.familymapclientstudent.UserInterface;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.familymapclientstudent.Proxies.DataCache;
import com.example.familymapclientstudent.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import Model.Event;
import Model.Person;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    public static final String OPTIONS_MENU_KEY = "optionsMenuKey";
    public static final String EVENT_ID_KEY = "eventID";

    private GoogleMap map;
    private DataCache dataCache;
    private String inputEventID;
    private String currentEventID;
    private List<Polyline> polyLines;


    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (map != null) {
            DataCache dataCache = DataCache.getInstance();
            map.clear();
            Set<Event> events = dataCache.getFilteredEvents();
            for (Event event : events) {
                LatLng markerData = new LatLng(event.getLatitude(), event.getLongitude());
                map.addMarker(new MarkerOptions().position(markerData)
                        .icon(BitmapDescriptorFactory.defaultMarker(createMarkerColor(event.getEventType()))))
                        .setTag(event.getEventID());

            }
            if (currentEventID == null) {
                drawLines(inputEventID);
            }
            else {
                drawLines(currentEventID);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);

        boolean hasMenu = getArguments().getBoolean(OPTIONS_MENU_KEY);
        setHasOptionsMenu(hasMenu);

        inputEventID = getArguments().getString(EVENT_ID_KEY);
        polyLines = new ArrayList<>();

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        //map.setOnMapLoadedCallback(this);

        dataCache = DataCache.getInstance();

        Event startingEvent = dataCache.getEventsByEventID().get(inputEventID);
        String startingEventID = startingEvent.getEventID();
        Person person = dataCache.getPersonFromEventID(inputEventID);
        String personID = person.getPersonID();

        TextView bottomTextView = (TextView) getView().findViewById(R.id.mapTextView);
        bottomTextView.setText(dataCache.getNameAndEventInfo(startingEvent.getEventID()));
        bottomTextView.setTag(startingEvent.getPersonID());

        char gender = person.getGender();
        ImageView genderImageView = getView().findViewById(R.id.genderImage);
        Drawable genderIcon;

        switch (gender) {
            case 'm':
                genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male)
                        .colorRes(R.color.gender_blue)
                        .sizeDp(40);
                genderImageView.setImageDrawable(genderIcon);
                break;
            case 'f':
                genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female)
                        .colorRes(R.color.gender_pink)
                        .sizeDp(40);
                genderImageView.setImageDrawable(genderIcon);
                break;
        }

        LatLng startingEventCoordinates = new LatLng(startingEvent.getLatitude(), startingEvent.getLongitude());
        map.animateCamera(CameraUpdateFactory.newLatLng(startingEventCoordinates));

        Set<Event> events = dataCache.getFilteredEvents();
        for (Event event : events) {
            LatLng markerData = new LatLng(event.getLatitude(), event.getLongitude());
            map.addMarker(new MarkerOptions().position(markerData)
                                             .icon(BitmapDescriptorFactory.defaultMarker(createMarkerColor(event.getEventType()))))
                                             .setTag(event.getEventID());

        }

        drawLines(startingEventID);

        LinearLayout eventInfoView = getView().findViewById(R.id.eventInfoView);
        eventInfoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentEventID == null) {
                    currentEventID = startingEventID;
                }
                Intent intent = new Intent(getActivity(), PersonActivity.class);
                intent.putExtra(PersonActivity.PERSON_ID_KEY, bottomTextView.getTag().toString());
                startActivity(intent);
            }
        });


        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String eventID = (String) marker.getTag();
                drawLines(eventID);
                currentEventID = eventID;
                TextView bottomTextView = (TextView) getView().findViewById(R.id.mapTextView);
                bottomTextView.setTag(dataCache.getEventsByEventID().get(eventID).getPersonID());
                bottomTextView.setText(dataCache.getNameAndEventInfo(eventID));

                char gender = dataCache.getPersonFromEventID(eventID).getGender();
                ImageView genderImageView = getView().findViewById(R.id.genderImage);
                Drawable genderIcon;

                switch (gender) {
                    case 'm':
                        genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male)
                                .colorRes(R.color.gender_blue)
                                .sizeDp(40);
                        genderImageView.setImageDrawable(genderIcon);
                        break;
                    case 'f':
                        genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female)
                                .colorRes(R.color.gender_pink)
                                .sizeDp(40);
                        genderImageView.setImageDrawable(genderIcon);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onMapLoaded() {
        //Won't need unless setOnMapLoadedCallback is necessary. Put all marker stuff in here if error occurs.
    }

    private float createMarkerColor(String eventType) {
        eventType = eventType.toLowerCase();
        DataCache dataCache = DataCache.getInstance();

        //Float values representing different colors for marker colors
        List<Float> markerColors = Arrays.asList(240f, 180f, 120f, 300f, 30f, 330f, 270f, 60f);

        Map<String, Integer> eventTypeIndexes = new HashMap<>();

        int index = 0;

        Set<String> eventTypes = dataCache.getEventTypes();
        Set<String> eventTypesFiltered = new HashSet<>();

        for (String type : eventTypes) {
            eventTypesFiltered.add(type.toLowerCase());
        }

        for (String type : eventTypesFiltered) {
            if (!(type.equalsIgnoreCase("birth") || type.equalsIgnoreCase("death"))) {
                eventTypeIndexes.put(type, index % markerColors.size());
                ++index;
            }
        }

        if (eventType.equalsIgnoreCase( "birth")) {
            return 210f;
        }
        if (eventType.equalsIgnoreCase("death")) {
            return 0f;
        }

        return markerColors.get(eventTypeIndexes.get(eventType));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.map_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.settingsButton:
                intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.searchButton:
                intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void drawSpouseLines(String eventID) {
        Person person = dataCache.getPersonFromEventID(eventID);

        if (!dataCache.isEventShown(eventID)) {
            return;
        }


        if (person.getSpouseID() == null) {
            return;
        }


        Person spouse = dataCache.getPeopleByID().get(person.getSpouseID());


        if (dataCache.getEventsByPersonID().get(spouse.getPersonID()).size() < 1) {
            return;
        }

        Event personEvent = dataCache.getEventsByEventID().get(eventID);
        Event spouseEvent = dataCache.getEventsByPersonID().get(spouse.getPersonID()).first();

        if (!dataCache.isEventShown(spouseEvent.getEventID())) {
            return;
        }

        PolylineOptions polylineOptions = new PolylineOptions()
                .add(new LatLng(personEvent.getLatitude(), personEvent.getLongitude()))
                .add(new LatLng(spouseEvent.getLatitude(), spouseEvent.getLongitude()))
                .width(25)
                .color(Color.RED);
        polyLines.add(map.addPolyline(polylineOptions));
    }

    private void drawFamilyLines(String eventID, int generationsBack) {
        double lineWidth = 50 / (1 + (double)generationsBack);

        if (!dataCache.isEventShown(eventID)) {
            return;
        }

        Person person = dataCache.getPersonFromEventID(eventID);

        if (person.getFatherID() != null) {
            Person father = dataCache.getPeopleByID().get(person.getFatherID());

            if (dataCache.getEventsByPersonID().get(father.getPersonID()).size() > 0) {
                Event personEvent = dataCache.getEventsByEventID().get(eventID);
                Event parentEvent = dataCache.getEventsByPersonID().get(father.getPersonID()).first();

                if (dataCache.isEventShown(parentEvent.getEventID())) {
                    PolylineOptions polylineOptions = new PolylineOptions()
                            .add(new LatLng(personEvent.getLatitude(), personEvent.getLongitude()))
                            .add(new LatLng(parentEvent.getLatitude(), parentEvent.getLongitude()))
                            .width((int)lineWidth)
                            .color(Color.GREEN);
                    polyLines.add(map.addPolyline(polylineOptions));
                    drawFamilyLines(parentEvent.getEventID(), generationsBack + 1);
                }
            }
        }

        if (person.getMotherID() != null) {
            Person mother = dataCache.getPeopleByID().get(person.getMotherID());

            if (dataCache.getEventsByPersonID().get(mother.getPersonID()).size() > 0) {
                Event personEvent = dataCache.getEventsByEventID().get(eventID);
                Event parentEvent = dataCache.getEventsByPersonID().get(mother.getPersonID()).first();

                if (dataCache.isEventShown(parentEvent.getEventID())) {
                    PolylineOptions polylineOptions = new PolylineOptions()
                            .add(new LatLng(personEvent.getLatitude(), personEvent.getLongitude()))
                            .add(new LatLng(parentEvent.getLatitude(), parentEvent.getLongitude()))
                            .width((int)lineWidth)
                            .color(Color.GREEN);
                    polyLines.add(map.addPolyline(polylineOptions));
                    drawFamilyLines(parentEvent.getEventID(), generationsBack + 1);
                }
            }
        }
    }

    private void drawLifeStoryLines(String eventID) {
        Person person = dataCache.getPersonFromEventID(eventID);
        if (!dataCache.isPersonShown(person.getPersonID())) {
            return;
        }

        SortedSet<Event> events = dataCache.getEventsByPersonID().get(person.getPersonID());
        Event firstEvent = null;
        for (Event secondEvent : events) {
            if (events.first().equals(secondEvent)) {
                firstEvent = secondEvent;
            }
            else {
                assert firstEvent != null;
                PolylineOptions polylineOptions = new PolylineOptions()
                        .add(new LatLng(firstEvent.getLatitude(), firstEvent.getLongitude()))
                        .add(new LatLng(secondEvent.getLatitude(), secondEvent.getLongitude()))
                        .width(25)
                        .color(Color.BLUE);
                polyLines.add(map.addPolyline(polylineOptions));
                firstEvent = secondEvent;
            }
        }
    }

    private void drawLines(String eventID) {
        for (Polyline line : polyLines) {
            line.remove();
        }
        polyLines.clear();

        if (dataCache.isShowSpouseLines()) {
            drawSpouseLines(eventID);
        }
        if (dataCache.isShowFamilyTreeLines()) {
            drawFamilyLines(eventID, 0);
        }
        if (dataCache.isShowLifeStoryLines()) {
            drawLifeStoryLines(eventID);
        }
    }
}