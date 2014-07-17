package clay.input;

import clay.filter.Filter;
import clay.filter.TrimFilter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Represents a simple CSV input backed up by a plain {@code String} which
 * this class will parse into a 2D list of strings.
 */
public abstract class SimpleInput implements Input {

    /**
     * A map holding filters that will be applied (in order!) to all
     * the data cells from the CSV input string.
     */
    protected final LinkedHashMap<Class<? extends Filter>, Filter> filters;

    /**
     * The actual CSV input string.
     */
    protected final String input;

    /**
     * The input string parsed into a 2D list of strings.
     */
    protected List<List<String>> data;

    /**
     * The delimiter that separate the CSV data cells.
     */
    protected final String delimiter;

    /**
     * The substring that denotes the quotation of a CSV cell.
     */
    protected final String quotation;

    /**
     * The substring that denotes the escape of a quotation of a CSV cell.
     */
    protected final String escapeQuotation;

    /**
     * The substring that denotes the end of a CSV record.
     */
    protected final String recordEnd;

    /**
     * Creates a new instance of a {@code SimpleInput}.
     *
     * @param input
     *         the actual CSV input string.
     * @param delimiter
     *         the delimiter that separate the CSV data cells.
     * @param quotation
     *         the substring that denotes the quotation of a CSV cell.
     * @param escapeQuotation
     *         the substring that denotes the escape of a quotation of a CSV cell.
     * @param recordEnd
     *         the substring that denotes the end of a CSV record.
     *
     * @throws IllegalArgumentException
     *         when any of the parameters is {@code null}.
     */
    public SimpleInput(String input, String delimiter, String quotation, String escapeQuotation, String recordEnd)
            throws IllegalArgumentException {

        if (input == null) {
            throw new IllegalArgumentException("input == null");
        }

        if (delimiter == null) {
            throw new IllegalArgumentException("delimiter == null");
        }

        if (quotation == null) {
            throw new IllegalArgumentException("quotation == null");
        }

        if (escapeQuotation == null) {
            throw new IllegalArgumentException("escapeQuotation == null");
        }

        if (recordEnd == null) {
            throw new IllegalArgumentException("recordEnd == null");
        }

        this.input = input;
        this.delimiter = delimiter;
        this.quotation = quotation;
        this.escapeQuotation = escapeQuotation;
        this.recordEnd = recordEnd;

        this.filters = new LinkedHashMap<Class<? extends Filter>, Filter>();
        this.data = null;

        this.with(new TrimFilter());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Input with(Filter filter) throws IllegalArgumentException, IllegalStateException {

        if (filter == null) {
            throw new IllegalArgumentException("filter == null");
        }

        if (this.data != null) {
            throw new IllegalStateException("the input is already parsed: cannot add " + filter.getClass());
        }

        this.filters.put(filter.getClass(), filter);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Input without(Class<? extends Filter> filterType) throws IllegalArgumentException, IllegalStateException {

        if (filterType == null) {
            throw new IllegalArgumentException("filterType == null");
        }

        if (this.data != null) {
            throw new IllegalStateException("the input is already parsed: cannot remove " + filterType);
        }

        this.filters.remove(filterType);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInput() {
        return input;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getQuotation() {
        return quotation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEscapeQuotation() {
        return escapeQuotation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRecordEnd() {
        return recordEnd;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<List<String>> getRecords() {

        if (this.data == null) {
            this.parse();
        }

        return new ArrayList<List<String>>(data);
    }

    // Parses the input, ignoring any empty records.
    private void parse() {

        this.data = new ArrayList<List<String>>();

        int inputIndex = 0;
        int rowIndex = 0;
        int columnIndex = 0;

        StringBuilder buffer = new StringBuilder();
        List<String> row = new ArrayList<String>();

        while (inputIndex < input.length()) {

            char current = this.input.charAt(inputIndex);

            if (ahead(this.delimiter, inputIndex)) {
                this.insert(rowIndex, columnIndex, buffer.toString(), row);
                buffer = new StringBuilder();
                inputIndex += this.delimiter.length();
                columnIndex++;
            }
            else if (ahead(this.recordEnd, inputIndex)) {
                this.insert(rowIndex, columnIndex, buffer.toString(), row);
                buffer = new StringBuilder();
                this.insert(row);
                row = new ArrayList<String>();
                inputIndex += this.recordEnd.length();
                rowIndex++;
                columnIndex = 0;
            }
            else if (ahead(this.quotation, inputIndex)) {

                inputIndex += this.quotation.length();
                buffer = new StringBuilder();

                while (true) {

                    if (inputIndex >= input.length()) {
                        throw new RuntimeException("missing closing quote '" + this.quotation + "'");
                    }

                    current = this.input.charAt(inputIndex);

                    if (ahead(this.escapeQuotation + this.quotation, inputIndex)) {
                        buffer.append(this.quotation);
                        inputIndex += (this.escapeQuotation.length() + this.quotation.length());
                    }
                    else if (ahead(this.quotation, inputIndex)) {
                        inputIndex += this.quotation.length();
                        break;
                    }
                    else {
                        buffer.append(current);
                        inputIndex++;
                    }
                }
            }
            else {
                buffer.append(current);
                inputIndex++;
            }
        }

        if (!buffer.toString().isEmpty()) {
            this.insert(rowIndex, columnIndex, buffer.toString(), row);
        }

        this.insert(row);
    }

    /**
     * Returns {@code true} iff the {@code substring} at the given {@code index}
     * can be seen from the {@link #input}.
     *
     * @param substring
     *         the sub string to check.
     * @param index
     *         the index from the {@link #input} to start looking ahead from.
     *
     * @return {@code true} iff the {@code substring} at the given {@code index}
     * can be seen from the {@link #input}.
     */
    protected boolean ahead(String substring, int index) {

        if ((index + substring.length()) > this.input.length()) {
            // No place for a substring: it would go beyond the end of the input.
            return false;
        }

        for (int i = 0; i < substring.length(); i++) {
            if (this.input.charAt(index + i) != substring.charAt(i)) {
                // No, at least one char differs.
                return false;
            }
        }

        return true;
    }

    /**
     * Inserts a cell value if none of the applied filters return {@code null}.
     *
     * @param rowIndex
     *         the index of the row of the cell.
     * @param column
     *         the index of the column of the cell.
     * @param value
     *         the value of the cell.
     * @param dataRow
     *         the list holding all of the values in the current row.
     */
    protected void insert(int rowIndex, int column, String value, List<String> dataRow) {

        boolean add = true;

        for (Filter filter : this.filters.values()) {

            value = filter.apply(rowIndex, column, value);

            if (value == null) {
                // One of the filters excluded this value.
                add = false;
                break;
            }
        }

        if (add) {
            dataRow.add(value);
        }
    }

    /**
     * Inserts a row of values iff the row contains at least one cell
     * that is non-null and not empty.
     *
     * @param dataRow
     *         the row to add.
     */
    protected void insert(List<String> dataRow) {

        boolean add = false;

        for (String cell : dataRow) {

            if (!(cell == null || cell.isEmpty())) {
                // There is at least one cell with a value in it.
                add = true;
                break;
            }
        }

        if (add) {
            this.data.add(dataRow);
        }
    }
}
