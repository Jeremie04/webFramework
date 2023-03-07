/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rc.data;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import rc.annotation.MyType;
import rc.annotation.__AClass;
import rc.annotation.__AFields;
import static rc.config.Config.DATABASE;
import static rc.config.Config.DATABASENAME;
import static rc.config.Config.HOST;
import static rc.config.Config.PASSWORD;
import static rc.config.Config.PORT;
import static rc.config.Config.USERNAME;
import rc.config.__Connection;

/**
 *
 * @author Mitasoa
 */
public class ObjetCRUD {

    protected PreparedStatement stmt = null;
    protected ResultSet res = null;
    public static int initialize = -1673; // Change it , if you need use this number
    protected Method method;
    private ArrayList fieldSpecific = new ArrayList();
    private String sequenceValue;
    private String sequenceValueBefore;
    private String specific;
    private String idName;
    private ArrayList type = new ArrayList();
    private int line;

    // Conditions
    private String order;
    private String plus;

    public ObjetCRUD() throws Exception {
        init();
        this.idName = "";
        this.order = "";
        this.plus = "";
    }

    // Initialize types primitifs
    public void init() throws Exception {
        Class c = this.getClass();
        Field[] f = c.getDeclaredFields();
        for (int i = 0; i < f.length; i++) {
            if (f[i].getType().isPrimitive()) {
                method = this.getClass().getMethod("set" + f[i].getName().substring(0, 1).toUpperCase()
                        + f[i].getName().toString().substring(1), f[i].getType());
                if (f[i].getType().toString().contains("int")
                        || f[i].getType().toString().contains("float")
                        || f[i].getType().toString().contains("double")
                        || f[i].getType().toString().contains("long")) {
                    method.invoke(this, initialize);
                }
            }
        }
    }

    /*
        All function that we need for this class CRUD
     */
    // All fields not ignored in one class
    public ArrayList<Field> getFieldsNotIgnored() throws Exception {
        Class c = this.getClass();
        Field[] f = c.getDeclaredFields();
        ArrayList<Field> fields = new ArrayList<Field>();
        for (int i = 0; i < f.length; i++) {
            if (f[i].getAnnotation(__AFields.class) != null) {
                if (!f[i].getAnnotation(__AFields.class).ignored()) {
                    fields.add(f[i]);
                }
            } else {
                fields.add(f[i]);
            }
        }
        return fields;
    }

    // Transform ArrayList into Object Field
    public Field[] __getFieldsNotIgnored() throws Exception {
        ArrayList<Field> fields = this.getFieldsNotIgnored();
        Field[] __fields = new Field[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            __fields[i] = (Field) fields.get(i);
        }
        return __fields;
    }

    // Obtain all name fields  of one class
    public String[] getFieldsName() throws Exception {
        Field[] f = this.__getFieldsNotIgnored();
        String[] allFields = new String[f.length];
        for (int i = 0; i < allFields.length; i++) {
            allFields[i] = f[i].getName();
            if (f[i].getAnnotation(__AFields.class) != null) {
                if ((f[i].getAnnotation(__AFields.class).type() == MyType.SERIAL)) {
                    fieldSpecific.add(allFields[i] + "//" + "DEFAULT");
                } else if ((f[i].getAnnotation(__AFields.class).type() == MyType.SEQUENCE)) {
                    this.setSequenceValue(f[i].getAnnotation(__AFields.class).sequence());
                    this.setSequenceValueBefore(f[i].getAnnotation(__AFields.class).sequenceBefore());
                    fieldSpecific.add(allFields[i] + "//" + this.getSequenceValue());
                }

                if ((f[i].getAnnotation(__AFields.class).isId())) {
                    this.setIdName(allFields[i]);
                }
            }
        }
        return allFields;
    }

    // Verif if one field is specific
    public boolean isSpecific(String name) {
        for (int i = 0; i < fieldSpecific.size(); i++) {
            if (name.equals(((String) fieldSpecific.get(i)).split("//")[0])) {
                this.setSpecific(((String) fieldSpecific.get(i)).split("//")[1]);
                return true;
            }
        }
        return false;
    }

