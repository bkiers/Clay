package clay.input;

/**
 * An {@code Input} represented as a plain {@code String}.
 */
public class StringInput extends SimpleInput {

    /**
     * Creates a new {@code StringInput} instance with default:
     * - delimiter: {@value #DEFAULT_DELIMITER}
     * - quotation: {@value #DEFAULT_QUOTATION}
     * - escape quotation: {@value #DEFAULT_ESCAPE_QUOTATION}
     * - end of record: {@value #DEFAULT_RECORD_END}
     *
     * @param input
     *         the CSV input.
     *
     * @throws IllegalArgumentException
     *         when any of the parameters is {@code null}.
     */
    public StringInput(String input) throws IllegalArgumentException {
        this(input, DEFAULT_DELIMITER);
    }

    /**
     * Creates a new {@code StringInput} instance with default:
     * - quotation: {@value #DEFAULT_QUOTATION}
     * - escape quotation: {@value #DEFAULT_ESCAPE_QUOTATION}
     * - end of record: {@value #DEFAULT_RECORD_END}
     *
     * @param input
     *         the CSV input.
     * @param delimiter
     *         the CSV cell delimiter.
     *
     * @throws IllegalArgumentException
     *         when any of the parameters is {@code null}.
     */
    public StringInput(String input, String delimiter) throws IllegalArgumentException {
        this(input, delimiter, DEFAULT_QUOTATION, DEFAULT_ESCAPE_QUOTATION);
    }

    /**
     * Creates a new {@code StringInput} instance with default:
     * - end of record: {@value #DEFAULT_RECORD_END}
     *
     * @param input
     *         the CSV input.
     * @param delimiter
     *         the CSV cell delimiter.
     * @param quotation
     *         the CSV cell quotation.
     * @param escapeQuotation
     *         the string that escapes the CSV quotation.
     *
     * @throws IllegalArgumentException
     *         when any of the parameters is {@code null}.
     */
    public StringInput(String input, String delimiter, String quotation, String escapeQuotation)
            throws IllegalArgumentException {

        this(input, delimiter, quotation, escapeQuotation, DEFAULT_RECORD_END);
    }

    /**
     * Creates a new {@code StringInput} instance.
     *
     * @param input
     *         the CSV input.
     * @param delimiter
     *         the CSV cell delimiter.
     * @param quotation
     *         the CSV cell quotation.
     * @param escapeQuotation
     *         the string that escapes the CSV quotation.
     * @param recordEnd
     *         the string that marks the end of a record.
     *
     * @throws IllegalArgumentException
     *         when any of the parameters is {@code null}.
     */
    public StringInput(String input, String delimiter, String quotation, String escapeQuotation, String recordEnd)
            throws IllegalArgumentException {

        super(input, delimiter, quotation, escapeQuotation, recordEnd);
    }
}
