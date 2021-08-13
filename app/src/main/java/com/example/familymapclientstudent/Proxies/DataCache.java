package com.example.familymapclientstudent.Proxies;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import Model.AuthToken;
import Model.Event;
import Model.Person;

public class DataCache {
    private static DataCache instance;
    private AuthToken authToken;

    private final Map<String, Person> peopleByID = new HashMap<>();
    private final Map<String, List<Person>> childrenByParentID = new HashMap<>();
    private final Map<String, Event> eventsByEventID = new HashMap<>();
    private final Map<String, SortedSet<Event>> eventsByPersonID = new HashMap<>();

    private final Comparator<Event> eventComparator = (o1, o2) -> {
        if (o1.getEventType().equalsIgnoreCase("birth")) {
            return -1;
        }
        if (o2.getEventType().equalsIgnoreCase("birth")) {
            return 1;
        }
        if (o1.getEventType().equalsIgnoreCase("death")) {
            return 1;
        }
        if (o2.getEventType().equalsIgnoreCase("death")) {
            return -1;
        }

        if (o1.getYear().equals(o2.getYear())) {
            return -1;
        }

        return Integer.compare(o1.getYear(), o2.getYear());
    };

    private final Set<String> eventTypes = new HashSet<>();

    //User
    private Person user;

    //Immediate Family
    private Person spouse;

    //Ancestors
    private final Set<Person> fatherSideMales = new HashSet<>();
    private final Set<Person> fatherSideFemales = new HashSet<>();
    private final Set<Person> motherSideMales = new HashSet<>();
    private final Set<Person> motherSideFemales = new HashSet<>();

    private boolean showFatherSide = true;
    private boolean showMotherSide = true;
    private boolean showMales = true;
    private boolean showFemales = true;

    private boolean showLifeStoryLines = true;
    private boolean showFamilyTreeLines = true;
    private boolean showSpouseLines = true;


    public static DataCache getInstance() {
        if (instance == null) {
            instance = new DataCache();
        }
        return instance;
    }

