import ExcelManager.ExcelWriter;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.HashMap;

public class Bot extends TelegramLongPollingBot {

    Long chatId;
    String firstNName;
    String lastName;
    BotUtils botUtils;
    final String donateDetailMessage = "please insert amount in format of" +
            "firstName lastName - amount";

    public void onUpdateReceived(Update update) {

        if (update.getMessage().hasText() && update.hasMessage()) {

            chatId = update.getMessage().getChatId();
            firstNName = update.getMessage().getChat().getFirstName();
            lastName = update.getMessage().getChat().getLastName();
            String messageFromClint = update.getMessage().getText();
            String donateStatus = null;
            botUtils = new BotUtils();
            boolean isNewReport = false;
            HashMap<String, Integer> donationMap = new HashMap<>();
            DonorsResponse donorsResponse = null;

            /**
             * starting point of the bot
             * asking user if want to add new donation
             * or to produce report by name*/
            if (messageFromClint.equals("/start")) {

                botUtils.sendTextMessage(chatId, "welcome");

                String[] whatToDo = {"add new donation", "get report by name"};

                botUtils.sendReplyKeyboard(chatId, whatToDo, "please select an option");

            }
            /**
             * to add new donation
             * from two options
             * cash or gift
             * */
            else if (messageFromClint.equals("add new donation")) {

                String[] paymentMethods = {"cash", "gift"};
                botUtils.sendReplyKeyboard(chatId, paymentMethods, "please select payment method");

            }
            /**
             * in case user choose to donate with cash
             * */
            else if (messageFromClint.equals("cash")) {

                donateStatus = DonateStatusEnum.CASH.getEnumValue();
                requestForDonation(chatId);
            }
            /**
             * in case user choose to donate with gift
             * */
            else if (messageFromClint.equals("gift")) {

                donateStatus = DonateStatusEnum.GIFT.getEnumValue();
                requestForDonation(chatId);

            }
            /**
             * in case user choose to produce previous report
             * bot will send all report names
             * user will select from ReplayKeyBord
             * */
            else if (messageFromClint.equals("get report by name")) {  // todo after complete excel creator

                botUtils.sendTextMessage(chatId, "please choose your report");
                // todo give to user list of all available reports

            }
            /**
             * in case user choose to write current donation to new report
             * */
            else if (messageFromClint.equals("new report")) {
                isNewReport = true;
                botUtils.sendTextMessage(chatId, "please give me the report name" +
                        "i want to know that is new report so write at the beginning" +
                        "'new'");
            }
            /**
             * in case user choose to write current donation to existing report
             * */
            else if (messageFromClint.equals("existing report")) {
                isNewReport = false;
            }
            else if(messageFromClint.startsWith("report")){
                String reportName = messageFromClint.substring(5, messageFromClint.length());
            }
            /**
             * this is may be donation message
             * if no, message of unknown command will send to user
             * */
            else {
                donorsResponse = botUtils.convertDonationToMap(messageFromClint, chatId);

                if (donorsResponse.getIsPassedMapping()) {

                    String[] paymentMethods = {"new report", "existing report"};
                    botUtils.sendReplyKeyboard(chatId, paymentMethods, "and the report will be written to?...");
                }
                else {
                    requestForDonation(chatId);
                }
            }
        }

    }

    private void requestForDonation(Long chatId) {
        botUtils.sendTextMessage(chatId, "please send the donors with '-' " +
                "between name and amount of donation");
    }

    public String getBotUsername() {
        return "meronBot";
    }

    public String getBotToken() {
        return "926814310:AAEjdF_lCjsFTVBZXfhFyoiPnUxrtE-HfZE";
    }
}
