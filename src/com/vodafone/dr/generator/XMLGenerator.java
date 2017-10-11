/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vodafone.dr.generator;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.and;
import com.vodafone.dr.configuration.ELEMENTS;
import com.vodafone.dr.configuration.XMLConf;
import static com.vodafone.dr.main.Processor.initApp;
import com.vodafone.dr.mongo.MongoDB;
import com.vodafone.dr.templates.XMLEntity;
import java.util.Date;
import java.util.TreeMap;
import org.bson.Document;


/**
 *  
    
 */
/**
 *
 * @author eamrela
 */
public class XMLGenerator {
    
// ---------------------------------------------------------------------Bundles 
    public static String generateUtranBundle(String siteName,String sourceMTX,String sourceRNC,String targetMTX,String targetRNC){
        FindIterable<Document> utranCells = MongoDB.getUtranCellCollection().find(
                and(Filters.regex("_id", ".*"+siteName+".*"),Filters.eq("RNC", sourceRNC)));
        
        String utranCellBundle = "";
        String utranCellBlock = "";
        String pchFachRachHsdsch = "";
        String serviceArea = "";
        String locationArea = "";
        String coverageRelations = "";
        String iubLink = "";
        String embbededContent = "";
        String id = null;
//        utranCellBundle = getUtranBundleHeader(targetRNC);
        for(Document utranCell : utranCells){
        id = utranCell.getString("UtranCellId");
        utranCellBlock = getUtranCell(id, sourceMTX, sourceRNC, targetMTX, targetRNC);
        pchFachRachHsdsch = getPchRachFachHsdsch(id, sourceMTX, sourceRNC, targetMTX, targetRNC);
        serviceArea = getServiceArea(id, sourceMTX, sourceRNC, targetMTX, targetRNC);
        locationArea = getLocationArea(id, sourceMTX, sourceRNC, targetMTX, targetRNC);
        coverageRelations = getCoverageRelation(id, sourceMTX, sourceRNC, targetMTX, targetRNC);
        iubLink = getIubLink(siteName, sourceMTX, sourceRNC, targetMTX, targetRNC);
        
        embbededContent = serviceArea+"\n\n"+locationArea+"\n\n"+pchFachRachHsdsch+"\n\n"+coverageRelations;
        
        utranCellBlock = utranCellBlock.replace("EMBBEDED-CONTENT", embbededContent);
        utranCellBundle += utranCellBlock;
        }
//        utranCellBundle += getUtranBundleFooter();
        return iubLink+"\n\n"+utranCellBundle;
    }
    
    public static String generateExternalGsmCellBundle(String siteName,String sourceMTX,String sourceRNC,String targetMTX,String targetRNC){
        return getExternalGsmCell(siteName, sourceMTX, sourceRNC, targetMTX, targetRNC);
    }
    
    public static String generateExternalGsmRelationBundle(String siteName,String sourceMTX,String sourceRNC,String targetMTX,String targetRNC){
        FindIterable<Document> gsmRelations = MongoDB.getGsmRelationCollection().find(
                and(Filters.regex("_id", ".*"+siteName+".*"),Filters.eq("RNC", sourceRNC)));
        String id = null;
        String gsmRelationBundle = "";
        for(Document gsmRelation : gsmRelations){
            id = gsmRelation.getString("_id");
            gsmRelationBundle += getUtranBundleHeader(targetRNC);
            gsmRelationBundle += getGsmRelation(id, sourceMTX, sourceRNC, targetMTX, targetRNC);
            gsmRelationBundle += getUtranBundleFooter();
        }
        
        return gsmRelationBundle;
    }
    
    public static String generateExternalUtranCellBundle(String siteName,String sourceMTX,String sourceRNC,String targetMTX,String targetRNC){
        return getExternalUtranCell(siteName, sourceMTX, sourceRNC, targetMTX, targetRNC);
    }
    
