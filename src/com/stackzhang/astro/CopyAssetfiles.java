package com.stackzhang.astro;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CopyAssetfiles {
    String pattern;
    Context ct;

    public CopyAssetfiles(String pattern, Context ct) {
        this.pattern = pattern;
        this.ct = ct;
    }
    void copy() {
        AssetManager assetManager = ct.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }

        String outdir = ct.getFilesDir() + File.separator + "/ephe";
        new File(outdir).mkdirs();
        outdir += File.separator;

        for(String filename : files) {
            if (new File(outdir + filename).exists() || !filename.matches(pattern)) {
                continue;
            }

            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);

                File outFile = new File(outdir, filename);

                out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}
