package validators.commons;

import org.junit.jupiter.api.Test;
import quepasa.api.validators.commons.StringValidator;

import static org.junit.jupiter.api.Assertions.*;
import static quepasa.api.validators.commons.builders.ValidatorBuilder.OnInvalidateAction.DO_NOTHING;

public class StringValidatorBuilderTests {

    @Test
    public void initTests() {
        var init = new StringValidator("");
        assertTrue(init.isValid());
        assertTrue(init.getErrors().isEmpty());
    }

    @Test
    public void nullTests() {
        var nullCase = new StringValidator(null)
                .onInvalidate(DO_NOTHING)
                .isNotNull();
        assertFalse(nullCase.isValid());
        assertFalse(nullCase.getErrors().isEmpty());
        assertEquals(nullCase.getErrors().size(), 1);

        var notNullCase = new StringValidator("")
                .onInvalidate(DO_NOTHING)
                .isNotNull();
        assertTrue(notNullCase.isValid());
        assertTrue(notNullCase.getErrors().isEmpty());

        var nullReplaceValue = new StringValidator(null)
                .onInvalidate(DO_NOTHING)
                .ifNullThen("newValue")
                .isNotBlank();
        assertTrue(nullReplaceValue.isValid());
        assertTrue(nullReplaceValue.getErrors().isEmpty());
        assertEquals("newValue", nullReplaceValue.build());

        var notNullReplaceValue = new StringValidator("abcdef")
                .onInvalidate(DO_NOTHING)
                .ifNullThen("newValue")
                .isNotBlank();
        assertTrue(notNullReplaceValue.isValid());
        assertTrue(notNullReplaceValue.getErrors().isEmpty());
        assertEquals("abcdef", notNullReplaceValue.build());

    }

    @Test
    public void emptyTests() {
        var emptyCase = new StringValidator("")
                .onInvalidate(DO_NOTHING)
                .isNotBlank();
        assertFalse(emptyCase.isValid());
        assertFalse(emptyCase.getErrors().isEmpty());
        assertEquals(emptyCase.getErrors().size(), 1);

        var nonEmptyCase = new StringValidator("non-empty")
                .onInvalidate(DO_NOTHING)
                .isNotBlank();
        assertTrue(nonEmptyCase.isValid());
        assertTrue(nonEmptyCase.getErrors().isEmpty());

        var nonBlankReplaceValue = new StringValidator("non-blank")
                .onInvalidate(DO_NOTHING)
                .ifBlankThen("newValue")
                .isNotBlank();
        assertTrue(nonBlankReplaceValue.isValid());
        assertTrue(nonBlankReplaceValue.getErrors().isEmpty());
        assertEquals("non-blank", nonBlankReplaceValue.build());

        var blankReplaceValue = new StringValidator("")
                .onInvalidate(DO_NOTHING)
                .ifBlankThen("newValue")
                .isNotBlank();
        assertTrue(blankReplaceValue.isValid());
        assertTrue(blankReplaceValue.getErrors().isEmpty());
        assertEquals("newValue", blankReplaceValue.build());

    }

    @Test
    public void minimumTests() {
        var meetsMinimumCase = new StringValidator("abc")
                .onInvalidate(DO_NOTHING)
                .hasMinimumLength(3);
        assertTrue(meetsMinimumCase.isValid());
        assertTrue(meetsMinimumCase.getErrors().isEmpty());

        var doesNotMeetMinimumCase = new StringValidator("de")
                .onInvalidate(DO_NOTHING)
                .hasMinimumLength(3);
        assertFalse(doesNotMeetMinimumCase.isValid());
        assertEquals(doesNotMeetMinimumCase.getErrors().size(), 1);
    }

    @Test
    public void maximumTests() {
        var meetsMaximumCase = new StringValidator("abcd")
                .onInvalidate(DO_NOTHING)
                .hasMaximumLength(5);
        assertTrue(meetsMaximumCase.isValid());
        assertTrue(meetsMaximumCase.getErrors().isEmpty());

        var meetsMaximumCaseInLimit = new StringValidator("abc")
                .onInvalidate(DO_NOTHING)
                .hasMaximumLength(5);
        assertTrue(meetsMaximumCaseInLimit.isValid());
        assertTrue(meetsMaximumCaseInLimit.getErrors().isEmpty());

        var doesNotMeetMaximumCase = new StringValidator("abcdef")
                .onInvalidate(DO_NOTHING)
                .hasMaximumLength(5);
        assertFalse(doesNotMeetMaximumCase.isValid());
        assertEquals(doesNotMeetMaximumCase.getErrors().size(), 1);
    }

