package clay.input;

import clay.filter.Filter;

import java.io.Serializable;
import java.util.List;

/**
 * An interface that describes a CSV input.
 */
public interface CSVInput extends Serializable {

    /**
     * The default delimiter that separates CSV cells: {@value}
     */
    public static final String DEFAULT_DELIMITER = ",";

    /**
     * The default quotation for CSV cells: {@value}
     */
    public static final String DEFAULT_QUOTATION = "\"";

    /**
     * The default string that escapes a quotation: {@value}
     */
    public static final String DEFAULT_ESCAPE_QUOTATION = DEFAULT_QUOTATION;

    /**
     * The default string that denotes the end of a CSV record: {@value}
     */
    public static final String DEFAULT_RECORD_END = "\n";

    /**
     * Adds a filter which will be applied to all CSV cells when
     * parsing the input.
     *
     * @param filter
     *         the filter to add.
     *
     * @return this instance.
     *
     * @throws IllegalArgumentException
     *         when the {@code filter} is {@code null}.
     * @throws IllegalStateException
     *         when the input is already parsed, which is done when
     *         {@link #getRecords()} is first called.
     */
    CSVInput with(Filter filter);

    /**
     * Removes a filter which will not be applied to all CSV cells when
     * parsing the input.
     *
     * @param filterType
     *         the filter type to remove.
     *
     * @return this instance.
     *
     * @throws IllegalArgumentException
     *         when the {@code filter} is {@code null}.
     * @throws IllegalStateException
     *         when the input is already parsed, which is done when
     *         {@link #getRecords()} is first called.
     */
    CSVInput without(Class<? extends Filter> filterType);

    /**
     * Returns the CSV input.
     *
     * @return the CSV input.
     */
    String getInput();

    /**
     * Returns the CSV cell delimiter.
     *
     * @return the CSV cell delimiter.
     */
    String getDelimiter();

    /**
     * Returns the CSV cell quotation.
     *
     * @return the CSV cell quotation.
     */
    String getQuotation();

    /**
     * Returns the CSV cell quotation escape.
     *
     * @return the CSV cell quotation escape.
     */
    String getEscapeQuotation();

    /**
     * Returns the end mark for the CSV record.
     *
     * @return the end mark for the CSV record.
     */
    String getRecordEnd();

    /**
     * Returns a shallow copy of the parsed {@code input} as a 2D list of strings.
     *
     * @return a shallow copy of the parsed {@code input} as a 2D list of strings.
     */
    List<List<String>> getRecords();
}
