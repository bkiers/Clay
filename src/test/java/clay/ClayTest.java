package clay;

import clay.input.FileInput;
import clay.input.CSVInput;
import clay.input.StringInput;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class ClayTest {

    static class Address {

        String street;
        String number;
        String zipcode;
        String country;
        String city;
        Location location;
    }

    static class Location {

        double latitude;
        double longitude;
    }

    static class A { B b; }
    static class B { C c; }
    static class C { D d; }
    static class D { E e; }
    static class E { String x; }

    static class SimplePerson {
        String name;
        int age;
    }

    static class Person {

        String name;
        SimpleAddress address;
    }

    static class SimpleAddress {

        String street;
        String zipcode;
        String city;
    }

    @Test
    public void as_NonNullsFile_ShouldAllMapToNonNulls() throws Exception {

        CSVInput input = new FileInput(new File("src/test/resources/addresses.csv"), "|");
        Clay clay = new Clay(input);

        List<Person> people = clay.as(Person.class);

        // 101 records of which the 1st is the header
        assertThat(people.size(), is(100));

        for (Person p : people) {
            assertThat(p.name, is(not(nullValue())));
            assertThat(p.address, is(not(nullValue())));
            assertThat(p.address.street, is(not(nullValue())));
            assertThat(p.address.zipcode, is(not(nullValue())));
            assertThat(p.address.city, is(not(nullValue())));
        }
    }

    @Test
    public void testWithAndWithoutHeaders() {

        // With headers in the input
        CSVInput input = new StringInput("street,number,zipcode,country,location.latitude,location.longitude,city\n" +
                "Kruiskade,1,2394CM,\"The Netherlands\",51.923626,4.477680,Rotterdam");
        Clay clay = new Clay(input);
        List<Address> addresses = clay.as(Address.class);

        assertThat(addresses.size(), is(1));

        Address address = addresses.get(0);

        assertThat(address.street, is("Kruiskade"));
        assertThat(address.number, is("1"));
        assertThat(address.zipcode, is("2394CM"));
        assertThat(address.country, is("The Netherlands"));
        assertThat(address.location.latitude, is(51.923626));
        assertThat(address.location.longitude, is(4.477680));
        assertThat(address.city, is("Rotterdam"));

        // Without headers in the input
        input = new StringInput("Kruiskade,1,2394CM,\"The Netherlands\",51.923626,4.477680,Rotterdam");
        clay = new Clay(input, "street", "number", "zipcode", "country", "location.latitude", "location.longitude", "city");
        addresses = clay.as(Address.class);

        assertThat(addresses.size(), is(1));

        address = addresses.get(0);

        assertThat(address.street, is("Kruiskade"));
        assertThat(address.number, is("1"));
        assertThat(address.zipcode, is("2394CM"));
        assertThat(address.country, is("The Netherlands"));
        assertThat(address.location.latitude, is(51.923626));
        assertThat(address.location.longitude, is(4.477680));
        assertThat(address.city, is("Rotterdam"));
    }

    @Test
    public void deeplyNestedFieldsTest() {

        CSVInput input = new StringInput("b.c.d.e.x\nMU");
        Clay clay = new Clay(input);
        List<A> as = clay.as(A.class);

        assertThat(as.size(), is(1));

        A a = as.get(0);

        assertThat(a.b.c.d.e.x, is("MU"));
    }

    @Test
    public void newClay_ListHeaderValues_ShouldPass() {

        List<String> header = Arrays.asList("name", "age");
        Clay clay = new Clay(new StringInput("John Doe,42"), header);

        List<SimplePerson> people = clay.as(SimplePerson.class);

        assertThat(people.size(), is(1));
        assertThat(people.get(0).name, is("John Doe"));
        assertThat(people.get(0).age, is(42));
    }

    @Test
    public void newClay_VarargsHeaderValues_ShouldPass() {

        Clay clay = new Clay(new StringInput("John Doe,42"), "name", "age");

        List<SimplePerson> people = clay.as(SimplePerson.class);

        assertThat(people.size(), is(1));
        assertThat(people.get(0).name, is("John Doe"));
        assertThat(people.get(0).age, is(42));
    }

    @Test(expected = IllegalArgumentException.class)
    public void newClay_NoRecords_ShouldThrowException() {
        new Clay(new StringInput(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void newClay_OnlyHeaderNoRecord1_ShouldThrowException() {
        new Clay(new StringInput("name,age"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void newClay_OnlyHeaderNoRecord2_ShouldThrowException() {
        new Clay(new StringInput("John Doe,42"), new ArrayList<String>());
    }
}

