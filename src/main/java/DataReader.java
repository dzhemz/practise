import java.io.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Класс считывания данных C/C++ формата
 * и представления их в виде Java объектов
 * Есть несколько вариантов использования:
 * Задать название файла откуда прочесть данные
 */
public class DataReader {

    /**
     * Входящий поток хранится в internalBuffer
     */
    protected BufferedInputStream internalBuffer;

    /**
     * Накапливает байты, если запрашиваемую структуру прочесть не удалось
     * например осталось 2 байта, а запрощенно прочесть int
     * в таком случае байты не будут утеряны
     */
    protected LinkedList<Integer> accumulator = new LinkedList<>();

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
                accumulator.add(piece);
                ++lengthAcc;
            }
        } catch (IOException ignored){
            return false;
        }
        return true;
    }

    public int readInt(){
        return ((Long)readNumber(4)).intValue();
    }

    public int readInt(int numberOfBytes){
        if (numberOfBytes <= 4) return ((Long)readNumber(numberOfBytes)).intValue();
        throw new RuntimeException("Integer имеет максимальный размер 4 байта");
    }

    public long readLong(){
        return ((Long)readNumber(4));
    }

    public long readLong(int numberOfBytes){
        if (numberOfBytes <= 8) return readNumber(numberOfBytes);
        throw new RuntimeException("Long имеет максимальный размер 8 байт");
    }

    public short readShort(){
        return readShort(2);
    }
    public short readShort(int numberOfBytes){
        if (numberOfBytes <= 2) return ((Long)readNumber(numberOfBytes)).shortValue();
        throw new RuntimeException("Short имеет максимальный размер 2 байта");
    }

    public byte readByte(){
        return ((Long)readNumber(1)).byteValue();
    }

    public long readNumber(int numberOfBytes){
        int powerOfTwo = numberOfBytes;

        if (!readBytes(numberOfBytes)) {
            throw new RuntimeException("Не удалось прочесть необходимое число байт");
        }
        numberOfBytes--;

        long result = accumulator.pollLast();

        boolean isNegative = false;

        if (result > 127){
            isNegative = true;
            result -= 128;
        }
        result <<= (numberOfBytes * 8L);
        numberOfBytes--;

        while (accumulator.size() > 0){
            result += (long) accumulator.pollLast() << (numberOfBytes * 8);
            numberOfBytes--;
        }
        if (isNegative){
            return (long)-Math.pow(2, powerOfTwo * 8) + result;
        }
        return result;
    }

    public float readFloat(){
        return (float) readFractionNumber(4, 8);
    }

    public float readFloat(int numberOfBytes){
        if (numberOfBytes == 4) return (float)readFractionNumber(numberOfBytes, 8);
        throw new RuntimeException("Float имеет размер 4 байта");
    }

    public double readDouble(){
        return readFractionNumber(8,  11);
    }

    public double readDouble(int numberOfBytes){
        if (numberOfBytes == 8) return readFractionNumber(numberOfBytes, 11);
        throw new RuntimeException("Double имеет размер 8 байт");
    }

    public double readFractionNumber(int numberOfBytes, int baseSizeBits){
        if (!readBytes(numberOfBytes)) throw new RuntimeException("Не удалось прочесть необходимое число байт");

        var stringList = accumulator.stream()
                .map(elem -> {
                    var string = Integer.toBinaryString(elem);
                    return "0".repeat(8 - string.length()) + string;})
                .collect(Collectors.toList());

        accumulator.clear();
        Collections.reverse(stringList);

        var binaryForm = String.join("", stringList);
        var sign = binaryForm.charAt(0);
        var exponent = binaryForm.substring(1, baseSizeBits + 1);
        var mantis = binaryForm.substring(baseSizeBits + 1);

        var powerOfTwo = Integer.parseInt(exponent, 2) - Math.pow(2, baseSizeBits - 1) + 1;
        double value = 1;
        var index = 1;
        for (char digit: mantis.toCharArray()){
            if (digit == '1') value += Math.pow(2, -index);
            ++index;
        }
        if (sign == '1'){
            return Math.pow(2, powerOfTwo) * value * -1;
        }
        return Math.pow(2, powerOfTwo) * value;
    }

    public boolean readBoolean(){
        if (!readBytes(1)) throw new RuntimeException("Нет данных для обработки");
        if (accumulator.pollLast() == 1) return true;
        return false;
    }

    public char readChar(){
        if (!readBytes(1)) throw new RuntimeException("Нет данных для обработки");
        return Character.toChars(accumulator.pollLast())[0];
    }
}
