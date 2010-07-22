/*
 * Copyright 2010 the original author or authors.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.schildbach.pte;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Andreas Schildbach
 */
public final class ParserUtils
{
	private static final String SCRAPE_USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.2) Gecko/20100115 Firefox/3.6 (.NET CLR 3.5.30729)";
	private static final int SCRAPE_INITIAL_CAPACITY = 4096;

	public static CharSequence scrape(final String url) throws IOException
	{
		final StringBuilder buffer = new StringBuilder(SCRAPE_INITIAL_CAPACITY);
		final URLConnection connection = new URL(url).openConnection();
		connection.addRequestProperty("User-Agent", SCRAPE_USER_AGENT);
		final Reader pageReader = new InputStreamReader(connection.getInputStream(), "ISO-8859-1");

		final char[] buf = new char[SCRAPE_INITIAL_CAPACITY];
		while (true)
		{
			final int read = pageReader.read(buf);
			if (read == -1)
				break;
			buffer.append(buf, 0, read);
		}

		pageReader.close();
		return buffer;
	}

	private static final Pattern P_ENTITY = Pattern.compile("&(?:#(x[\\da-f]+|\\d+)|(amp|quot|apos));");

	public static String resolveEntities(final CharSequence str)
	{
		if (str == null)
			return null;

		final Matcher matcher = P_ENTITY.matcher(str);
		final StringBuilder builder = new StringBuilder(str.length());
		int pos = 0;
		while (matcher.find())
		{
			final char c;
			final String code = matcher.group(1);
			if (code != null)
			{
				if (code.charAt(0) == 'x')
					c = (char) Integer.valueOf(code.substring(1), 16).intValue();
				else
					c = (char) Integer.parseInt(code);
			}
			else
			{
				final String namedEntity = matcher.group(2);
				if (namedEntity.equals("amp"))
					c = '&';
				else if (namedEntity.equals("quot"))
					c = '"';
				else if (namedEntity.equals("apos"))
					c = '\'';
				else
					throw new IllegalStateException("unknown entity: " + namedEntity);
			}
			builder.append(str.subSequence(pos, matcher.start()));
			builder.append(c);
			pos = matcher.end();
		}
		builder.append(str.subSequence(pos, str.length()));
		return builder.toString();
	}

	public static Date parseDate(String str)
	{
		try
		{
			return new SimpleDateFormat("dd.MM.yy").parse(str);
		}
		catch (ParseException x)
		{
			throw new RuntimeException(x);
		}
	}

	public static Date parseTime(String str)
	{
		try
		{
			return new SimpleDateFormat("HH:mm").parse(str);
		}
		catch (ParseException x)
		{
			throw new RuntimeException(x);
		}
	}

	public static Date joinDateTime(Date date, Date time)
	{
		final Calendar cDate = new GregorianCalendar();
		cDate.setTime(date);
		final Calendar cTime = new GregorianCalendar();
		cTime.setTime(time);
		cTime.set(Calendar.YEAR, cDate.get(Calendar.YEAR));
		cTime.set(Calendar.MONTH, cDate.get(Calendar.MONTH));
		cTime.set(Calendar.DAY_OF_MONTH, cDate.get(Calendar.DAY_OF_MONTH));
		return cTime.getTime();
	}

	public static long timeDiff(Date d1, Date d2)
	{
		return d1.getTime() - d2.getTime();
	}

	public static Date addDays(Date time, int days)
	{
		final Calendar c = new GregorianCalendar();
		c.setTime(time);
		c.add(Calendar.DAY_OF_YEAR, days);
		return c.getTime();
	}

	public static void printGroups(Matcher m)
	{
		final int groupCount = m.groupCount();
		for (int i = 1; i <= groupCount; i++)
			System.out.println("group " + i + ":'" + m.group(i) + "'");
	}

	public static String urlEncode(String part)
	{
		try
		{
			return URLEncoder.encode(part, "utf-8");
		}
		catch (UnsupportedEncodingException x)
		{
			throw new RuntimeException(x);
		}
	}
}