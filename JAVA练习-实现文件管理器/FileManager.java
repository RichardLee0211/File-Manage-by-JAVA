package fileManager;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileSystemView;

/**
 *@author wei
 *main�������������壬ʵ���ļ�������
 * */

public class FileManager {

	public static void main(String[] args) {
		// TODO �Զ����ɵķ������
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				// TODO �Զ����ɵķ������
				FileFrame frame=new FileFrame();
				
				//���ó�ʼĿ¼ΪϵͳĬ��
				FileSystemView rootview=FileSystemView.getFileSystemView();
				File root=rootview.getDefaultDirectory();
				frame.openFile(root.getPath());
				
				frame.setVisible(true);

			}
		});
		
	}

}
