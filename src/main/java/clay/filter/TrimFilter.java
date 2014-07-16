package clay.filter;

/**
 * A filter that trims all leading and trailing spaces from a cell value.
 */
public class TrimFilter implements Filter {

    /**
     * {@inheritDoc}
     */
    @Override
    public String apply(int rowIndex, int columnIndex, String value) {
        return value.trim();
    }
}