    // Obtain all name annotation of attributes
    public String[] __getFieldsName() throws Exception {
        Field[] f = this.__getFieldsNotIgnored();
        String[] allFields = new String[f.length];
        for (int i = 0; i < allFields.length; i++) {
            allFields[i] = f[i].getName();
            if (f[i].getAnnotation(__AFields.class) != null && !f[i].getAnnotation(__AFields.class).column().equals("NaN")) {
                allFields[i] = f[i].getAnnotation(__AFields.class).column();
            }
        }
        return allFields;
    }

    // Obtain all filelds's values of one class
    public Object[] getValuesFields() throws Exception {
        Class c = this.getClass();
        String[] fields = this.getFieldsName();
        Object[] values = new Object[fields.length];
        for (int i = 0; i < fields.length; i++) {
            Method methode = c.getMethod("get" + fields[i].substring(0, 1).toUpperCase()
                    + fields[i].substring(1));

            values[i] = methode.invoke(this);
        }
        return values;
    }

    // Obtain all fields with their values not null
    public ArrayList<String[]> getFieldsAndValuesNotNull() throws Exception {
        ArrayList<String[]> __fieldsAndValuesNotNull = new ArrayList<String[]>();
        String[] f = this.__getFieldsName();
        Object[] fv = this.getValuesFields();
        String name, value;
        Field[] field = this.__getFieldsNotIgnored();
        type.clear();
        for (int i = 0; i < f.length; i++) {
            if ((fv[i] != null
                    && !fv[i].equals(initialize)
                    && !fv[i].equals((long) initialize)
                    && !fv[i].equals((float) initialize)
                    && !fv[i].equals((double) initialize))
                    || (field[i].getAnnotation(__AFields.class) != null
                    && field[i].getAnnotation(__AFields.class).isId())) {
                name = f[i] + "";
                value = fv[i] + "";
                __fieldsAndValuesNotNull.add(new String[]{name, value});
                type.add(field[i].getType().toString());
            }
        }
        return __fieldsAndValuesNotNull;
    }

    // Transform ArrayList FieldsAndValuesNotNull into Object String
    public String[][] __getFieldsAndValuesNotNull() throws Exception {
        ArrayList<String[]> fieldsAndValuesNotNull = this.getFieldsAndValuesNotNull();
        String[][] __fieldsAndValuesNotNull = new String[fieldsAndValuesNotNull.size()][2];
        for (int i = 0; i < fieldsAndValuesNotNull.size(); i++) {
            __fieldsAndValuesNotNull[i][0] = ((String[]) (fieldsAndValuesNotNull.get(i)))[0].toString();
            __fieldsAndValuesNotNull[i][1] = ((String[]) (fieldsAndValuesNotNull.get(i)))[1].toString();
        }
        return __fieldsAndValuesNotNull;
    }

    // QueryInsert
    public String[] queryInsert() throws Exception {
        String[][] fieldsAndValuesNotNull = this.__getFieldsAndValuesNotNull();
        String[] retour = new String[2];
        String name = "(";
        String values = "(";
        for (int i = 0; i < fieldsAndValuesNotNull.length; i++) {
            String value = "?";
            if (this.isSpecific(fieldsAndValuesNotNull[i][0])) {
                value = this.getSpecific();

            }
            if (i == fieldsAndValuesNotNull.length - 1) {
                name += fieldsAndValuesNotNull[i][0];
                values += value;
                break;
            }
            name += fieldsAndValuesNotNull[i][0] + ",";
            values += value + ",";
        }
        name += ")";
        values += ")";
        retour[0] = name;
        retour[1] = values;
        return retour;
    }

    public String queryFinalInsert(String tableName) throws Exception {
        String retour = "";
        String[] queryInsert = this.queryInsert();
        retour = "INSERT INTO " + tableName + queryInsert[0] + " VALUES " + queryInsert[1];
        return retour;
    }

