package clay.input;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StringInputTest {

    @Test
    public void quotationTest() {

        CSVInput input = new StringInput("\"a b\",12,34\n" +
                "01,\"a\"\"b\" ,, 12,34");

        assertThat(input.getRecords().size(), is(2));

        assertThat(input.getRecords().get(0), is(Arrays.asList("a b", "12", "34")));
        assertThat(input.getRecords().get(1), is(Arrays.asList("01", "a\"b", "", "12", "34")));
    }

    @Test
    public void delimiterTest() {

        CSVInput input = new StringInput("1\t2\t3", "\t");
        assertThat(input.getRecords().size(), is(1));
        assertThat(input.getRecords().get(0), is(Arrays.asList("1", "2", "3")));

        input = new StringInput("1>>>>>>2>>>>>>3", ">>>>>>");
        assertThat(input.getRecords().size(), is(1));
        assertThat(input.getRecords().get(0), is(Arrays.asList("1", "2", "3")));
    }

    @Test
    public void aheadTest() {

        StringInput input= new StringInput("abc");

        assertThat(input.ahead("a", 0), is(true));
        assertThat(input.ahead("ab", 0), is(true));
        assertThat(input.ahead("abc", 0), is(true));

        assertThat(input.ahead("bc", 1), is(true));
        assertThat(input.ahead("c", 2), is(true));

        assertThat(input.ahead("A", 0), is(false));
        assertThat(input.ahead("c", 10), is(false));
        assertThat(input.ahead("abcd", 0), is(false));
    }
}
