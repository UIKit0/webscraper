/*
 i* Copyright (C) 2011 WebSquared Inc. http://websqrd.com
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.websqrd.catbot.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLUtils {

	private final static Logger logger = LoggerFactory.getLogger(HTMLUtils.class);
	private static String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // script
																											// pattern
	private static String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // style
																											// pattern
	private static String regEx_html = "<[^>]*>"; // html pattern
	// private static String regEx_html =
	// "<[/!]?[a-zA-Z]*(\\s)*(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>"; //html pattern
	private static String regEx_add1 = "<!--[\\s\\S가-힣ㄱ-ㅎ]+?-->";

	private static Pattern p_script = Pattern.compile(regEx_script,
			Pattern.CASE_INSENSITIVE);
	private static Pattern p_style = Pattern.compile(regEx_style,
			Pattern.CASE_INSENSITIVE);
	private static Pattern p_html = Pattern.compile(regEx_html,
			Pattern.CASE_INSENSITIVE);
	private static Pattern p_add1 = Pattern.compile(regEx_add1,
			Pattern.CASE_INSENSITIVE);

	public static String removeTag(String targetString) throws HTMLException {

		String htmlStr = targetString;
		try {
			Matcher m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // clean script

			Matcher m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // clean style

			Matcher m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // clean html

			Matcher m_add1 = p_add1.matcher(htmlStr);
			htmlStr = m_add1.replaceAll(""); // clean add1

			return htmlStr;

		} catch (Exception e) {
			logger.error("HTML Tag clean Error:" + e.getMessage());
			throw new HTMLException(e);
		}
	}

	public static String clean(String htmlString) {
		String result = "";
		HtmlCleaner htmlCleaner = new HtmlCleaner();
		try {
			TagNode root = htmlCleaner.clean(htmlString);
			List<TagNode> list = root.getChildren();
			Iterator<TagNode> it = list.iterator();
			while (it.hasNext()) {
				TagNode node = (TagNode) it.next();
				if ("body".equals(node.getName())) {
					result = node.getText().toString().replaceAll("\\s{2}", "");
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}

		return result;
	}

	/**
	 * Escape characters for text appearing in HTML markup.
	 * 
	 * <P>
	 * This method exists as a defence against Cross Site Scripting (XSS) hacks.
	 * The idea is to neutralize control characters commonly used by scripts,
	 * such that they will not be executed by the browser. This is done by
	 * replacing the control characters with their escaped equivalents. See
	 * {@link hirondelle.web4j.security.SafeText} as well.
	 * 
	 * <P>
	 * The following characters are replaced with corresponding HTML character
	 * entities :
	 * <table border='1' cellpadding='3' cellspacing='0'>
	 * <tr>
	 * <th>Character</th>
	 * <th>Replacement</th>
	 * </tr>
	 * <tr>
	 * <td><</td>
	 * <td>&lt;</td>
	 * </tr>
	 * <tr>
	 * <td>></td>
	 * <td>&gt;</td>
	 * </tr>
	 * <tr>
	 * <td>&</td>
	 * <td>&amp;</td>
	 * </tr>
	 * <tr>
	 * <td>"</td>
	 * <td>&quot;</td>
	 * </tr>
	 * <tr>
	 * <td>\t</td>
	 * <td>&#009;</td>
	 * </tr>
	 * <tr>
	 * <td>!</td>
	 * <td>&#033;</td>
	 * </tr>
	 * <tr>
	 * <td>#</td>
	 * <td>&#035;</td>
	 * </tr>
	 * <tr>
	 * <td>$</td>
	 * <td>&#036;</td>
	 * </tr>
	 * <tr>
	 * <td>%</td>
	 * <td>&#037;</td>
	 * </tr>
	 * <tr>
	 * <td>'</td>
	 * <td>&#039;</td>
	 * </tr>
	 * <tr>
	 * <td>(</td>
	 * <td>&#040;</td>
	 * </tr>
	 * <tr>
	 * <td>)</td>
	 * <td>&#041;</td>
	 * </tr>
	 * <tr>
	 * <td></td>
	 * <td>&#042;</td>
	 * </tr>
	 * <tr>
	 * <td>+</td>
	 * <td>&#043;</td>
	 * </tr>
	 * <tr>
	 * <td>,</td>
	 * <td>&#044;</td>
	 * </tr>
	 * <tr>
	 * <td>-</td>
	 * <td>&#045;</td>
	 * </tr>
	 * <tr>
	 * <td>.</td>
	 * <td>&#046;</td>
	 * </tr>
	 * <tr>
	 * <td>/</td>
	 * <td>&#047;</td>
	 * </tr>
	 * <tr>
	 * <td>:</td>
	 * <td>&#058;</td>
	 * </tr>
	 * <tr>
	 * <td>;</td>
	 * <td>&#059;</td>
	 * </tr>
	 * <tr>
	 * <td>=</td>
	 * <td>&#061;</td>
	 * </tr>
	 * <tr>
	 * <td>?</td>
	 * <td>&#063;</td>
	 * </tr>
	 * <tr>
	 * <td>@</td>
	 * <td>&#064;</td>
	 * </tr>
	 * <tr>
	 * <td>[</td>
	 * <td>&#091;</td>
	 * </tr>
	 * <tr>
	 * <td>\</td>
	 * <td>&#092;</td>
	 * </tr>
	 * <tr>
	 * <td>]</td>
	 * <td>&#093;</td>
	 * </tr>
	 * <tr>
	 * <td>^</td>
	 * <td>&#094;</td>
	 * </tr>
	 * <tr>
	 * <td>_</td>
	 * <td>&#095;</td>
	 * </tr>
	 * <tr>
	 * <td>`</td>
	 * <td>&#096;</td>
	 * </tr>
	 * <tr>
	 * <td>{</td>
	 * <td>&#123;</td>
	 * </tr>
	 * <tr>
	 * <td>|</td>
	 * <td>&#124;</td>
	 * </tr>
	 * <tr>
	 * <td></td>
	 * <td>&#125;</td>
	 * </tr>
	 * <tr>
	 * <td>~</td>
	 * <td>&#126;</td>
	 * </tr>
	 * </table>
	 * 
	 * <P>
	 * Note that JSTL's {@code <c:out>} escapes <em>only the first 
    five</em> of the above
	 * characters.
	 */
	public static String forHTML(String aText) {
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(
				aText);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) {
			if (character == '<') {
				result.append("&lt;");
			} else if (character == '>') {
				result.append("&gt;");
			} else if (character == '&') {
				result.append("&amp;");
			} else if (character == '\"') {
				result.append("&quot;");
			} else if (character == '\t') {
				addCharEntity(9, result);
			} else if (character == '!') {
				addCharEntity(33, result);
			} else if (character == '#') {
				addCharEntity(35, result);
			} else if (character == '$') {
				addCharEntity(36, result);
			} else if (character == '%') {
				addCharEntity(37, result);
			} else if (character == '\'') {
				addCharEntity(39, result);
			} else if (character == '(') {
				addCharEntity(40, result);
			} else if (character == ')') {
				addCharEntity(41, result);
			} else if (character == '*') {
				addCharEntity(42, result);
			} else if (character == '+') {
				addCharEntity(43, result);
			} else if (character == ',') {
				addCharEntity(44, result);
			} else if (character == '-') {
				addCharEntity(45, result);
			} else if (character == '.') {
				addCharEntity(46, result);
			} else if (character == '/') {
				addCharEntity(47, result);
			} else if (character == ':') {
				addCharEntity(58, result);
			} else if (character == ';') {
				addCharEntity(59, result);
			} else if (character == '=') {
				addCharEntity(61, result);
			} else if (character == '?') {
				addCharEntity(63, result);
			} else if (character == '@') {
				addCharEntity(64, result);
			} else if (character == '[') {
				addCharEntity(91, result);
			} else if (character == '\\') {
				addCharEntity(92, result);
			} else if (character == ']') {
				addCharEntity(93, result);
			} else if (character == '^') {
				addCharEntity(94, result);
			} else if (character == '_') {
				addCharEntity(95, result);
			} else if (character == '`') {
				addCharEntity(96, result);
			} else if (character == '{') {
				addCharEntity(123, result);
			} else if (character == '|') {
				addCharEntity(124, result);
			} else if (character == '}') {
				addCharEntity(125, result);
			} else if (character == '~') {
				addCharEntity(126, result);
			} else {
				// the char is not a special one
				// add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}

	private static void addCharEntity(Integer aIdx, StringBuilder aBuilder) {
		String padding = "";
		if (aIdx <= 9) {
			padding = "00";
		} else if (aIdx <= 99) {
			padding = "0";
		} else {
			// no prefix
		}
		String number = padding + aIdx.toString();
		aBuilder.append("&#" + number + ";");
	}

	/**
	 * Escape all ampersand characters in a URL.
	 * 
	 * <P>
	 * Replaces all <tt>'&'</tt> characters with <tt>'&amp;'</tt>.
	 * 
	 * <P>
	 * An ampersand character may appear in the query string of a URL. The
	 * ampersand character is indeed valid in a URL.
	 * <em>However, URLs usually appear as an <tt>HREF</tt> attribute, and 
   such attributes have the additional constraint that ampersands 
   must be escaped.</em>
	 * 
	 * <P>
	 * The JSTL <c:url> tag does indeed perform proper URL encoding of query
	 * parameters. But it does not, in general, produce text which is valid as
	 * an <tt>HREF</tt> attribute, simply because it does not escape the
	 * ampersand character. This is a nuisance when multiple query parameters
	 * appear in the URL, since it requires a little extra work.
	 */
	public static String forHrefAmpersand(String aURL) {
		return aURL.replace("&", "&amp;");
	}

	/**
	 * Synonym for <tt>URLEncoder.encode(String, "UTF-8")</tt>.
	 * 
	 * <P>
	 * Used to ensure that HTTP query strings are in proper form, by escaping
	 * special characters such as spaces.
	 * 
	 * <P>
	 * It is important to note that if a query string appears in an
	 * <tt>HREF</tt> attribute, then there are two issues - ensuring the query
	 * string is valid HTTP (it is URL-encoded), and ensuring it is valid HTML
	 * (ensuring the ampersand is escaped).
	 */
	public static String forURL(String aURLFragment) {
		String result = null;
		try {
			result = URLEncoder.encode(aURLFragment, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException("UTF-8 not supported", ex);
		}
		return result;
	}

	/**
	 * Escape characters for text appearing as XML data, between tags.
	 * 
	 * <P>
	 * The following characters are replaced with corresponding character
	 * entities :
	 * <table border='1' cellpadding='3' cellspacing='0'>
	 * <tr>
	 * <th>Character</th>
	 * <th>Encoding</th>
	 * </tr>
	 * <tr>
	 * <td><</td>
	 * <td>&lt;</td>
	 * </tr>
	 * <tr>
	 * <td>></td>
	 * <td>&gt;</td>
	 * </tr>
	 * <tr>
	 * <td>&</td>
	 * <td>&amp;</td>
	 * </tr>
	 * <tr>
	 * <td>"</td>
	 * <td>&quot;</td>
	 * </tr>
	 * <tr>
	 * <td>'</td>
	 * <td>&#039;</td>
	 * </tr>
	 * </table>
	 * 
	 * <P>
	 * Note that JSTL's {@code <c:out>} escapes the exact same set of characters
	 * as this method. <span class='highlight'>That is, {@code <c:out>} is good
	 * for escaping to produce valid XML, but not for producing safe
	 * HTML.</span>
	 */
	public static String forXML(String aText) {
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(
				aText);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) {
			if (character == '<') {
				result.append("&lt;");
			} else if (character == '>') {
				result.append("&gt;");
			} else if (character == '\"') {
				result.append("&quot;");
			} else if (character == '\'') {
				result.append("&#039;");
			} else if (character == '&') {
				result.append("&amp;");
			} else {
				// the char is not a special one
				// add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}

	public static String fromXML(String aText) {
		return aText.replaceAll("\\&amp;", "&").replaceAll("\\&lt;", "<").replaceAll("\\&gt;", ">")
				.replaceAll("\\&quot;;", "\"").replaceAll("\\&#039;", "'")
				;
	}

	/**
	 * Escapes characters for text appearing as data in the <a
	 * href='http://www.json.org/'>Javascript Object Notation</a> (JSON) data
	 * interchange format.
	 * 
	 * <P>
	 * The following commonly used control characters are escaped :
	 * <table border='1' cellpadding='3' cellspacing='0'>
	 * <tr>
	 * <th>Character</th>
	 * <th>Escaped As</th>
	 * </tr>
	 * <tr>
	 * <td>"</td>
	 * <td>\"</td>
	 * </tr>
	 * <tr>
	 * <td>\</td>
	 * <td>\\</td>
	 * </tr>
	 * <tr>
	 * <td>/</td>
	 * <td>\/</td>
	 * </tr>
	 * <tr>
	 * <td>back space</td>
	 * <td>\b</td>
	 * </tr>
	 * <tr>
	 * <td>form feed</td>
	 * <td>\f</td>
	 * </tr>
	 * <tr>
	 * <td>line feed</td>
	 * <td>\n</td>
	 * </tr>
	 * <tr>
	 * <td>carriage return</td>
	 * <td>\r</td>
	 * </tr>
	 * <tr>
	 * <td>tab</td>
	 * <td>\t</td>
	 * </tr>
	 * </table>
	 * 
	 * <P>
	 * See <a href='http://www.ietf.org/rfc/rfc4627.txt'>RFC 4627</a> for more
	 * information.
	 */
	public static String forJSON(String aText) {
		final StringBuilder result = new StringBuilder();
		StringCharacterIterator iterator = new StringCharacterIterator(aText);
		char character = iterator.current();
		while (character != StringCharacterIterator.DONE) {
			if (character == '\"') {
				result.append("\\\"");
			} else if (character == '\\') {
				result.append("\\\\");
			} else if (character == '/') {
				result.append("\\/");
			} else if (character == '\b') {
				result.append("\\b");
			} else if (character == '\f') {
				result.append("\\f");
			} else if (character == '\n') {
				result.append("\\n");
			} else if (character == '\r') {
				result.append("\\r");
			} else if (character == '\t') {
				result.append("\\t");
			} else {
				// the char is not a special one
				// add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}

/**
   Return <tt>aText</tt> with all <tt>'<'</tt> and <tt>'>'</tt> characters
   replaced by their escaped equivalents.
  */
	public static String toDisableTags(String aText) {
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(
				aText);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) {
			if (character == '<') {
				result.append("&lt;");
			} else if (character == '>') {
				result.append("&gt;");
			} else {
				// the char is not a special one
				// add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}

	/**
	 * Replace characters having special meaning in regular expressions with
	 * their escaped equivalents, preceded by a '\' character.
	 * 
	 * <P>
	 * The escaped characters include :
	 * <ul>
	 * <li>.
	 * <li>\
	 * <li>?, * , and +
	 * <li>&
	 * <li>:
	 * <li>{ and }
	 * <li>[ and ]
	 * <li>( and )
	 * <li>^ and $
	 * </ul>
	 */
	public static String forRegex(String aRegexFragment) {
		final StringBuilder result = new StringBuilder();

		final StringCharacterIterator iterator = new StringCharacterIterator(
				aRegexFragment);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) {
			/*
			 * All literals need to have backslashes doubled.
			 */
			if (character == '.') {
				result.append("\\.");
			} else if (character == '\\') {
				result.append("\\\\");
			} else if (character == '?') {
				result.append("\\?");
			} else if (character == '*') {
				result.append("\\*");
			} else if (character == '+') {
				result.append("\\+");
			} else if (character == '&') {
				result.append("\\&");
			} else if (character == ':') {
				result.append("\\:");
			} else if (character == '{') {
				result.append("\\{");
			} else if (character == '}') {
				result.append("\\}");
			} else if (character == '[') {
				result.append("\\[");
			} else if (character == ']') {
				result.append("\\]");
			} else if (character == '(') {
				result.append("\\(");
			} else if (character == ')') {
				result.append("\\)");
			} else if (character == '^') {
				result.append("\\^");
			} else if (character == '$') {
				result.append("\\$");
			} else {
				// the char is not a special one
				// add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}
}
