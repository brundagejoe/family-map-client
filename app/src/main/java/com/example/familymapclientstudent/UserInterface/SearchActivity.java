package com.example.familymapclientstudent.UserInterface;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familymapclientstudent.Proxies.DataCache;
import com.example.familymapclientstudent.R;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.List;

import Model.Event;
import Model.Person;

public class SearchActivity extends AppCompatActivity {

    private final int PERSON_VIEW_TYPE = 0;
    private final int EVENT_VIEW_TYPE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.searchRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        SearchView searchView = findViewById(R.id.searchTextField);

        DataCache dataCache = DataCache.getInstance();

        List<Event> events = new ArrayList<>(dataCache.getFilteredEvents());
        List<Person> people = new ArrayList<>(dataCache.getFilteredPeople());

        SearchAdapter adapter = new SearchAdapter(events, people);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        adapter.getFilter().filter("");
        recyclerView.setAdapter(adapter);

    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> implements Filterable {
        private final List<Person> people;
        private final List<Event> events;

        private final List<Person> peopleFull;
        private final List<Event> eventsFull;

        public SearchAdapter(List<Event> events, List<Person> people) {
            this.people = people;
            this.events = events;

            peopleFull = new ArrayList<>(people);
            eventsFull = new ArrayList<>(events);
        }

        @Override
        public int getItemViewType(int position) {
            return position < people.size() ? PERSON_VIEW_TYPE : EVENT_VIEW_TYPE;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            view = getLayoutInflater().inflate(R.layout.list_item, parent, false);

            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            if (position < people.size()) {
                holder.bind(people.get(position));
            }
            else {
                holder.bind(events.get(position - people.size()));
            }
        }

        @Override
        public int getItemCount() {
            return people.size() + events.size();
        }

        @Override
        public Filter getFilter() {
            return exampleFilter;
        }

        private Filter exampleFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Person> filteredPeople = new ArrayList<>();
                List<Event> filteredEvents = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {

                }
                else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (Person person : peopleFull) {
                        if (DataCache.doesPersonMatch(person, filterPattern)) {
                            filteredPeople.add(person);
                        }
                    }

                    for (Event event : eventsFull) {
                        if (DataCache.doesEventMatch(event, filterPattern)) {
                            filteredEvents.add(event);
                        }
                    }
                }

                Pair<List<Person>, List<Event>> resultValues = new Pair<>(filteredPeople, filteredEvents);
                FilterResults results = new FilterResults();
                results.values = resultValues;

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                Pair<List<Person>, List<Event>> pair = (Pair<List<Person>, List<Event>>) results.values;

                people.clear();
                people.addAll(pair.first);

                events.clear();
                events.addAll(pair.second);

                notifyDataSetChanged();
            }
        };
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView firstLine;
        private final TextView secondLine;
        private final ImageView icon;

        private final int viewType;
        private Event event;
        private Person person;


        public SearchViewHolder(@NonNull View view, int viewType) {
            super(view);

            this.viewType = viewType;

            itemView.setOnClickListener(this);

            firstLine = itemView.findViewById(R.id.firstLineField);
            secondLine = itemView.findViewById(R.id.secondLineField);
            icon = itemView.findViewById(R.id.iconField);
        }

        private void bind(Person person) {
            this.person = person;
            String name = this.person.getFirstName() + " " + this.person.getLastName();
            firstLine.setText(name);
            secondLine.setText("");

            Drawable genderIcon;
            switch (this.person.getGender()) {
                case 'm':
                    genderIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_male)
                            .colorRes(R.color.gender_blue)
                            .sizeDp(25);
                    icon.setImageDrawable(genderIcon);
                    break;
                case 'f':
                    genderIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_female)
                            .colorRes(R.color.gender_pink)
                            .sizeDp(25);
                    icon.setImageDrawable(genderIcon);
                    break;
            }
        }

        private void bind(Event event) {
            this.event = event;

            DataCache dataCache = DataCache.getInstance();
            firstLine.setText(dataCache.getEventInfo(this.event));

            Person person = dataCache.getPersonFromEventID(event.getEventID());
            String name = person.getFirstName() + " " + person.getLastName();
            secondLine.setText(name);

            Drawable eventIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker)
                    .colorRes(R.color.black)
                    .sizeDp(25);
            icon.setImageDrawable(eventIcon);
        }

        @Override
        public void onClick(View v) {
            if (viewType == PERSON_VIEW_TYPE) {
                Intent intent = new Intent(SearchActivity.this, PersonActivity.class);
                intent.putExtra(PersonActivity.PERSON_ID_KEY, person.getPersonID());
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(SearchActivity.this, EventActivity.class);
                intent.putExtra(MapFragment.EVENT_ID_KEY, event.getEventID());
                intent.putExtra(MapFragment.OPTIONS_MENU_KEY, false);
                startActivity(intent);
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        //this.finish();
        return true;
    }
}