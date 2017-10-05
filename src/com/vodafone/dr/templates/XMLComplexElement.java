/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vodafone.dr.templates;

import java.util.TreeMap;

/**
 *
 * @author eamrela
 */
public class XMLComplexElement {
    
    private String name;
    private String value;
    private String type;
    
    private TreeMap<String,XMLComplexElement> childs = new TreeMap<>();

    public String getType() {
        return type;
    }

    public XMLComplexElement setType(String type) {
        this.type = type;
        return this;
    }

    
    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    

    public XMLComplexElement setName(String name) {
        this.name = name;
        return this;
    }

    public TreeMap<String, XMLComplexElement> getChilds() {
        return childs;
    }

    public void setChilds(TreeMap<String, XMLComplexElement> childs) {
        this.childs = childs;
    }
    
    public XMLComplexElement addChild(XMLComplexElement element){
        childs.put(element.getName(), element);
        return this;    
    }
    
    public void setChildValue(String childName,String value){
        if(childs.containsKey(childName)){
            childs.get(childName).setValue(value);
        }
    }
}
