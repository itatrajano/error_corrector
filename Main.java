package correcter;

import java.util.Random;
import java.util.Scanner;

class RandomChar {

    public static char getChar() {
        Random random = new Random();
        char randomChar = 0;
        boolean isCharSpace = randomChar == 32;
        boolean isCharNumber = randomChar >= 48 && randomChar <= 57;
        boolean isCharUppercase = randomChar >= 65 && randomChar <= 90;
        boolean isCharLowercase = randomChar >= 97 && randomChar <= 122;

        while (!(isCharLowercase || isCharUppercase || isCharNumber || isCharSpace)) {
            randomChar = (char) random.nextInt(123);
            isCharSpace = randomChar == 32;
            isCharNumber = randomChar >= 48 && randomChar <= 57;
            isCharUppercase = randomChar >= 65 && randomChar <= 90;
            isCharLowercase = randomChar >= 97 && randomChar <= 122;
        }
        return randomChar;
    }
}

public class Main {
    public static void main(String[] args) {

        Scanner scanner =  new Scanner(System.in);
        String input = scanner.nextLine();
        char[] charsArray = input.toCharArray();

        //Creating random errors. One error at each group of three characters.
        int inputSize = charsArray.length; //Number of characters


        Random random = new Random();
        for (int i = 0; i < inputSize; i = i + 3) {
            int numberOfCharsAtLeft = inputSize - 1 - i;
            int randomBound;
            switch (numberOfCharsAtLeft) { //To define the limits to be used at random.nextInt
                case 0:
                    randomBound = 1; //To draw 0, always
                    break;
                case 1:
                    randomBound = 2; //To draw 0, 1
                    break;
                default:
                    randomBound = 3; //To draw 0, 1 or 2
            }

            int zeroOneOrTwo = random.nextInt(randomBound); //Draw which element of the triple will be changed.
            int targetIndex = i + zeroOneOrTwo;
            char newChar = 0;
            do {
                newChar = RandomChar.getChar();
            } while (newChar == charsArray[i]);
            charsArray[targetIndex] = newChar;
        }

        String damagedString = new String(charsArray);
        System.out.println(damagedString);
    }
}
