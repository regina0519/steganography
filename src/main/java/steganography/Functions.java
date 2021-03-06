/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steganography;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.filechooser.FileFilter;
import net.sf.image4j.util.ConvertUtil;

/**
 *
 * @author regina
 */
public class Functions {
    public static int channel=3;
    public static char PASSCHAR; 
    
    public static File saveEncryptedImageAs(Component parent, File image){
        if(image==null)return null;
        if(!image.exists())return null;
        File ret=null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                } else {
                    return file.getName().toLowerCase().endsWith(".bmp"); // lets try it
                }
            }

            @Override
            public String getDescription() {
                return "Bitmap (24-Bit)";
            }
        });
        fileChooser.setSelectedFile(new File(image.getName()));
        int option = fileChooser.showSaveDialog(parent);
        if(option == JFileChooser.APPROVE_OPTION){
           ret = fileChooser.getSelectedFile();
           String ext=Functions.getFileExt(ret);
           if(!ext.toUpperCase().equals("BMP")){
               ret=new File(ret.getAbsolutePath()+".bmp");
           }
            try {
                Files.copy(image.toPath(), ret.toPath(),StandardCopyOption.REPLACE_EXISTING);
                //Desktop.getDesktop().open(ret);
                JOptionPane.showMessageDialog(parent,"Image saved");
            } catch (IOException ex) {
                ret=null;
            }
        }
        return ret;
    }
    
    public static File saveSecretImageAs(Component parent, File image){
        if(image==null)return null;
        if(!image.exists())return null;
        String realExt=Functions.getFileExt(image);
        File ret=null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                } else {
                    return file.getName().toLowerCase().endsWith(".png")
                            || file.getName().toLowerCase().endsWith(".jpeg")
                            || file.getName().toLowerCase().endsWith(".jpg")
                            || file.getName().toLowerCase().endsWith(".bmp")
                            || file.getName().toLowerCase().endsWith(".gif"); // lets try it
                }
            }

            @Override
            public String getDescription() {
                return "All image Support!";
            }
        });
        fileChooser.setSelectedFile(new File(image.getName()));
        int option = fileChooser.showSaveDialog(parent);
        if(option == JFileChooser.APPROVE_OPTION){
           ret = fileChooser.getSelectedFile();
           String ext=Functions.getFileExt(ret);
           if(!ext.toUpperCase().equals(realExt.toUpperCase())){
               ret=new File(ret.getAbsolutePath()+"."+realExt);
           }
            try {
                Files.copy(image.toPath(), ret.toPath(),StandardCopyOption.REPLACE_EXISTING);
                //Desktop.getDesktop().open(ret);
                JOptionPane.showMessageDialog(parent,"Extracted Image saved");
            } catch (IOException ex) {
                ret=null;
            }
        }
        return ret;
    }
    
    public static File browseImage(Component parent){
        File ret=null;
        JFileChooser chooser=new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                } else {
                    return file.getName().toLowerCase().endsWith(".png")
                            || file.getName().toLowerCase().endsWith(".jpeg")
                            || file.getName().toLowerCase().endsWith(".jpg")
                            || file.getName().toLowerCase().endsWith(".bmp")
                            || file.getName().toLowerCase().endsWith(".gif"); // lets try it
                }
            }

            @Override
            public String getDescription() {
                return "All image Support!";
            }
        });
        
        int res = chooser.showOpenDialog(parent);
        if (res== JFileChooser.APPROVE_OPTION){
            ret = chooser.getSelectedFile();
        } else {
            //JOptionPane.showMessageDialog(parent,"Cancelled by user!");
        }           
        
        return ret;
    }
    
    public static File browseImageBMP(Component parent){
        File ret=null;
        JFileChooser chooser=new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                } else {
                    return file.getName().toLowerCase().endsWith(".bmp"); // lets try it
                }
            }

            @Override
            public String getDescription() {
                return "Bitmap 24 Bit Image.";
            }
        });
        
        int res = chooser.showOpenDialog(parent);
        if (res== JFileChooser.APPROVE_OPTION){
            ret = chooser.getSelectedFile();
        } else {
            //JOptionPane.showMessageDialog(parent,"Cancelled by user!");
        }           
        
        return ret;
    }
    
    public static String intToBinary(int number){
        String ret="";
        ret=Integer.toBinaryString(number);
        Integer tmp=Integer.valueOf(ret);
        ret=String.format("%08d", tmp);
        return ret;
    }
    
    public static String byteToBinary(byte b){
        String ret="";
        ret=Integer.toBinaryString(b & 0xFF);
        Integer tmp=Integer.valueOf(ret);
        ret=String.format("%08d", tmp);
        return ret;
    }
    
    public static Integer binToInt(String bin){
        Integer ret=0;
        ret=Integer.parseInt(bin, 2);
        return ret;
    }
    public static byte binToByte(String bin){
        byte ret=0;
        ret=(byte)Integer.parseInt(bin, 2);
        /*if(bin.charAt(0)=='1'){
            ret=(byte) 0xFF;
        }else{
            ret=Byte.parseByte(bin, 2);
        }*/
        return ret;
    }
    
    public static String insertBit(String bin,char bit,int pos){
        String ret=bin;
        if(bit=='0' || bit=='1'){
            String tmp="";
            for(int i=0;i<ret.length();i++){
                if(i!=pos){
                    tmp+=ret.charAt(i);
                }else{
                    tmp+=bit;
                }
            }
            ret=tmp;
        }
        return ret;
    }
    
    public static String getImageDimension(BufferedImage img){
        if(img==null)return "";
        return img.getWidth() + " x " + img.getHeight();
    }
    
    public static String getImageDimension(File img){
        if(img==null)return "";
        BufferedImage tmp;
        try {
            tmp = ImageIO.read(img);
            return getImageDimension(tmp);
        } catch (IOException ex) {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    
    public static void displayImage(JLabel lbl, File img){
        if(img==null)return;
        if(lbl==null)return;
        System.out.println(img.getAbsolutePath());
        try {
            BufferedImage tmp=ImageIO.read(img);
            //Image i=tmp.getScaledInstance(lbl.getWidth(), lbl.getHeight(), Image.SCALE_AREA_AVERAGING);
            Dimension d=Functions.scaledImageSize(lbl.getWidth(), lbl.getHeight(), tmp);
            Image i=tmp.getScaledInstance(d.width, d.height, Image.SCALE_SMOOTH);
            lbl.setIcon(new ImageIcon(i));
        } catch (IOException ex) {
            //Logger.getLogger(FormEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void displayImage(JLabel lbl, BufferedImage img){
        if(img==null)return;
        if(lbl==null)return;
        //Image i=img.getScaledInstance(lbl.getWidth(), lbl.getHeight(), Image.SCALE_AREA_AVERAGING);
        Dimension tmp=Functions.scaledImageSize(lbl.getWidth(), lbl.getHeight(), img);
        Image i=img.getScaledInstance(tmp.width, tmp.height, Image.SCALE_SMOOTH);
        lbl.setIcon(new ImageIcon(i));
        
    }
    
    public static char[] fileBits(File file, JProgressBar progress){
        progress.setVisible(true);
        
        try {
            byte[] fBytes=Files.readAllBytes(file.toPath());
            progress.setMaximum(fBytes.length);
            progress.setValue(0);
            List<Character> bits=new ArrayList();
            for(byte tmp:fBytes){
                String t=Functions.byteToBinary(tmp);
                for(int c=0;c<t.length();c++){
                    bits.add(t.charAt(c));
                }
                progress.setValue(progress.getValue()+1);
            }
            progress.setVisible(false);
            char[] r=new char[bits.size()];
            int i=0;
            for(Character c:bits){
                r[i++]=c;
            }
            return r;
            //Functions.tesBuild(bits);
        } catch (IOException ex) {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE, null, ex);
        }
        progress.setVisible(false);
        return null;
    }
    
    public static File getSecret(String bits, String ext){
        if(bits.equals(""))return null;
        File ret=null;
        List<String> bytes=new ArrayList();
        String tmp="";
        for(int i=0;i<bits.length();i++){
            if(i%8==0){
                if(!tmp.equals(""))bytes.add(tmp);
                tmp=""+bits.charAt(i);
            }else{
                tmp+=bits.charAt(i);
            }
        }
        bytes.add(tmp);
        
        byte realBytes[]=new byte[bytes.size()];
        for(int i=0;i<bytes.size();i++){
            realBytes[i]=Functions.binToByte(bytes.get(i));
        }
        String resDir=System.getProperty("user.dir")+"/decrypted/";
        String res=resDir+"result."+ext;
        System.out.println(res);
        if(!Files.exists(Paths.get(resDir))){
            try {
                Files.createDirectories(Paths.get(resDir));
            } catch (IOException ex) {
                Logger.getLogger(Functions.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(Files.exists(Paths.get(resDir))){
            try {
                OutputStream os = new FileOutputStream(new File(res));
                os.write(realBytes);
                os.close();
                ret=new File(res);
            } catch (FileNotFoundException ex) {
                System.out.println(ex.getMessage());
            } catch (IOException ex) {
                Logger.getLogger(Functions.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }
    
    public static File getSecret(List<Character> bits, String ext){
        if(bits.equals(""))return null;
        File ret=null;
        List<String> bytes=new ArrayList();
        String tmp="";
        for(int i=0;i<bits.size();i++){
            if(i%8==0){
                if(!tmp.equals(""))bytes.add(tmp);
                tmp=""+bits.get(i);
            }else{
                tmp+=bits.get(i);
            }
        }
        bytes.add(tmp);
        
        byte realBytes[]=new byte[bytes.size()];
        for(int i=0;i<bytes.size();i++){
            realBytes[i]=Functions.binToByte(bytes.get(i));
        }
        String resDir=System.getProperty("user.dir")+"/decrypted/";
        String res=resDir+"result."+ext;
        System.out.println(res);
        if(!Files.exists(Paths.get(resDir))){
            try {
                Files.createDirectories(Paths.get(resDir));
            } catch (IOException ex) {
                Logger.getLogger(Functions.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(Files.exists(Paths.get(resDir))){
            try {
                OutputStream os = new FileOutputStream(new File(res));
                os.write(realBytes);
                os.close();
                ret=new File(res);
            } catch (FileNotFoundException ex) {
                System.out.println(ex.getMessage());
            } catch (IOException ex) {
                Logger.getLogger(Functions.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }
    
    public static String getFileExt(File file){
        String ret="";
        ret=file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf('.')+1);
        return ret;
    }
    
    public static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
    
    public static String strFromBits(String bits){
        String ret="";
        List<String> lKey=new ArrayList();
        String tmp="";
        for(int i=0;i<bits.length();i++){
            if(i%8==0){
                if(!tmp.equals("")){
                    if(!tmp.equals("00000000"))lKey.add(tmp);
                    tmp=bits.charAt(i)+"";
                }
            }else{
                tmp+=bits.charAt(i);
            }
        }
        if(!tmp.equals("00000000"))lKey.add(tmp);
        int kInt[]=new int[lKey.size()];
        for(int bb=0;bb<lKey.size();bb++){
            kInt[bb]=Functions.binToInt(lKey.get(bb));
        }
        for(int bb=0;bb<kInt.length;bb++){
            kInt[bb]=Functions.binToInt(lKey.get(bb));
            //ret+=Character.toString(kInt[bb]);
            ret+=(char)kInt[bb];
        }
        return ret;
    } 
    
    public static Dimension scaledImageSize(int width, int height, BufferedImage img){
        Dimension ret=new Dimension();
        int w=width;
        int h=(width*img.getHeight())/img.getWidth();
        if(h>height){
            h=height;
            w=(height*img.getWidth())/img.getHeight();
        }
        ret.setSize(w, h);
        return ret;
    }
    
    public static String sizeInMegaBytes(long sizeBytes) {
        double ret=(double) sizeBytes/(1024*1024);
        return new DecimalFormat("#.##").format(ret)+" MB ("+sizeBytes+" bytes)";
    }
    
    public static String fileSizeInMegaBytes(File file) {
        double ret=(double) file.length()/(1024*1024);
        return new DecimalFormat("#.##").format(ret)+" MB ("+fileSizeInBytes(file)+")";
    }
 
    public static String fileSizeInKiloBytes(File file) {
        double ret=(double) file.length()/1024;
        return new DecimalFormat("#.##").format(ret)+"  kB ("+fileSizeInBytes(file)+")";
    }
 
    public static String fileSizeInBytes(File file) {
        return file.length()+" bytes";
    }
    
    public static int getMaxSecretSize(File coverImg){
        BufferedImage tmp;
        try {
            tmp = Functions.deepCopy(ConvertUtil.convert24(ImageIO.read(coverImg)));
            int w=tmp.getWidth();
            int h=tmp.getHeight();
            int totPixel=w*h;
            int maxSize=Math.floorDiv(totPixel*Functions.channel-LSB.headerWalker, 8);
            return maxSize;
            
        } catch (IOException ex) {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    
    
}
