package clay;

import clay.input.CSVInput;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.*;

/**
 * The class responsible for the mapping of CSV records to Java classes.
 *
 * To create a list of {@code User}s from the CSV input:
 *
 * <pre>
 * <code>
 * name,age
 * John,42
 * Sara,28
 * </code>
 * </pre>
 *
 * where the Java class simply looks like this:
 *
 * <pre>
 * <code>
 * public class User {
 *     String name;
 *     int age;
 * }
 * </code>
 * </pre>
 *
 * do the following:
 *
 * <pre>
 * <code>
 * Clay clay = new Clay(new StringInput("name,age\n" +
 *                                      "John,42\n" +
 *                                      "Sara,28"));
 *
 * for (User user : clay.as(User.class)) {
 *   System.out.println(user.name + " " + user.age);
 * }
 *
 * // John 42
 * // Sara 28
 * </code>
 * </pre>
 */
public class Clay {

    // A Gson instance to map CSV data to Java classes.
    private static Gson GSON = new Gson();

    // The records of the input.
    private final List<List<String>> records;

    // The headers that map the header names to instance variables of Java classes.
    private final List<String> headers;

    /**
     * Creates a new {@code Clay} instance where the first record of the {@code input}
     * will be considered the header.
     *
     * @param input
     *         the input from which to get the records.
     *
     * @throws IllegalArgumentException
     *         when the input contains less than 2 records, or the header (the first
     *         record) is empty.
     */
    public Clay(CSVInput input) throws IllegalArgumentException {

        this.records = input.getRecords();

        if (this.records.isEmpty()) {
            throw new IllegalArgumentException("input must contain at least 2 records " +
                    "(1 header record, 1 other record)");
        }

        this.headers = this.records.remove(0);
        check();
    }

    /**
     * Creates a new {@code Clay} instance from the given input and headers.
     *
     * @param input
     *         the input from which to get the records.
     * @param headers
     *         the headers that map the header names to instance variables of Java classes.
     *
     * @throws IllegalArgumentException
     *         when the input contains no records, or the header is empty.
     */
    public Clay(CSVInput input, String... headers) {
        this(input, Arrays.asList(headers));
    }

    /**
     * Creates a new {@code Clay} instance from the given input and headers.
     *
     * @param input
     *         the input from which to get the records.
     * @param headers
     *         the headers that map the header names to instance variables of Java classes.
     *
     * @throws IllegalArgumentException
     *         when the input contains no records, or the header is empty.
     */
    public Clay(CSVInput input, List<String> headers) {
        this.records = input.getRecords();
        this.headers = new ArrayList<String>(headers);
        check();
    }

    // Checks if there is at least a header and 1 record. If not, an exception is thrown.
    private void check() {

        if (this.records.isEmpty()) {
            throw new IllegalArgumentException("input must contain at least 1 record");
        }

        if (this.headers.isEmpty()) {
            throw new IllegalArgumentException("input must contain at least 1 header value");
        }
    }

    /**
     * Returns a list of values of a specific {@code type} from all records of
     * the {@code input}.
     *
     * @param type
     *         the type to convert each record from the  {@code input} to.
     * @param <T>
     *         the generic type of the Java class the input should be converted to.
     *
     * @return a list of values of a specific {@code type} from all records of
     * the {@code input}.
     *
     * @throws JsonSyntaxException
     *         when one of the records could not be converted to the provided
     *         {@code type}.
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> as(Class<T> type) throws JsonSyntaxException {

        List<T> values = new ArrayList<T>();

        // Iterate over all records.
        for (List<String> record : this.records) {

            // Create a map that will eventually be converted in a JSON string, which in its turn
            // will be mapped to the type provided as a parameter.
            Map<String, Object> map = new HashMap<String, Object>();

            // Now map each header key to a value.
            for (int i = 0; i < this.headers.size(); i++) {

                // Make sure the header (name of the variable) does not contain spaces.
                String key = this.headers.get(i).replaceAll("\\s", "");

                // Get the value to map to the header (variable).
                String value = i < record.size() ? record.get(i) : null;

                // The presence of a '.' means a nested object.
                if (key.contains(".")) {

                    Map<String, Object> pointer = map;
                    String[] tokens = key.split("\\.");

                    for (int j = 0; j < tokens.length - 1; j++) {

                        // Assume the nested object is already created.
                        Map<String, Object> existing = (Map<String, Object>) pointer.remove(tokens[j]);

                        if (existing == null) {
                            // If not created, do so now.
                            existing = new HashMap<String, Object>();
                        }

                        pointer.put(tokens[j], existing);
                        pointer = existing;
                    }

                    pointer.put(tokens[tokens.length - 1], value);
                }
                else {
                    map.put(key, value);
                }
            }

            String json = GSON.toJson(map);
            values.add(GSON.fromJson(json, type));
        }

        return values;
    }
}
