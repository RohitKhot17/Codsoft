/*TASK 4

Currency Selection: Allow the user to choose the base currency and the target
currency.
Currency Rates: Fetch real-time exchange rates from a reliable API.
Amount Input: Take input from the user for the amount they want to convert.
Currency Conversion: Convert the input amount from the base currency to the
target currency using the fetched exchange rate.
Display Result: Show the converted amount and the target currency symbol
to the user. */

import java.util.Scanner;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Task4 {
    private static final String API_KEY = "b642ab7d6e71c7dc2f2cdbc7"; 

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the base currency code (e.g., Rupee): ");
        String baseCurrency = scanner.next().toUpperCase();

        System.out.print("Enter the target currency code (e.g., USD): ");
        String targetCurrency = scanner.next().toUpperCase();

        System.out.print("Enter the amount to convert: ");
        double amount = scanner.nextDouble();

        double exchangeRate = getExchangeRate(baseCurrency, targetCurrency);

        if (exchangeRate == -1) {
            System.out.println("Unable to fetch exchange rates. Please try again later.");
        } else {
            double convertedAmount = amount * exchangeRate;
            System.out.printf("%.2f %s is equal to %.2f %s%n", amount, baseCurrency, convertedAmount, targetCurrency);
        }
        scanner.close();
    }

    private static double getExchangeRate(String baseCurrency, String targetCurrency) {
        try {
            URL url = new URL("https://open.er-api.com/v6/latest/" + baseCurrency + "?apikey=" + API_KEY);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (connection.getResponseCode() == 200) {
                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();

                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }

                scanner.close();

                Map<String, Object> responseData = parseJson(response.toString());
                Map<String, Double> rates = (Map<String, Double>) responseData.get("rates");

                return rates.get(targetCurrency);
            } else {
                System.out.println("Error fetching exchange rates. HTTP response code: " + connection.getResponseCode());
                return -1;
            }
        } catch (IOException e) {
            System.out.println("Error fetching exchange rates: " + e.getMessage());
            return -1;
        }
    }

    private static Map<String, Object> parseJson(String jsonString) {
        Map<String, Object> data = new HashMap<>();
        String[] keyValuePairs = jsonString.replaceAll("[{}\"]", "").split(",");
        for (String pair : keyValuePairs) {
            String[] entry = pair.split(":");
            data.put(entry[0].trim(), entry[1].trim());
        }
        return data;
    }
}
