
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.Image;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class SimpleSlideMaker extends JFrame {
    private JPanel slidePanel;  //幻灯片面板
    private JMenuBar menuBar;   //菜单栏
    private JFileChooser fileChooser;//文件选择器
    private List<DrawableObject> drawableObjects;   //存储当前幻灯片元素
    private boolean isPlaying = false;  // 是否处于播放模式
    private int currentSlideIndex = 0; // 当前幻灯片索引
    private List<Slide> slides; // 存储所有幻灯片
    private JPopupMenu jPopupMenu;   //右键弹出菜单
    private JScrollPane slideScrollPane;    //幻灯片列表滚动
    private JSplitPane slideSplitPane;     //分割幻灯片列表和面板
    private DefaultListModel<String> slideTitles;
    private JList<String> slideList;    //幻灯片列表
    private File slideFile=null; //幻灯片文件

    private DrawableObject selectedItem=null;  // 当前选中的元素
    private boolean isDragging = false;  // 标记是否正在拖动
    private boolean isResizing = false;  // 标记是否正在调整大小
    private int offsetX, offsetY;  // 记录鼠标相对文本框的位置

    private ListDataListener slideTitleslistener=new ListDataListener() {//动态更新列表与数组中存储的内容
        @Override
        public void intervalAdded(ListDataEvent e) {
            if(e.getIndex1()<slides.size()&&e.getIndex1()>=0) {
                newSlide(e.getIndex1() + 1);
                currentSlideIndex=e.getIndex1();
                slideTitlesShow();
            }else{
                newSlide();
                currentSlideIndex=slideTitles.size()-1;
                slideTitlesShow();
            }
            slideList.setSelectedIndex(currentSlideIndex);
            repaint();
        }
        //当删除列表元素时同时消除数组中的内容
        @Override
        public void intervalRemoved(ListDataEvent e) {
            if(!slides.isEmpty()) {
                slides.remove(e.getIndex0());
            }
            slideTitlesShow();
            if(e.getIndex0()>0)currentSlideIndex=e.getIndex0()-1;else currentSlideIndex=0;
            slideList.setSelectedIndex(currentSlideIndex);
            repaint();
        }
        @Override
        public void contentsChanged(ListDataEvent e) {

        }
        public void slideTitlesShow(){
            for(int i=0;i<slides.size();i++){
                slideTitles.setElementAt("幻灯片%d".formatted(i+1),i);
            }
        }
    };// slideTitles 专用事件监听用于控制开关,避免影响文件打开和新建
    public SimpleSlideMaker(){
        //初始化总窗体
        setTitle("简易ppt");
        Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width/2,screenSize.height/2);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        slides=new ArrayList<>();
        drawableObjects=new ArrayList<>();
        slidePanel=new JPanel();

        initMenu();
        initSlideScrollPane();
        initSlidePanel();
    }
    //初始化菜单栏
    private void initMenu(){
        menuBar= new JMenuBar();
        //文件菜单
        JMenu fileMenu=new JMenu("文件");
        JMenuItem newItem=new JMenuItem("新建");
        JMenuItem openItem=new JMenuItem("打开");
        JMenuItem saveItem=new JMenuItem("保存");
        //添加事件监听
        newItem.addActionListener(e->newSlideFile());
        openItem.addActionListener(e -> openSlideFile());
        saveItem.addActionListener(e -> saveSlideFile());
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);

        //幻灯片播放
        JMenu playMenu= new JMenu("幻灯片放映");
        JMenuItem playItem1=new JMenuItem("从当前幻灯片开始放映");
        JMenuItem playItem2=new JMenuItem("从头开始放映");
        playItem1.addActionListener(e->playSlideCur());
        playItem2.addActionListener(e->playSlideBegin());
        playMenu.add(playItem1);
        playMenu.add(playItem2);
        //插入
        JMenu addMenu=new JMenu("插入");
        JMenuItem textItem=new JMenuItem("文本框");
        JMenuItem rectionItem=new JMenuItem("矩形");
        JMenuItem circleItem=new JMenuItem("圆形");
        JMenuItem imageItem=new JMenuItem("图片");
        textItem.addActionListener(e->addTextBox());
        rectionItem.addActionListener(e->drawRectangle());
        circleItem.addActionListener(e->drawCircle());
        imageItem.addActionListener(e->addImage());
        addMenu.add(textItem);
        addMenu.add(rectionItem);
        addMenu.add(circleItem);
        addMenu.add(imageItem);
        //加入菜单栏
        menuBar.add(fileMenu);
        menuBar.add(playMenu);
        menuBar.add(addMenu);
        setJMenuBar(menuBar);
        //弹出菜单
        jPopupMenu=new JPopupMenu();
    }

    //初始化幻灯片列表
    private void initSlideScrollPane(){
        slideTitles=new DefaultListModel<>();  //存储列表中元素
        if(slides.isEmpty()){//初始化slideTitles
           newSlide();
           slideTitles.addElement("幻灯片1");    //当没有数据时新建一页幻灯片
        }
        else {
            for (int i = 0; i < slides.size(); i++) {
                slideTitles.addElement("幻灯片%d".formatted(i + 1));
            }
        }
        slideList=new JList<>(slideTitles);
        slideList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);    //设置单选
        slideList.setSelectedIndex(0);  // 默认选中第一个幻灯片
        //监听slideList选择来切换幻灯片
        slideList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {    //选择列表时调用方法
                if(!e.getValueIsAdjusting()){
                    // 获取选中的列表项索引
                    int index = slideList.getSelectedIndex();
                    if (index != -1) {
                        currentSlideIndex=index;
                        repaint();
                    }
                }
            }
        });
        //监听鼠标事件
        slideList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){ //鼠标右键弹出菜单
                if(e.getButton()==MouseEvent.BUTTON3){
                    int index=slideList.locationToIndex(e.getPoint());  // 获取点击位置对应的索引
                    jPopupMenu=new JPopupMenu();
                    if(index>=0) {
                        JMenuItem reSlide = new JMenuItem("删除幻灯片");
                        JMenuItem inSlide = new JMenuItem("在此处插入新幻灯片");
                        JMenuItem reAllSlide=new JMenuItem("删除所有幻灯片");
                        JMenuItem inLastSlide=new JMenuItem("在末尾新建幻灯片");
                        reSlide.addActionListener(e1 -> {
                            slideTitles.remove(index);
                        });
                        inSlide.addActionListener(e1 -> {
                            slideTitles.add(index,"幻灯片");
                        });
                        reAllSlide.addActionListener(e1->{
                            while(!slideTitles.isEmpty()){
                                slideTitles.remove(slideTitles.size()-1);
                            }

                        });
                        inLastSlide.addActionListener(e1->{
                            slideTitles.add(slideTitles.size()-1,"幻灯片%d".formatted(slides.size()+1));
                        });
                        jPopupMenu.add(reSlide);
                        jPopupMenu.add(inSlide);
                        jPopupMenu.add(reAllSlide);
                        jPopupMenu.add(inLastSlide);
                        jPopupMenu.show(slideList,e.getX(),e.getY());
                    }
                    else{
                        jPopupMenu=new JPopupMenu();
                        JMenuItem inLastSlide=new JMenuItem("新建幻灯片");
                        inLastSlide.addActionListener(e1->{
                            slideTitles.add(slideTitles.size(),"幻灯片%d".formatted(slides.size()+1));
                        });
                        jPopupMenu.add(inLastSlide);
                        jPopupMenu.show(slideList,e.getX(),e.getY());
                    }
                }
            }
        });
        //对列表数据变动进行监听

        slideTitles.addListDataListener(slideTitleslistener);
        // 将目录列表放入滚动列表置于左侧
        slideScrollPane=new JScrollPane(slideList);
        slideScrollPane.setPreferredSize(new Dimension(getWidth()/4,getHeight()));

    }

    //初始化幻灯片面板
    private void initSlidePanel(){
        slidePanel =new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                if(!slides.isEmpty()&&currentSlideIndex<slides.size()&&currentSlideIndex>=0&&!slides.get(currentSlideIndex).getObjects().isEmpty()) {
                    drawableObjects = slides.get(currentSlideIndex).getObjects();
                    for (DrawableObject obj : drawableObjects) {
                        obj.draw(g);
                    }
                }
            }
        };

        slidePanel.setBackground(Color.WHITE);
        slidePanel.setLayout(null);
        slidePanel.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                handleMousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e){
                handleMouseReleased(e);
            }
            //鼠标双击
            @Override
            public void mouseClicked(MouseEvent e){
                handleMouseClicked(e);
            }
        });
        slidePanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e){
                handleMouseDragged(e);
            }
        });
        slidePanel.setPreferredSize(new Dimension(getWidth()*3/4,getHeight()));
        slideSplitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,slideScrollPane,slidePanel);
        slideSplitPane.setDividerLocation(getWidth()/4);
        slideSplitPane.setDividerSize(5);
        add(slideSplitPane);
    }
    //插入图片
    private void addImage(){
        //创建文件选择器
        fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("选择符合格式的图片文件");
        //创建文件过滤器，只允许选择图片
        javax.swing.filechooser.FileFilter imageFilter=new FileFilter() {
            @Override
            public boolean accept(File f) {
                if(f.isDirectory()){
                    return true;
                }
                String fileName=f.getName().toLowerCase();
                //基本图片格式后缀
                return fileName.endsWith(".jpg")||fileName.endsWith(".jpeg")||
                        fileName.endsWith(".png")||fileName.endsWith(".gif");
            }
            public String getDescription(){
                return "Image Files (*.jpg, *.jpeg, *.png, *.gif)";
            }
        };
        fileChooser.setFileFilter(imageFilter);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if(file.exists()){
                try {
                    // 加载图像
                    MyImage newImage=new MyImage(5,5,file);
                    drawableObjects.add(newImage);
                    slidePanel.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //插入文本框
    private void addTextBox(){
        TextBox textBox = new TextBox("Text", 100, 100);
        drawableObjects.add(textBox);
//        slides.get(currentSlideIndex).addObject(textBox);
        slidePanel.repaint();
    }
    //插入矩形;
    private void drawRectangle(){
        RectangleShape rectangleShape=new RectangleShape(100,100);
        drawableObjects.add(rectangleShape);
//        slides.get(currentSlideIndex).addObject(rectangleShape);
        slidePanel.repaint();
    }
    //插入圆
    private void drawCircle(){
        CircleShape circleShape=new CircleShape(100,100);
        drawableObjects.add(circleShape);
//        slides.get(currentSlideIndex).addObject(circleShape);
        slidePanel.repaint();
    }
    //幻灯片播放
    private void playSlideCur(){
        JOptionPane.showMessageDialog(this, "功能尚未实现，按钮仅为装饰");
    }
    private void playSlideBegin(){
        JOptionPane.showMessageDialog(this, "功能尚未实现，按钮仅为装饰");
    }
    //处理鼠标点击事件
    private void handleMousePressed(MouseEvent e){
        if(e.getButton()==MouseEvent.BUTTON1) {
            if (selectedItem != null) {
                selectedItem.isSelected = false;
            }
            for (DrawableObject box : drawableObjects) {
                if (box.isMouseOver(e.getX(), e.getY())) {
                    selectedItem = box;
                    if (box.isMouseOverResizeArea(e.getX(), e.getY())) {
                        isResizing = true;  // 如果点击的是调整大小区域，开始调整大小
                    } else {
                        isDragging = true;  // 否则开始拖动
                        offsetX = e.getX() - box.x;
                        offsetY = e.getY() - box.y;
                    }
                    box.isSelected = true;  // 选中当前点击的文本框
                    repaint();
                    break;
                }
                else{
                    box.isSelected=false;
                    repaint();
                }
            }

        }
    }
    //处理鼠标释放事件
    private void handleMouseReleased(MouseEvent e){
        isDragging = false;
        isResizing = false;
    }
    //处理鼠标拖动事件
    private void handleMouseDragged(MouseEvent e){
        if (isDragging && selectedItem != null) {
            // 拖动
            selectedItem.x = e.getX() - offsetX;
            selectedItem.y = e.getY() - offsetY;
            repaint();
        } else if (isResizing && selectedItem != null) {
            // 调整大小
            selectedItem.resize(e.getX(), e.getY());
            repaint();
        }
    }
    // 鼠标双击及右键单击事件
    private void handleMouseClicked(MouseEvent e){
       if (e.getClickCount() == 2 && selectedItem != null&&selectedItem.isSelected) {
           selectedItem.doubleClicked();
           repaint();
       }
       else if(e.getButton()==MouseEvent.BUTTON3){
           if(selectedItem!=null&&selectedItem.isSelected){
               jPopupMenu=selectedItem.rightClicked();
               JMenuItem del=new JMenuItem("删除");
               del.addActionListener(e1->deleteItem());
               jPopupMenu.add(del);
           }
           else{
               jPopupMenu=new JPopupMenu();
           }
           jPopupMenu.show(e.getComponent(),e.getX(),e.getY());
           repaint();
       }
   }
   //删除元素
    void deleteItem(){
        if(selectedItem!=null){
            slides.get(currentSlideIndex).removeObject(selectedItem);
            selectedItem=null;
            repaint();
        }
    }
    //删除单页幻灯片
    void deleteSlide(int index){
        slides.remove(index);
    }
    //新建单页幻灯片
    private void newSlide(){//默认添加在末尾
        Slide nSlide=new Slide();
        nSlide.addObject(new TextBox("欢迎",getWidth()*3/9,getHeight()*3/8));
        slides.add(nSlide);
    }
    private void newSlide(int index){   //在选择位置插入
        Slide nSlide=new Slide();
        nSlide.addObject(new TextBox("欢迎",getWidth()*3/9,getHeight()*3/8));
        slides.add(index,nSlide);
    }
    //打开幻灯片文件
    private void openSlideFile(){
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                slideTitles.removeListDataListener(slideTitleslistener);
                slides = (List<Slide>) in.readObject();
                slideFile=file;
                slideTitles.clear();
                if(slides.isEmpty()){//初始化slideTitles
                    newSlide();
                    slideTitles.addElement("幻灯片1");    //当没有数据时新建一页幻灯片
                }
                else {
                    for (int i = 0; i < slides.size(); i++) {
                        slideTitles.addElement("幻灯片%d".formatted(i + 1));
                    }
                }
                JOptionPane.showMessageDialog(this, "幻灯片加载成功");
                slideTitles.addListDataListener(slideTitleslistener);
                repaint();
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "打开失败： " + ex.getMessage());
            }
        }
    }
    //新建幻灯片文件
    private void newSlideFile(){
        fileChooser=new JFileChooser();//用于选择文件位置和名称
        fileChooser.setDialogTitle("选择文件位置并填写文件名");
        int result = fileChooser.showSaveDialog(this);
        while(result== JFileChooser.APPROVE_OPTION){//判断是否选择了文件
            File file = fileChooser.getSelectedFile();
            if(file.exists()){
                JOptionPane.showMessageDialog(this, "文件名已存在!为避免覆盖请修改文件名");
                result=fileChooser.showSaveDialog(this);
            }else {
                try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                    slideTitles.removeListDataListener(slideTitleslistener);
                    slideFile = file;
                    slides = new ArrayList<>();
                    out.writeObject(slides);
                    slideTitles.clear();
                    if (slides.isEmpty()) {//初始化slideTitles
                        newSlide();
                        slideTitles.addElement("幻灯片1");    //当没有数据时新建一页幻灯片
                    } else {
                        for (int i = 0; i < slides.size(); i++) {
                            slideTitles.addElement("幻灯片%d".formatted(i + 1));
                        }
                    }
                    JOptionPane.showMessageDialog(this, "新建成功");
                    slideTitles.addListDataListener(slideTitleslistener);
                    repaint();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "新建失败" + ex.getMessage());
                }
                break;
            }
        }
    }
    //保存幻灯片文件
    private void saveSlideFile(){
        if(slideFile!=null) {//已打开文件直接保存
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(slideFile))) {
                out.writeObject(slides);
                JOptionPane.showMessageDialog(this, "保存成功");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "保存失败" + ex.getMessage());
            }
        }
        else
        {//未打开文件则先新建文件再保存
            fileChooser=new JFileChooser();//用于选择文件位置和名称
            fileChooser.setDialogTitle("选择文件位置");
            int result = fileChooser.showSaveDialog(this);
            if(result== JFileChooser.APPROVE_OPTION){//判断是否选择了文件
                File file = fileChooser.getSelectedFile();
                try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                    out.writeObject(slides);
                    JOptionPane.showMessageDialog(this, "保存成功");
                    slideFile=file;
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "保存失败" + ex.getMessage());
                }
            }
        }
    }

}
