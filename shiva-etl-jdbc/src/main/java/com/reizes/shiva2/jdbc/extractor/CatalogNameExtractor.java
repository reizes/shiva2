package com.reizes.shiva2.jdbc.extractor;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

/**
 * Database Connection으로부터 catalog name list를 extract
 * @author reizes
 * @since 2.1.0
 * @since 2010.4.12
 */
public class CatalogNameExtractor extends AbstractResultSetExtractor {
	public CatalogNameExtractor() {
		super();
	}

	public CatalogNameExtractor(DataSource datasource) {
		super(datasource);
	}

	public CatalogNameExtractor(Map<String, Object> datasourceProperties) throws Exception {
		super(datasourceProperties);
	}

	public CatalogNameExtractor(Properties prop) throws Exception {
		super(prop);
	}

	@Override
	protected ResultSet getResultSet(Connection conn) throws SQLException {
		DatabaseMetaData meta = conn.getMetaData();
		return meta.getCatalogs();
	}

	@Override
	protected Object fetchResultSet(ResultSet rs, Object input) throws Exception {
		while (rs.next()) {
			input = startProcessItem(rs.getString("TABLE_CAT"));
			executeInvalidateQuery();
		}
		return input;
	}

	@Override
	protected void beforeCloseResultSet() throws Exception {
	}

}
