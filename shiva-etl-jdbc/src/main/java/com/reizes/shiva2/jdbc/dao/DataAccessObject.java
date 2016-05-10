package com.reizes.shiva2.jdbc.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.reizes.shiva2.utils.StringUtil;

/**
 * <pre>
 * 1. 별다른 설정 없이 간단하게 사용 가능한 DAO 클래스로 사용 방법은 ibatis의 사용 방법과 유사하다.
 * 2. 문자열 변환의 경우 SQL 문에 $로 감싼 변수명을 입력하고 값 변환의 경우 #로 감싼 변수명을 입력한다.
 * 3. 파라미터 클래스와 리턴 클래스는 Map, JavaBeans, 기본 자료형만 사용 가능하다.
 * 4. 리턴 클래스는 반드시 디폴트 생성자가 있어야 한다.
 * 5. 기타 타입의 클래스를 사용할 경우 타입 핸들러가 등록되어 있지 않으면 기본으로 toString 값이 사용된다.
 * 6. 필요한 경우에는 TypeHandler를 구현해서 사용한다.
 * 7. 리턴 클래스의 경우 명시적으로 입력하지 않는 경우 Map&lt;String, Object&gt;가 기본으로 사용된다.
 * 8. RowHandler를 사용할 경우 반드시 streamResultMode를 true로 설정한다.
 * </pre>
 * @author inho
 * @since 2009-09-11
 */
public class DataAccessObject {
	private static final Log LOG = LogFactory.getLog(DataAccessObject.class);
	private static final Class<?> DEFAULT_RESULT_CLASS = HashMap.class;

	private static final Set<Class<?>> PRIMITIVE_CLASS = new HashSet<Class<?>>();

	static {
		PRIMITIVE_CLASS.add(Boolean.class);
		PRIMITIVE_CLASS.add(Byte.class);
		PRIMITIVE_CLASS.add(Character.class);
		PRIMITIVE_CLASS.add(Short.class);
		PRIMITIVE_CLASS.add(Integer.class);
		PRIMITIVE_CLASS.add(Long.class);
		PRIMITIVE_CLASS.add(Float.class);
		PRIMITIVE_CLASS.add(Double.class);
	}

	private Connection conn;
	private QueryBuilder queryBuilder;

	private Map<Class<?>, TypeHandler<?, ?>> handlerMap;
	private boolean streamResultMode = false;

	/**
	 * DAO 연결
	 * @param url 접속할 URL
	 * @param user 사용자 ID
	 * @param password 패스워드
	 * @param driverName JDBC 드라이버 이름
	 * @throws Exception -
	 */
	public DataAccessObject(String url, String user, String password, String driverName) throws Exception {
		Class.forName(driverName);
		conn = DriverManager.getConnection(url, user, password);
		initialize();
	}

	/**
	 * DAO 연결
	 * @param datasource 데이터 소스
	 * @throws SQLException -
	 */
	public DataAccessObject(DataSource datasource) throws SQLException {
		conn = datasource.getConnection();
		initialize();
	}

	private void initialize() {
		queryBuilder = new QueryBuilder();
		handlerMap = new HashMap<Class<?>, TypeHandler<?, ?>>();

		queryBuilder.setHandlerMap(handlerMap);

		handlerMap.put(Date.class, new DateTypeHandler());
		handlerMap.put(java.sql.Date.class, new SqlDateTypeHandler());
		handlerMap.put(java.sql.Time.class, new TimeTypeHandler());
		handlerMap.put(java.sql.Timestamp.class, new TimestampTypeHandler());
	}

	/**
	 * 타입 핸들러 추가
	 * @param typeClass 클래스 타입
	 * @param typeHandler 클래스 타입 핸들러
	 */
	public void addTypeHandler(Class<?> typeClass, TypeHandler<?, ?> typeHandler) {
		handlerMap.put(typeClass, typeHandler);
	}

	/**
	 * @return 스트림 모드 여부
	 */
	public boolean isStreamResultMode() {
		return streamResultMode;
	}

	/**
	 * 스트림 모드를 설정한다. RowHandler를 사용할 때 true로 설정한다.
	 * @param streamResultMode 스트림 모드
	 */
	public void setStreamResultMode(boolean streamResultMode) {
		this.streamResultMode = streamResultMode;
	}

	/**
	 * DAO 종료
	 * @throws SQLException -
	 */
	public void close() throws SQLException {
		conn.close();
	}

	/**
	 * 트랜잭션 시작
	 * @throws SQLException -
	 */
	public void startTransaction() throws SQLException {
		conn.setAutoCommit(false);
	}

