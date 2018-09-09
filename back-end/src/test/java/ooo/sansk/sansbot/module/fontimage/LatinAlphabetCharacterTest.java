package ooo.sansk.sansbot.module.fontimage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LatinAlphabetCharacterTest {

    @Test
    public void testFromCharacter_Valid() {
        assertEquals(LatinAlphabetCharacter.A, LatinAlphabetCharacter.fromCharacter('A'));
        assertEquals(LatinAlphabetCharacter.Z, LatinAlphabetCharacter.fromCharacter('Z'));
    }

    @Test
    public void testFromCharacter_IndexTooLow() {
        assertEquals(LatinAlphabetCharacter.WHITESPACE, LatinAlphabetCharacter.fromCharacter((char) ('A' - 1)));
    }

    @Test
    public void testFromCharacter_IndexTooHigh() {
        assertEquals(LatinAlphabetCharacter.WHITESPACE, LatinAlphabetCharacter.fromCharacter((char) ('Z' + 1)));
    }
}