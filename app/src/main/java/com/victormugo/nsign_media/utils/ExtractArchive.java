package com.victormugo.nsign_media.utils;

import android.util.Log;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import com.victormugo.nsign_media.activities.Core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ExtractArchive {

    public static void extractArchive(String archive, String destination) throws RarException {
        if (archive == null || destination == null) {
            throw new RuntimeException("archive and destination must me set");
        }
        File arch = new File(archive);
        if (!arch.exists()) {
            throw new RuntimeException("the archive does not exit: " + archive);
        }
        File dest = new File(destination);
        if (!dest.exists() || !dest.isDirectory()) {
            throw new RuntimeException(
                    "the destination must exist and point to a directory: "
                            + destination);
        }
        extractArchive(arch, dest);
    }

    public static void main(String[] args) throws RarException {
        if (args.length == 2) {
            extractArchive(args[0], args[1]);
        } else {
            System.out
                    .println("usage: java -jar extractArchive.jar <thearchive> <the destination directory>");
        }
    }

    public static void extractArchive(File archive, File destination) throws RarException {
        Archive arch = null;

        Log.d(Core.TAG, "------------------> archive: " + archive);
        Log.d(Core.TAG, "------------------> destination: " + destination);

        try {
            arch = new Archive(archive);

        } catch (RarException e) {
            Log.d(Core.TAG, "-----------------> e: " + e.getMessage());

        } catch (IOException e1) {
            Log.d(Core.TAG, "-----------------> e1: " + e1.getMessage());
        }

        if (arch != null) {
            if (arch.isEncrypted())  return;

            FileHeader fh;
            while (true) {
                fh = arch.nextFileHeader();

                if (fh == null)  break;
                if (fh.isEncrypted()) continue;

                try {
                    if (fh.isDirectory()) {
                        createDirectory(fh, destination);

                    } else {
                        File f = createFile(fh, destination);
                        OutputStream stream = new FileOutputStream(f);
                        arch.extractFile(fh, stream);
                        stream.close();
                    }

                } catch (IOException e) {
                    Log.d(Core.TAG, "-----------------> e2: " + e.getMessage());

                } catch (RarException e) {
                    Log.d(Core.TAG, "---------------> e3: " + e.getMessage());
                }
            }
        }
    }

    private static File createFile(FileHeader fh, File destination) {
        File f = null;
        String name = null;
        if (fh.isFileHeader() && fh.isUnicode()) {
            name = fh.getFileNameW();
        } else {
            name = fh.getFileNameString();
        }
        f = new File(destination, name);
        if (!f.exists()) {
            try {
                f = makeFile(destination, name);
            } catch (IOException e) {
            }
        }
        return f;
    }

    private static File makeFile(File destination, String name)
            throws IOException {
        String[] dirs = name.split("\\\\");
        if (dirs == null) {
            return null;
        }
        String path = "";
        int size = dirs.length;
        if (size == 1) {
            return new File(destination, name);
        } else if (size > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                path = path + File.separator + dirs[i];
                new File(destination, path).mkdir();
            }
            path = path + File.separator + dirs[dirs.length - 1];
            File f = new File(destination, path);
            f.createNewFile();
            return f;
        } else {
            return null;
        }
    }

    private static void createDirectory(FileHeader fh, File destination) {
        File f = null;
        if (fh.isDirectory() && fh.isUnicode()) {
            f = new File(destination, fh.getFileNameW());
            if (!f.exists()) {
                makeDirectory(destination, fh.getFileNameW());
            }
        } else if (fh.isDirectory() && !fh.isUnicode()) {
            f = new File(destination, fh.getFileNameString());
            if (!f.exists()) {
                makeDirectory(destination, fh.getFileNameString());
            }
        }
    }

    private static void makeDirectory(File destination, String fileName) {
        String[] dirs = fileName.split("\\\\");
        if (dirs == null) {
            return;
        }
        String path = "";
        for (String dir : dirs) {
            path = path + File.separator + dir;
            new File(destination, path).mkdir();
        }

    }
}
