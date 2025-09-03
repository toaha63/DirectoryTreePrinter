import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.*;

public class DirectoryTreePrinter
{

    public static void main(String[] args)
    {
        while (args.length < 1)
        {
            printUsage();
            return;
        }
        
        String folderPath = new String(args[0]);
        File rootDir = new File(folderPath);
        
        if (!rootDir.exists())
        {
            System.out.println("The specified path does not exist: " + folderPath);
            return;
        }
        
        if (!rootDir.isDirectory())
        {
            System.out.println("The specified path is not a directory: " + folderPath);
            return;
        }
        
        OutputMode outputMode = OutputMode.CONSOLE_ONLY;
        boolean showHidden = false;
        
        // Parse command line flags
        for (int i = 1; i < args.length; i++)
        {
            switch (args[i])
            {
                case "-c":
                    outputMode = OutputMode.CONSOLE_ONLY;
                    break;
                case "-f":
                    outputMode = OutputMode.FILE_ONLY;
                    break;
                case "-b":
                    outputMode = OutputMode.BOTH;
                    break;
                case "-h":
                    showHidden = true;
                    break;
                default:
                    System.out.println("Invalid flag: " + args[i]);
                    printUsage();
                    return;
            }
        }
        
        switch (outputMode)
        {
            case CONSOLE_ONLY:
                printToConsoleOnly(rootDir, showHidden);
                break;
            case FILE_ONLY:
                printToFileOnly(rootDir, showHidden);
                break;
            case BOTH:
                printToBoth(rootDir, showHidden);
                break;
            default:
                break;
        }
    }
    
    private static void printUsage()
    {
        System.out.println("Usage: java DirectoryTreePrinter <folder-path> [flags]");
        System.out.println("Flags:");
        System.out.println("  -c    Print to console only (default)");
        System.out.println("  -f    Print to file only");
        System.out.println("  -b    Print to both console and file");
        System.out.println("  -h    Show hidden files and directories (starting with '.')");
        System.out.println("Examples:");
        System.out.println("  java DirectoryTreePrinter /path/to/folder");
        System.out.println("  java DirectoryTreePrinter /path/to/folder -c -h");
        System.out.println("  java DirectoryTreePrinter /path/to/folder -f -h");
        System.out.println("  java DirectoryTreePrinter /path/to/folder -b -h");
    }
    
    private static void printToConsoleOnly(File rootDir, boolean showHidden)
    {
        System.out.println(rootDir.getName());
        printDirectoryTree(rootDir, new Vector<>(), showHidden);
    }
    
    private static void printToFileOnly(File rootDir, boolean showHidden)
    {
        String outputFileName = generateOutputFileName(rootDir);
        
        AtomicReference<Exception> exceptionRef = new AtomicReference<>();
        
        Thread virtualThread = Thread.startVirtualThread(() ->
        {
            try (PrintWriter writer = new PrintWriter(new FileWriter(outputFileName)))
            {
                writer.println(rootDir.getName());
                printDirectoryTreeToFile(rootDir, new Vector<>(), writer, showHidden);
                System.out.println("Directory tree successfully written to: " + outputFileName);
            }
            catch (IOException e)
            {
                exceptionRef.set(e);
            }
        });
        
        try
        {
            virtualThread.join();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            System.out.println("Thread was interrupted: " + e.getMessage());
        }
        
        Exception fileWriteException = exceptionRef.get();
        if (fileWriteException != null)
        {
            System.out.println("Error writing to file: " + fileWriteException.getMessage());
        }
    }
    
    private static void printToBoth(File rootDir, boolean showHidden)
    {
        String outputFileName = generateOutputFileName(rootDir);
        AtomicReference<Exception> exceptionRef = new AtomicReference<>();
        
        // Start virtual thread for file writing
        Thread virtualThread = Thread.startVirtualThread(() ->
        {
            try (PrintWriter writer = new PrintWriter(new FileWriter(outputFileName)))
            {
                writer.println(rootDir.getName());
                printDirectoryTreeToFile(rootDir, new Vector<>(), writer, showHidden);
                System.out.println("Directory tree successfully written to: " + outputFileName);
            }
            catch (IOException e)
            {
                exceptionRef.set(e);
            }
        });
        
        // Print to console in main thread
        System.out.println(rootDir.getName());
        printDirectoryTree(rootDir, new Vector<>(), showHidden);
        
        // Wait for virtual thread to complete
        try
        {
            virtualThread.join();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            System.out.println("Thread was interrupted: " + e.getMessage());
        }
        
        Exception fileWriteException = exceptionRef.get();
        if (fileWriteException != null)
        {
            System.out.println("Error writing to file: " + fileWriteException.getMessage());
        }
    }
    
