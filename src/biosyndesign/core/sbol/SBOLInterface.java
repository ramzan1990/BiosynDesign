package biosyndesign.core.sbol;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.*;
import java.util.*;

public class SBOLInterface {

    public Part[] findCompound(int type, int data1, String data2) {
        StringBuffer result = new StringBuffer();
        try {
            URL url = new URL("http://www.cbrc.kaust.edu.sa/sbolme/php/query.php");
            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put("type", type);
            params.put("data1", data1);
            params.put("data2", data2);
            params.put("seq", "");
            params.put("page", "1");
            params.put("max", "25");

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);

            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            for (int c; (c = in.read()) >= 0; )
                result.append((char) c);
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
            if(o.get("ID").getAsString().contains("R")){
                parts[i] = new Reaction(o.get("ID").getAsString(), name, o.get("URL").getAsString());

            }else{
                parts[i] = new Compound(o.get("ID").getAsString(), name, o.get("URL").getAsString());
            }
        }
        return parts;
    }

    public ECNumber findECNumber(String ECNumber) {
        StringBuffer result = new StringBuffer();
        try {
            URL url = new URL("http://www.cbrc.kaust.edu.sa/sbolme/php/query.php");
            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put("type", 2);
            params.put("data1", 0);
            params.put("data2", ECNumber);
            params.put("seq", "");
            params.put("page", "1");
            params.put("max", "25");

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);

            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            for (int c; (c = in.read()) >= 0; )
                result.append((char) c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObject jsonObject = new JsonParser().parse(result.toString()).getAsJsonObject();
        JsonArray a = jsonObject.getAsJsonArray("rows");
        ECNumber ec;
        JsonObject o = a.get(0).getAsJsonObject();
        ec = new ECNumber(o.get("ID").getAsString(), o.get("Title").getAsString(), o.get("URL").getAsString(), o.get("ECNumber").getAsString());
        return ec;
    }

    public Protein[] getProteins(String organism, String ecNumber) {
        StringBuffer result = new StringBuffer();
        try {
            URL url = new URL("http://www.cbrc.kaust.edu.sa/sbolme/php/query.php");
            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put("type", "3");
            params.put("data1", organism);
            params.put("data2", ecNumber);
            params.put("seq", "");
            params.put("page", "1");
            params.put("max", "25");

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);

            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            for (int c; (c = in.read()) >= 0; )
                result.append((char) c);
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
}
