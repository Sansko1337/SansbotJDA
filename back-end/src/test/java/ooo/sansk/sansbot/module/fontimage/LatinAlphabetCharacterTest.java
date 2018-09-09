package ooo.sansk.sansbot.module.fontimage;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class LatinAlphabetCharacterTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testFromCharacter_Valid() {
        assertEquals(LatinAlphabetCharacter.A, LatinAlphabetCharacter.fromCharacter('A'));
        assertEquals(LatinAlphabetCharacter.Z, LatinAlphabetCharacter.fromCharacter('Z'));
    }

    @Test
    public void testFromCharacter_IndexTooLow() {
        expectedException.expect(IllegalArgumentException.class);
        LatinAlphabetCharacter.fromCharacter((char) ('A' - 1));
    }

    @Test
    public void testFromCharacter_IndexTooHigh() {
        expectedException.expect(IllegalArgumentException.class);
        LatinAlphabetCharacter.fromCharacter((char) ('Z' + 1));
    }
}