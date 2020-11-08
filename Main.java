package correcter;

import java.io.*;
import java.util.*;

class ErrorGenerator {

    //Creates randoms errors. One error at each group of three characters.
    public static String getDamagedString(String originalString) {

        char[] charsArray = originalString.toCharArray();

        int inputSize = charsArray.length; //Number of characters
        for (int i = 0; i < inputSize; i = i + 3) {
            int numberOfCharsAtLeft = inputSize - 1 - i;
            int randomBound;
            switch (numberOfCharsAtLeft) { //To define the limits to be used at random.nextInt
                case 0:
                    randomBound = 1; //To draw 0, always
                    break;
                case 1:
                    randomBound = 2; //To draw 0 or 1
                    break;
                default:
                    randomBound = 3; //To draw 0, 1 or 2
            }

            Random random = new Random();
            int zeroOneOrTwo = random.nextInt(randomBound); //Draw which element of the triple will be changed.
            int targetIndex = i + zeroOneOrTwo;
            char newChar;
            do {
                newChar = getRandomChar();
            } while (newChar == charsArray[i]); //If the char to be changed is the same as the new char, try again.
            charsArray[targetIndex] = newChar;
        }
        String damagedString = new String(charsArray);
        return damagedString;
    }

    private static char getRandomChar() {
        Random random = new Random();
        char randomChar = 0;
        boolean isCharSpace = false;
        boolean isCharNumber = false;
        boolean isCharUppercase = false;
        boolean isCharLowercase = false;

        while (!(isCharLowercase || isCharUppercase || isCharNumber || isCharSpace)) {
            randomChar = (char) random.nextInt(123);
            isCharSpace = randomChar == 32;
            isCharNumber = randomChar >= 48 && randomChar <= 57;
            isCharUppercase = randomChar >= 65 && randomChar <= 90;
            isCharLowercase = randomChar >= 97 && randomChar <= 122;
        }
        return randomChar;
    }

    //Change a random single bit in a byte
    public static int getDamagedByteAsInt(int originalInt) {
        Random random = new Random();
        int drawnNumber = 1 + random.nextInt(7); //Drawn a number from 1 to 8, representing the bit to be changed
        drawnNumber = 1 << drawnNumber;
        return originalInt ^ drawnNumber;
    }

}

class EncoderDecoder {

    //Repeats every character in the string "repetition" times
    public static String encoder(String inputString, int repetition) {
        char[] inputCharArray = inputString.toCharArray();
        char[] encodedCharArray = new char[inputCharArray.length * repetition];
        for (int i = 0; i < inputCharArray.length; i++) { //For every char in the old array, add it "repetition" times to the new array
            int startingIndex = i * repetition;
            for (int j = 0; j < repetition; j++) {
                encodedCharArray[startingIndex + j] = inputCharArray[i];
            }
        }
        return String.copyValueOf(encodedCharArray);
    }

    //Doubles every bit and add a parity bit every 8 bits
    public static int encoder(int byteAsInt) {
        //Calculating parity bit
        boolean parity = false;
        int parityCheck = byteAsInt;
        while (parityCheck != 0) {
            parity = !parity;
            parityCheck = parityCheck & (parityCheck - 1);
        }
        int parityBit = parity ? 1 : 0;

        //This code is an adaptation of the bit interleaving algorithm, but using the same number twice.
        int input = byteAsInt;
        int doubledBits = 0;
        short iterationLimit = 8; //Number of bits / 2
        for (int i = 0; i < iterationLimit; i++) {
            int mask = input & (1 << i);
            doubledBits = doubledBits | (mask << i);
            doubledBits = doubledBits | (mask << i + 1);
        }
        //Adding parity bit
        doubledBits = (doubledBits << 1) + parityBit;

        return doubledBits;
    }

