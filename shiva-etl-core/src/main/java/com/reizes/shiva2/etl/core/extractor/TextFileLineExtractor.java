package com.reizes.shiva2.etl.core.extractor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.reizes.shiva2.etl.core.NullArgumentException;

/**
 * 텍스트 파일 라인 Extractor.
 *
 * @author inho
 * @since 2009-11-02
 */
public class TextFileLineExtractor extends AbstractExtractor {

	/** The encoding. */
	private String encoding = "UTF-8";

	/** The skip lines. */
	private int skipLines = 0;

	/** The limit. */
	private int limit = 0;

	/** The print line number. */
	private boolean printLineNumber;

	/**
	 * 매개변수로 전달된 파일 경로의 파일을 한 라인씩 읽어서 전달한다.
	 * @param input 파일 경로
	 * @return input
	 * @throws Exception -
	 * @see com.reizes.shiva2.etl.core.EtlElement#doProcess(java.lang.Object)
	 */
	public Object doProcess(Object input) throws Exception {
		if (input == null) {
			throw new NullArgumentException("input");
		}

		int currentLine = 0;
		BufferedReader br = null;

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream((String)input), encoding));

			String line = null;

			while ((line = br.readLine()) != null) {
				currentLine++;

				if (printLineNumber) {
					System.out.println(currentLine);
				}
				
				if (currentLine <= skipLines) {
					continue;
				}
				
				if (limit > 0 && currentLine > limit) {
					break;
				}
				
				startProcessItem(line);
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}

		return input;
	}


	/**
	 * Sets the encoding.
	 * (default UTF-8)
	 *
	 * @param encoding the new encoding
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Sets the skip lines.
	 *
	 * @param skipLines the new skip lines
	 */
	public void setSkipLines(int skipLines) {
		this.skipLines = skipLines;
	}

	/**
	 * Sets the limit.
	 *
	 * @param limit the new limit
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}

	/**
	 * Sets the prints the line number.
	 *
	 * @param printLineNumber the new prints the line number
	 */
	public void setPrintLineNumber(boolean printLineNumber) {
		this.printLineNumber = printLineNumber;
	}
}
