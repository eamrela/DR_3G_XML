/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vodafone.dr.templates;

import com.vodafone.dr.configuration.XMLConf;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author eamrela
 */
public class XMLEntity {
    
    // XML Attributes
    private String entityName;
    private TreeMap<String,XMLComplexElement> un = new TreeMap<>();
    private TreeMap<String,XMLComplexElement> gn = new TreeMap<>();
    private TreeMap<String,XMLComplexElement> xn = new TreeMap<>();
    private TreeMap<String,XMLComplexElement> es = new TreeMap<>();
    private String entityType;
    private static final List<String> enumSeq = Arrays.asList("accessClassNBarred","accessClassesBarredCs","accessClassesBarredPs","spareA","hsAqmCongCtrlSpiSupport");
    // Database Attributes 
    private String id;

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
    
    
    
    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public void addChild(String type,String parent,XMLComplexElement element){
        if(type.equals("un")){
            if(un.containsKey(parent)){
             un.get(parent).addChild(element);
            }else{
            un.put(element.getName(), element);
            }
        }
        if(type.equals("gn")){
            if(gn.containsKey(parent)){
             gn.get(parent).addChild(element);
            }else{
            gn.put(element.getName(), element);
            }
        }
        if(type.equals("xn")){
            if(xn.containsKey(parent)){
             xn.get(parent).addChild(element);
            }else{
            xn.put(element.getName(), element);
            }
        }
        if(type.equals("es")){
            if(es.containsKey(parent)){
             es.get(parent).addChild(element);
            }else{
            es.put(element.getName(), element);
            }
        }
    }
   
    public void addGrandChild(String type,String grandparentName,XMLComplexElement element){
        if(type.equals("un")){
            if(un.containsKey(grandparentName)){
                un.get(grandparentName).addChild(element);
            }else{
                boolean added = false;
                for (Map.Entry<String, XMLComplexElement> entry : un.entrySet()) {
                    if(entry.getValue().getChilds().containsKey(grandparentName)){
                        entry.getValue().getChilds().get(grandparentName).addChild(element);
                        added = true;
                    }
                }
                if(!added){
                 un.put(grandparentName, new XMLComplexElement().setName(grandparentName).addChild(element));   
                }
            }
        }
        if(type.equals("gn")){
            if(gn.containsKey(grandparentName)){
                gn.get(grandparentName).addChild(element);
            }else{
                boolean added = false;
                for (Map.Entry<String, XMLComplexElement> entry : gn.entrySet()) {
                    if(entry.getValue().getChilds().containsKey(grandparentName)){
                        entry.getValue().getChilds().get(grandparentName).addChild(element);
                        added = true;
                    }
                }
                if(!added){
                 gn.put(grandparentName, new XMLComplexElement().setName(grandparentName).addChild(element));   
                }
            }
        }
        if(type.equals("xn")){
            if(xn.containsKey(grandparentName)){
                xn.get(grandparentName).addChild(element);
            }else{
                boolean added = false;
                for (Map.Entry<String, XMLComplexElement> entry : xn.entrySet()) {
                    if(entry.getValue().getChilds().containsKey(grandparentName)){
                        entry.getValue().getChilds().get(grandparentName).addChild(element);
                        added = true;
                    }
                }
                if(!added){
                  xn.put(grandparentName, new XMLComplexElement().setName(grandparentName).addChild(element));  
                }
            }
        }
        if(type.equals("es")){
            if(es.containsKey(grandparentName)){
                es.get(grandparentName).addChild(element);
            }else{
                boolean added = false;
                for (Map.Entry<String, XMLComplexElement> entry : es.entrySet()) {
                    if(entry.getValue().getChilds().containsKey(grandparentName)){
                        entry.getValue().getChilds().get(grandparentName).addChild(element);
                        added = true;
                    }
                }
                if(!added){
                 es.put(grandparentName, new XMLComplexElement().setName(grandparentName).addChild(element));
                }
            }
            
        }
    }

