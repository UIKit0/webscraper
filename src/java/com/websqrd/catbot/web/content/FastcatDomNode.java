package com.websqrd.catbot.web.content;

import org.w3c.dom.Node;

public class FastcatDomNode {
	private Node node;
	private double contentScore;
	public Node getNode() {
		return node;
	}
	public void setNode(Node node) {
		this.node = node;
	}
	public double getContentScore() {
		return contentScore;
	}
	public void setContentScore(double contentScore) {
		this.contentScore = contentScore;
	}
}
