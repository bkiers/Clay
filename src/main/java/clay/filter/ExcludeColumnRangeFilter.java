package clay.filter;

/**
 * A filter that will exclude certain range of columns from the CSV input.
 */
public class ExcludeColumnRangeFilter implements Filter {

    // The index from which to exclude columns.
    private final int from;

    // The index from which to include columns again.
    private final int to;

    /**
     * Creates a new instance of this filter.
     *
     * @param from
     *         the index from which to exclude columns.
     * @param to
     *         the index from which to include columns again.
     */
    public ExcludeColumnRangeFilter(int from, int to) {

        if (from < 0) {
            throw new IllegalArgumentException("from < 0");
        }

        if (to < 0) {
            throw new IllegalArgumentException("to < 0");
        }

        if (from >= to) {
            throw new IllegalArgumentException("from >= to");
        }

        this.from = from;
        this.to = to;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String apply(int rowIndex, int columnIndex, String value) {
        return (columnIndex >= from && columnIndex < to) ? null : value;
    }
}
