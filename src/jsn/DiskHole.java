//JAMES NALEPA

package jsn;

public class DiskHole
{
    int holestart;
    int holesize;

    public DiskHole(int start, int size)
    {
        holestart = start;
        holesize = size;
    }

    public void blockalloc(int size)
    {
        holestart = holestart + size;
        holesize = holesize - size;
    }
}
