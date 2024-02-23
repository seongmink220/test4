package kr.co.ubcn.rm.util;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class StringXMLParse {
	
	private static String xmlFile = "config/query.xml";
	
	public static String rtnQuery(String node_Name) {
	    String rtnContent=null;
		try {		 
			// XML 문서 파싱
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = factory.newDocumentBuilder();
			Document document = documentBuilder.parse(xmlFile);
			
			// root 구하기
			Element root = document.getDocumentElement();
			
			// root의 속성
			//System.out.println("class name: " + root.getAttribute("name"));			
			NodeList childeren = root.getChildNodes(); // 자식 노드 목록 get
			for(int i = 0; i < childeren.getLength(); i++){
				Node node = childeren.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE){ // 해당 노드의 종류 판정(Element일 때)
					Element ele = (Element)node;
					String nodeName = ele.getNodeName();
					//System.out.println("node name: " + nodeName);
					
					if(nodeName.equals(node_Name)) {
						//System.out.println("node attribute: " + ele.getTextContent());
						rtnContent=ele.getTextContent();
						break;
					}
				}
			}
		}catch(ParserConfigurationException ex) {
			ex.printStackTrace();
		}catch(SAXException ex) {
			ex.printStackTrace();
		}catch(IOException ex) {
			ex.printStackTrace();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return rtnContent;		
	}
	
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException{
		// XML 문서 파싱
		/*
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		Document document = documentBuilder.parse("config/query.xml");
		
		// root 구하기
		Element root = document.getDocumentElement();
		
		// root의 속성
		System.out.println("class name: " + root.getAttribute("name"));
		
		NodeList childeren = root.getChildNodes(); // 자식 노드 목록 get
		for(int i = 0; i < childeren.getLength(); i++){
			Node node = childeren.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE){ // 해당 노드의 종류 판정(Element일 때)
				Element ele = (Element)node;
				String nodeName = ele.getNodeName();
				System.out.println("node name: " + nodeName);
				
				if(nodeName.equals("mang")) {
					System.out.println("node attribute: " + ele.getTextContent());
					break;
				}
			}
		}*/
		
		System.out.println(rtnQuery("mang"));
	}

}
