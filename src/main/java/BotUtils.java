import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.*;
import java.util.*;

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

    void sendReplyKeyboard(Long chatId, List<String> optionsNames, String messageToDisplayBefore) {

        SendMessage sendMessage = new SendMessage();

        ReplyKeyboardMarkup keyboardMarkup = setReplayKeyBord(optionsNames);
        sendMessage.setReplyMarkup(keyboardMarkup);
        sendMessage.setChatId(chatId);
        sendMessage.setText(messageToDisplayBefore);

        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    HashMap<String, String> convertDonationToMap(Long chatId, String messageFromClint) {

        Reader inputString = new StringReader(messageFromClint);
        BufferedReader reader = new BufferedReader(inputString);
        HashMap<String, String> donorsMap = new HashMap<String, String>();
        String line;
        String[] donorNameAndAmount;
        boolean isPassedMapping = false;

        try {
            while ((line = reader.readLine()) != null) {

                donorNameAndAmount = line.split("-");
                donorsMap.put(donorNameAndAmount[0], donorNameAndAmount[1].trim());
            }

            isPassedMapping = true;
        }

        catch (IOException e) {
            System.out.println("not found" + e.getMessage());
        }
        catch (Exception e){
            sendTextMessage(chatId, "please verify the syntax of the donors");
        }
        if(!isPassedMapping){
            donorsMap = null;
        }
        return donorsMap;
    }

    private ReplyKeyboardMarkup setReplayKeyBord(List<String> options){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        for (int i = 0; i < options.size(); i += 2){
            row.add(options.get(i));
            if(i + 1 < options.size()){
                row.add(options.get(i+1));
            }
            KeyboardRow tempRow = row;
            row = new KeyboardRow();
            keyboard.add(tempRow);
        }
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }
}
