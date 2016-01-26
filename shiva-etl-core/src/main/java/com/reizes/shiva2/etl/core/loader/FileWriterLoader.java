package com.reizes.shiva2.etl.core.loader;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import com.reizes.shiva2.etl.core.AfterProcessAware;
import com.reizes.shiva2.etl.core.context.ProcessContext;

/**
 * @author reizes
 * @since 2.1.0
 */
public class FileWriterLoader extends WriterLoader implements AfterProcessAware {
	private FileOutputStream fos;

	public FileWriterLoader(String path) throws FileNotFoundException, UnsupportedEncodingException {
		fos = new FileOutputStream(path);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF8");
		setWriter(osw);
	}

	@Override
	public void onAfterProcess(ProcessContext context, Object data) throws IOException {
		super.onAfterProcess(context, data);

		if (fos != null) {
			fos.close();
			fos = null;
		}
	}
}
