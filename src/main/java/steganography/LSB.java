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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

/**
 *
 * @author regina
 */
public class LSB {
    private BufferedImage coverImg;
    private File secretImg;
    public static final String key="ABAL";
    private String pass;
    private long walker;
    private String ext;
    public static final int headerWalker=32+40+64+40;
    private int channelPerPixel;
    private FormEncrypt frmEnc;
    private FormDecrypt frmDec;
    
    public static LSB create(BufferedImage coverImg, File secretImg, String pass, int channelPerPixel,FormEncrypt frmEncrypt,FormDecrypt frmDecrypt){
        return  new LSB(coverImg, secretImg, pass, channelPerPixel,frmEncrypt,frmDecrypt);
    }
    
    public static LSB create(BufferedImage img, String pass, int channelPerPixel,FormEncrypt frmEncrypt,FormDecrypt frmDecrypt){
        return  new LSB(img, null, pass, channelPerPixel,frmEncrypt,frmDecrypt);
    }
    
    public static LSB create(BufferedImage coverImg, File secretImg, String pass,FormEncrypt frmEncrypt,FormDecrypt frmDecrypt){
        return  new LSB(coverImg, secretImg, pass, 1,frmEncrypt,frmDecrypt);
    }
    
    public static LSB create(BufferedImage img, String pass,FormEncrypt frmEncrypt,FormDecrypt frmDecrypt){
        return  new LSB(img, null, pass, 1,frmEncrypt,frmDecrypt);
    }
    
    public LSB(BufferedImage coverImg, File secretImg, String pass, int channelPerPixel,FormEncrypt frmEncrypt,FormDecrypt frmDecrypt){
        if(channelPerPixel>3 ||channelPerPixel<1)this.channelPerPixel=1;
        if(secretImg!=null)this.ext=Functions.getFileExt(secretImg);
        this.channelPerPixel=channelPerPixel;
        this.coverImg=coverImg;
        this.secretImg=secretImg;
        this.pass=pass;
        this.frmEnc=frmEncrypt;
        this.frmDec=frmDecrypt;
    }
    
    public void setProgress(long value){
        
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
            this.frmEnc.setLocked(false);
            return null;
        }
        this.walker=this.secretImg.length()*8;
        this.frmEnc.getMsgLabel().setText("Reading secret image...");
        List<Character> allBits=this.join(this.getKeyBinary(),this.getPassBinary(),this.getWalkerBinary(),this.getExtBinary(),Functions.fileBits(this.secretImg,this.frmEnc.getProgress()));
        //String allBits=this.getKeyBinary()+this.getPassBinary()+this.getWalkerBinary()+this.getExtBinary()+Functions.fileBits(this.secretImg,this.frmEnc.getProgress());
        
        System.out.println("E-walker: "+this.getWalkerBinary()+"    "+this.walker);
        int allBitsWalker=0;
        
