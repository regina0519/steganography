/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steganography;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author regina
 */
public class LSB {
    private BufferedImage coverImg;
    private File secretImg;
    private final String key="ABAL";
    private String pass;
    private long walker;
    private String ext;
    private final int headerWalker=32+40+64+40;
    private int channelPerPixel;
    
    public static LSB create(BufferedImage coverImg, File secretImg, String pass, int channelPerPixel){
        return  new LSB(coverImg, secretImg, pass, channelPerPixel);
    }
    
    public static LSB create(BufferedImage img, String pass, int channelPerPixel){
        return  new LSB(img, null, pass, channelPerPixel);
    }
    
    public static LSB create(BufferedImage coverImg, File secretImg, String pass){
        return  new LSB(coverImg, secretImg, pass, 1);
    }
    
    public static LSB create(BufferedImage img, String pass){
        return  new LSB(img, null, pass, 1);
    }
    
    public LSB(BufferedImage coverImg, File secretImg, String pass, int channelPerPixel){
        if(channelPerPixel>3 ||channelPerPixel<1)this.channelPerPixel=1;
        if(secretImg!=null)this.ext=Functions.getFileExt(secretImg);
        this.channelPerPixel=channelPerPixel;
        this.coverImg=coverImg;
        this.secretImg=secretImg;
        this.pass=pass;
    }
    
    public BufferedImage encrypt(){
        if(this.coverImg==null)return null;
        if(this.secretImg==null)return null;
        if(this.pass.equals(""))return null;
        BufferedImage ret=Functions.deepCopy(this.coverImg);
        int w=this.coverImg.getWidth();
        int h=this.coverImg.getHeight();
        int totPixel=w*h;
        int maxSize=totPixel*this.channelPerPixel-this.headerWalker;
        
        
        if(this.secretImg.length()*8>maxSize){
            JOptionPane.showMessageDialog(null,"Lebe Gan. Maks:"+maxSize/8+" Bytes");
            return null;
        }
        this.walker=this.secretImg.length()*8;
        String allBits=this.getKeyBinary()+this.getPassBinary()+this.getWalkerBinary()+this.getExtBinary()+Functions.fileBits(this.secretImg);
        System.out.println("E-walker: "+this.getWalkerBinary()+"    "+this.walker);
        int allBitsWalker=0;
        for(int c=0;c<this.channelPerPixel;c++){
            for(int j=0;j<h;j++){
                for(int i=0;i<w;i++){
                    if(allBitsWalker==allBits.length())break;
                    int pixel=ret.getRGB(i, j);
                    Color color=new Color(pixel,true);
                    int red=color.getRed();
                    int green=color.getGreen();
                    int blue=color.getBlue();
                    String tmpB=Functions.intToBinary(blue);
                    String tmpG=Functions.intToBinary(green);
                    String tmpR=Functions.intToBinary(red);
                    if(c==0){
                        String bBits=Functions.intToBinary(blue);
                        bBits=Functions.insertBit(bBits, allBits.charAt(allBitsWalker++), 7);
                        blue=Functions.binToInt(bBits);
                    }else if(c==1){
                        String gBits=Functions.intToBinary(green);
                        gBits=Functions.insertBit(gBits, allBits.charAt(allBitsWalker++), 7);
                        green=Functions.binToInt(gBits);
                    }else{
                        String rBits=Functions.intToBinary(red);
                        rBits=Functions.insertBit(rBits, allBits.charAt(allBitsWalker++), 7);
                        red=Functions.binToInt(rBits);
                    }
                    String tmpB2=Functions.intToBinary(blue);
                    String tmpG2=Functions.intToBinary(green);
                    String tmpR2=Functions.intToBinary(red);
                    if(i==0 && j==0){
                        System.out.println("AAA: R="+tmpR+"   "+"G="+tmpG+"   "+"B="+tmpB+"   ");
                        System.out.println("BBB: R="+tmpR2+"   "+"G="+tmpG2+"   "+"B="+tmpB2+"   ");
                    }
                    
                    pixel=new Color(red,green,blue).getRGB();
                    //System.out.println(i+","+j);
                    ret.setRGB(i, j, pixel);
                    //System.out.println(c);
                }
            }
        }
        return ret;
    }
    
