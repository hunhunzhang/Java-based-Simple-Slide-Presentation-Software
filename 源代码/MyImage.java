import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MyImage extends DrawableObject{
    int width=100,height=50;//大小
    File imagefile;
    Color drawColor=Color.black,fillColor=Color.blue;//边框颜色和填充颜色
    private static final  int RESIZE_SIZE=8;
    public MyImage(int x, int y, File image){
        super(x,y);
        this.imagefile=image;
    }
    public MyImage(int x, int y, int width, int height, File image){
        super(x,y);
        this.width=width;
        this.height=height;
        this.imagefile=image;
    }
    @Override
    public void draw(Graphics g){
        Graphics2D g2d=(Graphics2D) g;
        //绘制填充背景
        if (isSelected) {
            g2d.setColor(Color.CYAN); // 被选中的文本框用不同的颜色
            g2d.fillRect(x, y, width, height);
        }
        try {
            //加载图像
            Image image = ImageIO.read(imagefile);
            g2d.drawImage(image, x, y, width, height, null);
        }catch (IOException e){
            e.printStackTrace();
        }
        //绘制边框
        g2d.setColor(drawColor);
        g2d.drawRect(x,y,width,height);
        //绘制调整大小区域
        if (isSelected) {
            g2d.setColor(Color.red);
            g2d.fillOval(x + width - RESIZE_SIZE,y + height - RESIZE_SIZE,RESIZE_SIZE,RESIZE_SIZE);
        }

    }
    @Override
    public boolean contains(Point p){
        return new Rectangle(x,y,width,height).contains(p);
    }
    // 检查鼠标是否在调整大小区域
    boolean isMouseOverResizeArea(int mouseX, int mouseY) {
        return mouseX >= x + width - RESIZE_SIZE && mouseX <= x + width+RESIZE_SIZE&&
                mouseY >= y + height - RESIZE_SIZE&& mouseY <= y + height+RESIZE_SIZE;
    }

    // 调整大小
    void resize(int mouseX, int mouseY) {
        if(mouseX-x>10) width = mouseX - x;
        if(mouseY-y>10) height = mouseY - y;
    }

    // 判断鼠标是否点击在矩形内
    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }
    //右键弹出菜单
    JPopupMenu rightClicked(){
        JPopupMenu jPopupMenu=new JPopupMenu();
        JMenuItem dColor=new JMenuItem("修改边框颜色");
        JMenuItem fColor=new JMenuItem("修改填充颜色");
        dColor.addActionListener(e->changeDrawColor());
        fColor.addActionListener(e->changeFillColor());
        jPopupMenu.add(dColor);
        jPopupMenu.add(fColor);
        return jPopupMenu;
    }
    //修改填充颜色
    void changeDrawColor(){
        Color chosenColor=JColorChooser.showDialog(null,"选择颜色",Color.white);
        if(chosenColor!=null){
            drawColor=chosenColor;
        }
    }//修改边框颜色
    void changeFillColor(){
        Color chosenColor=JColorChooser.showDialog(null,"选择颜色",Color.white);
        if(chosenColor!=null){
            fillColor=chosenColor;
        }
    }
}