    public static String decoderAndRestorer(String inputString, int repetition) {
        char[] inputCharArray = inputString.toCharArray();
        char[] decodedCharArray = new char[inputCharArray.length / repetition];
        for (int i = 0; i < inputCharArray.length; i = i + repetition) { //To each group of size "repetition"
            //Decision process - The first index with more points is chosen.
            int[] score = new int[repetition]; //Score of every char in "evaluatingGroup"
            char[] evaluatingGroup = new char[repetition];
            int evaluatingGroupIndex = 0;
            for (int j = i; j < i + repetition; j++) { //Copy the group to be analyzed to a new array
                evaluatingGroup[evaluatingGroupIndex] = inputCharArray[j];
                evaluatingGroupIndex++;
            }
            for (char c: evaluatingGroup) { //Compare each char to every other char in ints evaluatingGroup
                for (int j = 0; j < evaluatingGroup.length; j++) {
                    if (c == evaluatingGroup[j]) {
                        score[j]++;
                    }
                }
            }

            //Check the highest score
            int chosenIndex = 0;
            int higherScore = 0;
            for (int j = 0; j < score.length; j++) {
                if (score[j] > higherScore) {
                    higherScore = score[j];
                    chosenIndex = j;
                }
            }
            decodedCharArray[i / repetition] = evaluatingGroup[chosenIndex];
        }
        return String.copyValueOf(decodedCharArray);
    }
}

public class Main {
    public static void main(String[] args) {

        try {
            File inputFile = new File("D:\\Error Correcting Encoder-Decoder\\task\\src\\correcter\\send.txt");
            FileInputStream inputStream = new FileInputStream(inputFile);
            Queue<Integer> bytesAsIntQueue = new ArrayDeque<>();
            int byteAsInt = inputStream.read();
            while (byteAsInt != -1) {
                bytesAsIntQueue.offer(byteAsInt);
                byteAsInt = inputStream.read();
            }
            inputStream.close();

            int[] bytesAsIntArray = new int[bytesAsIntQueue.size()];

            //Adiciona o conteúdo do Queue ao Array de Ints
            for (int i = 0; i < bytesAsIntArray.length; i++) {
                bytesAsIntArray[i] = bytesAsIntQueue.poll();
            }

            //Prints input as text
            for (int i : bytesAsIntArray) {
                System.out.print((char) i);
            }
            System.out.println();

            //Prints input as binary
            for (int i : bytesAsIntArray) {
                System.out.print(Integer.toBinaryString(i) + " ");
            }
            System.out.println();

//TESTE DE DUPLICAÇÃO DE BITS
            int[] encodedBytesArray = new int[bytesAsIntArray.length];
            System.out.println("Bits duplicados + bit de paridade:");
            for (int i = 0; i < encodedBytesArray.length; i++) {
                encodedBytesArray[i] = EncoderDecoder.encoder(bytesAsIntArray[i]);
                System.out.print(Integer.toBinaryString(encodedBytesArray[i]) + " ");
            }
            System.out.println();

//FIM DO TESTE DE DUPLICAÇÃO DE BITS

/*
            //Insert errors
            int[] damagedBytesAsIntArray = new int[bytesAsIntArray.length];
            for (int i = 0; i < damagedBytesAsIntArray.length; i++) {
                damagedBytesAsIntArray[i] = ErrorGenerator.getDamagedByteAsInt(bytesAsIntArray[i]);
                //Prints damaged message as binary
                System.out.print(Integer.toBinaryString(damagedBytesAsIntArray[i]) + " ");
            }
            System.out.println();

            //Print damaged message as text
            for (int i : damagedBytesAsIntArray) {
                System.out.print((char) i);
            }
*/
            //Record damaged message
            try {
                File outputFile = new File("D:\\Error Correcting Encoder-Decoder\\task\\src\\correcter\\received.txt");
                FileOutputStream outputStream = new FileOutputStream(outputFile);
                for (int i : encodedBytesArray) {
                //for (int i : damagedBytesAsIntArray) {
                    outputStream.write(i);
                }
               outputStream.close();


            } catch (Exception e) {
                System.out.println("Erro de Gravação");
                System.out.println(e.getLocalizedMessage());
            }

        } catch (Exception e) {
            System.out.println("Erro de Leitura");
            System.out.println(e.getLocalizedMessage());
        }
/*
        Scanner scanner =  new Scanner(System.in);
        String input = scanner.nextLine();
        System.out.println(input);
        String encodedString = EncoderDecoder.encoder(input, 3);
        System.out.println(encodedString);
        String damagedString = ErrorGenerator.getDamagedString(encodedString);
        System.out.println(damagedString);
        String restoredString = EncoderDecoder.decoderAndRestorer(damagedString, 3);
        System.out.println(restoredString);
*/
    }
}
