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
    UtranCell, // COMPLEX
    Pch, // FLAT - UtranCell
    Fach, // FLAT - UtranCell
    Rach, // FLAT - UtranCell
    Hsdsch, // FLAT - UtranCell
    IubLink, // COMPLEX - UtranCell
    CoverageRelation, // COMPLEX - UtranCell
    ServiceArea, // FLAT - UtranCell
    LocationArea, // FLAT - UtranCell
    
    
    GsmRelation, // FLAT - StandAlone
    ExternalGsmCell, // FLAT - StandAlone
    ExternalUtranCell, // COMPLEX - StandAlone
    UtranRelation, // COMPLEX - StandAlone
    
    VsDataContainer // FLAT/COMPLEX
    
    
    
    
    
    
    
    
    

}
