package com.reizes.shiva2.jdbc.helper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * TableNameExtractor에서 사용되는 모델
 * @author reizes
 * @since 2.1.0
 * @since 2010.4.12
 */
public class JdbcTableMetaData implements JdbcMetaData {
	private String catalogName;
	private String schemaName;
	private String tableName;
	private String tableType;
	private String remarks;
	private List<JdbcColumnMetaData> columns;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Override
	public String toString() {
		return String.format("%s.%s.%s (%s) - %s %s", catalogName, schemaName, tableName, tableType, remarks,
			(columns != null ? ("\n" + columns.toString()) : ""));
	}

	@Override
	public void setFromResultSet(ResultSet rs) throws SQLException {
		setCatalogName(rs.getString("TABLE_CAT"));
		setSchemaName(rs.getString("TABLE_SCHEM"));
		setRemarks(rs.getString("REMARKS"));
		setTableName(rs.getString("TABLE_NAME"));
		setTableType(rs.getString("TABLE_TYPE"));
	}

	public List<JdbcColumnMetaData> getColumns() {
		return columns;
	}

	public void setColumns(List<JdbcColumnMetaData> columns) {
		this.columns = columns;
	}

	public String getCatalogName() {
		return catalogName;
	}

	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
}
