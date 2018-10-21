package utility;

import java.awt.HeadlessException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import mainMenu.MainMenu;


public class FileThread extends Thread {
	private ReceiveFile recv;
	private Socket sock;
	private DataInputStream in;
	private String header;
	private byte[] data;
	BufferedOutputStream bout;
	MainMenu mainMenu;
	File file;
	static StringTokenizer fileNameTokenizer;
	static int fileNumber=1;
	public FileThread(MainMenu mainMenu, ReceiveFile recv, Socket sock) {
		this.mainMenu = mainMenu;
		this.recv  = recv;
	
		try {
			this.recv = recv;
			this.sock = sock;
			in = new DataInputStream(sock.getInputStream());
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	public void decodePacket(String senderID, String fileName, int fileLen) {
		
		
		
	}
	public void setFilePath(String fileName) {
		
		File dir= new File("받은파일\\");
		if(!dir.exists())
		{
			dir.mkdir();
		}
		
		file = new File(dir,  fileName );
		if(file.exists()) {
			fileNameTokenizer =new StringTokenizer(fileName, ".");
			
			String fileReName;
			String fileExt;
			fileName = fileNameTokenizer.nextToken();
			fileExt = fileNameTokenizer.nextToken();
			if(fileNumber == Integer.MAX_VALUE)
				fileNumber = 1;
			
		//	for(int i=fileNumber;i<Integer.MAX_VALUE;i+=1,fileNumber+=1)
			{	
				fileReName = fileName + "(" + (fileNumber++) + ")." + fileExt ; 
				file = new File(dir,fileReName) ;
				if(!file.exists())
				{
				//	break;
				}
				
			}
			
		}
		else
		{
			try {
				if(!file.createNewFile()) {
					JOptionPane.showInputDialog(fileName + "파일생성에러");
				}
			} catch (HeadlessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	public void run() {
		int writeByte=0;
		int sum=0;
		try {
			header = in.readUTF();
	//		mainMenu.popUp(header);
	//		System.out.println(header);
			StringTokenizer st = new StringTokenizer(header,"#");
			String senderID = st.nextToken();
			String fileName = st.nextToken();
			int fileLen = Integer.parseInt(st.nextToken());
		//	System.out.println("fileLen: " + fileLen);
			// decodePacket
	//		JOptionPane.showInputDialog(header);
			
			
			setFilePath(fileName);
			
			
			data = new byte[fileLen];
			BufferedInputStream bin = new BufferedInputStream(in, 2048);
			DataOutputStream dout = null;
			try {
				dout = new DataOutputStream(sock.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			readFile(file, bin, dout, data,fileLen);
			dout.writeUTF("파일을 전부 받았습니다.");
		//	mainMenu.popUp("파일을 전부 받았습니다.");
			
			
		
			
			
		//	sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				bout.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(sock!=null) {try {
				sock.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} sock = null;}
			if(in!=null) {try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} in =null;}
			
		
		}
		
	}
	private void readFile(File file,BufferedInputStream bin, DataOutputStream dout, byte[] data, int fileLen) throws IOException {
		int size =0;
		int recvBytes=0;
		int count =0;
		int rest =0;
		int flag = 1;
		int sum=0;
		
		size = 2048;
		count = fileLen / size;
		
		rest = fileLen % size;
		if(count ==0)flag=0;
		
	//	System.out.println("count : " + count);
	//System.out.println("rest: " + rest);
		Scanner sc = new Scanner(System.in);
		
		
		FileOutputStream fout = new FileOutputStream(file);
		 bout = new BufferedOutputStream(fout, 2048);
		
		
		while(sum!=fileLen)
		{
			recvBytes = bin.read(data);
			bout.write(data, 0, recvBytes);
			sum+=recvBytes;
			/*
			if(i==count && flag == 0) {
				recvBytes = bin.read(data, 0, rest);
				sum+=recvBytes;
				dout.writeUTF("파일을 전부 받았습니다.(1)");
				System.out.printf("파일을  %d bytes 받았습니다.(last1: i=%d)",sum,i);
				return;
			}
			else if(i== count) {
				bin.read(data, i*size, rest);
				sum+=recvBytes;
				dout.writeUTF("파일을 전부 받았습니다.(2)");
				System.out.printf("파일을  %d bytes 받았습니다.(last2: i=%d)",sum,i);
				return;
			}
			else {
				recvBytes = bin.read(data,i*size, size);
				sum+=recvBytes;
				
				if(i%10000==0) {
					System.out.printf("파일을 %dbytes받았습니다.",sum);
					sc.nextLine();
				}
					
					
			}*/
		}
		//System.out.printf("파일을  %d bytes 받았습니다.(last)",sum);
		
	}
}
