package com.wising.easyhkstock.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

public class Utils {

	public static List<String> getElementText(Element elt) {
		Objects.requireNonNull(elt);
		List<String> result = new ArrayList<String>();
		new NodeTraversor(new NodeVisitor() {
			public void head(Node node, int depth) {
				if (node instanceof TextNode) {
					TextNode textNode = (TextNode) node;
					String text = org.jsoup.helper.StringUtil.normaliseWhitespace(textNode.getWholeText()).trim();
					if (StringUtils.isNotBlank(text)) {
						result.add(text);
					}
				}
			}
			public void tail(Node node, int depth) {
			}
		}).traverse(elt);
		return result;
	}
}
