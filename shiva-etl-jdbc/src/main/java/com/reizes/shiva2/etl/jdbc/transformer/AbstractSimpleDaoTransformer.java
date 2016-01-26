package com.reizes.shiva2.etl.jdbc.transformer;

import java.sql.SQLException;

import javax.sql.DataSource;

import com.reizes.shiva2.etl.core.AfterProcessAware;
import com.reizes.shiva2.etl.core.BeforeProcessAware;
import com.reizes.shiva2.etl.core.context.ProcessContext;
import com.reizes.shiva2.etl.core.transformer.AbstractTransformer;
import com.reizes.shiva2.etl.jdbc.dao.DataAccessObject;

/**
 * JDBC SimpleDAO를 사용할 수 있는 Transformer
 * @author reizes
 * @since 2009.10.12
 */
public abstract class AbstractSimpleDaoTransformer extends AbstractTransformer implements BeforeProcessAware,
	AfterProcessAware {
	private DataAccessObject dao;
	private DataSource dataSource;

	public AbstractSimpleDaoTransformer(DataSource ds) {
		this.dataSource = ds;
	}

	public DataAccessObject getDao() {
		return dao;
	}

	public void setDao(DataAccessObject dao) {
		this.dao = dao;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * ETL 프로세스 전 호출됨
	 * @param context -
	 * @param data -
	 * @throws Exception -
	 * @see com.reizes.shiva2.etl.core.BeforeProcessAware#onBeforeProcess(com.reizes.shiva2.etl.core.context.ProcessContext, java.lang.Object)
	 */
	@Override
	public void onBeforeProcess(ProcessContext context, Object data) throws Exception {
		dao = new DataAccessObject(dataSource);
	}

	/**
	 * ETL 프로세스 후 호출됨
	 * @param context -
	 * @param data -
	 * @throws SQLException -
	 * @see com.reizes.shiva2.etl.core.BeforeProcessAware#onBeforeProcess(com.reizes.shiva2.etl.core.context.ProcessContext, java.lang.Object)
	 */
	@Override
	public void onAfterProcess(ProcessContext context, Object data) throws SQLException {
		dao.close();
		dao = null;
	}

}
