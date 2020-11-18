/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steganography;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

/**
 *
 * @author regina
 */
public class EncryptedImage extends JPanel {
    private File file;
    private JLabel img;
    private JLabel fileName;
    private JLabel fileDate;
    private JLabel filePass;
    private String pass;

    
    public static EncryptedImage create(String file){
        if(file.equals(""))return null;
        File f=new File(file);
        return EncryptedImage.create(f);
    }
    public static EncryptedImage create(File file){
        if(file==null)return null;
        if(!file.exists())return null;
        
        return new EncryptedImage(file,EncryptedImage.getPass(file));
    }
    public EncryptedImage(File file, String pass){
        this.file=file;
        this.pass=pass;
        this.img=new JLabel();
        
        this.fileName=new JLabel(this.file.getName());
        this.fileName.setForeground(Color.WHITE);
        FileTime creationTime;
        try {
            creationTime = (FileTime) Files.getAttribute(Paths.get(this.file.getAbsolutePath()), "creationTime");
            this.fileDate=new JLabel(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(creationTime.toMillis())));
        } catch (IOException ex) {
            this.fileDate=new JLabel();
        }
        this.fileDate.setForeground(Color.WHITE);
        this.filePass=new JLabel("");
        this.filePass.setForeground(Color.WHITE);
        this.showPass(false);
        
        this.img.setBackground(new Color(40,40,40,255));
        this.fileName.setBackground(new Color(0,0,0,0));
        this.fileDate.setBackground(new Color(0,0,0,0));
        this.filePass.setBackground(new Color(0,0,0,0));
        
        JPanel left=new JPanel();
        left.setLayout(new BorderLayout());
        JPanel right=new JPanel();
        right.setLayout(new SpringLayout());
        this.img.setSize(100, 100);
        Functions.displayImage(this.img, this.file);
        left.add(this.img,BorderLayout.CENTER);
        
        JLabel lblName=new JLabel("File : ",JLabel.TRAILING);
        lblName.setForeground(Color.WHITE);
        JLabel lblDate=new JLabel("Date : ",JLabel.TRAILING);
        lblDate.setForeground(Color.WHITE);
        JCheckBox cbPass=new JCheckBox("Key : ");
        cbPass.setFocusable(false);
        cbPass.setForeground(Color.WHITE);
        cbPass.setBackground(new Color(2,2,2,0));
        cbPass.setHorizontalAlignment(JCheckBox.TRAILING);
        cbPass.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent event) {
                JCheckBox cb = (JCheckBox) event.getSource();
                EncryptedImage.this.showPass(cb.isSelected());
            }
        });
        
        
        right.add(lblName);
        right.add(this.fileName);
        right.add(lblDate);
        right.add(this.fileDate);
        right.add(cbPass);
        right.add(this.filePass);
        SpringUtilities.makeCompactGrid(right,
                                3, 2, //rows, cols
                                6, 6,        //initX, initY
                                6, 6);       //xPad, yPad
        
        JPanel content=new JPanel();
        //content.setLayout(new GridLayout(1,2));
        content.add(left);
        content.add(right);
        this.add(content);
        
        left.setBackground(new Color(10,10,10,255));
        right.setBackground(new Color(10,10,10,255));
        content.setBackground(new Color(10,10,10,255));
        this.setBackground(new Color(2,2,2,100));
        
        this.img.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent me) {
                if(SwingUtilities.isRightMouseButton(me)){
                    EncryptedImage.this.popupMenu().show(me.getComponent(), me.getX(), me.getY());
                }
             }
        });
    }
    private JPopupMenu popupMenu(){
        JPopupMenu popup = new JPopupMenu();
        JMenuItem save=new JMenuItem("Save As");
        popup.add(save);
        save.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent me) {
                if(!SwingUtilities.isRightMouseButton(me)){
                    Functions.saveEncryptedImageAs(EncryptedImage.this.getParent(), EncryptedImage.this.file);
                }
             }
        });
        return popup;
    }
    private void showPass(boolean show){
        if(show){
            this.filePass.setText(this.pass);
        }else{
            char c=new JPasswordField().getEchoChar();
            String p="";
            for(int i=0;i<this.pass.length();i++){
                p+=c;
            }
            this.filePass.setText(p);
        }
    }
    private static String getPass(File file){
        if(file==null)return "";
        BufferedImage image=EncryptedImage.getBufferedImage(file);
        if(image==null)return "";
        int w=image.getWidth();
        int h=image.getHeight();
        int walkerHeader=0;
        
        String headerBits="";
        List<Character> secretBits=new ArrayList();
        boolean headerRead=false;
        
        for(int c=0;c<Functions.channel;c++){
            for(int j=0;j<h;j++){
                for(int i=0;i<w;i++){
                    int pixel=image.getRGB(i, j);
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
                    if(walkerHeader==LSB.headerWalker){
                        if(!headerRead){
                            String keyBits=headerBits.substring(0, 32);
                            String passBits=headerBits.substring(32, 32+40);
                            
                            String myKey=Functions.strFromBits(keyBits);
                            
                            if(!myKey.equals(LSB.key)){
                                return "";
                            }
                            
                            String myPass=Functions.strFromBits(passBits);
                            return myPass;
                        }
                        return "";
                    }else{
                        headerBits+=bit;
                        walkerHeader++;
                        if(walkerHeader>LSB.headerWalker)return "";
                    }
                }
            }
        }
        return "";
    }
    
    public File getFile(){
        return this.file;
    }
    private static BufferedImage getBufferedImage(File file){
        if(file==null)return null;
        try {
            return ImageIO.read(file);
        } catch (IOException ex) {
            return null;
        }
    }
}