	/**
	 * 트랜잭션 종료 (commit)
	 * @throws SQLException -
	 */
	public void endTransaction() throws SQLException {
		conn.commit();
		conn.setAutoCommit(true);
	}

	/**
	 * 트랜잭션 종료 (rollback)
	 * @throws SQLException -
	 */
	public void rollbackTransaction() throws SQLException {
		conn.rollback();
		conn.setAutoCommit(true);
	}

	/**
	 * 하나의 결과 값을 갖는 SQL문을 실행한다. (2개 이상의 값이 나올 경우 null이 리턴된다.)
	 * @param query SQL문
	 * @return Map&lt;String, Object&gt; 형태의 결과 값
	 * @throws Exception -
	 */
	public Map<String, Object> executeSelectSingle(String query) throws Exception {
		return (Map<String, Object>)executeSelectSingle(query, null);
	}

	/**
	 * 하나의 결과 값을 갖는 SQL문을 실행한다. (2개 이상의 값이 나올 경우 null이 리턴된다.)
	 * @param query SQL문
	 * @param param SQL 파라미터
	 * @return Map&lt;String, Object&gt; 형태의 결과 값
	 * @throws Exception -
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> executeSelectSingle(String query, Object param) throws Exception {
		return (Map<String, Object>)executeSelectSingle(query, param, DEFAULT_RESULT_CLASS);
	}

	/**
	 * 하나의 결과 값을 갖는 SQL문을 실행한다.
	 * @param query SQL문
	 * @param param SQL 파라미터
	 * @param resultClass 결과 값 클래스 타입
	 * @return 결과 값
	 * @throws Exception 2개 이상의 값이 나올 경우
	 */
	public <T> T executeSelectSingle(String query, Object param, Class<T> resultClass) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = prepareStatement(queryBuilder.build(query, param));
			rs = ps.executeQuery();

			T result = null;

			if (rs.next()) {
				result = getResultFromResultSet(rs, getResultObject(resultClass));

				if (rs.next()) {
					if (streamResultMode) {
						while (rs.next()) {
							continue;
						}
					}

					throw new IllegalStateException("There is more than one result.");
				}
			}

