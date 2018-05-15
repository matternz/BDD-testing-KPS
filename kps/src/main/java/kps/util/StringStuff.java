package kps.util;

public class StringStuff {
	/**
	 * Returns a lower-case version of a string with '_' replaced with ' '
	 * and the first letter capitalised
	 * @param enumValueString
	 * @return
	 */
	public static String[] enumValuesToHumanReadable(Object[] values) {
		String[] ret = new String[values.length];
		
		for (int i = 0; i < values.length; i++) {
			ret[i] = enumValueToHumanReadable(values[i].toString());
		}
		return ret;
	}
	
	/**
	 * Returns a lower-case version of a string with '_' replaced with ' '
	 * and the first letter capitalised
	 * @param enumValueString
	 * @return
	 */
	public static String enumValueToHumanReadable(String enumValueString) {
		return enumValueString.substring(0,1)
				+ enumValueString.toLowerCase().replace('_', ' ').substring(1);
	}
}
