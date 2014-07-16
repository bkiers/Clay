package clay.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A filter that will exclude certain columns from the CSV input.
 */
public class ExcludeColumnFilter implements Filter {

    // All row indexes whose columns should not be included.
    private final Set<Integer> indexSet;

    /**
     * Creates a new instance of this filter.
     *
     * @param indexes
     *         all column indexes whose columns should not be included.
     */
    public ExcludeColumnFilter(Integer... indexes) {
        this.indexSet = new HashSet<Integer>(Arrays.asList(indexes));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String apply(int rowIndex, int columnIndex, String value) {
        return indexSet.contains(columnIndex) ? null : value;
    }
}
