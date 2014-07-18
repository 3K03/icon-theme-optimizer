package net.threekzerothree.themeranalizer;

public class Component { 
	//implements Comparable<Component> {
	
	String name;
	String type;
	String component;
	String drawable;
	
	/*
	@Override
    public int compareTo(Component component) {
        return  component.drawable.compareTo(component.drawable);
    }
	*/
	
	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		type = type == "" ? "APP" : type;
		this.type = type;
	}
	
	public void setComponent(String component) {
		this.component = component;
	}
	
	public void setDrawable(String drawable) {
		this.drawable = drawable;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public String getComponent() {
		return component;
	}
	
	public String getDrawable() {
		return drawable;
	}
}
