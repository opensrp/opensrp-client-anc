package org.smartregister.anc.library.util;

import java.util.regex.Pattern;

public class FormattedDateMatcher  {

    private static Pattern DATE_PATTERN = Pattern.compile(
      "^\\d{2}-\\d{2}-\\d{4}$");


    public static boolean matches(String date) {
        return DATE_PATTERN.matcher(date).matches();
    }
}
