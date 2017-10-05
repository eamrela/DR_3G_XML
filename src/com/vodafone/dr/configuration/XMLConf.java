/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vodafone.dr.configuration;

import com.vodafone.dr.templates.XMLEntity;
import java.util.TreeMap;

/**
 *
 * @author eamrela
 */
public class XMLConf {
    
    // Constants -- to be fileld from database 
    private static String MCC;
    private static String MNC;
    private static String MNCLength;
    private static String PREFIX_ES;
    private static String PREFIX_UN;
    private static String PREFIX_XN;
    private static String PREFIX_GN;
    private static String vsDataFormatVersion;
    private static String fileFormatVersion;
    private static String senderName;
    private static String vendorName;
    private static String dnPrefix;
    private static String SubNetwork;
    private static String BulkCMHeader;
    private static String gnPath;
    private static String unPath;
    private static String xnPath;
    private static String esPath;
    private static TreeMap<String,XMLEntity> xmlEntities = new TreeMap<>();


    public static boolean isElement(String test) {

    for (ELEMENTS c : ELEMENTS.values()) {
        if (c.name().equals(test)) {
            return true;
        }
    }
    return false;
    }

    public static String getEntityType(String fileName){
        if(fileName.contains(PREFIX_ES)){
            return "es";
        }
        if(fileName.contains(PREFIX_UN)){
            return "un";
        }
        if(fileName.contains(PREFIX_XN)){
            return "xn";
        }
        if(fileName.contains(PREFIX_GN)){
            return "gn";
        }
        return null;
    }
    
    public static String getPREFIX_ES() {
        return PREFIX_ES;
    }

    public static void setPREFIX_ES(String PREFIX_ES) {
        XMLConf.PREFIX_ES = PREFIX_ES;
    }

    public static String getPREFIX_UN() {
        return PREFIX_UN;
    }

    public static void setPREFIX_UN(String PREFIX_UN) {
        XMLConf.PREFIX_UN = PREFIX_UN;
    }

    public static String getPREFIX_XN() {
        return PREFIX_XN;
    }

    public static void setPREFIX_XN(String PREFIX_XN) {
        XMLConf.PREFIX_XN = PREFIX_XN;
    }

    public static String getPREFIX_GN() {
        return PREFIX_GN;
    }

    public static void setPREFIX_GN(String PREFIX_GN) {
        XMLConf.PREFIX_GN = PREFIX_GN;
    }

    public static String getFileFormatVersion() {
        return fileFormatVersion;
    }

    public static void setFileFormatVersion(String fileFormatVersion) {
        XMLConf.fileFormatVersion = fileFormatVersion;
    }

    public static String getSenderName() {
        return senderName;
    }

    public static void setSenderName(String senderName) {
        XMLConf.senderName = senderName;
    }

    public static String getVendorName() {
        return vendorName;
    }

    public static void setVendorName(String vendorName) {
        XMLConf.vendorName = vendorName;
    }

    public static String getDnPrefix() {
        return dnPrefix;
    }

    public static void setDnPrefix(String dnPrefix) {
        XMLConf.dnPrefix = dnPrefix;
    }

    public static String getSubNetwork() {
        return SubNetwork;
    }

    public static void setSubNetwork(String SubNetwork) {
        XMLConf.SubNetwork = SubNetwork;
    }

    public static String getBulkCMHeader() {
        return BulkCMHeader;
    }

    public static void setBulkCMHeader(String BulkCMHeader) {
        XMLConf.BulkCMHeader = BulkCMHeader;
    }

    public static TreeMap<String, XMLEntity> getXmlEntities() {
        return xmlEntities;
    }

    public static void setXmlEntities(TreeMap<String, XMLEntity> xmlEntities) {
            xmlEntities = xmlEntities;
    }

    public static void addXmlElement(XMLEntity entity){
        xmlEntities.put(entity.getEntityName(), entity);
    }

    public static String getVsDataFormatVersion() {
        return vsDataFormatVersion;
    }

    public static String getMCC() {
        return MCC;
    }

    public static String getMNC() {
        return MNC;
    }

    public static String getMNCLength() {
        return MNCLength;
    }

    public static void setMCC(String MCC) {
        XMLConf.MCC = MCC;
    }

    public static void setMNC(String MNC) {
        XMLConf.MNC = MNC;
    }

    public static void setMNCLength(String MNCLength) {
        XMLConf.MNCLength = MNCLength;
    }

    public static void setVsDataFormatVersion(String vsDataFormatVersion) {
        XMLConf.vsDataFormatVersion = vsDataFormatVersion;
    }

    public static String getGnPath() {
        return gnPath;
    }

    public static void setGnPath(String gnPath) {
        XMLConf.gnPath = gnPath;
    }

    public static String getUnPath() {
        return unPath;
    }

    public static void setUnPath(String unPath) {
        XMLConf.unPath = unPath;
    }

    public static String getXnPath() {
        return xnPath;
    }

    public static void setXnPath(String xnPath) {
        XMLConf.xnPath = xnPath;
    }

    public static String getEsPath() {
        return esPath;
    }

    public static void setEsPath(String esPath) {
        XMLConf.esPath = esPath;
    }
    
    
    
}