    public static String generateUtranRelationBundle(String siteName,String sourceMTX,String sourceRNC,String targetMTX,String targetRNC){
       FindIterable<Document> utranRelations = MongoDB.getUtranRelationCollection().find(
                and(Filters.regex("_id", ".*UtranCell="+siteName+".*,UtranRelation=.*"),
                   Filters.eq("RNC", sourceRNC)));
        String id = null;
        String utranCell=null;
        String utranRelationBundle = "";
        for(Document utranRelation : utranRelations){
            id = utranRelation.getString("_id");
            utranCell = utranRelation.getString("UtranRelationId");
            utranCell = utranCell.substring(utranCell.indexOf("UtranCell=")+10, utranCell.indexOf(",UtranRelation="));
            utranRelationBundle += getUtranRelationBundleHeader(targetRNC,utranCell);
            utranRelationBundle += getUtranRelation(id, sourceMTX, sourceRNC, targetMTX, targetRNC);
            utranRelationBundle += getUtranRelationBundleFooter();
        }
        return utranRelationBundle;
    }
 // ---------------------------------------------------------------------Bundles    



// ---------------------------------------------------------------------Elements 
//VsDataContainer
    public static String getLocationArea(String siteName,String sourceMTX,String sourceRNC,String targetMTX,String targetRNC){
        XMLEntity lacXML = XMLConf.getXmlEntities().get(ELEMENTS.LocationArea.toString());
        Document internallCell = MongoDB.getUtranCellCollection().find(
                and(Filters.regex("_id", ".*"+siteName+".*"),Filters.eq("RNC", sourceRNC))).first();
        if(internallCell!=null){
        TreeMap<String,String> param = new TreeMap<>();
        String lac = internallCell.get("locationAreaRef").toString().replaceAll("LocationArea=", "");
        param.put("id", lac);
        param.put("lac", lac);
        param.put("att", "1");
        param.put("t3212", "20");
        return lacXML.generate(param,false);
        }
        return "";
    }
    
// Seperate
    public static String getIubLink(String siteName,String sourceMTX,String sourceRNC,String targetMTX,String targetRNC){
        XMLEntity iubLinkXML = XMLConf.getXmlEntities().get(ELEMENTS.IubLink.toString());
        FindIterable<Document> iubLinks = MongoDB.getIubLinkCollection().find(
                and(Filters.regex("reservedBy", ".*"+siteName+".*"),Filters.eq("RNC", sourceRNC)));
        TreeMap<String,String> param = null;
        String iubLinksStr="";
        for(Document iubLink : iubLinks){
        param = new TreeMap<>();
        //<editor-fold defaultstate="collapsed" desc="comment">
        
        param.put("id", iubLink.getString("IubLinkId"));
        param.put("userLabel", iubLink.getString("userLabel"));
        param.put("administrativeState", "0");
        param.put("atmUserPlaneTermSubrackRef", "Subrack=MS");
//        param.put("cachedRemoteCpIpAddress1", "");
//        param.put("cachedRemoteCpIpAddress2", "");
        param.put("controlPlaneTransportOption-atm", ((Document)iubLink.get("controlPlaneTransportOption")).getString("atm"));
        param.put("controlPlaneTransportOption-ipv4", ((Document)iubLink.get("controlPlaneTransportOption")).getString("ipv4"));
        param.put("dlHwAdm", iubLink.getString("dlHwAdm"));
        param.put("l2EstReqRetryTimeNbapC", iubLink.getString("l2EstReqRetryTimeNbapC"));
        param.put("l2EstReqRetryTimeNbapD", iubLink.getString("l2EstReqRetryTimeNbapD"));
        param.put("linkType", iubLink.getString("linkType"));
        param.put("poolRedundancy",iubLink.getString("poolRedundancy"));
        param.put("rbsId", iubLink.getString("rbsId"));
        param.put("remoteCpIpAddress1", iubLink.getString("remoteCpIpAddress1"));
        param.put("remoteCpIpAddress2", iubLink.getString("remoteCpIpAddress2"));
        param.put("rncModuleAllocWeight", iubLink.getString("rncModuleAllocWeight"));
        param.put("rncModulePreferredRef", iubLink.getString("rncModulePreferredRef"));
//        param.put("rncModuleReallocate", "");
//        param.put("rncModuleRef", "");
//        param.put("sctpRef", "");
        param.put("softCongThreshGbrBwDl", iubLink.getString("softCongThreshGbrBwDl"));
        param.put("softCongThreshGbrBwUl", iubLink.getString("softCongThreshGbrBwUl"));
        param.put("spare", iubLink.getString("spare"));
        param.put("spareA", iubLink.getString("spareA"));
        param.put("ulHwAdm", iubLink.getString("ulHwAdm"));
        param.put("userPlaneGbrAdmBandwidthDl", iubLink.getString("userPlaneGbrAdmBandwidthDl"));
        param.put("userPlaneGbrAdmBandwidthUl", iubLink.getString("userPlaneGbrAdmBandwidthUl"));
        param.put("userPlaneGbrAdmEnabled", iubLink.getString("userPlaneGbrAdmEnabled"));
        param.put("userPlaneGbrAdmMarginDl", iubLink.getString("userPlaneGbrAdmMarginDl"));
        param.put("userPlaneGbrAdmMarginUl", iubLink.getString("userPlaneGbrAdmMarginUl"));
        param.put("userPlaneIpResourceRef", iubLink.getString("userPlaneIpResourceRef"));
        param.put("userPlaneTransportOption-atm", ((Document)iubLink.get("userPlaneTransportOption")).getString("atm"));
        param.put("userPlaneTransportOption-ipv4", ((Document)iubLink.get("userPlaneTransportOption")).getString("ipv4"));
//</editor-fold>
        iubLinksStr += iubLinkXML.generate(param,false);
        }
        return iubLinksStr;
    }
    
//VsDataContainer
    public static String getServiceArea(String siteName,String sourceMTX,String sourceRNC,String targetMTX,String targetRNC){
        XMLEntity sacXML = XMLConf.getXmlEntities().get(ELEMENTS.ServiceArea.toString());
        FindIterable<Document> sacs = MongoDB.getSacCollection().find(
                and(Filters.regex("reservedBy", ".*"+siteName+".*"),Filters.eq("RNC", sourceRNC)));
        TreeMap<String,String> param = null;
        for(Document sac : sacs){
        param = new TreeMap<>();
        param.put("id", sac.getString("ServiceAreaId"));
        param.put("sac", sac.getString("sac"));
        param.put("userLabel",sac.getString("userLabel"));
        return sacXML.generate(param,false);
        }
        return "";
    }
    
//Seperate
    public static String getExternalGsmCell(String siteName,String sourceMTX,String sourceRNC,String targetMTX,String targetRNC){
        XMLEntity externalGsmCellXML = XMLConf.getXmlEntities().get(ELEMENTS.ExternalGsmCell.toString());
        FindIterable<Document> externalGsmCells = MongoDB.getExternalGsmCellCollection().find(
                and(Filters.regex("reservedBy", ".*"+siteName+".*"),Filters.eq("RNC", sourceRNC)));
        TreeMap<String,String> param = null;
        String externalGsmCellsStr="";
        for(Document externalGsmCell : externalGsmCells){
        param = new TreeMap<>();
        //<editor-fold defaultstate="collapsed" desc="comment">
        
        param.put("id", externalGsmCell.getString("_id").replaceAll(sourceRNC+"_", ""));
        param.put("bcc", externalGsmCell.getString("bcc"));
        param.put("bcchFrequency",externalGsmCell.getString("bcchFrequency"));
        param.put("cellIdentity",externalGsmCell.getString("cellIdentity"));
        param.put("lac",externalGsmCell.getString("lac"));
        param.put("userLabel",externalGsmCell.getString("userLabel"));
        param.put("bandIndicator",externalGsmCell.getString("bandIndicator"));
        param.put("individualOffset",externalGsmCell.getString("individualOffset"));
        param.put("maxTxPowerUl",externalGsmCell.getString("maxTxPowerUl"));
        param.put("qRxLevMin",externalGsmCell.getString("qRxLevMin"));
        param.put("ncc",externalGsmCell.getString("ncc"));
        param.put("mcc",XMLConf.getMCC());
        param.put("mnc",XMLConf.getMNC());
        param.put("mncLength",XMLConf.getMNCLength());
    //</editor-fold>
        externalGsmCellsStr += externalGsmCellXML.generate(param,false);
        }
        return externalGsmCellsStr;
    }
    
//Seperate
    public static String getUtranCell(String siteName,String sourceMTX,String sourceRNC,String targetMTX,String targetRNC){
        XMLEntity utranCellXML = XMLConf.getXmlEntities().get(ELEMENTS.UtranCell.toString());
        FindIterable<Document> utranCells = MongoDB.getUtranCellCollection().find(
                and(Filters.regex("_id", ".*"+siteName+".*"),Filters.eq("RNC", sourceRNC)));
        TreeMap<String,String> param = null;
        String script="";
        String sac = null;
        String lac = null;
        String utranCellIubLink = null;
        String utranCellsStr = "";
        for(Document utranCell : utranCells){
            //<editor-fold defaultstate="collapsed" desc="comment">
        param = new TreeMap<>();
        param.put("id", utranCell.getString("UtranCellId"));
        param.put("bchPower",utranCell.getString("bchPower"));
        param.put("cId",utranCell.getString("cId"));
        lac = utranCell.getString("locationAreaRef");
        lac = lac.substring(lac.indexOf("LocationArea=")+13);
        param.put("lac",lac);
        param.put("localCellId",utranCell.getString("localCellId"));
        param.put("maximumTransmissionPower",utranCell.getString("maximumTransmissionPower"));
        param.put("primaryCpichPower",utranCell.getString("primaryCpichPower"));
        param.put("primarySchPower",utranCell.getString("primarySchPower"));
        param.put("primaryScramblingCode",utranCell.getString("primaryScramblingCode"));
        sac = utranCell.getString("serviceAreaRef");
        sac = sac.substring(sac.indexOf("ServiceArea=")+12);
        param.put("sac",sac);
        param.put("secondarySchPower",utranCell.getString("secondarySchPower"));
        param.put("uarfcnDl",utranCell.getString("uarfcnDl"));
        param.put("uarfcnUl",utranCell.getString("uarfcnUl"));
        param.put("userLabel",utranCell.getString("userLabel"));
        utranCellIubLink = utranCell.getString("iubLinkRef");
        utranCellIubLink = utranCellIubLink.substring(utranCellIubLink.indexOf("=")+1);
        param.put("utranCellIubLink",utranCellIubLink);
        param.put("absPrioCellRes-cellReselectionPriority",((Document)utranCell.get("absPrioCellRes")).getString("cellReselectionPriority"));
        param.put("absPrioCellRes-measIndFach",((Document)utranCell.get("absPrioCellRes")).getString("measIndFach"));
        param.put("absPrioCellRes-sPrioritySearch1",((Document)utranCell.get("absPrioCellRes")).getString("sPrioritySearch1"));
        param.put("absPrioCellRes-sPrioritySearch2",((Document)utranCell.get("absPrioCellRes")).getString("sPrioritySearch2"));
        param.put("absPrioCellRes-threshServingLow",((Document)utranCell.get("absPrioCellRes")).getString("threshServingLow"));
        

        
        param.put("accessClassNBarred",utranCell.getString("accessClassNBarred"));
        param.put("accessClassesBarredCs",utranCell.getString("accessClassesBarredCs"));
        param.put("accessClassesBarredPs",utranCell.getString("accessClassesBarredPs"));

        param.put("admBlockRedirection-gsmRrc",((Document)utranCell.get("admBlockRedirection")).getString("gsmRrc"));
        param.put("admBlockRedirection-rrc",((Document)utranCell.get("admBlockRedirection")).getString("rrc"));
        param.put("admBlockRedirection-speech",((Document)utranCell.get("admBlockRedirection")).getString("speech"));

        param.put("administrativeState","0");
        param.put("agpsEnabled",utranCell.getString("agpsEnabled"));
        param.put("amrNbSelector",utranCell.getString("amrNbSelector"));
        param.put("amrWbRateDlMax",utranCell.getString("amrWbRateDlMax"));
        param.put("amrWbRateUlMax",utranCell.getString("amrWbRateUlMax"));

        param.put("anrIafUtranCellConfig-anrEnabled",((Document)utranCell.get("anrIafUtranCellConfig")).getString("anrEnabled"));
        param.put("anrIafUtranCellConfig-relationAddEnabled",((Document)utranCell.get("anrIafUtranCellConfig")).getString("relationAddEnabled"));

        param.put("antennaPosition-latitude",((Document)utranCell.get("antennaPosition")).getString("latitude"));
        param.put("antennaPosition-latitudeSign",((Document)utranCell.get("antennaPosition")).getString("latitudeSign"));
        param.put("antennaPosition-longitude",((Document)utranCell.get("antennaPosition")).getString("longitude"));

        param.put("aseDlAdm",utranCell.getString("aseDlAdm"));

        param.put("aseLoadThresholdUlSpeech-amr12200",((Document)utranCell.get("aseLoadThresholdUlSpeech")).getString("amr12200"));
        param.put("aseLoadThresholdUlSpeech-amr5900",((Document)utranCell.get("aseLoadThresholdUlSpeech")).getString("amr5900"));
        param.put("aseLoadThresholdUlSpeech-amr7950",((Document)utranCell.get("aseLoadThresholdUlSpeech")).getString("amr7950"));
        param.put("aseLoadThresholdUlSpeech-amrWb12650",((Document)utranCell.get("aseLoadThresholdUlSpeech")).getString("amrWb12650"));
        param.put("aseLoadThresholdUlSpeech-amrWb8850",((Document)utranCell.get("aseLoadThresholdUlSpeech")).getString("amrWb8850"));

        param.put("aseUlAdm",utranCell.getString("aseUlAdm"));
        param.put("autoAcbEnabled",utranCell.getString("autoAcbEnabled"));
        param.put("autoAcbMaxPsClassesToBar",utranCell.getString("autoAcbMaxPsClassesToBar"));
        param.put("autoAcbMinRcssrInput",utranCell.getString("autoAcbMinRcssrInput"));
        param.put("autoAcbRcssrThresh",utranCell.getString("autoAcbRcssrThresh"));
        param.put("autoAcbRcssrWeight",utranCell.getString("autoAcbRcssrWeight"));
        param.put("autoAcbRtwpThresh",utranCell.getString("autoAcbRtwpThresh"));
        param.put("autoAcbRtwpWeight",utranCell.getString("autoAcbRtwpWeight"));
        param.put("cbsSchedulePeriodLength",utranCell.getString("cbsSchedulePeriodLength"));
        param.put("cellBroadcastSac",utranCell.getString("cellBroadcastSac"));
        param.put("cellReserved",utranCell.getString("cellReserved"));
        param.put("cellUpdateConfirmCsInitRepeat",utranCell.getString("cellUpdateConfirmCsInitRepeat"));
        param.put("cellUpdateConfirmPsInitRepeat",utranCell.getString("cellUpdateConfirmPsInitRepeat"));
        param.put("codeLoadThresholdDlSf128",utranCell.getString("codeLoadThresholdDlSf128"));
        param.put("compModeAdm",utranCell.getString("compModeAdm"));
        param.put("cpcSupport",utranCell.getString("cpcSupport"));
        param.put("creationTime",utranCell.getString("creationTime"));
        param.put("ctchAdmMargin",utranCell.getString("ctchAdmMargin"));
        param.put("ctchOccasionPeriod",utranCell.getString("ctchOccasionPeriod"));

        param.put("cyclicAcb-acbEnabled",((Document)utranCell.get("cyclicAcb")).getString("acbEnabled"));
        param.put("cyclicAcb-rotationGroupSize",((Document)utranCell.get("cyclicAcb")).getString("rotationGroupSize"));

        param.put("cyclicAcbCs-acbEnabled",((Document)utranCell.get("cyclicAcbCs")).getString("acbEnabled"));
        param.put("cyclicAcbCs-rotationGroupSize",((Document)utranCell.get("cyclicAcbCs")).getString("rotationGroupSize"));

        param.put("cyclicAcbPs-acbEnabled",((Document)utranCell.get("cyclicAcbPs")).getString("acbEnabled"));
        param.put("cyclicAcbPs-rotationGroupSize",((Document)utranCell.get("cyclicAcbPs")).getString("rotationGroupSize"));

        param.put("dchIflsMarginCode",utranCell.getString("dchIflsMarginCode"));
        param.put("dchIflsMarginPower",utranCell.getString("dchIflsMarginPower"));
        param.put("dchIflsThreshCode",utranCell.getString("dchIflsThreshCode"));
        param.put("dchIflsThreshPower",utranCell.getString("dchIflsThreshPower"));
        param.put("dlCodeAdm",utranCell.getString("dlCodeAdm"));
        param.put("dlCodeOffloadLimit",utranCell.getString("dlCodeOffloadLimit"));
        param.put("dlCodePowerCmEnabled",utranCell.getString("dlCodePowerCmEnabled"));
        param.put("dlPowerOffloadLimit",utranCell.getString("dlPowerOffloadLimit"));
        param.put("dmcrEnabled",utranCell.getString("dmcrEnabled"));
        param.put("dnclEnabled",utranCell.getString("dnclEnabled"));
        param.put("downswitchTimer",utranCell.getString("downswitchTimer"));
        param.put("eulMcServingCellUsersAdmTti2",utranCell.getString("eulMcServingCellUsersAdmTti2"));
        param.put("eulNonServingCellUsersAdm",utranCell.getString("eulNonServingCellUsersAdm"));
        param.put("eulServingCellUsersAdm",utranCell.getString("eulServingCellUsersAdm"));
        param.put("eulServingCellUsersAdmTti2",utranCell.getString("eulServingCellUsersAdmTti2"));
        param.put("fachMeasOccaCycLenCoeff",utranCell.getString("fachMeasOccaCycLenCoeff"));
        param.put("fdpchSupport",utranCell.getString("fdpchSupport"));
        param.put("ganHoEnabled",utranCell.getString("ganHoEnabled"));
        param.put("hardIfhoCorr",utranCell.getString("hardIfhoCorr"));

        param.put("hcsSib3Config-hcsPrio",((Document)utranCell.get("hcsSib3Config")).getString("hcsPrio"));
        param.put("hcsSib3Config-qHcs",((Document)utranCell.get("hcsSib3Config")).getString("qHcs"));
        param.put("hcsSib3Config-sSearchHcs",((Document)utranCell.get("hcsSib3Config")).getString("sSearchHcs"));

        param.put("hcsUsage-connectedMode",((Document)utranCell.get("hcsUsage")).getString("connectedMode"));
        param.put("hcsUsage-idleMode",((Document)utranCell.get("hcsUsage")).getString("idleMode"));

        param.put("hoType",utranCell.getString("hoType"));

        param.put("hsIflsDownswitchTrigg-fastDormancy",((Document)utranCell.get("hsIflsDownswitchTrigg")).getString("fastDormancy"));
        param.put("hsIflsDownswitchTrigg-toFach",((Document)utranCell.get("hsIflsDownswitchTrigg")).getString("toFach"));
        param.put("hsIflsDownswitchTrigg-toUra",((Document)utranCell.get("hsIflsDownswitchTrigg")).getString("toUra"));

        param.put("hsIflsHighLoadThresh",utranCell.getString("hsIflsHighLoadThresh"));
        param.put("hsIflsMarginUsers",utranCell.getString("hsIflsMarginUsers"));
        param.put("hsIflsPowerLoadThresh",utranCell.getString("hsIflsPowerLoadThresh"));
        param.put("hsIflsRedirectLoadLimit",utranCell.getString("hsIflsRedirectLoadLimit"));
        param.put("hsIflsSpeechMultiRabTrigg",utranCell.getString("hsIflsSpeechMultiRabTrigg"));
        param.put("hsIflsThreshUsers",utranCell.getString("hsIflsThreshUsers"));

        param.put("hsIflsTrigger-fromFach",((Document)utranCell.get("hsIflsTrigger")).getString("fromFach"));
        param.put("hsIflsTrigger-fromUra",((Document)utranCell.get("hsIflsTrigger")).getString("fromUra"));

        param.put("hsdpaUsersAdm",utranCell.getString("hsdpaUsersAdm"));
        param.put("hsdpaUsersOffloadLimit",utranCell.getString("hsdpaUsersOffloadLimit"));
        param.put("hsdschInactivityTimer",utranCell.getString("hsdschInactivityTimer"));
        param.put("hsdschInactivityTimerCpc",utranCell.getString("hsdschInactivityTimerCpc"));
        param.put("iFCong",utranCell.getString("iFCong"));
        param.put("iFHyst",utranCell.getString("iFHyst"));
        param.put("ifIratHoPsIntHsEnabled",utranCell.getString("ifIratHoPsIntHsEnabled"));
        param.put("iflsCpichEcnoThresh",utranCell.getString("iflsCpichEcnoThresh"));
        param.put("iflsMode",utranCell.getString("iflsMode"));
        param.put("iflsRedirectUarfcn",utranCell.getString("iflsRedirectUarfcn"));
        param.put("inactivityTimeMultiPsInteractive",utranCell.getString("inactivityTimeMultiPsInteractive"));
        param.put("inactivityTimer",utranCell.getString("inactivityTimer"));
        param.put("inactivityTimerEnhUeDrx",utranCell.getString("inactivityTimerEnhUeDrx"));
        param.put("inactivityTimerPch",utranCell.getString("inactivityTimerPch"));
        param.put("individualOffset",utranCell.getString("individualOffset"));
        param.put("interFreqFddMeasIndicator",utranCell.getString("interFreqFddMeasIndicator"));
        param.put("interPwrMax",utranCell.getString("interPwrMax"));
        param.put("interRate",utranCell.getString("interRate"));
        param.put("iubLinkRef",utranCell.getString("iubLinkRef"));
        param.put("loadBasedHoSupport",utranCell.getString("loadBasedHoSupport"));
        param.put("loadBasedHoType",utranCell.getString("loadBasedHoType"));
        param.put("loadSharingGsmFraction",utranCell.getString("loadSharingGsmFraction"));
        param.put("loadSharingGsmThreshold",utranCell.getString("loadSharingGsmThreshold"));
        param.put("loadSharingMargin",utranCell.getString("loadSharingMargin"));
        param.put("maxPwrMax",utranCell.getString("maxPwrMax"));
        param.put("maxRate",utranCell.getString("maxRate"));
        param.put("maxTxPowerUl",utranCell.getString("maxTxPowerUl"));
        param.put("minPwrMax",utranCell.getString("minPwrMax"));
        param.put("minPwrRl",utranCell.getString("minPwrRl"));
        param.put("minimumRate",utranCell.getString("minimumRate"));
//        param.put("mocnCellProfileRef",utranCell.getString("mocnCellProfileRef"));
        param.put("nOutSyncInd",utranCell.getString("nOutSyncInd"));

//        param.put("pagingPermAccessCtrl-locRegAcb",((Document)utranCell.get("pagingPermAccessCtrl")).getString("locRegAcb"));
//        param.put("pagingPermAccessCtrl-locRegRestr",((Document)utranCell.get("pagingPermAccessCtrl")).getString("locRegRestr"));
//        param.put("pagingPermAccessCtrl-pagingRespRestr",((Document)utranCell.get("pagingPermAccessCtrl")).getString("pagingRespRestr"));

        param.put("pathlossThreshold",utranCell.getString("pathlossThreshold"));
        param.put("poolRedundancy",utranCell.getString("poolRedundancy"));
        param.put("primaryTpsCell",utranCell.getString("primaryTpsCell"));
        param.put("pwrAdm",utranCell.getString("pwrAdm"));
        param.put("pwrHyst",utranCell.getString("pwrHyst"));

        param.put("pwrLoadThresholdDlSpeech-amr12200",((Document)utranCell.get("pwrLoadThresholdDlSpeech")).getString("amr12200"));
        param.put("pwrLoadThresholdDlSpeech-amr5900",((Document)utranCell.get("pwrLoadThresholdDlSpeech")).getString("amr5900"));
        param.put("pwrLoadThresholdDlSpeech-amr7950",((Document)utranCell.get("pwrLoadThresholdDlSpeech")).getString("amr7950"));
        param.put("pwrLoadThresholdDlSpeech-amrWb12650",((Document)utranCell.get("pwrLoadThresholdDlSpeech")).getString("amrWb12650"));
        param.put("pwrLoadThresholdDlSpeech-amrWb8850",((Document)utranCell.get("pwrLoadThresholdDlSpeech")).getString("amrWb8850"));

        param.put("pwrOffset",utranCell.getString("pwrOffset"));
        param.put("qHyst1",utranCell.getString("qHyst1"));
        param.put("qHyst2",utranCell.getString("qHyst2"));
        param.put("qQualMin",utranCell.getString("qQualMin"));
        param.put("qRxLevMin",utranCell.getString("qRxLevMin"));
        param.put("qualMeasQuantity",utranCell.getString("qualMeasQuantity"));
        param.put("rachOverloadProtect",utranCell.getString("rachOverloadProtect"));

        param.put("rateSelectionPsInteractive-channelType",((Document)utranCell.get("rateSelectionPsInteractive")).getString("channelType"));
        param.put("rateSelectionPsInteractive-dlPrefRate",((Document)utranCell.get("rateSelectionPsInteractive")).getString("dlPrefRate"));
        param.put("rateSelectionPsInteractive-ulPrefRate",((Document)utranCell.get("rateSelectionPsInteractive")).getString("ulPrefRate"));

        param.put("redirectUarfcn",utranCell.getString("redirectUarfcn"));
        param.put("releaseAseDl",utranCell.getString("releaseAseDl"));
        param.put("releaseAseDlNg",utranCell.getString("releaseAseDlNg"));
        param.put("releaseRedirect",utranCell.getString("releaseRedirect"));

        param.put("releaseRedirectEutraTriggers-csFallbackCsRelease",((Document)utranCell.get("releaseRedirectEutraTriggers")).getString("csFallbackCsRelease"));
        param.put("releaseRedirectEutraTriggers-csFallbackDchToFach",((Document)utranCell.get("releaseRedirectEutraTriggers")).getString("csFallbackDchToFach"));
        param.put("releaseRedirectEutraTriggers-dchToFach",((Document)utranCell.get("releaseRedirectEutraTriggers")).getString("dchToFach"));
        param.put("releaseRedirectEutraTriggers-fachToUra",((Document)utranCell.get("releaseRedirectEutraTriggers")).getString("fachToUra"));
        param.put("releaseRedirectEutraTriggers-fastDormancy",((Document)utranCell.get("releaseRedirectEutraTriggers")).getString("fastDormancy"));
        param.put("releaseRedirectEutraTriggers-normalRelease",((Document)utranCell.get("releaseRedirectEutraTriggers")).getString("normalRelease"));

        param.put("releaseRedirectHsIfls",utranCell.getString("releaseRedirectHsIfls"));
        param.put("reportingRange1a",utranCell.getString("reportingRange1a"));
        param.put("reportingRange1b",utranCell.getString("reportingRange1b"));

        param.put("rlFailureT",utranCell.getString("rlFailureT"));
        param.put("rrcLcEnabled",utranCell.getString("rrcLcEnabled"));
        param.put("rwrEutraCc",utranCell.getString("rwrEutraCc"));
        param.put("sHcsRat",utranCell.getString("sHcsRat"));
        param.put("sInterSearch",utranCell.getString("sInterSearch"));
        param.put("sIntraSearch",utranCell.getString("sIntraSearch"));
        param.put("sRatSearch",utranCell.getString("sRatSearch"));
        param.put("secondaryCpichPower",utranCell.getString("secondaryCpichPower"));
        param.put("servDiffRrcAdmHighPrioProfile",utranCell.getString("servDiffRrcAdmHighPrioProfile"));

        param.put("serviceRestrictions-csVideoCalls",((Document)utranCell.get("serviceRestrictions")).getString("csVideoCalls"));

        param.put("sf128Adm",utranCell.getString("sf128Adm"));
        param.put("sf16Adm",utranCell.getString("sf16Adm"));
        param.put("sf16AdmUl",utranCell.getString("sf16AdmUl"));
        param.put("sf16gAdm",utranCell.getString("sf16gAdm"));
        param.put("sf32Adm",utranCell.getString("sf32Adm"));
        param.put("sf4AdmUl",utranCell.getString("sf4AdmUl"));
        param.put("sf64AdmUl",utranCell.getString("sf64AdmUl"));
        param.put("sf8Adm",utranCell.getString("sf8Adm"));
        param.put("sf8AdmUl",utranCell.getString("sf8AdmUl"));
        param.put("sf8gAdmUl",utranCell.getString("sf8gAdmUl"));
        param.put("sib1PlmnScopeValueTag",utranCell.getString("sib1PlmnScopeValueTag"));
        param.put("spare",utranCell.getString("spare"));
        

        param.put("spareA",utranCell.getString("spareA"));
        param.put("srbAdmExempt",utranCell.getString("srbAdmExempt"));
        param.put("standAloneSrbSelector",utranCell.getString("standAloneSrbSelector"));
        param.put("tCell",utranCell.getString("tCell"));
        param.put("timeToTrigger1a",utranCell.getString("timeToTrigger1a"));
        param.put("timeToTrigger1b",utranCell.getString("timeToTrigger1b"));
        param.put("tmCongAction",utranCell.getString("tmCongAction"));
        param.put("tmCongActionNg",utranCell.getString("tmCongActionNg"));
        param.put("tmInitialG",utranCell.getString("tmInitialG"));

        param.put("tpsCellThresholds-tpsCellThreshEnabled",((Document)utranCell.get("tpsCellThresholds")).getString("tpsCellThreshEnabled"));
        param.put("tpsCellThresholds-tpsLockThreshold",((Document)utranCell.get("tpsCellThresholds")).getString("tpsLockThreshold"));
        param.put("tpsCellThresholds-tpsUnlockThreshold",((Document)utranCell.get("tpsCellThresholds")).getString("tpsUnlockThreshold"));

        param.put("tpsPowerLockState",utranCell.getString("tpsPowerLockState"));
        param.put("transmissionScheme",utranCell.getString("transmissionScheme"));
        param.put("treSelection",utranCell.getString("treSelection"));
        param.put("ueHsThpMeasSupport",utranCell.getString("ueHsThpMeasSupport"));
        param.put("updateLocator",utranCell.getString("updateLocator"));
        param.put("usedFreqThresh2dEcno",utranCell.getString("usedFreqThresh2dEcno"));
        param.put("usedFreqThresh2dRscp",utranCell.getString("usedFreqThresh2dRscp"));
//        param.put("utranCellPosition","");
        //</editor-fold>
        utranCellsStr += utranCellXML.generate(param,true);
        }
        return utranCellsStr;
    }
    
//VsDataContainer
    public static String getPchRachFachHsdsch(String siteName,String sourceMTX,String sourceRNC,String targetMTX,String targetRNC){
        XMLEntity pchXML = XMLConf.getXmlEntities().get(ELEMENTS.Pch.toString());
        XMLEntity fachXML = XMLConf.getXmlEntities().get(ELEMENTS.Fach.toString());
        XMLEntity rachXML = XMLConf.getXmlEntities().get(ELEMENTS.Rach.toString());
        XMLEntity hsdschXML = XMLConf.getXmlEntities().get(ELEMENTS.Hsdsch.toString());
        FindIterable<Document> utranCells = MongoDB.getUtranCellCollection().find(
                and(Filters.regex("_id", ".*"+siteName+".*"),Filters.eq("RNC", sourceRNC)));
        TreeMap<String,String> paramPch = null;
        TreeMap<String,String> paramFach = null;
        TreeMap<String,String> paramRach = null;
        TreeMap<String,String> paramHsdsch = null;
        String pchFachRachHsdschStr = "";
        for(Document uranCell : utranCells){
        paramPch = new TreeMap<>();
        paramFach = new TreeMap<>();
        paramRach = new TreeMap<>();
        paramHsdsch = new TreeMap<>();
        //<editor-fold defaultstate="collapsed" desc="Pch">
        paramPch.put("id",((Document)uranCell.get("pch")).getString("PchId"));
        paramPch.put("administrativeState","0");
        paramPch.put("pchPower",((Document)uranCell.get("pch")).getString("pchPower"));
        paramPch.put("pichPower",((Document)uranCell.get("pch")).getString("pichPower"));
        paramPch.put("sccpchOffset",((Document)uranCell.get("pch")).getString("sccpchOffset"));
        paramPch.put("userLabel","Pch "+((Document)uranCell.get("pch")).getString("PchId"));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Fach">
        paramFach.put("id",((Document)uranCell.get("fach")).getString("FachId"));
        paramFach.put("administrativeState","0");
        paramFach.put("maxFach1Power",((Document)uranCell.get("fach")).getString("maxFach1Power"));
        paramFach.put("maxFach2Power",((Document)uranCell.get("fach")).getString("maxFach2Power"));
        paramFach.put("pOffset1Fach",((Document)uranCell.get("fach")).getString("pOffset1Fach"));
        paramFach.put("pOffset3Fach",((Document)uranCell.get("fach")).getString("pOffset3Fach"));
        paramFach.put("sccpchOffset",((Document)uranCell.get("fach")).getString("sccpchOffset"));
        paramFach.put("userLabel","Fach "+((Document)uranCell.get("fach")).getString("FachId"));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Rach">
        paramRach.put("id",((Document)uranCell.get("rach")).getString("RachId"));
        paramRach.put("administrativeState","0");
        paramRach.put("aichPower",((Document)uranCell.get("rach")).getString("aichPower"));
        paramRach.put("aichTransmissionTiming",((Document)uranCell.get("rach")).getString("aichTransmissionTiming"));
        paramRach.put("constantValueCprach",((Document)uranCell.get("rach")).getString("constantValueCprach"));
        paramRach.put("increasedRachCoverageEnabled",((Document)uranCell.get("rach")).getString("increasedRachCoverageEnabled"));
        paramRach.put("maxPreambleCycle",((Document)uranCell.get("rach")).getString("maxPreambleCycle"));
        paramRach.put("nb01Max",((Document)uranCell.get("rach")).getString("nb01Max"));
        paramRach.put("nb01Min",((Document)uranCell.get("rach")).getString("nb01Min"));
        paramRach.put("powerOffsetP0",((Document)uranCell.get("rach")).getString("powerOffsetP0"));
        paramRach.put("powerOffsetPpm",((Document)uranCell.get("rach")).getString("powerOffsetPpm"));
        paramRach.put("preambleRetransMax",((Document)uranCell.get("rach")).getString("preambleRetransMax"));
        paramRach.put("preambleSignatures",((Document)uranCell.get("rach")).getString("preambleSignatures"));
        paramRach.put("scramblingCodeWordNo",((Document)uranCell.get("rach")).getString("scramblingCodeWordNo"));
        paramRach.put("spreadingFactor",((Document)uranCell.get("rach")).getString("spreadingFactor"));
        paramRach.put("subChannelNo",((Document)uranCell.get("rach")).getString("subChannelNo"));
        paramRach.put("userLabel","Rach "+((Document)uranCell.get("rach")).getString("RachId"));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Hsdsch">
        paramHsdsch.put("id",((Document)uranCell.get("Hsdsch")).getString("HsdschId"));
        paramHsdsch.put("administrativeState","0");
        paramHsdsch.put("codeThresholdPdu656",((Document)uranCell.get("Hsdsch")).getString("codeThresholdPdu656"));
        paramHsdsch.put("cqiFeedbackCycle",((Document)uranCell.get("Hsdsch")).getString("cqiFeedbackCycle"));
        paramHsdsch.put("deltaAck1",((Document)uranCell.get("Hsdsch")).getString("deltaAck1"));
        paramHsdsch.put("deltaAck2",((Document)uranCell.get("Hsdsch")).getString("deltaAck2"));
        paramHsdsch.put("deltaCqi1",((Document)uranCell.get("Hsdsch")).getString("deltaCqi1"));
        paramHsdsch.put("deltaCqi2",((Document)uranCell.get("Hsdsch")).getString("deltaCqi2"));
        paramHsdsch.put("deltaNack1",((Document)uranCell.get("Hsdsch")).getString("deltaNack1"));
        paramHsdsch.put("deltaNack2",((Document)uranCell.get("Hsdsch")).getString("deltaNack2"));
        paramHsdsch.put("enhUeDrxSupport",((Document)uranCell.get("Hsdsch")).getString("enhUeDrxSupport"));
        paramHsdsch.put("enhancedL2Support",((Document)uranCell.get("Hsdsch")).getString("enhancedL2Support"));
        paramHsdsch.put("hsAqmCongCtrlSpiSupport",((Document)uranCell.get("Hsdsch")).getString("hsAqmCongCtrlSpiSupport"));
        paramHsdsch.put("hsAqmCongCtrlSupport",((Document)uranCell.get("Hsdsch")).getString("hsAqmCongCtrlSupport"));
        paramHsdsch.put("hsFachSupport",((Document)uranCell.get("Hsdsch")).getString("hsFachSupport"));
        paramHsdsch.put("hsMeasurementPowerOffset",((Document)uranCell.get("Hsdsch")).getString("hsMeasurementPowerOffset"));
        paramHsdsch.put("initialAckNackRepetitionFactor",((Document)uranCell.get("Hsdsch")).getString("initialAckNackRepetitionFactor"));
        paramHsdsch.put("initialCqiRepetitionFactor",((Document)uranCell.get("Hsdsch")).getString("initialCqiRepetitionFactor"));
        paramHsdsch.put("numHsPdschCodes",((Document)uranCell.get("Hsdsch")).getString("numHsPdschCodes"));
        paramHsdsch.put("numHsScchCodes",((Document)uranCell.get("Hsdsch")).getString("numHsScchCodes"));
        paramHsdsch.put("qam64MimoSupport",((Document)uranCell.get("Hsdsch")).getString("qam64MimoSupport"));
        paramHsdsch.put("qam64Support",((Document)uranCell.get("Hsdsch")).getString("qam64Support"));
        paramHsdsch.put("userLabel","hsdsch "+((Document)uranCell.get("Hsdsch")).getString("HsdschId"));
        //</editor-fold>
        pchFachRachHsdschStr += pchXML.generate(paramPch,false);
        pchFachRachHsdschStr += fachXML.generate(paramFach,false);
        pchFachRachHsdschStr += rachXML.generate(paramRach,false);
        pchFachRachHsdschStr += hsdschXML.generate(paramHsdsch,false);
        }
        return pchFachRachHsdschStr;
    }
    
//VsDataContainer
    public static String getCoverageRelation(String siteName,String sourceMTX,String sourceRNC,String targetMTX,String targetRNC){
        XMLEntity coverageXML = XMLConf.getXmlEntities().get(ELEMENTS.CoverageRelation.toString());
        FindIterable<Document> coverageRelations = MongoDB.getCoverageRelationCollection().find(
                and(Filters.regex("_id", ".*UtranCell=.*"+siteName+".*"),Filters.eq("RNC", sourceRNC)));
        TreeMap<String,String> param = null;
        String coverageRelationStr="";
        for(Document coverageRelation : coverageRelations){
        param = new TreeMap<>();
        param.put("id",coverageRelation.getString("CoverageRelationId"));
        param.put("coverageIndicator",coverageRelation.getString("coverageIndicator"));
        param.put("hsIflsDownswitch",coverageRelation.getString("hsIflsDownswitch"));
        param.put("hsPathlossThreshold",coverageRelation.getString("hsPathlossThreshold"));
        param.put("relationCapability-dchLoadSharing",((Document)coverageRelation.get("relationCapability")).getString("dchLoadSharing"));
        param.put("relationCapability-hsCellSelection",((Document)coverageRelation.get("relationCapability")).getString("hsCellSelection"));
        param.put("relationCapability-hsLoadSharing",((Document)coverageRelation.get("relationCapability")).getString("hsLoadSharing"));
        param.put("relationCapability-powerSave",((Document)coverageRelation.get("relationCapability")).getString("powerSave"));
        param.put("utranCellRef",coverageRelation.getString("utranCellRef"));
       
       coverageRelationStr += coverageXML.generate(param,false);
        }
        return coverageRelationStr;
    }
    
//Seperate
    public static String getGsmRelation(String siteName,String sourceMTX,String sourceRNC,String targetMTX,String targetRNC){
        XMLEntity gsmRelationXML = XMLConf.getXmlEntities().get(ELEMENTS.GsmRelation.toString());
        FindIterable<Document> gsmRelations = MongoDB.getGsmRelationCollection().find(
                and(Filters.regex("_id", siteName),Filters.eq("RNC", sourceRNC)));
        TreeMap<String,String> param = null;
        String id = null;
        String gsmRelationStr="";
        for(Document gsmRelation : gsmRelations){
        param = new TreeMap<>();
        id = gsmRelation.getString("GsmRelationId");
        param.put("id", id.substring(id.indexOf(",GsmRelation=")+13));
        param.put("adjacentCell",gsmRelation.getString("externalGsmCellRef"));
        param.put("mobilityRelationType",gsmRelation.getString("mobilityRelationType"));
        param.put("qOffset1sn",gsmRelation.getString("qOffset1sn"));
        param.put("selectionPriority",gsmRelation.getString("selectionPriority"));
        gsmRelationStr += gsmRelationXML.generate(param,false);
        }
        return gsmRelationStr;
    }
    
//Seperate
    public static String getExternalUtranCell(String siteName,String sourceMTX,String sourceRNC,String targetMTX,String targetRNC){
        XMLEntity externalUtranXML = XMLConf.getXmlEntities().get(ELEMENTS.ExternalUtranCell.toString());
        FindIterable<Document> externalUtranCells = MongoDB.getExternalUtranCellCollection().find(
                and(Filters.regex("reservedBy", ".*UtranCell="+siteName+".*,UtranRelation=.*"),Filters.eq("RNC", sourceRNC)));
        TreeMap<String,String> param = null;
        String externalUtranCellStr="";
        String rncId = null;
        for(Document externalUtranCell : externalUtranCells){
        param = new TreeMap<>();
        //<editor-fold defaultstate="collapsed" desc="comment">
        rncId = externalUtranCell.getString("_id");
        rncId = rncId.substring(rncId.indexOf("IurLink=")+8,rncId.indexOf("IurLink=")+13);
        param.put("id", externalUtranCell.getString("ExternalUtranCellId"));
        param.put("cId",externalUtranCell.getString("cId"));
        param.put("lac",externalUtranCell.getString("lac"));
        param.put("mcc",XMLConf.getMCC());
        param.put("mnc",XMLConf.getMNC());
        param.put("primaryCpichPower",externalUtranCell.getString("primaryCpichPower"));
        param.put("primaryScramblingCode",externalUtranCell.getString("primaryScramblingCode"));
        param.put("rac",externalUtranCell.getString("rac"));
        param.put("rncId",rncId);
        param.put("uarfcnDl",externalUtranCell.getString("uarfcnDl"));
        param.put("uarfcnUl",externalUtranCell.getString("uarfcnUl"));
        param.put("userLabel",externalUtranCell.getString("userLabel"));
        param.put("agpsEnabled",externalUtranCell.getString("agpsEnabled"));
        param.put("cellCapability-cpcSupport",((Document)externalUtranCell.get("cellCapability")).getString("cpcSupport"));
        param.put("cellCapability-edchSupport",((Document)externalUtranCell.get("cellCapability")).getString("edchSupport"));
        param.put("cellCapability-edchTti2Support",((Document)externalUtranCell.get("cellCapability")).getString("edchTti2Support"));
        param.put("cellCapability-enhancedL2Support",((Document)externalUtranCell.get("cellCapability")).getString("enhancedL2Support"));
        param.put("cellCapability-fdpchSupport",((Document)externalUtranCell.get("cellCapability")).getString("fdpchSupport"));
        param.put("cellCapability-hsdschSupport",((Document)externalUtranCell.get("cellCapability")).getString("hsdschSupport"));
        param.put("cellCapability-multiCarrierSupport",((Document)externalUtranCell.get("cellCapability")).getString("multiCarrierSupport"));
        param.put("cellCapability-qam64MimoSupport",((Document)externalUtranCell.get("cellCapability")).getString("qam64MimoSupport"));
        param.put("hsAqmCongCtrlSpiSupport",externalUtranCell.getString("hsAqmCongCtrlSpiSupport"));
        param.put("hsAqmCongCtrlSupport",externalUtranCell.getString("hsAqmCongCtrlSupport"));
        param.put("individualOffset",externalUtranCell.getString("individualOffset"));
//        param.put("lbUtranCellOffloadCapacity",null);
        param.put("maxTxPowerUl",externalUtranCell.getString("maxTxPowerUl"));
        param.put("mncLength",XMLConf.getMNCLength());
        param.put("qQualMin",externalUtranCell.getString("qQualMin"));
        param.put("qRxLevMin",externalUtranCell.getString("qRxLevMin"));
        param.put("reportingRange1a",externalUtranCell.getString("reportingRange1a"));
        param.put("reportingRange1b",externalUtranCell.getString("reportingRange1b"));
        param.put("timeToTrigger1a",externalUtranCell.getString("timeToTrigger1a"));
        param.put("timeToTrigger1b",externalUtranCell.getString("timeToTrigger1b"));
        param.put("transmissionScheme",externalUtranCell.getString("transmissionScheme"));
        //</editor-fold>
        externalUtranCellStr += externalUtranXML.generate(param,false);
        }
        return externalUtranCellStr;
    }
    
//Seperate
    public static String getUtranRelation(String siteName,String sourceMTX,String sourceRNC,String targetMTX,String targetRNC){
        XMLEntity utranRelationXML = XMLConf.getXmlEntities().get(ELEMENTS.UtranRelation.toString());
        FindIterable<Document> utranRelations = MongoDB.getUtranRelationCollection().find(
                and(Filters.regex("_id", siteName),
                   Filters.eq("RNC", sourceRNC)));
        String id = null;
        TreeMap<String,String> param = null;
        String utranRelationStr="";
        String subNetwork = null;
        String externalUtranCell=null;
        for(Document utranRelation : utranRelations){
        param = new TreeMap<>();
        id = utranRelation.getString("UtranRelationId"); 
        id = id.substring(id.indexOf("UtranRelation=")+14);
        
        subNetwork = utranRelation.getString("utranCellRef");
        if(subNetwork.toLowerCase().contains("iur")){
        subNetwork = subNetwork.substring(subNetwork.indexOf("IurLink=")+8,subNetwork.indexOf("IurLink=")+13);
        }else{
        subNetwork = targetRNC;
        }
        externalUtranCell = utranRelation.getString("utranCellRef");
        externalUtranCell = externalUtranCell.substring(externalUtranCell.indexOf("UtranCell="));
        externalUtranCell = externalUtranCell.replaceAll("UtranCell=", "");
        param.put("id", id);
        param.put("adjacentCell","SubNetwork="+XMLConf.getSubNetwork()+",SubNetwork="+subNetwork+",MeContext="+subNetwork+",ManagedElement=1,RncFunction=1,UtranCell="+externalUtranCell);
        param.put("createdBy",utranRelation.getString("createdBy"));
        param.put("creationTime",utranRelation.getString("creationTime"));
        param.put("frequencyRelationType",utranRelation.getString("frequencyRelationType"));
        param.put("hcsSib11Config-hcsPrio",((Document)utranRelation.get("hcsSib11Config")).getString("hcsPrio"));
        param.put("hcsSib11Config-penaltyTime",((Document)utranRelation.get("hcsSib11Config")).getString("penaltyTime"));
        param.put("hcsSib11Config-qHcs",((Document)utranRelation.get("hcsSib11Config")).getString("qHcs"));
        param.put("hcsSib11Config-temporaryOffset1",((Document)utranRelation.get("hcsSib11Config")).getString("temporaryOffset1"));
        param.put("hcsSib11Config-temporaryOffset2",((Document)utranRelation.get("hcsSib11Config")).getString("temporaryOffset2"));
        param.put("loadSharingCandidate",utranRelation.getString("loadSharingCandidate"));
        param.put("mobilityRelationType",utranRelation.getString("mobilityRelationType"));
        param.put("nodeRelationType",utranRelation.getString("nodeRelationType"));
        param.put("qOffset1sn",utranRelation.getString("qOffset1sn"));
        param.put("qOffset2sn",utranRelation.getString("qOffset2sn"));
        param.put("selectionPriority",utranRelation.getString("selectionPriority"));
        if(utranRelation.get("utranCellRef").toString().contains("ExternalUtranCell=")){
            // adjust RNC here
        }else{
            // adjust RNC here
        }

        utranRelationStr += utranRelationXML.generate(param,false);
        }
        return utranRelationStr;
    }

