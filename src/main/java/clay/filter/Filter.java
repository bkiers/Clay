package clay.filter;

/**
 * An interface that describes a filter through which each data cell will be
 * passed. A filter can be used to change the original value or to omit it
 * entirely.
 *
 * For example, if you'd like to remove the 2nd column from your input and
 * upper case the remaining values, you could do something like this:
 *
 * <pre>
 * <code>
 * Input input = new Input("a,b,c\n1,2,3")
 *     .with(new Filter() {
 *         &#64;Override
 *         public String apply(int rowIndex, int columnIndex, String value) {
 *             return columnIndex == 1 ? null : value.toUpperCase();
 *         }
 *     });
 *
 * List&lt;List&lt;String&gt;&gt; records = input.getRecords();
 *
 * assertThat(records.size(), is(2));
 *
 * assertThat(records.get(0), is(Arrays.asList("A", "C")));
 * assertThat(records.get(1), is(Arrays.asList("1", "3")));
 * </code>
 * </pre>
 */
public interface Filter {

    /**
     * Filters a cell in the input data. The cell value will be skipped if this
     * method returns {@code null}, else the value that this method returns will
     * be used.
     *
     * @param rowIndex
     *         the index of the row of the value (starting from 0).
     * @param columnIndex
     *         the index of the column of the value  (starting from 0).
     * @param value
     *         the value to be filtered. Note that this might not be the original
     *         value from the input anymore. It could have been changed by a
     *         previous {@code Filter}.
     *
     * @return {@code null} if the value should be omitted from the input, else
     * the value that will be used.
     */
    String apply(int rowIndex, int columnIndex, String value);
}
