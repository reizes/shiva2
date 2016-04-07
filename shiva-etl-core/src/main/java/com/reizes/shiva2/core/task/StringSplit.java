package com.reizes.shiva2.core.task;

import org.apache.commons.lang3.StringUtils;

import com.reizes.shiva2.utils.StringUtil;

/**
 * delimiter에 의해 String을 분리
 * @since 2.0.2 - normalize property 추가
 * @since 2.1.5 - stripChars property 추가
 * @author reizes
 */
public class StringSplit extends AbstractTask {
	private String delimiter = "\t"; // default TAB character
	private boolean normalize = false;
	private String stripChars = null;

	@Override
	public Object doProcess(Object input) throws Exception {
		if (input instanceof String) {
			//String[] resultStringUtils.split((String)input,delimiter);
			String[] result = StringUtil.explode((String)input, delimiter);

			if (stripChars != null) {
				for (int i = 0; i < result.length; i++) {
					result[i] = StringUtils.strip(result[i], stripChars);
				}
			}

			if (normalize) {
				for (int i = 0; i < result.length; i++) {
					result[i] = StringUtil.normalize(result[i]);
				}
			}

			return result;
		}

		return input;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public boolean isNormalize() {
		return normalize;
	}

	public void setNormalize(boolean normalize) {
		this.normalize = normalize;
	}

	public String getStripChars() {
		return stripChars;
	}

	public void setStripChars(String stripChars) {
		this.stripChars = stripChars;
	}

}