    // ---------------------------------------------------------------------Elements
    
    
    
    
// Footers and Headers    
    public static String getFileHeader(){
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                XMLConf.getBulkCMHeader() +"\n"+
                "<fileHeader fileFormatVersion=\""+XMLConf.getFileFormatVersion()+"\" senderName=\""+XMLConf.getSenderName()+"\" vendorName=\""+XMLConf.getVendorName()+"\"/>\n" +
                "<configData dnPrefix=\""+XMLConf.getDnPrefix()+"\">\n" +
                "  <xn:SubNetwork id=\""+XMLConf.getSubNetwork()+"\">\n";
    }
    
    public static String getFileFooter(){
        return "\n</xn:SubNetwork>\n" +
                "</configData>\n" +
                "<fileFooter dateTime=\""+new Date()+"\"/>\n" +
                "</bulkCmConfigDataFile>";
    }
    
    public static String getUtranBundleHeader(String targetRNC){
        return "<xn:SubNetwork id=\""+targetRNC+"\">\n" +
                "<xn:MeContext id=\""+targetRNC+"\">\n" +
                "<xn:ManagedElement id=\"1\">\n" +
                "<un:RncFunction id=\"1\">\n";
    }
    
    public static String getUtranBundleFooter(){
        return "\n</un:RncFunction>\n" +
                "</xn:ManagedElement>\n" +
                "</xn:MeContext>\n" +
                "</xn:SubNetwork>\n";
    }
    
    public static String getUtranRelationBundleHeader(String targetRNC,String utranCell){
        return "<xn:SubNetwork id=\""+targetRNC+"\">\n" +
                "<xn:MeContext id=\""+targetRNC+"\">\n" +
                "<xn:ManagedElement id=\"1\">\n" +
                "<un:RncFunction id=\"1\">\n"
                + "<un:UtranCell id=\""+utranCell+"\">\n";
    }
    
    public static String getUtranRelationBundleFooter(){
        return "\n</un:UtranCell>\n"
                + "</un:RncFunction>\n" +
                "</xn:ManagedElement>\n" +
                "</xn:MeContext>\n" +
                "</xn:SubNetwork>\n";
    }
    
    public static void main(String[] args) {
        initApp("C:\\Documents\\DR_3G\\DR3G.conf");
        System.out.println(getIubLink("UCAI2586", "Source", "CRX06", "Target", "RNC30"));
    }
    
   
}
