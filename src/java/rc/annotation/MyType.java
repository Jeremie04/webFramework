/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rc.annotation;

/**
 *
 * @author Mitasoa
 */
public enum MyType {
    SERIAL("auto_increment"),
    SEQUENCE("sequence"),
    None("normal");
    private final String sequence;
    
    private MyType(String sequence) {
        this.sequence = sequence;
    }
    
    private String getSequence(){
        return sequence;
    }
    
}
