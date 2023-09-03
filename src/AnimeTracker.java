import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import static java.lang.Float.parseFloat;


public class AnimeTracker {
    public static BufferedWriter fileWriter;
    public static Map<String, Anime> animeList = new LinkedHashMap<String, Anime>() {
        @Override
        // remove the eldest entry when the list size gets higher than 10
        protected boolean removeEldestEntry(Map.Entry<String, Anime> eldest) {
            return size() > 10;
        }
    };


    /**
     * Splits the line from the last space before the rating into two strings, the anime name and rating
     *
     * @param line the string containing anime name and a rating
     * @return Anime object
     */
    public static Anime Tokenize(String line) {
        Anime temp = new Anime();

        //Split the line at the last space, the one before the rating
        String[] strArr = line.split("\\s(?=[^\\s]*$)", 2);
        temp.setAnimeName(strArr[0]);
        temp.setAnimeRating(parseFloat(strArr[1]));
        return temp;
    }

    /**
     * writes the string provided into the file "LOG.txt" and creates a newline afterwards
     *
     * @param str the string that you want to write into the file
     * @throws IOException if the user doesn't have permission to write to the file
     */
    public static void LogToFile(String str) throws IOException {
        fileWriter.write(str);
        fileWriter.write("\n");
    }

    /**
     * calculates the levenshtien distance between two strings, levenshtien distance is the amount
     * of edits (deletion,substitution,insertion) that can be made to one of the strings
     * to make the two strings the same
     *
     * @param string1   the first string
     * @param string2   the second string
     * @param threshold the amount of edits allowed
     * @return if the two strings are threshold edits away or lower from being the same
     */
    public static boolean similar(String string1, String string2, int threshold) {
        // make the two strings lowercase and remove spaces for accuracy
        String str1 = string1.toLowerCase();
        str1 = str1.replaceAll("\\s", "");
        String str2 = string2.toLowerCase();
        str2 = str2.replaceAll("\\s", "");
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];

        // iterate through the strings
        for (int i = 0; i <= str1.length(); i++) {
            for (int j = 0; j <= str2.length(); j++) {
                // If one string is empty, the distance is the length of the other string
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    // Calculate the cost of replacing or not replacing characters
                    int cost = (str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1;
                    // Choose the minimum of three possible operations: insertion, deletion, or substitution
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
                }
            }
        }
        return dp[str1.length()][str2.length()] <= threshold;
    }

    public static void main(String[] args) throws IOException {

        // file writer to write to file "LOG.txt", and it overwrites the file each run.
        fileWriter = new BufferedWriter(new FileWriter("src/LOG.txt"));
        Scanner userInputScanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n------------------------------------------------------------");
            System.out.println("Enter anime name or rating or ( \"man of a culture\" to stop).");
            LogToFile("\n------------------------------------------------------------");
            LogToFile("Enter anime name or rating or ( \"man of a culture\" to stop).");
            String userInput = userInputScanner.nextLine();
            LogToFile(userInput);
            // if the user inputs "man of a culture" stop the program.
            if (similar(userInput, "man of a culture", 2)) {
                System.out.println("Exited the program");
                LogToFile("Exited the program");
                break;
            } else {
                // if the input has characters
                if (userInput.matches(".*[a-zA-Z].*")) {
                    // check if the anime is in the cache
                    boolean inCache = false;
                    for (Anime anime : animeList.values()) {
                        // if it is in cache print it
                        if (similar(anime.getAnimeName(), userInput, 2)) {
                            inCache = true;
                            System.out.println("Anime: " + anime.getAnimeName() + " (Rating: " + anime.getAnimeRating() + ")");
                            LogToFile("Anime: " + anime.getAnimeName() + " (Rating: " + anime.getAnimeRating() + ")");
                            break;
                        }
                    }
                    // if the anime is not in cache
                    if (!inCache) {
                        // create a file reader to read the anime list
                        BufferedReader fileReader = new BufferedReader(new FileReader("src/anime.txt"));
                        String currentLine;
                        boolean found = false;
                        while ((currentLine = fileReader.readLine()) != null) {
                            // split the line into anime name and rating
                            Anime temp = Tokenize(currentLine);
                            // if the anime exists in the file print it
                            if (similar(temp.getAnimeName(), userInput, 2)) {
                                found = true;
                                System.out.println("Anime: " + temp.getAnimeName() + " (Rating: " + temp.getAnimeRating() + ")");
                                LogToFile("Anime: " + temp.getAnimeName() + " (Rating: " + temp.getAnimeRating() + ")");
                            }
                        }
                        fileReader.close();

                        // if the anime is not in the cache nor the file
                        if (!found) {
                            System.out.println("Please Enter a Rating from 0 to 10: ");
                            LogToFile("Please Enter a Rating from 0 to 10: ");
                            String rating = userInputScanner.nextLine();
                            LogToFile(rating);
                            // if the user enters an incorrect rating, keep asking them to enter a correct one
                            while (!rating.matches("^(?:[0-9](?:\\.\\d+)?|10(?:\\.0+)?)$")) {
                                System.out.println("Incorrect Rating please enter a rating between 0 and 10.");
                                LogToFile("Incorrect Rating please enter a rating between 0 and 10.");
                                rating = userInputScanner.nextLine();
                            }
                            Anime newAnime = new Anime();
                            newAnime.setAnimeRating(parseFloat(rating));
                            newAnime.setAnimeName(userInput);
                            // append the added anime into the "anime.txt" file
                            BufferedWriter writer = new BufferedWriter(new FileWriter("src/anime.txt", true));
                            writer.newLine();
                            writer.write(userInput + " " + newAnime.getAnimeRating());
                            writer.close();
                        }
                    }
                }
                //if the input is only numbers
                else if (userInput.matches("^(?:[0-9](?:\\.\\d+)?|10(?:\\.0+)?)$")) {
                    Float rating = parseFloat(userInput);
                    BufferedReader fileReader = new BufferedReader(new FileReader("src/anime.txt"));
                    String currentLine;
                    // search the file
                    while ((currentLine = fileReader.readLine()) != null) {
                        // split each line into an anime name and rating
                        Anime temp = Tokenize(currentLine);
                        // if the anime in the file has a rating greater than or equals the input print it
                        if (temp.getAnimeRating() >= rating) {
                            System.out.println("Anime: " + temp.getAnimeName() + " (Rating: " + temp.getAnimeRating() + ")");
                            LogToFile("Anime: " + temp.getAnimeName() + " (Rating: " + temp.getAnimeRating() + ")");
                        }
                    }
                    fileReader.close();
                }
            }
        }
        userInputScanner.close();
        fileWriter.close();
    }
}
