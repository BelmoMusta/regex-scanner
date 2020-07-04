package mustabelmo.regex.scanner;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

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

    @Test
    public void testNextWithExplicitTokens() {
        final String regex = "(condimentum)|(faucibus)";
        final String lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean ultrices, sem nec pellentesque viverra, urna risus ornare urna, eget accumsan purus tellus eget eros. Pellentesque sit amet dapibus tellus, quis condimentum orci. Etiam a leo libero. Vestibulum eu libero vitae erat mattis aliquet sit amet pellentesque ante. Aenean scelerisque neque sed bibendum aliquam. Sed ante ligula, vulputate quis consequat dignissim, efficitur nec lacus. Etiam gravida, dolor nec condimentum fermentum, velit mi viverra nulla, nec porttitor tortor odio vel tortor. Maecenas faucibus tempus dolor id hendrerit. Sed ultrices tristique velit, varius aliquet dolor euismod eu. Maecenas imperdiet, ipsum vitae suscipit sollicitudin, nisl arcu hendrerit orci, a blandit tortor lectus sit amet nulla.\n" +
                "\n" +
                "Nulla quis elit tellus. Sed ultrices suscipit urna, non faucibus odio pretium eu. Nunc ligula quam, feugiat eget velit in, blandit imperdiet ligula. Proin in varius justo, nec porta sapien. Ut finibus est eu arcu lobortis cursus. Ut luctus lorem ac lorem laoreet, sed scelerisque sapien pharetra. Curabitur justo mauris, molestie quis auctor nec, interdum non lacus. Sed auctor quam enim, sodales tristique enim tincidunt eget. Ut malesuada vulputate orci, quis congue tortor mattis id. Sed fermentum risus felis, vel ullamcorper dolor porttitor non.";

        final String[] expectedMatches = {"condimentum", "faucibus"};
        RegexScanner regexScanner = new RegexScanner(lorem, regex);
        Set<String> listOfMatchedTokens = new TreeSet<>();
        while (regexScanner.hasNext()) {
            final String next = regexScanner.next();
            listOfMatchedTokens.add(next);
        }
        System.out.println(listOfMatchedTokens);
        Assert.assertArrayEquals(expectedMatches, listOfMatchedTokens.toArray());
    }

    @Test
    public void testNextGetJustCapitalizedWords() {
        final String regex = "([A-Z][A-Za-z]*)";
        final String lorem = "Lorem Nulla dolor sit AAAAmet 12354,";
        RegexScanner regexScanner = new RegexScanner(lorem, regex);

        int count = 0;
        while (regexScanner.hasNext()) {
            regexScanner.next();
            count++;

        }
        Assert.assertEquals(3, count);
    }
    
    @Test
    public void testUUID() {
        final String regex = "[0-9abcdef]{8}(-[0-9abcdef]{4}){3}-[0-9abcdef]{12}";
        final String text = "uuid : 6d0a3538-9760-41ae-965d-7aad70021f81\n" +
                "uuid : d7d97fb3-3676-4109-9a94-7acc5f593ace\n" +
                "uuid : 02e87dd3-10ff-43cf-9572-bd9d151bb439\n" +
                "uuid : 632a4c31-8dfe-43a3-8f8d-15b472292cc9";
    
        final List<String> expectedUUIDs = Arrays.asList("6d0a3538-9760-41ae-965d-7aad70021f81",
                "d7d97fb3-3676-4109-9a94-7acc5f593ace",
                "02e87dd3-10ff-43cf-9572-bd9d151bb439",
                "632a4c31-8dfe-43a3-8f8d-15b472292cc9");
        
        final List<String> foundUUIDs = new ArrayList<>();
        final RegexScanner regexScanner = new RegexScanner(text, regex);
        while (regexScanner.hasNext()) {
            foundUUIDs.add(regexScanner.next());
        }
        Assert.assertArrayEquals(expectedUUIDs.toArray(), foundUUIDs.toArray());
    }

    @Test
    public void testNextWithMapper() {
        final String regex = "Lorem";
        final String lorem = "Lorem Nulla dolor sit AAAAmet 12354 Lorem,";
        RegexScanner regexScanner = new RegexScanner(lorem, regex);

        int countOfLengths = 0;
        while (regexScanner.hasNext()) {
            Integer length = regexScanner.next(String::length);
            countOfLengths += length;
        }
        Assert.assertEquals(2 * "Lorem".length(), countOfLengths);
        regexScanner.close();
    }


    @Test
    public void getMapFromKeyValuePairs() {
        final String regex = "\\w+[\\t ]*:[\\t ]*\\w+";
        final String keyValuePairs = "key0:0\n" +
                "key1:   1\n" +
                "key2: 10\n" +
                "key3  :11\n" +
                "key4:  111";

        final RegexScanner regexScanner = new RegexScanner(keyValuePairs, regex);
        final Map<String, String> keyValueMap = new LinkedHashMap<>();

        while (regexScanner.hasNext()) {
            final Function<String, String[]> mapper = token -> token.split("[\\t ]*:[\\t ]*");
            final String[] split = regexScanner.next(mapper);
            keyValueMap.put(split[0], split[1]);
        }

        System.out.println(keyValueMap);
        Assert.assertEquals(5, keyValueMap.size());
        regexScanner.close();
    }
    
    @Test(expected = IllegalStateException.class)
    public void testHasNextWhenScannerClosed(){
       final RegexScanner regexScanner = new RegexScanner("some input string",".*");
        regexScanner.close();
        while (regexScanner.hasNext()) {
            regexScanner.next();
        }
    }
    
    @Test(expected = IllegalStateException.class)
    public void testNextWhenScannerClosed(){
        final RegexScanner regexScanner = new RegexScanner("some input string",".*");
        regexScanner.close();
        regexScanner.next();
    }
}
