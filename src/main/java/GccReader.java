import java.io.InputStream;

public class GccReader extends DataReader{

    public GccReader(String newFileName) {
        super(newFileName);
    }

    public GccReader(InputStream inputStream){
        super(inputStream);
    }

    public int readInt(){
        return readInt(4);
    }

    public int readInt(int numberOfByte){
        readBytes(numberOfByte);
        boolean hasMinus = false;



        return 0;
    }


}