    public String generate(TreeMap<String,String> param,boolean embbed){
        String entity = "";
        if(!gn.isEmpty()){
        if(entityType.equals("gn")){
            if(getParam(param, "id")!=null){
            entity ="<gn:"+entityName+" id=\""+getParam(param, "id")+"\" modifier=\"create\">\n";
            }
            //<editor-fold defaultstate="collapsed" desc="GN">
        if(!gn.isEmpty()){
            for (Map.Entry<String, XMLComplexElement> GN : gn.entrySet()) {
                if(GN.getValue().getChilds().isEmpty()){
                    if(!GN.getValue().getName().contains(":")){
                    if(getParam(param, GN.getValue().getName())!=null){
                    entity+="   <gn:"+GN.getValue().getName()+">"+getParam(param, GN.getValue().getName())+"</gn:"+GN.getValue().getName()+">\n";
                    }
                    }
                }else{
                    if(GN.getValue().getName().equals(entityName)){
                    entity+="    <gn:vsData"+GN.getValue().getName()+">\n";
                    }else{
                    entity+="    <gn:"+GN.getValue().getName()+">\n";    
                    }
                    for (Map.Entry<String, XMLComplexElement> child : GN.getValue().getChilds().entrySet()) {
                        if(child.getValue().getChilds().isEmpty()){
                          if(!child.getValue().getName().contains(":")){
                              if(getParam(param, child.getValue().getName())!=null){
                        entity+="     <gn:"+child.getValue().getName()+">"+getParam(param, child.getValue().getName())+"</gn:"+child.getValue().getName()+">\n";
                              }
                          }
                        }else{
                            entity+="    <gn:"+child.getValue().getName()+">\n";
                            for (Map.Entry<String, XMLComplexElement> grandChild : child.getValue().getChilds().entrySet()) {
                                if(!grandChild.getValue().getName().contains(":")){
                                    if(getParam(param, grandChild.getValue().getName())!=null){
                                entity+="     <gn:"+grandChild.getValue().getName()+">"+getParam(param, grandChild.getValue().getName())+"</gn:"+grandChild.getValue().getName()+">\n";
                                    }
                                }
                            }
                            
                            
                            entity+="    </gn:"+child.getValue().getName()+">\n";
                        }
                    }
                    if(GN.getValue().getName().equals(entityName)){
                    entity+="    </gn:vsData"+GN.getValue().getName()+">\n";
                    }else{
                    entity+="    </gn:"+GN.getValue().getName()+">\n";    
                    }
                }
            }
        }
                
//</editor-fold>
        }
        }
        if(!un.isEmpty()){
        if(entityType.equals("un")){
            entity ="<un:"+entityName+" id=\""+getParam(param, "id")+"\" modifier=\"create\">\n";
            //<editor-fold defaultstate="collapsed" desc="UN">
        if(!un.isEmpty()){
            for (Map.Entry<String, XMLComplexElement> UN : un.entrySet()) {
                if(UN.getValue().getChilds().isEmpty()){
                    if(!UN.getValue().getName().contains(":")){
                        if(getParam(param,  UN.getValue().getName())!=null){
                    entity+="   <un:"+UN.getValue().getName()+">"+getParam(param, UN.getValue().getName())+"</un:"+UN.getValue().getName()+">\n";
                        }
                    }
                }else{
                    if(UN.getValue().getName().equals(entityName)){
                    entity+="    <un:vsData"+UN.getValue().getName()+">\n";
                    }else{
                    entity+="    <un:"+UN.getValue().getName()+">\n";    
                    }
                    
                    for (Map.Entry<String, XMLComplexElement> child : UN.getValue().getChilds().entrySet()) {
                        if(child.getValue().getChilds().isEmpty()){
                          if(!child.getValue().getName().contains(":")){
                              if(getParam(param,  child.getValue().getName())!=null){
                        entity+="     <un:"+child.getValue().getName()+">"+getParam(param, child.getValue().getName())+"</un:"+child.getValue().getName()+">\n";
                              }
                          }
                        }else{
                            entity+="    <un:"+child.getValue().getName()+">\n";
                            for (Map.Entry<String, XMLComplexElement> grandChild : child.getValue().getChilds().entrySet()) {
                                if(!grandChild.getValue().getName().contains(":")){
                                     if(getParam(param,  grandChild.getValue().getName())!=null){
                                entity+="     <un:"+grandChild.getValue().getName()+">"+getParam(param, grandChild.getValue().getName())+"</un:"+grandChild.getValue().getName()+">\n";
                                     }
                                }
                            }
                            entity+="    </un:"+child.getValue().getName()+">\n";
                        }
                    }
                    if(UN.getValue().getName().equals(entityName)){
                    entity+="    </un:vsData"+UN.getValue().getName()+">\n";
                    }else{
                    entity+="    </un:"+UN.getValue().getName()+">\n";    
                    }
                }
            }
        }
                
//</editor-fold>
        }
        }
        if(!es.isEmpty()){
        entity+=" <xn:VsDataContainer id=\""+getParam(param, "id")+"\" modifier=\"create\">\n";
        entity+="  <xn:attributes>\n";
        entity+=" <xn:vsDataType>vsData"+entityName+"</xn:vsDataType>\n";
        entity+=" <xn:vsDataFormatVersion>"+XMLConf.getVsDataFormatVersion()+"</xn:vsDataFormatVersion>\n";
        //<editor-fold defaultstate="collapsed" desc="ES">
            for (Map.Entry<String, XMLComplexElement> ES : es.entrySet()) {
                if(ES.getValue().getChilds().isEmpty()){
                    if(!ES.getValue().getName().contains(":")){
                        if(getParam(param,  ES.getValue().getName())!=null){
                            if(enumSeq.contains(ES.getValue().getName())){
                            String[] vals = getParam(param, ES.getValue().getName()).split(",");
                            for (String val : vals) {
                                entity+="   <es:"+ES.getValue().getName()+">"+val+"</es:"+ES.getValue().getName()+">\n";
                            }
                            }else{
                    entity+="   <es:"+ES.getValue().getName()+">"+getParam(param, ES.getValue().getName())+"</es:"+ES.getValue().getName()+">\n";
                            }
                        }
                    }
                }else{
                    if(ES.getValue().getName().equals(entityName)){
                    entity+="    <es:vsData"+ES.getValue().getName()+">\n";
                    }else{
                    entity+="    <es:"+ES.getValue().getName()+">\n";    
                    }
                    TreeMap<Integer,XMLComplexElement> sortedChilds = soretChilds(ES.getValue().getChilds());
                    for (Map.Entry<Integer, XMLComplexElement> child : sortedChilds.entrySet()) {
                        if(child.getValue().getChilds().isEmpty()){
                            if(!child.getValue().getName().contains(":")){
                                if(getParam(param,   child.getValue().getName())!=null){
                            if(enumSeq.contains(child.getValue().getName())){
                            String[] vals = getParam(param, child.getValue().getName()).split(",");
                            for (String val : vals) {
                                entity+="   <es:"+child.getValue().getName()+">"+val+"</es:"+child.getValue().getName()+">\n";
                            }
                            }else{
                            entity+="     <es:"+child.getValue().getName()+">"+getParam(param, child.getValue().getName())+"</es:"+child.getValue().getName()+">\n";
                                    }
                                }
                            }
                        }else{
                            entity+="    <es:"+child.getValue().getName()+">\n";
                            TreeMap<Integer,XMLComplexElement> sortedGrandChilds = soretChilds(child.getValue().getChilds());
                            for (Map.Entry<Integer, XMLComplexElement> grandChild : sortedGrandChilds.entrySet()) {
                                if(grandChild.getValue().getChilds().isEmpty()){
                                    if(getParam(param,   child.getValue().getName()+"-"+grandChild.getValue().getName())!=null){
                                        if(enumSeq.contains(child.getValue().getName()+"-"+grandChild.getValue().getName())){
                            String[] vals = getParam(param, child.getValue().getName()+"-"+grandChild.getValue().getName()).split(",");
                            for (String val : vals) {
                                entity+="   <es:"+grandChild.getValue().getName()+">"+val+"</es:"+grandChild.getValue().getName()+">\n";
                            }
                            }else{
                                entity+="     <es:"+grandChild.getValue().getName()+">"+getParam(param, child.getValue().getName()+"-"+grandChild.getValue().getName())+"</es:"+grandChild.getValue().getName()+">\n";
                                        }
                                    }
                                }
                            }
                            entity+="    </es:"+child.getValue().getName()+">\n";
                        }
                    }
                    if(ES.getValue().getName().equals(entityName)){
                    entity+="    </es:vsData"+ES.getValue().getName()+">\n";
                    }else{
                    entity+="    </es:"+ES.getValue().getName()+">\n";    
                    }
                }
            }
        //</editor-fold>
        entity+=" </xn:attributes>\n";
        entity+=" </xn:VsDataContainer>\n";
        
        }
        if(!gn.isEmpty()){
        if(entityType.equals("gn")){
          entity+="</gn:"+entityName+">\n";  
          if(embbed){
            entity+="<gn:"+entityName+" id=\""+getParam(param, "id")+"\" modifier=\"update\">\n";
            entity+="\nEMBBEDED-CONTENT\n";
            entity+="</gn:"+entityName+">\n"; 
            }
        }
        }
        if(!un.isEmpty()){
        if(entityType.equals("un")){
          entity+="</un:"+entityName+">\n\n";  
          if(embbed){
            entity+="<un:"+entityName+" id=\""+getParam(param, "id")+"\" modifier=\"update\">\n";
            entity+="\nEMBBEDED-CONTENT\n";
            entity+="</un:"+entityName+">\n"; 
        }
        }
        }
        
        return entity;
    }
    
    public String getParam(TreeMap<String,String> param,String key){
        if(param.containsKey(key)){
            if(param.get(key).contains("i[")){
                return null;
            }
            return param.get(key);
        }else{
            return null;
        }
    }
    
    public TreeMap<Integer,XMLComplexElement> soretChilds(TreeMap<String, XMLComplexElement> original){
        TreeMap<Integer,XMLComplexElement> sorted  = new TreeMap<>();
        for (Map.Entry<String, XMLComplexElement> entry : original.entrySet()) {
            sorted.put(entry.getValue().getOrder(), entry.getValue());
        }
        return sorted;
    }

    public static void main(String[] args) {
        TreeMap<String, XMLComplexElement> x = new TreeMap<>();
        
    }
    
}