    public File decrypt(){
        if(this.coverImg==null)return null;
        if(this.pass.equals(""))return null;
        File ret=null;
        int w=this.coverImg.getWidth();
        int h=this.coverImg.getHeight();
        int walkerHeader=0;
        int walkerSecret=0;
        
        String headerBits="";
        String secretBits="";
        boolean headerRead=false;
        for(int c=0;c<this.channelPerPixel;c++){
            for(int j=0;j<h;j++){
                for(int i=0;i<w;i++){
                    int pixel=this.coverImg.getRGB(i, j);
                    Color color=new Color(pixel,true);
                    int red=color.getRed();
                    int green=color.getGreen();
                    int blue=color.getBlue();
                    char bit;
                    if(c==0){
                        String bBits=Functions.intToBinary(blue);
                        bit=bBits.charAt(7);
                    }else if(c==1){
                        String gBits=Functions.intToBinary(green);
                        bit=gBits.charAt(7);
                    }else{
                        String rBits=Functions.intToBinary(red);
                        bit=rBits.charAt(7);
                    }
                    if(walkerHeader==this.headerWalker){
                        if(!headerRead){
                            String keyBits=headerBits.substring(0, 32);
                            String passBits=headerBits.substring(32, 32+40);
                            String walkerBits=headerBits.substring(32+40, 32+40+64);
                            String extBits=headerBits.substring(32+40+64, 32+40+64+40);
                            
                            String myKey=Functions.strFromBits(keyBits);
                            
                            if(!myKey.equals(this.key)){
                                JOptionPane.showMessageDialog(null,"Gambar Tidak Mengandung Enkripsi.   "+myKey);
                                return null;
                            }
                            
                            String myPass=Functions.strFromBits(passBits);
                            if(!myPass.equals(this.pass)){
                                JOptionPane.showMessageDialog(null,"Password Salah");
                                return null;
                            }
                            
                            this.ext=Functions.strFromBits(extBits);
                            
                            this.walker=Long.parseLong(walkerBits, 2);
                            System.out.println("D-ext : "+this.ext);
                            headerRead=true;
                        }
                        if(walkerSecret==this.walker)break;
                        System.out.println(walkerSecret+" - "+this.walker);
                        secretBits+=bit;
                        walkerSecret++;
                    }else{
                        headerBits+=bit;
                        walkerHeader++;
                    }
                }
            }
        }
        ret=Functions.getSecret(secretBits, this.ext);
        return ret;
    }
    
    private String getKeyBinary(){
        String ret="";
        int keys[]={this.key.charAt(0),this.key.charAt(1),this.key.charAt(2),this.key.charAt(3)};
        for(int i=0;i<4;i++){
            ret+=Functions.intToBinary(keys[i]);
        }
        System.out.println(ret);
        return ret;
    }
    private String getPassBinary(){
        String ret="";
        int keys[]=new int[5];
        for(int i=0;i<5;i++){
            if(i>=this.pass.length()){
                keys[i]=0;
            }else{
                keys[i]=this.pass.charAt(i);
            }
        }
        
        for(int i=0;i<5;i++){
            ret+=Functions.intToBinary(keys[i]);
        }
        return ret;
    }
    private String getExtBinary(){
        String ret="";
        int keys[]=new int[5];
        for(int i=0;i<5;i++){
            if(i>=this.ext.length()){
                keys[i]=0;
            }else{
                keys[i]=this.ext.charAt(i);
            }
        }
        
        for(int i=0;i<5;i++){
            ret+=Functions.intToBinary(keys[i]);
        }
        return ret;
    }
    private String getWalkerBinary(){
        String ret="";
        String wBin=Long.toBinaryString(this.walker);
        for(int i=0;i<64-wBin.length();i++){
            ret+="0";
        }
        ret+=wBin;
        return ret;
    }
    
}