    public String[] queryUpdate() throws Exception {
        String update = "";
        String where = "";
        String[] retour = new String[2];
        String[][] fieldsAndValuesNotNull = this.__getFieldsAndValuesNotNull();
        for (int i = 0; i < fieldsAndValuesNotNull.length; i++) {
            if (where == "" && fieldsAndValuesNotNull[i][0].equals(this.getIdName())) {
                if (((String) this.type.get(i)).contains("int")
                        || ((String) this.type.get(i)).contains("Integer")
                        || ((String) this.type.get(i)).contains("float")
                        || ((String) this.type.get(i)).contains("Float")
                        || ((String) this.type.get(i)).contains("Double")
                        || ((String) this.type.get(i)).contains("double")) {
                    where = " WHERE " + this.getIdName() + " = " + fieldsAndValuesNotNull[i][1];
                } else {
                    where = " WHERE " + this.getIdName() + " = '" + fieldsAndValuesNotNull[i][1] + "'";
                }

            } else {
                update += fieldsAndValuesNotNull[i][0] + "=" + "?,";
            }
        }
        if (update.contains(",")) {
            retour[0] = update.substring(0, update.length() - 1);
        } else {
            retour[0] = update;
        }
        retour[1] = where;
        return retour;
    }

    public String queryFinalUpdate(String tableName) throws Exception {
        String retour = "";
        String[] queryUpdate = this.queryUpdate();
        retour = "UPDATE " + tableName + " SET " + queryUpdate[0] + queryUpdate[1];
        return retour;
    }

    public String queryCondition() throws Exception {
        String retour = "";
        String[][] fieldsAndValuesNotNull = this.__getFieldsAndValuesNotNull();
        for (int i = 0; i < fieldsAndValuesNotNull.length; i++) {
            if (!fieldsAndValuesNotNull[i][1].equals(initialize + "")) {
                retour += fieldsAndValuesNotNull[i][0] + "=" + "? AND ";
            }
        }
        if (retour.contains("AND")) {
            retour = " WHERE " + retour.substring(0, retour.length() - 5);
        }
        return retour;
    }

    public String queryFinalDelete(String tableName) throws Exception {
        String retour = "";
        String[] queryUpdate = this.queryUpdate();
        retour = "DELETE FROM " + tableName + queryCondition();
        return retour;
    }

    public String queryFinalSelect(String tableName) throws Exception {
        String retour = "";
        String[] queryUpdate = this.queryUpdate();
        retour = "SELECT * FROM " + tableName + queryCondition();
        return retour;
    }

