package mustabelmo.regex.scanner;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RegexScannerTest {
    @Test
    public void testNextWithIntegerNumbersRegex() {
        final String regex = "\\d+";
        final String input = "this is an integer 254, and this is another one 14587";
        final String[] expectedMatches = {"254", "14587"};
        RegexScanner regexScanner = new RegexScanner(input, regex);
        List<String> listOfMatchedTokens = new ArrayList<>();
        while (regexScanner.hasNext()) {
            final String next = regexScanner.next();
            listOfMatchedTokens.add(next);
        }
        Assert.assertArrayEquals(expectedMatches, listOfMatchedTokens.toArray());
    }
}
