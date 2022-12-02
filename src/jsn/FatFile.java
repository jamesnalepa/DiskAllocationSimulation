//JAMES NALEPA

package jsn;

import java.util.ArrayList;

public class FatFile
{
    String filename;
    int blocks=0;
    ArrayList<Integer> blockstart;
    ArrayList<Integer> blocksize;

    public FatFile(String name)
    {
        filename = name;
        blocks = 0;
        blockstart = new ArrayList<Integer>();
        blocksize = new ArrayList<Integer>();
    }

    public void addBlock(int start, int size)
    {
        blocks++;
        blockstart.add(start);
        blocksize.add(size);
    }
}

