package clay.input;

import clay.filter.Filter;
import clay.filter.TrimFilter;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SimpleInputTest {

    @Test(expected = IllegalArgumentException.class)
    public void newSimpleInput_NullInputParam_ShouldThrowException() {
        new StringInput(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newSimpleInput_NullDelimiterParam_ShouldThrowException() {
        new StringInput("a,b,c", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newSimpleInput_NullQuoteParam_ShouldThrowException() {
        new StringInput("a,b,c", ",", null, "'");
    }

    @Test(expected = IllegalArgumentException.class)
    public void newSimpleInput_NullEscapeQuoteParam_ShouldThrowException() {
        new StringInput("a,b,c", ",", "'", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newSimpleInput_NullEndOfRecordParam_ShouldThrowException() {
        new StringInput("a,b,c", ",", "'", "'", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void with_NullParam_ShouldThrowException() {
        new StringInput("a,b,c").with(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void without_NullParam_ShouldThrowException() {
        new StringInput("a,b,c").without(null);
    }

    @Test(expected = RuntimeException.class)
    public void getRecords_UnclosedQuote_ShouldThrowException() {
        new StringInput("a,\"b,c").getRecords();
    }

    @Test(expected = IllegalStateException.class)
    public void with_AlreadyParsed_ShouldThrowException() {

        Input input = new StringInput("a,b,c");

        // parse it
        input.getRecords();

        // now try to add a filter
        input.with(new TrimFilter());
    }

    @Test(expected = IllegalStateException.class)
    public void without_AlreadyParsed_ShouldThrowException() {

        Input input = new StringInput("a,b,c");

        // parse it
        input.getRecords();

        // now try to add a filter
        input.without(TrimFilter.class);
    }

    @Test
    public void getInput_NormalUse_ShouldReturnExpectedValue() {
        Input input = new StringInput("a,b,c");
        assertThat(input.getInput(), is("a,b,c"));
    }

    @Test
    public void getDelimiter_NormalUse_ShouldReturnExpectedValue() {

        Input input = new StringInput("a,b,c");
        assertThat(input.getDelimiter(), is(Input.DEFAULT_DELIMITER));

        input = new StringInput("a,b,c", "|");
        assertThat(input.getDelimiter(), is("|"));
    }

    @Test
    public void getQuotation_NormalUse_ShouldReturnExpectedValue() {

        Input input = new StringInput("a,b,c");
        assertThat(input.getQuotation(), is(Input.DEFAULT_QUOTATION));

        input = new StringInput("a,b,c", ",", "#", "\\");
        assertThat(input.getQuotation(), is("#"));
    }

    @Test
    public void getEscapeQuotation_NormalUse_ShouldReturnExpectedValue() {

        Input input = new StringInput("a,b,c");
        assertThat(input.getEscapeQuotation(), is(Input.DEFAULT_ESCAPE_QUOTATION));

        input = new StringInput("a,b,c", ",", "#", "\\");
        assertThat(input.getEscapeQuotation(), is("\\"));
    }

    @Test
    public void getRecordEnd_NormalUse_ShouldReturnExpectedValue() {

        Input input = new StringInput("a,b,c");
        assertThat(input.getRecordEnd(), is(Input.DEFAULT_RECORD_END));

        input = new StringInput("a,b,c", ",", "#", "\\", "\t\t\t");
        assertThat(input.getRecordEnd(), is("\t\t\t"));
    }

    @Test
    public void getRecords_WithRowIndexFilter_ShouldSkipRow() throws Exception {

        String csv = "1,2,3\n" +
                "11,12,13\n" +
                "21,22,23";

        Input input = new StringInput(csv).with(new Filter(){
            @Override
            public String apply(int rowIndex, int columnIndex, String value) {
                return rowIndex == 0 ? null : value;
            }
        });

        List<List<String>> records = input.getRecords();

        assertThat(records.size(), is(2));
        assertThat(records.get(0), is(Arrays.asList("11", "12", "13")));
        assertThat(records.get(1), is(Arrays.asList("21", "22", "23")));
    }

    @Test
    public void getRecords_WithColumnIndexFilter_ShouldSkipColumn() throws Exception {

        String csv = "1,2,3\n" +
                "11,12,13\n" +
                "21,22,23";

        Input input = new StringInput(csv).with(new Filter(){
            @Override
            public String apply(int rowIndex, int columnIndex, String value) {
                return columnIndex == 2 ? null : value;
            }
        });

        List<List<String>> records = input.getRecords();

        assertThat(records.size(), is(3));
        assertThat(records.get(0), is(Arrays.asList("1", "2")));
        assertThat(records.get(1), is(Arrays.asList("11", "12")));
        assertThat(records.get(2), is(Arrays.asList("21", "22")));
    }

    @Test
    public void getRecords_WithUnicodeMetaChars_ShouldReturnExpectedValues() throws Exception {

        String delimiter = "⅀";
        String quotation = "\u210b"; // == "ℋ";
        String escapeQuotation = "\\";

        String csv = "a⅀b⅀c\n" +
                "1⅀ℋ2⅀\\ℋ2⅀2ℋ⅀3";

        Input input = new StringInput(csv, delimiter, quotation, escapeQuotation);

        List<List<String>> records = input.getRecords();

        assertThat(records.size(), is(2));
        assertThat(records.get(0), is(Arrays.asList("a", "b", "c")));
        assertThat(records.get(1), is(Arrays.asList("1", "2⅀ℋ2⅀2", "3")));
    }
}
