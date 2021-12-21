package soiiii;

public class PrintArray {
    public static void main(String[] args) {
        int[] myIntArray = new int[3];
        for (int i = 0; i < myIntArray.length; i++){
            myIntArray[i] = i*10;
        }
    }
    public static void PrintArray(int array[]){
        for (int i = 0; i < array.length; i++ ){
            System.out.println("Elemnt " + i + ", value is " + array[i]);
        }
    }
}
