//JAMES NALEPA

package jsn;

import jsn.DiskHole;
import jsn.FatFile;

import java.util.ArrayList;

public class FatDisk
{
    private int size_in_blocks = 0;
    private ArrayList<DiskHole> holes = new ArrayList<>();
    private ArrayList<FatFile> files = new ArrayList<>();
    private int fileIndex = 0;

    public FatDisk(int size)
    {
        size_in_blocks = size;
        holes.add(new DiskHole(0, size_in_blocks));
    }

    public boolean add(String name, int size)
    {
        FatFile f = new FatFile(name); // create the file and add it to the files linkedlist
        int sizeleft = size; // starting with the entire size of the file left to allocate
        while(sizeleft > 0)
        {
            // allocate from the available holes
            int sizetoallocate = sizeleft>7 ? 7 : sizeleft;
            DiskHole hole = holes.get(0);
            if (hole.holesize > sizetoallocate+1) // we can allocate the block+fat with this hole
            {
                f.addBlock(hole.holestart,sizetoallocate+1);
                hole.blockalloc(sizetoallocate+1);
                if( hole.holesize == 0 ) // remove hole if we used it all up
                {
                    holes.remove(hole);
                }
                sizeleft = sizeleft - sizetoallocate;
            }
            else // entire hole and more needed for the block
            {
                f.addBlock(hole.holestart,hole.holesize);
                holes.remove(hole);
                sizeleft = sizeleft - (hole.holesize-1); //hole has data plus fat block
            }
        }

        files.add(f);
        System.out.printf("File %s was added successfully\n",name);
        return true;
    }

    public void del(String name)
    {
        FatFile file = findFile(name);
        if (file == null)
        {
            System.out.printf("File %s does not exist on the disk.\n", name);
        }
        else
        {
            files.remove(file);
            // need to handle each of the blocks allocated for the file
            for(int idx=0;idx<file.blocks;idx++)
            {
                int start = file.blockstart.get(idx);
                int size = file.blocksize.get(idx);
                // see if we can add to an existing hole
                for (DiskHole hole : holes)
                {
                    // does hole end where file started?
                    if (start == hole.holestart + hole.holesize)
                    {
                        hole.holestart = start;
                        return;
                    }
                    // did file end where hole starts?
                    else if (start + size == hole.holestart)
                    {
                        hole.holesize = hole.holesize + size;
                        return;
                    }
                }
                // otherwise create a new hole
                DiskHole newhole = new DiskHole(start,size);
                holes.add(newhole);
            }
            System.out.printf("File %s was deleted successfully.\n",name);
        }
    }

    public int read(String name)
    {
        int headMoves = 0;
        FatFile file = findFile(name);
        if (file == null)
        {
            System.out.printf("File %s does not exist on the disk.\n", name);
        } else
        {
            // reading the file requires one head move per non-contiguous block
            headMoves = 1; // head move to first block
            // loop through all remaining blocks and check for head moves
            int pos = file.blockstart.get(0)+file.blocksize.get(0); // position after reading first block
            for (int bidx=1; bidx<file.blocks; bidx++)
            {
                if (file.blockstart.get(bidx) != pos+1 ){
                    headMoves++;
                }
                pos = file.blockstart.get(bidx)+file.blocksize.get(bidx);
            }
        }
        System.out.printf("File %s was read successfully with %d head move(s).\n", name, headMoves);
        return headMoves;
    }

    private FatFile findFile(String name)
    {
        FatFile file = null;
        for (FatFile f : files)
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
            FatFile f = files.get(fileindex-1);
            System.out.printf("%d. %s, Blocks ",fileindex,f.filename);
            for (int bidx=0; bidx<f.blockstart.size(); bidx++) // loop through all the fat blocks the file uses
            {
                int start = f.blockstart.get(bidx);
                int size = f.blocksize.get(bidx);
                for (int idx=start; idx<start+size; idx++ )
                {
                    System.out.printf(" %d",idx);
                    blocks[idx] = fileindex;
                }
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