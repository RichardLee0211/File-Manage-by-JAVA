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
import javax.swing.JPanel;
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
	String mouseSelectFileName;
	
	public FileFrame()
	{
		//��ʵ�����ʼ��
		filelistShow=new FileListPanel();
		pathPanel=new PathPanel();
		mouseSelectFileName=new String("");
		
		//���ô��ڹرշ���
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//���ô���λ�úʹ�С����ϵͳƽ̨������
		Toolkit kit=Toolkit.getDefaultToolkit();  
		Dimension screen=kit.getScreenSize();
		this.setSize(screen.width/2,screen.height/2);
		this.setLocationByPlatform(true);
		
		//ʵ��·���ı������ֱ�ӿ���Ŀ¼��ת
		pathTextToFile();
		
		//ʵ���������ļ���ʾ�б�
		mouseControlFilelist();
		
		//��������ڷ�λ��
		this.add(filelistShow,BorderLayout.CENTER);
		this.add(pathPanel, BorderLayout.NORTH);
	}
	
	public boolean openFile(String path)
	{
		pathPanel.showpath(path);
		return filelistShow.openFile(path);
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
				if(filelistShow.getItemCanClick())
				{
					if(e.getClickCount()==2)
					{
						openFile(pathPanel.getPathInput()+"\\"+mouseSelectFileName);
						mouseSelectFileName="";
					 }
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
}

//�ļ��б����
class FileListPanel extends JPanel 
{
	DefaultListModel<String> item;
	JList<?> filelist;
	JScrollPane basepanel;
	String localPath;
	String[] manyFiles={""};
	
	
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