			return result;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				LOG.warn(e);
			}
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				LOG.warn(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T getResultObject(Class<T> resultClass) throws Exception {
		if (PRIMITIVE_CLASS.contains(resultClass) || resultClass.isPrimitive()) {
			return (T)ConvertUtils.convert(new Object(), resultClass);
		}

		return resultClass.newInstance();
	}

	/**
	 * 하나 이상의 결과 값을 갖는 SQL문을 실행한다.
	 * @param query SQL문
	 * @return Map&lt;String, Object&gt; 형태의 결과 값 리스트
	 * @throws Exception -
	 */
	public List<Map<String, Object>> executeSelect(String query) throws Exception {
		return executeSelect(query, null);
	}

	/**
	 * 하나 이상의 결과 값을 갖는 SQL문을 실행한다.
	 * @param query SQL문
	 * @param param SQL 파라미터
	 * @return Map&lt;String, Object&gt; 형태의 결과 값 리스트
	 * @throws Exception -
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> executeSelect(String query, Object param) throws Exception {
		return (List<Map<String, Object>>)executeSelect(query, param, DEFAULT_RESULT_CLASS);
	}

	/**
	 * 하나 이상의 결과 값을 갖는 SQL문을 실행한다.
	 * @param query SQL문
	 * @param param SQL 파라미터
	 * @param resultClass 결과 값 클래스 타입
	 * @return 결과 값 리스트
	 * @throws Exception -
	 */
	public <T> List<T> executeSelect(String query, Object param, Class<T> resultClass) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = prepareStatement(queryBuilder.build(query, param));
			rs = ps.executeQuery();

			List<T> resultList = new ArrayList<T>();

			while (rs.next()) {
				T result = getResultFromResultSet(rs, getResultObject(resultClass));
				resultList.add(result);
			}

			return resultList;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				LOG.warn(e);
			}
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				LOG.warn(e);
			}
		}
	}

	/**
	 * SQL문의 실행 결과 값을 RowHandler를 통해 하나씩 처리한다.
	 * @param query SQL문
	 * @param handler RowHandler
	 * @throws Exception -
	 */
	public void executeSelectWithHandler(String query, RowHandler handler) throws Exception {
		executeSelectWithHandler(query, null, handler);
	}

	/**
	 * SQL문의 실행 결과 값을 RowHandler를 통해 하나씩 처리한다.
	 * @param query SQL문
	 * @param param SQL 파라미터
	 * @param handler RowHandler
	 * @throws Exception -
	 */
	public void executeSelectWithHandler(String query, Object param, RowHandler handler) throws Exception {
		executeSelectWithHandler(query, param, DEFAULT_RESULT_CLASS, handler);
	}

	/**
	 * SQL문의 실행 결과 값을 RowHandler를 통해 하나씩 처리한다.
	 * @param query SQL문
	 * @param param SQL 파라미터
	 * @param resultClass 결과 값 클래스 타입
	 * @param handler RowHandler
	 * @throws Exception -
	 */
	public <T> void executeSelectWithHandler(String query, Object param, Class<T> resultClass, RowHandler handler) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = prepareStatement(queryBuilder.build(query, param));
			rs = ps.executeQuery();

			while (rs.next()) {
				handler.handle(getResultFromResultSet(rs, getResultObject(resultClass)));
			}
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				LOG.warn(e);
			}
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				LOG.warn(e);
			}
		}
	}

	/**
	 * INSERT를 실행하고 LAST_INSERT_ID를 얻는다.
	 * @author reizes
	 * @param query SQL문
	 * @return last_insert_id
	 * @throws Exception -
	 */
	public Object executeInsertGetLastId(String query) throws Exception {
		return executeInsertGetLastId(query, null);
	}

	/**
	 * INSERT를 실행하고 LAST_INSERT_ID를 얻는다.
	 * @param query SQL문
	 * @param param SQL 파라미터
	 * @return last_insert_id
	 * @throws Exception -
	 */
	public Object executeInsertGetLastId(String query, Object param) throws Exception {
		executeQuery(query, param);

		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");

			if (rs.next()) {
				return rs.getLong(1);
			}
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				LOG.warn(e);
			}
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				LOG.warn(e);
			}
		}
		return null;
	}

	/**
	 * INSERT, UPDATE, DELETE 등의 SQL문을 실행한다.
	 * @param query SQL문
	 * @return 실행 결과
	 * @throws Exception -
	 */
	public int executeQuery(String query) throws Exception {
		return executeQuery(query, null);
	}

	/**
	 * INSERT, UPDATE, DELETE 등의 SQL문을 실행한다.
	 * @param query SQL문
	 * @param param SQL 파라미터
	 * @return 실행 결과
	 * @throws Exception -
	 */
	public int executeQuery(String query, Object param) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = prepareStatement(queryBuilder.build(query, param));

			if (ps.execute()) {
				int resultCount = 0;
				rs = ps.getResultSet();

				while (rs.next()) {
					resultCount++;
				}

				return resultCount;
			}

			return ps.getUpdateCount();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				LOG.warn(e);
			}
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				LOG.warn(e);
			}
		}
	}

	private PreparedStatement prepareStatement(Query query) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(query.getQuery());
		if (streamResultMode) {
			ps.setFetchSize(Integer.MIN_VALUE);
		}

		List<Object> params = query.getParameters();
		ps.clearParameters();

		if (params != null) {
			for (int i = 0, size = params.size(); i < size; i++) {
				ps.setObject(i + 1, params.get(i));
			}
		}

		return ps;
	}

	@SuppressWarnings("unchecked")
	private <T> T getResultFromResultSet(ResultSet rs, T resultObject) throws Exception {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();

		for (int i = 1; i <= columnCount; i++) {
			String label = StringUtils.isBlank(rsmd.getColumnLabel(i)) ? rsmd.getColumnName(i) : rsmd.getColumnLabel(i);
			String property = StringUtil.camelize(label);

			Object value = rs.getObject(i);

			if (value == null) {
				continue;
			}

			if (resultObject instanceof Map) {
				((Map<String, Object>)resultObject).put(label, value);
			} else if (PropertyUtils.isWriteable(resultObject, property)) {
				Class<?> targetType = PropertyUtils.getPropertyType(resultObject, property);
				TypeHandler<?, ?> handler = handlerMap.get(targetType);

				if (handler != null) {
					PropertyUtils.setSimpleProperty(resultObject, property, handler.getJavaTypeValue(value));
				} else {
					PropertyUtils.setSimpleProperty(resultObject, property, ConvertUtils.convert(value, targetType));
				}
			} else {
				if (columnCount > 1) {
					throw new IllegalStateException(resultObject.getClass().getName() + " can contain only one column.");
				}

				TypeHandler<?, ?> handler = handlerMap.get(resultObject.getClass());

				if (handler != null) {
					return (T)handler.getJavaTypeValue(value);
				} else {
					return (T)ConvertUtils.convert(value, resultObject.getClass());
				}
			}
		}

		return resultObject;
	}

}