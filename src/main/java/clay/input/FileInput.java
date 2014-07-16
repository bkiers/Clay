package clay.input;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * An {@code Input} represented as a local {@code File}.
 */
public class FileInput extends SimpleInput {

    /**
     * Creates a new {@code StringInput} instance with default:
     * - delimiter: {@value #DEFAULT_DELIMITER}
     * - quotation: {@value #DEFAULT_QUOTATION}
     * - escape quotation: {@value #DEFAULT_ESCAPE_QUOTATION}
     * - end of record: {@value #DEFAULT_RECORD_END}
     *
     * @param file
     *         the CSV input file.
     *
     * @throws FileNotFoundException
     *         when {@code file} does not exist.
     * @throws IllegalArgumentException
     *         when any of the parameters is {@code null}.
     */
    public FileInput(File file) throws FileNotFoundException, IllegalArgumentException {
        this(file, DEFAULT_DELIMITER);
    }

    /**
     * Creates a new {@code StringInput} instance with default:
     * - quotation: {@value #DEFAULT_QUOTATION}
     * - escape quotation: {@value #DEFAULT_ESCAPE_QUOTATION}
     * - end of record: {@value #DEFAULT_RECORD_END}
     *
     * @param file
     *         the CSV input file.
     * @param delimiter
     *         the CSV cell delimiter.
     *
     * @throws FileNotFoundException
     *         when {@code file} does not exist.
     * @throws IllegalArgumentException
     *         when any of the parameters is {@code null}.
     */
    public FileInput(File file, String delimiter) throws FileNotFoundException, IllegalArgumentException {
        this(file, delimiter, DEFAULT_QUOTATION, DEFAULT_ESCAPE_QUOTATION);
    }

    /**
     * Creates a new {@code StringInput} instance with default:
     * - end of record: {@value #DEFAULT_RECORD_END}
     *
     * @param file
     *         the CSV input file.
     * @param delimiter
     *         the CSV cell delimiter.
     * @param quotation
     *         the CSV cell quotation.
     * @param escapeQuotation
     *         the string that escapes the CSV quotation.
     *
     * @throws FileNotFoundException
     *         when {@code file} does not exist.
     * @throws IllegalArgumentException
     *         when any of the parameters is {@code null}.
     */
    public FileInput(File file, String delimiter, String quotation, String escapeQuotation)
            throws FileNotFoundException, IllegalArgumentException {

        this(file, delimiter, quotation, escapeQuotation, DEFAULT_RECORD_END);
    }

    /**
     * Creates a new {@code StringInput} instance.
     *
     * @param file
     *         the CSV input file.
     * @param delimiter
     *         the CSV cell delimiter.
     * @param quotation
     *         the CSV cell quotation.
     * @param escapeQuotation
     *         the string that escapes the CSV quotation.
     * @param recordEnd
     *         the string that marks the end of a record.
     *
     * @throws FileNotFoundException
     *         when {@code file} does not exist.
     * @throws IllegalArgumentException
     *         when any of the parameters is {@code null}.
     */
    public FileInput(File file, String delimiter, String quotation, String escapeQuotation, String recordEnd)
            throws FileNotFoundException, IllegalArgumentException {

        super(new Scanner(file).useDelimiter("\\Z").next(), delimiter, quotation, escapeQuotation, recordEnd);
    }
}
