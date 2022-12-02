//JAMES NALEPA

package jsn;

public class DiskFile
{
    String filename;
    int filestart;
    int filesize;

    public DiskFile(String name, int start, int size)
    {
        filename = name;
        filestart = start;
        filesize = size;
    }
}

