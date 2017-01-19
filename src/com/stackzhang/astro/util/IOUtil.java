package com.stackzhang.astro.util;

import java.io.*;

/**
 * IO流工具
 *
 */
public class IOUtil {

    /**
     * 根据输入流获得字节数组
     *
     * @param InputStream is 不需要手动关闭
     * @param int         length 如果知道流数据字节大小，则需要指定。如果不知道需填入0
     * @return
     */
    public static byte[] getBytes(InputStream is) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        int len = -1;
        byte[] bytes = new byte[1024 * 8];
        try {
            while ((len = is.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();    //关闭流
                    is = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bos.toByteArray();
    }

    public static void writeBytes(File file, byte[] bytes) {

        try {
            BufferedOutputStream bufos = new BufferedOutputStream(new FileOutputStream(file));
            try {
                bufos.write(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bufos != null) {
                    try {
                        bufos.close();    //关闭流
                        bufos = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void appened(File file, String info){
    	FileWriter fw = null;
    	try {
    		fw = new FileWriter(file, true);
    		fw.write(info);
    		fw.write("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fw != null){
				try {
					fw.close();
					fw = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    }
}
