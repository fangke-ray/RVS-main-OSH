
import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
 
 
 public class TrayTest extends JFrame implements ActionListener {
 
     private static final long serialVersionUID = -7078030311369039390L;
     private JMenu menu;
     private JMenuBar jmenuBar;
     private String[] jmItemName = {"置于托盘", "系统退出"};
 
     private TrayTest() throws UnsupportedEncodingException {
         super("电话薄");
         init();
         this.setSize(500, 400);
         this.setJMenuBar(jmenuBar);
         this.setLocationRelativeTo(null);
 
         JButton jbutton = new JButton("test");
         add(jbutton);
 
 
         systemTray(jbutton);    //系统托盘
     }
 
     /**
      * 初始化界面
      */
     private void init() {
         menu = new JMenu("系统窗体");
         for (String s : jmItemName) {
             JMenuItem menuItem = new JMenuItem(s);
             menuItem.addActionListener(this);
             menu.add(menuItem);
         }
         this.jmenuBar = new JMenuBar();
         this.jmenuBar.add(menu);
     }
 
     @Override
     public void actionPerformed(ActionEvent e) {
         String actions = e.getActionCommand();
         if ("置于托盘".equals(actions)) {
             this.setVisible(false);
         }
         if ("系统退出".equals(actions)) {
             System.exit(0);
         }
 
     }
 
     /**
      * 系统托盘图标处理.
      *
      * @param jbutton
      */
     private void systemTray(JButton jbutton) throws UnsupportedEncodingException {
         if (SystemTray.isSupported()) {    //判断系统是否支持托盘功能.
 //            URL resource = this.getClass().getResource("car.jpg");    //获得图片路径
 
             URL resource = getClass().getClassLoader().getResource("back.png");
 //            File targetFile = new File("files/8k.wav");
 
 //            ClassPathResource classPathResource = new ClassPathResource("static/something.txt");
 //
 //            InputStream inputStream = classPathResource.getInputStream();
			final ImageIcon icon = new ImageIcon(resource); //创建图片对象
             final JPopupMenu popupMenu = new JPopupMenu(); //创建弹出菜单对象
             JMenuItem itemExit = new JMenuItem("退出系统");    //创建弹出菜单中的退出项
             JMenuItem itemShow = new JMenuItem("显示窗体"); //创建弹出菜单中的显示主窗体项.
 
 
             popupMenu.add(itemExit);
             popupMenu.add(itemShow);
             final TrayIcon trayIcon = new TrayIcon(icon.getImage(), "电话薄系统");
             final SystemTray sysTray = SystemTray.getSystemTray();
 
             try {
                 sysTray.add(trayIcon);
             } catch (AWTException ignored) {
             }
             trayIcon.addMouseListener(new MouseAdapter() {
                 @Override
                 public void mouseReleased(MouseEvent e) {
                     if (e.isPopupTrigger()) {
                         popupMenu.setLocation(e.getX(), e.getY());
                         popupMenu.setInvoker(popupMenu);
                         popupMenu.setVisible(true);
                     }
                 }
             });
             //给窗体最小化添加事件监听.
             itemShow.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
	                 setVisible(true);
				}
				});
             //给退出像添加事件监听
             itemExit.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
//	                System.exit(0);
	                 sysTray.remove(trayIcon);
	                 dispose();
				
				}
              });

             jbutton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {

	                 TrayIcon trayIcon4 = new TrayIcon(icon.getImage(), "电话薄系统");
	                 try {
	                     sysTray.add(trayIcon4);
	                 } catch (AWTException ex) {
	                     ex.printStackTrace();
	                 }
	             				}
			});
 
         }
     }
 
     /**
      * 主方法
      *
      * @param args sdf
      */
     public static void main(String[] args) throws UnsupportedEncodingException {
         new TrayTest().setVisible(true);
     }
 }