package com.example.familymapclientstudent.UserInterface;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.familymapclientstudent.Proxies.DataCache;
import com.example.familymapclientstudent.R;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.List;

import Model.Event;
import Model.Person;

public class PersonActivity extends AppCompatActivity {
    public static final String PERSON_ID_KEY = "PersonIDKey";
    private String personID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        personID = intent.getStringExtra(PERSON_ID_KEY);

        DataCache dataCache = DataCache.getInstance();

        Person person = dataCache.getPeopleByID().get(personID);

        TextView firstNameField = findViewById(R.id.firstNameField);
        TextView lastNameField = findViewById(R.id.lastNameField);
        TextView genderField = findViewById(R.id.genderField);

        firstNameField.setText(person.getFirstName());
        lastNameField.setText(person.getLastName());

        char gender = person.getGender();

        if (gender == 'm') {
            genderField.setText("Male");
        }
        if (gender == 'f') {
            genderField.setText("Female");
        }

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);

        List<Event> eventList = new ArrayList<>(dataCache.getEventsByPersonID().get(personID));
        List<Person> peopleList = new ArrayList<>();

        if (person.getFatherID() != null) {
            Person father = dataCache.getPeopleByID().get(person.getFatherID());
            peopleList.add(father);
        }
        if (person.getMotherID() != null) {
            Person mother = dataCache.getPeopleByID().get(person.getMotherID());
            peopleList.add(mother);
        }
        if (person.getSpouseID() != null) {
            Person spouse = dataCache.getPeopleByID().get(person.getSpouseID());
            peopleList.add(spouse);
        }
        if (dataCache.getChildrenByParentID().containsKey(personID)) {
            peopleList.addAll(dataCache.getChildrenByParentID().get(personID));
        }

        if (!dataCache.isPersonShown(personID)) {
            eventList.clear();
        }

        expandableListView.setAdapter(new ExpandableListAdapter(eventList, peopleList));


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                          Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        return true;
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        private static final int LIFE_EVENT_GROUP_POSITION = 0;
        private static final int FAMILY_GROUP_POSITION = 1;

        private final List<Event> eventList;
        private final List<Person> peopleList;

        public ExpandableListAdapter(List<Event> eventList, List<Person> peopleList) {
            this.eventList = eventList;
            this.peopleList = peopleList;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch(groupPosition) {
                case LIFE_EVENT_GROUP_POSITION:
                    return eventList.size();
                case FAMILY_GROUP_POSITION:
                    return peopleList.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch(groupPosition) {
                case LIFE_EVENT_GROUP_POSITION:
                    return "Life Events";
                case FAMILY_GROUP_POSITION:
                    return "Family";
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch(groupPosition) {
                case LIFE_EVENT_GROUP_POSITION:
                    return eventList.get(childPosition);
                case FAMILY_GROUP_POSITION:
                    return peopleList.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch(groupPosition) {
                case LIFE_EVENT_GROUP_POSITION:
                    titleView.setText("Life Events");
                    break;
                case FAMILY_GROUP_POSITION:
                    titleView.setText("Family");
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }


            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView = getLayoutInflater().inflate(R.layout.list_item, parent, false);

            switch(groupPosition) {
                case LIFE_EVENT_GROUP_POSITION:
                    initializeEventView(itemView, childPosition);
                    break;
                case FAMILY_GROUP_POSITION:
                    intializeFamilyView(itemView, childPosition);
                    break;
            }
            return itemView;
        }

        private void initializeEventView(View itemView, int childPosition) {
            TextView firstLineView = itemView.findViewById(R.id.firstLineField);
            TextView secondLineView = itemView.findViewById(R.id.secondLineField);
            ImageView imageView = itemView.findViewById(R.id.iconField);

            DataCache dataCache = DataCache.getInstance();

            Event event = eventList.get(childPosition);
            firstLineView.setText(dataCache.getEventInfo(event));

            Person person = dataCache.getPersonFromEventID(event.getEventID());
            String personInfo = person.getFirstName() + " " + person.getLastName();
            secondLineView.setText(personInfo);

            Drawable eventIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker)
                    .colorRes(R.color.black)
                    .sizeDp(25);
            imageView.setImageDrawable(eventIcon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PersonActivity.this, EventActivity.class);
                    intent.putExtra(MapFragment.EVENT_ID_KEY, event.getEventID());
                    intent.putExtra(MapFragment.OPTIONS_MENU_KEY, false);
                    startActivity(intent);
                }
            });
        }

        private void intializeFamilyView(View itemView, int childPosition) {
            TextView firstLineView = itemView.findViewById(R.id.firstLineField);
            TextView secondLineView = itemView.findViewById(R.id.secondLineField);

            Person relatedPerson = peopleList.get(childPosition);
            String personInfo = relatedPerson.getFirstName() + " " + relatedPerson.getLastName();
            firstLineView.setText(personInfo);

            DataCache dataCache = DataCache.getInstance();
            Person person = dataCache.getPeopleByID().get(personID);

            String relationshipStatus = "Child";
            assert person != null;
            if (person.getSpouseID() != null) {
                if (person.getSpouseID().equals(relatedPerson.getPersonID())) {
                    relationshipStatus = "Spouse";
                }
            }
            if (person.getFatherID() != null) {
                if (person.getFatherID().equals(relatedPerson.getPersonID())) {
                    relationshipStatus = "Father";
                }
            }
            if (person.getMotherID() != null) {
                if (person.getMotherID().equals(relatedPerson.getPersonID())) {
                    relationshipStatus = "Mother";
                }
            }
            secondLineView.setText(relationshipStatus);

            char gender = relatedPerson.getGender();
            ImageView imageView = itemView.findViewById(R.id.iconField);
            Drawable genderIcon;

            switch (gender) {
                case 'm':
                    genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_male)
                            .colorRes(R.color.gender_blue)
                            .sizeDp(25);
                    imageView.setImageDrawable(genderIcon);
                    break;
                case 'f':
                    genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_female)
                            .colorRes(R.color.gender_pink)
                            .sizeDp(25);
                    imageView.setImageDrawable(genderIcon);
                    break;
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
                    intent.putExtra(PersonActivity.PERSON_ID_KEY, relatedPerson.getPersonID());
                    startActivity(intent);
                }
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}