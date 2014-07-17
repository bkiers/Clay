package clay.example;

import clay.Clay;
import clay.filter.Filter;
import clay.filter.TrimFilter;
import clay.input.CSVInput;
import clay.input.StringInput;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class Examples {

    @Test
    public void example1() {

        CSVInput input = new StringInput("name,age\n\"John,\"\"A.\",24\nSara,31\nPete,18");
        List<List<String>> data = input.getRecords();

        // The input has 4 records (1 header and 3 records).
        assertThat(data.size(), is(4));
        assertThat(data.get(2), is(Arrays.asList("Sara", "31")));

        Clay clay = new Clay(input);
        List<User> users = clay.as(User.class);

        // The header is omitted when mapping to a Java class.
        assertThat(users.size(), is(3));
        assertThat(users.get(0).getName(), is("John,\"A."));
        assertThat(users.get(2).getAge(), is(18));
    }

    @Test
    public void example2() {

        CSVInput input = new StringInput("John,24\nSara,31\nPete,18");
        Clay clay = new Clay(input, "name", "age");
        List<User> users = clay.as(User.class);

        assertThat(users.size(), is(3));
        assertThat(users.get(1).getName(), is("Sara"));
        assertThat(users.get(2).getName(), is("Pete"));
    }

    @Test
    public void example3() {

        String csv = "name, location.lat, location.lon\n" +
                "New York, 40.711356, -74.010962\n" +
                "Rotterdam, 51.930724, 4.481480\n" +
                "Moscow, 55.760611, 37.618762";

        CSVInput input = new StringInput(csv);
        Clay clay = new Clay(input);
        List<City> cities = clay.as(City.class);

        assertThat(cities.size(), is(3));
        assertThat(cities.get(0).getLocation().getLon(), is(-74.010962));
        assertThat(cities.get(1).getLocation().getLat(), is(51.930724));
        assertThat(cities.get(2).getName(), is("Moscow"));
    }

    @Test
    public void example4() {

        String csv = "name, location.lat, location.lon\n" +
                "NOISE, New York, 40.711356, -74.010962\n" +
                "NOISE, Rotterdam, 51.930724, 4.481480\n" +
                "NOISE, Moscow, 55.760611, 37.618762";

        CSVInput input = new StringInput(csv).with(new Filter(){
            @Override
            public String apply(int rowIndex, int columnIndex, String value) {

                if (rowIndex == 0) {
                    // We want to keep the header!
                    return value;
                }

                // If the column index is 0, remove it from the input by returning `null`.
                return columnIndex == 0 ? null : value;
            }
        });

        Clay clay = new Clay(input);
        List<City> cities = clay.as(City.class);

        assertThat(cities.size(), is(3));
        assertThat(cities.get(0).getLocation().getLon(), is(-74.010962));
        assertThat(cities.get(1).getLocation().getLat(), is(51.930724));
        assertThat(cities.get(2).getName(), is("Moscow"));
    }

    @Test
    public void example5() {

        String csvWithTrailingSpaces = "John ,24\nSara ,31\nPete ,18";

        // Default: with the TrimFilter
        CSVInput input = new StringInput(csvWithTrailingSpaces);
        Clay clay = new Clay(input, "name", "age");
        List<User> users = clay.as(User.class);

        assertThat(users.size(), is(3));
        assertThat(users.get(1).getName(), is("Sara"));
        assertThat(users.get(2).getName(), is("Pete"));

        // Remove the TrimFilter
        input = new StringInput(csvWithTrailingSpaces).without(TrimFilter.class);
        clay = new Clay(input, "name", "age");
        users = clay.as(User.class);

        assertThat(users.size(), is(3));
        assertThat(users.get(1).getName(), is("Sara "));
        assertThat(users.get(2).getName(), is("Pete "));
    }

    @Test
    public void example6() {

        CSVInput input1 = new StringInput("\"John, A.\",24\nSara,31\nPete,18");

        // delimiter        -> |
        // quotation        -> #
        // escape quotation -> \
        // end of record    -> ⅀⅀⅀
        CSVInput input2 = new StringInput("#John, A.#|24⅀⅀⅀Sara|31⅀⅀⅀Pete|18",
                "|", "#", "\\", "⅀⅀⅀");

        assertThat(input1.getRecords(), is(input2.getRecords()));
    }

    @Test
    public void example7() {

        String csv = "NAME, LAT, LON\n" +
                "New York, 40.711356, -74.010962\n" +
                "Rotterdam, 51.930724, 4.481480\n" +
                "Moscow, 55.760611, 37.618762";

        CSVInput input = new StringInput(csv).with(new Filter(){
            @Override
            public String apply(int rowIndex, int columnIndex, String value) {

                if (rowIndex == 0) {
                    // Remove all cells from the first row.
                    return null;
                }

                // Leave all other values as they were.
                return value;
            }
        });

        Clay clay = new Clay(input, "name", "location.lat", "location.lon");
        List<City> cities = clay.as(City.class);

        assertThat(cities.size(), is(3));
        assertThat(cities.get(0).getLocation().getLon(), is(-74.010962));
        assertThat(cities.get(1).getLocation().getLat(), is(51.930724));
        assertThat(cities.get(2).getName(), is("Moscow"));
    }

    static class User {

        String name;
        int age;

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    static class City {

        String name;
        Location location;

        public String getName() {
            return name;
        }

        public Location getLocation() {
            return location;
        }

        @Override
        public String toString() {
            return "City{" +
                    "name='" + name + '\'' +
                    ", location=" + location +
                    '}';
        }
    }

    static class Location {

        double lat;
        double lon;

        public double getLat() {
            return lat;
        }

        public double getLon() {
            return lon;
        }

        @Override
        public String toString() {
            return "(" +
                    "lat=" + lat +
                    ", lon=" + lon +
                    ')';
        }
    }
}
