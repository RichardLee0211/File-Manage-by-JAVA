package fileManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

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
	
	public FileFrame()
	{
		//��ʵ�����ʼ��
		filelistShow=new FileListPanel();
		pathPanel=new PathPanel();
		
		//���ô��ڹرշ���
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//���ô���λ�úʹ�С����ϵͳƽ̨������
		Toolkit kit=Toolkit.getDefaultToolkit();  
		Dimension screen=kit.getScreenSize();
		this.setSize(screen.width/2,screen.height/2);
		this.setLocationByPlatform(true);
		
		//ʵ��·���ı������ֱ�ӿ���Ŀ¼��ת
		pathTextToFile();
		
		//��������ڷ�λ��
		this.add(filelistShow,BorderLayout.CENTER);
		this.add(pathPanel, BorderLayout.NORTH);
	}
	
	public void openFile(String path)
	{
		filelistShow.openFile(path);
		pathPanel.showpath(path);
	}
	
	private void pathTextToFile()
	{
		pathPanel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String command = e.getActionCommand();
				if(command.equals("����"))  //���°�ť�����롱
				{
					openFile(pathPanel.getPathInput());
				}
				if(command.equals("����"))   //���°�ť�����ء�
				{
					String backString=pathBackTo(pathPanel.getPathInput());
					openFile(backString);
				}
				openFile(pathPanel.getPathInput());  //���ı����лس�
			}
		});
	}
	
	static public String pathBackTo(String path)  //����·���ַ�����ɾ�����һ��\��֮����ַ���
	{
		StringBuffer temp=new StringBuffer(path);
		int start=temp.lastIndexOf("\\");  //ת���ַ�\\��ʾ\
		temp.delete(start, temp.length());
		if(temp.charAt(temp.length()-1)==':')
				temp.append('\\');
		return temp.toString();
	}
}

//�ļ��б����
class FileListPanel extends JPanel 
{
	DefaultListModel<String> item;
	JList<?> filelist;
	JScrollPane basepanel;
	
	public FileListPanel()
	{
		item=new DefaultListModel<>();
		filelist=new JList<String>(item);
		basepanel=new JScrollPane(filelist);
		
		this.setLayout(new BorderLayout());
		this.add(basepanel);
	}
	
	public void openFile(String path)
	{
		item.removeAllElements();
		File f=new File(path);
		if(f.isDirectory())
		{
			String[] manyFiles=f.list();
			for(int i=0;i<manyFiles.length;i++)
			{
				item.addElement(manyFiles[i]);
			}
		}
		else
		{
			item.addElement("This is not a Directory");
		}
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