    @Test
    public void matchesTests() {
        var matchesCase = new StringValidator("abc123")
                .onInvalidate(DO_NOTHING)
                .matches("^[a-zA-Z0-9]+$", "(!)");

        assertTrue(matchesCase.isValid());
        assertTrue(matchesCase.getErrors().isEmpty());

        var doesNotMatchCase = new StringValidator("$%&")
                .onInvalidate(DO_NOTHING)
                .matches("^[a-zA-Z0-9]+$", "(!)");

        assertFalse(doesNotMatchCase.isValid());
        assertEquals(doesNotMatchCase.getErrors().size(), 1);

    }

    @Test
    public void hasXUpperCaseLetters() {
        var correctCase = new StringValidator("ABCabc123")
                .onInvalidate(DO_NOTHING)
                .hasXUpperCaseLetters(3);
        assertTrue(correctCase.isValid());
        assertTrue(correctCase.getErrors().isEmpty());

        var moreCase = new StringValidator("ABCDEFabc123")
                .onInvalidate(DO_NOTHING)
                .hasXUpperCaseLetters(3);
        assertFalse(moreCase.isValid());
        assertEquals(moreCase.getErrors().size(), 1);

        var lessCase = new StringValidator("Aabc123")
                .onInvalidate(DO_NOTHING)
                .hasXUpperCaseLetters(3);
        assertFalse(lessCase.isValid());
        assertEquals(lessCase.getErrors().size(), 1);

    }

    @Test
    public void hasXLowerCaseLetters() {
        var correctCase = new StringValidator("ABCabc123")
                .onInvalidate(DO_NOTHING)
                .hasXLowerCaseLetters(3);
        assertTrue(correctCase.isValid());
        assertTrue(correctCase.getErrors().isEmpty());

        var moreCase = new StringValidator("ABCabcdef123")
                .onInvalidate(DO_NOTHING)
                .hasXLowerCaseLetters(3);
        assertFalse(moreCase.isValid());
        assertEquals(moreCase.getErrors().size(), 1);

        var lessCase = new StringValidator("ABCa123")
                .onInvalidate(DO_NOTHING)
                .hasXLowerCaseLetters(3);
        assertFalse(lessCase.isValid());
        assertEquals(lessCase.getErrors().size(), 1);

    }

    @Test
    public void hasXDigits() {
        var correctCase = new StringValidator("ABCabc123")
                .onInvalidate(DO_NOTHING)
                .hasXDigits(3);
        assertTrue(correctCase.isValid());
        assertTrue(correctCase.getErrors().isEmpty());

        var moreCase = new StringValidator("ABCabc12345")
                .onInvalidate(DO_NOTHING)
                .hasXDigits(3);
        assertFalse(moreCase.isValid());
        assertEquals(moreCase.getErrors().size(), 1);

        var lessCase = new StringValidator("ABCabc1")
                .onInvalidate(DO_NOTHING)
                .hasXDigits(3);
        assertFalse(lessCase.isValid());
        assertEquals(lessCase.getErrors().size(), 1);

    }

    @Test
    public void hasXSpecialCharacters() {
        var correctCase = new StringValidator("ABCabc123$%&")
                .onInvalidate(DO_NOTHING)
                .hasXSpecialCharacters(3);
        assertTrue(correctCase.isValid());
        assertTrue(correctCase.getErrors().isEmpty());

        var moreCase = new StringValidator("ABCabc123$%&/?")
                .onInvalidate(DO_NOTHING)
                .hasXSpecialCharacters(3);
        assertFalse(moreCase.isValid());
        assertEquals(moreCase.getErrors().size(), 1);

        var lessCase = new StringValidator("ABCabc1$%")
                .onInvalidate(DO_NOTHING)
                .hasXSpecialCharacters(3);
        assertFalse(lessCase.isValid());
        assertEquals(lessCase.getErrors().size(), 1);

    }