        this.frmEnc.getProgress().setMaximum(allBits.size());
        this.frmEnc.getProgress().setValue(0);
        this.frmEnc.getProgress().setVisible(true);
        this.frmEnc.getMsgLabel().setText("Encrypting...");
        for(int c=0;c<this.channelPerPixel;c++){
            for(int j=0;j<h;j++){
                for(int i=0;i<w;i++){
                    if(allBitsWalker==allBits.size())break;
                    int pixel=ret.getRGB(i, j);
                    Color color=new Color(pixel,true);
                    int red=color.getRed();
                    int green=color.getGreen();
                    int blue=color.getBlue();
                    if(c==0){
                        String bBits=Functions.intToBinary(blue);
                        bBits=Functions.insertBit(bBits, allBits.get(allBitsWalker++), 7);
                        blue=Functions.binToInt(bBits);
                    }else if(c==1){
                        String gBits=Functions.intToBinary(green);
                        gBits=Functions.insertBit(gBits, allBits.get(allBitsWalker++), 7);
                        green=Functions.binToInt(gBits);
                    }else{
                        String rBits=Functions.intToBinary(red);
                        rBits=Functions.insertBit(rBits, allBits.get(allBitsWalker++), 7);
                        red=Functions.binToInt(rBits);
                    }
                    
                    pixel=new Color(red,green,blue).getRGB();
                    //System.out.println(i+","+j);
                    ret.setRGB(i, j, pixel);
                    this.frmEnc.getProgress().setValue(this.frmEnc.getProgress().getValue()+1);
                    this.frmEnc.getProgress().repaint();
                    //System.out.println(c);
                }
            }
        }
        this.frmEnc.getProgress().setVisible(false);
        this.frmEnc.getMsgLabel().setText(" ");
        this.frmEnc.setLocked(false);
        return ret;
    }
    
    public File decrypt(){
        if(this.coverImg==null)return null;
        if(this.pass.equals(""))return null;
        File ret=null;
        int w=this.coverImg.getWidth();
        int h=this.coverImg.getHeight();
        int walkerHeader=0;
        long walkerSecret=0;
        
        String headerBits="";
        List<Character> secretBits=new ArrayList();
        boolean headerRead=false;
        this.frmDec.getMsgLabel().setText("Decrypting...");
        this.frmDec.getProgress().setValue(0);
        
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
                                JOptionPane.showMessageDialog(null,"Image not encrypted");
                                this.frmDec.setLocked(false);
                                return null;
                            }
                            
                            String myPass=Functions.strFromBits(passBits);
                            if(!myPass.equals(this.pass)){
                                JOptionPane.showMessageDialog(null,"Sorry, Wrong key");
                                this.frmDec.setLocked(false);
                                return null;
                            }
                            
                            this.ext=Functions.strFromBits(extBits);
                            
                            this.walker=Long.parseLong(walkerBits, 2);
                            //System.out.println("D-ext : "+this.ext);
                            headerRead=true;
                            this.frmDec.getProgress().setMaximum((int)this.walker);
                            this.frmDec.getProgress().setVisible(true);
                        }
                        if(walkerSecret==this.walker)break;
                        //System.out.println(walkerSecret+" - "+this.walker);
                        secretBits.add(bit);
                        walkerSecret++;
                        this.frmDec.getProgress().setValue(this.frmDec.getProgress().getValue()+1);
                    }else{
                        headerBits+=bit;
                        walkerHeader++;
                    }
                }
            }
        }
        ret=Functions.getSecret(secretBits, this.ext);
        //ret=null;
        this.frmDec.getProgress().setVisible(false);
        this.frmDec.getMsgLabel().setText(" ");
        this.frmDec.setLocked(false);
        return ret;
    }
    
    private List<Character> getKeyBinary(){
        List<Character> ret=new ArrayList();
        int keys[]={this.key.charAt(0),this.key.charAt(1),this.key.charAt(2),this.key.charAt(3)};
        for(int i=0;i<4;i++){
            String tmp=Functions.intToBinary(keys[i]);
            for(int c=0;c<tmp.length();c++){
                ret.add(tmp.charAt(c));
            }
        }
        return ret;
    }
    private List<Character> getPassBinary(){
        List<Character> ret=new ArrayList();
        int keys[]=new int[5];
        for(int i=0;i<5;i++){
            if(i>=this.pass.length()){
                keys[i]=0;
            }else{
                keys[i]=this.pass.charAt(i);
            }
        }
        
        for(int i=0;i<5;i++){
            String tmp=Functions.intToBinary(keys[i]);
            for(int c=0;c<tmp.length();c++){
                ret.add(tmp.charAt(c));
            }
        }
        return ret;
    }
    private List<Character> getExtBinary(){
        List<Character> ret=new ArrayList();
        int keys[]=new int[5];
        for(int i=0;i<5;i++){
            if(i>=this.ext.length()){
                keys[i]=0;
            }else{
                keys[i]=this.ext.charAt(i);
            }
        }
        
        for(int i=0;i<5;i++){
            String tmp=Functions.intToBinary(keys[i]);
            for(int c=0;c<tmp.length();c++){
                ret.add(tmp.charAt(c));
            }
        }
        return ret;
    }
    private List<Character> getWalkerBinary(){
        List<Character> ret=new ArrayList();
        String wBin=Long.toBinaryString(this.walker);
        for(int i=0;i<64-wBin.length();i++){
            ret.add('0');
        }
        for(int i=0;i<wBin.length();i++){
            ret.add(wBin.charAt(i));
        }
        
        return ret;
    }
    
    private List<Character> join(List<Character>... list){
        List<Character> ret=new ArrayList();
        for(List<Character> l:list){
            for(Character c:l){
                ret.add(c);
            }
        }
        return ret;
    }
    
}
