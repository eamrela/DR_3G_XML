/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vodafone.dr.configuration;

/**
 *
 * @author eamrela
 */
public enum ELEMENTS {
    // VALIDATED
    UtranCell, // COMPLEX - Done
    Pch, // FLAT - UtranCell - Done
    Fach, // FLAT - UtranCell - Done
    Rach, // FLAT - UtranCell - Done
    Hsdsch, // FLAT - UtranCell - Done
    CoverageRelation, // COMPLEX - UtranCell - Done
    
    IubLink, // COMPLEX - UtranCell
    ServiceArea, // FLAT - UtranCell
    LocationArea, // FLAT - UtranCell
    
    
    GsmRelation, // FLAT - StandAlone - Done
    ExternalGsmCell, // FLAT - StandAlone - Done
    ExternalUtranCell, // COMPLEX - StandAlone - Done
    UtranRelation, // COMPLEX - StandAlone - Done
    
    VsDataContainer // FLAT/COMPLEX
    
    
    
    
    
    
    
    
    

}
