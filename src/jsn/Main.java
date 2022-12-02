//JAMES NALEPA
// this is a simulation of disk allocation. It adds files and keeps track of the blocks containing each one. When a file is added or removed it performs a number of head moves.
// The program simulates BOTH contiguous and indexed allocation of files.

package jsn;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main
{
    public static void main(String[] args)
    {
        File file = new File("disk.dat");
        doContiguous(file);
        doIndexed(file);
    }

    private static void doContiguous(File file)
    {
        Scanner sc = null;
        try
        {
            sc = new Scanner(file);
        } catch (FileNotFoundException e)
        {
            System.out.println("Input file not found.");
            return;
        }

        System.out.println("--------------- START CONTIGUOUS SIMULATION ---------------");
        int headMoves = 0;
        int failedAllocations = 0;
        int disksize = Integer.parseInt(sc.nextLine());
        System.out.println("totBlock = "+disksize);
        Disk disk = new Disk(disksize);

        while (sc.hasNextLine())
        {
            String line = sc.nextLine();
            String[] cmd = ParseCommand(line);

            if (cmd[0].equals("add"))
            {
                String filename = cmd[1];
                int filesize = Integer.parseInt(cmd[2]);
                if( !disk.add(filename, filesize) )
                {
                    failedAllocations++;
                }
            }
            else if (cmd[0].equals("print"))
            {
                disk.print();
            }
            else if (cmd[0].equals("read"))
            {
                String filename = cmd[1];
                headMoves = headMoves + disk.read(filename);
            }
            else if (cmd[0].equals("del"))
            {
                String filename = cmd[1];
                disk.del(filename);
            }
            else
            {
                System.out.printf("Invalid command '%s' in input file.\n", cmd);
            }
        }
        sc.close();

        System.out.println("========= Contiguous Allocation Statistics =============\n\nDuring this simulation,");
        System.out.printf("Total head moves = %d\n",headMoves);
        System.out.printf("Total number of files that could not be allocated = %d\n",failedAllocations);
        System.out.println("\n----------------- END of CONTIGUOUS SIMULATION ---------------");
    }

    private static void doIndexed(File file)
    {
        Scanner sc = null;
        try
        {
            sc = new Scanner(file);
        } catch (FileNotFoundException e)
        {
            System.out.println("Input file not found.");
            return;
        }

        System.out.println("--------------- START INDEXED SIMULATION ---------------");
        int headMoves = 0;
        int failedAllocations = 0;
        int disksize = Integer.parseInt(sc.nextLine());
        System.out.println("totBlock = "+disksize);
        FatDisk disk = new FatDisk(disksize);

        while (sc.hasNextLine())
        {
            String line = sc.nextLine();
            String[] cmd = ParseCommand(line);

            if (cmd[0].equals("add"))
            {
                String filename = cmd[1];
                int filesize = Integer.parseInt(cmd[2]);
                if( !disk.add(filename, filesize) )
                {
                    failedAllocations++;
                }
            }
            else if (cmd[0].equals("print"))
            {
                disk.print();
            }
            else if (cmd[0].equals("read"))
            {
                String filename = cmd[1];
                headMoves = headMoves + disk.read(filename);
            }
            else if (cmd[0].equals("del"))
            {
                String filename = cmd[1];
                disk.del(filename);
            }
            else
            {
                System.out.printf("Invalid command '%s' in input file.\n", cmd);
            }
        }
        sc.close();

        System.out.println("========= Indexed Allocation Statistics =============\n\nDuring this simulation,");
        System.out.printf("Total head moves = %d\n",headMoves);
        System.out.printf("Total number of files that could not be allocated = %d\n",failedAllocations);
        System.out.println("\n----------------- END of INDEXED SIMULATION ---------------");
    }

    private static String[] ParseCommand(String line)
    {
        String regex = "\"([^\"]*)\"|(\\S+)";
        ArrayList<String> tokens = new ArrayList<>();
        Matcher m = Pattern.compile(regex).matcher(line);
        while (m.find())
        {
            if (m.group(1) != null)
            {
                tokens.add(m.group(1));
            } else
            {
                tokens.add(m.group(2));
            }
        }
        return tokens.toArray(new String[0]);
    }
}




