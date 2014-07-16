package clay.filter;

import clay.input.Input;
import clay.input.StringInput;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ExcludeColumnRangeFilterTest {

    @Test(expected = IllegalArgumentException.class)
    public void newExcludeColumnRangeFilter_InvalidFromParam_ShouldThrowException() {
        new ExcludeColumnRangeFilter(-1, 7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newExcludeColumnRangeFilter_InvalidToParam_ShouldThrowException() {
        new ExcludeColumnRangeFilter(1, -7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newExcludeColumnRangeFilter_InvalidParams_ShouldThrowException() {
        new ExcludeColumnRangeFilter(4, 4);
    }

    @Test
    public void with_ExcludeColumnRangeFilter_ShouldReturnExpectedValues() {

        // Test 1
        Input input = new StringInput("11,12,13\n" + // 0
                "21,22,23\n" + // 1
                "31,32,33\n" + // 2
                "41,42,43\n" + // 3
                "51,52,53\n" + // 4
                "61,62,63")    // 5
                .with(new ExcludeColumnRangeFilter(1, 2));

        List<List<String>> records = input.getRecords();
        assertThat(records.size(), is(6));
        assertThat(records.get(0), is(Arrays.asList("11", "13")));
        assertThat(records.get(5), is(Arrays.asList("61", "63")));

        // Test 2
        input = new StringInput("11,12,13\n" + // 0
                "21,22,23\n" + // 1
                "31,32,33\n" + // 2
                "41,42,43\n" + // 3
                "51,52,53\n" + // 4
                "61,62,63")    // 5
                .with(new ExcludeColumnRangeFilter(1, 42));

        records = input.getRecords();
        assertThat(records.size(), is(6));
        assertThat(records.get(0), is(Arrays.asList("11")));
        assertThat(records.get(5), is(Arrays.asList("61")));

        // Test 3
        input = new StringInput("11,12,13\n" + // 0
                "21,22,23\n" + // 1
                "31,32,33\n" + // 2
                "41,42,43\n" + // 3
                "51,52,53\n" + // 4
                "61,62,63")    // 5
                .with(new ExcludeColumnRangeFilter(0, 42));

        records = input.getRecords();
        assertThat(records.size(), is(0));
    }
}
