package dv360updater;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * @version 1.0.00
 * @author Arturo Martínez <arturo.martinez@gdsmodellica.com>
 */
public class DV360Logo extends JPanel{
    
    private Image _img;
    private Dimension _size;
    
    public DV360Logo(String src){
        this(new ImageIcon(src).getImage());
    }
    
    public DV360Logo(Image img){
        this._img = img;
        this._size = new Dimension(img.getWidth(null), img.getHeight(null));
        setPreferredSize(this._size);
        setMaximumSize(this._size);
        setMinimumSize(this._size);
        setSize(this._size);
        setLayout(null);
    }
    
    public DV360Logo(Image img, Dimension size){
        this._img = img;
        this._size = size;
        setPreferredSize(this._size);
        setMaximumSize(this._size);
        setMinimumSize(this._size);
        setSize(this._size);
        setLayout(null);
    }
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(_img, 0, 0, this);
    }
}
