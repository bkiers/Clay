package clay.filter;

import clay.input.CSVInput;
import clay.input.StringInput;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TrimFilterTest {

    @Test
    public void with_TrimFilter_ShouldTrimCells() {

        CSVInput input = new StringInput(" a , b , c \n 1, \" 2 \" ,3    ").with(new TrimFilter());

        List<List<String>> records = input.getRecords();

        assertThat(records.size(), is(2));

        assertThat(records.get(0), is(Arrays.asList("a", "b", "c")));
        assertThat(records.get(1), is(Arrays.asList("1", "2", "3")));
    }

    @Test
    public void without_TrimFilter_ShouldNotTrimCells() {

        CSVInput input = new StringInput(" a , b , c \n 1 , 2 , 3 ")
                .without(TrimFilter.class);

        List<List<String>> records = input.getRecords();

        assertThat(records.size(), is(2));

        assertThat(records.get(0), is(Arrays.asList(" a ", " b ", " c " )));
        assertThat(records.get(1), is(Arrays.asList(" 1 ", " 2 ", " 3 " )));
    }
}
