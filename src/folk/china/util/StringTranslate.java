package folk.china.util;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author wangchen11
 *
 */
public class StringTranslate {
	public static String encodeList(List<String> strings)
	{
		String []array = new String[strings.size()];
		strings.toArray(array);
		return encodeArray(array);
	}
	
	public static List<String> decodeList(String string)
	{
		String []array = decodeArray(string);
		List<String> list = new LinkedList<String>();
		for(String str : array)
		{
			list.add(str);
		}
		return list;
	}
	
	public static String encodeArray(String array[])
	{
		StringBuilder stringBuilder = new StringBuilder();
		for(String str : array)
		{
			stringBuilder.append(encodeString(str));
			stringBuilder.append("#");
		}
		return stringBuilder.toString();
	}
	
	public static String[] decodeArray(String string)
	{
		return string.replaceAll("##", "#").split("#");
	}

	private static String encodeString(String string)
	{
		return string.replaceAll("#", "##");
	}
}
