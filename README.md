# regex-scanner
Regex based java scanner
This repository contains some utilities to extract tokens that match a given regex from a string.

For example, when you want to extract lines that represent a `key:value` items :

``` java
public static Map<String, String> getMapFromKeyValuePairs(String keyValuePairs) {
        final String regex = "\\w+[\\t ]*:[\\t ]*\\w+";
        final RegexScanner regexScanner = new RegexScanner(keyValuePairs, regex);
        final Map<String, String> keyValueMap = new LinkedHashMap<>();

        while (regexScanner.hasNext()) {
            final Function<String, String[]> mapper = token -> token.split("[\\t ]*:[\\t ]*");
            String[] split = regexScanner.next(mapper);
            keyValueMap.put(split[0], split[1]);
        }
        return keyValueMap;
    }

    public static void main(String[] args) {
            final String keyValuePairs = "key0:0\n" +
                    "key1:   1\n" +
                    "key2: 10\n" +
                    "key3  :11\n" +
                    "key4:  111";

            Map<String, String> mapFromKeyValuePairs = getMapFromKeyValuePairs(keyValuePairs);
            System.out.println(mapFromKeyValuePairs);
        }
```
##Output:
```json
{key0=0, key1=1, key2=10, key3=11, key4=111}
```

Notice that you may use the `RegexScanner.next()` if you do not want to convert the found tokens.

In the previous example a `mapper` is used to convert the found token to an array of paired key values.
