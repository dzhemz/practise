import java.io.*;
import java.util.LinkedList;

/**
 * Класс считывания данных C/C++ формата
 * и представления их в виде Java объектов
 * Есть несколько вариантов использования:
 * Задать название файла откуда прочесть данные
 * Использовать другой входящий поток, который получен не из файла.
 */
abstract public class DataReader {

    /**
     * Входящий поток хранится в internalBuffer
     */
    protected BufferedInputStream internalBuffer;

    /**
     * Накапливает байты, если запрашиваемую структуру прочесть не удалось
     * например осталось 2 байта, а запрощенно прочесть int
     * в таком случае байты не будут утеряны
     */
    protected LinkedList<Byte> accumulator = new LinkedList<>();

    /**
     * @param newFileName имя файла, который необходимо прочесть
     */
    public DataReader(String newFileName) {
        try {
            internalBuffer = new BufferedInputStream(new FileInputStream(newFileName));
        } catch (FileNotFoundException exception){
            System.out.println("Файл не найден");
        }
    }

    /**
     * @param inputStream входной поток, из которого необходимо прочесть файлы
     */
    public DataReader(InputStream inputStream){
        internalBuffer = new BufferedInputStream(inputStream);
    }


    /**
     * @param numberBytes число байтов, которое необходимо прочитать из бинарного потока
     */
    protected boolean readBytes(Integer numberBytes){
        int lengthAcc = accumulator.size();
        int piece;
        try {
            while (lengthAcc < numberBytes){
                piece = internalBuffer.read();
                if (piece == -1){
                    return false;
                }
                ++lengthAcc;
            }
        } catch (IOException ignored){
            return false;
        }
        return true;
    }
}
