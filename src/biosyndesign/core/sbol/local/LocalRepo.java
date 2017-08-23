package biosyndesign.core.sbol.local;


import biosyndesign.core.sbol.*;
import biosyndesign.core.sbol.parts.*;
import biosyndesign.core.utils.Common;
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
            boolean first = false;
            try {
                s.setMaxRows(1);
                rs = s.executeQuery("select * from compounds");
            } catch (Exception e) {
                first = true;
            }
            if (first) {
                reset();

            }
//            PreparedStatement psInsert;
//            PreparedStatement psUpdate;
//            psInsert = conn.prepareStatement(
//                    "insert into compounds values (?,  ?, ?, ?, ?, ?)");
//            statements.add(psInsert);
//
//            psInsert.setString(1, "C0000000000000001");
//            psInsert.setString(2, "2");
//            psInsert.setString(3, "3");
//            psInsert.setString(4, "4");
//            psInsert.setString(5, "5");
//            psInsert.setString(6, "6");
//            psInsert.executeUpdate();
//            conn.commit();
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

    public JsonArray executeJSON(String sql) {
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
            s.execute(sql);
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
        return result;
    }

    @Override
    public Part[] findParts(int type, int filter, String value) {
        JsonArray ja;
        String sql = null, op = null;
        if ((type == 0 && filter == 4) || (type == 1 && filter == 2) || (type == 2 && filter == 0)) {
            if (Common.countMatches(value, ".") == 3) {
                op = "= '" + value + "'";
            } else {
                op = "LIKE '" + value + "%'";
            }
        } else if ((type == 0 && (filter == 0 || filter == 3)) || (type == 1 && filter == 1) || (type == 2 && filter == 1)) {
            ja = executeJSON("SELECT * FROM compounds WHERE ID = '" + value + "'");
            if (ja.size() == 0) {
                ja = executeJSON("SELECT * FROM compounds WHERE KeggID = '" + value + "'");
                if (ja.size() > 0) {
                    value = ja.get(0).getAsJsonObject().get("ID").getAsString();
                } else {
                    ja = executeJSON("SELECT Compound FROM compound_names WHERE Name='" + value + "'");
                    if (ja.size() > 0) {
                        value = ja.get(0).getAsJsonObject().get("COMPOUND").getAsString();
                    }
                }
            }

        } else if ((type == 0 && filter == 2) || (type == 1 && filter == 0) || (type == 2 && filter == 2)) {
            JsonArray ja1 = executeJSON("SELECT * FROM reactions WHERE ID = '" + value + "'");
            if (ja1.size() == 0) {
                ja1 = executeJSON("SELECT * FROM reactions WHERE KeggID = '" + value + "'");
                if (ja1.size() > 0) {
                    value = ja1.get(0).getAsJsonObject().get("ID").getAsString();
                }
            }
        }
        if (type == 0) {
            if (filter == 0) {
                sql = "SELECT ID, Name, URL, KeggID, DrugID FROM compounds AS c WHERE c.ID = '" + value + "'";
            } else if (filter == 1) {
                sql = "SELECT ID, Name, URL, KeggID, DrugID FROM compounds AS c WHERE c.DrugID = '" + value + "'";
            } else if (filter == 2) {
                sql = "SELECT ID, Name, URL, KeggID, DrugID FROM compounds AS c WHERE ID IN (SELECT Compound FROM reaction_compounds WHERE Reaction = '" + value + "')";
            } else if (filter == 3) {
                sql = "SELECT ID, Name, URL, KeggID, DrugID FROM compounds AS c INNER JOIN (SELECT rc.Compound FROM reaction_compounds AS rc  INNER JOIN reaction_compounds AS rc2 ON rc2.Reaction = rc.Reaction AND rc2.Compound =  '" + value + "' AND rc.Compound!='" + value + "') AS tt ON tt.Compound = c.ID";
            } else if (filter == 4) {
                sql = "SELECT ID, Name, URL, KeggID, DrugID FROM compounds AS c WHERE ID IN (SELECT Compound FROM reaction_compounds AS rc INNER JOIN reaction_enzymes AS re ON rc.Reaction = re.Reaction AND re.Enzyme " + op + " )";
            } else if (filter == 5) {
                sql = "SELECT ID, Name, URL, KeggID, DrugID FROM compounds AS c WHERE SMILES = " + value;
            }
        } else if (type == 1) {
            if (filter == 0) {
                sql = "SELECT r.ID, r.URL, r.KeggID, r.Name FROM reactions AS r WHERE ID = '" + value + "'";
            } else if (filter == 1) {
                sql = "SELECT r.ID, r.URL, r.KeggID, r.Name FROM reactions AS r INNER JOIN reaction_compounds AS rc ON r.ID = rc.Reaction AND rc.Compound='" + value + "'";
            } else if (filter == 2) {
                sql = "SELECT r.ID, r.URL, r.KeggID, r.Name FROM reactions AS r INNER JOIN reaction_enzymes AS re ON r.ID = re.Reaction AND re.Enzyme " + op;
            }
        } else if (type == 2) {
            if (filter == 0) {
                sql = "SELECT e.ID, e.URL, e.ECNumber, e.Title FROM ecnum AS e WHERE ECNumber " + op;
            } else if (filter == 1) {
                sql = "SELECT e.ID, e.URL, e.ECNumber, e.Title FROM ecnum AS e INNER JOIN (SELECT * FROM compund_enzymes WHERE Compound = '" + value + "') AS ce ON e.ECNumber = ce.Enzyme";
            } else if (filter == 2) {
                sql = "SELECT e.ID, e.URL, e.ECNumber, e.Title FROM ecnum AS e INNER JOIN (SELECT * FROM reaction_enzymes WHERE Reaction = '" + value + "') AS re ON e.ECNumber = re.Enzyme";
            }
        }

        ja = executeJSON(sql);
        if (ja != null && ja.size() > 0) {
            JsonObject o = ja.get(0).getAsJsonObject();
            Part[] parts = new Part[ja.size()];
            for (int i = 0; i < parts.length; i++) {
                Part p = null;
                if (type == 0) {
                    p = new Compound(o.get("ID").getAsString(), o.get("NAME").getAsString(), o.get("URL").getAsString());
                } else if (type == 1) {
                    double energy = 1000;
                    if (o.has("ENERGY") && !o.get("ENERGY").isJsonNull()) {
                        energy = o.get("ENERGY").getAsDouble();
                    }
                    p = new Reaction(o.get("ID").getAsString(), o.get("NAME").getAsString(), o.get("URL").getAsString(), energy);
                } else if (type == 2) {
                    p = new Part(o.get("ID").getAsString(), o.get("NAME").getAsString(), o.get("URL").getAsString());
                }
                p.local = true;
                parts[i] = p;
            }
            return parts;
        }
        return null;
    }

    @Override
    public Protein[] getProteins(String ECNumber) {
        JsonArray a = executeJSON("SELECT p.ID, p.URL, p.OrganismID, p.OrganismName, p.ECNumber FROM proteins AS p WHERE p.ECNumber = '" + ECNumber + "'");
        Protein[] p = new Protein[a.size()];
        for (int i = 0; i < a.size(); i++) {
            JsonObject o = a.get(i).getAsJsonObject();
            p[i] = new Protein(o.get("ID").getAsString(), o.get("OrganismName").getAsString(), o.get("URL").getAsString(), ECNumber);
        }
        return p;
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
        JsonArray a = executeJSON("SELECT * FROM reaction_enzymes AS re INNER JOIN  proteins AS p ON re.Reaction = '" + reaction + "' AND p.OrganismName = '" + organism + "' AND re.Enzyme = p.ECNumber");
        if (a != null && a.size() > 0) {
            return true;
        }
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
        String url = f.getAbsolutePath();
        SAXBuilder jdomBuilder = new SAXBuilder();
        Document jdomDocument = jdomBuilder.build(new StringReader(xml));
        XPathFactory xFactory = XPathFactory.instance();
        Element root = jdomDocument.getRootElement();
        if (root.getChild("sbolComponentDefinition") != null) {
            Element definition = root.getChild("sbolComponentDefinition");
            String id = definition.getChildText("sboldisplayId");
            String name = definition.getChildText("dctermstitle").replaceAll("'", "''");
            if (definition.getChild("compoundsynonym") != null) {
                //Compound
                String keggid = definition.getChildText("compoundkegg_id");
                String drugID = definition.getChild("compounddrug").getChild("druginformation").getAttributeValue("rdfabout");
                drugID = drugID.substring(drugID.length() - 7, drugID.length());
                String SMILES = "NULL";
                if (root.getChild("sbolSequence") != null) {
                    SMILES = "'" + root.getChild("sbolSequence").getChildText("sbolelements") + "'";
                }
                XPathExpression<Element> expr = xFactory.compile("//compoundsynonym", Filters.element());
                List<Element> links = expr.evaluate(jdomDocument);
                for (Element e : links) {
                    String synonym = e.getText().replaceAll("'", "''");
                    execute("INSERT INTO compound_names(Compound,  Name) VALUES ('" + id + "','" + synonym + "')");
                }
                execute("INSERT INTO compounds(ID, Name, KeggID, DrugID, URL, SMILES) VALUES ('" + id + "','" + name + "','" + keggid + "','" + drugID + "','" + url + "'," + SMILES + ")");
            } else if (definition.getChild("ecnumid") != null) {
                //EC Number
                String ECNumber = definition.getChildText("ecnumid");
                execute("INSERT INTO ecnum(ID, ECNumber, Title, URL) VALUES ('" + id + "','" + ECNumber + "','" + name + "','" + url + "')");
            } else {
                //Protein
                String oID = definition.getChildText("organismkegg_id");
                String oName = definition.getChildText("organismname");
                String ECNumber = definition.getChildText("ecnumid");
                String seq = root.getChild("sbolSequence").getChildText("sbolelements");
                execute("INSERT INTO proteins(ID,  OrganismID, OrganismName, ECNumber, URL, Sequence) VALUES ('" + id + "','" + oID + "','" + oName + "','" + ECNumber + "','" + url + "','" + seq + "')");
            }
        } else {
            //Reaction
            Element definition = root.getChild("sbolModuleDefinition");
            String id = definition.getChildText("sboldisplayId");
            String name = definition.getChildText("dctermstitle").replaceAll("'", "''");
            String keggid = definition.getChildText("reactionkegg_id");
            ArrayList<String> enzymes = new ArrayList();
            ArrayList<String> compounds = new ArrayList();
            XPathExpression<Element> expr = xFactory.compile("//sbolfunctionalComponent", Filters.element());
            List<Element> links = expr.evaluate(definition);
            for (Element e : links) {
                Element fc = e.getChild("sbolFunctionalComponent");
                String fcName = fc.getChildText("sboldisplayId");
                if (fcName.endsWith("enzyme")) {
                    enzymes.add(fc.getChildText("ecnumid"));
                } else {
                    compounds.add(fcName.substring(0, fcName.lastIndexOf("_")));
                }
            }
            execute("INSERT INTO reactions(ID, KeggID, URL, Name, Energy) VALUES ('" + id + "','" + keggid + "','" + url + "','" + name + "', '0')");
            for (String e : enzymes) {
                execute("INSERT INTO reaction_enzymes(Reaction,  Enzyme) VALUES ('" + id + "','" + e + "')");
                for (String c : compounds) {
                    execute("INSERT INTO compound_enzymes(Compound,  Enzyme) VALUES ('" + c + "','" + e + "')");
                }
            }
            for (String c : compounds) {
                execute("INSERT INTO reaction_compounds(Reaction,  Compound) VALUES ('" + id + "','" + c + "')");
            }
        }
    }

    public String[] getBatches() {
        return null;
    }

    public void reset() {
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
    }
}