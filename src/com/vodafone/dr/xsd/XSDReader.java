/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vodafone.dr.xsd;

import com.vodafone.dr.configuration.ELEMENTS;
import com.vodafone.dr.configuration.XMLConf;
import com.vodafone.dr.templates.XMLComplexElement;
import com.vodafone.dr.templates.XMLEntity;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eamrela
 */
public class XSDReader {
    
    public static void readXSD(String filePath){
        try {
            RandomAccessFile raf = new RandomAccessFile(new File(filePath), "r");
            String line = null;
            XMLEntity entity = null;
            String entityName="";
            String type = XMLConf.getEntityType(filePath);
            boolean commented = false;
            boolean complex = false;
            String complexEntityName="";
            String previousEntityName="";
            int elementCount=0;
            while((line=raf.readLine())!=null){
                //<editor-fold defaultstate="collapsed" desc="Element tags tracking">
                if(line.contains("<element") && !line.contains("/>")){
                    elementCount++;
                }
                if(line.contains("</element")){
                    elementCount--;
                }
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Comment Handling">
                if(line.contains("<!--")){
                    commented = true;
                }
                if(line.contains("-->")){
                    commented = false;
                }
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="ComplexType Handling">
                
                else if(line.contains("<complexType")){
                    complex = true;
                    complexEntityName = entityName;
                }
                else if(line.contains("</complexType")){
                    complex = false;
                }
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Element Handling">
                else if(line.contains("</element>") && entity!=null){
                     XMLConf.addXmlElement(entity);
                     if(elementCount==0){
                     entity=null;
                     }
                }
                else if(line.contains("<element ") && !commented){
                    entityName = getElementName(line);
                    if(entityName.contains("vsData")){
                        entityName = entityName.replaceAll("vsData", "");
                    }
                    if(XMLConf.isElement(entityName)){
                        previousEntityName = entityName;
                        if(XMLConf.getXmlEntities().containsKey(entityName)){
                            entity = XMLConf.getXmlEntities().get(entityName);
                            XMLConf.addXmlElement(entity);
                        }else{
                            entity = new XMLEntity();
                            entity.setEntityName(entityName);
                            entity.setEntityType(type);
                            XMLConf.addXmlElement(entity);
                        }
                    }else if(!getElementName(line).equals("attributes") && entity!=null && !complex){
                        entity.addChild(type, previousEntityName,new XMLComplexElement().setName(getElementName(line)).setType("FLAT"));
                    }else if(!getElementName(line).equals("attributes") && entity!=null && complex){
                        entity.addGrandChild(type,complexEntityName,  new XMLComplexElement().setName(getElementName(line)).setType("COMPLEX"));
                    }
                }
                
                //</editor-fold>
            }
            if(entity!=null){
            XMLConf.addXmlElement(entity);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(XSDReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static String getElementName(String line){
        if(line.contains("name=")){
            return line.substring(line.indexOf("name=\"")+6,line.indexOf("\"", line.indexOf("name=\"")+6));
        }else if(line.contains("ref=")){
             return line.substring(line.indexOf("ref=\"")+5,line.indexOf("\"", line.indexOf("ref=\"")+5));
        }
        return null;
    }
    
//    public static void main(String[] args) {
//        
//        readXSD("C:\\Documents\\DR_3G\\XML\\XSD\\geranNrm.xsd"); // gn
//        readXSD("C:\\Documents\\DR_3G\\XML\\XSD\\genericNrm.xsd"); // xn
//        readXSD("C:\\Documents\\DR_3G\\XML\\XSD\\utranNrm.xsd"); // un
//        readXSD("C:\\Documents\\DR_3G\\XML\\XSD\\EricssonSpecificAttributes.16.28.xsd"); // es
//        
//        TreeMap<String,XMLEntity> entities = XMLConf.getXmlEntities();
//        System.out.println(entities.size());
////        System.out.println(entities.get("ExternalGsmCell").generateEntity());
////        System.out.println(entities.get("CoverageRelation").generateEntity());
//        TreeMap<String,String> param = new TreeMap<>();
//        System.out.println(entities.get(ELEMENTS.UtranRelation.toString()).generate(param));
//        
//    }
}
