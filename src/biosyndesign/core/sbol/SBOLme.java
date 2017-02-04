package biosyndesign.core.sbol;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class SBOLme implements SBOLInterface{

    public Part[] findParts(int type, int data1, String data2) {
        StringBuffer result = new StringBuffer();
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("http://www.cbrc.kaust.edu.sa/sbolme/php/query.php");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("type", "0" + type));
            nvps.add(new BasicNameValuePair("data1", "" + data1));
            nvps.add(new BasicNameValuePair("data2", data2));
            nvps.add(new BasicNameValuePair("seq", ""));
            nvps.add(new BasicNameValuePair("page", "1"));
            nvps.add(new BasicNameValuePair("max", "25"));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                System.out.println(response.getStatusLine());
                HttpEntity entity = response.getEntity();
                Reader in = new BufferedReader(new InputStreamReader(entity.getContent()));
                for (int c; (c = in.read()) >= 0; )
                    result.append((char) c);
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
            httpclient.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObject jsonObject = new JsonParser().parse(result.toString()).getAsJsonObject();
        JsonArray a = jsonObject.getAsJsonArray("rows");
        Part[] parts = new Part[a.size()];
        for (int i = 0; i < a.size(); i++) {
            JsonObject o = a.get(i).getAsJsonObject();
            String name = "";
            if (o.has("Names")) {
                name = o.get("Names").getAsString();
            } else {
                name = o.get("Name").getAsString();
            }
            if (o.get("ID").getAsString().contains("R")) {
                double energy = 1000;
                if (o.has("Energy") && !o.get("Energy").isJsonNull()) {
                    energy = o.get("Energy").getAsDouble();
                }
                parts[i] = new Reaction(o.get("ID").getAsString(), name, o.get("URL").getAsString(), energy);

            } else {
                parts[i] = new Compound(o.get("ID").getAsString(), name, o.get("URL").getAsString());
            }
        }
        return parts;
    }

    public ECNumber findECNumber(String ECNumber) {
        StringBuffer result = new StringBuffer();
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("http://www.cbrc.kaust.edu.sa/sbolme/php/query.php");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("type", "2"));
            nvps.add(new BasicNameValuePair("data1", "0"));
            nvps.add(new BasicNameValuePair("data2", ECNumber));
            nvps.add(new BasicNameValuePair("seq", ""));
            nvps.add(new BasicNameValuePair("page", "1"));
            nvps.add(new BasicNameValuePair("max", "25"));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                System.out.println(response.getStatusLine());
                HttpEntity entity = response.getEntity();
                Reader in = new BufferedReader(new InputStreamReader(entity.getContent()));
                for (int c; (c = in.read()) >= 0; )
                    result.append((char) c);
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
            httpclient.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObject jsonObject = new JsonParser().parse(result.toString()).getAsJsonObject();
        JsonArray a = jsonObject.getAsJsonArray("rows");
        if (a.size() > 0) {
            ECNumber ec;
            JsonObject o = a.get(0).getAsJsonObject();
            ec = new ECNumber(o.get("ID").getAsString(), o.get("Title").getAsString(), o.get("URL").getAsString(), o.get("ECNumber").getAsString());
            return ec;
        } else {
            return null;
        }
    }

    public Protein[] getProteins(String organism, String ecNumber) {
        StringBuffer result = new StringBuffer();
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("http://www.cbrc.kaust.edu.sa/sbolme/php/query.php");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("type", "3"));
            nvps.add(new BasicNameValuePair("data1", organism));
            nvps.add(new BasicNameValuePair("data2", ecNumber));
            nvps.add(new BasicNameValuePair("seq", ""));
            nvps.add(new BasicNameValuePair("page", "1"));
            nvps.add(new BasicNameValuePair("max", "25"));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                System.out.println(response.getStatusLine());
                HttpEntity entity = response.getEntity();
                Reader in = new BufferedReader(new InputStreamReader(entity.getContent()));
                for (int c; (c = in.read()) >= 0; )
                    result.append((char) c);
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
            httpclient.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObject jsonObject = new JsonParser().parse(result.toString()).getAsJsonObject();
        JsonArray a = jsonObject.getAsJsonArray("rows");
        Protein[] p = new Protein[a.size()];
        for (int i = 0; i < a.size(); i++) {
            JsonObject o = a.get(i).getAsJsonObject();
            p[i] = new Protein(o.get("ID").getAsString(), organism, o.get("URL").getAsString(), ecNumber);
        }
        return p;
    }

    public Reaction[] findCompetingReactions(String organism, String compound) {
        StringBuffer result = new StringBuffer();
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("http://www.cbrc.kaust.edu.sa/sbolme/php/competing.php");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("compound", compound));
            nvps.add(new BasicNameValuePair("organism", organism));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                System.out.println(response.getStatusLine());
                HttpEntity entity = response.getEntity();
                Reader in = new BufferedReader(new InputStreamReader(entity.getContent()));
                for (int c; (c = in.read()) >= 0; )
                    result.append((char) c);
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
            httpclient.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObject jsonObject = new JsonParser().parse(result.toString()).getAsJsonObject();
        JsonArray a = jsonObject.getAsJsonArray("rows");
        int max = 10;
        if (a.size() < max) {
            max = a.size();
        }
        Reaction[] p = new Reaction[max];
        for (int i = 0; i < p.length; i++) {
            JsonObject o = a.get(i).getAsJsonObject();
            p[i] = new Reaction(o.get("ID").getAsString(), o.get("Name").getAsString(), o.get("URL").getAsString(), o.get("FreeEnergy").getAsDouble());
        }
        return p;
    }
}
