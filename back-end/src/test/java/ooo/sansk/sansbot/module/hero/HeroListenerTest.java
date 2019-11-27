package ooo.sansk.sansbot.module.hero;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HeroListenerTest {

    private HeroListener heroListener;

    @Before
    public void setUp() throws Exception {
        heroListener = new HeroListener(null);
    }

    @Test
    public void findHeroName_JustMan() {
        String result = heroListener.findHeroName("man");
        assertNull(result);
    }

    @Test
    public void findHeroName_ShortSentence() {
        String result = heroListener.findHeroName("Ik begrijp helemaal niks van deze onzin man");
        assertEquals("Ik begrijp helemaal niks van deze onzin", result);
    }

    @Test
    public void findHeroName_MultipleMan_TrailingSentence() {
        String result = heroListener.findHeroName("Man man man, wat een drama zeg");
        assertEquals("Man man", result);
    }

    @Test
    public void findHeroName_MultipleMan_LeadingSentence() {
        String result = heroListener.findHeroName("Wat een drama zeg, Man man man.");
        assertEquals("Wat een drama zeg, Man man", result);
    }

    @Test
    public void findHeroName_MultipleSentence() {
        String result = heroListener.findHeroName("Wat een chaos. Hou eens op man ik word er gek van");
        assertEquals("Hou eens op", result);
    }

}