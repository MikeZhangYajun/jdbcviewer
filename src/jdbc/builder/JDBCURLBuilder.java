package jdbc.builder;

import java.util.HashMap;
import java.util.Map;

public abstract class JDBCURLBuilder {
	protected final static String JDBC = "jdbc";
	protected Map<String, String> properties;
	protected String dbType, hostAddress, catalogName;
	protected int portNumber;

	public JDBCURLBuilder() {
		properties = new HashMap<String, String>();
	}

	public void setPort(String port) {
		portNumber = Integer.parseInt(port);
	}

	public void addURLProperty(String key, String value) {
		if (value == null)
			throw new NullPointerException();
		properties.put(key, value);
	}

	protected void setDB(String db) {
		dbType = db;
	}

	public abstract String getURL();

	public void setPort(int port) {
		if (port < 0)
			throw new IllegalArgumentException();
		portNumber = port;
	}

	public void setAddress(String address) {
		hostAddress = address;
	}

	public void setCatalog(String catalog) {
		catalogName = catalog;
	}

}
