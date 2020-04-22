package bitongchong;

import com.bitongchong.notebook.util.InputStreamToString;
import lombok.SneakyThrows;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.common.Strings;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author liuyuehe
 * @date 2020/4/21 16:58
 */
public class ElasticDemo {
    private static final RestHighLevelClient client;
    static {
        client = new RestHighLevelClient(RestClient.builder(
                new HttpHost("106.54.55.61", 9200, "http")
        ));
    }
    public static void restClientDemo() throws InterruptedException, IOException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        RestClient client = RestClient.builder(new HttpHost("106.54.55.61", 9200, "http"))
                .build();
        Request request = new Request("GET", "/");
        request.addParameter("pretty", "true");
        // 为HttpEntity指定的ContentType很重要，因为它将用于设置Content-Type标头，以便Elasticsearch可以正确解析内容。
        // 也可以不对其设置格式头，直接传入字符串，会传入一个默认的ContentType --> application/json
        // 例如：request.setJsonEntity("{\"json\":\"text\"}");
        request.setEntity(new NStringEntity(
                "{\"json\":\"text\"}",
                ContentType.APPLICATION_JSON));
        Response response = client.performRequest(request);
        System.out.println("这是同步响应： " + response.toString());
        client.performRequestAsync(request, new ResponseListener() {
            @SneakyThrows
            @Override
            public void onSuccess(Response response) {
                countDownLatch.countDown();
                InputStream inputStream = response.getEntity().getContent();
                String responseStr = InputStreamToString.parse(inputStream, 1024);
                System.out.println("这是异步响应： " + responseStr);
            }

            @Override
            public void onFailure(Exception e) {
                System.out.println("something is wrong!");
            }
        });
        countDownLatch.await();
        client.close();
    }

    public static void highRestClient() throws IOException, InterruptedException {
        Map<String, Object> jsonMap = new HashMap<>(16);
        for (int i = 101; i < 200; i++) {
            jsonMap.put("name", "tom" + i);
            jsonMap.put("time", new Date());
            jsonMap.put("msg", "this is the " + i + " message!");
            IndexRequest request = new IndexRequest("post").id(String.valueOf(i)).source(jsonMap);
            Thread.sleep(1000);
            client.index(request, RequestOptions.DEFAULT);
        }
    }

    public static void getTest() throws IOException {
        GetRequest getRequest = new GetRequest(
                "post",
                "1");
//        String[] includes = Strings.EMPTY_ARRAY;
//        String[] excludes = new String[]{"message"};
//        FetchSourceContext fetchSourceContext =
//                new FetchSourceContext(true, includes, excludes);
//        GetRequest request = getRequest.fetchSourceContext(fetchSourceContext);
        GetResponse fields = client.get(getRequest, RequestOptions.DEFAULT);
        Map<String, Object> source = fields.getSource();
        source.forEach((key, value) -> System.out.println("the key is :" + key + " and the value is :" + value));
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        highRestClient();
        Thread.sleep(2000);
        // getTest();
        client.close();
    }
}
