package clay.filter;

import clay.input.Input;
import clay.input.StringInput;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ExcludeRowFilterTest {

    @Test
    public void with_ExcludeRowFilter_ShouldReturnExpectedValues() {

        // Test 1
        Input input = new StringInput("11,12,13\n" + // 0
                "21,22,23\n" + // 1
                "31,32,33\n" + // 2
                "41,42,43\n" + // 3
                "51,52,53\n" + // 4
                "61,62,63")    // 5
                .with(new ExcludeRowFilter(1, 2));

        List<List<String>> records = input.getRecords();
        assertThat(records.size(), is(4));
        assertThat(records.get(0), is(Arrays.asList("11", "12", "13")));
        assertThat(records.get(1), is(Arrays.asList("41", "42", "43")));

        // Test 2
        input = new StringInput("11,12,13\n" + // 0
                "21,22,23\n" + // 1
                "31,32,33\n" + // 2
                "41,42,43\n" + // 3
                "51,52,53\n" + // 4
                "61,62,63")    // 5
                .with(new ExcludeRowFilter(-1, -333, 0, 1, 2, 3, 4, 5, 6, 7, 8));

        records = input.getRecords();
        assertThat(records.size(), is(0));
    }
}
