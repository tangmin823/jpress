import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonToObj {
    public static void main(String[] args) {
        StringBuilder result = new StringBuilder();
        File file = new File("D:\\json.txt");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String s = null;
            while ((s = br.readLine()) != null) {
                result.append(System.lineSeparator() + s);
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        JSONObject obj = JSONObject.parseObject(result.toString());

        Map<String, Object> map = (Map<String, Object>) obj.get("urlset");
        List<Map<String, String>> list = (List<Map<String, String>>) map.get("url");

        StringBuilder urlJoins = new StringBuilder();
        for (Map<String, String> urlMap : list) {
            urlJoins.append(urlMap.get("loc")).append("\n");
        }
        urlJoins.deleteCharAt(urlJoins.length() - 1);

        PoolingHttpClientConnectionManager phcm = new PoolingHttpClientConnectionManager();
        phcm.setMaxTotal(2);
        phcm.setDefaultMaxPerRoute(2);
        phcm.setValidateAfterInactivity(5000);
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(phcm).build();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();
        HttpPost httpPost = new HttpPost("http://data.zz.baidu.com/urls?appid=1592273023116351&token=xjQnMh9yoPX9z15i&type=batch");
        httpPost.setConfig(requestConfig);
        try {
            httpPost.setEntity(new StringEntity(urlJoins.toString()));
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            String response = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            System.out.println(response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
