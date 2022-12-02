//JAMES NALEPA

package jsn;

import jsn.DiskHole;
import jsn.DiskFile;

import java.util.ArrayList;

public class Disk
{
    private int size_in_blocks = 0;
    private ArrayList<DiskHole> holes = new ArrayList<>();
    private ArrayList<DiskFile> files = new ArrayList<>();
    private int fileIndex = 0;

    public Disk(int size)
    {
        size_in_blocks = size;
        holes.add(new DiskHole(0, size_in_blocks));
    }

    public boolean add(String name, int size)
    {
        DiskHole holeToUse = null;
        // loop through the holes and find the smallest hole that can store the file
        for (DiskHole hole : holes)
        {
            if (hole.holesize >= size && (holeToUse == null || holeToUse.holesize > hole.holesize))
            {
                holeToUse = hole; // we'll allocate from this hole
            }
        }
        if (holeToUse == null) // failed to allocate the file
        {
            System.out.printf("No space available to add file %s.", name);
            return false;
        }
        else
        {
            DiskFile f = new DiskFile(name, holeToUse.holestart, size); // create the file and add it to the files linkedlist
            fileIndex++;
            files.add(f);
            holeToUse.blockalloc(size); // reduce the size of the free block
            System.out.printf("File %s was added successfully\n",name);
            return true;
        }
    }

    public void del(String name)
    {
        DiskFile file = findFile(name);
        if (file == null)
        {
            System.out.printf("File %s does not exist on the disk.\n", name);
        } else
        {
            files.remove(file);
            // see if we can add to an existing hole
            for (DiskHole hole : holes)
            {
                // does hole end where file started?
                if (file.filestart == hole.holestart + hole.holesize)
                {
                    hole.holestart = file.filestart;
                    return;
                }
                // did file end where hole starts?
                else if (file.filestart + file.filesize == hole.holestart)
                {
                    hole.holesize = hole.holesize + file.filesize;
                    return;
                }
            }
            // create a new hole
            DiskHole newhole = new DiskHole(file.filestart,file.filesize);
            holes.add(newhole);
            System.out.printf("File %s was deleted successfully.\n",name);
        }
    }

    public int read(String name)
    {
        int headMoves = 0;
        DiskFile file = findFile(name);
        if (file == null)
        {
            System.out.printf("File %s does not exist on the disk.\n", name);
        } else
        {
            // file is in one contiguous block so always requires only 1 head move
            System.out.printf("File %s was read successfully with %d head move(s).\n", name, 1);
            headMoves = 1;
        }
        return headMoves;
    }

    private DiskFile findFile(String name)
    {
        DiskFile file = null;
        for (DiskFile f : files)
        {
            if (f.filename.equals(name))
            {
                file = f;
                break;
            }
        }
        return file;
    }

    public void print()
    {
        int[] blocks = new int[size_in_blocks];

        System.out.println("\n============== Current Drive Contents =================");

        System.out.println("\nDIRECTORY:");
        for (int fileindex=1; fileindex<=files.size(); fileindex++)
        {
            DiskFile f = files.get(fileindex-1);
            System.out.printf("%d. %s, Blocks ",fileindex,f.filename);
            for (int idx=f.filestart; idx<f.filestart+f.filesize; idx++ )
            {
                System.out.printf(" %d",idx);
                blocks[idx] = fileindex;
            }
            System.out.println();
        }

        System.out.println("\nDETAILS:");
        for (int block = 0; block < size_in_blocks; block++)
        {
            if (blocks[block] == 0)
                System.out.print("  *");
            else
                System.out.printf("%3d", blocks[block]);
            if (block % 10 == 9)
                System.out.println();
        }
        System.out.println();
    }
}