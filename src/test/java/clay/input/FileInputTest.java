package clay.input;

import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FileInputTest {

    @Test
    public void getRecords_DefaultDelimiter_ShouldReturnExpectedValue() throws Exception {

        Input input = new FileInput(new File("src/test/resources/test.csv"));

        List<List<String>> records = input.getRecords();

        assertThat(records.size(), is(2));
    }

    @Test
    public void getRecords_CustomDelimiter_ShouldReturnExpectedValue() throws Exception {

        Input input = new FileInput(new File("src/test/resources/addresses.csv"), "|");

        List<List<String>> records = input.getRecords();

        assertThat(records.size(), is(101));
    }
}
