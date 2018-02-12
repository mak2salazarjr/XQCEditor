package person.wangchen11.zipfile;
 
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
 
/**
 * Function : �ļ�ѹ����zip
 * @author  : lqf
 * @Date    : 2015-12-15
 */
public class ZipCompressing {
	static final String TAG="ZipCompressing";
	private static final int BUFFER_SIZE = 4096;
    static int k = 1; // ����ݹ��������
 
    /**
     * ѹ��ָ���ĵ��������ļ��������Ŀ¼�������Ŀ¼�������ļ�����ѹ��
     * @param zipFileName ZIP�ļ�������ȫ·��
     * @param files  �ļ��б�
     */
    public static boolean zip(String zipFileName, File... files) {
        ZipOutputStream out = null;
        try {
            createDir(zipFileName);
            out = new ZipOutputStream(new FileOutputStream(zipFileName));
            for (int i = 0; i < files.length; i++) {
                if (null != files[i]) {
                    zip(out, files[i], files[i].getName());
                }
            }
            out.close(); // ������ر�
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
 
    /**
     * ִ��ѹ��
     * @param out ZIP������
     * @param f   ��ѹ�����ļ�
     * @param base  ��ѹ�����ļ���
     */
    private static void zip(ZipOutputStream out, File f, String base) { // ��������
        try {
            if (f.isDirectory()) {//ѹ��Ŀ¼
                try {
                    File[] fl = f.listFiles();
                    if (fl.length == 0) {
                        out.putNextEntry(new ZipEntry(base + "/"));  // ����zipʵ��
                    }
                    for (int i = 0; i < fl.length; i++) {
                        zip(out, fl[i], base + "/" + fl[i].getName()); // �ݹ�������ļ���
                    }
                    //System.out.println("��" + k + "�εݹ�");
                    k++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{ //ѹ�������ļ� 
            	ZipEntry entry=new ZipEntry(base);
                out.putNextEntry(entry); // ����zipʵ��
                FileInputStream in = new FileInputStream(f);
                BufferedInputStream bi = new BufferedInputStream(in);
                int readLen=0;
                byte []data=new byte[BUFFER_SIZE];
                while ( (readLen=bi.read(data))>0 ) {
                    out.write(data,0,readLen); // ���ֽ���д�뵱ǰzipĿ¼
				}
                out.closeEntry(); //�ر�zipʵ��
                in.close(); // �������ر�
            }
 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
 
    /**
     * Ŀ¼������ʱ���ȴ���Ŀ¼
     * @param zipFileName
     */
    private static void createDir(String zipFileName){
    	new File(zipFileName).getParentFile().mkdirs();
    }
}