package net.threekzerothree.themeranalizer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class ThemerAnalizer {

	public static void main(String argv[]) {
 
		loadComponents();
		optimizeComponents();
		writeOptimizedComponents();
		
		loadDrawables();
	}
	
	private static List<Component> mComponents = new ArrayList<Component>();
	private static List<String> mComponentDrawables = new ArrayList<String>();
	private static List<String> mResourceDrawables = new ArrayList<String>();
	private static List<String> mUnUtilizedDrawables = new ArrayList<String>();
	
	private static void loadComponents() {
		
		// clear existing components before adding more
		mComponents.clear();
					
        try {
        	File fXmlFile = new File("res/xml/appfilter.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			
			doc.getDocumentElement().normalize();
            
            NodeList nList = doc.getElementsByTagName("item");
            
            for (int temp = 0; temp < nList.getLength(); temp++) {
            	
            	Node node = nList.item(temp);

				if (node.getNodeType() == Node.ELEMENT_NODE) {
					
					Element element = (Element) node;
					String info = element.getAttribute("component");
					String drawable = element.getAttribute("drawable");
					String type = element.getAttribute("type");
					
					if(type.equals("")) {
						int endSubstring = drawable.indexOf("_");
						endSubstring = endSubstring == -1 ? 0 : endSubstring;
						
						type = drawable.substring(0,endSubstring).toUpperCase();
					}
					
					Component component = new Component();
					component.setType(type);
					component.setComponent(info.substring(14, info.length() - 1));
					component.setDrawable(drawable);
					
					mComponents.add(component);
				}
			}
            
        } catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	private static void optimizeComponents() {
		
		int originalSize = mComponents.size();

		// remove duplicate components
		HashSet<Component> componentsHS = new HashSet<Component>();
		componentsHS.addAll(mComponents);
		mComponents.clear();
		mComponents.addAll(componentsHS);
		
		// sorts components.... 
		Collections.sort(mComponents, new Comparator<Component>() {
	        @Override
	        public int compare(Component o1, Component o2) {
	            int value1 = o1.type.compareTo(o2.type);
	            
	            if (value1 == 0) {
	            	return o1.component.compareTo(o2.component);
	            }
	            
	            return value1;
	        }
	    });

		System.out.println("COMPONENTS::OLD-COUNT::" + originalSize);
		System.out.println("COMPONENTS::NEW-COUNT::" + mComponents.size());
	}
	
	private static void writeOptimizedComponents() {
		
		Writer writer = null;

		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream("out/xml/appfilter.xml"), "utf-8"));
		    
		    String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		    header +="\n" + "<resources>";
		    header +="\n" + "	<!-- Icon Layers -->";
		    header +="\n" + "	<iconback img1=\"app_3k03_icon_background\"/>";
		    header +="\n" + "	<iconupon img1=\"app_3k03_icon_overlay\"/>";
		    header +="\n" + "	<iconmask img1=\"app_3k03_icon_mask\"/>";
		    header +="\n" + "	<scale factor=\"0.5\" />";
		    
		    writer.write(header);
		    
		    String type = "";
		    for(Component component : mComponents) {
		    	String item = "\n";
		    	
		    	if(type.equals("") || !type.equals(component.type)) {
		    		type = component.type;
		    		item += "\n" + "	<!-- " + type + " -->" + "\n";
		    	}
		    		
				item += "	<item type=\"";
				item += component.type;
				item += "\" component=\"ComponentInfo{";
				item += component.component;
				item += "}\" drawable=\"";
				item += component.drawable;
				item += "\" />";
				
				writer.write(item);
			}
		        
		    String footer = "</resources>";
		    writer.write(footer);
		    
		} catch (IOException ex) {
			// report
		} finally {
		   try {writer.close();} catch (Exception ex) {}
		}
	}
	
	private static void loadDrawables() {
		
		// load drawables from components
		// utilized within appfilter.xml
		for(Component component : mComponents) {
			mComponentDrawables.add(component.getDrawable());
		}
		
		HashSet<String> drawablesHS = new HashSet<String>();
		drawablesHS.addAll(mComponentDrawables);
		mComponentDrawables.clear();
		mComponentDrawables.addAll(drawablesHS);
		
		Collections.sort(mComponentDrawables);
		
		// load drawables from resource files
		File directory = new File("res/drawables");
		Collection<File> files = FileUtils.listFiles(
				directory,
				new RegexFileFilter("^(.*?)"),
				DirectoryFileFilter.DIRECTORY);
		
		
		String filename;
		for(File file : files) {
			filename = file.getName();
			
			int endSubstring = filename.indexOf(".");
			endSubstring = endSubstring == -1 ? 0 : endSubstring;
			
			filename = filename.substring(0,endSubstring);
			
			mResourceDrawables.add(filename);
		}
		
		// identify un-utilized drawables
		for(String drawable : mResourceDrawables) {
			if(!mComponentDrawables.contains(drawable)) {
				mUnUtilizedDrawables.add(drawable);
				System.out.println("NOT-UTILIZED::" + drawable);
			}
		}
		
		System.out.println("DRAWABLES::TOTAL::COUNT::" + mResourceDrawables.size());
		System.out.println("DRAWABLES::NOT-USED::COUNT::" + mUnUtilizedDrawables.size());
	}
}
