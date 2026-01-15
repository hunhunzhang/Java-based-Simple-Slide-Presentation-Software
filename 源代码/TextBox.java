import javax.swing.*;
import java.awt.*;

public class TextBox extends DrawableObject{
    String text="NewText";//文本内容
    int stringsize=30;
    String fontType="Serif";
    Font font=new Font(fontType, Font.PLAIN, stringsize);
    Color color =Color.black,drawColor=Color.white,fillColor=Color.white;
    int width=100,height=50;

    private static final  int RESIZE_SIZE=8;
    public TextBox(String text,int x,int y){
        super(x,y);
        this.text=text;
    }
    @Override
    public void draw(Graphics g){
        Graphics2D g2d=(Graphics2D) g;
        //初始化
        g2d.setFont(font);
        g2d.setColor(color);
        FontMetrics metrics=g2d.getFontMetrics();
        width=metrics.stringWidth(text);
        height=metrics.getHeight();

        //绘制文本背景
        if (isSelected) {
            g2d.setColor(Color.CYAN); // 被选中的文本框用不同的颜色
        } else {
            g2d.setColor(fillColor);
        }
        g2d.fillRect(x,y,width,height);
        //绘制边框
        g2d.setColor(drawColor);
        g2d.drawRect(x,y,width,height);
        //绘制调整大小区域
        if(isSelected){
            g2d.setColor(Color.red);
            g2d.fillOval(x + width - RESIZE_SIZE,y+height - RESIZE_SIZE,RESIZE_SIZE,RESIZE_SIZE);
        }
        //绘制文本
        g2d.setColor(color);
        g2d.drawString(text,x,y+height*2/3);

    }
    @Override
    public boolean contains(Point p){
        return new Rectangle(x,y,text.length()*10,20).contains(p);
    }
    // 检查鼠标是否在调整大小区域
    @Override
    public boolean isMouseOverResizeArea(int mouseX, int mouseY) {
        return mouseX >= x + width - RESIZE_SIZE && mouseX <= x + width&&
                mouseY >= y + height - RESIZE_SIZE && mouseY <= y + height;
    }

    // 调整文本大小
    void resize(int mouseX, int mouseY) {
        int nwidth,nheight;
        if(mouseX-x>10&&mouseY-y>10) {
            nwidth = mouseX - x;
            nheight = mouseY - y;
            float rate;
            if(nwidth>=nheight){
                rate=(float)nwidth/width;
            }else{
                rate=(float)nheight/height;
            }
            stringsize*=rate;
            font=new Font(font.getFontName(), font.getStyle(), stringsize);
        }
    }

    // 判断鼠标是否点击在文本内
    @Override
    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }
    //双击时，改变文本
    void doubleClicked(){

        String newText = JOptionPane.showInputDialog(null,"文本内容:", text);
        if (newText != null) {
            text = newText;
        }
    }
    JPopupMenu rightClicked(){
        JPopupMenu jPopupMenu=new JPopupMenu();
        JMenuItem dColor=new JMenuItem("修改边框颜色");
        JMenuItem fColor=new JMenuItem("修改填充颜色");
        JMenuItem cColor=new JMenuItem("修改字体颜色");
        JMenuItem cFont=new JMenuItem("修改字体");
        JMenuItem cFontStyle=new JMenuItem("修改字体样式");

        dColor.addActionListener(e->changeDrawColor());
        fColor.addActionListener(e->changeFillColor());
        cColor.addActionListener(e->changeFontColor());
        cFont.addActionListener(e->changeFont());
        cFontStyle.addActionListener(e->changeFontStyle());

        jPopupMenu.add(cColor);
        jPopupMenu.add(dColor);
        jPopupMenu.add(fColor);
        jPopupMenu.add(cFont);
        jPopupMenu.add(cFontStyle);
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
    //修改字体颜色
    void changeFontColor(){
        Color chosenColor=JColorChooser.showDialog(null,"选择颜色",Color.white);
        if(chosenColor!=null){
            color=chosenColor;
        }
    }
    //修改字体
    void changeFont(){
        //获取可用字体列表
        String[] options=new String[]{"Serif","Monospaced","Handwritten","Display"};

        int choice=JOptionPane.showOptionDialog(
                null,
                "选择字体",
                "字体",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                null
        );
        if(choice>=0&&choice<options.length) font=new Font(options[choice], Font.PLAIN, stringsize);
    }
    void changeFontStyle(){
        String[] options=new String[]{"Plain", "Bold", "Italic", "Bold & Italic"};
        int choice=JOptionPane.showOptionDialog(
                null,
                "选择字体样式",
                "字体样式",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                null
        );
        if(choice>=0&&choice<options.length) font=new Font(font.getFontName(),choice, stringsize);

    }
}
