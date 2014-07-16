package clay.input;

import clay.filter.Filter;

import java.io.Serializable;
import java.util.List;

public interface Input extends Serializable {

    public static final String DEFAULT_DELIMITER = ",";
    public static final String DEFAULT_QUOTATION = "\"";
    public static final String DEFAULT_ESCAPE_QUOTATION = DEFAULT_QUOTATION;
    public static final String DEFAULT_RECORD_END = "\n";

    Input with(Filter filter);

    Input without(Class<? extends Filter> filterType);

    String getInput();

    String getDelimiter();

    String getQuotation();

    String getEscapeQuotation();

    String getRecordEnd();

    List<List<String>> getRecords();
}
