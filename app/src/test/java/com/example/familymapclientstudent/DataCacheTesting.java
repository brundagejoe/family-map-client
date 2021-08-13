package com.example.familymapclientstudent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.familymapclientstudent.Proxies.DataCache;
import com.example.familymapclientstudent.Proxies.ServerProxy;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

import Model.Event;
import Model.Person;
import requests.LoginRequest;
import results.LoginResult;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DataCacheTesting {
    //Assumes a user is already registered (from SignInTesting)
    LoginResult result;
    DataCache dataCache;

    @Before
    public void setUp() {
        dataCache = DataCache.getInstance();
        dataCache.clearData();

        ServerProxy serverProxy = new ServerProxy();
        LoginRequest r = new LoginRequest();
        r.setUsername("test");
        r.setPassword("password");

        result = serverProxy.login("localHost", 8080, r);
    }


    @Test
    public void datacacheAddition() {
        System.out.println("People size: " + dataCache.getPeopleByID().size());
        System.out.println("Event size: " + dataCache.getEventsByPersonID().size());
        assertEquals(31, dataCache.getPeopleByID().size());
        assertEquals(31, dataCache.getEventsByPersonID().size());
    }

    @Test
    public void correctUsernameTest() {
        Person user = dataCache.getUser();

        assertNotNull(user);
        assertEquals("test", user.getFirstName());
        assertEquals("person", user.getLastName());
    }

    @Test
    public void personByIDTest() {
        assertFalse(dataCache.getPeopleByID().size() == 0);
        for (String key : dataCache.getPeopleByID().keySet()) {
            assertEquals(key, dataCache.getPeopleByID().get(key).getPersonID());
        }
    }

    @Test
    public void childrenByIDTest() {
        assertNotNull(dataCache.getChildrenByParentID());
        assertNotEquals(0, dataCache.getChildrenByParentID().size());
        assertEquals(30, dataCache.getChildrenByParentID().size());//31 in system, user has no children
    }

    @Test
    public void eventsByIDTest() {
        assertNotEquals(0, dataCache.getEventsByPersonID().size());
        for (String id : dataCache.getEventsByPersonID().keySet()) {
            if (!id.equals(dataCache.getUser().getPersonID())) {
                SortedSet<Event> currentSet = dataCache.getEventsByPersonID().get(id);
                assertNotNull(currentSet);
                assertEquals(3, currentSet.size());
                assertEquals("birth", currentSet.first().getEventType().toLowerCase());
                assertEquals("death", currentSet.last().getEventType().toLowerCase());
            }
        }
    }

    @Test
    public void eventTypesTest() {
        Set<String> testSet = new HashSet<>();
        testSet.add("birth");
        testSet.add("marriage");
        testSet.add("death");

        assertEquals(testSet, dataCache.getEventTypes());
    }

    @Test
    public void familyFillTest() {
        assertNotNull(dataCache.getFatherSideMales());
        assertNotNull(dataCache.getFatherSideFemales());
        assertNotNull(dataCache.getMotherSideMales());
        assertNotNull(dataCache.getMotherSideFemales());

        assertEquals(8, dataCache.getFatherSideMales().size());
        assertEquals(7, dataCache.getFatherSideFemales().size());
        assertEquals(7, dataCache.getMotherSideMales().size());
        assertEquals(8, dataCache.getMotherSideFemales().size());

        assertEquals(30, dataCache.getFatherSideMales().size() +
                                        dataCache.getFatherSideFemales().size() +
                                        dataCache.getMotherSideMales().size() +
                                        dataCache.getMotherSideFemales().size());

        int fatherLastNameCount = 0;
        for (Person person : dataCache.getFatherSideMales()) {
            if (person.getLastName().equalsIgnoreCase("person")) {
                ++fatherLastNameCount;
            }
            assertEquals('m', (char) person.getGender());
        }
        assertEquals(4, fatherLastNameCount);

        for (Person person : dataCache.getFatherSideFemales()) {
            assertEquals('f', (char) person.getGender());
        }

        for (Person person : dataCache.getMotherSideMales()) {
            assertEquals('m', (char) person.getGender());
        }

        for (Person person : dataCache.getMotherSideFemales()) {
            assertEquals('f', (char) person.getGender());
        }
    }

    @Test
    public void eventsByEventIDTest() {
        assertNotNull(dataCache.getEventsByEventID());
        assertEquals(91, dataCache.getEventsByEventID().keySet().size());
    }

    @Test
    public void searchTestPerson() {
        Person person = new Person();
        person.setFirstName("test");
        person.setLastName("password");
        assertTrue(DataCache.doesPersonMatch(person, "test"));
        assertTrue(DataCache.doesPersonMatch(person, "Test"));
        assertTrue(DataCache.doesPersonMatch(person, "Te"));
        assertTrue(DataCache.doesPersonMatch(person, "password"));
        assertTrue(DataCache.doesPersonMatch(person, "Password"));
        assertTrue(DataCache.doesPersonMatch(person, "PASS"));
        assertTrue(DataCache.doesPersonMatch(person, "WORD"));

        assertFalse(DataCache.doesPersonMatch(person, "abc"));
        assertFalse(DataCache.doesPersonMatch(person, "q"));
        assertFalse(DataCache.doesPersonMatch(person, "Testing"));
        assertFalse(DataCache.doesPersonMatch(person, "passwword"));
    }

    @Test
    public void searchTestEvent() {
        Event event = new Event();
        event.setCity("Provo");
        event.setCountry("United States of America");
        event.setEventType("Birth");
        event.setYear(2000);

        assertTrue(DataCache.doesEventMatch(event, "Provo"));
        assertTrue(DataCache.doesEventMatch(event, "United States of America"));
        assertTrue(DataCache.doesEventMatch(event, "Birth"));
        assertTrue(DataCache.doesEventMatch(event, "2000"));
        assertTrue(DataCache.doesEventMatch(event, "provo"));
        assertTrue(DataCache.doesEventMatch(event, "united states"));
        assertTrue(DataCache.doesEventMatch(event, "2"));
        assertTrue(DataCache.doesEventMatch(event, "rth"));

        assertFalse(DataCache.doesEventMatch(event, "Provo, Utah"));
        assertFalse(DataCache.doesEventMatch(event, "3"));
        assertFalse(DataCache.doesEventMatch(event, "2001"));
        assertFalse(DataCache.doesEventMatch(event, "USA"));

    }
}