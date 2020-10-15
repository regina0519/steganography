/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steganography;

import java.io.File;

/**
 *
 * @author regina
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        //File tes=new File("/home/regina/Desktop/tes.bmp");
        System.out.println(Functions.strFromBits("01000001010000100100000101001100"));
        //Functions.tesBuild("0101010100000000");
        
        new FormMain().setVisible(true);
    }
    
}
