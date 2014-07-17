package clay.filter;

import clay.input.CSVInput;
import clay.input.StringInput;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FilterTest {

    @Test
    public void with_MultipleFilters_LastFilterShouldApply() {

        CSVInput input = new StringInput("a        , B , c")
                .with(new TrimFilter())
                .with(new Filter() {
                    @Override
                    public String apply(int rowIndex, int columnIndex, String value) {
                        // Simply return the value twice, and upper-cased.
                        return (value + value).toUpperCase();
                    }
                })
                .with(new Filter() {
                    @Override
                    public String apply(int rowIndex, int columnIndex, String value) {
                        // Simply return the value twice, and upper-cased.
                        return value.toLowerCase();
                    }
                });

        List<List<String>> records = input.getRecords();

        assertThat(records.size(), is(1));

        assertThat(records.get(0), is(Arrays.asList("aa", "bb", "cc")));
    }

    @Test
    public void with_CustomFilterSkipColumn_ShouldSkipColumn() {

        CSVInput input = new StringInput("a,b,c\n1,2,3")
                .with(new Filter() {
                    @Override
                    public String apply(int rowIndex, int columnIndex, String value) {
                        // Ignore the second column (index 1).
                        return columnIndex == 1 ? null : value.toUpperCase();
                    }
                });

        List<List<String>> records = input.getRecords();

        assertThat(records.size(), is(2));

        assertThat(records.get(0), is(Arrays.asList("A", "C")));
        assertThat(records.get(1), is(Arrays.asList("1", "3")));
    }
}
