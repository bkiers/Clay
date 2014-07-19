# Clay &nbsp; [![Build Status](https://travis-ci.org/bkiers/Clay.png)](https://travis-ci.org/bkiers/Clay)

A small library to parse CSV files and optionally map the records
from the CSV file to a Java class.
 
API docs: [bkiers.github.io/Clay/site/apidocs](http://bkiers.github.io/Clay/site/apidocs)

Code coverage report: [bkiers.github.io/Clay/site/cc](http://bkiers.github.io/Clay/site/cc)

# Install

To install/build, do the following:

    git clone https://github.com/bkiers/Clay.git
    cd Clay
    mvn clean install

The JAR file will then be available in your local Maven repository. To use
it, add the following dependency to your POM:

```xml
<dependency>
    <groupId>nl.big-o</groupId>
    <artifactId>clay</artifactId>
    <version>0.1.0</version>
</dependency>
```

After `mvn clean install`, the JAR file can also be found in the `target/`
directory.

# Examples

Below are some use-cases accompanied by example code, which is available 
in `src/test/clay/example/Examples.java`.

1. Basic usage
2. No headers
3. Nested objects
4. Filtering input
5. Removing a filter
6. Custom meta chars
7. Wrong headers

## Java classes

The examples below use the following Java classes (methods are excluded for
brevity):

```java
class User {

    String name;
    int age;
}

class City {

    String name;
    Location location;
}

class Location {

    double lat;
    double lon;
}
```

## 1. Basic usage

Below is some basic usage on how to retrieve the raw 2D data from the `input`
and how to map this input to a `User` class using `Clay`:

```java
Input input = new StringInput("name,age\n\"John,\"\"A.\",24\nSara,31\nPete,18");
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
```

## 2. No headers

When the input does not contain any headers, you can include it when creating 
a `Clay` instance:

```java
Input input = new StringInput("John,24\nSara,31\nPete,18");
Clay clay = new Clay(input, "name", "age");
List<User> users = clay.as(User.class);

assertThat(users.size(), is(3));
assertThat(users.get(1).getName(), is("Sara"));
assertThat(users.get(2).getName(), is("Pete"));
```

## 3. Nested objects

When the input contains nested objects, like the `Location` inside a `City`, you 
can map the values from the CSV file to the Java object by separating the header
by dots:

```java
String csv = "name, location.lat, location.lon\n" +
             "New York, 40.711356, -74.010962\n" +
             "Rotterdam, 51.930724, 4.481480\n" +
             "Moscow, 55.760611, 37.618762";

Input input = new StringInput(csv);
Clay clay = new Clay(input);
List<City> cities = clay.as(City.class);

assertThat(cities.size(), is(3));
assertThat(cities.get(0).getLocation().getLon(), is(-74.010962));
assertThat(cities.get(1).getLocation().getLat(), is(51.930724));
assertThat(cities.get(2).getName(), is("Moscow"));
```

## 4. Filtering input

Let's say the input from the previous example contains some noise in the first 
column (but not in the first column of the header!) and you'd like to exclude it.
 
You can do this by attaching a `Filter` to the input. A `Filter` will be applied 
to all cells in the input. When you want to exclude a particular cell, you 
simply return `null`. If you want the cell to be unchanged, return the `value` 
itself. And if you want to upper case it for example, return `value.toUpperCase()`.  

```java
String csv = "name, location.lat, location.lon\n" +
             "NOISE, New York, 40.711356, -74.010962\n" +
             "NOISE, Rotterdam, 51.930724, 4.481480\n" +
             "NOISE, Moscow, 55.760611, 37.618762";

Input input = new StringInput(csv).with(new Filter(){
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
```

You can add multiple filters to the input. It is important to note that once a filter 
has returned `null` for a particular `(row,column)` cell, the remaining filters will *not* 
be applied to it. Filters are applied in the order they have been added. This mean that 
when you first add a filter that changes values in upper case and then add a filter 
that changes them to lower case, the end result will be lower cased values.

## 5. Removing a filter

You might have noticed that the cells are automatically trimmed. This is because all the
provided `Input` instances automatically get a `clay.filter.TrimFilter` added to them.

To remove a filter, do the following:

```java
String csvWithTrailingSpaces = "John ,24\nSara ,31\nPete ,18";

// Default: with the TrimFilter
Input input = new StringInput(csvWithTrailingSpaces);
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
```

## 6. Custom meta chars

The following meta characters are customizable:

* cell delimiter (default `,`)
* quotation char (default `"`)
* escape quotation char (default `"`)
* end of record (default `\n`)

The meta characters can be strings, they need not be single characters.

```java
Input input1 = new StringInput("\"John, A.\",24\nSara,31\nPete,18");

// delimiter        -> |
// quotation        -> #
// escape quotation -> \
// end of record    -> ⅀⅀⅀
Input input2 = new StringInput("#John, A.#|24⅀⅀⅀Sara|31⅀⅀⅀Pete|18",
        "|", "#", "\\", "⅀⅀⅀");

assertThat(input1.getRecords(), is(input2.getRecords()));
```

## 7. Wrong headers

When the input contains headers that cannot be mapped to the Java class' instance
variables, you could remove this header using a `Filter`, and add the correct 
headers manually:

```java
String csv = "NAME, LAT, LON\n" +
             "New York, 40.711356, -74.010962\n" +
             "Rotterdam, 51.930724, 4.481480\n" +
             "Moscow, 55.760611, 37.618762";

Input input = new StringInput(csv).with(new Filter(){
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
```

In the package `clay.filter`, there are some filters provided. One of which
could replace the custom filter used above:

```java
// Exclude row with index 0
Input input = new StringInput(csv).with(new ExcludeRowFilter(0));
```

Excluding the first 5 rows would go like this:

```java
// Exclude indexes 0, 1, 2, 3 and 4.
Input input = new StringInput(csv).with(new ExcludeRowFilter(0, 1, 2, 3, 4));
```

or the equivalent:    

```java
// Exclude indexes 0, 1, 2, 3 and 4.
Input input = new StringInput(csv).with(new ExcludeRowRangeFilter(0, 5));
```

# Credits

* [Gson](https://code.google.com/p/google-gson): for the mapping of CSV data to Java classes

# License

* Clay: [MIT](http://opensource.org/licenses/MIT)
* Gson: [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)
