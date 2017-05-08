//package fileManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.zip.*;
import java.util.*;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author wei
 *	实现文件管理器窗口
 *	FileFrame 窗口框架
 *	FileListPanel 文件列表面板
 *	PathPanel 路径显示面板和按钮
 *
 */

// 窗口框架
public class FileFrame extends JFrame{

	private FileListPanel filelistShow;
	private PathPanel pathPanel;
	private String mouseSelectFileName;
	private MouseRightPopup popup;

	public FileFrame()
	{
		//类实例域初始化
		filelistShow=new FileListPanel();
		pathPanel=new PathPanel();
		mouseSelectFileName=new String("");
		popup=new MouseRightPopup();

		//设置窗口关闭方法
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//设置窗口位置和大小（由系统平台决定）
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension screen=kit.getScreenSize();
		this.setSize(screen.width/2,screen.height/2);
		this.setLocationByPlatform(true);

		//实现路径文本框可以直接控制目录跳转
		pathTextToFile();

		//实现鼠标控制文件显示列表和右键显示菜单
		mouseControlFilelist();

		//实现右键菜单功能
		mouseRightMenuFunction();

		//控制组件摆放位置
		this.add(filelistShow,BorderLayout.CENTER);
		this.add(pathPanel, BorderLayout.NORTH);
	}

	public boolean openFile(String path)
	{
		pathPanel.showpath(path);
		return filelistShow.openFile(path);
	}

	public boolean deleteFile(String path)
	{
		boolean temp= filelistShow.deleteFile(path);
		File f=new File(path);
		openFile(f.getParent());
        return temp;
	}

	public boolean createDir(String parentPath)
	{
		boolean temp=filelistShow.createDir(parentPath);
		openFile(parentPath);
		return temp;
	}

	public boolean createTXTfile(String parentPath)
	{
		boolean temp=filelistShow.createTXTfile(parentPath);
		openFile(parentPath);
		return temp;
	}

    public boolean ZipFile(String path)

    {
        boolean temp = filelistShow.ZipFile(path);
		openFile(pathPanel.getPathInput());
        return temp;
    }

    public boolean UnzipFile(String path){
        boolean temp = filelistShow.UnzipFile(path);
		openFile(pathPanel.getPathInput());
        return temp;
    }

    public boolean EncFile(String file){
        boolean temp = filelistShow.EncFile(file);
		openFile(pathPanel.getPathInput());
        return temp;
    }

    public boolean DecFile(String file){
        boolean temp = filelistShow.DecFile(file);
		openFile(pathPanel.getPathInput());
        return temp;
    }

    public boolean copy(String src, String des){
        boolean temp = filelistShow.copy(src, des);
		openFile(pathPanel.getPathInput());
        return temp;
    }

