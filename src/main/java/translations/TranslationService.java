package translations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by Roy
 */
public class TranslationService {
    private final String endpoint = "https://api.google.com/translate";

    /*
    I was not sure what exactly was expected regarding the mock and expected printouts
    Added 200ms sleep to simulate delay
     */
    public String translate(String text, String inputLang, String outputLang) throws Exception {
        JSONObject json = new JSONObject();
        json.put("input_lang",inputLang);
        json.put("output_lang",outputLang);
        json.put("text",text);

        JSONObject responseJson = new JSONObject();
        responseJson.put("text",text);

        HttpPost request = new HttpPost(endpoint);
        StringEntity params = new StringEntity(json.toString());
        request.addHeader("content-type", "application/json");
        request.setEntity(params);
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse mockHttpResponse = mock(HttpResponse.class);
        HttpEntity httpEntity = mock(HttpEntity.class);
        when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream(responseJson.toString().getBytes(StandardCharsets.UTF_8)));
        when(mockHttpResponse.getEntity()).thenReturn(httpEntity);

        when(httpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);
        HttpResponse response = httpClient.execute(request);
        String responseJsonStr = EntityUtils.toString(response.getEntity());
        JSONObject responseJsonStrObj = new JSONObject(responseJsonStr);
        Thread.sleep(200);

        String translation = responseJsonStrObj.getString("text");
        return translation;
    }
}
