package biosyndesign.core.sbol.local;


import biosyndesign.core.sbol.*;
import biosyndesign.core.sbol.parts.*;
import com.google.gson.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

import java.util.*;
import java.util.List;


public class LocalRepo implements SBOLInterface {

    private String protocol = "jdbc:derby:";
    private final String lp = System.getProperty("user.home") + File.separator + "BiosynDesign" + File.separator + "LocalParts";

    public void init() {
        Connection conn = null;
        ArrayList<Statement> statements = new ArrayList<Statement>();
        Statement s;
        ResultSet rs = null;
        try {
            Properties props = new Properties();
            String dbName = "sbol";
            conn = DriverManager.getConnection(protocol + dbName
                    + ";create=true", props);

            conn.setAutoCommit(false);
            s = conn.createStatement();
            statements.add(s);
            boolean first = true;
            try {
                rs = s.executeQuery("select * from compounds limit 1");
            } catch (Exception e) {
                first = true;
            }
            if (first) {
                Utils.dropSchema(conn.getMetaData(), "APP");
                String dbcreate = new Scanner(new File("db")).useDelimiter("\\Z").next();
                String batch[] = dbcreate.split(";");
                s = conn.createStatement();
                statements.add(s);
                for (String b : batch) {
                    s.addBatch(b);
                }
                s.executeBatch();
                conn.commit();
            }
            PreparedStatement psInsert;
            PreparedStatement psUpdate;
            psInsert = conn.prepareStatement(
                    "insert into compounds values (?,  ?, ?, ?, ?, ?)");
            statements.add(psInsert);

            psInsert.setString(1, "C0000000000000001");
            psInsert.setString(2, "2");
            psInsert.setString(3, "3");
            psInsert.setString(4, "4");
            psInsert.setString(5, "5");
            psInsert.setString(6, "6");
            psInsert.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
            } catch (SQLException sqle) {
            }
            int i = 0;
            while (!statements.isEmpty()) {
                Statement st = (Statement) statements.remove(i);
                try {
                    if (st != null) {
                        st.close();
                        st = null;
                    }
                } catch (SQLException sqle) {
                }
            }
            try {
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException sqle) {
            }
        }
        File p = new File(lp);
        if (!p.exists()) {
            p.mkdirs();
        }
    }

    public JsonArray execute(String sql) {
        JsonArray result = null;
        Connection conn = null;
        ArrayList<Statement> statements = new ArrayList<Statement>();
        Statement s;
        ResultSet rs = null;
        try {
            Properties props = new Properties();
            String dbName = "sbol";
            conn = DriverManager.getConnection(protocol + dbName
                    + ";create=true", props);

            s = conn.createStatement();
            statements.add(s);
            rs = s.executeQuery(sql);
            result = Utils.resultSetToJsonArray(rs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
            } catch (SQLException sqle) {
            }
            int i = 0;
            while (!statements.isEmpty()) {
                Statement st = (Statement) statements.remove(i);
                try {
                    if (st != null) {
                        st.close();
                        st = null;
                    }
                } catch (SQLException sqle) {
                }
            }
            try {
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException sqle) {
            }
        }
        return result;
    }

    public JsonArray execute2(String sql) {
        JsonArray result = null;
        Connection conn = null;
        ArrayList<Statement> statements = new ArrayList<Statement>();
        Statement s;
        ResultSet rs = null;
        try {
            Properties props = new Properties();
            String dbName = "sbol";
            conn = DriverManager.getConnection(protocol + dbName
                    + ";create=true", props);

            s = conn.createStatement();
            statements.add(s);
            s.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
            } catch (SQLException sqle) {
            }
            int i = 0;
            while (!statements.isEmpty()) {
                Statement st = (Statement) statements.remove(i);
                try {
                    if (st != null) {
                        st.close();
                        st = null;
                    }
                } catch (SQLException sqle) {
                }
            }
            try {
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException sqle) {
            }
        }
        return result;
    }

    @Override
    public Part[] findParts(int type, int filter, String value) {
        String sql = "SELECT ID, Name, URL, KeggID, DrugID FROM compounds AS c WHERE c.ID = '"+value+"'";
        JsonArray ja = execute(sql);
        JsonObject o = ja.get(0).getAsJsonObject();
        Part[] parts = new Part[1];
        parts[0] = new Compound(o.get("ID").getAsString(), o.get("NAME").getAsString(), o.get("URL").getAsString());
        return parts;
    }

    @Override
    public ECNumber findECNumber(String ECNumber) {
        return null;
    }

    @Override
    public Protein[] getProteins(String ecNumber) {
        return new Protein[0];
    }

    @Override
    public Reaction[] findCompetingReactions(String organism, String compound, int maxCompeting) {
        return new Reaction[0];
    }

    @Override
    public Reaction[] commonReactions(String id1, String id2) {
        return new Reaction[0];
    }

    @Override
    public boolean isNative(String reaction, String organism) {
        return false;
    }


    public void importParts() {
        String name = JOptionPane.showInputDialog("Choose name for the batch:");
        FileDialog fd = new FileDialog((Frame) null, "Choose Directory", FileDialog.LOAD);
        fd.setMultipleMode(true);
        fd.setVisible(true);
        if (fd.getFiles().length > 0) {
            File nd = new File(lp + File.separator + name);
            nd.mkdirs();
            for (File f : fd.getFiles()) {
                try {
                    File np = new File(lp + File.separator + name + File.separator + f.getName());
                    Files.copy(f.toPath(), np.toPath());
                    addPart(np, name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void deleteParts() {
        String[] choices = getBatches();
        String name = (String) JOptionPane.showInputDialog(null, "Choose batch",
                "Delete Parts", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
        //deleteBatch(name);
    }

    public void addPart(File f, String batchName) throws IOException, JDOMException {
        String xml = new String(Files.readAllBytes(f.toPath()));
        xml = xml.replaceAll(":", "");
        SAXBuilder jdomBuilder = new SAXBuilder();
        Document jdomDocument = jdomBuilder.build(new StringReader(xml));
        XPathFactory xFactory = XPathFactory.instance();
        Element root = jdomDocument.getRootElement().getChild("sbolComponentDefinition");
        String id = root.getChildText("sboldisplayId");
        String keggid = root.getChildText("compoundkegg_id");
        String drugID = root.getChild("compounddrug").getChild("druginformation").getAttributeValue("rdfabout");
        drugID = drugID.substring(drugID.length() - 7, drugID.length());
        XPathExpression<Element> expr = xFactory.compile("//compoundsynonym", Filters.element());
        List<Element> links = expr.evaluate(jdomDocument);
        String names ="";
        for (Element e : links) {
            names+=e.getText().replaceAll("'", "''") + " | ";
            //execute2("INSERT INTO compound_names(Compound,  Name) VALUES ('" + id + "','" + name + "')");
        }
        execute2("INSERT INTO compounds(ID, Name, KeggID, DrugID, URL) VALUES ('" + id + "','" + names +"','"+ keggid + "','" + drugID + "','" + f.getAbsolutePath() + "')");

    }

    public String[] getBatches() {
        return null;
    }

}