	private void pathTextToFile()
	{
		pathPanel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String command = e.getActionCommand();
				if(command.equals("进入"))  //按下按钮“进入”
				{
					if(!Objects.equals(filelistShow.getLocalPath(),pathPanel.getPathInput()))
						openFile(pathPanel.getPathInput());
					else
						{
							//openFile(pathPanel.getPathInput()+"\\"+mouseSelectFileName);  //this is for win
                            if(mouseSelectFileName != "")
                                openFile(pathPanel.getPathInput()+ File.separator +mouseSelectFileName);
							mouseSelectFileName="";
						}

				}
				if(command.equals("返回"))   //按下按钮”返回“
				{
					String backString=pathBackTo(pathPanel.getPathInput());
					if(!openFile(backString))
					{
						openFile(filelistShow.getLocalPath());
					}
				}
				openFile(pathPanel.getPathInput());  //在文本框中回车
			}
		});
	}

	private void mouseControlFilelist()
	{

		filelistShow.addJListMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)
			{
				//设置左键双击打开文件夹
				if(filelistShow.getItemCanClick())
				{
					if(e.getClickCount()==2 && e.getButton()==MouseEvent.BUTTON1)
					{
						//openFile(pathPanel.getPathInput()+"\\"+mouseSelectFileName);  //this is for win
						openFile(pathPanel.getPathInput()+File.separator+mouseSelectFileName);
						mouseSelectFileName="";
					 }
				}

				//设置右键显示菜单
				if(e.getButton()==MouseEvent.BUTTON3)
			    {
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		filelistShow.addJListSelectListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO 自动生成的方法存根
				int i=e.getFirstIndex();
				mouseSelectFileName=filelistShow.getListFileName(i);

			}
		});
	}

	static public String pathBackTo(String path)  //处理路径字符串，删除最后一个separator和之后的字符串
	{
		StringBuffer temp=new StringBuffer(path);
		if(temp.length()!=0)
		{
			int start=temp.lastIndexOf(File.separator);
			if(start!=-1) temp.delete(start, temp.length());
			if(temp.charAt(temp.length()-1)==':')
				temp.append(File.separator);
		}
		return temp.toString();
	}

	public void mouseRightMenuFunction()
	{
		ActionListener itemAction=new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String temp = e.getActionCommand();
				if(temp.equals("打开"))
				{
					//openFile(pathPanel.getPathInput()+"\\"+mouseSelectFileName); //this is for win
					openFile(pathPanel.getPathInput()+File.separator+mouseSelectFileName);
				}
				if(temp.equals("删除"))
				{
					//deleteFile(pathPanel.getPathInput()+"\\"+mouseSelectFileName);  //this is for win
					deleteFile(pathPanel.getPathInput()+File.separator+mouseSelectFileName);
				}
				if(temp.equals("新建文件夹"))
				{
					createDir(pathPanel.getPathInput());
				}
				if(temp.equals("新建文件"))
				{
					createTXTfile(pathPanel.getPathInput());
				}
				if(temp.equals("刷新"))
				{
					openFile(pathPanel.getPathInput());
				}
                if(temp.equals("压缩")){
                   System.out.println("压缩");
                   //this.ZipFile(pathPanel.getPathInput() + "/" + mouseSelectFileName);   //??what do U mean by 找不到符号
                   ZipFile(pathPanel.getPathInput() + File.separator + mouseSelectFileName);
                }
                if(temp.equals("解压")){
                    System.out.println("解压");
                    boolean tmp = UnzipFile(pathPanel.getPathInput() + File.separator  + mouseSelectFileName);
                    System.out.println("unzip result: " + tmp);
                }
                if(temp.equals("加密")){
                    System.out.println("加密");
                    boolean tmp = EncFile(pathPanel.getPathInput() + File.separator  + mouseSelectFileName);
                    System.out.println("encFile result: " + tmp);
                }
                if(temp.equals("解密")){
                    System.out.println("解密");
                    boolean tmp = DecFile(pathPanel.getPathInput() + File.separator  + mouseSelectFileName);
                    System.out.println("DecFile result: " + tmp);
                }
                if(temp.equals("复制到")){
                   System.out.println("复制到");
                   System.out.println("input your destination");
                   try{
                       BufferedReader br = new BufferedReader(
                               new InputStreamReader(System.in));
                       String des = br.readLine();
                       boolean tmp = copy(pathPanel.getPathInput() + File.separator + mouseSelectFileName, des);
                       System.out.println("copy result: " + tmp);
                   }catch(IOException ee){
                       System.out.println(ee.toString());
                   }
                }
			}
		};


		popup.addItemListener(0,itemAction);
		popup.addItemListener(1, itemAction);
		popup.addItemListener(2, itemAction);
		popup.addItemListener(3, itemAction);
		popup.addItemListener(4, itemAction);
		popup.addItemListener(5, itemAction);
		popup.addItemListener(6, itemAction);
		popup.addItemListener(7, itemAction);
		popup.addItemListener(8, itemAction);
		popup.addItemListener(9, itemAction);
	}
}

//文件列表面板
class FileListPanel extends JPanel
{
	private DefaultListModel<String> item;
	private JList<?> filelist;
	private JScrollPane basepanel;
	private String localPath;
	private String[] manyFiles={""};


	public FileListPanel()
	{
		item=new DefaultListModel<>();
		filelist=new JList<String>(item);
		basepanel=new JScrollPane(filelist);
		localPath=new String();

		this.setLayout(new BorderLayout());
		this.add(basepanel);
	}

	public boolean openFile(String path)
	{
		item.removeAllElements();
		File f=new File(path);
		if(f!=null && f.isDirectory())
		{
			manyFiles=f.list();
			if(manyFiles==null)
			{
				manyFiles=new String[1];
				manyFiles[0]="";
			}
			else{
				for(int i=0;i<manyFiles.length;i++)
				{
					item.addElement(manyFiles[i]);
				}
			}
			localPath=path;
			return true;
		}
		else
		{
			item.addElement("This is not a Directory");
			return false;
		}
	}

	public String getListFileName(int i)
	{
		File f=new File(localPath);
		manyFiles=f.list();
		if(manyFiles!=null && manyFiles.length>i && i>=0)
		     return manyFiles[i];
		else return "";
	}