    @Test
    public void hasAtLeastXUpperCaseLetters() {
        var moreCase = new StringValidator("ABCDEFabc123$%&")
                .onInvalidate(DO_NOTHING)
                .hasAtLeastXUpperCaseLetters(3);
        assertTrue(moreCase.isValid());
        assertTrue(moreCase.getErrors().isEmpty());

        var limitCase = new StringValidator("ABCabc123$%&")
                .onInvalidate(DO_NOTHING)
                .hasAtLeastXUpperCaseLetters(3);
        assertTrue(limitCase.isValid());
        assertTrue(limitCase.getErrors().isEmpty());

        var lessCase = new StringValidator("ABabc123$%&/?")
                .onInvalidate(DO_NOTHING)
                .hasAtLeastXUpperCaseLetters(3);
        assertFalse(lessCase.isValid());
        assertEquals(lessCase.getErrors().size(), 1);

        var noneCase = new StringValidator("abc1$%")
                .onInvalidate(DO_NOTHING)
                .hasAtLeastXUpperCaseLetters(3);
        assertFalse(noneCase.isValid());
        assertEquals(noneCase.getErrors().size(), 1);

    }

    @Test
    public void hasAtLeastXLowerCaseLetters() {
        var moreCase = new StringValidator("ABCabcdef123$%&")
                .onInvalidate(DO_NOTHING)
                .hasAtLeastXLowerCaseLetters(3);
        assertTrue(moreCase.isValid());
        assertTrue(moreCase.getErrors().isEmpty());

        var limitCase = new StringValidator("ABCabc123$%&")
                .onInvalidate(DO_NOTHING)
                .hasAtLeastXLowerCaseLetters(3);
        assertTrue(limitCase.isValid());
        assertTrue(limitCase.getErrors().isEmpty());

        var lessCase = new StringValidator("ABCab123$%&/?")
                .onInvalidate(DO_NOTHING)
                .hasAtLeastXLowerCaseLetters(3);
        assertFalse(lessCase.isValid());
        assertEquals(lessCase.getErrors().size(), 1);

        var noneCase = new StringValidator("ABC1$%")
                .onInvalidate(DO_NOTHING)
                .hasAtLeastXLowerCaseLetters(3);
        assertFalse(noneCase.isValid());
        assertEquals(noneCase.getErrors().size(), 1);

    }

    @Test
    public void hasAtLeastXDigits() {
        var moreCase = new StringValidator("ABCabc123456$%&")
                .onInvalidate(DO_NOTHING)
                .hasAtLeastXDigits(3);
        assertTrue(moreCase.isValid());
        assertTrue(moreCase.getErrors().isEmpty());

        var limitCase = new StringValidator("ABCabc123$%&")
                .onInvalidate(DO_NOTHING)
                .hasAtLeastXDigits(3);
        assertTrue(limitCase.isValid());
        assertTrue(limitCase.getErrors().isEmpty());

        var lessCase = new StringValidator("ABCabc12$%&/?")
                .onInvalidate(DO_NOTHING)
                .hasAtLeastXDigits(3);
        assertFalse(lessCase.isValid());
        assertEquals(lessCase.getErrors().size(), 1);

        var noneCase = new StringValidator("ABCabc$%")
                .onInvalidate(DO_NOTHING)
                .hasAtLeastXDigits(3);
        assertFalse(noneCase.isValid());
        assertEquals(noneCase.getErrors().size(), 1);

    }

    @Test
    public void hasAtLeastXSpecialCharacters() {
        var moreCase = new StringValidator("ABCabc123$%&?*")
                .onInvalidate(DO_NOTHING)
                .hasAtLeastXSpecialCharacters(3);
        assertTrue(moreCase.isValid());
        assertTrue(moreCase.getErrors().isEmpty());

        var limitCase = new StringValidator("ABCabc123$%&")
                .onInvalidate(DO_NOTHING)
                .hasAtLeastXSpecialCharacters(3);
        assertTrue(limitCase.isValid());
        assertTrue(limitCase.getErrors().isEmpty());

        var lessCase = new StringValidator("ABCabc123$%")
                .onInvalidate(DO_NOTHING)
                .hasAtLeastXSpecialCharacters(3);
        assertFalse(lessCase.isValid());
        assertEquals(lessCase.getErrors().size(), 1);

        var noneCase = new StringValidator("ABCabc123")
                .onInvalidate(DO_NOTHING)
                .hasAtLeastXSpecialCharacters(3);
        assertFalse(noneCase.isValid());
        assertEquals(noneCase.getErrors().size(), 1);

    }


