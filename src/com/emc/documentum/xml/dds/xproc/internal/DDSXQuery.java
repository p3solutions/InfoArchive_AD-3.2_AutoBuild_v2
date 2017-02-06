/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.xproc.XProcConfiguration
 *  com.emc.documentum.xml.xproc.impl.api.xquery.XQuery
 *  com.emc.documentum.xml.xproc.impl.api.xquery.XQueryException
 *  com.emc.documentum.xml.xproc.impl.api.xquery.XQueryValue
 *  com.emc.documentum.xml.xproc.io.Resolver
 *  com.emc.documentum.xml.xproc.io.Source
 *  com.emc.documentum.xml.xproc.util.ExtendedNamespaceContext
 *  com.emc.documentum.xml.xproc.util.impl.CollectionUtil
 *  com.emc.documentum.xml.xproc.util.impl.IterableIterator
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.dom.interfaces.XhiveNodeIf
 *  com.xhive.error.XhiveException
 *  com.xhive.query.interfaces.XQueryResolverIf
 *  com.xhive.query.interfaces.XhiveXQueryQueryIf
 *  com.xhive.query.interfaces.XhiveXQueryResultIf
 *  com.xhive.query.interfaces.XhiveXQueryValueIf
 */
package com.emc.documentum.xml.dds.xproc.internal;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.namespace.QName;

import org.w3c.dom.Node;

import com.emc.dds.xmlarchiving.client.p3.util.SecurityUtil;
import com.emc.documentum.xml.dds.xquery.xdb.internal.DDSXQueryUtils;
import com.emc.documentum.xml.xproc.XProcConfiguration;
import com.emc.documentum.xml.xproc.impl.api.xquery.XQuery;
import com.emc.documentum.xml.xproc.impl.api.xquery.XQueryException;
import com.emc.documentum.xml.xproc.impl.api.xquery.XQueryValue;
import com.emc.documentum.xml.xproc.io.Source;
import com.emc.documentum.xml.xproc.util.ExtendedNamespaceContext;
import com.emc.documentum.xml.xproc.util.impl.CollectionUtil;
import com.emc.documentum.xml.xproc.util.impl.IterableIterator;
import com.emc.internal.utils.InstanceConstants;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveNamedNodeMapIf;
import com.xhive.dom.interfaces.XhiveNodeIf;
import com.xhive.error.XhiveException;
import com.xhive.query.interfaces.XQueryResolverIf;
import com.xhive.query.interfaces.XhiveXQueryQueryIf;
import com.xhive.query.interfaces.XhiveXQueryValueIf;

public class DDSXQuery implements XQuery {
	private final XProcConfiguration config;
	private final Map<QName, Object> variables = CollectionUtil.map();
	private final String expression;
	private final ExtendedNamespaceContext namespaceContext;
	private SecurityUtil sec;

	private static final Character SYMBOL_START1 = ' ';
	private static final Character SYMBOL_END1 = '/';
	private static final Character SYMBOL_START2 = ':';
	private static final Character SYMBOL_END2 = '@';
	private static final Character SYMBOL_START3 = '[';
	private static final Character SYMBOL_END3 = '`';
	private static final Character SYMBOL_START4 = '{';
	private static final Character SYMBOL_END4 = '~';

	private static final Character HEBREW_START = 1424;
	private static final Character HEBREW_END = 1535;

	private static final String LTR = "LTR";
	private static final String RTL = "RTL";
	private static final String SPA = "SPA";
	private static final String NET = "NET";

	public DDSXQuery(XProcConfiguration config, String expression, ExtendedNamespaceContext namespaceContext) {
		this.config = config;
		this.expression = expression;
		this.namespaceContext = namespaceContext;
		this.sec = InstanceConstants.getSec();
	}

	public String getExpression() {
		return this.expression;
	}

	public void setVariable(QName variable, Object value) {
		Objects.requireNonNull(variable, "<null> variable");
		Objects.requireNonNull(value, "<null> value");
		this.variables.put(variable, value);
	}