	public String getLocalPath()
	{
		return localPath;
	}

	public void addJListMouseListener(MouseListener l)
	{
		filelist.addMouseListener(l);
	}

	public void addJListSelectListener(ListSelectionListener l)
	{
		filelist.addListSelectionListener(l);
	}

	public boolean getItemCanClick()
	{
		if(item==null) return false;
		return item.size()!=0 &&
				!Objects.equals(item.firstElement(),"This is not a Directory");
	}

	public boolean deleteFile(String path)  //删除文件的公有接口
	{
		File f=new File(path);
		if(f.exists() )
		{
			if(!f.isDirectory())
				return f.delete();
			else
				return deleteDirectory(f);
		}
		return false;
	}

	private boolean deleteDirectory(File f)   //递归删除目录下所有文件
	{
		String [] children = f.list();
		if(children==null)   return f.delete();  //没有子目录则直接删除
		for(String ch : children)
		{
			File file=new File(f, ch);
			if(file.isDirectory())
				deleteDirectory(file);
			else
				file.delete();
		}
		f.delete();  //目录为空时可以直接删除
		return true;
	}

	public boolean createDir(String parentPath)  //在parentPath目录下创建文件夹
	{
		File f=new File(parentPath);
		if(!f.exists() || !f.isDirectory())  return false;
		String [] child=f.list();
		int i =0;    //父目录下有多少“新建文件夹”前缀的文件
		if(child!=null)
		{
			for(String ch : child)
				if(ch.startsWith("新建文件夹")) i++;
		}
		File newDir=new File(f, "新建文件夹"+"("+i+")");
		newDir.mkdir();
		return true;
	}

	public boolean createTXTfile(String parentPath)
	{
		File f=new File(parentPath);
		if(!f.exists() || !f.isDirectory())  return false;
		String [] child=f.list();
		int i =0;    //父目录下有多少“新建文件夹”前缀的文件
		if(child!=null)
		{
			for(String ch : child)
				if(ch.startsWith("新建文件")) i++;
		}
		File newfile =new File(f, "新建文件"+"("+i+")"+".txt");
		try {
			newfile.createNewFile();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return true;
	}

	public boolean ZipFile(String path)  //compress file with Zip algorithm
    {
        File f = new File(path);
        if(f.exists()){
            try{
                FileOutputStream fos = new FileOutputStream(this.getLocalPath()
                        + File.separator + f.getName() + ".zip");
                CheckedOutputStream csum =
                    new CheckedOutputStream(fos, new Adler32());
                ZipOutputStream zos = new ZipOutputStream(csum);
                BufferedOutputStream out =
                    new BufferedOutputStream(zos);

                if(!f.isDirectory()){
                    zos.putNextEntry(new ZipEntry(f.getName()));
                    //deal with output
                    System.out.println("Writing zip file "+ f.getName());
                    BufferedReader in =
                        new BufferedReader(new FileReader(f));
                    int c;
                    while((c = in.read()) != -1)
                        out.write(c);
                    in.close();
                }
                if(f.isDirectory()){
                    File lists[] = f.listFiles() ;
                    InputStream input = null;
                    int temp =0;
                    for(int i=0;i<lists.length;i++){
                        input = new FileInputStream(lists[i]) ;
                        zos.putNextEntry(new ZipEntry(f.getName()
                                    +File.separator+lists[i].getName())) ;  // 设置ZipEntry对象
                        while((temp=input.read())!=-1){ // 读取内容
                            zos.write(temp) ;    // 压缩输出
                        }
                        input.close() ; // 关闭输入流
                    }
                }
                out.close();

            }
            catch(IOException e){
                System.out.println("error IOExcemption" + e.toString()) ;
            }
            }else{ //if file doesn't exist
                return false;
            }

        return true;
    }


    public boolean UnzipFile(String path){
        boolean isSuccessful = true;
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
            ZipInputStream zis = new ZipInputStream(bis);

            BufferedOutputStream bos = null;
            ZipEntry entry = null;

            while ((entry=zis.getNextEntry()) != null) {
                String entryName = entry.getName();
                bos = new BufferedOutputStream(new FileOutputStream(new File(path).getParent() + File.separator +entryName));
                int b = 0;
                while ((b = zis.read()) != -1) {
                    bos.write(b);
                }
                bos.flush();
                bos.close();
            }
            zis.close();
        } catch (IOException e) {
            System.out.println(e.toString());
            isSuccessful = false;
        }
        return isSuccessful;
    }

