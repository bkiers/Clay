package clay.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A filter that will exclude certain rows from the CSV input.
 */
public class ExcludeRowFilter implements Filter {

    // All row indexes whose rows should not be included.
    private final Set<Integer> indexSet;

    /**
     * Creates a new instance of this filter.
     *
     * @param indexes
     *         all row indexes whose rows should not be included.
     */
    public ExcludeRowFilter(Integer... indexes) {
        this.indexSet = new HashSet<Integer>(Arrays.asList(indexes));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String apply(int rowIndex, int columnIndex, String value) {
        return indexSet.contains(rowIndex) ? null : value;
    }
}
