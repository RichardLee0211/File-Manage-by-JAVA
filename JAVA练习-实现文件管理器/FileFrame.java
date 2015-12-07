package fileManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Objects;

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
 *	ʵ���ļ�����������
 *	FileFrame ���ڿ��
 *	FileListPanel �ļ��б����
 *	PathPanel ·����ʾ���Ͱ�ť
 *
 */

// ���ڿ��
public class FileFrame extends JFrame{
	
	private FileListPanel filelistShow;
	private PathPanel pathPanel;
	private String mouseSelectFileName;
	private MouseRightPopup popup;
	
	public FileFrame()
	{
		//��ʵ�����ʼ��
		filelistShow=new FileListPanel();
		pathPanel=new PathPanel();
		mouseSelectFileName=new String("");
		popup=new MouseRightPopup();
		
		//���ô��ڹرշ���
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//���ô���λ�úʹ�С����ϵͳƽ̨������
		Toolkit kit=Toolkit.getDefaultToolkit();  
		Dimension screen=kit.getScreenSize();
		this.setSize(screen.width/2,screen.height/2);
		this.setLocationByPlatform(true);
		
		//ʵ��·���ı������ֱ�ӿ���Ŀ¼��ת
		pathTextToFile();
		
		//ʵ���������ļ���ʾ�б���Ҽ���ʾ�˵�
		mouseControlFilelist();
		
		//ʵ���Ҽ��˵�����
		mouseRightMenuFunction();
		
		//��������ڷ�λ��
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
	
	private void pathTextToFile()
	{
		pathPanel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String command = e.getActionCommand();
				if(command.equals("����"))  //���°�ť�����롱
				{
					if(!Objects.equals(filelistShow.getLocalPath(),pathPanel.getPathInput()))
						openFile(pathPanel.getPathInput());
					else
						{
							openFile(pathPanel.getPathInput()+"\\"+mouseSelectFileName);
							mouseSelectFileName="";
						}
						
				}
				if(command.equals("����"))   //���°�ť�����ء�
				{
					String backString=pathBackTo(pathPanel.getPathInput());
					if(!openFile(backString))
					{
						openFile(filelistShow.getLocalPath());
					}
				}
				openFile(pathPanel.getPathInput());  //���ı����лس�
			}
		});
	}
	
	private void mouseControlFilelist()
	{
		
		filelistShow.addJListMouseListener(new MouseAdapter() {
			@Override 
			public void mouseClicked(MouseEvent e)
			{
				//�������˫�����ļ���
				if(filelistShow.getItemCanClick())
				{
					if(e.getClickCount()==2 && e.getButton()==MouseEvent.BUTTON1)
					{
						openFile(pathPanel.getPathInput()+"\\"+mouseSelectFileName);
						mouseSelectFileName="";
					 }
				}
				
				//�����Ҽ���ʾ�˵�
				if(e.getButton()==MouseEvent.BUTTON3)
			    {
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		
		filelistShow.addJListSelectListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO �Զ����ɵķ������
				int i=e.getFirstIndex();
				mouseSelectFileName=filelistShow.getListFileName(i);
				
			}
		});
	}
	
	static public String pathBackTo(String path)  //����·���ַ�����ɾ�����һ��\��֮����ַ���
	{
		StringBuffer temp=new StringBuffer(path);
		if(temp.length()!=0)
		{
			int start=temp.lastIndexOf("\\");  //ת���ַ�\\��ʾ\
			if(start!=-1) temp.delete(start, temp.length());
			if(temp.charAt(temp.length()-1)==':')
				temp.append('\\');
		}
		return temp.toString();
	}
	
	public void mouseRightMenuFunction()
	{
		ActionListener itemAction=new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO �Զ����ɵķ������
				String temp = e.getActionCommand();
				if(temp.equals("��"))
				{
					openFile(pathPanel.getPathInput()+"\\"+mouseSelectFileName);
				}
				if(temp.equals("ɾ��"))
				{
					deleteFile(pathPanel.getPathInput()+"\\"+mouseSelectFileName);
				}
				if(temp.equals("�½��ļ���"))
				{
					createDir(pathPanel.getPathInput());
				}
				
			}
		};
		
		
		popup.addItemListener(0,itemAction);
		popup.addItemListener(1, itemAction);
		popup.addItemListener(2, itemAction);
	}
}

//�ļ��б����
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
	
	public boolean deleteFile(String path)  //ɾ���ļ��Ĺ��нӿ�
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
	
	private boolean deleteDirectory(File f)   //�ݹ�ɾ��Ŀ¼�������ļ�
	{
		String [] children = f.list();
		if(children==null)   return f.delete();  //û����Ŀ¼��ֱ��ɾ��
		for(String ch : children)
		{
			File file=new File(f, ch);
			if(file.isDirectory())
				deleteDirectory(file);
			else
				file.delete();
		}
		f.delete();  //Ŀ¼Ϊ��ʱ����ֱ��ɾ��
		return true;
	}
	
	public boolean createDir(String parentPath)  //��parentPathĿ¼�´����ļ���
	{
		File f=new File(parentPath);
		if(!f.exists() || !f.isDirectory())  return false;
		String [] child=f.list();
		int i =0;    //��Ŀ¼���ж��١��½��ļ��С�ǰ׺���ļ�
		if(child!=null)
		{
			for(String ch : child)
				if(ch.startsWith("�½��ļ���")) i++;
		}
		File newDir=new File(f, "�½��ļ���"+"("+i+")");
		newDir.mkdir();
		return true;
	}
}

//·����ʾ���Ͱ�ť
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
		enter=new JButton("����");
		back=new JButton("����");
		buttonPanel=new JPanel();
		
		buttonPanel.add(enter);
		buttonPanel.add(back);
		
		this.setLayout(new BorderLayout());
		this.add(pathtext,BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.EAST);
	}
	 
	public void showpath(String path)  //��ʾ·���ַ���
	{
		pathtext.setText(path);
	}
	
	public String getPathInput()    //��ȡ·���ı�������
	{
		return pathtext.getText();
	}
	
	
	public void addActionListener(ActionListener a)  //Ϊ������ť���ActionListener��������ʹ��ͬһ������������
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
		item=new JMenuItem[5];
		
		item[0]=new JMenuItem("��");
		item[1]=new JMenuItem("ɾ��");
		item[2]=new JMenuItem("�½��ļ���");
		item[3]=new JMenuItem("�½��ļ�");
		item[4]=new JMenuItem("ˢ��");
		
		this.add(item[0]);
		this.add(item[1]);
		this.add(item[2]);
		this.add(item[3]);
		this.add(item[4]);
	}
	
	public void addItemListener(int i,ActionListener a)
	{
		item[i].addActionListener(a);
	}
}