    public boolean EncFile(String file){
        int dataOfFile = 0;
        int numOfEncAndDec = 0x99;
        File srcFile = new File(file);
        File encFile = new File(file + ".encFile");
        try{
            if(!srcFile.exists()){
                System.out.println("source file not exixt");
                return false;
            }

            if(!encFile.exists()){
                System.out.println("encrypt file created");
                encFile.createNewFile();
            }
            InputStream fis  = new FileInputStream(srcFile);
            OutputStream fos = new FileOutputStream(encFile);

            while ((dataOfFile = fis.read()) > -1) {
                fos.write(dataOfFile^numOfEncAndDec);
            }

            fis.close();
            fos.flush();
            fos.close();
        }catch(Exception e){
            System.out.println("Exception in encFile: "+ e.toString());
        }
        return true;
    }

    public boolean DecFile(String file){
        int dataOfFile = 0;
        int numOfEncAndDec = 0x99;
        File encFile = new File(file);
        File decFile = new File(file + ".decFile");
        try{
            if(!encFile.exists()){
                System.out.println("encrypt file not exixt");
                return false;
            }

            if(!decFile.exists()){
                System.out.println("decrypt file created");
                decFile.createNewFile();
            }

            InputStream fis  = new FileInputStream(encFile);
            OutputStream fos = new FileOutputStream(decFile);

            while ((dataOfFile = fis.read()) > -1) {
                fos.write(dataOfFile^numOfEncAndDec);
            }

            fis.close();
            fos.flush();
            fos.close();

        }catch(Exception e){
            System.out.println("Exception in DecFile: "+ e.toString());
            return false;
        }
        return true;
    }


    public boolean copy(String src, String des) {

        File file1=new File(src);
        if(!file1.isDirectory())
            return fileCopy(src, des);
        File[] fs=file1.listFiles();
        File file2=new File(des);
        if(!file2.exists()){
            file2.mkdirs();
        }
        for (File f : fs) {
            copy(f.getPath(),des+File.separator+f.getName());
        }
        return true;
    }

    /**
     * 文件拷贝的方法
     */
    private boolean fileCopy(String src, String des) {

        if(!new File(src).exists())
            return false;

        BufferedReader br=null;
        PrintStream ps=null;

        try {
            br=new BufferedReader(new InputStreamReader(new FileInputStream(src)));
            ps=new PrintStream(new FileOutputStream(des));
            String s=null;
            while((s=br.readLine())!=null){
                ps.println(s);
                ps.flush();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally{

            try {
                if(br!=null)  br.close();
                if(ps!=null)  ps.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

}

//路径显示面板和按钮
class PathPanel extends JPanel
{
	JTextField pathtext;
	JButton enter;
	JButton back;
	JPanel buttonPanel;

	public PathPanel()
	{
		pathtext=new JTextField();
		pathtext.setHorizontalAlignment(JTextField.CENTER);
		enter=new JButton("进入");
		back=new JButton("返回");
		buttonPanel=new JPanel();

		buttonPanel.add(enter);
		buttonPanel.add(back);

		this.setLayout(new BorderLayout());
		this.add(pathtext,BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.EAST);
	}

	public void showpath(String path)  //显示路径字符串
	{
		pathtext.setText(path);
	}

	public String getPathInput()    //获取路径文本框内容
	{
		return pathtext.getText();
	}


	public void addActionListener(ActionListener a)  //为两个按钮添加ActionListener监听器，使用同一个监听器对象
	{
		enter.addActionListener(a);
		back.addActionListener(a);
		pathtext.addActionListener(a);
	}

}

class MouseRightPopup extends JPopupMenu
{
	private JMenuItem[] item;


	public MouseRightPopup()
	{
		super();
		item=new JMenuItem[10];

		item[0]=new JMenuItem("打开");
		item[1]=new JMenuItem("删除");
		item[2]=new JMenuItem("新建文件夹");
		item[3]=new JMenuItem("新建文件");
		item[4]=new JMenuItem("刷新");
		item[5]=new JMenuItem("压缩");
		item[6]=new JMenuItem("解压");
		item[7]=new JMenuItem("加密");
		item[8]=new JMenuItem("解密");
		item[9]=new JMenuItem("复制到");

		this.add(item[0]);
		this.add(item[1]);
		this.add(item[2]);
		this.add(item[3]);
		this.add(item[4]);
		this.add(item[5]);
		this.add(item[6]);
		this.add(item[7]);
		this.add(item[8]);
		this.add(item[9]);
	}

	public void addItemListener(int i,ActionListener a)
	{
		item[i].addActionListener(a);
	}
}
