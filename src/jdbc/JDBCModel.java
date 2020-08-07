package jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class JDBCModel {
	private List<String> columnNames, tableNames;
	private Connection connection;
	private String user, pass;

	JDBCModel() {
		columnNames = new ArrayList<String>();
		tableNames = new ArrayList<String>();
	}

	public void setCredentials(String user, String pass) {
		this.user = user;
		this.pass = pass;
	}

	private void checkConnectionIsValid() throws SQLException {
		if (connection == null || connection.isClosed())
			throw new SQLException();
	}

	private void checkTableNameAndColumnsAreValid(String table) throws SQLException {
		Objects.requireNonNull(table, "table name cannot be null");
		table = table.trim();
		if (tableNames.isEmpty())
			getAndInitializeTableNames();
		else if (columnNames.isEmpty())
			getAndInitializeColumnNames(table);
		else if (table.isEmpty() || !tableNames.contains(table))
			throw new IllegalArgumentException("table name=\"" + table + "\" is not valid");
	}

	public void connectTo(String url) throws SQLException {
		close();
		connection = DriverManager.getConnection(url, user, pass);
	}

	public boolean isConnected() throws SQLException {
		return connection != null && connection.isValid(1);
	}

	public List<String> getAndInitializeColumnNames(String table) throws SQLException {
		checkConnectionIsValid();
		columnNames.clear();
		DatabaseMetaData dbMeta = connection.getMetaData();
		try (ResultSet rs = dbMeta.getColumns(connection.getCatalog(), null, table, null)) {
			while (rs.next())
				columnNames.add(rs.getString("COLUMN_NAME"));
		}
		return Collections.unmodifiableList(columnNames);
	}

	public List<String> getAndInitializeTableNames() throws SQLException {
		checkConnectionIsValid();
		tableNames.clear();
		DatabaseMetaData dbMeta = connection.getMetaData();
		try (ResultSet rs = dbMeta.getTables(connection.getCatalog(), null, null, new String[] { "TABLE" })) {
			while (rs.next())
				tableNames.add(rs.getString("TABLE_NAME"));
		}
		return Collections.unmodifiableList(tableNames);
	}

	public List<List<Object>> getAll(String table) throws SQLException {
		return search(table, null);
	}

	public List<List<Object>> search(String table, String searchTerm) throws SQLException {
		checkConnectionIsValid();
		checkTableNameAndColumnsAreValid(table);
		List<List<Object>> list = new LinkedList<>();
		String query = buildSQLSearchQuery(table, searchTerm != null);
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			if (searchTerm != null) {
				searchTerm = String.format("%%%s%%", searchTerm);
				for (int i = 1; i <= columnNames.size(); i++) {
					ps.setObject(i, searchTerm);
				}
			}
			extractRowsFromResultSet(ps, list);
		}
		return list;
	}

	private String buildSQLSearchQuery(String table, boolean withParameters) {
		StringBuilder sb = new StringBuilder("select * from ");
		sb.append(table);
		if (withParameters) {
			sb.append(" where ");
			for (int index = 0; index < columnNames.size(); index++) {
				sb.append(columnNames.get(index) + " like ? or ");
			}
			sb.setLength(sb.length() - 4);
		}
		return sb.toString();
	}

	private void extractRowsFromResultSet(PreparedStatement ps, List<List<Object>> list) throws SQLException {
		try (ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				List<Object> row = new ArrayList<>();
				for (int i = 1; i <= columnNames.size(); i++) {
					row.add(rs.getObject(i));
				}
				list.add(row);
			}
		}
	}

	public void close() throws SQLException {
		if (connection != null)
			connection.close();
	}

}