	public IterableIterator<XQueryValue> evaluate(List<Source> context) {
		XDBQueryIntern origResults = new XDBQueryIntern(this.expression, context, this.variables,
				this.namespaceContext);
		Iterator<? extends XhiveXQueryValueIf> xmlResult = origResults.results;

		if (InstanceConstants.isValid() && sec.isValid()
				&& this.variables.get(new QName("restrictionsDecrypt")).equals("true")) {
			XhiveXQueryValueIf wrapperXML = null;
			while (xmlResult.hasNext()) {
				wrapperXML = xmlResult.next();
				XhiveNodeIf doc = wrapperXML.asNode();
				com.xhive.util.interfaces.IterableIterator<? extends XhiveNodeIf> element = doc.getChildren();

				while (element.hasNext()) {
					XhiveNamedNodeMapIf item = element.next().getAttributes();
					for (int i = 0; i < item.getLength(); i++) {
						try {
							String value = item.item(i).getLocalName().toString().startsWith("enc_")
									? applyHebrewFix(appendDec(item.item(i).getNodeValue()))
									: applyHebrewFix(item.item(i).getNodeValue());
							if (item.item(i).getLocalName().toString().startsWith("enc_")) {
								switch (item.item(i).getLocalName().toString()) {
								case "enc_mask_credit_card":
									value = "XXXX-XXXX-XXXX-" + (value).substring(12);
									break;
								case "enc_mask_ssn":
									value = "XXX-XX-" + (value).substring(5);
									break;
								default:
									item.item(i).setNodeValue(value);
									break;
								}
							} else
								item.item(i).setNodeValue(value);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			this.variables.put(new QName("wrapperXML"), wrapperXML);
			XDBQueryIntern wrapperResult = new XDBQueryIntern("declare variable $wrapperXML external; $wrapperXML",
					context, this.variables, this.namespaceContext);
			return wrapperResult;
		}
		return origResults;
	}

	private String appendDec(String value) throws Exception {
		if (value == null || value.equals(""))
			return "";
		StringBuffer sb = new StringBuffer();
		char[] inChar = value.toCharArray();
		StringBuffer word = new StringBuffer();
		for (int i = 0; i < inChar.length; i++) {
			if (inChar[i] == '&' || inChar[i] == '<' || inChar[i] == '>' || inChar[i] == '\"' || inChar[i] == '\'') {
				sb.append(sec.decryption(word.toString())).append(replaceChar(inChar[i]));
				word = new StringBuffer();
			} else
				word.append(inChar[i]);
		}
		if (word.length() > 0)
			sb.append(sec.decryption(word.toString()));
		return sb.toString();
	}

	public static String applyHebrewFix(String input) {
		if (containsHebrew(input)) {
			boolean previous = false;
			String intermediate = "";
			String output = "";
			for (int i = 0; i < input.length(); i++) {
				if (checkHebrew(input.charAt(i)).equals(SPA)) {
					if ((previous && !checkHebrew(input.charAt(i + 1)).equals(RTL))
							|| (!previous && !checkHebrew(input.charAt(i + 1)).equals(LTR))) {
						intermediate += input.charAt(i);
						previous = true;
					} else if ((previous && !checkHebrew(input.charAt(i + 1)).equals(LTR))
							|| (!previous && !checkHebrew(input.charAt(i + 1)).equals(RTL))) {
						output = checkBrack(input.charAt(i)) + intermediate + output;
						intermediate = "";
						previous = false;
					}
				} else if (checkHebrew(input.charAt(i)).equals(NET)) {
					if (previous) {
						intermediate += input.charAt(i);
						previous = true;
					} else {
						output = checkBrack(input.charAt(i)) + intermediate + output;
						intermediate = "";
						previous = false;
					}
				} else if (checkHebrew(input.charAt(i)).equals(LTR)) {
					intermediate += input.charAt(i);
					previous = true;
				} else {
					output = checkBrack(input.charAt(i)) + intermediate + output;
					intermediate = "";
					previous = false;
				}
			}
			return intermediate + output;
		} else
			return input;
	}

	private static boolean containsHebrew(String input) {
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) >= HEBREW_START && input.charAt(i) <= HEBREW_END)
				return true;
		}
		return false;
	}

	private static char checkBrack(char ch) {
		switch (ch) {
		case '(':
			return ')';
		case ')':
			return '(';
		case '{':
			return '}';
		case '}':
			return '{';
		case '[':
			return ']';
		case ']':
			return '[';
		case '>':
			return '<';
		case '<':
			return '>';
		default:
			return ch;
		}

	}

	private static String checkHebrew(char charAt) {
		if (charAt >= HEBREW_START && charAt <= HEBREW_END)
			return RTL;
		else if (charAt == SYMBOL_START1)
			return SPA;
		else if ((charAt >= SYMBOL_START1 && charAt <= SYMBOL_END1)
				|| (charAt >= SYMBOL_START2 && charAt <= SYMBOL_END2)
				|| (charAt >= SYMBOL_START3 && charAt <= SYMBOL_END3)
				|| (charAt >= SYMBOL_START4 && charAt <= SYMBOL_END4))
			return NET;
		else
			return LTR;
	}

	private static String replaceChar(char c) {
		switch (c) {
		case '&':
			return "&amp;";
		case '<':
			return "&gt;";
		case '>':
			return "&lt;";
		case '\"':
		case '\'':
			return "&quot;";
		default:
			return Character.toString(c);
		}
	}

	private static class XDBXQueryValue implements XQueryValue {
		private final XhiveXQueryValueIf value;

		public XDBXQueryValue(XhiveXQueryValueIf value) {
			this.value = value;
		}

		public boolean isNode() {
			return this.value.isNode();
		}

		public Node asNode() {
			try {
				return this.value.asNode();
			} catch (XhiveException xe) {
				return null;
			}
		}

		public String asString() {
			return this.value.asString();
		}
	}

	private class XDBQueryIntern implements IterableIterator<XQueryValue> {
		private Iterator<? extends XhiveXQueryValueIf> results;

		public XDBQueryIntern(String expression, List<Source> context, Map<QName, Object> variables,
				ExtendedNamespaceContext namespaceContext) {
			try {
				XhiveNodeIf initialContextItem = this.getInitialContextItem(context);
				XhiveSessionIf session = initialContextItem.getSession();
				DDSXProcXQueryResolver xqueryResolver = new DDSXProcXQueryResolver(DDSXQuery.this.config.getResolver(),
						context);
				XhiveXQueryQueryIf query = DDSXQueryUtils.newXQuery(session, expression, null,
						(XQueryResolverIf) xqueryResolver);
				this.setXQueryVariables(query);
				this.results = query.executeOn(initialContextItem);
			} catch (Exception e) {
				throw new XQueryException((Throwable) e);
			}
		}

		@SuppressWarnings("rawtypes")
		private void setXQueryVariables(XhiveXQueryQueryIf xdbQuery) {
			if (DDSXQuery.this.variables != null) {
				for (Map.Entry entry : DDSXQuery.this.variables.entrySet()) {
					QName var = (QName) entry.getKey();
					Object value = entry.getValue();
					String nsURI = var.getNamespaceURI();
					String localPart = var.getLocalPart();
					if (nsURI == null || "".equals(nsURI)) {
						xdbQuery.setVariable(localPart, value);
						continue;
					}
					xdbQuery.setVariable(nsURI, localPart, value);
				}
			}
		}

		private XhiveNodeIf getInitialContextItem(List<Source> context) {
			if (context == null || context.isEmpty()) {
				throw new XQueryException("xDB does not support XQueries with an undefined initial context item");
			}
			Source first = context.get(0);
			Node node = first.getNode();
			if (node instanceof XhiveNodeIf) {
				return (XhiveNodeIf) node;
			}
			throw new XQueryException(
					"Context item source must be " + XhiveNodeIf.class + " based, got: " + (Object) first);
		}

		public IterableIterator<XQueryValue> iterator() {
			return this;
		}

		public boolean hasNext() {
			return this.results.hasNext();
		}

		public XQueryValue next() {
			XhiveXQueryValueIf xhiveValue = this.results.next();
			return new XDBXQueryValue(xhiveValue);
		}

		public void remove() {
			this.results.remove();
		}
	}

}
