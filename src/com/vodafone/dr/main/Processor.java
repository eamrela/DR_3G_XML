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
        String siteScript = null;
        for (Map.Entry<String, DR> entry : Plan.entrySet()) {
            siteScript = XMLGenerator.generateSiteScript(entry.getValue().getSiteName(),
                                                    entry.getValue().getSourceMTX(),
                                                  entry.getValue().getSourceRNC(), 
                                                  entry.getValue().getTargetMTX(), 
                                                  entry.getValue().getTargetRNC());
            appendToFile(scriptsDir, entry.getValue().getSourceMTX(), entry.getValue().getTargetRNC(), "", siteScript);
        }
    }
    
    public static void appendToFile(String dir,String sourceMTX,String targetRNC,String fileName,String script){
        File mtxDir = new File(dir+"/"+sourceMTX);
        if(!mtxDir.exists()){
                mtxDir.mkdir();
        }
        File file = new File(dir+"/"+sourceMTX+"/"+targetRNC+"_"+fileName+".xml");
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
                out.append(script+"\n");
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
//        generateDR();
        appendToFile(scriptsDir, "MTX-Source", "TargetRNC", "Relations", "<TESTING></TESTING>");

    }
}
