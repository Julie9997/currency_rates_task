package org.julie;
import java.io.IOException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrencyRatesApp {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Использование: java CurrencyRatesApp --code=USD --date=2022-10-08");
            return;
        }

        String code = null;
        String date = null;

        for (String arg : args) {
            if (arg.startsWith("--code=")) {
                code = arg.substring(7);
            } else if (arg.startsWith("--date=")) {
                date = arg.substring(7);
            }
        }

        if (code == null || date == null) {
            System.out.println("Необходимо указать параметры --code и --date");
            return;
        }

        try {
            String currencyName = getCurrencyName(code, date);
            System.out.println(currencyName);
        } catch (IOException e) {
            System.out.println("Произошла ошибка при получении данных: " + e.getMessage());
        }
    }

    private static String getCurrencyName(String code, String date) throws IOException {
        String url = "https://www.cbr.ru/scripts/XML_daily.asp";
        OkHttpClient httpClient = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("date_req", date);

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();

            // Преобразование XML в JSON для удобства обработки данных
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody.getBytes("UTF-8"));
            JsonNode valuteNode = rootNode.findValue("Valute");

            for (JsonNode currency : valuteNode) {
                String charCode = currency.findValue("CharCode").asText();
                if (charCode.equals(code)) {
                    String name = currency.findValue("Name").asText();
                    String value = currency.findValue("Value").asText();
                    return code + " (" + name + "): " + value;
                }
            }
        }

        return "Курс для валюты с кодом " + code + " за " + date + " не найден.";
    }
}