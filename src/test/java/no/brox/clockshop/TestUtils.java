package no.brox.clockshop;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TestUtils {
  String BASE_URL = "/";
  final OkHttpClient client = new OkHttpClient();
  private static final Logger logger = LoggerFactory.getLogger(TestUtils.class);

  public Response checkout(List<String> products) throws IOException {
    JSONArray json = new JSONArray();

    for (String product : products) {
      json.put(product);
    }

    RequestBody body = RequestBody.create(json.toString(),MediaType.parse("application/json"));

    Request request = new Request.Builder()
        .url(BASE_URL + "/checkout")
        .post(body)
        .build();

    Call call = client.newCall(request);
    Response response = call.execute();

    assertThat(response.code()).isEqualTo(200);

    return response;

  }
}
