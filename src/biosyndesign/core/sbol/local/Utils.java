package biosyndesign.core.sbol.local;

import com.google.gson.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Optional;

public class Utils {
    static JsonArray resultSetToJsonArray(final ResultSet resultSet)
            throws SQLException {
        final ResultSetMetaData metaData = resultSet.getMetaData();
        // JsonArray is a Gson built-in class to hold JSON arrays
        final JsonArray jsonArray = new JsonArray();
        while ( resultSet.next() ) {
            jsonArray.add(resultSetRowToJsonObject(resultSet, metaData));
        }
        return jsonArray;
    }

    private static JsonElement resultSetRowToJsonObject(final ResultSet resultSet, final ResultSetMetaData metaData)
            throws SQLException {
        final int columnCount = metaData.getColumnCount();
        // Every result set row is a JsonObject equivalent
        final JsonObject jsonObject = new JsonObject();
        // JDBC uses 1-based loops
        for ( int i = 1; i <= columnCount; i++ ) {
            jsonObject.add(metaData.getColumnName(i), fieldToJsonElement(resultSet, metaData, i));
        }
        return jsonObject;
    }

    private static JsonElement fieldToJsonElement(final ResultSet resultSet, final ResultSetMetaData metaData, final int column)
            throws SQLException {
        final int columnType = metaData.getColumnType(column);
        final Optional<JsonElement> jsonElement;
        // Process each SQL type mapping a value to a JSON tree equivalent
        switch ( columnType ) {
            case Types.BIT:
            case Types.TINYINT:
            case Types.SMALLINT:
                throw new UnsupportedOperationException("TODO: " + JDBCType.valueOf(columnType));
            case Types.INTEGER:
                // resultSet.getInt() returns 0 in case of null, so it must be extracted with getObject and cast, then converted to a JsonPrimitive
                jsonElement = Optional.ofNullable((Integer) resultSet.getObject(column)).map(JsonPrimitive::new);
                break;
            case Types.BIGINT:
            case Types.FLOAT:
            case Types.REAL:
            case Types.DOUBLE:
            case Types.NUMERIC:
            case Types.DECIMAL:
            case Types.CHAR:
                throw new UnsupportedOperationException("TODO: " + JDBCType.valueOf(columnType));
            case Types.VARCHAR:
                jsonElement = Optional.ofNullable(resultSet.getString(column)).map(JsonPrimitive::new);
                break;
            case Types.LONGVARCHAR:
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
            case Types.NULL:
            case Types.OTHER:
            case Types.JAVA_OBJECT:
            case Types.DISTINCT:
            case Types.STRUCT:
            case Types.ARRAY:
            case Types.BLOB:
            case Types.CLOB:
            case Types.REF:
            case Types.DATALINK:
            case Types.BOOLEAN:
            case Types.ROWID:
            case Types.NCHAR:
            case Types.NVARCHAR:
            case Types.LONGNVARCHAR:
            case Types.NCLOB:
            case Types.SQLXML:
            case Types.REF_CURSOR:
            case Types.TIME_WITH_TIMEZONE:
            case Types.TIMESTAMP_WITH_TIMEZONE:
                throw new UnsupportedOperationException("TODO: " + JDBCType.valueOf(columnType));
            default:
                throw new UnsupportedOperationException("Unknown type: " + columnType);
        }
        // If the optional value is missing, assume it's a null
        return jsonElement.orElse(JsonNull.INSTANCE);
    }