    private static String generateOutputFileName(File rootDir)
    {
        return rootDir.getName() + "_tree.txt";
    }
    
    private static void printDirectoryTree(File directory, List<Boolean> isLastList, boolean showHidden)
    {
        File[] files = directory.listFiles();
        if (files == null) return;
        
        // Filter files based on hidden flag
        List<File> filteredFiles = new Vector<>();
        for (File file : files)
        {
            if (showHidden || !file.isHidden())
            {
                filteredFiles.add(file);
            }
        }
        
        // Sort files: directories first, then files, both alphabetically
        List<File> directories = new Vector<>();
        List<File> fileList = new Vector<>();
        
        for (File file : filteredFiles)
        {
            if (file.isDirectory())
            {
                directories.add(file);
            }
            else
            {
                fileList.add(file);
            }
        }
        
        directories.sort((f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
        fileList.sort((f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
        
        List<File> allFiles = new Vector<>();
        allFiles.addAll(directories);
        allFiles.addAll(fileList);
        
        for (int i = 0; i < allFiles.size(); i++)
        {
            File file = allFiles.get(i);
            boolean isLast = (i == allFiles.size() - 1);
            
            // Print the connectors
            for (boolean isLastInParent : isLastList)
            {
                if (isLastInParent)
                {
                    System.out.print("    ");
                }
                else
                {
                    System.out.print("│   ");
                }
            }
            
            if (isLast)
            {
                System.out.print("└── ");
            }
            else
            {
                System.out.print("├── ");
            }
            
            System.out.println(file.getName());
            
            if (file.isDirectory())
            {
                List<Boolean> newIsLastList = new Vector<>(isLastList);
                newIsLastList.add(isLast);
                printDirectoryTree(file, newIsLastList, showHidden);
            }
        }
    }
    
    private static void printDirectoryTreeToFile(File directory, List<Boolean> isLastList, PrintWriter writer, boolean showHidden)
    {
        File[] files = directory.listFiles();
        if (files == null) return;
        
        // Filter files based on hidden flag
        List<File> filteredFiles = new Vector<>();
        for (File file : files)
        {
            if (showHidden || !file.isHidden())
            {
                filteredFiles.add(file);
            }
        }
        
        // Sort files: directories first, then files, both alphabetically
        List<File> directories = new Vector<>();
        List<File> fileList = new Vector<>();
        
        for (File file : filteredFiles)
        {
            if (file.isDirectory())
            {
                directories.add(file);
            }
            else
            {
                fileList.add(file);
            }
        }
        
        directories.sort((f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
        fileList.sort((f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
        
        List<File> allFiles = new Vector<>();
        allFiles.addAll(directories);
        allFiles.addAll(fileList);
        
        for (int i = 0; i < allFiles.size(); i++)
        {
            File file = allFiles.get(i);
            boolean isLast = (i == allFiles.size() - 1);
            
            // Print the connectors
            for (boolean isLastInParent : isLastList)
            {
                if (isLastInParent)
                {
                    writer.print("    ");
                }
                else
                {
                    writer.print("│   ");
                }
            }
            
            if (isLast)
            {
                writer.print("└── ");
            }
            else
            {
                writer.print("├── ");
            }
            
            writer.println(file.getName());
            
            if (file.isDirectory())
            {
                List<Boolean> newIsLastList = new Vector<>(isLastList);
                newIsLastList.add(isLast);
                printDirectoryTreeToFile(file, newIsLastList, writer, showHidden);
            }
        }
    }
    
    private enum OutputMode
    {
        CONSOLE_ONLY,
        FILE_ONLY,
        BOTH
    }
}