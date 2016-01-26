package com.reizes.shiva2.etl.jdbc.helper;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * JDBC Extractor의 컬럼 메타 데이터
 * @author reizes
 * @since 2.1.0
 * @since 2010.4.12
 */
public class JdbcColumnMetaData implements JdbcMetaData {
	private String columnName;
	private int dataType;
	private String typeName;
	private int columnSize;
	private int decimalDigits;
	private int numPrecRadix;
	private boolean nullable;
	private String remarks;
	private int charOctetLength;
	private int ordinalPosition;
	private boolean autoIncrement;
	private String className;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public int getColumnSize() {
		return columnSize;
	}

	public void setColumnSize(int columnSize) {
		this.columnSize = columnSize;
	}

	public int getDecimalDigits() {
		return decimalDigits;
	}

	public void setDecimalDigits(int decimalDigits) {
		this.decimalDigits = decimalDigits;
	}

	public int getNumPrecRadix() {
		return numPrecRadix;
	}

	public void setNumPrecRadix(int numPrecRadix) {
		this.numPrecRadix = numPrecRadix;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public int getCharOctetLength() {
		return charOctetLength;
	}

	public void setCharOctetLength(int charOctetLength) {
		this.charOctetLength = charOctetLength;
	}

	public int getOrdinalPosition() {
		return ordinalPosition;
	}

	public void setOrdinalPosition(int ordinalPosition) {
		this.ordinalPosition = ordinalPosition;
	}

	/**
	 * SQL Type을 JavaType으로 변환하여 클래스 이름 리턴 (java.lang 제외)
	 * @return
	 * @throws SQLException 
	 */
	private String convertJavaType(ResultSet rs) throws SQLException {
		switch (rs.getInt("DATA_TYPE")) {
			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT:
				return "java.lang.Integer";
			case Types.BIGINT:
				return "java.lang.Long";
			case Types.BINARY:
			case Types.BLOB:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
				return "byte[]";
			case Types.BIT:
				return "java.lang.Boolean";
			case Types.CHAR:
			case Types.CLOB:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case Types.LONGNVARCHAR:
				return "java.lang.String";
			case Types.DATE:
			case Types.TIME:
			case Types.TIMESTAMP:
				return "java.util.Date";
			case Types.DECIMAL:
			case Types.NUMERIC:
				return "java.math.BigDecimal";
			case Types.DOUBLE:
			case Types.FLOAT:
				return "java.lang.Double";
			case Types.REAL:
				return "java.lang.Float";
			case Types.JAVA_OBJECT:
			case Types.OTHER:
			case Types.NULL:
				return "java.lang.Object";
			default:
				return null;
		}
	}

	@Override
	public void setFromResultSet(ResultSet rs) throws SQLException {
		setColumnName(rs.getString("COLUMN_NAME"));
		setDataType(rs.getInt("DATA_TYPE"));
		setTypeName(rs.getString("TYPE_NAME"));
		setColumnSize(rs.getInt("COLUMN_SIZE"));
		setDecimalDigits(rs.getInt("DECIMAL_DIGITS"));
		setNumPrecRadix(rs.getInt("NUM_PREC_RADIX"));
		setNullable(rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
		setRemarks(rs.getString("REMARKS"));
		setCharOctetLength(rs.getInt("CHAR_OCTET_LENGTH"));
		setOrdinalPosition(rs.getInt("ORDINAL_POSITION"));
		setClassName(convertJavaType(rs));
		//setAutoIncrement("YES".equals(rs.getString("IS_AUTOINCREMENT")));
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isNullable() {
		return nullable;
	}

	@Override
	public String toString() {
		return String.format("%d %s (%s(%d) - %d) [%d,%d] %s - %s\n", ordinalPosition, columnName, typeName,
			columnSize, dataType, decimalDigits, numPrecRadix, nullable ? "nullable" : "notnull", remarks).toString();
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}