    public static void dropSchema(DatabaseMetaData dmd, String schema) throws SQLException {
        Connection conn = dmd.getConnection();
        Statement s = dmd.getConnection().createStatement();

        PreparedStatement psf = conn.prepareStatement(
                "SELECT ALIAS FROM SYS.SYSALIASES A, SYS.SYSSCHEMAS S" +
                        " WHERE A.SCHEMAID = S.SCHEMAID " +
                        " AND CHAR(A.ALIASTYPE) = ? " +
                        " AND S.SCHEMANAME = ?");
        psf.setString(1, "F");
        psf.setString(2, schema);
        ResultSet rs = psf.executeQuery();
        dropUsingDMD(s, rs, schema, "ALIAS", "FUNCTION");
        // Procedures
        rs = dmd.getProcedures((String) null,
                schema, (String) null);

        dropUsingDMD(s, rs, schema, "PROCEDURE_NAME", "PROCEDURE");

        // Views
        rs = dmd.getTables((String) null, schema, (String) null,
                GET_TABLES_VIEW);

        dropUsingDMD(s, rs, schema, "TABLE_NAME", "VIEW");

        // Tables
        rs = dmd.getTables((String) null, schema, (String) null,
                GET_TABLES_TABLE);

        dropUsingDMD(s, rs, schema, "TABLE_NAME", "TABLE");
        ResultSet table_rs = dmd.getTables((String) null, schema, (String) null,
                GET_TABLES_TABLE);
        while (table_rs.next()) {
            String tablename = table_rs.getString("TABLE_NAME");
            rs = dmd.getExportedKeys((String) null, schema, tablename);
            while (rs.next()) {
                short keyPosition = rs.getShort("KEY_SEQ");
                if (keyPosition != 1)
                    continue;
                String fkName = rs.getString("FK_NAME");
                // No name, probably can't happen but couldn't drop it anyway.
                if (fkName == null)
                    continue;
                String fkSchema = rs.getString("FKTABLE_SCHEM");
                String fkTable = rs.getString("FKTABLE_NAME");
                String ddl = "ALTER TABLE " +
                        escape(fkSchema, fkTable) +
                        " DROP FOREIGN KEY " +
                        escape(fkName);
                s.executeUpdate(ddl);
            }
            rs.close();
        }
        table_rs.close();
        conn.commit();

// Tables (again)
        rs = dmd.getTables((String) null, schema, (String) null,
                GET_TABLES_TABLE);
        dropUsingDMD(s, rs, schema, "TABLE_NAME", "TABLE");
// drop UDTs
        psf.setString(1, "A");
        psf.setString(2, schema);
        rs = psf.executeQuery();
        dropUsingDMD(s, rs, schema, "ALIAS", "TYPE");
        psf.close();
// Synonyms - need work around for DERBY-1790 where
// passing a table type of SYNONYM fails.
        rs = dmd.getTables((String) null, schema, (String) null,
                GET_TABLES_SYNONYM);
        dropUsingDMD(s, rs, schema, "TABLE_NAME", "SYNONYM");
        conn.commit();
        s.close();
    }

    private static void dropUsingDMD(
            Statement s, ResultSet rs, String schema,
            String mdColumn,
            String dropType) throws SQLException {
        String dropLeadIn = "DROP " + dropType + " ";

        // First collect the set of DROP SQL statements.
        ArrayList ddl = new ArrayList();
        while (rs.next()) {
            String objectName = rs.getString(mdColumn);
            String raw = dropLeadIn + escape(schema, objectName);
            if ("TYPE".equals(dropType) || "SEQUENCE".equals(dropType)) {
                raw = raw + " restrict ";
            }
            ddl.add(raw);
        }
        rs.close();
        if (ddl.isEmpty())
            return;

        // Execute them as a complete batch, hoping they will all succeed.
        s.clearBatch();
        int batchCount = 0;
        for (Iterator i = ddl.iterator(); i.hasNext(); ) {
            Object sql = i.next();
            if (sql != null) {
                s.addBatch(sql.toString());
                batchCount++;
            }
        }
        int[] results;
        boolean hadError;
        try {
            results = s.executeBatch();
            hadError = false;
        } catch (BatchUpdateException batchException) {
            results = batchException.getUpdateCounts();
            hadError = true;
        }

        // Remove any statements from the list that succeeded.
        boolean didDrop = false;
        for (int i = 0; i < results.length; i++) {
            int result = results[i];
            if (result == Statement.EXECUTE_FAILED)
                hadError = true;
            else if (result == Statement.SUCCESS_NO_INFO || result >= 0) {
                didDrop = true;
                ddl.set(i, null);
            }
        }
        s.clearBatch();
        if (didDrop) {
            // Commit any work we did do.
            s.getConnection().commit();
        }
        if (hadError) {
            do {
                hadError = false;
                didDrop = false;
                for (ListIterator i = ddl.listIterator(); i.hasNext(); ) {
                    Object sql = i.next();
                    if (sql != null) {
                        try {
                            s.executeUpdate(sql.toString());
                            i.set(null);
                            didDrop = true;
                        } catch (SQLException e) {
                            hadError = true;
                        }
                    }
                }
                if (didDrop)
                    s.getConnection().commit();
            } while (hadError && didDrop);
        }
    }

    public static String escape(String schema, String name) {
        return escape(schema) + "." + escape(name);
    }
    public static String escape(String name)
    {
        StringBuffer buffer = new StringBuffer(name.length() + 2);
        buffer.append('"');
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '"') buffer.append('"');
            buffer.append(c);
        }
        buffer.append('"');
        return buffer.toString();
    }

    public static final String[] GET_TABLES_TABLE = new String[]{"TABLE"};
    public static final String[] GET_TABLES_VIEW = new String[]{"VIEW"};
    public static final String[] GET_TABLES_SYNONYM =
            new String[]{"SYNONYM"};

    public static void close(ResultSet rs, ArrayList<Statement> statements, Connection conn) {
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
