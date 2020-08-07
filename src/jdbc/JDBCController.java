package jdbc;

import java.sql.SQLException;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import jdbc.builder.JDBCURLBuilder;
import javafx.collections.FXCollections;

public class JDBCController {
	private JDBCURLBuilder builder;
	private JDBCModel model;
	private StringProperty tableInUse;
	private ObservableList<String> tableNamesList;

	public JDBCController() {
		tableNamesList = FXCollections.observableArrayList();
		model = new JDBCModel();
		tableInUse = new SimpleStringProperty();
		tableInUse.addListener((v, o, n) -> {
			try {
				model.getAndInitializeColumnNames(n);
			} catch (SQLException e) {
				throw new IllegalStateException(e);
			}
		});
	}

	public StringProperty tableInUseProperty() {
		return tableInUse;

	}

	public JDBCController setURLBuilder(JDBCURLBuilder builder) {
		this.builder = builder;
		return this;
	}

	public JDBCController setDataBase(String address, String port, String catalog) {
		builder.setAddress(address);
		builder.setPort(port);
		builder.setCatalog(catalog);
		return this;
	}

	public JDBCController addConnectionURLProperty(String key, String value) {
		builder.addURLProperty(key, value);
		return this;
	}

	public JDBCController setCredentials(String user, String pass) {
		model.setCredentials(user, pass);
		return this;
	}

	public JDBCController connect() throws SQLException {
		model.connectTo(builder.getURL());
		return this;
	}

	public boolean isConnected() throws SQLException {
		return model.isConnected();
	}

	public List<String> getColumnNames() throws SQLException {
		return model.getAndInitializeColumnNames(tableInUseProperty().getValue());
	}

	public List<List<Object>> getAll() throws SQLException {
		return model.getAll(tableInUseProperty().getValue());
	}

	public List<List<Object>> search(String searchTerm) throws SQLException {
		return model.search(tableInUseProperty().getValue(), searchTerm);
	}

	public void close() throws SQLException {
		model.close();
	}

	public ObservableList<String> getTableNames() throws SQLException {
		if (model.isConnected()) {
			tableNamesList.clear();
			tableNamesList.addAll(model.getAndInitializeTableNames());
		}
		return tableNamesList;
	}

}