    @Test
    public void hasAtMostXUpperCaseLetters() {
        var lessCase = new StringValidator("ABabc123$%&")
                .onInvalidate(DO_NOTHING)
                .hasAtMostXUpperCaseLetters(3);
        assertTrue(lessCase.isValid());
        assertTrue(lessCase.getErrors().isEmpty());

        var limitCase = new StringValidator("ABCabc123$%&")
                .onInvalidate(DO_NOTHING)
                .hasAtMostXUpperCaseLetters(3);
        assertTrue(limitCase.isValid());
        assertTrue(limitCase.getErrors().isEmpty());

        var moreCase = new StringValidator("ABCDabc123$%&/?")
                .onInvalidate(DO_NOTHING)
                .hasAtMostXUpperCaseLetters(3);
        assertFalse(moreCase.isValid());
        assertEquals(moreCase.getErrors().size(), 1);

        var noneCase = new StringValidator("abc1$%")
                .onInvalidate(DO_NOTHING)
                .hasAtMostXUpperCaseLetters(3);
        assertTrue(noneCase.isValid());
        assertTrue(noneCase.getErrors().isEmpty());

    }

    @Test
    public void hasAtMostXLowerCaseLetters() {
        var lessCase = new StringValidator("ABCab123$%&")
                .onInvalidate(DO_NOTHING)
                .hasAtMostXLowerCaseLetters(3);
        assertTrue(lessCase.isValid());
        assertTrue(lessCase.getErrors().isEmpty());

        var limitCase = new StringValidator("ABCabc123$%&")
                .onInvalidate(DO_NOTHING)
                .hasAtMostXLowerCaseLetters(3);
        assertTrue(limitCase.isValid());
        assertTrue(limitCase.getErrors().isEmpty());

        var moreCase = new StringValidator("ABCabcdef123$%&")
                .onInvalidate(DO_NOTHING)
                .hasAtMostXLowerCaseLetters(3);
        assertFalse(moreCase.isValid());
        assertEquals(moreCase.getErrors().size(), 1);

        var noneCase = new StringValidator("ABC123$%&")
                .onInvalidate(DO_NOTHING)
                .hasAtMostXLowerCaseLetters(3);
        assertTrue(noneCase.isValid());
        assertTrue(noneCase.getErrors().isEmpty());

    }

    @Test
    public void hasAtMostXDigits() {
        var lessCase = new StringValidator("ABCabc12$%&")
                .onInvalidate(DO_NOTHING)
                .hasAtMostXDigits(3);
        assertTrue(lessCase.isValid());
        assertTrue(lessCase.getErrors().isEmpty());

        var limitCase = new StringValidator("ABCabc123$%&")
                .onInvalidate(DO_NOTHING)
                .hasAtMostXDigits(3);
        assertTrue(limitCase.isValid());
        assertTrue(limitCase.getErrors().isEmpty());

        var moreCase = new StringValidator("ABCabc123456$%&")
                .onInvalidate(DO_NOTHING)
                .hasAtMostXDigits(3);
        assertFalse(moreCase.isValid());
        assertEquals(moreCase.getErrors().size(), 1);

        var noneCase = new StringValidator("ABCabc$%&")
                .onInvalidate(DO_NOTHING)
                .hasAtMostXDigits(3);
        assertTrue(noneCase.isValid());
        assertTrue(noneCase.getErrors().isEmpty());

    }

    @Test
    public void hasAtMostXSpecialCharacters() {
        var lessCase = new StringValidator("ABCabc123$&")
                .onInvalidate(DO_NOTHING)
                .hasAtMostXSpecialCharacters(3);
        assertTrue(lessCase.isValid());
        assertTrue(lessCase.getErrors().isEmpty());

        var limitCase = new StringValidator("ABCabc123$%&")
                .onInvalidate(DO_NOTHING)
                .hasAtMostXSpecialCharacters(3);
        assertTrue(limitCase.isValid());
        assertTrue(limitCase.getErrors().isEmpty());

        var moreCase = new StringValidator("ABCabc123$%¿?&")
                .onInvalidate(DO_NOTHING)
                .hasAtMostXSpecialCharacters(3);
        assertFalse(moreCase.isValid());
        assertEquals(moreCase.getErrors().size(), 1);

        var noneCase = new StringValidator("ABCabc123")
                .onInvalidate(DO_NOTHING)
                .hasAtMostXSpecialCharacters(3);
        assertTrue(noneCase.isValid());
        assertTrue(noneCase.getErrors().isEmpty());

    }

}
