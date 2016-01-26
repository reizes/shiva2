package com.reizes.shiva2.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

public class StringUtil {

	private static final Pattern UPPER_CASE = Pattern.compile("([A-Z])");

	public static String[] explode(String src, String sep) {
		if (src == null) {
			return null;
		}

		ArrayList<String> outputTmp = new ArrayList<String>();
		char[] buffer = src.toCharArray();
		char[] delimiter = sep.toCharArray();

		boolean separate = false;
		int start = 0;

		for (int i = 0; i < buffer.length; i++) {
			if (buffer[i] == delimiter[0]) {
				separate = true;

				if (i + delimiter.length <= buffer.length) {
					for (int j = 1; j < delimiter.length; j++) {
						if (buffer[i + j] != delimiter[j]) {
							separate = false;
							break;
						}
					}
					if (separate) {
						outputTmp.add(new String(buffer, start, i - start));
						i += delimiter.length - 1;
						start = i + 1;
					}
				}
			}
		}

		outputTmp.add(new String(buffer, start, buffer.length - start));
		return outputTmp.toArray(new String[0]);
	}

	public static String join(String[] strings, String delimiter) {
		StringBuilder sb = new StringBuilder();

		for (String string : strings) {
			if (string == null) {
				string = "";
			}
			if (sb.length() > 0) {
				sb.append(delimiter).append(string);
			} else {
				sb.append(string);
			}
		}

		return sb.toString();
	}

	public static String join(String[] strings, char delimiter) {
		StringBuilder sb = new StringBuilder();

		for (String string : strings) {
			if (string == null) {
				string = "";
			}
			if (sb.length() > 0) {
				sb.append(delimiter).append(string);
			} else {
				sb.append(string);
			}
		}

		return sb.toString();
	}

	public static String join(Collection<?> strings, char delimiter) {
		StringBuilder sb = new StringBuilder();

		for (Object string : strings) {
			if (string == null) {
				string = "";
			}
			if (sb.length() > 0) {
				sb.append(delimiter).append(string.toString());
			} else {
				sb.append(string.toString());
			}
		}

		return sb.toString();
	}

	public static String join(Collection<?> strings, String delimiter) {
		StringBuilder sb = new StringBuilder();

		for (Object string : strings) {
			if (string == null) {
				string = "";
			}
			if (sb.length() > 0) {
				sb.append(delimiter).append(string.toString());
			} else {
				sb.append(string.toString());
			}
		}

		return sb.toString();
	}

	public static String join(Number[] strings, String delimiter) {
		StringBuilder sb = new StringBuilder();

		for (Object string : strings) {
			if (string == null) {
				string = "";
			}
			if (sb.length() > 0) {
				sb.append(delimiter).append(string.toString());
			} else {
				sb.append(string.toString());
			}
		}

		return sb.toString();
	}

	public static String join(Number[] strings, char delimiter) {
		StringBuilder sb = new StringBuilder();

		for (Object string : strings) {
			if (string == null) {
				string = "";
			}
			if (sb.length() > 0) {
				sb.append(delimiter).append(string.toString());
			} else {
				sb.append(string.toString());
			}
		}

		return sb.toString();
	}

	public static String stripTag(String in) {
		return in.replaceAll("</?[^>]+>", "");
	}

	/**
	 * in에서 dash를 제거하고 각 dash의 앞 글자를 대문자로 하여 camel화 함
	 * @param in - string
	 * @return camel String
	 */
	public static String camelize(String in) {
		String camel = StringUtils.replaceChars(WordUtils.capitalizeFully(in, new char[] {'_', '-', '.'}), "_-.", null);
		return StringUtils.uncapitalize(camel);
	}

	public static String uncamelize(String fieldName) {
		StringBuffer sb = new StringBuffer();
		Matcher matcher = null;

		synchronized (UPPER_CASE) {
			matcher = UPPER_CASE.matcher(fieldName);
		}

		while (matcher.find()) {
			matcher.appendReplacement(sb, "_" + matcher.group(1).toLowerCase());
		}

		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 주어진 이름을 클래스 이름으로 변환
	 * @param in - 이름
	 * @return - 클래스 이름
	 */
	public static String beanNamelize(String in) {
		return StringUtils.replaceChars(WordUtils.capitalizeFully(in, new char[] {'_', '-', '.'}), "_-.", null);
	}

	/**
	 * @param data - 입력 스트링
	 * @return String - 정제된 스트링
	 */
	public static String normalize(String data) {
		data = StringUtils.remove(data, '\u0000'); // 2012.11.22 \u0000도 제거
		data = StringUtils.strip(data, " \t　"); // 2012.10.4 ㄱ+한자+1 특수기호 공백도 제거
		if (StringUtils.isBlank(data) || data.length() == 0) { // 2012.11.22 사이즈가 0이면 null 리턴
			return null;
		}

		return StringUtils.trim(data);
	}

	/**
	 * @param data String
	 * @return XML에 허용되지 않는 제어 문자를 제거한다.
	 */
	public static String removeInvalidXmlChar(String data) {
		if (StringUtils.isBlank(data))
			return null;

		return data.replaceAll("[\u0000-\u0008\u000B\u000C\u000E-\u001F\uFFFE\uFFFF]", "");
	}

	/**
	 * 지정 글자가 넘어가는 경우 자른다.
	 * 2.1.1 2012.7.5 reizes
	 * @param data
	 * @param length
	 * @return
	 */
	public static String truncate(String data, int length) {
		if (data != null) {
			return data.length() > length ? data.substring(0, length) : data;
		}

		return null;
	}
}
