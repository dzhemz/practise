public class EntryPoint {
    public static void main(String[] args) {
        var reader = new DataReader("src/main/resources/simpleFile");
        System.out.println(reader.readInt(2));
    }
}
