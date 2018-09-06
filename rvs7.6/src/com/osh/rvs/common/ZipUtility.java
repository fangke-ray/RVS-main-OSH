package com.osh.rvs.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import de.schlichtherle.io.ArchiveDetector;
import de.schlichtherle.io.DefaultArchiveDetector;
import de.schlichtherle.io.archive.tar.TarBZip2Driver;
import de.schlichtherle.io.archive.tar.TarDriver;
import de.schlichtherle.io.archive.tar.TarGZipDriver;
import de.schlichtherle.io.archive.zip.CheckedZip32Driver;
import de.schlichtherle.util.zip.ZipEntry;
import de.schlichtherle.util.zip.ZipOutputStream;

public class ZipUtility {
	/**
	 * eg:zipper("c:\\����Ŀ¼","c:\\����Ŀ¼777.zip","GBK");
	 * @param directory ѹ��Ŀ¼������ָ��һ���ļ�
	 * @param destFile ��ɵ�ѹ���ļ�
	 * @param encoding ����
	 */
	@SuppressWarnings("static-access")
	public static void zipper(String directory,String destFile,String encoding){
		ZipUtility zipCompress = new ZipUtility ();//��ʼ��֧�ֶ༶Ŀ¼ѹ����ZipMultiDirectoryCompress
	    String defaultParentPath="";//Ĭ�ϵ���Ե�ַ��Ϊ��·��
	    ZipOutputStream zos = null;
	    try{
	    	zos = new ZipOutputStream(new FileOutputStream(destFile),"GBK");//����һ��Zip�����
	    	zipCompress.startCompress(zos,defaultParentPath,directory);//����ѹ�����
	    }catch(FileNotFoundException e){e.getMessage();
	    }catch(NullPointerException e){e.getMessage();
	    }catch(UnsupportedEncodingException e){e.getMessage();
	    }finally{
	    	try{if(zos != null)zos.close();
	        }catch(IOException e){e.getMessage();}
	    }    
	}
	
	/**
	 * ("d:/����Ŀ¼777.zip","d:/","GBK");
	 * @param zipFileName zip�ļ�
	 * @param destDir ��ѹ·��
	 * @param encoding ����
	 */
	public static void unzipper(String zipFileName, String destDir, String encoding){
		new DefaultArchiveDetector(
			ArchiveDetector.ALL, 
			new Object[]{"zip",new CheckedZip32Driver(encoding),"tar",new TarDriver(encoding), "tgz|tar.gz",
			new TarGZipDriver(encoding),"tbz|tar.bz2",new TarBZip2Driver(encoding)}).createFile(
				zipFileName).archiveCopyAllTo(new File(destDir));
	 }

	/**
	 * ("d:/����Ŀ¼777.zip","d:/","GBK");
	 * @param zipFileName zip�ļ�
	 * @param destDir ��ѹ·��
	 * @param encoding ����
	 */
	public static void unzipper6(String zipFileName, String destDir, String encoding){
		new DefaultArchiveDetector(
			ArchiveDetector.ALL, 
			new Object[]{"zip",new CheckedZip32Driver(encoding)}).createFile(
				zipFileName).archiveCopyAllTo(new File(destDir));
	}

    private static void startCompress(ZipOutputStream zos, String oppositePath, String directory){
    	File file = new File(directory);
        if(file.isDirectory()){//�����ѹ��Ŀ¼
            File[] files = file.listFiles();
            for(int i=0;i<files.length;i++){
                File aFile = files[i];               
                if(aFile.isDirectory()){
                    String newOppositePath = oppositePath + aFile.getName() + "/";//�����Ŀ¼���޸���Ե�ַ
                    compressDirectory(zos, oppositePath, aFile);//����Ŀ¼
                    startCompress(zos,newOppositePath,aFile.getPath());//���еݹ����
                }else compressFile(zos,oppositePath,aFile);//�����Ŀ¼�������ѹ��
            }
        }else compressFile(zos,oppositePath,file);//�����ѹ���ļ���ֱ�ӵ���ѹ����������ѹ��
    }
    
    private static void compressFile(ZipOutputStream zos, String oppositePath, File file){
        ZipEntry entry = new ZipEntry(oppositePath + file.getName());//����һ��Zip��Ŀ��ÿ��Zip��Ŀ���Ǳ�������ڸ�·��
        InputStream is = null;
        try{
            zos.putNextEntry(entry);//����Ŀ���浽Zipѹ���ļ�����
            is = new FileInputStream(file);//���ļ����������ж�ȡ��ݣ��������д�����������        
            int length = 0;
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            while((length=is.read(buffer,0,bufferSize))>=0) zos.write(buffer, 0, length);
            zos.closeEntry();
        }catch(IOException ex){ex.getMessage();
        }finally{
            try{if(is != null)is.close();
            }catch(IOException ex){ex.getMessage();}
        }       
    }

    private static void compressDirectory(ZipOutputStream zos, String oppositePath, File file){
        ZipEntry entry = new ZipEntry(oppositePath + file.getName() + "/");//ѹ��Ŀ¼�����ǹؼ��һ��Ŀ¼����Ŀʱ����Ҫ��Ŀ¼�����Ӷ�һ��"/"
        try{
            zos.putNextEntry(entry);
            zos.closeEntry();
        }catch(IOException e){e.getMessage();}
    }
}