    // Obtain value of specific sequence 
    public String getSequence(Connection con, String name, boolean isClose) throws SQLException {
        Connection __con = con;
        __Connection c = new __Connection();
        if (con == null) {
            __con = c.getConnect(DATABASENAME, USERNAME, PASSWORD, DATABASE, HOST, PORT);
        }
        String query = "SELECT NEXTVAL(?)";
        String sequence = "";
        try {
            stmt = __con.prepareStatement(query);
            stmt.setString(1, name);
            res = stmt.executeQuery();
            while (res.next()) {
                sequence += res.getString(1);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (res != null) {
                res.close();
            }
            if (stmt != null && isClose) {
                stmt.close();
            }
            if (__con != null && isClose) {
                __con.close();
            }
        }
        return sequence;
    }

    public void setStmt(PreparedStatement stmt, boolean isAcceptId) throws Exception {
        String[][] FieldAndValue = this.__getFieldsAndValuesNotNull();
        int __index = 1;
        String idName = this.getIdName();
        // C'est dans le cas des delete et select
        if (!isAcceptId) {
            idName = "";
        }
        for (int i = 0; i < FieldAndValue.length; i++) {
            if (FieldAndValue[i][1].equals("null")) {
                System.out.println("[ RC Framework : ATTENTION ! il y a une valeur null ]");
            }
            if (!(FieldAndValue[i][0].toString().equals(idName)) && !(FieldAndValue[i][1].toString().equals(initialize + ""))) {
                if (((String) this.type.get(i)).contains("Integer") || ((String) this.type.get(i)).contains("int")) {
                    stmt.setInt(__index, Integer.parseInt(FieldAndValue[i][1]));
                } else if (((String) this.type.get(i)).contains("Double") || ((String) this.type.get(i)).contains("double")) {
                    stmt.setDouble(__index, Double.parseDouble(FieldAndValue[i][1]));
                } else if (((String) this.type.get(i)).contains("Long") || ((String) this.type.get(i)).contains("long")) {
                    stmt.setLong(__index, Long.parseLong(FieldAndValue[i][1]));
                } else if (((String) this.type.get(i)).toString().contains("Float") || ((String) this.type.get(i)).contains("float")) {
                    stmt.setFloat(__index, Float.parseFloat(FieldAndValue[i][1]));
                } else if (((String) this.type.get(i)).toString().endsWith("Date")) {
                    stmt.setDate(__index, Date.valueOf(FieldAndValue[i][1]));
                } else if (((String) this.type.get(i)).toString().endsWith("LocalDateTime")) {
                    stmt.setObject(__index, LocalDateTime.parse(FieldAndValue[i][1]));
                } else if (((String) this.type.get(i)).toString().endsWith("LocalTime")) {
                    stmt.setObject(__index, LocalTime.parse(FieldAndValue[i][1]));
                } else if (((String) this.type.get(i)).toString().endsWith("boolean") || ((String) this.type.get(i)).toString().endsWith("Boolean")) {
                    stmt.setBoolean(__index, Boolean.parseBoolean(FieldAndValue[i][1]));
                } // We can add here if you have other
                else {
                    stmt.setString(__index, FieldAndValue[i][1]);
                }
                __index++;
            }
        }
    }

    public String tableName() throws Exception {
        Class c = this.getClass();
        if (c.isAnnotationPresent(__AClass.class)) {
            __AClass __a = (__AClass) c.getAnnotation(__AClass.class);
            if (__a.tableName() != "" || __a.tableName() != null) {
                return __a.tableName();
            }
        }
        return this.getClass().getSimpleName();
    }

    // Save
    public void Save(Connection con, boolean isClose) throws Exception {
        Connection __con = con;
        __Connection c = new __Connection();
        if (con == null) {
            __con = c.getConnect(DATABASENAME, USERNAME, PASSWORD, DATABASE, HOST, PORT);
        }
        String tableName = this.tableName();
        String query = this.queryFinalInsert(tableName);
        System.out.println("[ RC Framework : " + query + " ]");
        try {
            stmt = __con.prepareStatement(query);
            this.setStmt(stmt, true);
            stmt.executeUpdate();
            __con.commit();
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (res != null) {
                res.close();
            }
            if (stmt != null && isClose) {
                stmt.close();
            }
            if (__con != null && isClose) {
                __con.close();
            }
        }
    }

    // Update 
    public void Update(Connection con, boolean isClose) throws Exception {
        Connection __con = con;
        __Connection c = new __Connection();
        if (con == null) {
            __con = c.getConnect(DATABASENAME, USERNAME, PASSWORD, DATABASE, HOST, PORT);
        }
        String tableName = this.tableName();
        String query = this.queryFinalUpdate(tableName);
        System.out.println("[ RC Framework : " + query + " ]");
        try {
            stmt = __con.prepareStatement(query);
            this.setStmt(stmt, true);
            stmt.executeUpdate();
            __con.commit();
        } catch (Exception ex) {
            throw new Exception("UpdateException : Error of your sql ?  : " + ex);
        } finally {
            if (res != null) {
                res.close();
            }
            if (stmt != null && isClose) {
                stmt.close();
            }
            if (__con != null && isClose) {
                __con.close();
            }
        }
    }

    // Delete
    public void Delete(Connection con, boolean isClose) throws Exception {
        Connection __con = con;
        __Connection c = new __Connection();
        if (con == null) {
            __con = c.getConnect(DATABASENAME, USERNAME, PASSWORD, DATABASE, HOST, PORT);
        }
        String tableName = this.tableName();
        String query = this.queryFinalDelete(tableName);
        System.out.println("[ RC Framework : " + query + " ]");
        try {
            stmt = __con.prepareStatement(query);
            this.setStmt(stmt, false);
            stmt.executeUpdate();
            __con.commit();
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (res != null) {
                res.close();
            }
            if (stmt != null && isClose) {
                stmt.close();
            }
            if (__con != null && isClose) {
                __con.close();
            }
        }
    }

    public int nbSelect(Connection con, int offset, int limit) throws Exception {
        String tableName = this.tableName();
        String query = this.queryFinalSelect(tableName) + " " + this.getPlus() + " " + this.getOrder() + " OFFSET " + offset + " LIMIT " + limit;
        PreparedStatement __stmt = con.prepareStatement(query);
        this.setStmt(__stmt, false);
        ResultSet __res = __stmt.executeQuery();
        int nb = 0;
        while (__res.next()) {
            nb++;
        }
        return nb;
    }

    public void setOrder(String order, String column) {
        this.order = " ORDER BY " + column + " DESC ";
    }

    // Obatin all Object select : select
    public void setObjectSelect(PreparedStatement stmt, Object ob) throws Exception {
        Field[] f = this.__getFieldsNotIgnored();
        String[][] FieldAndValue = this.__getFieldsAndValuesNotNull();
        String[] fields = this.getFieldsName();
        int __index = 1;
        for (int i = 0; i < fields.length; i++) {
            method = this.getClass().getMethod("set" + fields[i].substring(0, 1).toUpperCase()
                    + f[i].getName().toString().substring(1), f[i].getType());
            if (f[i].getType().toString().contains("Integer") || f[i].getType().toString().contains("int")) {
                method.invoke(ob, res.getInt(i + 1));
            } else if (f[i].getType().toString().contains("Double") || f[i].getType().toString().contains("double")) {
                method.invoke(ob, res.getDouble(i + 1));
            } else if (f[i].getType().toString().contains("Float") || f[i].getType().toString().contains("float")) {
                method.invoke(ob, res.getFloat(i + 1));
            } else if (f[i].getType().toString().endsWith("Date")) {
                method.invoke(ob, res.getDate(i + 1));
            } else if (f[i].getType().toString().endsWith("LocalDateTime")) {
                method.invoke(ob, LocalDateTime.parse(res.getString(i + 1).replace(" ", "T")));
            } else if (f[i].getType().toString().endsWith("LocalTime")) {
                method.invoke(ob, LocalTime.parse(res.getString(i + 1)));
            } // We can add here if you have other
            else {
                method.invoke(ob, res.getString(i + 1));
            }
            __index++;
        }
    }

    // Select
    public Object[] Select(Connection con, boolean isClose, int offset, int limit) throws Exception {
        Connection __con = con;
        __Connection c = new __Connection();
        if (con == null) {
            __con = c.getConnect(DATABASENAME, USERNAME, PASSWORD, DATABASE, HOST, PORT);
        }
        String tableName = this.tableName();
        String query = this.queryFinalSelect(tableName) + " " + this.getPlus() + " " + this.getOrder() + " OFFSET " + offset + " LIMIT " + limit;
        Object[] all = new Object[0];
        System.out.println("[ RC Framework : " + query + " ]");
        try {
            int j = 0;
            int nb = this.nbSelect(__con, offset, limit);
            stmt = __con.prepareStatement(query);
            this.setStmt(stmt, false);
            res = stmt.executeQuery();
            ResultSetMetaData __meta = res.getMetaData();
            all = new Object[nb];
            while (res.next()) {
                Object ob = this.getClass().newInstance();
                for (int i = 1; i <= __meta.getColumnCount(); i++) {
                    this.setObjectSelect(stmt, ob);
                }
                all[j] = ob;
                j++;
            }
        } catch (Exception ex) {
            if (!this.queryFinalSelect(tableName).contains("WHERE")) {
                System.out.println("[ RC Framework : Peut-etre une erreur SQL : Vous avez ajouter une condition de plus , vous devez peut ajouter un « WHERE » ou reverifier votre condition plus ou le order ! ]");
            }
            throw ex;
        } finally {
            if (res != null) {
                res.close();
            }
            if (stmt != null && isClose) {
                stmt.close();
            }
            if (__con != null && isClose) {
                __con.close();
            }
        }
        return all;
    }

    public int nbSelectQuery(Connection con, String query) throws Exception {
        PreparedStatement __stmt = con.prepareStatement(query);
        ResultSet __res = __stmt.executeQuery();
        int nb = 0;
        while (__res.next()) {
            nb++;
        }
        this.setLine(nb);
        return nb;
    }

    public Object[][] select(Connection con, String query, boolean isClose) throws Exception {
        System.out.println("[ RC Framework : " + query + " ]");
        Connection __con = con;
        __Connection c = new __Connection();
        if (con == null) {
            __con = c.getConnect(DATABASENAME, USERNAME, PASSWORD, DATABASE, HOST, PORT);
        }
        int nb = nbSelectQuery(__con, query);
        Object[][] retour = new Object[nb][0];
        try {
            int j = 0;
            stmt = __con.prepareStatement(query);
            res = stmt.executeQuery();
            ResultSetMetaData __meta = res.getMetaData();
            while (res.next()) {
                retour[j] = new Object[__meta.getColumnCount()];
                for (int i = 1; i <= __meta.getColumnCount(); i++) {
                    retour[j][i - 1] = res.getObject(i);
                }
                j++;
            }
            __con.commit();
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (res != null) {
                res.close();
            }
            if (stmt != null && isClose) {
                stmt.close();
            }
            if (__con != null && isClose) {
                __con.close();
            }
        }
        return retour;
    }

    public void executeQuery(Connection con, String query, boolean isClose) throws Exception {
        Connection __con = con;
        try {
            __Connection c = new __Connection();
            if (con == null) {
                __con = c.getConnect(DATABASENAME, USERNAME, PASSWORD, DATABASE, HOST, PORT);
            }
            System.out.println("[ RC Framework : " + query + " ]");
            stmt = __con.prepareStatement(query);
            stmt.executeUpdate();
            __con.commit();
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (res != null) {
                res.close();
            }
            if (stmt != null && isClose) {
                stmt.close();
            }
            if (__con != null && isClose) {
                __con.close();
            }
        }
    }

    /**
     * @return the sequenceValue
     */
    public String getSequenceValue() {
        return "concat('" + this.getSequenceValueBefore() + "',(SELECT nextval('" + sequenceValue + "'))::varchar)";
    }

    /**
     * @param sequenceValue the sequenceValue to set
     */
    public void setSequenceValue(String sequenceValue) {
        this.sequenceValue = sequenceValue;
    }

    /**
     * @return the specific
     */
    public String getSpecific() {
        return specific;
    }

    /**
     * @param specific the specific to set
     */
    public void setSpecific(String specific) {
        this.specific = specific;
    }

    /**
     * @return the sequenceValueBefore
     */
    public String getSequenceValueBefore() {
        return sequenceValueBefore;
    }

    /**
     * @param sequenceValueBefore the sequenceValueBefore to set
     */
    public void setSequenceValueBefore(String sequenceValueBefore) {
        this.sequenceValueBefore = sequenceValueBefore;
    }

    /**
     * @return the idName
     */
    public String getIdName() {
        return idName;
    }

    /**
     * @param idName the idName to set
     */
    public void setIdName(String idName) {
        this.idName = idName;
    }

    /**
     * @return the order
     */
    public String getOrder() {
        return order;
    }

    /**
     * @return the plus
     */
    public String getPlus() {
        return plus;
    }

    /**
     * @param plus the plus to set
     */
    public void setPlus(String plus) {
        this.plus = plus;
    }

    /**
     * @return the line
     */
    public int getLine() {
        return line;
    }

    /**
     * @param line the line to set
     */
    public void setLine(int line) {
        this.line = line;
    }
}
