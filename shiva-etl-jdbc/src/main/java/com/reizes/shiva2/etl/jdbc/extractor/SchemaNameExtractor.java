package com.reizes.shiva2.etl.jdbc.extractor;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Database Connection으로부터 schema name list를 extract
 * @author reizes
 * @since 2.1.0
 * @since 2010.4.12
 */
public class SchemaNameExtractor extends AbstractResultSetExtractor {
	private String catalog;

	@Override
	protected ResultSet getResultSet(Connection conn) throws SQLException {
		DatabaseMetaData meta = conn.getMetaData();

		if (catalog == null) {
			return meta.getSchemas();
		} else {
			return meta.getSchemas(catalog, null);
		}

	}

	@Override
	protected Object fetchResultSet(ResultSet rs, Object input) throws Exception {
		while (rs.next()) {
			input = startProcessItem(rs.getString("TABLE_SCHEM"));
			executeInvalidateQuery();
		}
		return input;
	}

	@Override
	protected void beforeCloseResultSet() throws Exception {
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

}
