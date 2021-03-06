package biosyndesign.core.sbol;

import biosyndesign.core.Main;
import biosyndesign.core.sbol.parts.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SBOLme implements SBOLInterface {
    public String prefix;
    private HttpClient httpclient;
    private JsonObject foundParts;


    public SBOLme(String prefix) {
        this.prefix = prefix;
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(20 * 1000).setSocketTimeout(20 * 1000).setConnectionRequestTimeout(20 * 1000).build();
        httpclient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
    }


    public Part[] findParts(int type, int filter, String value, int page) {
        String result = null;
        try {
            HttpPost httpPost;
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            if (type == 0 && filter == 5) {
                httpPost = new HttpPost(prefix + "/php/Bd/exact.php");
                nvps.add(new BasicNameValuePair("smiles", value));
            } else {
                httpPost = new HttpPost(prefix + "/php/query.php");
                nvps.add(new BasicNameValuePair("type", "0" + type));
                nvps.add(new BasicNameValuePair("filter", "" + filter));
                nvps.add(new BasicNameValuePair("value", value));
                nvps.add(new BasicNameValuePair("page", Integer.toString(page + 1)));
                nvps.add(new BasicNameValuePair("max", "25"));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            result = getResponse(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
        foundParts = new JsonParser().parse(result.toString()).getAsJsonObject();
        JsonArray a = foundParts.getAsJsonArray("rows");
        Part[] parts = new Part[a.size()];
        for (int i = 0; i < a.size(); i++) {
            JsonObject o = a.get(i).getAsJsonObject();
            String name = "";
            if (o.has("Names")) {
                name = o.get("Names").getAsString();
            } else if (o.has("Title")) {
                name = o.get("Title").getAsString();
            } else if (o.has("Name")) {
                name = o.get("Name").getAsString();
            }
            if (type == 0) {
                parts[i] = new Compound(o.get("ID").getAsString(), name, o.get("URL").getAsString());
            } else if (type == 1) {
                double energy = 1000;
                if (o.has("Energy") && !o.get("Energy").isJsonNull()) {
                    energy = o.get("Energy").getAsDouble();
                }
                parts[i] = new Reaction(o.get("ID").getAsString(), name, o.get("URL").getAsString(), energy);
            } else if (type == 2) {
                parts[i] = new Enzyme(o.get("ID").getAsString(), o.get("Title").getAsString(), o.get("URL").getAsString(), o.get("ECNumber").getAsString());
            }
        }
        return parts;
    }

    public Protein[] getProteins(String enzyme) {
        return getProteins(enzyme, "");
    }

    public Protein[] getProteins(String enzyme, String organism) {
        String result = null;
        try {
            HttpPost httpPost = new HttpPost(prefix + "/php/query.php");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("type", "3"));
            nvps.add(new BasicNameValuePair("organism", organism));
            nvps.add(new BasicNameValuePair("ec_number", enzyme));
            nvps.add(new BasicNameValuePair("sequence", ""));
            nvps.add(new BasicNameValuePair("page", "1"));
            nvps.add(new BasicNameValuePair("max", "300"));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            result = getResponse(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObject jsonObject = new JsonParser().parse(result.toString()).getAsJsonObject();
        JsonArray a = jsonObject.getAsJsonArray("rows");
        Protein[] p = new Protein[a.size()];
        for (int i = 0; i < a.size(); i++) {
            JsonObject o = a.get(i).getAsJsonObject();
            p[i] = new Protein(o.get("ID").getAsString(), o.get("OrganismName").getAsString(), o.get("URL").getAsString(), enzyme);
        }
        return p;
    }

    public Reaction[] findCompetingReactions(String organism, String compound, int maxCompeting) {
        String result = null;
        try {
            HttpPost httpPost = new HttpPost(prefix + "/php/Bd/competing.php");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("compound", compound));
            nvps.add(new BasicNameValuePair("organism", organism));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            result = getResponse(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Reaction[] p = new Reaction[0];
        try {
            JsonObject jsonObject = new JsonParser().parse(result.toString()).getAsJsonObject();
            JsonArray a = jsonObject.getAsJsonArray("rows");
            int max = maxCompeting;
            if (a.size() < max) {
                max = a.size();
            }
            p = new Reaction[max];
            for (int i = 0; i < p.length; i++) {
                JsonObject o = a.get(i).getAsJsonObject();
                Reaction r = new Reaction(o.get("ID").getAsString(), o.get("Name").getAsString(), o.get("URL").getAsString(), o.get("FreeEnergy").getAsDouble());
                r.nat = true;
                p[i] = r;
            }
        } catch (Exception e) {

        }
        return p;
    }

    @Override
    public Reaction[] commonReactions(String id1, String id2) {
        String result = null;
        try {
            HttpPost httpPost = new HttpPost(prefix + "/php/Bd/common_reaction.php");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("compound1", id1));
            nvps.add(new BasicNameValuePair("compound2", id2));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            result = getResponse(httpPost);
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
            p[i] = new Reaction(o.get("ID").getAsString(), o.get("Name").getAsString(), o.get("URL").getAsString(), 1000); //o.get("FreeEnergy").getAsDouble()
        }
        return p;
    }

    @Override
    public boolean isNative(String reaction, String organism) {
        String result = null;
        try {
            HttpPost httpPost = new HttpPost(prefix + "/php/Bd/is_native.php");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("reaction", reaction));
            nvps.add(new BasicNameValuePair("organism", organism));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            result = getResponse(httpPost);
            JsonObject jsonObject = new JsonParser().parse(result.toString()).getAsJsonObject();
            return jsonObject.get("native").getAsBoolean();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return false;
    }

    @Override
    public String[] getOrganisms(String enzyme) {
        String result = null;
        try {
            HttpPost httpPost = new HttpPost(prefix + "/php/Bd/get_organisms.php");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("ec", enzyme));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            result = getResponse(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObject jsonObject = new JsonParser().parse(result.toString()).getAsJsonObject();
        JsonArray a = jsonObject.getAsJsonArray("rows");
        String options[] = new String[a.size()];
        for (int i = 0; i < a.size(); i++) {
            JsonObject o = a.get(i).getAsJsonObject();
            options[i] = o.get("OrganismName").getAsString();
        }
        return options;
    }

/*    @Override
    public String getCDNA(String sequence, String organism) {
        String result = null;
        try {
            HttpPost httpPost = new HttpPost(prefix + "/php/Bd/get_cdna.php");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("sequence", sequence));
            nvps.add(new BasicNameValuePair("organism", organism));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            result = getResponse(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObject jsonObject = new JsonParser().parse(result.toString()).getAsJsonObject();
        JsonArray a = jsonObject.getAsJsonArray("rows");
        return a.get(0).getAsJsonObject().get("CDS").getAsString();
    }*/

    public ArrayList<String> getZipAndReturnProteins(String reaction, String organism, String enzyme, String output) {
        ZipInputStream zin = null;
        ArrayList<String> prots = new ArrayList<>();
        try {
            HttpPost httpPost = new HttpPost(prefix + "/php/Bd/get_zip.php");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("reaction", reaction));
            nvps.add(new BasicNameValuePair("organism", organism));
            nvps.add(new BasicNameValuePair("ec_number", enzyme));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            HttpResponse response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                zin = new ZipInputStream(entity.getContent());
            }
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                FileOutputStream fout = new FileOutputStream(output + ze.getName());
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                if (ze.getName().length() - ze.getName().replace("_", "").length() == 3) {
                    prots.add(ze.getName());
                }
                zin.closeEntry();
                fout.close();
            }
            zin.close();
            EntityUtils.consume(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prots;
    }

    @Override
    public JsonObject getQueryInfo() {
        return foundParts;
    }

    private String getResponse(HttpPost httpPost) {
        StringBuffer result = new StringBuffer();
        try {
            HttpResponse response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            Reader in = new BufferedReader(new InputStreamReader(entity.getContent()));
            for (int c; (c = in.read()) >= 0; )
                result.append((char) c);
            EntityUtils.consume(entity);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Connection to the server failed please try again.");
            Main.mainWindow.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        return result.toString();
    }
}
