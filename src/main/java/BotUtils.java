import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.PatternSyntaxException;

public class BotUtils extends Bot {

    void sendTextMessage(Long chatId, String textMessage) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(textMessage);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.getMessage();
        }
    }

    void sendReplyKeyboard(Long chatId, String[] optionsNames, String messageToDisplayBefore) {

        SendMessage sendMessage = new SendMessage();

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        // Create the keyboard (list of keyboard rows)
        List<KeyboardRow> keyboard = new ArrayList<KeyboardRow>();
        // Create a keyboard row
        KeyboardRow row = new KeyboardRow();
        // Set each button, you can also use KeyboardButton objects if you need something else than text

        for (String optionName : optionsNames) {
            row.add(optionName);
        }

        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        // Add it to the message
        sendMessage.setReplyMarkup(keyboardMarkup);
        sendMessage.setChatId(chatId);
        sendMessage.setText(messageToDisplayBefore);

        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    DonorsResponse convertDonationToMap(String messageFromClint, Long chatId) {

        Reader inputString = new StringReader(messageFromClint);
        BufferedReader reader = new BufferedReader(inputString);
        HashMap<String, Integer> donorsMap = new HashMap<String, Integer>();
        String line;
        String[] donorNameAndAmount;
        boolean isPassedMapping = false;

        try {
            while ((line = reader.readLine()) != null) {

                donorNameAndAmount = line.split("-");
                donorsMap.put(donorNameAndAmount[0], Integer.parseInt(donorNameAndAmount[1].trim()));
            }
            isPassedMapping = true;
        }

        catch (IOException e) {
            System.out.println("file not found" + e.getMessage());
        }
        catch (Exception e){
            sendTextMessage(chatId, "please verify the syntax of the donors");
        }
        if(!isPassedMapping){
            donorsMap = null;
        }
        return new DonorsResponse(donorsMap, isPassedMapping);
    }
}
