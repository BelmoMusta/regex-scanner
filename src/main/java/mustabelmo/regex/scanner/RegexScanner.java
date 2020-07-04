package mustabelmo.regex.scanner;
import java.io.Closeable;
import java.util.Iterator;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scanner for text using regex to match full tokens.
 *
 * @author Mustapha Belmokhtar
 * @version 0.1
 */
public class RegexScanner implements Iterator<String>, Closeable {
    private Pattern pattern;
    private String input;
    private boolean closed;
    private boolean endReached;

    /**
     * Consumes string tokens that matches the given regex
     *
     * @param input the input string.
     * @param regex the regex to match against.
     */
    public RegexScanner(String input, String regex) {
        this.input = input;
        pattern = Pattern.compile(regex);
    }

    /**
     * Constructor
     * Consumes each non blank token of the input string.
     *
     * @param input the input {@link String}
     */
    public RegexScanner(String input) {
        this(input, "[^\\s]+");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        if (closed) {
            throw new IllegalStateException("Scanner is closed");
        }
        
        return !endReached && !input.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String next() {
        if (closed) {
            throw new IllegalStateException("Scanner is closed");
        } else {
            String fullMatch = null;
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                int end = matcher.end();
                int start = matcher.start();

                fullMatch = input.substring(start, end);
                input = input.substring(end);
                verifyEndReached();

            } else do {
                if (!input.isEmpty()) {
                    input = input.substring(1);
                }
                matcher = pattern.matcher(input);
            } while (!endReached && !matcher.find() && !input.isEmpty());
            return fullMatch;
        }
    }
    public <R> R next(Function<String ,R> mapper){
        final String next = next();
        return mapper.apply(next);
    }

    /**
     * after each next invocation, a verification is done to avoid null
     * and empty strings when the whole rest of the string does not contain
     * any match of the given regex
     */
    private void verifyEndReached() {
        String temp = input;
        Matcher matcher;
        do {
            if (!temp.isEmpty()) {
                temp = temp.substring(1);
            }
            matcher = pattern.matcher(temp);
        } while (!matcher.find() && !temp.isEmpty());

        if (temp.isEmpty()) {
            endReached = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        closed = true;
        pattern = null;
        input = null;
    }
}
