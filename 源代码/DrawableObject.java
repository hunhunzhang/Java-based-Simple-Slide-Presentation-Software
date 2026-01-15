import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

abstract class DrawableObject implements Serializable {
    int x,y;
    boolean isSelected=false; //是否被选中//是否调整大小
    public DrawableObject(int x,int y){
        this.x=x;
        this.y=y;
    }
    public abstract void draw(Graphics g);
    public boolean contains(Point p){
        return new Rectangle(x,y,100,50).contains(p);   //默认矩形范围
    }

    public void setLocation(Point p){
        this.x=p.x;
        this.y=p.y;
    }
    void resize(int x,int y){}
    boolean isMouseOver(int MouseX,int MouseY){return false;}
    boolean isMouseOverResizeArea(int mouseX, int mouseY){return false;}
    void doubleClicked(){}
    JPopupMenu rightClicked(){
        return new JPopupMenu();
    }
}
