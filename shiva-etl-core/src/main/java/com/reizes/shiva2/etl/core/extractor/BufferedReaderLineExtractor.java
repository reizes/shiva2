package com.reizes.shiva2.etl.core.extractor;

import java.io.BufferedReader;
import java.io.IOException;

import com.reizes.shiva2.etl.core.AfterProcessAware;
import com.reizes.shiva2.etl.core.context.ProcessContext;
import com.reizes.shiva2.utils.StringUtil;

/**
 * 
 * @author reizes
 * @since 2.1.5 - skipNullLine, normalize property 추가
 */
public class BufferedReaderLineExtractor extends AbstractExtractor implements AfterProcessAware {
	//
	private BufferedReader reader;
	private int skipLines = 0;
	private int limit = 0;
	private boolean printLineNumber;
	private boolean skipNullLine = false; // 2.1.5 
	private boolean normalize = false; // 2.1.5

	public BufferedReaderLineExtractor() {

	}

	public BufferedReaderLineExtractor(BufferedReader reader) {
		this.setReader(reader);
	}

	@Override
	public void onAfterProcess(ProcessContext context, Object data) throws IOException {
		if (reader != null) {
			this.reader.close();
			this.reader = null;
		}
	}

	@Override
	public Object doProcess(Object input) throws Exception {
		Object output = null;
		int lines = 0;

		do {
			String line = reader.readLine();

			if (printLineNumber) {
				System.out.println(lines);
			}

			if (line == null) {
				break;
			}

			if (normalize) { // 2.1.5
				line = StringUtil.normalize(line);
			}

			if (skipNullLine && line == null) { // 2.1.5
				continue;
			}

			lines++;

			if (lines <= this.skipLines) {
				continue;
			}

			output = startProcessItem(line);

			if (limit > 0 && lines > limit) {
				break; // 2009.8.31 apply limit
			}

		} while (true);

		return output;
	}

	public BufferedReader getReader() {
		return reader;
	}

	public void setReader(BufferedReader reader) {
		this.reader = reader;
	}

	public int getSkipLines() {
		return skipLines;
	}

	public void setSkipLines(int skipLines) {
		this.skipLines = skipLines;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public void setPrintLineNumber(boolean printLineNumber) {
		this.printLineNumber = printLineNumber;
	}

	public boolean isSkipNullLine() {
		return skipNullLine;
	}

	public void setSkipNullLine(boolean skipNullLine) {
		this.skipNullLine = skipNullLine;
	}

	public boolean isNormalize() {
		return normalize;
	}

	public void setNormalize(boolean normalize) {
		this.normalize = normalize;
	}
}
