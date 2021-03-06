package biosyndesign.core.sbol.local;


import biosyndesign.core.sbol.*;
import biosyndesign.core.sbol.parts.*;
import biosyndesign.core.utils.Common;
import com.google.gson.*;
import org.apache.commons.io.FileUtils;
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
    public String dbName;
    private ArrayList<String> dbNames;
    static int sc = 0;
    public int totalRows = 0;
    private String prevSQL;
    public int maxRowsPage = 50;
    private int count = 0;

    public void init() {
        Connection conn = null;
        ArrayList<Statement> statements = new ArrayList<Statement>();
        Statement s;
        ResultSet rs = null;
        try {
            Properties props = new Properties();
            conn = DriverManager.getConnection(protocol + "default"
                    + ";create=true", props);

            conn.setAutoCommit(false);
            s = conn.createStatement();
            statements.add(s);
            boolean first = false;
            try {
                rs = s.executeQuery("select * from dblist");
                if (!rs.next()) {
                    first = true;
                }
            } catch (Exception e) {
                first = true;
            }
            dbNames = new ArrayList<>();
            if (first) {
                try {
                    s.execute("drop table dblist");
                } catch (Exception e) {
                    System.out.println("no dblist");
                }
                try {
                    s.execute("drop table cdb");
                } catch (Exception e) {
                    System.out.println("no cdb");
                }
                s.addBatch("CREATE TABLE dblist (\n" +
                        "  name varchar(300)\n" +
                        ")");
                s.addBatch("CREATE TABLE cdb (\n" +
                        "  name varchar(300)\n" +
                        ")");
                s.executeBatch();
                conn.commit();
            } else {
                while (rs.next()) {
                    dbNames.add(rs.getString("name"));
                }
                rs = s.executeQuery("select * from cdb");
                if (rs.next()) {
                    dbName = rs.getString("name");
                } else {
                    if (dbNames.size() > 0) {
                        dbName = dbNames.get(0);
                    } else {
                        dbName = null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.close(rs, statements, conn);
        }
        File p = new File(lp);
        if (!p.exists()) {
            p.mkdirs();
        }
    }

    public void checkDB() {
        Connection conn = null;
        ArrayList<Statement> statements = new ArrayList<Statement>();
        Statement s;
        ResultSet rs = null;
        try {
            Properties props = new Properties();
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
                resetDB();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.close(rs, statements, conn);
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
            conn = DriverManager.getConnection(protocol + dbName
                    + ";create=true", props);

            s = conn.createStatement();
            statements.add(s);
            rs = s.executeQuery(sql);
            totalRows = 0;
            while (rs.next()) {
                totalRows++;
            }
            prevSQL = sql;
            s = conn.createStatement();
            s.setMaxRows(maxRowsPage);
            statements.add(s);
            rs = s.executeQuery(sql);
            result = Utils.resultSetToJsonArray(rs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.close(rs, statements, conn);
        }
        return result;
    }

    public JsonArray getJSON(int page) {
        JsonArray result = null;
        Connection conn = null;
        ArrayList<Statement> statements = new ArrayList<Statement>();
        Statement s;
        ResultSet rs = null;
        try {
            Properties props = new Properties();
            conn = DriverManager.getConnection(protocol + dbName
                    + ";create=true", props);

            s = conn.createStatement();
            statements.add(s);
            rs = s.executeQuery(prevSQL + " offset " + (page - 1) * maxRowsPage + " rows fetch first " + maxRowsPage + " rows only");
            result = Utils.resultSetToJsonArray(rs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.close(rs, statements, conn);
        }
        return result;
    }

    public Part[] getPage(int page, int type) {
        JsonArray ja = getJSON(page);
        if (ja != null && ja.size() > 0) {
            Part[] parts = new Part[ja.size()];
            for (int i = 0; i < parts.length; i++) {
                JsonObject o = ja.get(i).getAsJsonObject();
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
                    p = new Enzyme(o.get("ID").getAsString(), o.get("NAME").getAsString(), o.get("URL").getAsString(), o.get("ClassID").getAsString());
                } else if (type == 3) {
                    p = new Protein(o.get("ID").getAsString(), o.get("ORGANISMNAME").getAsString(), o.get("URL").getAsString(), o.get("ENZYME").getAsString());
                }
                p.local = true;
                parts[i] = p;
            }
            return parts;
        }
        return null;
    }

    public JsonArray execute(String sql) {
        JsonArray result = null;
        Connection conn = null;
        ArrayList<Statement> statements = new ArrayList<Statement>();
        Statement s;
        ResultSet rs = null;
        try {
            Properties props = new Properties();
            conn = DriverManager.getConnection(protocol + dbName
                    + ";create=true", props);

            s = conn.createStatement();
            statements.add(s);
            s.execute(sql);
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.close(rs, statements, conn);
        }
        return result;
    }

    @Override
    public Part[] findParts(int type, int filter, String value, int page) {
        page += 1;
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
                sql = "SELECT e.ID, e.URL, e.ClassID, e.Name FROM enzymes AS e WHERE ClassID " + op;
            } else if (filter == 1) {
                sql = "SELECT e.ID, e.URL, e.ClassID, e.Name FROM enzymes AS e INNER JOIN (SELECT * FROM compund_enzymes WHERE Compound = '" + value + "') AS ce ON e.ClassID = ce.Enzyme";
            } else if (filter == 2) {
                sql = "SELECT e.ID, e.URL, e.ClassID, e.Name FROM enzymes AS e INNER JOIN (SELECT * FROM reaction_enzymes WHERE Reaction = '" + value + "') AS re ON e.ClassID = re.Enzyme";
            }
        } else if (type == 3) {
            if (filter == 0) {
                sql = "SELECT p.ID, p.OrganismName, p.Enzyme, URL FROM proteins AS p WHERE p.ID ='" + value + "'";
            } else if (filter == 1) {
                sql = "SELECT p.ID, p.OrganismName, p.Enzyme, URL FROM proteins AS p WHERE p.Enzyme ='" + value + "'";
            } else if (filter == 2) {
                sql = "SELECT p.ID, p.OrganismName, p.Enzyme, URL FROM proteins AS p WHERE p.OrganismName ='" + value + "'";
            }
        }
        ja = executeJSON(sql + " offset " + (page - 1) * maxRowsPage + " rows fetch first " + maxRowsPage + " rows only");
        if (ja != null && ja.size() > 0) {
            Part[] parts = new Part[ja.size()];
            for (int i = 0; i < parts.length; i++) {
                JsonObject o = ja.get(i).getAsJsonObject();
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
                    p = new Enzyme(o.get("ID").getAsString(), o.get("NAME").getAsString(), o.get("URL").getAsString(), o.get("ClassID").getAsString());
                } else if (type == 3) {
                    p = new Protein(o.get("ID").getAsString(), o.get("ORGANISMNAME").getAsString(), o.get("URL").getAsString(), o.get("ENZYME").getAsString());
                }
                p.local = true;
                parts[i] = p;
            }
            return parts;
        }
        return null;
    }

    @Override
    public Protein[] getProteins(String enzyme) {
        JsonArray a = executeJSON("SELECT p.ID, p.URL, p.OrganismID, p.OrganismName, p.Enzyme FROM proteins AS p WHERE p.Enzyme = '" + enzyme + "'");
        Protein[] p = new Protein[a.size()];
        for (int i = 0; i < a.size(); i++) {
            JsonObject o = a.get(i).getAsJsonObject();
            p[i] = new Protein(o.get("ID").getAsString(), o.get("ORGANISMNAME").getAsString(), o.get("URL").getAsString(), enzyme);
        }
        return p;
    }

    @Override
    public Protein[] getProteins(String enzyme, String organism) {
        JsonArray a = executeJSON("SELECT p.ID, p.URL, p.OrganismID, p.OrganismName, p.Enzyme FROM proteins AS p WHERE p.Enzyme = '" + enzyme + "' AND p.OrganismName = '" + organism + "'");
        Protein[] p = new Protein[a.size()];
        for (int i = 0; i < a.size(); i++) {
            JsonObject o = a.get(i).getAsJsonObject();
            p[i] = new Protein(o.get("ID").getAsString(), o.get("OrganismName").getAsString(), o.get("URL").getAsString(), enzyme);
        }
        return p;
    }

    @Override
    public Reaction[] findCompetingReactions(String organism, String compound, int maxCompeting) {
        return new Reaction[0];
    }

    @Override
    public Reaction[] commonReactions(String id1, String id2) {
        JsonArray a = executeJSON("SELECT r.ID, r.URL, r.Name FROM reactions AS r WHERE EXISTS" +
                " (SELECT NULL FROM reaction_compounds AS rc WHERE rc.Reaction = r.ID AND rc.Compound = '" + id1 + "')" +
                " AND EXISTS (SELECT NULL FROM reaction_compounds AS rc WHERE rc.Reaction = r.ID AND rc.Compound = '" + id2 + "')");
        Reaction[] r = new Reaction[a.size()];
        for (int i = 0; i < a.size(); i++) {
            JsonObject o = a.get(i).getAsJsonObject();
            r[i] = new Reaction(o.get("ID").getAsString(), o.get("NAME").getAsString(), o.get("URL").getAsString(), 0);
        }
        return r;
    }

    @Override
    public boolean isNative(String reaction, String organism) {
        JsonArray a = executeJSON("SELECT * FROM reaction_enzymes AS re INNER JOIN  proteins AS p ON re.Reaction = '" + reaction + "' AND p.OrganismName = '" + organism + "' AND re.Enzyme = p.Enzyme");
        if (a != null && a.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public String[] getOrganisms(String enzyme) {
        JsonArray a = executeJSON("SELECT DISTINCT o.Name AS OrganismName FROM (SELECT OrganismID FROM proteins WHERE ECNumber = 'enzyme') AS p JOIN organisms AS o ON p.OrganismID = o.ID");
        String[] organisms = new String[a.size()];
        for (int i = 0; i < a.size(); i++) {
            JsonObject o = a.get(i).getAsJsonObject();
            organisms[i] = o.get("ORGANISMNAME").getAsString();
        }
        return organisms;
    }


    @Override
    public ArrayList<String> getZipAndReturnProteins(String reaction, String organism, String enzyme, String output) {
        return null;
    }

    @Override
    public JsonObject getQueryInfo() {
        JsonObject ob = new JsonObject();
        ob.add("count", new JsonPrimitive(Math.ceil((double) totalRows / maxRowsPage)));
        ob.add("total", new JsonPrimitive(totalRows));
        return ob;
    }

    public void createDB(String name) {
        dbNames.add(name);
        dbName = name;
        changeDBList(name, true);
        resetDB();
    }

    public void importParts(String name, JLabel status) {
        changeDBList(name, true);
        checkDB();
        count = 0;
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setMultiSelectionEnabled(false);
        fc.showDialog(null, "Import");
        if (fc.getSelectedFile() != null) {
            File nd = new File(lp + File.separator + name);
            nd.mkdirs();
            List<File> files = new ArrayList<>();
            listFiles(fc.getSelectedFile(), files);
            for (File f : files) {
                try {
                    File np = new File(lp + File.separator + name + File.separator + f.getName());
                    try {
                        if (np.exists()) {
                            np.delete();
                        }
                        Files.copy(f.toPath(), np.toPath());
                    } catch (Exception e) {

                    }
                    addPart(np);
                    try {
                        Files.copy(f.toPath(), np.toPath());
                    } catch (Exception e) {

                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            status.setText("Added " + ++count + " parts");
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                status.setText("Finished. Added " + count + " parts");
            }
        });
    }

    public void listFiles(File directory, List<File> files) {
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                listFiles(file, files);
            }
        }
    }

    public void importCustomPart(File f) {
        try {
            File nd = new File(lp + File.separator + "custom");
            nd.mkdirs();
            File np = new File(lp + File.separator + "custom" + File.separator + f.getName());
            Files.copy(f.toPath(), np.toPath());
            addPart(np);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteDataset(String name) {
        try {
            FileUtils.deleteDirectory(new File(lp + File.separator + name));
            changeDBList(name, false);
            dbNames.remove(name);
            try {
                DriverManager.getConnection("jdbc:derby:" + name + ";shutdown=true");
            } catch (Exception e) {
            }
            FileUtils.deleteDirectory(new File(name));
            setCurrentDataset(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeDBList(String name, boolean b) {
        Connection conn = null;
        ArrayList<Statement> statements = new ArrayList<Statement>();
        Statement s;
        ResultSet rs = null;
        try {
            Properties props = new Properties();
            conn = DriverManager.getConnection(protocol + "default"
                    + ";create=true", props);

            conn.setAutoCommit(false);
            s = conn.createStatement();
            statements.add(s);
            try {
                if (b) {
                    s.execute("insert into dblist values ('" + name + "') ");
                } else {
                    s.execute("delete from dblist where name = '" + name + "'");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.close(rs, statements, conn);
        }
    }

    public void addPart(File f) throws IOException, JDOMException {
        String xml = new String(Files.readAllBytes(f.toPath()));
        xml = xml.replaceAll(":", "");
        String url = f.getAbsolutePath();
        SAXBuilder jdomBuilder = new SAXBuilder();
        Document jdomDocument = null;
        jdomDocument = jdomBuilder.build(new StringReader(xml));
        XPathFactory xFactory = XPathFactory.instance();
        Element root = jdomDocument.getRootElement();
        if (root.getChild("sbolComponentDefinition") != null) {
            Element definition = root.getChild("sbolComponentDefinition");
            String id = definition.getChildText("sboldisplayId");
            String name;
            if (definition.getChildText("dctermstitle") != null) {
                name = definition.getChildText("dctermstitle").replaceAll("'", "''");
            } else {
                name = id;
            }
            if (definition.getAttributeValue("rdfabout").contains("/parts/compound")) {
                //Compound
                String keggid = "";
                if (definition.getChild("compoundkegg_id") != null) {
                    keggid = definition.getChildText("compoundkegg_id");
                }
                String drugID = "";
                try {
                    drugID = definition.getChild("compounddrug").getChild("druginformation").getAttributeValue("rdfabout");
                    drugID = drugID.substring(drugID.length() - 7, drugID.length());
                } catch (Exception e) {
                }
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
            } else if (definition.getChild("organismname") != null) {
                //Protein
                String oID = definition.getChildText("organismkegg_id");
                String oName = definition.getChildText("organismname");
                if (oName.contains("'")) {
                    oName = oName.replaceAll("'", "''");
                }
                String enzyme = null;
                if (definition.getChildText("enzyme_classid") != null) {
                    enzyme = Common.ltrim("ec", definition.getChildText("enzyme_classid"));
                }
                String seq = root.getChild("sbolSequence").getChildText("sbolelements");
                if (seq.length() > 32700) {
                    sc++;
                }
                execute("INSERT INTO proteins(ID,  OrganismID, OrganismName, Enzyme, URL, Sequence) VALUES ('" + id + "','" + oID + "','" + oName + "','" + enzyme + "','" + url + "','" + seq + "')");
            } else {
                //EC Number
                String enzyme = Common.ltrim("ec", definition.getChildText("enzyme_classid"));
                execute("INSERT INTO enzymes(ID, ClassID, Name, URL) VALUES ('" + id + "','" + enzyme + "','" + name + "','" + url + "')");
            }
        } else {
            //Reaction
            Element definition = root.getChild("sbolModuleDefinition");
            String id = definition.getChildText("sboldisplayId");
            String name;
            if (definition.getChildText("dctermstitle") != null) {
                name = definition.getChildText("dctermstitle").replaceAll("'", "''");
            } else {
                name = id;
            }
            String keggid = definition.getChildText("reactionkegg_id");
            ArrayList<String> enzymes = new ArrayList();
            ArrayList<String> compounds = new ArrayList();
            XPathExpression<Element> expr = xFactory.compile("//sbolfunctionalComponent", Filters.element());
            List<Element> links = expr.evaluate(definition);
            for (Element e : links) {
                Element fc = e.getChild("sbolFunctionalComponent");
                String fcName = fc.getChildText("sboldisplayId");
                if (fcName.endsWith("enzyme")) {
                    enzymes.add(Common.ltrim("ec", fc.getChildText("enzyme_classid")));
                } else {
                    if (Common.countMatches(fcName, "_") == 1) {
                        compounds.add(fcName);
                    } else {
                        compounds.add(fcName.substring(0, fcName.lastIndexOf("_")));
                    }
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

    public String[] getDatasets() {
        return dbNames.toArray(new String[0]);
    }

    public void resetDB() {
        Connection conn = null;
        ArrayList<Statement> statements = new ArrayList<Statement>();
        Statement s;
        ResultSet rs = null;
        try {
            Properties props = new Properties();
            conn = DriverManager.getConnection(protocol + dbName
                    + ";create=true", props);

            conn.setAutoCommit(false);
            s = conn.createStatement();
            statements.add(s);
            //Utils.dropSchema(conn.getMetaData(), "APP");
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

    public Part[] catalog(String table) {
        if (dbName == null) {
            return new Part[]{};
        }
        Part[] p;
        if (table.equals("proteins")) {
            JsonArray a = executeJSON("SELECT ID, OrganismName, Enzyme, URL FROM " + table);
            p = new Part[a.size()];
            for (int i = 0; i < a.size(); i++) {
                JsonObject o = a.get(i).getAsJsonObject();
                p[i] = new Protein(o.get("ID").getAsString(), o.get("ORGANISMNAME").getAsString(), o.get("URL").getAsString(), o.get("ENZYME").getAsString()).setLocal(true);
            }
        } else {
            JsonArray a = executeJSON("SELECT ID, Name, URL FROM " + table);
            p = new Part[a.size()];
            for (int i = 0; i < a.size(); i++) {
                JsonObject o = a.get(i).getAsJsonObject();
                if (table.equals("compounds")) {
                    p[i] = new Compound(o.get("ID").getAsString(), o.get("NAME").getAsString(), o.get("URL").getAsString()).setLocal(true);
                } else if (table.equals("reactions")) {
                    p[i] = new Reaction(o.get("ID").getAsString(), o.get("NAME").getAsString(), o.get("URL").getAsString(), 0).setLocal(true);
                } else if (table.equals("enzymes")) {
                    p[i] = new Enzyme(o.get("ID").getAsString(), o.get("NAME").getAsString(), o.get("URL").getAsString(), "").setLocal(true);
                }
            }
        }
        return p;
    }

    public void setCurrentDataset(String value) {
        dbName = value;
        Connection conn = null;
        ArrayList<Statement> statements = new ArrayList<Statement>();
        Statement s;
        ResultSet rs = null;
        try {
            Properties props = new Properties();
            conn = DriverManager.getConnection(protocol + "default"
                    + ";create=true", props);

            conn.setAutoCommit(false);
            s = conn.createStatement();
            statements.add(s);
            try {
                s.execute("TRUNCATE TABLE cdb ");
                if (value != null) {
                    s = conn.createStatement();
                    statements.add(s);
                    s.execute("insert into cdb values ('" + value + "') ");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.close(rs, statements, conn);
        }
    }
}