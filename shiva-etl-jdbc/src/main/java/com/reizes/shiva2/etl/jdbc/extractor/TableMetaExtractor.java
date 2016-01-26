package com.reizes.shiva2.etl.jdbc.extractor;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.reizes.shiva2.etl.jdbc.helper.JdbcColumnMetaData;
import com.reizes.shiva2.etl.jdbc.helper.JdbcTableMetaData;

/**
 * Database Connection으로부터 schema name list를 extract
 * @author reizes
 * @since 2.1.0
 * @since 2010.4.12
 */
public class TableMetaExtractor extends AbstractResultSetExtractor {
	private String catalog = null;
	private String schemaPattern = "%";
	private String tableNamePattern = "%";
	private String columnNamePattern = "%";
	private String[] types = new String[] {"TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY",
		"SYNONYM"};
	private boolean extractColumnInfo = false;
	private Connection conn;

	private void extractColumnMeta(JdbcTableMetaData meta) throws SQLException {
		DatabaseMetaData dbmeta = this.conn.getMetaData();
		ResultSet rs = dbmeta.getColumns(meta.getCatalogName(), meta.getSchemaName(), meta.getTableName(),
			columnNamePattern);
		ArrayList<JdbcColumnMetaData> list = new ArrayList<JdbcColumnMetaData>();

		while (rs.next()) {
			JdbcColumnMetaData column = new JdbcColumnMetaData();
			column.setFromResultSet(rs);
			list.add(column);
		}

		rs.close();
		meta.setColumns(list);
	}

	@Override
	protected ResultSet getResultSet(Connection conn) throws SQLException {
		this.conn = conn;
		DatabaseMetaData meta = conn.getMetaData();
		return meta.getTables(catalog, schemaPattern, tableNamePattern, types);
	}

	@Override
	protected Object fetchResultSet(ResultSet rs, Object input) throws Exception {
		while (rs.next()) {
			JdbcTableMetaData meta = new JdbcTableMetaData();
			meta.setFromResultSet(rs);

			if (extractColumnInfo) {
				extractColumnMeta(meta);
			}

			input = startProcessItem(meta);
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

	public String getSchemaPattern() {
		return schemaPattern;
	}

	public void setSchemaPattern(String schemaPattern) {
		this.schemaPattern = schemaPattern;
	}

	public String getTableNamePattern() {
		return tableNamePattern;
	}

	public void setTableNamePattern(String tableNamePattern) {
		this.tableNamePattern = tableNamePattern;
	}

	public String[] getTypes() {
		return types;
	}

	public void setTypes(String[] types) {
		this.types = types;
	}

	public boolean isExtractColumnInfo() {
		return extractColumnInfo;
	}

	public void setExtractColumnInfo(boolean extractColumnInfo) {
		this.extractColumnInfo = extractColumnInfo;
	}

}
