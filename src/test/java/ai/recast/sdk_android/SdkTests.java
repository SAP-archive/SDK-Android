package ai.recast.sdk_android;

import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.exceptions.misusing.UnfinishedStubbingException;
import org.mockito.runners.MockitoJUnitRunner;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SdkTests {
    static final String FAKE_JSON = "{" +
            "   \"results\": {" +
            "        \"source\": \"What is the weather in London tomorrow? And in Paris?\"," +
            "        \"intents\": [" +
            "                {" +
            "                    \"name\": \"weather\"," +
            "                    \"confidence\": 0.88" +
            "                }" +
            "            ]," +
            "        \"act\": \"wh-query\"," +
            "        \"type\": \"desc:desc\"," +
            "        \"sentiment\": \"neutral\"," +
            "        \"entities\": {" +
            "              \"action\": [" +
            "                  {" +
            "                      \"agent\": \"the weather in london\"," +
            "                      \"tense\": \"present\"," +
            "                      \"raw\": \"is\"," +
            "                      \"confidence\": 0.97" +
            "                  }" +
            "              ]," +
            "              \"location\": [" +
            "                  {" +
            "                     \"formated\": \"London, london, Greater London, England, United Kingdom\"," +
            "                     \"lng\": -0.1277583," +
            "                     \"lat\": 51.5073509," +
            "                     \"raw\": \"London\"," +
            "                     \"confidence\": 0.97" +
            "                  }," +
            "                  {" +
            "                     \"formated\": \"Paris, Paris, Ile-de-France, France\"," +
            "                     \"lng\": 2.3522219," +
            "                     \"lat\": 48.856614," +
            "                     \"raw\": \"Paris\"," +
            "                     \"confidence\": 0.83" +
            "                  }" +
            "              ]," +
            "              \"datetime\": [" +
            "                  {" +
            "                      \"value\": \"2016-07-11T10:00:00+00:00\"," +
            "                      \"raw\": \"tomorrow\"," +
            "                      \"confidence\": 0.83" +
            "                  }" +
            "              ]" +
            "        }," +
            "        \"language\": \"en\"," +
	    "        \"processing_language\": \"en\"," +
            "        \"version\": \"2.0.0\"," +
            "        \"timestamp\": \"\"," +
            "        \"uuid\": \"34b3f548-4aaf-4e3a-add1-f8f29f30e7fb\"," +
            "        \"status\": 200" +
            "   }," +
            "   \"message\": \"Requests rendered with success\"" +
            "}";

    static final String INVALID_JSON = "{" +
            "},";
    @Mock
    Client c = new Client("token", "en");

    @Test
    public void testResponseGetters() {
        when(c.textRequest("text")).thenReturn(new Response(FAKE_JSON));
        Response r = c.textRequest("text");

        assertTrue(r != null);
        assertTrue(r.getSource().equals("What is the weather in London tomorrow? And in Paris?"));
        assertTrue(r.getVersion().equals("2.0.0"));
        assertTrue(r.getRaw().equals(FAKE_JSON));

        assertTrue(r.getAct().equals(Response.ACT_WH_QUERY));
        assertTrue(r.getType().equals("desc:desc"));
        assertTrue(r.getSentiment().equals(Response.SENTIMENT_NEUTRAL));

        assertTrue(r.getEntities("location").length == 2);
        assertTrue(r.getEntities("datetime").length == 1);
        assertTrue(r.getEntities("fakename") == null);
        assertTrue(r.getEntity("fakename") == null);
        assertTrue(r.getStatus() == 200);
        assertTrue(r.getLanguage().equals("en"));
	assertTrue(r.getProcessingLanguage().equals("en"));
        assertTrue(r.getUuid().equals("34b3f548-4aaf-4e3a-add1-f8f29f30e7fb"));

        assertTrue(r.getIntents().length == 1);
    }

    @Test
    public void testResponseHelpers() {
        when(c.textRequest("text")).thenReturn(new Response(FAKE_JSON));
        Response r = c.textRequest("text");

        assertTrue(r.isWhQuery());

        assertTrue(r != null);
        assertFalse(r.isAssert());
        assertFalse(r.isCommand());
        assertFalse(r.isYesNoQuery());

        assertTrue(r.isDescription());

        assertFalse(r.isPositive());
        assertFalse(r.isNegative());
        assertTrue(r.isNeutral());
        assertFalse(r.isVeryPositive());
        assertFalse(r.isVeryNegative());

        assertFalse(r.isAbbreviation());
        assertFalse(r.isEntity());
        assertFalse(r.isHuman());
        assertFalse(r.isLocation());
        assertFalse(r.isNumber());
    }

    @Test
    public void testEntities() {
        when(c.textRequest("text")).thenReturn(new Response(FAKE_JSON));
        Response r = c.textRequest("text");

        Entity london = r.getEntity("location");
        assertTrue(london.getName().equals("location"));
        assertTrue((Double)london.getField("lng") == -0.1277583);
        assertTrue((Double)london.getField("lat") == 51.5073509);
        assertTrue(london.getConfidence() == 0.97);
    }

    @Test
    public void testIntent() {
        when(c.textRequest("text")).thenReturn(new Response(FAKE_JSON));
        Response r = c.textRequest("text");

        Intent i = r.getIntent();
        assertTrue(i.getConfidence() == 0.88);
        assertTrue(i.getName().equals("weather"));
    }
}
