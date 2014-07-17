package clay.filter;

import clay.input.CSVInput;
import clay.input.StringInput;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ExcludeColumnFilterTest {

    @Test
    public void with_ExcludeColumnFilter_ShouldReturnExpectedValues() {

        // Test 1
        CSVInput input = new StringInput("11,12,13\n" + // 0
                "21,22,23\n" + // 1
                "31,32,33\n" + // 2
                "41,42,43\n" + // 3
                "51,52,53\n" + // 4
                "61,62,63")    // 5
                .with(new ExcludeColumnFilter(1, 2));

        List<List<String>> records = input.getRecords();
        assertThat(records.size(), is(6));
        assertThat(records.get(0), is(Arrays.asList("11")));
        assertThat(records.get(5), is(Arrays.asList("61")));

        // Test 2
        input = new StringInput("11,12,13\n" + // 0
                "21,22,23\n" + // 1
                "31,32,33\n" + // 2
                "41,42,43\n" + // 3
                "51,52,53\n" + // 4
                "61,62,63")    // 5
                .with(new ExcludeColumnFilter(1, 42));

        records = input.getRecords();
        assertThat(records.size(), is(6));
        assertThat(records.get(0), is(Arrays.asList("11", "13")));
        assertThat(records.get(5), is(Arrays.asList("61", "63")));

        // Test 3
        input = new StringInput("11,12,13\n" + // 0
                "21,22,23\n" + // 1
                "31,32,33\n" + // 2
                "41,42,43\n" + // 3
                "51,52,53\n" + // 4
                "61,62,63")    // 5
                .with(new ExcludeColumnFilter(0, 1, 2, 3, 4));

        records = input.getRecords();
        assertThat(records.size(), is(0));
    }
}