    private DataCache() {

    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public void addPeople(List<Person> people, String personIDofUser) {
        for (Person person : people) {
            //Find user and add to list
            if (personIDofUser.equals(person.getPersonID())) {
                this.user = person;
            }

            //Add into peopleByID list
            this.peopleByID.put(person.getPersonID(), person);

            //Add into childrenByID list
            addToChildrenByID(person);
        }

        try {
            fillFamilySets();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addToChildrenByID(Person person) {
        if (person.getFatherID() != null) {
            List<Person> childrenByFather = childrenByParentID.get(person.getFatherID());
            if (childrenByFather == null) {
                childrenByFather = new ArrayList<>();
                childrenByParentID.put(person.getFatherID(), childrenByFather);
            }
            childrenByFather.add(person);
        }

        if (person.getMotherID() != null) {
            List<Person> childrenByMother = childrenByParentID.get(person.getMotherID());
            if (childrenByMother == null) {
                childrenByMother = new ArrayList<>();
                childrenByParentID.put(person.getMotherID(), childrenByMother);
            }
            childrenByMother.add(person);
        }
    }

    private void fillFamilySets() throws Exception {
        if (peopleByID.isEmpty() || user == null) {
            throw new Exception("peopleByID and user must be filled before use of fillFamilySets");
        }

        if (user.getSpouseID() != null) {
            spouse = peopleByID.get(user.getSpouseID());
        }

        if (user.getFatherID() != null) {
            fatherSideMales.add(peopleByID.get(user.getFatherID()));
            fillSetOfAncestorsByGender(fatherSideMales, fatherSideFemales, user.getFatherID());
        }

        if (user.getMotherID() != null) {
            motherSideFemales.add(peopleByID.get(user.getMotherID()));
            fillSetOfAncestorsByGender(motherSideMales, motherSideFemales, user.getMotherID());
        }
    }

    private void fillSetOfAncestorsByGender(Set<Person> maleSet, Set<Person> femaleSet, String childPersonID) {
        if (peopleByID.get(childPersonID).getFatherID() != null) {
            String fatherID = peopleByID.get(childPersonID).getFatherID();
            maleSet.add(peopleByID.get(fatherID));
            fillSetOfAncestorsByGender(maleSet, femaleSet, fatherID);
        }

        if (peopleByID.get(childPersonID).getMotherID() != null) {
            String motherID = peopleByID.get(childPersonID).getMotherID();
            femaleSet.add(peopleByID.get(motherID));
            fillSetOfAncestorsByGender(maleSet, femaleSet, motherID);
        }
    }

    public Person getUser() {
        return user;
    }

    public Person getSpouse() {
        return spouse;
    }

    public Map<String, Person> getPeopleByID() {
        return peopleByID;
    }

    public Map<String, List<Person>> getChildrenByParentID() {
        return childrenByParentID;
    }

    public Map<String, SortedSet<Event>> getEventsByPersonID() {
        return eventsByPersonID;
    }

    public Set<Person> getFatherSideMales() {
        return fatherSideMales;
    }

    public Set<Person> getFatherSideFemales() {
        return fatherSideFemales;
    }

    public Set<Person> getMotherSideMales() {
        return motherSideMales;
    }

    public Set<Person> getMotherSideFemales() {
        return motherSideFemales;
    }

    public void addEvents(List<Event> events) {
        for (Event event : events) {
            eventsByEventID.put(event.getEventID(), event);
            eventTypes.add(event.getEventType());
            SortedSet<Event> currentEventSet = eventsByPersonID.get(event.getPersonID());
            if (currentEventSet == null) {
                currentEventSet = new TreeSet<>(eventComparator);
                eventsByPersonID.put(event.getPersonID(), currentEventSet);
            }
            currentEventSet.add(event);
        }
    }

    public boolean isShowFatherSide() {
        return showFatherSide;
    }

    public void setShowFatherSide(boolean showFatherSide) {
        this.showFatherSide = showFatherSide;
    }

    public boolean isShowMotherSide() {
        return showMotherSide;
    }

    public void setShowMotherSide(boolean showMotherSide) {
        this.showMotherSide = showMotherSide;
    }

    public boolean isShowMales() {
        return showMales;
    }

    public void setShowMales(boolean showMales) {
        this.showMales = showMales;
    }

    public boolean isShowFemales() {
        return showFemales;
    }

    public void setShowFemales(boolean showFemales) {
        this.showFemales = showFemales;
    }

    public boolean isShowLifeStoryLines() {
        return showLifeStoryLines;
    }

    public void setShowLifeStoryLines(boolean showLifeStoryLines) {
        this.showLifeStoryLines = showLifeStoryLines;
    }

    public boolean isShowFamilyTreeLines() {
        return showFamilyTreeLines;
    }

    public void setShowFamilyTreeLines(boolean showFamilyTreeLines) {
        this.showFamilyTreeLines = showFamilyTreeLines;
    }

    public boolean isShowSpouseLines() {
        return showSpouseLines;
    }

    public void setShowSpouseLines(boolean showSpouseLines) {
        this.showSpouseLines = showSpouseLines;
    }

    public Set<Event> getFilteredEvents() {
        Set<Event> eventSet = new HashSet<>();

        Set<Person> personSet = filterHelper();

        for (Person person : personSet) {
            eventSet.addAll(Objects.requireNonNull(eventsByPersonID.get(person.getPersonID())));
        }
        return eventSet;
    }

    private Set<Person> filterHelper() {
        Set<Person> personSet = new HashSet<>();
        if (showMales && user.getGender() == 'm') {
            personSet.add(user);
        }
        if (showFemales && user.getGender() == 'f') {
            personSet.add(user);
        }
        if (spouse != null) {
            if (showMales && spouse.getGender() == 'm') {
                personSet.add(spouse);
            }
            if (showFemales && spouse.getGender() == 'f') {
                personSet.add(spouse);
            }
        }
        if (showFatherSide && showMales) {
            personSet.addAll(fatherSideMales);
        }

        if (showFatherSide && showFemales) {
            personSet.addAll(fatherSideFemales);
        }

        if (showMotherSide && showMales) {
            personSet.addAll(motherSideMales);
        }

        if (showMotherSide && showFemales) {
            personSet.addAll(motherSideFemales);
        }
        return personSet;
    }

    public Set<Person> getFilteredPeople() {
        return filterHelper();
    }

    public boolean isEventShown(String eventID) {
        Set<Event> filteredEvents = this.getFilteredEvents();

        for (Event event : filteredEvents) {
            if (event.getEventID().equals(eventID)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPersonShown(String personID) {
        Set<Person> personList = this.filterHelper();
        for (Person person : personList) {
            if (person.getPersonID().equals(personID)) {
                return true;
            }
        }
        return false;
    }

    public Map<String, Event> getEventsByEventID() {
        return eventsByEventID;
    }

    public Person getPersonFromEventID(String eventID) {
        Event event = eventsByEventID.get(eventID);
        String personID = event.getPersonID();
        return peopleByID.get(personID);
    }

    public String getNameAndEventInfo(String eventID) {
        Event event = eventsByEventID.get(eventID);
        Person person = getPersonFromEventID(eventID);

        String firstName = person.getFirstName();
        String lastName = person.getLastName();

        String eventType = event.getEventType();
        String eventCity = event.getCity();
        String eventCountry = event.getCountry();
        int year = event.getYear();

        return firstName + " " + lastName + "\n" +
                        eventType.toUpperCase() + ": " + eventCity + ", " + eventCountry +
                        " (" + year + ")";
    }

    public String getEventInfo(Event event) {
        String eventType = event.getEventType();
        String eventCity = event.getCity();
        String eventCountry = event.getCountry();
        int year = event.getYear();

        return eventType.toUpperCase() + ": " + eventCity + ", " + eventCountry + " (" + year + ")";
    }


    public Set<String> getEventTypes() {
        return eventTypes;
    }

    public static boolean doesPersonMatch(Person person, String inputText) {
        inputText = inputText.toLowerCase();
        if (person.getFirstName().toLowerCase().contains(inputText) ||
            person.getLastName().toLowerCase().contains(inputText)) {
            return true;
        }

        return false;
    }

    public static boolean doesEventMatch(Event event, String inputText) {
        inputText = inputText.toLowerCase();

        String year = String.valueOf(event.getYear());
        if (event.getCity().toLowerCase().contains(inputText) ||
            event.getCountry().toLowerCase().contains(inputText) ||
            event.getEventType().toLowerCase().contains(inputText) ||
            year.toLowerCase().contains(inputText)) {
            return true;
        }

        return false;
    }

    public void clearData() {
        authToken = null;
        user = null;
        spouse = null;

        peopleByID.clear();
        childrenByParentID.clear();
        eventsByPersonID.clear();
        eventsByEventID.clear();
        eventTypes.clear();

        fatherSideMales.clear();
        fatherSideFemales.clear();
        motherSideMales.clear();
        motherSideFemales.clear();

        showLifeStoryLines = true;
        showFamilyTreeLines = true;
        showSpouseLines = true;
        showFatherSide = true;
        showMotherSide = true;
        showMales = true;
        showFemales = true;
    }
}
