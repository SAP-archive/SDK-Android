package ai.recast.sdk_android;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testGetters() throws Exception {
        Client c = new Client("c695293367883d21229b95dd734540bd");
        assertTrue(c != null);
        Response r = c.textRequest("Hello! How are you ? Do you like apples?");
        Sentence[] se = r.getSentences();
        assertTrue(se.length == 3);
        Sentence s;

        s = se[0];
        assertTrue(s.getType() != null);
        assertTrue(s.getType().equals("assert"));
        assertTrue(s.getType().equals(Sentence.TYPE_ASSERT));
        assertTrue(s.getSource().equals("Hello!"));
        assertTrue(s.getPolarity().equals("positive"));
        assertTrue(s.getPolarity().equals(Sentence.POLARITY_POSITIVE));
        assertTrue(s.getAgent().equals(""));
        assertTrue(s.getAction().equals("hello"));

        assertTrue(s.getEntities().size() == 0);

        r = c.textRequest("Hello! How are you ? De we start at London or at Paris ?");
        se = r.getSentences();
        s = se[2];
        Entity[] ents = s.getEntities("start");
        assertTrue(ents.length == 2);
        assertTrue(ents[0].getName().equals("start"));
        assertTrue(((String)(ents[0].getField("value"))).equals("london"));
        assertTrue(ents[1].getName().equals("start"));
        assertTrue(((String)(ents[1].getField("value"))).equals("paris"));
    }

    @Test
    public void testResponse() {
        Client c = new Client("c695293367883d21229b95dd734540bd");
        assertTrue(c != null);
        Response r = c.textRequest("Hello !");

        assertTrue(r.getStatus() == 200);
        assertTrue(r.getIntent().equals("greetings"));
    }

    @Test
    public void testEntGetters() {
        Client c = new Client("c695293367883d21229b95dd734540bd");
        assertTrue(c != null);
        Response r = c.textRequest("How are you today?");

        assertTrue(r.getEntity("LOL") == null);
    }

    @Test
    public void testNoIntent() {
        Client c = new Client("c695293367883d21229b95dd734540bd");
        assertTrue(c != null);
        Response r = c.textRequest("adadasd");
        assertTrue(r.getIntent() == null);

    }

    @Test
    public void testNoEntities() {
        Client c = new Client("c695293367883d21229b95dd734540bd");
        assertTrue(c != null);
        Response r = c.textRequest("adadasd");
        assertTrue(r != null);
        assertTrue(r.getEntity("location") == null);
        assertTrue(r.getEntity("duration") == null);

    }

    @Test
    public void testSentenceGetters() {
        Client c = new Client("c695293367883d21229b95dd734540bd");
        assertTrue(c != null);
        Response r = c.textRequest("Hello! How are you ? I'm fine.");

        Sentence[] s = r.getSentences();
        assertTrue(s != null);
        assertTrue(s.length == 3);
        Sentence s2 = r.getSentence();
        assertTrue(s2 != null);
        assertTrue(s2 == s[0]);
    }

    @Test
    public void testEntitiesGetters() {
        Client c = new Client("c695293367883d21229b95dd734540bd");
        assertTrue(c != null);
        Response r = c.textRequest("Hello! How are you ? I'm fine. I live in London. What is the weather in Paris ?");
        assertTrue(r != null);

        //All entities
        Map<String, Entity[]> m = r.getEntities();
        Entity[] ent = m.get("ordinal");
        assertTrue(ent == null);
        for (Map.Entry<String, Entity[]> entry : m.entrySet()) {
            String name = entry.getKey();
            Entity[] ents = entry.getValue();
            assertTrue(name.equals("pronoun") || name.equals("start"));
            if (name.equals("pronoun")) {
                assertTrue(ents.length == 3);
            } else {
                assertTrue(ents.length == 2);
            }
        }

        //Entities by name
        ent = r.getEntities("pronoun");
        assertTrue(ent.length == 3);
        for (Entity e : ent) {
            assertTrue(e.getName().equals("pronoun"));
        }

        //Order
        assertTrue((Integer)(ent[0].getField("person")) == 2);
        assertTrue(((String)(ent[0].getField("raw"))).equals("you"));
        assertTrue(((String)(ent[0].getField("gender"))).equals("unknown"));

        assertTrue((Integer)(ent[1].getField("person")) == 1);
        assertTrue(((String)(ent[1].getField("raw"))).equals("I"));
        assertTrue(((String)(ent[1].getField("number"))).equals("singular"));

        assertTrue((Integer)(ent[2].getField("person")) == 1);
        assertTrue(((String)(ent[2].getField("raw"))).equals("I"));
        assertTrue(((String)(ent[2].getField("gender"))).equals("unkown"));
    }

    @Test
    public void testToken()
    {
        // Expecting HTTP auth failure
        Throwable ex = null;
        Client c = new Client();
        try {
            c.textRequest("LOL");
        } catch (Throwable e) {
            ex = e;
        }
        assertTrue(ex != null);

        //Expecting successful request
        ex = null;
        Response r = null;
        try {
            r = c.textRequest("LOL", "c695293367883d21229b95dd734540bd");
        } catch (Throwable e) {
            ex = e;
        }
        assertTrue(ex == null);
        assertTrue(r != null);

        // Expecting HTTP auth failure
        ex = null;
        try {
            r = c.textRequest("LOL", "YOLOSWAG");
        } catch (Throwable e) {
            ex = e;
        }
        assertTrue(ex != null);
        assertTrue(ex instanceof RecastException);
        assertTrue(((RecastException)ex).getStatusCode() == 401);
    }

    @Test
    public void testRequest() {

        Client c = new Client("c695293367883d21229b95dd734540bd");

        Response r = c.textRequest("How are you ?");
        assertTrue(r.getSource().equals("How are you ?"));
        assertTrue(r.getIntent().equals("greetings"));

        String[] intents = r.getIntents();
        assertTrue(intents.length == 1);
        assertTrue(intents[0].equals("greetings"));

        assertTrue(r.getStatus() == 200);

        assertTrue(r.getVersion().equals("0.1.4"));

        assertTrue(r.getEntity("bdajksdb") == null);

        assertTrue(r.getEntity("pronoun") != null);
    }


}