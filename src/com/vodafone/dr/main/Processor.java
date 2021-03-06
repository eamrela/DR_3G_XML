/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vodafone.dr.main;

import com.vodafone.dr.configuration.AppConf;
import com.vodafone.dr.configuration.DR;
import com.vodafone.dr.configuration.DR_Plan;
import com.vodafone.dr.configuration.XMLConf;
import com.vodafone.dr.generator.XMLGenerator;
import com.vodafone.dr.mongo.MongoDB;
import com.vodafone.dr.xsd.XSDReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class Processor {
    
    private static String workingDir = null;
    private static String scriptsDir = null;
    private static TreeMap<String,TreeMap<Integer,String>> fileFooters = new TreeMap<>();
    
    public static void initApp(String confPath){
            System.out.println("Initializing App");
            AppConf.configureApp(confPath);
            System.out.println("Building DR Plan");
            AppConf.configureDR();
            System.out.println("Initializing Mongo DB");
            MongoDB.initializeDB();
            System.out.println("Initializing XML");
            XSDReader.readXSD(XMLConf.getGnPath()); // gn
            XSDReader.readXSD(XMLConf.getXnPath()); // xn
            XSDReader.readXSD(XMLConf.getUnPath()); // un
            XSDReader.readXSD(XMLConf.getEsPath()); // es
            
            workingDir = AppConf.getWorkingDir()+"\\DR_3G_"+AppConf.getMydate();
            scriptsDir = workingDir+"\\scripts";
            new File(scriptsDir).mkdirs();
    }

    public static void generateDR(){
        TreeMap<String, DR> Plan = DR_Plan.getDR_PLAN();
        String UtranBundle = null;
        String ExtGsmCellBundle = null;
        String ExtGsmRelationBundle = null;
        String ExtUtranCellBundle = null;
        String UtranRelationBundle = null;
        for (Map.Entry<String, DR> entry : Plan.entrySet()) {
            //<editor-fold defaultstate="collapsed" desc="Generation">
            UtranBundle = XMLGenerator.generateUtranBundle(entry.getValue().getSiteName(),
                                                    entry.getValue().getSourceMTX(),
                                                  entry.getValue().getSourceRNC(), 
                                                  entry.getValue().getTargetMTX(), 
                                                  entry.getValue().getTargetRNC());
            appendToFile(scriptsDir, entry.getValue().getSourceMTX(), entry.getValue().getTargetRNC(), "UtranBundle", UtranBundle);
            ExtGsmCellBundle = XMLGenerator.generateExternalGsmCellBundle(entry.getValue().getSiteName(),
                                                    entry.getValue().getSourceMTX(),
                                                  entry.getValue().getSourceRNC(), 
                                                  entry.getValue().getTargetMTX(), 
                                                  entry.getValue().getTargetRNC());
            appendToFile(scriptsDir, entry.getValue().getSourceMTX(), entry.getValue().getTargetRNC(), "ExtGsmCellBundle", ExtGsmCellBundle);
            ExtGsmRelationBundle = XMLGenerator.generateExternalGsmRelationBundle(entry.getValue().getSiteName(),
                                                    entry.getValue().getSourceMTX(),
                                                  entry.getValue().getSourceRNC(), 
                                                  entry.getValue().getTargetMTX(), 
                                                  entry.getValue().getTargetRNC());
            appendToFile(scriptsDir, entry.getValue().getSourceMTX(), entry.getValue().getTargetRNC(), "ExtGsmRelationBundle", ExtGsmRelationBundle);
            ExtUtranCellBundle = XMLGenerator.generateExternalUtranCellBundle(entry.getValue().getSiteName(),
                                                    entry.getValue().getSourceMTX(),
                                                  entry.getValue().getSourceRNC(), 
                                                  entry.getValue().getTargetMTX(), 
                                                  entry.getValue().getTargetRNC());
            appendToFile(scriptsDir, entry.getValue().getSourceMTX(), entry.getValue().getTargetRNC(), "ExtUtranCellBundle", ExtUtranCellBundle);
            UtranRelationBundle = XMLGenerator.generateUtranRelationBundle(entry.getValue().getSiteName(),
                                                    entry.getValue().getSourceMTX(),
                                                  entry.getValue().getSourceRNC(), 
                                                  entry.getValue().getTargetMTX(), 
                                                  entry.getValue().getTargetRNC());
            appendToFile(scriptsDir, entry.getValue().getSourceMTX(), entry.getValue().getTargetRNC(), "UtranRelationBundle", UtranRelationBundle);
            //</editor-fold>
        }
        addFooters();
    }
    
    public static void addFooters(){
        for (Map.Entry<String, TreeMap<Integer, String>> footer : fileFooters.entrySet()) {
            for (Map.Entry<Integer, String> innerFooter : footer.getValue().entrySet()) {
                appendFooter(footer.getKey(), innerFooter.getValue());
            }
        }
    }
    
    public static void appendToFile(String dir,String sourceMTX,String targetRNC,String fileName,String script){
        File mtxDir = new File(dir+"/"+sourceMTX+"/"+targetRNC);
        if(!mtxDir.exists()){
                mtxDir.mkdirs();
        }
        String filePath = dir+"/"+sourceMTX+"/"+targetRNC+"/"+targetRNC+"_"+fileName+".xml";
        File file = new File(filePath);
        PrintWriter out = null;
        if ( file.exists() && !file.isDirectory() ) {
            try {
                out = new PrintWriter(new FileOutputStream(file, true));
                out.append(script+"\n");
                out.flush();
                out.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Processor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else {
            try {
                out = new PrintWriter(file);
                out.append(XMLGenerator.getFileHeader());
                fileFooters.put(filePath, new TreeMap<>());
                fileFooters.get(filePath).put(2, XMLGenerator.getFileFooter());
                if(fileName.contains("UtranBundle")){
                out.append(XMLGenerator.getUtranBundleHeader(targetRNC));
                fileFooters.get(filePath).put(1, XMLGenerator.getUtranBundleFooter());
                }
                
                out.append(script+"\n");
                out.flush();
                out.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Processor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void appendFooter(String filePath,String footer){
        
        
        File file = new File(filePath);
        PrintWriter out = null;
        if ( file.exists() && !file.isDirectory() ) {
            try {
                out = new PrintWriter(new FileOutputStream(file, true));
                out.append(footer+"\n");
                out.flush();
                out.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Processor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void main(String[] args) {
//        if(args.length!=1){
//            System.out.println("Please set the input paramters");
//            System.out.println("Configuration File");
//            System.exit(1);
//        }
        
        String conf ="C:\\Documents\\DR_3G\\DR3G.conf";
//        String conf = args[0];
        initApp(conf);
        generateDR();

    }
}
