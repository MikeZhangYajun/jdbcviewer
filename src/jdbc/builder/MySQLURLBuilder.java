package jdbc.builder;

import java.util.Map;

public class MySQLURLBuilder extends JDBCURLBuilder {
	public MySQLURLBuilder() {
		super();
		setDB("mysql");
	}

	@Override
	public String getURL() {
		StringBuilder sb = new StringBuilder();
		sb.append(JDBC).append(":").append(dbType).append("://").append(hostAddress).append(":").append(portNumber)
				.append("/").append(catalogName);
		if (!properties.isEmpty()) {
			sb.append("?");
			for (Map.Entry<String, String> me : properties.entrySet()) {
				sb.append(me.getKey() + "=" + me.getValue() + "&");
			}
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}

}
